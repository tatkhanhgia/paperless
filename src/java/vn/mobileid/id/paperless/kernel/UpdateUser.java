/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

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
}
