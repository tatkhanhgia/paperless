/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.database.DatabaseImpl_V2_Transaction;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class CreateTransaction {
    
    /**
     * Create a new Transaction
     * @param U_EMAIL
     * @param pLOG_ID
     * @param pOBJECT_ID
     * @param pOBJECT_TYPE
     * @param pIP_ADDRESS
     * @param pMETADATA
     * @param pDESCRIPTION
     * @param pHMAC
     * @param pCREATED_BY
     * @param transactionId
     * @return String TransactionId
     * @throws Exception 
     */
    public static InternalResponse createTransaction(
            String U_EMAIL, 
            long pLOG_ID, 
            long pOBJECT_ID,
            int pOBJECT_TYPE, 
            String pIP_ADDRESS,
            String pMETADATA, 
            String pDESCRIPTION, 
            String pHMAC, 
            String pCREATED_BY, 
            String transactionId
    )throws Exception{
        DatabaseImpl_V2_Transaction callDb = new DatabaseImpl_V2_Transaction();
        
        DatabaseResponse response = callDb.createTransaction(
                U_EMAIL,
                pLOG_ID,
                pOBJECT_ID,
                pOBJECT_TYPE,
                pIP_ADDRESS, 
                pMETADATA,
                pDESCRIPTION, 
                pHMAC, 
                pCREATED_BY,
                transactionId);
        
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        response.getStatus(),
                        "en",
                        transactionId)
                );
        }
        
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                response.getObject()
        );
    }
    
    public static void main(String[] args) throws Exception{
        InternalResponse response = CreateTransaction.createTransaction(
                "khanhpx@mobile-id.vn",
                675,
                0,
                0,
                "127.0.0.1",
                "meta data",
                "Description",
                "hmac",
                "GiaTK",
                "transactionId");
        
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            System.out.println("Mess:"+response.getMessage());
        }
        else {
            System.out.println("Id:"+response.getData());
        }
    }
}
