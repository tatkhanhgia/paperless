/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernelADMIN;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.email.SendMail;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.kernel.GetEnterpriseInfo;
import vn.mobileid.id.paperless.kernel.GetUser;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.EmailTemplate;
import vn.mobileid.id.paperless.objects.Enterprise;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ResendActivation {

    public static InternalResponse resend(
            String email,      
            int enterprise_id,
            String transactionID
    ) {
        try {
//            //Get info enterprise (get ID owner)
            InternalResponse res = null;
            //Get ID Owner of enterprise
            res = GetUser.getUser(
                    email,
                    0,
                    enterprise_id,
                    transactionID,
                    false);
            if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return res;
            }
            Account user = (Account) res.getData();
            EmailTemplate template;

            DatabaseResponse db = new DatabaseImpl().getEmailTemplate(
                    2,
                    PaperlessConstant.EMAIL_SEND_PASSWORD,
                    transactionID);
            if (db.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                if (LogHandler.isShowErrorLog()) {
                    message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            res.getStatus(),
                            "en",
                            null);
                    LogHandler.error(CreateAccount.class,
                            "TransactionID:" + transactionID
                            + "\nCannot get Email Template - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            template = (EmailTemplate) db.getObject();
            String authorizeCode = Utils.generateOneTimePassword(6);
            //Send mail
            SendMail send = new SendMail(
                    email,
                    template.getSubject(),
                    template.getBody(),
                    null,
                    null,
                    null
            );
            send.appendAuthorizationCode(authorizeCode);
            send.appendNameUser(user.getUser_name());
            
            send.start();
            Resources.getQueueAuthorizeCode().put(email,authorizeCode);

            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, new String(""));
        } catch (Exception ex) {
            ex.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(ResendActivation.class,
                        "TransactionID:" + transactionID
                        + "\nUNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(ex));
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }
   
}
