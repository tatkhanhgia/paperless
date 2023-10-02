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
public class DeleteUser {
    public static InternalResponse deleteUser(
            String pUSER_EMAIL,
            String pDELETED_USER_EMAIL,
            long pENTERPRISE_ID,
            String transactionId
    )throws Exception{
        DatabaseV2_User callDb = new DatabaseImpl_V2_User();
        
        DatabaseResponse response = callDb.deleteUser(
                pUSER_EMAIL,
                pDELETED_USER_EMAIL, 
                pENTERPRISE_ID,
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
        InternalResponse response = DeleteUser.deleteUser(
                "khanhpx@mobile-id.vn", 
                "mdsa1s@mobile-id.vn",
                3,
                "transactionId");
        
        System.out.println("Mess:"+response.getMessage());
    }
}
