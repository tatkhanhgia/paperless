/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import java.util.Date;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
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
    
    //<editor-fold defaultstate="collapsed" desc="update All Data of User">
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
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Update Password of User">
    /**
     * Update password of User
     *
     * @param email
     * @param password
     * @param transactionID
     * @return
     * @throws Exception
     */
    public static InternalResponse updateUserPassword(
            String email, //Truyền email get dữ liệu
            String password,
            String transactionID
    ) throws Exception {
        Database DB = new DatabaseImpl();

        DatabaseResponse callDB = DB.updateUserPassword(
                email,
                password,
                transactionID);

        if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = null;
            message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_FAIL,
                    callDB.getStatus(),
                    "en",
                    null);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "");
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="update Password of user">
    /**
     * Update Password of User
     * @param email
     * @param old_password
     * @param new_password
     * @param transactionID
     * @return
     * @throws Exception 
     */
    public static InternalResponse updateUserPassword(
            String email, //Truyền email get dữ liệu
            String old_password,
            String new_password,
            String transactionID
    ) throws Exception {
        Database DB = new DatabaseImpl();        

        DatabaseResponse callDB = DB.updateUserPassword(
                email,
                old_password,
                new_password,
                transactionID);

        if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = null;            
                message = PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
//                LogHandler.error(GetUser.class,
//                        "TransactionID:" + transactionID
//                        + "\nCannot update User Password - Detail:" + message);
//            }
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "");
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="update Role of user">
    /**
     * Update Password of User
     * @param email     
     * @param enterprise_id     
     * @param roleName     
     * @param transactionID
     * @return
     * @throws Exception 
     */
    public static InternalResponse updateRole(
            String email, //Truyền email get dữ liệu
            long enterprise_id,
            String roleName,
            String transactionID
    ) throws Exception {
        DatabaseV2_User DB = new DatabaseImpl_V2_User();        

        DatabaseResponse callDB = DB.updateRole(
                email, 
                enterprise_id, 
                roleName, 
                transactionID);

        if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = null;            
                message = PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
//                LogHandler.error(GetUser.class,
//                        "TransactionID:" + transactionID
//                        + "\nCannot update User Password - Detail:" + message);
//            }
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "");
    }
    //</editor-fold>
    
    public static void main(String[] args) throws Exception{
//        InternalResponse response = UpdateUser.updateUser(
//                "fit.nguyentrunghieu.711@gmail.com",
//                "UserName",
//                "0123456789",
//                null,
//                0,
//                "2", 
//                0, 
//                true, 
//                null, 
//                0, 
//                "https://google.com",
//                "hmac",
//                "GiaTK",
//                "transactionId");
//        
//        System.out.println("Mess:"+response.getMessage());

        InternalResponse response = UpdateUser.updateRole("khanhpx@mobile-id.vn", 3, "OWNER", "transactionId");
        System.out.println("MesS:"+response.getMessage());
    }
}
