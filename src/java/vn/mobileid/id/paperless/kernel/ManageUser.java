/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.email.SendMail;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.EmailTemplate;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ManageUser {

    /**
     * Forgot password of user
     *
     * @param email
     * @param language
     * @param transactionID
     * @return
     * @throws Exception
     */
    public static InternalResponse forgotPassword(
            String email,
            int language,
            String transactionID
    ) throws Exception {
        InternalResponse response = GetUser.getStatusUser(
                email,
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        //Get data User
        Account account = (Account) response.getData();

        String OTP = Utils.generateRandomString(9);

        //Get email template
        response = GetEmailTemplate.getEmailTemplate(
                (language < 1 || language > 2) ? PaperlessConstant.LANGUAGE_EN : language,
                PaperlessConstant.EMAIL_FORGOT_PASSWORD,
                transactionID);

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        EmailTemplate template = (EmailTemplate) response.getData();
        SendMail mail = new SendMail();
        mail.setSendTo(email);
        mail.setSubject(template.getSubject());
        mail.setContent(template.getBody());
        mail.appendNameUser(account.getUser_name());
        mail.appendEmailUser(email);
        mail.appendLink(OTP);
        mail.start();

        //Append queue
        if(Resources.getQueueForgotPassword().containsValue(email)){
            for(String otp: Resources.getQueueForgotPassword().keySet()){
                if(Resources.getQueueForgotPassword().get(otp).equals(email)){
                    Resources.getQueueForgotPassword().remove(otp);
                }
            }
        }
        Resources.getQueueForgotPassword().put(OTP, email);

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "");
    }

    /**
     * Set new password to user
     *
     * @param otp
     * @param password
     * @param transactionID
     * @return
     * @throws Exception
     */
    public static InternalResponse setNewPassword(
            String otp,
            String password,
            String transactionID
    ) throws Exception {
        if (!Resources.getQueueForgotPassword().containsKey(otp)) {
            String message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_FAIL,
                    PaperlessConstant.SUBCODE_RESET_PASSWORD_ACCOUNT_AGAIN,
                    "en",
                    null);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message);
        }
        String email = Resources.getQueueForgotPassword().get(otp);
        InternalResponse response = UpdateUser.updateUserPassword(
                email,
                password,
                transactionID);

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        Resources.getQueueForgotPassword().remove(otp);
        return response;
    }
}
