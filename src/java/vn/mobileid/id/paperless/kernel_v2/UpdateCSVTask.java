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
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class UpdateCSVTask {
    //<editor-fold defaultstate="collapsed" desc="Update CSVTask">
    /**
     * Update data of the CSV
     * @param pCSV_TASK_ID
     * @param pNAME
     * @param pUUID
     * @param pDMS_PROPERTY
     * @param pPAGES
     * @param pSIZE
     * @param pMETA_DATA
     * @param pHMAC
     * @param pLAST_MODIFIED_BY
     * @param transactionId
     * @return none
     * @throws Exception 
     */
    public static InternalResponse updateCSV(
            long pCSV_TASK_ID,
            String pNAME,
            String pUUID,
            String pDMS_PROPERTY,
            int pPAGES,
            long pSIZE,
            String pMETA_DATA,
            String pHMAC,
            String pLAST_MODIFIED_BY,
            String transactionId
    )throws Exception{
        DatabaseV2_CSV callDb = new DatabaseImpl_V2_CSV();
        DatabaseResponse response = callDb.updateCSVTask(pCSV_TASK_ID, pNAME, pUUID, pDMS_PROPERTY, pPAGES, pSIZE, pMETA_DATA, pHMAC, pLAST_MODIFIED_BY, transactionId);
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
        
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,""
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Update CSVTask with Upload to FMS">
    public static InternalResponse updateCSV(
            long pCSV_TASK_ID,
            String pNAME,
            String pDMS_PROPERTY,
            int pPAGES,
            long pSIZE,
            String pMETA_DATA,
            String pHMAC,
            String pLAST_MODIFIED_BY,
            byte[] fileData,
            String transactionId
    )throws Exception{
        //Upload to FMS
        InternalResponse response = UploadToFMS.uploadToFMS(fileData, transactionId);
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){return response;}
        
        String uuid = response.getMessage();
        
        return updateCSV(pCSV_TASK_ID, pNAME, uuid, pDMS_PROPERTY, pPAGES, pSIZE, pMETA_DATA, pHMAC, pLAST_MODIFIED_BY, transactionId);
    }
    //</editor-fold>
    
    public static void main(String[] args) throws Exception{
        InternalResponse response = updateCSV(
                2, 
                "new Name" ,
                "0", 
                "New DVM", 
                0, 
                100, 
                null, 
                null,
                "GiaTK",
                "transactionId");
        System.out.println("Respoonse:"+response.getMessage());
    }
}
