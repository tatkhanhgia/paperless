/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel.process;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.itextpdf.kernel.geom.Rectangle;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.mobileid.id.eid.object.JWT_Authenticate;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
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
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.objects.Enterprise_SigningInfo;
import vn.mobileid.id.paperless.objects.SignaturePositionProperties;
import vn.mobileid.id.paperless.kernel_v2.GetAsset;
import vn.mobileid.id.paperless.kernel_v2.GetWorkflowDetails;
import vn.mobileid.id.paperless.kernel_v2.Parsing;
import vn.mobileid.id.paperless.kernel_v2.UpdateFileManagement;
import vn.mobileid.id.paperless.kernel_v2.UpdateWorkflowActivity;
import vn.mobileid.id.paperless.object.enumration.FileType;
import vn.mobileid.id.paperless.object.enumration.TemplateSignature;
import vn.mobileid.id.paperless.object.enumration.WorkflowAttributeTypeName;
import vn.mobileid.id.paperless.objects.Item_JSNObject;
import vn.mobileid.id.paperless.objects.KYC_V2;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;
import vn.mobileid.id.utils.PDF_Processing;
import vn.mobileid.id.utils.Utils;
import vn.mobileid.id.utils.XSLT_PDF_Processing;

/**
 *
 * @author GiaTK
 */
public class ProcessELaborContract {

