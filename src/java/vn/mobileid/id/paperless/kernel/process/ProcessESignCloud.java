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
import vn.mobileid.id.eid.object.JWT_Authenticate;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.SigningService;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.FileDataDetails;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.ItemDetails;
import vn.mobileid.id.paperless.objects.KYC;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.FrameSignatureProperties;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.general.annotation.AnnotationJWT;
import vn.mobileid.id.paperless.kernel_v2.GetAsset;
import vn.mobileid.id.paperless.kernel_v2.GetWorkflowDetails;
import vn.mobileid.id.paperless.kernel_v2.UpdateFileManagement;
import vn.mobileid.id.paperless.kernel_v2.UpdateWorkflowActivity;
import vn.mobileid.id.paperless.object.enumration.FileType;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;
import vn.mobileid.id.utils.PDFAnalyzer;
import vn.mobileid.id.utils.XSLT_PDF_Processing;

/**
 *
 * @author GiaTK
 */
public class ProcessESignCloud {

    public static InternalResponse assignEsignCloud(
            WorkflowActivity woAc,
            List<ItemDetails> fileItem,
            User user,
            String file_name,
            String transactionID
    ) throws IOException, Exception {
        Database DB = new DatabaseImpl();
        //Get Workflow Detail to get Asset
        InternalResponse response = GetWorkflowDetails.getWorkflowDetail(
                woAc.getWorkflow_id(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        //Get Asset template file from DB   
        //Get Asset template file from DB        
        List<WorkflowAttributeType> list = (List<WorkflowAttributeType>)response.getData();
        WorkflowAttributeType q = new WorkflowAttributeType();
        for(WorkflowAttributeType a : list){
            if(a.getId() == 1) //Asset Template{
            {
                q = a;
                break;
            }
        }
        int assetId = 0;
        if(q.getValue() instanceof String){
            assetId = Integer.parseInt((String)q.getValue());
        }
        if(q.getValue() instanceof Integer){
            assetId = (int)q.getValue();
        }
        InternalResponse temp = GetAsset.getAsset(
                assetId,
                transactionID);

        if (temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return temp;
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
        byte[] xsltC = ((Asset) temp.getData()).getBinaryData();

        // Check file name
        if (file_name == null || file_name.isEmpty()) {
            file_name = "eSignCloud - " + AnnotationJWT.Name.getNameAnnot() + ".pdf";
        }

        //Write into DB
        response = UpdateFileManagement.updateFileManagement(
                woAc.getFile().getID(),
                null,
                file_name,
                -1,
                -1,
                -1,
                -1,
                -1,
                null,
                null,
                user.getEmail(),
                xsltC,
                false,
                FileType.XSLT.getName(),
                null,
                null,
                transactionID);
        
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            return response;
        }

        //Update Request Data of Workflow Activity
        response = UpdateWorkflowActivity.updateMetadata(
                woAc.getId(),
                new ObjectMapper().writeValueAsString(object),
                user.getName() == null ? user.getEmail() : user.getName(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

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
            FrameSignatureProperties signing,
            String transactionID
    ) throws IOException, Exception {
        List<byte[]> result1;
        List<byte[]> result2;
        KYC objects = new ObjectMapper().readValue(object, KYC.class);
        try {
//            //Assign JWT Data
            String name = user.getName();
            if (jwt != null && jwt.isMath_result()) {
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(ProcessESignCloud.class, transactionID, "Get data from JWT!");
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
            if (result1 == null) {
                String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                        PaperlessConstant.SUBCODE_SIGNING_ERROR,
                        "en",
                        null);
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message);
            }

            result2 = SigningService.getInstant(3).signHashBussiness(result1.get(0));
        } catch (NullPointerException ex) {
            LogHandler.error(
                    ProcessESignCloud.class,
                    transactionID,
                    "Cannot get Certificare from RSSP !",
                    ex);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_SIGNING_ERROR,
                            "en",
                            null)
            );
        } catch (Exception ex) {
            LogHandler.error(
                    ProcessESignCloud.class,
                    transactionID,
                    "Error while Signing !",
                    ex);
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
        FileManagement file_ = PDFAnalyzer.analysisPDF(result2.get(0));
        InternalResponse res = UpdateFileManagement.updateFileManagement(
                file.getID(),
                null,
                name,
                file_.getPages(),
                file_.getSize(),
                file_.getWidth(),
                file_.getHeight(),
                -1,
                null,
                null,
                user.getEmail(),
                result2.get(0),
                true,
                FileType.PDF.getName(),
                new ObjectMapper().writeValueAsString(signing),
                null,
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
 
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
                    LogHandler.error(
                            ProcessESignCloud.class,
                            "transaction",
                            "Cannot assign Data into KYC Object !",
                            ex);
                } catch (IllegalAccessException ex) {
                    LogHandler.error(
                            ProcessESignCloud.class,
                            "transaction",
                            "Cannot assign Data into KYC Object !",
                            ex);
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
            FrameSignatureProperties signing
    ) throws NullPointerException, Exception {
        if (jwt.getDocument_number() != null) {
            if (!SigningService.getInstant(3).checkExist(jwt.getDocument_number(), "")) {
                SigningService.getInstant(3).createOwner(
                        jwt.getDocument_number(),
                        user.getEmail(),
                        jwt.getPhone_number(),
                        jwt.getDocument_number());
                SigningService.getInstant(3).issueCertificate(
                        jwt.getDocument_number(),
                        user.getEmail(),
                        jwt.getPhone_number(),
                        jwt.getPlace_of_residence(),
                        jwt.getCity_province(),
                        jwt.getNationality(),
                        jwt.getDocument_number());
            }
            return SigningService.getInstant(3).signHashUser(
                    jwt.getDocument_number(),
                    "12345678",
                    jwt.getName(),
                    (String) photo,
                    content,
                    signing,
                    jwt);
        } else {
            throw new NullPointerException();
        }
    }

    public static void main(String[] args) {
//        ProcessELaborContract a = new ProcessELaborContract(100);
//        System.out.println("A:"+a.TEMPLATE_E_LABOR_CONTRACT);
//        ProcessELaborContract b = new ProcessELaborContract();
//        System.out.println("B:"+b.TEMPLATE_E_LABOR_CONTRACT);

    }
}
