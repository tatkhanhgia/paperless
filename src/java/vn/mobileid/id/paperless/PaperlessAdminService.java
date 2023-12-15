
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
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import vn.mobileid.id.eid.object.JWT_Authenticate;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.InternalResponse;
import static vn.mobileid.id.paperless.PaperlessService.verifyToken;
import vn.mobileid.id.paperless.kernel.CheckPasswordRule;
import vn.mobileid.id.paperless.kernel.ProcessTrustManager;
import vn.mobileid.id.paperless.kernel.ManageTokenWithDB;
import vn.mobileid.id.paperless.kernel.ManageUser;
import vn.mobileid.id.paperless.kernel.process.ProcessEID_JWT;
import vn.mobileid.id.paperless.kernelADMIN.CreateAccount;
import vn.mobileid.id.paperless.kernelADMIN.GetAccount;
import vn.mobileid.id.paperless.kernelADMIN.ResendActivation;
import vn.mobileid.id.paperless.kernelADMIN.VerifyEmail;
import vn.mobileid.id.paperless.kernel_v2.GetUser;
import vn.mobileid.id.paperless.object.enumration.BusinessType;
import vn.mobileid.id.paperless.object.enumration.Language;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.Enterprise;
import vn.mobileid.id.paperless.objects.JWT_Request;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.serializer.CustomListAccount;
import vn.mobileid.id.utils.Utils;

/**
 * Using for check valid of data!!
 *
 * @author GiaTK
 */
public class PaperlessAdminService {

    //<editor-fold defaultstate="collapsed" desc="Get token SSO">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create Account">
    public static InternalResponse createAccount(
            final HttpServletRequest request,
            String payload,
            String transactionID) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException, Exception {
        //<editor-fold defaultstate="collapsed" desc="Check Token">
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
        //</editor-fold>
        
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
            Date now = Date.from(Instant.now().plusSeconds(PaperlessConstant.password_expired_at));
            response = CreateAccount.createAccount(
                    jwtdata.getEmail(),
                    account.getUser_password(),
                    jwtdata.getPhone_number(),
                    jwtdata.getName(),
                    jwtdata.getDocument_number(), //Document Number
                    ent.getId(), //int Enterprise_id
                    ent.getName(), //enterprise_name or id
                    "USER",
                    now, //Date when expired
                    BusinessType.BUSINESS.getType(), //int - businessType
                    "https://paperless.mobile-id.vn",
                    transactionID);
            response.setEnterprise(ent);
            return response;
        }

        //FLOW CREATE ACCOUNT BASE ON PAYLOAD    
        Date now = Date.from(Instant.now().plusSeconds(PaperlessConstant.password_expired_at));
        response = CreateAccount.createAccount(
                account.getUser_email(),
                account.getUser_password(),
                account.getMobile_number(),
                account.getUser_name(),
                null, //Document Number
                ent.getId(), //int Enterprise_id
                ent.getName(), //enterprise_name or id
                "USER",
                now, //int 
                BusinessType.BUSINESS.getType(), //int - businessType
                "https://paperless.mobile-id.vn",
                transactionID);
        response.setEnterprise(ent);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Verify Email">
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

