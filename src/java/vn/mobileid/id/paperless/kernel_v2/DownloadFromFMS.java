/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import fmsclient.FMSClient;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class DownloadFromFMS {
    //<editor-fold defaultstate="collapsed" desc="Download Document From FMS">
    protected static InternalResponse download(
            String uuid,            
            String transactionId
    )throws Exception{
        //Check if UUID is default => return error
        if(uuid.equals("uuid")){
                    return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_PROCESS_YET,
                            "en",
                            null)
            );
        }
        
        //Download from FMS
        FMSClient client = new FMSClient();
        client.setUUID(uuid);
        try{
            client.downloadFile();
        } catch(Exception ex){
            LogHandler.error(
                    GetFileManagement.class,
                    transactionId,
                    ex);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FMS,
                            PaperlessConstant.SUBCODE_ERROR_WHILE_DOWNLOADING_FROM_FMS,
                            "en",
                            transactionId));
        }
        if(client.getHttpCode() != 200){
            LogHandler.fatal(
                    GetFileManagement.class, 
                    transactionId, 
                    client.getMessage_Error()
                    );
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FMS,
                            PaperlessConstant.SUBCODE_FMS_REJECT_DOWNLOAD,
                            "en",
                            transactionId));
        }
        byte[] data = client.getData();
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                data
        );
    }
    //</editor-fold>
}
