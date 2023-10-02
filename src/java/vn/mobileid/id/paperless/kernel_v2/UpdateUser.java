/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import java.util.Date;
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
public class UpdateUser {
    public static InternalResponse updateUser(
            String pUSER_EMAIL,
            String pUSER_NAME,
            String pMOBILE_NUMBER, 
            Date pPASSWORD_EXPIRED_AT, 
            int pREMAINING_COUNTER, 
            String pSTATUS_NAME, 
            int pCHANGE_PASSWORD,
            boolean pLOCKED_ENABLED,
            Date pLOCKED_AT, 
            int pBUSINESS_TYPE, 
            String pORGANIZATION_WEBSITE,
            String pHMAC, 
            String pLAST_MODIFIED_BY, 
            String transactionId
    )throws Exception{
        DatabaseV2_User callDb = new DatabaseImpl_V2_User();
        
        DatabaseResponse response = callDb.updateUser(
                pUSER_EMAIL,
                pUSER_NAME, 
                pMOBILE_NUMBER, 
                pPASSWORD_EXPIRED_AT, 
                pREMAINING_COUNTER,
                pSTATUS_NAME, 
                pCHANGE_PASSWORD,
                pLOCKED_ENABLED,
                pLOCKED_AT,
                pBUSINESS_TYPE,
                pORGANIZATION_WEBSITE, 
                pHMAC,
                pLAST_MODIFIED_BY,
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
                ""
        );
    }
    
    public static void main(String[] args) throws Exception{
        InternalResponse response = UpdateUser.updateUser(
                "fit.nguyentrunghieu.711@gmail.com",
                "UserName",
                "0123456789",
                null,
                0,
                "2", 
                0, 
                true, 
                null, 
                0, 
                "https://google.com",
                "hmac",
                "GiaTK",
                "transactionId");
        
        System.out.println("Mess:"+response.getMessage());
    }
}
