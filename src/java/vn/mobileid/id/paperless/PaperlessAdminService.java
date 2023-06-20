/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import javax.servlet.http.HttpServletRequest;
import vn.mobileid.id.eid.object.JWT_Authenticate;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.kernel.ProcessTrustManager;
import vn.mobileid.id.paperless.kernel.ManageTokenWithDB;
import vn.mobileid.id.paperless.kernel.ManageUser;
import vn.mobileid.id.paperless.kernel.process.ProcessEID_JWT;
import vn.mobileid.id.paperless.kernelADMIN.CreateAccount;
import vn.mobileid.id.paperless.kernelADMIN.GetAccount;
import vn.mobileid.id.paperless.kernelADMIN.ResendActivation;
import vn.mobileid.id.paperless.kernelADMIN.VerifyEmail;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.Enterprise;
import vn.mobileid.id.paperless.objects.JWT_Request;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.utils.Utils;

/**
 * Using for check valid of data!!
 *
 * @author GiaTK
 */
public class PaperlessAdminService {

    public static InternalResponse getTokenSSO(
            final HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyADMINToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        //Check input
        if (Utils.isNullOrEmpty(payload)) {            
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        response = ManageTokenWithDB.processLoginSSO(                
                payload,
                transactionID);
        return response;
    }

    public static InternalResponse createAccount(
            final HttpServletRequest request,
            String payload,
            String transactionID) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException, Exception {
        //Check valid token        
        InternalResponse response = verifyADMINToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        Enterprise ent = (Enterprise) response.getData();

        ObjectMapper mapper = new ObjectMapper();
        Account account = new Account();
        JWT_Request jwt = new JWT_Request();
        try {
            account = mapper.readValue(payload, Account.class);
            jwt = mapper.readValue(payload, JWT_Request.class);
        } catch (Exception e) {
            LogHandler.error(
                    PaperlessAdminService.class,
                    transactionID,
                    "Cannot parse payload",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check valid data 
        InternalResponse result = null;
        result = CreateAccount.checkDataAccount(account, (jwt != null && jwt.getJwt_token() != null));
        if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return result;
        }
        if (jwt != null && jwt.getJwt_token() != null) {
            result = verifyTrustManager(
                    jwt.getJwt_token(),
                    transactionID);
            if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return result;
            }
        }

        //FLOW CREATE ACCOUNT BASE ON JWT
        JWT_Authenticate jwtdata = new JWT_Authenticate();
        if (jwt != null && jwt.getJwt_token() != null && !jwt.getJwt_token().isEmpty()) {
            try {
                InternalResponse data = ProcessEID_JWT.getInfoJWT(jwt.getJwt_token());
                if (data.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return data;
                }
                jwtdata = (JWT_Authenticate) data.getData();
            } catch (Exception ex) {
                LogHandler.error(
                        PaperlessAdminService.class,
                        transactionID,
                        "Cannot parse JWT data from token input!",
                        ex);
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_FAIL,
                                PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                                PaperlessMessageResponse.getLangFromJson(payload),
                                null));
            }

            if (jwtdata.getEmail() != null && jwtdata.getEmail().isEmpty()) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_INVALID_PARAMS_JWT,
                                PaperlessConstant.SUBCODE_MISSING_EMAIL_IN_JWT,
                                PaperlessMessageResponse.getLangFromJson(payload),
                                null));
            }
            return CreateAccount.createAccount(
                    jwtdata.getEmail(),
                    account.getUser_password(),
                    jwtdata.getPhone_number(),
                    jwtdata.getName(),
                    jwtdata.getDocument_number(), //Document Number
                    ent.getId(), //int Enterprise_id
                    ent.getName(), //enterprise_name or id
                    "OWNER",
                    (int) PaperlessConstant.password_expired_at, //int 
                    PaperlessConstant.BUSINESSTYPE_BUSINESS, //int - businessType
                    "https://paperless.mobile-id.vn",
                    transactionID);
        }

        //FLOW CREATE ACCOUNT BASE ON PAYLOAD        
        return CreateAccount.createAccount(
                account.getUser_email(),
                account.getUser_password(),
                account.getMobile_number(),
                account.getUser_name(),
                null, //Document Number
                ent.getId(), //int Enterprise_id
                ent.getName(), //enterprise_name or id
                "OWNER",
                (int) PaperlessConstant.password_expired_at, //int 
                PaperlessConstant.BUSINESSTYPE_BUSINESS, //int - businessType
                "https://paperless.mobile-id.vn",
                transactionID);
    }

    public static InternalResponse verifyEmail(
            final HttpServletRequest request,
            String payload,
            String transactionID
    ) throws Exception {
        //Check valid token
        InternalResponse response = verifyADMINToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        //Parse Data
        Account account = new Account();
        try {
            account = new ObjectMapper().readValue(payload, Account.class);
        } catch (Exception ex) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            "en",
                            null));
        }

        //Check data
        InternalResponse res = VerifyEmail.checkData(account);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        return VerifyEmail.verifyEmail(
                account.getUser_email(),
                account.getAuthorization_code(),
                transactionID);
    }

    public static InternalResponse getAccounts(
            final HttpServletRequest request,
            String transactionID
    ) throws Exception {
        //Check valid token
        InternalResponse response = verifyADMINToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        Enterprise ent = (Enterprise) response.getData();
        
        //Get headers
        String x_user_email = Utils.getRequestHeader(request, "x-user-email");
        String x_enterprise_name = Utils.getRequestHeader(request, "x-enterprise-name");

        return GetAccount.getAccount(
                x_user_email,
                null, //mobile number
                null, //name
                null,   //enterprise name
                ent.getId(),
                transactionID);

    }

    public static InternalResponse forgotPassword(
            final HttpServletRequest request,
            String payload,
            String transactionID
    ) throws Exception {
        //Check valid token
        InternalResponse response = verifyADMINToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Mapper data
        Account account = new Account();
        try {
            account = new ObjectMapper().readValue(payload, Account.class);
        } catch (Exception ex) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        return ManageUser.forgotPassword(
                account.getUser_email(),
                PaperlessConstant.LANGUAGE_EN,
                transactionID);
    }

    public static InternalResponse setNewPassword(
            final HttpServletRequest request,
            String payload,
            String transactionID
    ) throws Exception {
        //Check valid token
        InternalResponse response = verifyADMINToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Mapper data
        Account account = new Account();
        try {
            account = new ObjectMapper().readValue(payload, Account.class);
        } catch (Exception ex) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        if (Utils.getRequestHeader(request, "x-security-code") == null) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_MISSING_X_SECURITY_CODE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        return ManageUser.setNewPassword(
                Utils.getRequestHeader(request, "x-security-code"),
                account.getUser_password(),
                transactionID);
    }

    public static InternalResponse verifyADMINToken(
            final HttpServletRequest request,
            String transactionID
    ) throws Exception {
        InternalResponse response = ManageTokenWithDB.processVerify(
                request,
                transactionID,
                true);
        return response;
    }

    public static InternalResponse verifyTrustManager(
            String jwt,
            String transactionID
    ) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException, Exception {
        InternalResponse res = ProcessTrustManager.verifyTrustManager(jwt, transactionID);
        return res;
    }

    public static InternalResponse resendActivation(
            final HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyADMINToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        Enterprise ent = (Enterprise) response.getData();

        //Check input
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        if (Utils.getFromJson("user_email", payload) == null) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_MISSING_USER_EMAIL,
                            "en",
                            null));
        }

        response = ResendActivation.resend(
                Utils.getFromJson("user_email", payload),
                ent.getId(),
                transactionID);
        return response;
    }
}
