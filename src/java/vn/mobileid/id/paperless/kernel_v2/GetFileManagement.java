/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import fmsclient.FMSClient;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.DatabaseImpl_V2_FileManagement;
import vn.mobileid.id.general.database.DatabaseV2_FileManagement;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class GetFileManagement {
    
    //<editor-fold defaultstate="collapsed" desc="Get File Management">
    public static InternalResponse getFileManagement(
            long fileManagementId,
            String transactionId
    )throws Exception{
        DatabaseV2_FileManagement callDb = new DatabaseImpl_V2_FileManagement();
        DatabaseResponse response = callDb.getFileManagement(
                fileManagementId, 
                transactionId);
        
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            transactionId));
        }
        
        FileManagement file = (FileManagement) response.getObject();
        
        //Download data from FMS
        InternalResponse response_ = downloadDocumentFromFMS(file.getUUID(), transactionId);
        
        if(response_.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            return response_;
        }
        
        file.setData((byte[])response_.getData());
        System.out.println("Data from FMS:"+((byte[])response_.getData()).length);
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                file
        );
    }
    //</editor-fold>        
    
    //<editor-fold defaultstate="collapsed" desc="Get File Management without check FMS">
    public static InternalResponse getFileManagement_NoneGetFromFMS(
            long fileManagementId,
            String transactionId
    )throws Exception{
        DatabaseV2_FileManagement callDb = new DatabaseImpl_V2_FileManagement();
        DatabaseResponse response = callDb.getFileManagement(
                fileManagementId, 
                transactionId);
        
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            transactionId));
        }
        
        FileManagement file = (FileManagement) response.getObject();                
        
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                file
        );
    }
    //</editor-fold>   
    
    //<editor-fold defaultstate="collapsed" desc="Download Document From FMS">
    protected static InternalResponse downloadDocumentFromFMS(
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
    
    public static void main(String[] args) throws Exception{
        InternalResponse response = GetFileManagement.getFileManagement(655, "transactionId");
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            System.out.println("Mess:"+response.getMessage());
        }
        else {
            FileManagement file = (FileManagement)response.getData();
            System.out.println("UUID:"+file.getUUID());
            System.out.println("FileType:"+file.getFile_type().getName());
        }
    }
}
