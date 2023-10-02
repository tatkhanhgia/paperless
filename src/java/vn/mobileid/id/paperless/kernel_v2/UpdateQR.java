/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.database.DatabaseImpl_V2_QR;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class UpdateQR {
    /**
     * Update QR data
     * @param pQR_ID
     * @param pMETA_DATA
     * @param pIMAGE
     * @param pLAST_MODIFIED_BY
     * @param transactionId
     * @return
     * @throws Exception 
     */
    public static InternalResponse updateQR(
            long pQR_ID,
            String pMETA_DATA,
            String pIMAGE,
            String pLAST_MODIFIED_BY,
            String transactionId
    ) throws Exception{
        DatabaseImpl_V2_QR callDb = new DatabaseImpl_V2_QR();
        
        DatabaseResponse response = callDb.updateQR(
                pQR_ID,
                pMETA_DATA, 
                pIMAGE,
                pLAST_MODIFIED_BY, 
                transactionId);
        
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
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
                PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    
    public static void main(String[] args) throws Exception{
        InternalResponse response = UpdateQR.updateQR(
                7, 
                "META",
                "IMAGE",
                "GIATK",
                "TRANSACTIONID");
        System.out.println("Mess:"+response.getMessage());
    }
}
