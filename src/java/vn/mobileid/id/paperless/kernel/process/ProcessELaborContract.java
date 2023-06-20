/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import vn.mobileid.id.eid.object.JWT_Authenticate;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.SigningService;
import vn.mobileid.id.paperless.kernel.GetAsset;
import vn.mobileid.id.paperless.kernel.GetWorkflowDetail_option;
import vn.mobileid.id.paperless.kernel.UpdateFileManagement;
import vn.mobileid.id.paperless.kernel.UpdateWorkflowActivity;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.FileDataDetails;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.FileManagement.FileType;
import vn.mobileid.id.paperless.objects.ItemDetails;
import vn.mobileid.id.paperless.objects.KYC;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.FrameSignatureProperties;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;
import vn.mobileid.id.utils.AnnotationJWT;
import vn.mobileid.id.utils.PDFAnalyzer;
import vn.mobileid.id.utils.XSLT_PDF_Processing;

/**
 *
 * @author GiaTK
 */
public class ProcessELaborContract {

    /**
     * Processing Elabor Contract
     *
     * @param woAc Workflow Activity Object
     * @param fileItem listItem get from request.getItem();
     * @param photo the Object of fileData (can be photo, fingerprint, pdf,card)
     * @param user
     * @param file_name
     * @param jwt
     * @param transactionID
     * @return InternalResponse with Document ID.
     * @throws java.lang.Exception
     */
    @Deprecated
    public static InternalResponse processELaborContract(
            WorkflowActivity woAc,
            List<ItemDetails> fileItem,
            Object[] photo,
            User user,
            String file_name,
            JWT_Authenticate jwt,
            String transactionID
    ) throws Exception {
//        Database DB = new DatabaseImpl();
//
//        //Get Workflow Detail to get Asset
//        InternalResponse response = GetWorkflowDetail_option.getWorkflowDetail(
//                woAc.getWorkflow_id(),
//                transactionID);
//        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//            return response;
//        }
//
//        //Get Asset template file from DB
////        DatabaseResponse template = DB.getAsset(
////                ((WorkflowDetail_Option) response.getData()).getAsset_Template(),
////                transactionID);
////        if (template.getStatus() != PaperlessConstant.CODE_SUCCESS) {
////            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
////                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
////                            template.getStatus(),
////                            "en",
////                            null)
////            );
////        }
//        InternalResponse temp = GetAsset.getTemplateOfAsset(
//                ((WorkflowDetail_Option) response.getData()).getAsset_Template(),
//                transactionID);
//
//        if (temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//            return temp;
//        }
//
//        //Assign data into KYC object
//        KYC object = new KYC();
//        object = assignAllItem(fileItem);
//        object.setCurrentDate(String.valueOf(LocalDate.now()));
//        object.setDateAfterOneYear(String.valueOf(LocalDate.now().plusYears(1)));
//        object.setPreviousDay(String.valueOf(LocalDate.now().plusDays(1).getDayOfMonth()));
//        object.setPreviousMonth(String.valueOf(LocalDate.now().minusMonths(1).getMonthValue()));
//        object.setPreviousYear(String.valueOf(LocalDate.now().minusYears(1).getYear()));
//
//        //Assign JWT Data
//        if (jwt != null && jwt.isMath_result()) {
////                LogHandler.debug(
////                        ProcessELaborContract.class,
////                        "Get Data from JWT!");            
//            object.setFullName(jwt.getName());
//            object.setNationality(jwt.getNationality());
//            object.setPersonalNumber(jwt.getPhone_number());
//            object.setPlaceOfResidence(jwt.getPlace_of_residence());
//        }
//        //Read file XSLT - Assign KYC Object into Template XSLT
//        byte[] xsltC = ((Asset) temp.getData()).getBinaryData();
//        byte[] html = XSLT_PDF_Processing.appendData(object, xsltC);
//
//        //Convert from HTML to PDF
//        byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);
//
//        //Call SignHashWitness
//        List<byte[]> result1 = null;
//        if (photo[1] instanceof String) {
////            result1 = SigningService.getInstant(3).signHashWitness(object.getFullName(), (String) photo[1], pdf, jwt);
//        }
//        if (photo[1] instanceof byte[]) {
//            String temp2 = Base64.getEncoder().encodeToString((byte[]) photo[1]);
////            result1 = SigningService.getInstant(3).signHashWitness(object.getFullName(), temp, pdf, jwt);
//        }
//
//        //Call SignHashBusiness        
//        List<byte[]> result2 = SigningService.getInstant(3).signHashBussiness(result1.get(0));
//
//        if (file_name == null || file_name.isEmpty()) {
//            if (!jwt.getName().isEmpty()) {
//                file_name = "eLaborContract-" + jwt.getName() + "-SoCCCD.pdf";
//            } else {
//                file_name = "eLaborContract-" + user.getName() + "-SoCCCD.pdf";
//            }
//        }
//        //Write into DB
//        UpdateFileManagement.updateFileManagement(
//                Integer.parseInt(woAc.getFile().getID()),
//                null,
//                null,
//                file_name,
//                0,
//                0,
//                0,
//                0,
//                0,
//                null,
//                null,
//                user.getEmail(),
//                result2.get(0),
//                true,
//                transactionID);
//        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
//                woAc
//        );
        throw new Exception("Not Support");
    }