    //<editor-fold defaultstate="collapsed" desc="process Elabor contract - Deprecated">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Assign Elabor Contract">
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
        InternalResponse response = GetWorkflowDetails.getWorkflowDetail(
                woAc.getWorkflow_id(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        //Get Asset template file from DB        
        List<WorkflowAttributeType> list = (List<WorkflowAttributeType>) response.getData();
        WorkflowAttributeType q = new WorkflowAttributeType();
        for (WorkflowAttributeType a : list) {
            if (a.getId() == WorkflowAttributeTypeName.ASSET_ELABOR.getId()) //Asset Template{
            {
                q = a;
                break;
            }
        }
        int assetId = 0;
        if (q.getValue() instanceof String) {
            assetId = Integer.parseInt((String) q.getValue());
        }
        if (q.getValue() instanceof Integer) {
            assetId = (int) q.getValue();
        }

        InternalResponse temp = GetAsset.getAsset(
                assetId,
                transactionID);

        if (temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return temp;
        }

        //Get Metadata in Asset Template - PositionSignatureProperties
        InternalResponse temp2 = GetAsset.getMetadataOfAsset(assetId, transactionID);
        if (temp2.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return temp2;
        }
        String metadata = ((Asset) temp2.getData()).getMetadata();

        if (metadata == null || metadata.isEmpty()) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_ASSET,
                            PaperlessConstant.SUBCODE_MISSING_METADATA_SIGNING_POSITION_IN_ASSET,
                            "en",
                            transactionID)
            );
        }

        //<editor-fold defaultstate="collapsed" desc="Assign data into KYC object  -- Version 2 ">
        KYC_V2.Component component = new KYC_V2.Component();
        component.setCurrentDate((String.valueOf(LocalDate.now())));
        component.setDateAfterOneYear(String.valueOf(LocalDate.now().plusYears(1)));
        component.setPreviousDay(String.valueOf(LocalDate.now().plusDays(1).getDayOfMonth()));
        component.setPreviousMonth(String.valueOf(LocalDate.now().minusMonths(1).getMonthValue()));
        component.setPreviousYear(String.valueOf(LocalDate.now().minusYears(1).getYear()));
        //</editor-fold>

        //Read file XSLT - Assign KYC Object into Template XSLT
        byte[] xsltC = ((Asset) temp.getData()).getBinaryData();

        // Check file name
        if (file_name == null || file_name.isEmpty()) {
            file_name = "eLaborContract - " + AnnotationJWT.Name.getNameAnnot() + " - " + AnnotationJWT.DocNumber.getNameAnnot() + ".pdf";
        }

        //Write into DB
        response = UpdateFileManagement.updateFileManagement(
                woAc.getFile().getID(),
                null,
                file_name,
                -1,
                xsltC.length,
                -1,
                -1,
                -1,
                null,
                null,
                user.getEmail(),
                xsltC,
                false,
                FileType.XSLT.getName(),
                metadata,
                null,
                transactionID);

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        Item_JSNObject tem = new Item_JSNObject();
        tem.setItems(fileItem);
        String json = "";
        json += new ObjectMapper().writeValueAsString(tem);
        json = json.substring(0, json.length() - 1);
        json += ",";
        json += new ObjectMapper().enable(SerializationFeature.WRAP_ROOT_VALUE).writeValueAsString(component).replaceFirst("[{]", "");
        response = UpdateWorkflowActivity.updateMetadata(
                woAc.getId(),
                //                new ObjectMapper().writeValueAsString(object), //Old 
                json,
                user.getName() == null ? user.getEmail() : user.getName(),
                transactionID);

        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Process Elabor Contract With Authen">
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
            byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html, XSLT_PDF_Processing.FontOfTemplate.Elabor_Template);

            FileDataDetails data = image.get(0);
            String photo = "";
            if (data.getValue() instanceof byte[]) {
                photo = Base64.getEncoder().encodeToString((byte[]) data.getValue());
            } else {
                photo = (String) data.getValue();
            }

            //Parse Signing Properties in FileManagement into PositionSignatureProperties
            Enterprise_SigningInfo position = new ObjectMapper().readValue(file.getSigningProperties(), Enterprise_SigningInfo.class);
            SignaturePositionProperties signerPosition = null;
            SignaturePositionProperties businessPosition = null;
            for (Enterprise_SigningInfo.TemplateSignature template : position.getDataSignature()) {
                if (template.getType().equals(TemplateSignature.Elabor.getName())) {
                    signerPosition = template.getSignerPosition();
                    businessPosition = template.getBusinessPosition();
                }
            }

            if (Integer.parseInt(signerPosition.getPage()) < file.getPages()) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_CANNOT_SIGNING,
                                PaperlessConstant.SUBCODE_INVALID_PAGE_IN_POSITION_CONFIGURATION,
                                "en",
                                transactionID));
            }

            result1 = SigningService
                    .getInstant(user.getAid())
                    .signHashWitness(
                            name,
                            (String) photo,
                            signerPosition,
                            pdf,
                            jwt,
                            signing,
                            transactionID
                    );
            if (result1 == null) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_CANNOT_SIGNING,
                                PaperlessConstant.SUBCODE_CANNOT_GET_SIGNING_INFO_PROPERTIES,
                                "en",
                                transactionID));
            }
            result2 = SigningService
                    .getInstant(user.getAid())
                    .signHashBussiness(
                            result1.get(0),
                            businessPosition,
                            TemplateSignature.Elabor);
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
        FileManagement file_ = PDF_Processing.analysisPDF(result2.get(0));
        InternalResponse res = UpdateFileManagement.updateFileManagement(
                file.getID(),
                null,
                name,
                file_.getPages(),
                file_.getSize(),
                file_.getWidth(),
                file_.getHeight(),
                0,
                null,
                null,
                user.getEmail(),
                result2.get(0),
                true,
                FileType.PDF.getName(),
                null,
                null,
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        //Update WorkflowActivity
        StringBuilder builder = new StringBuilder();
        builder.append("{")
                .append(new ObjectMapper().writeValueAsString(objects))
                .append(",")
                .append(new ObjectMapper().writeValueAsString(signing))
                .append("}");

        res = UpdateWorkflowActivity.updateMetadata(
                id,
                builder.toString(),
                user.getName() == null ? user.getEmail() : user.getName(),
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Process Elabor Contract With Authen Version2">
    public static InternalResponse processELaborContractWithAuthenV2(
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
        System.out.println("Metadata:" + object);
        Item_JSNObject items = new ObjectMapper().readValue(object, Item_JSNObject.class);
        KYC_V2.Component component = new ObjectMapper().readValue(object, KYC_V2.Component.class);
        try {
            //Assign JWT Data
//            if (jwt != null && jwt.isMath_result()) {
//                for (ItemDetails item : items.getItems()) {
//                    if(item.getField().equals(""))
//                    objects.setFullName(jwt.getName());
//                    objects.setNationality(jwt.getNationality());
//                    objects.setPersonalNumber(jwt.getDocument_number());
//                    objects.setPlaceOfResidence(jwt.getPlace_of_residence());
//                }
//            }
            String name = user.getName();
            if (jwt.getName() != null) {
                name = jwt.getName();
            }

            byte[] xsltC = file.getData();
            byte[] html = XSLT_PDF_Processing.appendData(items, component, xsltC);
            byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html, XSLT_PDF_Processing.FontOfTemplate.Elabor_Template);

            FileDataDetails data = image.get(0);
            String photo = "";
            if (data.getValue() instanceof byte[]) {
                photo = Base64.getEncoder().encodeToString((byte[]) data.getValue());
            } else {
                photo = (String) data.getValue();
            }

            //Parse Signing Properties in FileManagement into PositionSignatureProperties
            Enterprise_SigningInfo position = new ObjectMapper().readValue(file.getSigningProperties(), Enterprise_SigningInfo.class);
            SignaturePositionProperties signerPosition = null;
            SignaturePositionProperties businessPosition = null;

            //Parse boxCoordinate from percentage to point
            try {
                if (Utils.isNullOrEmpty(position.getDataSignature())) {
                    return new InternalResponse(
                            PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                            PaperlessMessageResponse.getErrorMessage(
                                    PaperlessConstant.CODE_INVALID_PARAMS_ASSET,
                                    PaperlessConstant.SUBCODE_MISSING_SIGNATURE_POSITION_PROPERTIES_IN_PAYLOAD,
                                    "en",
                                    transactionID)
                    );
                }

                for (Enterprise_SigningInfo.TemplateSignature template : position.getDataSignature()) {
                    try {
                        String boxCoordinate_signer = template.getSignerPosition().getBoxCoordinate();
                        String boxCoordinate_business = template.getBusinessPosition().getBoxCoordinate();
                        Rectangle pageSize = PDF_Processing.getPageSize(pdf);
                        if (boxCoordinate_signer != null && !boxCoordinate_signer.isEmpty()) {
                            String finalBoxCoordinateSigner = Parsing.parseBoxCoordinate(
                                    boxCoordinate_signer,
                                    Math.round(pageSize.getWidth()),
                                    Math.round(pageSize.getHeight()),
                                    transactionID);
                            template.getSignerPosition().setBoxCoordinate(finalBoxCoordinateSigner);
                        }

                        if (boxCoordinate_business != null || !boxCoordinate_business.isEmpty()) {
                            String finalBoxCoordinateBusiness = Parsing.parseBoxCoordinate(
                                    boxCoordinate_business,
                                    Math.round(pageSize.getWidth()),
                                    Math.round(pageSize.getHeight()),
                                    transactionID);
                            template.getBusinessPosition().setBoxCoordinate(finalBoxCoordinateBusiness);
                        }
                        if (template.getType().equals(TemplateSignature.Elabor.getName())) {
                            signerPosition = template.getSignerPosition();
                            businessPosition = template.getBusinessPosition();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return new InternalResponse(
                                PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                                PaperlessMessageResponse.getErrorMessage(
                                        PaperlessConstant.CODE_INVALID_PARAMS_ASSET,
                                        PaperlessConstant.SUBCODE_MISSING_SIGNATURE_POSITION_PROPERTIES_IN_PAYLOAD,
                                        "en",
                                        transactionID)
                        );
                    }
                }
            } catch (Exception ex) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_INVALID_PARAMS_ASSET,
                                PaperlessConstant.SUBCODE_MISSING_METADATA_SIGNING_POSITION_IN_ASSET,
                                "en",
                                transactionID)
                );
            }

            if (Integer.parseInt(signerPosition.getPage()) < file.getPages()) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_CANNOT_SIGNING,
                                PaperlessConstant.SUBCODE_INVALID_PAGE_IN_POSITION_CONFIGURATION,
                                "en",
                                transactionID));
            }

            System.out.println("ProcessELaborContract - Signer Position BoxCoordinate:" + signerPosition.getBoxCoordinate());

            result1 = SigningService
                    .getInstant(user.getAid())
                    .signHashWitness(
                            name,
                            (String) photo,
                            signerPosition,
                            pdf,
                            jwt,
                            signing,
                            transactionID
                    );
            if (result1 == null) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_CANNOT_SIGNING,
                                PaperlessConstant.SUBCODE_CANNOT_GET_SIGNING_INFO_PROPERTIES,
                                "en",
                                transactionID));
            }
            result2 = SigningService
                    .getInstant(user.getAid())
                    .signHashBussiness(
                            result1.get(0),
                            businessPosition,
                            TemplateSignature.Elabor);
            if (result2 == null) {
                String message = "Business signing error!";
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message);
            }
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
        FileManagement file_ = PDF_Processing.analysisPDF(result2.get(0));
        InternalResponse res = UpdateFileManagement.updateFileManagement(
                file.getID(),
                null,
                name,
                file_.getPages(),
                file_.getSize(),
                file_.getWidth(),
                file_.getHeight(),
                0,
                null,
                null,
                user.getEmail(),
                result2.get(0),
                true,
                FileType.PDF.getName(),
                null,
                null,
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        //Update WorkflowActivity
        StringBuilder builder = new StringBuilder();
        builder.append("{")
                .append(new ObjectMapper().writeValueAsString(items))
                .append(",")
                .append(new ObjectMapper().writeValueAsString(signing))
                .append("}");

        res = UpdateWorkflowActivity.updateMetadata(
                id,
                builder.toString(),
                user.getName() == null ? user.getEmail() : user.getName(),
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Hash Document">
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
            if (jwt != null) {
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
            byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html, XSLT_PDF_Processing.FontOfTemplate.Elabor_Template);

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
            System.out.println("Name:" + objects.getFullName());
            result1 = SigningService.hashDocument(
                    objects.getFullName(),
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
    //</editor-fold>

//=====================INTERNAL METHOD =======================
    //<editor-fold defaultstate="collapsed" desc="Assign All Item into KYC Object">
    public static KYC assignAllItem(List<ItemDetails> listItem) {
        KYC kyc = new KYC();
        for (ItemDetails details : listItem) {
            String field = details.getField();
            int type = details.getType();
            Object checkType = ItemDetails.checkType(type);
            Object value = null;
            if (checkType instanceof String) {
                value = (String) details.getValue();
            }
            if (checkType instanceof Integer && details.getValue() instanceof Integer) {
                value = (Integer) details.getValue();
            }
            if (checkType instanceof Integer && details.getValue() instanceof String) {
                value = (Integer) Integer.parseInt((String) details.getValue());
            }
            if (checkType instanceof Date) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat(
                            PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
                    System.out.println("Date:" + (String) details.getValue());
                    value = format.parse((String) details.getValue());
                    kyc = assignIntoKYC(kyc, "date", value);
                } catch (ParseException ex) {
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        System.out.println("Date:" + (String) details.getValue());
                        value = format.parse((String) details.getValue());
                        kyc = assignIntoKYC(kyc, "date", value);
                    } catch (ParseException ex1) {
                        Logger.getLogger(ProcessELaborContract.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
            kyc = assignIntoKYC(kyc, field, value);
        }
        return kyc;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Assign Data into KYC Field">
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
    //</editor-fold>

}
