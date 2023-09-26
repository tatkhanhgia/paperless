/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import java.util.Date;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class UpdateUser {

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

    /**
     * Update Password of User
     *
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

    public static InternalResponse updateGeneralProfile(
            String email,
            String username,
            String mobile,
            Date passwordExpiredAt,
            int remaining,
            String status,
            int changePass, //0 - None 1 - Enforce Reset pass 2 - Pass changes by admin
            boolean lock,
            Date lockAt,
            int businessType,// 1 - Personal 2- Business
            String website,
            String hmac,
            String modifiedBy,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = new DatabaseImpl().updateUser(
                email,
                username,
                mobile,
                passwordExpiredAt,
                remaining,
                status,
                changePass,
                lock,
                lockAt,
                businessType,
                website,
                hmac,
                modifiedBy,
                transactionId);
        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = null;
            message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_FAIL,
                    response.getStatus(),
                    "en",
                    null);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    
    public static InternalResponse updateRole(
            String email,
            int enterprise_id,
            String role_name,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = new DatabaseImpl().updateRole(
                email, 
                enterprise_id, 
                role_name, 
                transactionId);
        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = null;
            message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_FAIL,
                    response.getStatus(),
                    "en",
                    null);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
}
