/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.eid.object.JWT_Authenticate;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.SigningService;
import vn.mobileid.id.paperless.kernel.GetWorkflowDetail_option;
import vn.mobileid.id.paperless.kernel.UpdateFileManagement;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.FileDataDetails;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.ItemDetails;
import vn.mobileid.id.paperless.objects.KYC;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.SigningProperties;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;
import vn.mobileid.id.utils.AnnotationJWT;
import vn.mobileid.id.utils.XSLT_PDF_Processing;

/**
 *
 * @author GiaTK
 */
public class ProcessESignCloud {

//    final private static Logger LOG = LogManager.getLogger(ProcessESignCloud.class);

    public static InternalResponse assignEsignCloud(
            WorkflowActivity woAc,
            List<ItemDetails> fileItem,
            User user,
            String file_name,
            String transactionID
    ) throws IOException {
        Database DB = new DatabaseImpl();
        //Get Workflow Detail to get Asset
        InternalResponse response = GetWorkflowDetail_option.getWorkflowDetail(
                woAc.getWorkflow_id(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        //Get Asset template file from DB        
        DatabaseResponse template = DB.getAsset(
                ((WorkflowDetail_Option) response.getData()).getAsset_Template(),
                transactionID);
        if (template.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            template.getStatus(),
                            "en",
                            null)
            );
        }

        //Assign data into KYC object
        KYC object = new KYC();
        object = assignAllItem(fileItem);
        object.setCurrentDate(String.valueOf(LocalDate.now()));
        object.setDateAfterOneYear(String.valueOf(LocalDate.now().plusYears(1)));
        object.setPreviousDay(String.valueOf(LocalDate.now().plusDays(1).getDayOfMonth()));
        object.setPreviousMonth(String.valueOf(LocalDate.now().minusMonths(1).getMonthValue()));
        object.setPreviousYear(String.valueOf(LocalDate.now().minusYears(1).getYear()));

        //Read file XSLT - Assign KYC Object into Template XSLT
        byte[] xsltC = ((Asset) template.getObject()).getBinaryData();
        byte[] html = XSLT_PDF_Processing.appendData(object, xsltC);

        //Convert from HTML to PDF
        byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);

        // Check file name
        if (file_name == null || file_name.isEmpty()) {
            file_name = "eSignCloud - " + AnnotationJWT.Name.getNameAnnot() + ".pdf";
        }

        //Write into DB
        UpdateFileManagement.updateFileManagement(
                Integer.parseInt(woAc.getFile().getID()),
                null,
                null,
                file_name,
                0,
                0,
                0,
                0,
                null,
                null,
                user.getEmail(),
                pdf,
                false,
                transactionID);

