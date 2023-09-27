///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.paperless.kernel_v2;
//
//import fmsclient.FMSClient;
//import vn.mobileid.id.general.database.Database;
//import vn.mobileid.id.general.database.DatabaseImpl;
//import vn.mobileid.id.general.database.DatabaseImpl_V2_FileManagement;
//import vn.mobileid.id.general.keycloak.obj.User;
//import vn.mobileid.id.general.objects.DatabaseResponse;
//import vn.mobileid.id.general.objects.InternalResponse;
//import vn.mobileid.id.paperless.PaperlessConstant;
//import vn.mobileid.id.paperless.objects.FileManagement;
//import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
//import vn.mobileid.id.paperless.objects.WorkflowActivity;
//import vn.mobileid.id.utils.Configuration;
//
///**
// *
// * @author GIATK
// */
//public class CreateFileManagement {
//    
//    public static InternalResponse processingCreateFileManagement(
//            WorkflowActivity workflow,
//            String UUID,
//            String nameFile,
//            int page,
//            int size,
//            int width,
//            int height,
//            String HMAC,
//            byte[] fileData,
//            User user,
//            String DBMS,
//            FileManagement.FileType file_type,
//            String signing_properties,
//            String hash_values,
//            String transactionID) throws Exception {
//        //Upload to FMS
//        int uuid = 0;
//        FMSClient client = new FMSClient();
//        client.setURL(Configuration.getInstance().getUrlFMS());
//        client.setData(fileData);
//        client.setFormat("pdf");
//        try {
//            client.uploadFile();
//        } catch (Exception ex) {
//            LogHandler.error(
//                    UploadDocument.class,
//                    transactionId,
//                    ex);
//            return new InternalResponse(
//                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                    ResponseMessageController.getErrorMessageAdvanced(
//                            A_FPSConstant.CODE_FMS,
//                            A_FPSConstant.SUBCODE_ERROR_WHILE_UPLOAD_FMS,
//                            type,
//                            transactionId));
//        }
//        if (client.getHttpCode() != 200) {
//            return new InternalResponse(
//                    A_FPSConstant.HTTP_CODE_BAD_REQUEST,
//                    ResponseMessageController.getErrorMessageAdvanced(
//                            A_FPSConstant.CODE_FMS,
//                            A_FPSConstant.SUBCODE_FMS_REJECT_UPLOAD,
//                            type,
//                            transactionId));
//        }
//        String uuid = client.getUUID();
//        return new InternalResponse(
//                A_FPSConstant.HTTP_CODE_SUCCESS,
//                (Object) uuid
//        );
//        
//        DatabaseImpl_V2_FileManagement DB = new DatabaseImpl_V2_FileManagement();
//
//        //Create new File Management
//        DatabaseResponse callDB = DB.createFileManagement(
//                UUID, //UUID
//                nameFile, //name
//                page, //page
//                size, //size
//                width, //width
//                height, //height
//                uuid, //file data
//                HMAC, //HMAC
//                user.getName(),
//                DBMS,
//                file_type.getName(),
//                signing_properties,
//                hash_values,
//                transactionID);
//
//        try {
//            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
//                String message = null;
//                message = PaperlessMessageResponse.getErrorMessage(
//                        PaperlessConstant.CODE_FAIL,
//                        callDB.getStatus(),
//                        "en",
//                        null);
//                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
//                        message
//                );
//            }
//            return new InternalResponse(
//                    PaperlessConstant.HTTP_CODE_SUCCESS,
//                    callDB.getIDResponse());
//        } catch (Exception e) {
//            throw new Exception("Cannot create File Management!", e);
////            );
//        }
//    }
//}
