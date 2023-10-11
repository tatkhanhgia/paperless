/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import java.nio.file.Files;
import java.nio.file.Paths;
import vn.mobileid.id.general.database.DatabaseImpl_V2_FileManagement;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class UpdateFileManagement {

    public static InternalResponse updateFileManagement(
            long id,
            String DBMS,
            String name,
            int pages,
            long size,
            float width,
            float height,
            int status,
            String hmac,
            String created_by,
            String last_modified_by,
            byte[] data,
            boolean isSigned,
            String file_type,
            String signing_properties,
            String hash_values,
            String transactionID) throws Exception {
        //Upload to FMS        
//        System.out.println("Length Data:"+data.length);
//        FMSClient client = new FMSClient();
//        client.setURL(Configuration.getInstance().getUrlFMS());
//        client.setData(data);
//        client.setFormat("pdf");
//
//        //Create Log
//        StringBuilder builder = new StringBuilder();
//        builder.append("Send to FMS").append("\n\t");
//        builder.append("URL:" + Configuration.getInstance().getUrlFMS()).append("\n\t");
//        builder.append("Format:").append("pdf");
//        System.out.println(builder.toString());
//        LogHandler.info(
//                UpdateFileManagement.class,
//                transactionID,
//                builder.toString());
//        
//        try {
//            client.uploadFile();
//        } catch (Exception ex) {
//            LogHandler.error(
//                    UpdateFileManagement.class,
//                    transactionID,
//                    ex);
//            return new InternalResponse(
//                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
//                    PaperlessMessageResponse.getErrorMessage(
//                            PaperlessConstant.CODE_FMS,
//                            PaperlessConstant.SUBCODE_ERROR_WHILE_UPLOADING_TO_FMS,
//                            "en",
//                            transactionID));
//        }
//        if (client.getHttpCode() != 200) {
//            LogHandler.error(
//                    UpdateFileManagement.class,
//                    transactionID,
//                    client.getMessage_Error());
//            return new InternalResponse(
//                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
//                    PaperlessMessageResponse.getErrorMessage(
//                            PaperlessConstant.CODE_FMS,
//                            PaperlessConstant.SUBCODE_FMS_REJECT_UPLOAD,
//                            "en",
//                            transactionID));
//        }
//String uuid = client.getUUID();
            
        System.out.println("Data leg:"+data.length);
        System.out.println("Start call FMS");
        InternalResponse response = UploadToFMS.uploadToFMS(data, transactionID);
        System.out.println("End call FMS");
        if(response.getStatus()!=PaperlessConstant.HTTP_CODE_SUCCESS){return response;}
        
        String uuid = response.getMessage();
        
        System.out.println("UUID:"+uuid);
        
        DatabaseImpl_V2_FileManagement DB = new DatabaseImpl_V2_FileManagement();

        //Update new FileManagement
        DatabaseResponse callDB = DB.updateFileManagement(
                id,
                uuid,
                DBMS,
                name,
                pages,
                size,
                width,
                height,
                status,
                hmac,
                created_by,
                last_modified_by,
                isSigned,
                file_type,
                signing_properties,
                hash_values,
                transactionID);

        try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                message = PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    "");
        } catch (Exception e) {
            throw new Exception("Cannot create File Management!", e);
//            );
        }
    }

    public static void main(String[] args) throws Exception {
        InternalResponse response = UpdateFileManagement.updateFileManagement(
                658,
                //                "New UUID",
                null,
                null,
                -1,
                -1,
                -1,
                -1,
                -1,
                null,
                null,
                null,
                Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\response.pdf")),
                true,
                null,
                null,
                null,
                null);

        System.out.println("Mess:" + response.getMessage());
    }
}
