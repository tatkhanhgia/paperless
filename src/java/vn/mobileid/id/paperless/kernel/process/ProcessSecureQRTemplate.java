///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.paperless.kernel.process;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Base64;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.List;
//import vn.mobileid.id.eid.object.JWT_Authenticate;
//import vn.mobileid.id.general.LogHandler;
//import vn.mobileid.id.general.Resources;
//import vn.mobileid.id.general.database.Database;
//import vn.mobileid.id.general.database.DatabaseImpl;
//import vn.mobileid.id.general.keycloak.obj.User;
//import vn.mobileid.id.general.objects.InternalResponse;
//import vn.mobileid.id.paperless.PaperlessConstant;
//import vn.mobileid.id.paperless.SigningService;
//import vn.mobileid.id.paperless.kernel.GetQRSize;
//import vn.mobileid.id.paperless.kernel.GetWorkflowDetail_option;
//import vn.mobileid.id.paperless.kernel.UpdateFileManagement;
//import vn.mobileid.id.paperless.objects.FileDataDetails;
//import vn.mobileid.id.paperless.objects.FileManagement;
//import vn.mobileid.id.paperless.objects.ItemDetails;
//import vn.mobileid.id.paperless.objects.KYC;
//import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
//import vn.mobileid.id.paperless.objects.QRSize;
//import vn.mobileid.id.paperless.objects.SigningProperties;
//import vn.mobileid.id.paperless.objects.WorkflowActivity;
//import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;
//import vn.mobileid.id.qrypto.object.Configuration;
//import vn.mobileid.id.qrypto.object.QRSchema;
//import vn.mobileid.id.qrypto.object.QRSchema.fieldType;
//import vn.mobileid.id.qrypto.object.qryptoEffectiveDate;
//import vn.mobileid.id.utils.AnnotationJWT;
//import vn.mobileid.id.utils.Utils;
//import vn.mobileid.id.utils.XSLT_PDF_Processing;
//
///**
// *
// * @author GiaTK
// */
//public class ProcessSecureQRTemplate {
//
//    public static InternalResponse process(
//            WorkflowActivity woAc,
//            List<FileDataDetails> fileData,
//            List<ItemDetails> fileItem,
//            User user,
//            String file_name,
//            String transactionID
//    ) throws IOException, Exception {        
//        Configuration configure = new Configuration();
//
//        Database DB = new DatabaseImpl();
//
//        //Get Workflow Detail 
//        InternalResponse response = GetWorkflowDetail_option.getWorkflowDetail(
//                woAc.getWorkflow_id(),
//                transactionID);
//        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//            return response;
//        }
//
//        //Get detail and write to Object QR
//        configure = appendWorkflowDetail_into_Configure((WorkflowDetail_Option) response.getData(), transactionID);
//        QRSchema QR = appendData_into_QRScheme(fileData, fileItem, transactionID);                
//
//        //Update Request Data of Workflow Activity
//        WorkflowActivity get = Resources.getListWorkflowActivity().get(String.valueOf(woAc.getId()));
//        get.getFile().setData(xsltC);
//        get.getFile().setName(file_name);
//        get.setRequestData(new ObjectMapper().writeValueAsString(object));
//        Resources.getListWorkflowActivity().replace(String.valueOf(get.getId()), get);
//
//        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
//                ""
//        );
//    }
//
//    public static InternalResponse processELaborContractWithAuthen(
//            User user,
//            int id, //workflow activity
//            FileManagement file,
//            String object, // dữ liệu truyền lên từ client
//            JWT_Authenticate jwt,
//            List<FileDataDetails> image,
//            SigningProperties signing,
//            String transactionID
//    ) throws IOException, Exception {
//        List<byte[]> result1;
//        List<byte[]> result2;
//        KYC objects = new ObjectMapper().readValue(object, KYC.class);
//        try {
//            //Assign JWT Data
//            if (jwt != null && jwt.isMath_result()) {
//                if (LogHandler.isShowDebugLog()) {
//                    LogHandler.debug(ProcessSecureQRTemplate.class, "Get data from JWT!");
//                }
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
//            if (LogHandler.isShowErrorLog()) {
//                ex.printStackTrace();
//                LogHandler.error(ProcessSecureQRTemplate.class, transactionID, "Error while Signing - Detail:" + ex);
//            }
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
////        if (name.contains(AnnotationJWT.DocNumber.getNameAnnot())) {
////            name = name.replace(AnnotationJWT.DocNumber.getNameAnnot(), jwt.getDocument_number() == null ? "null" : jwt.getDocument_number());
////        }
////        if (name.contains(AnnotationJWT.Name.getNameAnnot())){
////            name = name.replace(AnnotationJWT.Name.getNameAnnot(), jwt.getName());
////        }
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
//        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
//                ""
//        );
//    }
////=====================INTERNAL METHOD =======================    
//
//    private static Configuration appendWorkflowDetail_into_Configure(
//            WorkflowDetail_Option detail,
//            String transactionID) throws Exception {
//        Configuration config = new Configuration();
//        config.setContextIdentifier("QC1:");
//        config.setIsTransparent(true);
//
//        qryptoEffectiveDate effectiveDate = new qryptoEffectiveDate();
//        Calendar now = Calendar.getInstance();
//        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now.getTime());
//        now.add(Calendar.YEAR, 1);
//        String timeStamp2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now.getTime());
//        effectiveDate.setNotValidBefore(timeStamp2);
//        effectiveDate.setNotValidAfter(timeStamp);
//
//        config.setQryptoEffectiveDate(effectiveDate);
//
//        InternalResponse call = GetQRSize.getQRSize(String.valueOf(detail.getQr_size()), transactionID);
//
//        int size = 0;
//        if (call.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//            LogHandler.fatal(
//                    ProcessSecureQRTemplate.class,
//                    transactionID,
//                    call.getMessage());
//            size = 1080;
//        } else {
//            size = ((QRSize) call.getData()).getSize();
//        }
//        config.setQryptoHeight(size);
//        config.setQryptoWidth(size);
//
//        return config;
//    }
//
//    private static QRSchema appendData_into_QRScheme(
//            List<FileDataDetails> fileData,
//            List<ItemDetails> items,
//            String transactionID
//    ) throws Exception {
//        QRSchema QR = new QRSchema();
//        QRSchema.format format = new QRSchema.format();        
//        QR.setScheme("QC1");
//        format.setVersion("2");
//
//        List<QRSchema.data> listData = new ArrayList<>();        
//        List<QRSchema.field> listField = new ArrayList<>();
//        HashMap<String, byte[]> headers = new HashMap<>();
//
//        for (ItemDetails item : items) {
//            QRSchema.data data = new QRSchema.data();
//            QRSchema.field field = new QRSchema.field();
//
//            switch (item.getType()) {
//                case 1: { //Type String _ text
//                    String random = Utils.generateRandomString(6);
//                    data.setName(random);
//                    data.setValue((String) item.getValue());
//                    field.setName(item.getField());
//                    field.setKvalue(random);
//                    field.setType(fieldType.t2);
//                    break;
//                }
//                case 2: { //Type Boolean
//                    String random = Utils.generateRandomString(6);
//                    data.setName(random);
//                    data.setValue(((Boolean) item.getValue()) == true ? "true" : "false");
//                    field.setName(item.getField());
//                    field.setKvalue(random);
//                    field.setType(fieldType.t2);
//                    break;
//                }
//                case 3: { //Type INT
//                    String random = Utils.generateRandomString(6);
//                    data.setName(random);
//                    data.setValue(String.valueOf((Integer) item.getValue()));
//                    field.setName(item.getField());
//                    field.setKvalue(random);
//                    field.setType(fieldType.t2);
//                    break;
//                }
//                case 4: { //Type DATE
//                    String random = Utils.generateRandomString(6);
//                    data.setName(random);
//                    data.setValue((String) item.getValue());
//                    field.setName(item.getField());
//                    field.setKvalue(random);
//                    field.setType(fieldType.t2);
//                    break;
//                }
//                case 5: { //Type custom                                        
//                    field.setName(item.getFile_field());
//                    field.setType(fieldType.f1);
//                    field.setFile_type(item.getFile_format());
//                    field.setField_field(item.getFile_field());
//                    if (item.getValue() instanceof String) {
//                        headers.put(item.getFile_field(), Base64.getDecoder().decode((String) item.getValue()));
//                    } else {
//                        headers.put(item.getFile_field(), (byte[]) item.getValue());
//                    }
//                    break;
//                }
//                default: {
//                    throw new Exception("Invalid type of items");
//                }
//            }
//            listData.add(data);
//            listField.add(field);
//        }
//        format.setFields(listField);
//        QR.setData(listData);
//        QR.setFormat(format);
//        QR.setTitle("Paperless Service");
//        QR.setCi("");
//        QR.setHeader(headers);
//        return QR;
//    }
//
//    public static void main(String[] args) {
//
//    }
//}
