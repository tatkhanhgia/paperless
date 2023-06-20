/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernelADMIN;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.email.SendMail;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.kernel.GetEmailTemplate;
import vn.mobileid.id.paperless.kernel.GetUser;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.EmailTemplate;
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

            if (user.isVerified()) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                                PaperlessConstant.SUBCODE_USER_ALREADY_VERIFIED,
                                "en",
                                null)
                );
            }

            EmailTemplate template;

            res = GetEmailTemplate.getEmailTemplate(
                    PaperlessConstant.LANGUAGE_EN,
                    PaperlessConstant.EMAIL_SEND_PASSWORD,
                    transactionID);

            if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return res;
            }

            template = (EmailTemplate) res.getData();
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
            send.appendNameUser(user.getUser_name() == null ? user.getUser_email() : user.getUser_name());

            send.start();
            if (Resources.getQueueAuthorizeCode().containsKey(email)) {
                Resources.getQueueAuthorizeCode().replace(email, authorizeCode);
            } else {
                Resources.getQueueAuthorizeCode().put(email, authorizeCode);
            }

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    "");
        } catch (Exception ex) {
            LogHandler.error(
                    ResendActivation.class,
                    transactionID,
                    "Cannot Resend Activation!!",
                    ex);
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_500,
                PaperlessConstant.INTERNAL_EXP_MESS);
    }
}
