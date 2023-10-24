/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.database.DatabaseImpl_V2_CSV;
import vn.mobileid.id.general.database.DatabaseV2_CSV;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.CSVTask;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class GetCSVTask {
    //<editor-fold defaultstate="collapsed" desc="Get CSV Task none get from FMS">
    /**
     * Get CSV Task data
     * @param pCSV_ID
     * @param transactionId
     * @return CSV
     * @throws Exception 
     */
    public static InternalResponse getCSVTask_noneGetFromFMS(
            long pCSV_ID,
            String transactionId
    )throws Exception{
        DatabaseV2_CSV callDb = new DatabaseImpl_V2_CSV();
        DatabaseResponse response = callDb.getCSVTask(pCSV_ID, transactionId);
        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            transactionId)
            );
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, response.getObject());
    }
    //</editor-fold>        
    
    //<editor-fold defaultstate="collapsed" desc="Get CSV Task">
    /**
     * Get CSV Task data
     * @param pCSV_ID
     * @param transactionId
     * @return CSV
     * @throws Exception 
     */
    public static InternalResponse getCSVTask(
            long pCSV_ID,
            String transactionId
    )throws Exception{
        DatabaseV2_CSV callDb = new DatabaseImpl_V2_CSV();
        DatabaseResponse response = callDb.getCSVTask(pCSV_ID, transactionId);
        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            transactionId)
            );
        }
        
        CSVTask csv = (CSVTask) response.getObject();
        //Get CSV File from FMSs
        InternalResponse result = DownloadFromFMS.download(csv.getUuid(), transactionId);
        
        if(result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){return result;}
        
        csv.setBinary_data((byte[])result.getData());
        
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, csv);
    }
    //</editor-fold>  
    
    public static void main(String[] args) throws Exception{
        InternalResponse response = GetCSVTask.getCSVTask_noneGetFromFMS(3, "");
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            System.out.println("Message:"+response.getMessage());
        } else {
            System.out.println(((CSVTask)response.getData()).getId());
        }
    }
}
