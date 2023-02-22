/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.kernel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
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
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.SigningService;
import vn.mobileid.id.qrypto.objects.Asset;
import vn.mobileid.id.qrypto.objects.FileDataDetails;
import vn.mobileid.id.qrypto.objects.FileManagement;
import vn.mobileid.id.qrypto.objects.ItemDetails;
import vn.mobileid.id.qrypto.objects.KYC;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.qrypto.objects.WorkflowActivity;
import vn.mobileid.id.qrypto.objects.WorkflowDetail_Option;
import vn.mobileid.id.utils.Utils;
import vn.mobileid.id.utils.XSLT_PDF_Processing;

/**
 *
 * @author GiaTK
 */
public class ProcessELaborContract {

    final private static Logger LOG = LogManager.getLogger(ProcessELaborContract.class);

    /**
     * Processing Elabor Contract
     *
     * @param woAc Workflow Activity Object
     * @param fileItem listItem get from request.getItem();
     * @param photo the Object of fileData (can be photo, fingerprint, pdf,card)
     * @return InternalResponse with Document ID.
     */
    public static InternalResponse processELaborContract(
            WorkflowActivity woAc,
            List<ItemDetails> fileItem,
            Object[] photo, User user,
            String file_name,
            JWT_Authenticate jwt
    ) {
        Database DB = new DatabaseImpl();
        //Get Workflow Detail to get Asset
        InternalResponse response = GetWorkflowDetail_option.getWorkflowDetail(woAc.getWorkflow_id());
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        //Get Asset template file from DB
        DatabaseResponse template = DB.getAsset(((WorkflowDetail_Option) response.getData()).getAsset_Template());
        if (template.getStatus() != QryptoConstant.CODE_SUCCESS) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                    QryptoMessageResponse.getErrorMessage(
                            QryptoConstant.CODE_FAIL,
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

        //Assign JWT Data
        if (jwt != null && jwt.isMath_result()) {
            LOG.info("Get KYC data from JWT!");
            object.setFullName(jwt.getName());
            object.setNationality(jwt.getNationality());
            object.setPersonalNumber(jwt.getPhone_number());
            object.setPlaceOfResidence(jwt.getPlace_of_residence());
        }
        //Read file XSLT - Assign KYC Object into Template XSLT
        byte[] xsltC = ((Asset) template.getObject()).getBinaryData();
        byte[] html = XSLT_PDF_Processing.appendData(object, xsltC);

        //Convert from HTML to PDF
        byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);

        //Call Create Owner
//            DataCreateOwner createOwner = new DataCreateOwner();
//            createOwner.setEmail(uer_info.getEmail());
//            createOwner.setUsername(uer_info.getEmail());
//            createOwner.setPa(jwt);
//            TokenResponse token = (TokenResponse) EIDService.getInstant().v1VeriOidcToken();
//            String access_token = "Bearer " + token.access_token;
//            CreateOwnerResponse res = (CreateOwnerResponse) EIDService.getInstant().v1OwnerCreate(createOwner, access_token);
        //Call SignHashWitness
        List<byte[]> result1 = null;
        if (photo[1] instanceof String) {
            result1 = SigningService.getInstant(3).signHashWitness(object.getFullName(), (String) photo[1], pdf, jwt);
        }
        if (photo[1] instanceof byte[]) {
            String temp = Base64.getEncoder().encodeToString((byte[]) photo[1]);
            result1 = SigningService.getInstant(3).signHashWitness(object.getFullName(), temp, pdf, jwt);
        }

        //Call SignHashBusiness        
        List<byte[]> result2 = SigningService.getInstant(3).signHashBussiness(result1.get(0));

        if (file_name == null || file_name.isEmpty()) {
            if ( !jwt.getName().isEmpty()){
                file_name = "eLaborContract-" + jwt.getName() + "-SoCCCD.pdf";
            } else {
                file_name = "eLaborContract-" + user.getName() + "-SoCCCD.pdf";
            }            
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
                result2.get(0));
        return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                woAc
        );
    }

    public static InternalResponse assignELaborContract(
            WorkflowActivity woAc,
            List<ItemDetails> fileItem,
            User user,
            String file_name
    ) throws IOException {
        Database DB = new DatabaseImpl();
        //Get Workflow Detail to get Asset
        InternalResponse response = GetWorkflowDetail_option.getWorkflowDetail(woAc.getWorkflow_id());
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        //Get Asset template file from DB
        DatabaseResponse template = DB.getAsset(((WorkflowDetail_Option) response.getData()).getAsset_Template());
        if (template.getStatus() != QryptoConstant.CODE_SUCCESS) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                    QryptoMessageResponse.getErrorMessage(
                            QryptoConstant.CODE_FAIL,
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
        if( file_name == null || file_name.isEmpty()){
            file_name = "eLaborContract - @Name - @CCCD";
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
                pdf);

//        if (photo instanceof String) {
//            Resources.getListPDFWaiting().put(String.valueOf(woAc.getId()), (String) photo);
////            System.out.println("Put Base64 ID:" + String.valueOf(woAc.getId()));
////            Files.write(new File("D:\\NetBean\\qrypto\\file\\send.txt").toPath(), ((String) photo).getBytes(), StandardOpenOption.CREATE);
//        }
//        if (photo instanceof byte[]) {
//            String temp = Base64.getEncoder().encodeToString((byte[]) photo);
////            Files.write(new File("file/send.txt").toPath(), (byte[]) photo, StandardOpenOption.CREATE);
//            Resources.getListPDFWaiting().put(String.valueOf(woAc.getId()), temp);
////            System.out.println("Put byte ID:" + String.valueOf(woAc.getId()));
//        }

        //Update Request Data of Workflow Activity
        WorkflowActivity get = Resources.getListWorkflowActivity().get(String.valueOf(woAc.getId()));
        get.getFile().setData(xsltC);
        get.getFile().setName(file_name);
        get.setRequestData(new ObjectMapper().writeValueAsString(object));
        Resources.getListWorkflowActivity().replace(String.valueOf(get.getId()), get);
        return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }

    public static InternalResponse processELaborContractWithAuthen(
            User user,
            int id,
            FileManagement file,
            String object, 
            JWT_Authenticate jwt,
            List<FileDataDetails> image
    ) throws IOException {
//        if (file.getData() == null) {
//            InternalResponse response = GetDocument.getDocument(id);
//            file = (FileManagement) response.getData();
//        }
//        String photo = Resources.getListPDFWaiting().get(String.valueOf(id));
//        if (photo == null) {
//            return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
//                    QryptoMessageResponse.getErrorMessage(
//                            QryptoConstant.CODE_FAIL,
//                            QryptoConstant.SUBCODE_MISSING_IMAGE,
//                            "en",
//                            null)
//            );
//        }
        List<byte[]> result1;
        List<byte[]> result2;
        KYC objects = new ObjectMapper().readValue(object, KYC.class);
        try {
            //Assign JWT Data
            if (jwt != null && jwt.isMath_result()) {
                LOG.info("Get KYC data from JWT!");
                objects.setFullName(jwt.getName());
                objects.setNationality(jwt.getNationality());
                objects.setPersonalNumber(jwt.getPhone_number());
                objects.setPlaceOfResidence(jwt.getPlace_of_residence());
            }
            String name = user.getName();
            if (jwt.getName() != null) {
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
                photo = (String)data.getValue();
            }

            result1 = SigningService.getInstant(3).signHashWitness(name, (String) photo, pdf, jwt);
            result2 = SigningService.getInstant(3).signHashBussiness(result1.get(0));
        } catch (Exception ex) {
            if (LogHandler.isShowErrorLog()) {
                ex.printStackTrace();
                LOG.error("Error while Signing - Detail:" + ex);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                    QryptoMessageResponse.getErrorMessage(
                            QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_SIGNING_ERROR,
                            "en",
                            null)
            );
        }

        String name = file.getName();
        if (name.contains("@CCCD")) {
            name = name.replace("@CCCD", jwt.getDocument_number() == null ? "null" : jwt.getDocument_number());
        }
        if (name.contains("@Name")){
            name = name.replace("@Name", jwt.getName());
        }
        //Write into DB
        InternalResponse res = UpdateFileManagement.updateFileManagement(
                id,
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
                result2.get(0));
        if (res.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        WorkflowActivity get = Resources.getListWorkflowActivity().get(String.valueOf(id));
        get.getFile().setData(result2.get(0));
        get.getFile().setName(file.getName());
        get.setRequestData(new ObjectMapper().writeValueAsString(objects));
        Resources.getListWorkflowActivity().replace(String.valueOf(get.getId()), get);
        return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
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
                        LOG.error("Cannot assign Data into KYC Object - Details:" + ex);
                    }
                } catch (IllegalAccessException ex) {
                    if (LogHandler.isShowErrorLog()) {
                        LOG.error("Cannot assign Data into KYC Object - Details:" + ex);
                    }
                }
            }
        }
        return oldValue;
    }

    public static void main(String[] args) {
//        ProcessELaborContract a = new ProcessELaborContract(100);
//        System.out.println("A:"+a.TEMPLATE_E_LABOR_CONTRACT);
//        ProcessELaborContract b = new ProcessELaborContract();
//        System.out.println("B:"+b.TEMPLATE_E_LABOR_CONTRACT);

    }
}
