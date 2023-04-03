/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import vn.mobileid.id.eid.object.JWT_Authenticate;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.kernel.ManageTokenWithDB;
import vn.mobileid.id.paperless.kernel.process.ProcessEID_JWT;
import vn.mobileid.id.paperless.kernelADMIN.CreateAccount;
import vn.mobileid.id.paperless.kernelADMIN.GetAccount;
import vn.mobileid.id.paperless.kernelADMIN.VerifyEmail;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.JWT_Request;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.utils.Utils;

/**
 * Using for check valid of data!!
 *
 * @author GiaTK
 */
public class PaperlessAdminService {

    final private static Properties appInfo = Configuration.getInstance().getAppInfo();

    public static InternalResponse getTokenSSO(
            final HttpServletRequest request,
            String payload,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        //Check input
        if (Utils.isNullOrEmpty(payload)) {
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(PaperlessAdminService.class, transactionID, "No Payload found!!");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        response = ManageTokenWithDB.processLoginSSO(
                request,
                payload,
                transactionID);
        return response;
    }

    public static InternalResponse createAccount(
            final HttpServletRequest request,
            String payload,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        if (Utils.isNullOrEmpty(payload)) {
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(PaperlessAdminService.class, transactionID, "No Payload found!!");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        ObjectMapper mapper = new ObjectMapper();
        Account account = new Account();
        JWT_Request jwt = new JWT_Request();
        JWT_Authenticate jwtdata = new JWT_Authenticate();
        try {
            account = mapper.readValue(payload, Account.class);
            jwt = mapper.readValue(payload, JWT_Request.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessAdminService.class, transactionID, "Cannot parse payload");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
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
        
        //FLOW CREATE ACCOUNT BASE ON JWT
        if (jwt != null && jwt.getJwt_token() != null && !jwt.getJwt_token().isEmpty()) {
            try {
                InternalResponse data = ProcessEID_JWT.getInfoJWT(jwt.getJwt_token());
                if (data.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return data;
                }
                jwtdata = (JWT_Authenticate) data.getData();
            } catch (Exception ex) {
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(PaperlessAdminService.class, transactionID, "Cannot parse JWT data from token input!");
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                        PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                                PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                                PaperlessMessageResponse.getLangFromJson(payload),
                                null));
            }

            if (jwtdata.getEmail() != null && jwtdata.getEmail().isEmpty()) {
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(PaperlessAdminService.class, transactionID, "Missing Email in jwt and payload!");
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                        PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_JWT,
                                PaperlessConstant.SUBCODE_MISSING_EMAIL_IN_JWT,
                                PaperlessMessageResponse.getLangFromJson(payload),
                                null));
            }

            try {
                return CreateAccount.createAccount(
                        jwtdata.getEmail(),
                        account.getUser_password(),
                        account.getMobile_number(),
                        jwtdata.getName(),
                        jwtdata.getDocument_number(), //Document Number
                        3, //int Enterprise_id
                        account.getEnterprise_name(), //enterprise_name or id
                        "OWNER",
                        (int) PaperlessConstant.password_expired_at, //int 
                        PaperlessConstant.BUSINESSTYPE_BUSINESS, //int - businessType
                        "https://paperless.mobile-id.vn",
                        transactionID);
            } catch (Exception e) {
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(PaperlessAdminService.class, transactionID, "Cannot create new Workflow");
                }
                return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
            }
        }

        //FLOW CREATE ACCOUNT BASE ON PAYLOAD
        try {
            return CreateAccount.createAccount(
                    account.getUser_email(),
                    account.getUser_password(),
                    account.getMobile_number(),
                    account.getUser_name(),
                    null, //Document Number
                    0, //int Enterprise_id
                    account.getEnterprise_name(), //enterprise_name or id
                    "OWNER",
                    (int) PaperlessConstant.password_expired_at, //int 
                    PaperlessConstant.BUSINESSTYPE_BUSINESS, //int - businessType
                    "https://paperless.mobile-id.vn",
                    transactionID);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessAdminService.class, transactionID, "Cannot create new Workflow");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse verifyEmail(
            final HttpServletRequest request,
            String payload,
            String transactionID
    ){
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        if (Utils.isNullOrEmpty(payload)) {
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(PaperlessAdminService.class, transactionID, "No Payload found!!");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }
        
        //Parse Data
        Account account = new Account();
        try{
            account = new ObjectMapper().readValue(payload, Account.class);            
        } catch (Exception ex){
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(PaperlessAdminService.class, transactionID, "Cannot parse payload!!");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            "en",
                            null));
        }
        
        //Check data
        InternalResponse res = VerifyEmail.checkData(account);
        if( res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            return res;
        }
        
        try{
            return VerifyEmail.verifyEmail(account.getUser_email(), account.getAuthorization_code(), transactionID);
        } catch (Exception ex){
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessAdminService.class, transactionID, "Cannot verify Email");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }
    
    public static InternalResponse getAccounts(
            final HttpServletRequest request,            
            String transactionID
    ){
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }              
        
        //Get headers
        String x_user_email = Utils.getRequestHeader(request, "x-user-email");
        String x_enterprise_name = Utils.getRequestHeader(request,"x-enterprise-name");
                
        try{
            return GetAccount.getListAccount(
                    x_user_email,
                    null, //mobile number
                    null, //name
                    x_enterprise_name,
                    transactionID);
        } catch (Exception ex){
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessAdminService.class, transactionID, "Cannot verify Email");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }
    
    public static InternalResponse verifyToken(
            final HttpServletRequest request,
            String transactionID
    ) {
        InternalResponse response = ManageTokenWithDB.processVerify(
                request,
                transactionID,
                true);
        return response;
    }
}