        response = VerifyEmail.verifyEmail(
                account.getUser_email(),
                account.getAuthorization_code(),
                transactionID);
        response.setData(account);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Accounts">
    public static InternalResponse getAccounts(
            final HttpServletRequest request,
            String transactionID
    ) throws Exception {
        //Check admin token or user token
        String authorizeHeader = Utils.getRequestHeader(request, "Authorization");
        String token = null;
        User user_info = null;
        Enterprise ent = null;
        try {
            authorizeHeader = authorizeHeader.replace("Bearer ", "");
            token = new String(Base64.getUrlDecoder().decode(authorizeHeader), "UTF-8");

            InternalResponse response = verifyADMINToken(request, transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
                return response;
            }
            ent = (Enterprise) response.getData();
        } catch (Exception ex) {
            try {
                InternalResponse response = verifyToken(request, transactionID);
                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
                    return response;
                }
                user_info = response.getUser();
            } catch (Exception e) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                                PaperlessConstant.SUBCODE_INVALID_TOKEN,
                                "en",
                                null));
            }
        }

        //Process headers
        String x_user_email = Utils.getRequestHeader(request, "x-user-email");
        String x_enterprise_name = Utils.getRequestHeader(request, "x-enterprise-name");
        String role = Utils.getRequestHeader(request, "x-search-role");
        String URI = request.getRequestURI();
        URI = URI.replaceFirst(".*accounts/", "");
        String[] data = URI.split("/");
        String status = "0,1";
        String statusFromUrl = data[0];
        if (statusFromUrl != null) {
            switch (statusFromUrl) {
                case "INACTIVE": {
                    status = "0";
                    break;
                }
                case "ACTIVE": {
                    status = "1";
                    break;
                }
                default: {
                    status = "0,1";
                    break;
                }
            }
        }

        int page_no;
        int record;
        int numberOfRecords = PaperlessConstant.DEFAULT_ROW_COUNT;
        try {
            page_no = (data[1] == null ? 0 : Integer.parseInt(data[1]));
            record = (data[2] == null ? 0 : Integer.parseInt(data[2]));
        } catch (Exception e) {
            page_no = 0;
            record = 0;
        }
        try {
            numberOfRecords = Integer.parseInt(Utils.getRequestHeader(request, "x-number-records"));
            page_no = 0;
            record = 0;
        } catch (Exception ex) {
            numberOfRecords = PaperlessConstant.DEFAULT_ROW_COUNT;
        }

        //Tồn tại x_user_email => get về 1 account
        if (!Utils.isNullOrEmpty(x_user_email)) {
            if (ent == null) {
                InternalResponse response = GetAccount.getAccount(
                        x_user_email,
                        null, //mobile number
                        null, //name
                        null, //enterprise name
                        user_info.getAid(),
                        transactionID);
                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return response;
                }
                List<Account> accounts = (List<Account>) response.getData();
                CustomListAccount custom = new CustomListAccount(
                        accounts,
                        page_no,
                        record);
                HashMap<String, Object> hashmap = new HashMap<>();
                hashmap.put("x-total-records", accounts.size());
                response.setHeaders(hashmap);
                response.setMessage(new ObjectMapper().writeValueAsString(custom));
                return response;
            } else {
                InternalResponse response = GetAccount.getAccount(
                        x_user_email,
                        null, //mobile number
                        null, //name
                        null, //enterprise name
                        ent.getId(),
                        transactionID);
                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return response;
                }
                List<Account> accounts = (List<Account>) response.getData();
                CustomListAccount custom = new CustomListAccount(
                        accounts,
                        page_no,
                        record);
                HashMap<String, Object> hashmap = new HashMap<>();
                hashmap.put("x-total-records", accounts.size());
                response.setHeaders(hashmap);
                response.setMessage(new ObjectMapper().writeValueAsString(custom));
                response.setEnterprise(ent);
                return response;
            }
        } else {
            //Get về hàng loạt
            InternalResponse res = GetAccount.getAccounts(
                    ent == null ? user_info.getAid() : ent.getId(),
                    role==null?"31,32,33,34":role,
                    (page_no <= 1) ? 0 : (page_no - 1) * record, //offset
                    record == 0 ? numberOfRecords : record,
                    transactionID);
            if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return res;
            }

            //Parse to client
            List<Account> accounts = (List<Account>) res.getData();
            CustomListAccount custom = new CustomListAccount(
                    accounts,
                    page_no,
                    record);
            HashMap<String, Object> hashmap = new HashMap<>();
            hashmap.put("x-total-records", accounts.size());
            res.setHeaders(hashmap);
            res.setMessage(new ObjectMapper().writeValueAsString(custom));
            res.setEnterprise(ent);
            return res;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Forgot Password">
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
                Language.English.getId(),
                transactionID);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Set New Password">
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

        //<editor-fold defaultstate="collapsed" desc="Check complexity of Password">
        response = CheckPasswordRule.checkComplexOfPassword(account.getUser_password());
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        //</editor-fold>

        response = ManageUser.setNewPassword(
                Utils.getRequestHeader(request, "x-security-code"),
                account.getUser_password(),
                transactionID);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Set New Password for User (without login as scope)">
    public static InternalResponse setNewPasswordForUser(
            final HttpServletRequest request,
            String payload,
            String transactionID
    ) throws Exception {
        //Check valid token
        InternalResponse response = verifyADMINToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        Enterprise ent = response.getEnterprise();

        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //<editor-fold defaultstate="collapsed" desc="Check data in payload">
        String email = Utils.getFromJson("user_email", payload);
        String oldPass = Utils.getFromJson("user_old_password", payload);
        String newPass = Utils.getFromJson("user_new_password", payload);
        if (Utils.isNullOrEmpty(email)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_MISSING_USER_EMAIL,
                            "en",
                            transactionID)
            );
        }
        if (Utils.isNullOrEmpty(oldPass) || Utils.isNullOrEmpty(newPass)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_MISSING_OLD_OR_NEW_PASSWORD,
                            "en",
                            transactionID)
            );
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Check complexity of Password">
        response = CheckPasswordRule.checkComplexOfPassword(newPass);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            response.setEnterprise(ent);
            return response;
        }
        //</editor-fold>

        response = ManageUser.setNewPasswordWithoutLogin(email, oldPass, newPass, transactionID);
        response.setEnterprise(ent);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Verify Admin Token">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Verify Trust Manager">
    public static InternalResponse verifyTrustManager(
            String jwt,
            String transactionID
    ) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException, Exception {
        InternalResponse res = ProcessTrustManager.verifyTrustManager(jwt, transactionID);
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Resend Activtion">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Total record of Accounts">
    public static InternalResponse getTotalRecordOfAccounts(
            final HttpServletRequest request,
            String transactionID
    ) throws Exception {
        //Check admin token or user token
        String authorizeHeader = Utils.getRequestHeader(request, "Authorization");
        String token = null;
        User user_info = null;
        Enterprise ent = null;
        try {
            authorizeHeader = authorizeHeader.replace("Bearer ", "");
            token = new String(Base64.getUrlDecoder().decode(authorizeHeader), "UTF-8");

            InternalResponse response = verifyADMINToken(request, transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
                return response;
            }
            ent = (Enterprise) response.getData();
        } catch (Exception ex) {
            try {
                InternalResponse response = verifyToken(request, transactionID);
                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
                    return response;
                }
                user_info = response.getUser();
            } catch (Exception e) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                                PaperlessConstant.SUBCODE_INVALID_TOKEN,
                                "en",
                                null));
            }
        }

        //Process headers        
        String URI = request.getRequestURI();
        URI = URI.replaceFirst(".*accounts/", "");

        String role = Utils.getRequestHeader(request, "x-search-role");

        //Process
        InternalResponse res = GetUser.getTotalRecordUser(
                user_info!=null?user_info.getAid():ent.getId(),
                role==null?"31,32,33,34":role,
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        long records = (long) res.getData();

        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("x-total-records", records);
        res.setHeaders(hashmap);
        res.setMessage("{\"x-total-record\":" + records + "}");
        res.setEnterprise(ent);
        return res;
    }
    //</editor-fold>
}