    //First: get Asset Template, request data and Update it into Workflow Activity + FileManagement
    public static InternalResponse assignELaborContract(
            WorkflowActivity woAc,
            List<ItemDetails> fileItem,
            User user,
            String file_name,
            String transactionID
    ) throws IOException, Exception {
        Database DB = new DatabaseImpl();

        //Get Workflow Detail to get Asset
        InternalResponse response = GetWorkflowDetail_option.getWorkflowDetail(
                woAc.getWorkflow_id(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        //Get Asset template file from DB        
        InternalResponse temp = GetAsset.getAsset(
                ((WorkflowDetail_Option) response.getData()).getAsset_Template(),
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
            file_name = "eLaborContract - " + AnnotationJWT.Name.getNameAnnot() + " - " + AnnotationJWT.DocNumber.getNameAnnot() + ".pdf";
        }

        //Write into DB
        response = UpdateFileManagement.updateFileManagement(
                Integer.parseInt(woAc.getFile().getID()),
                null,
                null,
                file_name,
                0,
                0,
                0,
                0,
                0,
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
        
        response = UpdateWorkflowActivity.updateMetadata(
                woAc.getId(),
                new ObjectMapper().writeValueAsString(object),
                user.getName() == null ? user.getEmail() : user.getName(),
                transactionID);

        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }

    public static InternalResponse processELaborContractWithAuthen(
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
            //Assign JWT Data
            if (jwt != null && jwt.isMath_result()) {
                objects.setFullName(jwt.getName());
                objects.setNationality(jwt.getNationality());
                objects.setPersonalNumber(jwt.getDocument_number());
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
                photo = (String) data.getValue();
            }

            result1 = SigningService
                    .getInstant(user.getAid())
                    .signHashWitness(
                            name,
                            (String) photo,
                            pdf,
                            jwt,
                            signing,
                            transactionID
                    );
            result2 = SigningService
                    .getInstant(user.getAid())
                    .signHashBussiness(result1.get(0));
        } catch (Exception ex) {
            LogHandler.error(
                    ProcessELaborContract.class,
                    transactionID,
                    "Error while Signing !", ex);
            String message = "SIGNING ERROR!" + ex.toString();
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message);
        }

        String name = file.getName();
        name = AnnotationJWT.replaceWithJWT(name, jwt);

        //Write into DB
        int ids = Integer.parseInt(file.getID());
        file = PDFAnalyzer.analysisPDF(result2.get(0));
        InternalResponse res = UpdateFileManagement.updateFileManagement(
                ids,
                null,
                null,
                name,
                file.getPages(),
                file.getSize(),
                file.getWidth(),
                file.getHeight(),
                0,
                null,
                null,
                user.getEmail(),
                result2.get(0),
                true,
                null,
                null,
                null,
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        //Update WorkflowActivity
        res = UpdateWorkflowActivity.updateMetadata(
                id,
                new ObjectMapper().writeValueAsString(objects),
                user.getName() == null ? user.getEmail() : user.getName(),
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }

    //Second: Get FrameSignatureProperties,Hash and update it into WorkflowActivity + Filemanagement
    public static InternalResponse getHashDocument(
            User user,
            int id, //workflow activity
            Asset file,
            String object, // dữ liệu truyền lên từ client
            JWT_Authenticate jwt,
            List<FileDataDetails> image,
            FrameSignatureProperties signing,
            String transactionID
    ) throws IOException, Exception {
        List<String> result1 = null;
        KYC objects = new ObjectMapper().readValue(object, KYC.class);
        try {
            //Assign JWT Data
            if (jwt != null && jwt.isMath_result()) {                
                objects.setFullName(jwt.getName());
                objects.setNationality(jwt.getNationality());
                objects.setPersonalNumber(jwt.getDocument_number());
                objects.setPlaceOfResidence(jwt.getPlace_of_residence());
            }
            String name = user.getName();
            if (jwt.getName() != null) {
                name = jwt.getName();
            }

            byte[] xsltC = file.getBinaryData();
            byte[] html = XSLT_PDF_Processing.appendData(objects, xsltC);
            byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);

            List<String> listPhoto = new ArrayList<>();
            for (FileDataDetails data : image) {
                String photo = "";
                if (data.getValue() instanceof byte[]) {
                    photo = Base64.getEncoder().encodeToString((byte[]) data.getValue());
                } else {
                    photo = (String) data.getValue();
                }
                listPhoto.add(photo);
            }
            result1 = SigningService.hashDocument(
                    name,
                    pdf,
                    listPhoto,
                    signing,
                    jwt,
                    transactionID
            );
            for (int i = 0; i < result1.size(); i++) {
                image.get(i).setHash_value(result1.get(i));
                image.get(i).setValue(null);
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                    image
            );
        } catch (Exception ex) {
            LogHandler.error(ProcessELaborContract.class, transactionID, "Error while Signing!", ex);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_SIGNING_ERROR,
                            "en",
                            null)
            );
        }

//        String name = file.getName();
//        name = AnnotationJWT.replaceWithJWT(name, jwt);
//
//        //Write into DB
//        InternalResponse res = UpdateFileManagement.updateFileManagement(
//                Integer.parseInt(file.getID()),
//                null,
//                null,
//                name,
//                0,
//                0,
//                0,
//                0,
//                null,
//                null,
//                user.getEmail(),
//                result2.get(0),
//                true,
//                transactionID);
//        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//            return res;
//        }
//        
//        WorkflowActivity get = Resources.getListWorkflowActivity().get(String.valueOf(id));
//        get.getFile().setData(result2.get(0));
//        get.getFile().setName(file.getName());
//        get.setRequestData(new ObjectMapper().writeValueAsString(objects));
//        Resources.getListWorkflowActivity().replace(String.valueOf(get.getId()), get);        
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
                            ProcessELaborContract.class,
                            "transactionID",
                            "Cannot assign Data into KYC Object!",
                            ex);
                } catch (IllegalAccessException ex) {
                    LogHandler.error(
                            ProcessELaborContract.class,
                            "transactionID",
                            "Cannot assign Data into KYC Object!",
                            ex);
                }
            }
        }
        return oldValue;
    }

    //backup
