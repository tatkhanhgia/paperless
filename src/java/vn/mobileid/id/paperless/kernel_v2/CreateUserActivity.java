/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.database.DatabaseImpl_V2_User;
import vn.mobileid.id.general.database.DatabaseV2_User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class CreateUserActivity {
    /**
     * Create a new User Activity
     * @param pUSER_EMAIL
     * @param pENTERPRISE_ID
     * @param pTRANSACTION_ID
     * @param pCATEGORY_NAME
     * @param pUSER_ACTIVITY_EVENT
     * @param pHMAC
     * @param pCREATED_BY
     * @param transactionId
     * @return long UserActivityId
     * @throws Exception 
     */
    public static InternalResponse createUserActivity(
            String pUSER_EMAIL,
            int pENTERPRISE_ID,
            String pTRANSACTION_ID,
            String pCATEGORY_NAME,
            String pUSER_ACTIVITY_EVENT,
            String pHMAC,
            String pCREATED_BY, 
            String transactionId
    )throws Exception{
        DatabaseV2_User callDb = new DatabaseImpl_V2_User();
        
        DatabaseResponse response = callDb.createUserActivity(
                pUSER_EMAIL, 
                pENTERPRISE_ID,
                pTRANSACTION_ID,
                pCATEGORY_NAME, 
                pUSER_ACTIVITY_EVENT,
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
        InternalResponse response = CreateUserActivity.createUserActivity(
                "khanhpx@mobile-id.vn", 
                3,
                "675-0", 
                "Accounts", 
                "Testing", 
                "hmac", 
                "GiaTK",
                "transactionid");
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            System.out.println("Mess:"+response.getMessage());
        } else {
            System.out.println("Id:"+response.getData());
        }
    }
}
