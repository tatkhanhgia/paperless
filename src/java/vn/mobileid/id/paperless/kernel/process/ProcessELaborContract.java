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
import vn.mobileid.id.paperless.kernel.UpdateWorkflowActivity;
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
public class ProcessELaborContract {

//    final private static Logger LOG = LogManager.getLogger(ProcessELaborContract.class);
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
            Object[] photo,
            User user,
            String file_name,
            JWT_Authenticate jwt,
            String transactionID
    ) throws Exception {
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

        //Assign JWT Data
        if (jwt != null && jwt.isMath_result()) {
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(ProcessELaborContract.class, "Get Data from JWT!");
            }
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

        //Call SignHashWitness
        List<byte[]> result1 = null;
        if (photo[1] instanceof String) {
//            result1 = SigningService.getInstant(3).signHashWitness(object.getFullName(), (String) photo[1], pdf, jwt);
        }
        if (photo[1] instanceof byte[]) {
            String temp = Base64.getEncoder().encodeToString((byte[]) photo[1]);
//            result1 = SigningService.getInstant(3).signHashWitness(object.getFullName(), temp, pdf, jwt);
        }

        //Call SignHashBusiness        
        List<byte[]> result2 = SigningService.getInstant(3).signHashBussiness(result1.get(0));

        if (file_name == null || file_name.isEmpty()) {
            if (!jwt.getName().isEmpty()) {
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
                result2.get(0),
                true,
                transactionID);
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                woAc
        );
    }

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
        DatabaseResponse template = DB.getAsset(
                ((WorkflowDetail_Option) response.getData()).getAsset_Template(),
                transactionID
        );
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
            file_name = "eLaborContract - " + AnnotationJWT.Name.getNameAnnot() + " - " + AnnotationJWT.DocNumber.getNameAnnot() + ".pdf";
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
                xsltC,
                false,
                transactionID);

        response = UpdateWorkflowActivity.updateMetadata(
                woAc.getId(),
                new ObjectMapper().writeValueAsString(object),
                user.getName() == null ? user.getEmail() : user.getName(),
                transactionID);
        
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
//        WorkflowActivity get = Resources.getListWorkflowActivity().get(String.valueOf(woAc.getId()));
//        get.getFile().setData(xsltC);
//        get.getFile().setName(file_name);
//        get.setRequestData(new ObjectMapper().writeValueAsString(object));
//        Resources.getListWorkflowActivity().replace(String.valueOf(get.getId()), get);       
        
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
            SigningProperties signing,
            String transactionID
    ) throws IOException, Exception {
        List<byte[]> result1;
        List<byte[]> result2;        
        KYC objects = new ObjectMapper().readValue(object, KYC.class);
        try {
            //Assign JWT Data
            if (jwt != null && jwt.isMath_result()) {
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(ProcessELaborContract.class, "Get data from JWT!");
                }
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

            result1 = SigningService.getInstant(3).signHashWitness(
                    name,
                    (String) photo,
                    pdf,
                    jwt,
                    signing,
                    transactionID
            );
            result2 = SigningService.getInstant(3).signHashBussiness(result1.get(0));
        } catch (Exception ex) {
            if (LogHandler.isShowErrorLog()) {
                ex.printStackTrace();
                LogHandler.error(ProcessELaborContract.class, transactionID, "Error while Signing - Detail:" + ex);
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
//        if (name.contains(AnnotationJWT.DocNumber.getNameAnnot())) {
//            name = name.replace(AnnotationJWT.DocNumber.getNameAnnot(), jwt.getDocument_number() == null ? "null" : jwt.getDocument_number());
//        }
//        if (name.contains(AnnotationJWT.Name.getNameAnnot())){
//            name = name.replace(AnnotationJWT.Name.getNameAnnot(), jwt.getName());
//        }
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

//        WorkflowActivity get = Resources.getListWorkflowActivity().get(String.valueOf(id));
//        get.getFile().setData(result2.get(0));
//        get.getFile().setName(file.getName());
//        get.setRequestData(new ObjectMapper().writeValueAsString(objects));
//        Resources.getListWorkflowActivity().replace(String.valueOf(get.getId()), get);
        
        res = UpdateWorkflowActivity.updateMetadata(
                id,
                new ObjectMapper().writeValueAsString(object),
                user.getName() == null ? user.getEmail() : user.getName(),
                transactionID);
        if(res.getStatus()!= PaperlessConstant.HTTP_CODE_SUCCESS){
            return res;
        }
        
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }

    public static InternalResponse getHashDocument(
            User user,
            int id, //workflow activity
            Asset file,
            String object, // dữ liệu truyền lên từ client
            JWT_Authenticate jwt,
            List<FileDataDetails> image,
            SigningProperties signing,
            String transactionID
    ) throws IOException, Exception {
        List<String> result1 = null;
        KYC objects = new ObjectMapper().readValue(object, KYC.class);
        try {            
            //Assign JWT Data
            if (jwt != null && jwt.isMath_result()) {
                LogHandler.debug(ProcessELaborContract.class, "Get data from JWT!");
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
            for(int i=0; i<result1.size() ; i++){
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
                    if (LogHandler.isShowErrorLog()) {
                        LogHandler.error(ProcessELaborContract.class, "Cannot assign Data into KYC Object - Details:" + ex);
                    }
                } catch (IllegalAccessException ex) {
                    if (LogHandler.isShowErrorLog()) {
                        LogHandler.error(ProcessELaborContract.class, "Cannot assign Data into KYC Object - Details:" + ex);
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
