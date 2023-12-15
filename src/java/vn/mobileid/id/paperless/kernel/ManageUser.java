/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.email.SendMail;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.EmailTemplate;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.utils.Utils;
import vn.mobileid.id.paperless.kernel_v2.UpdateUser;
import vn.mobileid.id.paperless.object.enumration.Language;

/**
 *
 * @author GiaTK
 */
public class ManageUser {

    //<editor-fold defaultstate="collapsed" desc="Forgot Password">
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
        InternalResponse response = vn.mobileid.id.paperless.kernel_v2.GetUser.getStatusUser(email, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        //Get data User
        Account account = (Account) response.getData();

        //If User/Account has been locked => return error
        try {
            if (account.isLocked_enabled()) {
                System.out.println("Date:"+account.getLocked_at());
                Date lockAt = account.getLocked_at();
                int minute_lock = PolicyConfiguration.getInstant().getPasswordExpired().getMinute_lock();
                Date expected_time = new Date(lockAt.toInstant().plus(minute_lock, ChronoUnit.MINUTES).toEpochMilli());
                Date now = Date.from(Instant.now());
                System.out.println("Now:"+now);
                if (expected_time.after(now)) {
                    return new InternalResponse(
                            PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                            PaperlessMessageResponse.getErrorMessage(
                                    PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                                    PaperlessConstant.SUBCODE_CANNOT_FORGOT_PASSWORD_USER_HAS_BEEN_LOCKED,
                                    "en",
                                    transactionID)
                    );
                }
            }
        } catch (Exception ex) {
            System.out.println("Cannot check lock of Account => Continue forgot password");
        }

        String OTP = Utils.generateRandomString(9);

        //Get email template
        response = GetEmailTemplate.getEmailTemplate(
                (language < 1 || language > 2)
                        ? Language.English.getId() : language,
                vn.mobileid.id.paperless.object.enumration.EmailTemplate.EMAIL_FORGOT_PASSWORD.getName(),
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
        mail.appendAuthorizationCode(OTP);
        mail.appendLink(PolicyConfiguration.getInstant().getHostURLofForgotPassword().getUrl());
        mail.start();

        //Append queue
        if (!Resources.getQueueForgotPassword().isEmpty()) {
            if (Resources.getQueueForgotPassword().containsValue(email)) {
                Iterator<Entry<String, String>> iter = Resources.getQueueForgotPassword().entrySet().iterator();
                while (iter.hasNext()) {
                    Entry<String, String> entry = iter.next();
                    String otp = entry.getKey();
                    String email_ = entry.getValue();
                    if (email_.equals(email)) {
                        iter.remove();
                    }
                }
            }
        }
        Resources.getQueueForgotPassword().put(OTP, email);

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Set new Password">
    /**
     * Set new password to user with OTP from user
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Set new Password without Login">
    /**
     * Set new password to user with OTP from user
     *
     * @param email
     * @param passwordOld
     * @param passwordNew
     * @param transactionID
     * @return
     * @throws Exception
     */
    public static InternalResponse setNewPasswordWithoutLogin(
            String email,
            String passwordOld,
            String passwordNew,
            String transactionID
    ) throws Exception {
        InternalResponse response = UpdateUser.updateUserPassword(
                email,
                passwordOld,
                passwordNew,
                transactionID);

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        return response;
    }
    //</editor-fold>
}
