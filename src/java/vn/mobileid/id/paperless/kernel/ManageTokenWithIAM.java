/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.paperless.kernel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.general.keycloak.KeyCloakInvocation;
import vn.mobileid.id.general.keycloak.obj.KeycloakReq;
import vn.mobileid.id.general.keycloak.obj.KeycloakRes;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.general.Configuration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author ADMIN
 */
public class ManageTokenWithIAM {

    final private static Logger LOG = LogManager.getLogger(ManageTokenWithIAM.class);

    private String URL = Configuration.getInstance().getKeyCloakURL();
    private String Realm = Configuration.getInstance().getKeyCloakRealm();

    public InternalResponse processJSON_getToken(final HttpServletRequest request, String payload) {
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        KeycloakReq object = new KeycloakReq();
        ObjectMapper mapper = new ObjectMapper();
        try {
            object = mapper.readValue(payload, KeycloakReq.class);
        } catch (JsonProcessingException ex) {
            if (LogHandler.isShowErrorLog()) {                
                LOG.error("Cannot parse payload - payload:"+payload);
                LOG.error("Details:"+ex);
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }
        
        //Check Client Secret. If not => found in system
        if (object.getClient_secret() == null) {
            object.setClient_secret(Configuration.getInstance().getKeycloakClient_secret());
        }

        KeyCloakInvocation keycloakServer = new KeyCloakInvocation(
                URL,
                Realm,
                object.getClient_id(),
                object.getClient_secret(),
                object.getGrant_type(),
                null,
                null,
                null,
                object.getUsername(),
                object.getPassword());

        KeycloakRes accessToken = keycloakServer.getAccessToken(null,true);

        InternalResponse response;
        if (accessToken.getAccess_token() != null) {
            response = new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                    PaperlessMessageResponse.getAccessTokenMessage(PaperlessConstant.CODE_SUCCESS,
                            PaperlessConstant.SUBCODE_SUCCESS,
                            "en",
                            accessToken,
                            "transactionID"
                    ));
        } else {
//            int sub_code = ManageTokenWithIAM.convertError(accessToken.getError_description());
//            response = new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
//                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK, sub_code, "EN", null));
            response = new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST, accessToken.getError_description());
        }

