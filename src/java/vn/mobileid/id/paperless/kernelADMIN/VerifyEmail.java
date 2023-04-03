/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernelADMIN;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class VerifyEmail {

    public static InternalResponse checkData(Account account){
        if (account.getUser_email() == null || account.getUser_email().isEmpty()) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_MISSING_USER_EMAIL, "en", null));
        }
        if (account.getAuthorization_code() == null || account.getAuthorization_code().isEmpty()) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_MISSING_AUTHORIZATION_CODE, "en", null));
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, "");
    }
    
    public static InternalResponse verifyEmail(
            String email,
            String pass,
            String transactionID
    ) {
        try {
            Database db = new DatabaseImpl();
            DatabaseResponse res = db.verifyEmail(email, pass, transactionID);
            if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                if (LogHandler.isShowErrorLog()) {
                    message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            res.getStatus(),
                            "en",
                            null);
                    LogHandler.error(CreateAccount.class,
                            "TransactionID:" + transactionID
                            + "\nCannot verify Email - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    "");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(CreateAccount.class,
                        "TransactionID:" + transactionID
                        + "\nUNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(ex));
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }
}
