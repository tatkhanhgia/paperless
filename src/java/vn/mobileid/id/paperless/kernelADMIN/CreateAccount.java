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
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class CreateAccount {

    public static InternalResponse checkDataAccount(Account account) {
        if (account.getEnterprise_name() == null || account.getEnterprise_name().isEmpty()) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_MISSING_ENTERPRISE_DATA, "en", null));
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_SUCCESS,
                        PaperlessConstant.SUBCODE_SUCCESS, "en", null));

    }

    public static InternalResponse createAccount(
            String email,
            String name,
            int enterprise_id,
            String transactionID
    ) {
        try {
            Database db = new DatabaseImpl();
return null;
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

    public static class CreateUser {

        public static InternalResponse createUser(
                String email,
                String created_user_email,
                int enterprise_id,
                String role_name,
                long pass_expired_at,
                int business_type,
                String org_web,
                String hmac,
                String transactionID
        ) {
            try {
                InternalResponse response = new InternalResponse();
                Database db = new DatabaseImpl();
                DatabaseResponse res = db.createUser(
                        email,
                        created_user_email,
                        enterprise_id,
                        role_name,
                        pass_expired_at,
                        business_type,
                        org_web,
                        hmac,
                        transactionID);

                if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                    String message = null;
                    if (LogHandler.isShowErrorLog()) {
                        message = QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                                res.getStatus(),
                                "en",
                                 null);
                        LogHandler.error(CreateAccount.class,
                                "TransactionID:" + transactionID
                                + "\nCannot create User - Detail:" + message);
                    }
                    return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                            message
                    );
                }
                response.setStatus(PaperlessConstant.HTTP_CODE_SUCCESS);
                response.setData((String)res.getObject());
                return response;
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
        
        public static void main(String[] args){
            InternalResponse res = CreateUser.createUser(
                    "khanhpx@mobile-id.vn",
                    "vyhlt@huflit.edu.com",
                    3,
                    "OWNER",
                    100000,
                    3,
                    "https://paperless.mobile-id.vn",
                    "HMAC",
                    "transactionID");
            System.out.println(res.getData());
        }
    }
}