        return response;
    }

    public InternalResponse processFORM_getToken(final HttpServletRequest request, String payload) {
        KeycloakReq object = new KeycloakReq();

        //Check Client Secret. If not => found in system
        if (object.getClient_secret() == null) {
            object.setClient_secret(Configuration.getInstance().getKeycloakClient_secret());
        }                

        KeyCloakInvocation keycloakServer = new KeyCloakInvocation(
                URL,
                Realm,
                object.getClient_id(),
                object.getClient_secret(),
                object.getGrant_type(),
                null,
                null,
                null,
                object.getUsername(),
                object.getPassword());

        KeycloakRes accessToken = keycloakServer.getAccessToken(payload,true);

        InternalResponse response;
        if (accessToken.getAccess_token() != null) {
            response = new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                    PaperlessMessageResponse.getAccessTokenMessage(PaperlessConstant.CODE_SUCCESS,
                            PaperlessConstant.SUBCODE_SUCCESS,
                            "en",
                            accessToken,
                            "transactionID"
                    ));
        } else {
//            int sub_code = ManageTokenWithIAM.convertError(accessToken.getError_description());
            response = new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,accessToken.getError_description());
        }

        return response;
    }

    public InternalResponse processJSON_revoke(final HttpServletRequest request, String payload) {
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }
        
        KeycloakReq object = new KeycloakReq();
        ObjectMapper mapper = new ObjectMapper();
        try {
            object = mapper.readValue(payload, KeycloakReq.class);
        } catch (JsonProcessingException ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse payload");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }       
        
        //Check Client Secret. If not => found in system
        if (object.getClient_secret() == null) {
            object.setClient_secret(Configuration.getInstance().getKeycloakClient_secret());
        }

        KeyCloakInvocation keycloakServer = new KeyCloakInvocation(
                URL,
                Realm,
                object.getClient_id(),
                object.getClient_secret(),
                object.getRefreshToken(),
                object.getToken_type_hint());

        KeycloakRes accessToken = keycloakServer.revokeToken(null, true);
                 
        
        InternalResponse response;
        if (accessToken.getAccess_token() != null) {         
            response = new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,"");
        } else {
            int sub_code = ManageTokenWithIAM.convertError(accessToken.getError_description());
            response = new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK, sub_code, "EN", null));
        }

        return response;
    }
    
    public InternalResponse processFORM_revoke(final HttpServletRequest request, String payload) {        
        KeycloakReq object = new KeycloakReq();

        //Check Client Secret. If not => found in system
        if (object.getClient_secret() == null) {
            object.setClient_secret(Configuration.getInstance().getKeycloakClient_secret());
        }

        KeyCloakInvocation keycloakServer = new KeyCloakInvocation(
                URL,
                Realm,
                object.getClient_id(),
                object.getClient_secret(),
                object.getGrant_type(),
                null,
                null,
                null,
                object.getUsername(),
                object.getPassword());

        KeycloakRes accessToken = keycloakServer.revokeToken(payload,true);

        InternalResponse response;
        if (accessToken.getAccess_token() != null) {
            response = new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,"SUCCESS");
        } else {
            int sub_code = ManageTokenWithIAM.convertError(accessToken.getError_description());
            response = new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK, sub_code, "EN", null));
        }

        return response;
    }
    
    private static int convertError(String errorKeycloak) {
        if (errorKeycloak.equalsIgnoreCase("Unsupported grant_type")) {
            //Error in grant_type
            return PaperlessConstant.SUBCODE_UNSUPPORTED_GRANT_TYPE;
        }
        if (errorKeycloak.equalsIgnoreCase("Invalid user credentials")) {
            //Error in Username, error and missing data in Password
            return PaperlessConstant.SUBCODE_INVALID_USER_CREDENTIALS;
        }
        if (errorKeycloak.equalsIgnoreCase("Invalid client secret")) {
            //Error in Client Secret
            return PaperlessConstant.SUBCODE_INVALID_CLIENT_SECRET;
        }
        if (errorKeycloak.equalsIgnoreCase("Invalid client credentials")) {
            //Error and missing data in Client ID
            return PaperlessConstant.SUBCODE_INVALID_CLIENT_CREDENTIALS;
        }
        if(errorKeycloak.contains("grant_type")){
            //Missing data in grant_type
            return PaperlessConstant.SUBCODE_MISSING_GRANT_TYPE;
        }
        if(errorKeycloak.contains("username")){
            return PaperlessConstant.SUBCODE_MISSING_USER_NAME_OR_PASSWORD;
        }
        if(errorKeycloak.contains("Client secret")){
            //Missing client secret
            return PaperlessConstant.SUBCODE_MISSING_CLIENT_SECRET;
        }
        if(errorKeycloak.contains("Token") && errorKeycloak.contains("provided")){
            return PaperlessConstant.SUBCODE_TOKEN_NOT_PROVIDED;
        }
        if(errorKeycloak.contains("Invalid token")){
            return PaperlessConstant.SUBCODE_INVALID_TOKEN;
        }
        if(errorKeycloak.contains("Token is invalid!")){
            return PaperlessConstant.SUBCODE_INVALID_TOKEN;
        }
        if(errorKeycloak.contains("Token is expired!")){
            return PaperlessConstant.SUBCODE_TOKEN_EXPIRED;
        }
        if(errorKeycloak.contains("INTERNAL ERROR")){
            return PaperlessConstant.SUBCODE_INTERNAL_ERROR;
        }
        return PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE;
    }

    public InternalResponse verifyAccessToken(final HttpServletRequest request){
        KeycloakReq object = new KeycloakReq();      

        //Get Access Token
        String token = request.getHeader("Authorization");
        if(LogHandler.isShowDebugLog()){
            LOG.info("Checking Header!!");            
            LOG.info("Token:"+token);
        } 
        
        if(token == null || token.contains("null")){
            return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_MISSING_ACCESS_TOKEN, "en", null)
            );
        }
        
        KeyCloakInvocation keycloakServer = new KeyCloakInvocation(
                URL,
                Realm);

        KeycloakRes accessToken = keycloakServer.verifyToken(token);        
        
        InternalResponse response;
//        System.out.println("Status:"+accessToken.getStatus());
        if (accessToken.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
            response = new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,"SUCCESS");
            response.setUser(accessToken.getUser());
            return response;
        } if( accessToken.getStatus() == PaperlessConstant.HTTP_CODE_UNAUTHORIZED){
            response = new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_UNAUTHORIZED_USER, "EN", null));
        }
        else {
//            System.out.println("ErrDes:"+);
            int sub_code = ManageTokenWithIAM.convertError(accessToken.getError_description());
            response = new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK, sub_code, "EN", null));
        }
        return response;
    }         
    
    public static void main(String[] args) {
    }
}
