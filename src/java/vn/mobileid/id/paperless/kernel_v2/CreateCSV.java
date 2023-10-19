/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.database.DatabaseImpl_V2_CSV;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class CreateCSV {

    //<editor-fold defaultstate="collapsed" desc="Create CSV Task">
    /**
     * Create new CSV Task
     * @param pNAME
     * @param pUUID
     * @param pDMS_PROPERTY
     * @param pPAGES
     * @param pSIZE
     * @param pMETA_DATA
     * @param pHMAC
     * @param pCREATED_BY
     * @param transactionId
     * @return long CSK_TASK_ID
     * @throws Exception 
     */
    public static InternalResponse createCSV(
            String pNAME,
            String pUUID,
            String pDMS_PROPERTY,
            int pPAGES,
            long pSIZE,
            String pMETA_DATA,
            String pHMAC,
            String pCREATED_BY,
            String transactionId
    ) throws Exception {
        DatabaseImpl_V2_CSV callDb = new DatabaseImpl_V2_CSV();
        DatabaseResponse response = callDb.createCSVTask(pNAME, pUUID, pDMS_PROPERTY, pPAGES, pSIZE, pMETA_DATA, pHMAC, pCREATED_BY, transactionId);
        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            null
                    ));
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, response.getObject());
    }
    //</editor-fold>
    
    public static void main(String[] args) throws Exception{
        InternalResponse response = CreateCSV.createCSV("csv1", "uuid", "dmv", 0, 0, "metadata", "hmac", "GiaTK", "tran");
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            System.out.println("Mess:"+response.getMessage());
        }
        else {
            System.out.println("Id:"+response.getData());
        }
    }
}
