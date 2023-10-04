/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import fmsclient.FMSClient;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.DatabaseImpl_V2_FileManagement;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.object.enumration.FileType;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.utils.Configuration;

/**
 *
 * @author GIATK
 */
public class CreateFileManagement {

    //<editor-fold defaultstate="collapsed" desc="Create File Management">
    public static InternalResponse processingCreateFileManagement(
            WorkflowActivity workflow,
            String nameFile,
            int page,
            int size,
            float width,
            float height,
            String HMAC,
            byte[] fileData,
            User user,
            String DBMS,
            FileType file_type,
            String signing_properties,
            String hash_values,
            String transactionID) throws Exception {
        //Upload to FMS
        String uuid = "uuid";
        if (fileData != null) {
            FMSClient client = new FMSClient();
            client.setURL(Configuration.getInstance().getUrlFMS());
            client.setData(fileData);
            client.setFormat(file_type.getName().toLowerCase());
            try {
                client.uploadFile();
            } catch (Exception ex) {
                LogHandler.error(
                        CreateFileManagement.class,
                        transactionID,
                        ex);
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_FMS,
                                PaperlessConstant.SUBCODE_ERROR_WHILE_UPLOADING_TO_FMS,
                                "en",
                                transactionID));
            }
            if (client.getHttpCode() != 200) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_FMS,
                                PaperlessConstant.SUBCODE_FMS_REJECT_UPLOAD,
                                "en",
                                transactionID));
            }
            uuid = client.getUUID();
        }

        DatabaseImpl_V2_FileManagement DB = new DatabaseImpl_V2_FileManagement();

        //Create new File Management
        DatabaseResponse callDB = DB.createFileManagement(
                nameFile,
                page,
                size,
                width,
                height,
                uuid,
                HMAC,
                user.getEmail(),
                DBMS,
                file_type.getName(),
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
                    callDB.getObject());
        } catch (Exception e) {
            throw new Exception("Cannot create File Management!", e);
        }
    }
    //</editor-fold>

}