//    public static InternalResponse processELaborContractWithAuthen(
//            User user,
//            int id, //workflow activity
//            FileManagement file,
//            String object, // dữ liệu truyền lên từ client
//            JWT_Authenticate jwt,
//            List<FileDataDetails> image,
//            FrameSignatureProperties signing,
//            String transactionID
//    ) throws IOException, Exception {
//        List<byte[]> result1;
//        List<byte[]> result2;
//        KYC objects = new ObjectMapper().readValue(object, KYC.class);
//        try {
//            //Assign JWT Data
//            if (jwt != null && jwt.isMath_result()) {
//                objects.setFullName(jwt.getName());
//                objects.setNationality(jwt.getNationality());
//                objects.setPersonalNumber(jwt.getDocument_number());
//                objects.setPlaceOfResidence(jwt.getPlace_of_residence());
//            }
//            String name = user.getName();
//            if (jwt.getName() != null) {
//                name = jwt.getName();
//            }
//
//            byte[] xsltC = file.getData();
//            byte[] html = XSLT_PDF_Processing.appendData(objects, xsltC);
//            byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);
//
//            FileDataDetails data = image.get(0);
//            String photo = "";
//            if (data.getValue() instanceof byte[]) {
//                photo = Base64.getEncoder().encodeToString((byte[]) data.getValue());
//            } else {
//                photo = (String) data.getValue();
//            }
//
//            result1 = SigningService.getInstant(3).signHashWitness(
//                    name,
//                    (String) photo,
//                    pdf,
//                    jwt,
//                    signing,
//                    transactionID
//            );
//            result2 = SigningService.getInstant(3).signHashBussiness(result1.get(0));
//        } catch (Exception ex) {
//            LogHandler.error(
//                    ProcessELaborContract.class,
//                    transactionID,
//                    "Error while Signing !", ex);
//            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
//                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
//                            PaperlessConstant.SUBCODE_SIGNING_ERROR,
//                            "en",
//                            null)
//            );
//        }
//
//        String name = file.getName();
//        name = AnnotationJWT.replaceWithJWT(name, jwt);
//
//        //Write into DB
//        InternalResponse res = UpdateFileManagement.updateFileManagement(
//                Integer.parseInt(file.getID()),
//                null,
//                null,
//                name,
//                0,
//                0,
//                0,
//                0,
//                0,
//                null,
//                null,
//                user.getEmail(),
//                result2.get(0),
//                true,
//                transactionID);
//        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//            return res;
//        }
//
////        WorkflowActivity get = Resources.getListWorkflowActivity().get(String.valueOf(id));
////        get.getFile().setData(result2.get(0));
////        get.getFile().setName(file.getName());
////        get.setRequestData(new ObjectMapper().writeValueAsString(objects));
////        Resources.getListWorkflowActivity().replace(String.valueOf(get.getId()), get);
//        //Update WorkflowActivity
//        res = UpdateWorkflowActivity.updateMetadata(
//                id,
//                new ObjectMapper().writeValueAsString(objects),
//                user.getName() == null ? user.getEmail() : user.getName(),
//                transactionID);
//        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//            return res;
//        }
//
//        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
//                ""
//        );
//    }
}