        //Update Request Data of Workflow Activity
        WorkflowActivity get = Resources.getListWorkflowActivity().get(String.valueOf(woAc.getId()));
        get.getFile().setData(xsltC);
        get.getFile().setName(file_name);
        get.setRequestData(new ObjectMapper().writeValueAsString(object));
        Resources.getListWorkflowActivity().replace(String.valueOf(get.getId()), get);

        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }

    public static InternalResponse processEsignCloudWithAuthen(
            User user,
            int id, //workflow activity
            FileManagement file,
            String object, // dữ liệu truyền lên từ client
            JWT_Authenticate jwt,
            List<FileDataDetails> image,
            SigningProperties signing,
            String transactionID
    ) throws IOException {
        List<byte[]> result1;
        List<byte[]> result2;
        KYC objects = new ObjectMapper().readValue(object, KYC.class);
        try {
//            //Assign JWT Data
            String name = user.getName();
            if (jwt != null && jwt.isMath_result()) {
                if(LogHandler.isShowDebugLog()){
                    LogHandler.debug(ProcessESignCloud.class,transactionID,"Get data from JWT!");
                }
                objects.setFullName(jwt.getName());
                objects.setNationality(jwt.getNationality());
                objects.setPersonalNumber(jwt.getDocument_number());
                objects.setPlaceOfResidence(jwt.getPlace_of_residence());
                name = jwt.getName();
            }          

            byte[] xsltC = file.getData();
            byte[] html = XSLT_PDF_Processing.appendData(objects, xsltC);
            byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);

            FileDataDetails data = image.get(0);
            String photo = "";
            if (data.getValue() instanceof byte[]) {
                photo = Base64.getEncoder().encodeToString((byte[]) data.getValue());
            } else {
                photo = (String) data.getValue();
            }

            //Signing Flow
            result1 = eSignCloudFlow(
                    jwt,
                    user,
                    photo,
                    pdf,
                    signing);
            result2 = SigningService.getInstant(3).signHashBussiness(result1.get(0));
        } catch (Exception ex) {
            if (LogHandler.isShowErrorLog()) {
                ex.printStackTrace();
                LogHandler.error(ProcessESignCloud.class,transactionID,"Error while Signing - Detail:" + ex);
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_SIGNING_ERROR,
                            "en",
                            null)
            );
        }

        String name = file.getName();
        name = AnnotationJWT.replaceWithJWT(name, jwt);

        //Write into DB
        InternalResponse res = UpdateFileManagement.updateFileManagement(
                Integer.parseInt(file.getID()),
                null,
                null,
                name,
                0,
                0,
                0,
                0,
                null,
                null,
                user.getEmail(),
                result2.get(0),
                true,
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        WorkflowActivity get = Resources.getListWorkflowActivity().get(String.valueOf(id));
        get.getFile().setData(result2.get(0));
        get.getFile().setName(file.getName());
        get.setRequestData(new ObjectMapper().writeValueAsString(objects));
        Resources.getListWorkflowActivity().replace(String.valueOf(get.getId()), get);
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
//=====================INTERNAL METHOD =======================
    //Function Assign All value in Item into KYC Object
    private static KYC assignAllItem(List<ItemDetails> listItem) {
        KYC kyc = new KYC();
        for (ItemDetails details : listItem) {
            String field = details.getField();
            int type = details.getType();
            Object checkType = ItemDetails.checkType(type);
            Object value = null;
            if (checkType instanceof String) {
                value = (String) details.getValue();
            }
//                if( checkType instanceof Boolean){
//                    Boolean temp = (Boolean) details.getValue();
//                    temp.
//                }
            if (checkType instanceof Integer) {
                value = (Integer) details.getValue();
            }
            kyc = assignIntoKYC(kyc, field, value);
        }
        return kyc;
    }

    //Assign data into KYC Object
    private static KYC assignIntoKYC(KYC oldValue, String field, Object value) {
        Field[] fields = KYC.getHashMapFieldName();
        for (Field temp : fields) {
            if (temp.getName().contains(field)) {
                try {
                    oldValue.set(temp, value);
                    return oldValue;
                } catch (IllegalArgumentException ex) {
                    if (LogHandler.isShowErrorLog()) {
                        LogHandler.error(ProcessESignCloud.class,"Cannot assign Data into KYC Object - Details:" + ex);
                    }
                } catch (IllegalAccessException ex) {
                    if (LogHandler.isShowErrorLog()) {
                        LogHandler.error(ProcessESignCloud.class,"Cannot assign Data into KYC Object - Details:" + ex);
                    }
                }
            }
        }
        return oldValue;
    }

    private static List<byte[]> eSignCloudFlow(
            JWT_Authenticate jwt,
            User user,
            String photo,
            byte[] content,
            SigningProperties signing            
    ){
        if (jwt.getDocument_number() != null) {
                if (!SigningService.getInstant(3).checkExist(jwt.getDocument_number(), "")) {
                    SigningService.getInstant(3).createOwner(jwt.getDocument_number(),
                            user.getEmail(),
                            jwt.getPhone_number(),
                            jwt.getDocument_number());
                    SigningService.getInstant(3).issueCertificate(
                            jwt.getDocument_number(),
                            user.getEmail(),
                            jwt.getPhone_number(),
                            jwt.getCity_province(),
                            jwt.getNationality(),
                            jwt.getDocument_number());
                }
                return  SigningService.getInstant(3).signHashUser(
                    jwt.getDocument_number(),
                    "12345678",
                    jwt.getName(),
                    (String) photo,
                    content,
                    signing,
                    jwt);
            } else {                
                return  null;
            }            
    }
    public static void main(String[] args) {
//        ProcessELaborContract a = new ProcessELaborContract(100);
//        System.out.println("A:"+a.TEMPLATE_E_LABOR_CONTRACT);
//        ProcessELaborContract b = new ProcessELaborContract();
//        System.out.println("B:"+b.TEMPLATE_E_LABOR_CONTRACT);

    }
}
