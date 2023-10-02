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
public class CreateQR {
    
    /**
     * Create new QR
     * @param pMETA_DATA
     * @param pIMAGE
     * @param pHMAC
     * @param pCREATED_BY
     * @param transaction_id
     * @return long QRId
     * @throws Exception 
     */
    public static InternalResponse processingCreateQR(
            String pMETA_DATA,
            String pIMAGE,
            String pHMAC,
            String pCREATED_BY,
            String transaction_id
    )throws Exception{
        DatabaseImpl_V2_QR callDb = new DatabaseImpl_V2_QR();
        
        DatabaseResponse response = callDb.createQR(
                pMETA_DATA,
                pIMAGE,
                pHMAC,
                pCREATED_BY, 
                transaction_id);
        
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        response.getStatus(),
                        "en",
                        transaction_id)
                );
        }
        
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                response.getObject()
        );
    }
    
    public static void main(String[] args) throws Exception{
        InternalResponse response = CreateQR.processingCreateQR(
                "Meta data",
                "Image",
                "HMAC",
                "GiaTK",
                "transactionId");
        
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            System.out.println("Mess:"+response.getMessage());
        } else {
            long id = (long)response.getData();
            System.out.println("ID:"+id);
        }
    }
}
