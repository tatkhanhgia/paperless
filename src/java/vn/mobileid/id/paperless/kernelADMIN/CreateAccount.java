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
import vn.mobileid.id.paperless.kernel.GetEmailTemplate;
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
public class CreateAccount {

    public static InternalResponse checkDataAccount(
            Account account,
            boolean isJWTTokenExisted) {
        if (!isJWTTokenExisted && (account.getEnterprise_name() == null || account.getEnterprise_name().isEmpty())) {
//            LogHandler.error(CreateAccount.class, "Missing Enterprise name in payload!!");
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_MISSING_ENTERPRISE_DATA,
                            "en",
                            null));
        }
        if (!isJWTTokenExisted) {
//            LogHandler.error(CreateAccount.class, "Missing email data in JWT!!");
            if (account.getUser_email() == null || account.getUser_email().isEmpty()) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                                PaperlessConstant.SUBCODE_MISSING_USER_EMAIL,
                                "en",
                                null));
            }
        }
        if (account.getUser_password() == null || account.getUser_password().isEmpty()) {
//            LogHandler.error(CreateAccount.class, "Missing Password!!");
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_MISSING_USER_NAME_OR_PASSWORD,
                            "en",
                            null));
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_SUCCESS,
                        PaperlessConstant.SUBCODE_SUCCESS,
                        "en",
                        null));

    }

    public static InternalResponse createAccount(
            String email,
            String password,
            String mobile_number,
            String name,
            String doc_num,
            int enterprise_id,
            String enteprise_name,
            String role_name,
            int password_expired_at,
            int business_type,
            String org_web,
            String transactionID
    ) throws Exception {
        //Get info enterprise (get ID owner)
        InternalResponse res = GetEnterpriseInfo.getEnterprise(
                enteprise_name,
                enterprise_id,
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        if (enterprise_id <= 0) {
            enterprise_id = ((Enterprise) res.getData()).getId();
        }

        //Get ID Owner of enterprise
        res = GetUser.getUser(
                null,
                ((Enterprise) res.getData()).getOwner_id(),
                enterprise_id,
                transactionID,
                true);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        //Processing
        res = CreateUser.createUser(
                ((User) res.getData()).getEmail(),
                password,
                mobile_number,
                email,
                name,
                enterprise_id,
                role_name,
                password_expired_at,
                business_type,
                org_web,
                "HMAC",
                transactionID);

        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        EmailTemplate template;

//            if (password == null || password.isEmpty()) {
//                //Get password
//                res = GetAuthenticatePassword.getAuthenticatePassword(
//                        email,
//                        2,
//                        PaperlessConstant.EMAIL_SEND_PASSWORD,
//                        transactionID);
//
//                if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//                    return res;
//                }
//                template = (EmailTemplate) res.getData();
//                //Send mail
//                SendMail send = new SendMail(
//                        email,
//                        template.getSubject(),
//                        template.getBody(),
//                        null,
//                        null,
//                        null
//                );
//                send.appendAuthorizationCode(template.getPassword());
//                send.appendNameUser(name);
//                if (doc_num != null) {
//                    send.appendDocNumber(doc_num);
//                }
//                send.start();
//            } else {
        res = GetEmailTemplate.getEmailTemplate(
                PaperlessConstant.LANGUAGE_EN,
                PaperlessConstant.EMAIL_SEND_PASSWORD,
                transactionID);
        if(res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
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
        send.appendNameUser(name == null ? email : name);
        if (doc_num != null) {
            send.appendDocNumber(doc_num);
        }
        send.start();
        Resources.getQueueAuthorizeCode().put(email, authorizeCode);
        
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "");
    }

    public static class CreateUser {

        public static InternalResponse createUser(
                String email,
                String password,
                String mobile_number,
                String created_user_email,
                String created_user_name,
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
                        password,
                        mobile_number,
                        created_user_email,
                        created_user_name,
                        enterprise_id,
                        role_name,
                        pass_expired_at,
                        business_type,
                        org_web,
                        hmac,
                        transactionID);

                if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                    String message = null;
                    message = PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            res.getStatus(),
                            "en",
                            null);
                    return new InternalResponse(
                            PaperlessConstant.HTTP_CODE_FORBIDDEN,
                            message
                    );
                }
                response.setStatus(PaperlessConstant.HTTP_CODE_SUCCESS);
                response.setData((String) res.getObject());
                return response;
            } catch (Exception ex) {
                LogHandler.error(
                        CreateAccount.class,
                        transactionID,
                        "Cannot create User!!",
                        ex);
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_500,
                        PaperlessConstant.INTERNAL_EXP_MESS);
            }
        }
    }

    public static class GetAuthenticatePassword {

        public static InternalResponse getAuthenticatePassword(
                String email,
                int language_id,
                String email_type,
                String transactionID
        ) {
            try {
                InternalResponse response = new InternalResponse();
                Database db = new DatabaseImpl();
                DatabaseResponse res = db.getAuthenticatePassword(
                        email,
                        language_id,
                        email_type,
                        transactionID);

                if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                    String message = null;
                    message = PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            res.getStatus(),
                            "en",
                            null);
//                        LogHandler.error(CreateAccount.class,
//                                "TransactionID:" + transactionID
//                                + "\nCannot get Authenticate Password - Detail:" + message);                    
                    return new InternalResponse(
                            PaperlessConstant.HTTP_CODE_FORBIDDEN,
                            message
                    );
                }
                response.setStatus(PaperlessConstant.HTTP_CODE_SUCCESS);
                response.setData((EmailTemplate) res.getObject());
                return response;
            } catch (Exception ex) {
                LogHandler.error(
                        CreateAccount.class,
                        transactionID,
                        "Cannot Get Authenticate Password!!",
                        ex);

                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_500,
                        PaperlessConstant.INTERNAL_EXP_MESS);
            }
        }

        public static void main(String[] args) {
            InternalResponse res = GetAuthenticatePassword.getAuthenticatePassword(
                    "giatk@mobile-id.vn",
                    2,
                    "email_send_password",
                    "transactionID");
            EmailTemplate a = (EmailTemplate) res.getData();
            System.out.println(a.getPassword());
        }
    }
}
