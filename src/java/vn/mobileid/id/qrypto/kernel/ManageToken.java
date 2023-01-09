/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.qrypto.kernel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ejbca.util.CertTools;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.api.awscore.AWSV4Constants;
import vn.mobileid.id.general.objects.IDXCertificate;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.utils.Crypto;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.keycloak.KeyCloakInvocation;
import vn.mobileid.id.general.keycloak.obj.KeycloakReq;
import vn.mobileid.id.general.keycloak.obj.KeycloakRes;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.AWSV4PropertiesJSNObject;
import vn.mobileid.id.general.objects.RelyingParty;
import vn.mobileid.id.general.objects.VerificationPropertiesJSNObject;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author ADMIN
 */
public class ManageToken {

    final private static Logger LOG = LogManager.getLogger(ManageToken.class);

    private String URL = Configuration.getInstance().getKeyCloakURL();
    private String Realm = Configuration.getInstance().getKeyCloakRealm();

    public InternalResponse processJSON_getToken(final HttpServletRequest request, String payload) {
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            QryptoMessageResponse.getLangFromJson(payload),
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
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
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
            response = new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                    QryptoMessageResponse.getAccessTokenMessage(QryptoConstant.CODE_SUCCESS,
                            QryptoConstant.SUBCODE_SUCCESS,
                            "en",
                            accessToken
                    ));
        } else {
            int sub_code = ManageToken.convertError(accessToken.getError_description());
            response = new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK, sub_code, "EN", null));
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
            response = new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                    QryptoMessageResponse.getAccessTokenMessage(QryptoConstant.CODE_SUCCESS,
                            QryptoConstant.SUBCODE_SUCCESS,
                            "en",
                            accessToken
                    ));
        } else {
            int sub_code = ManageToken.convertError(accessToken.getError_description());
            response = new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK, sub_code, "EN", null));
        }

        return response;
    }

    public InternalResponse processJSON_revoke(final HttpServletRequest request, String payload) {
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            QryptoMessageResponse.getLangFromJson(payload),
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
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
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
                object.getToken(),
                object.getToken_type_hint());

        KeycloakRes accessToken = keycloakServer.revokeToken(null, true);
                 
        
        InternalResponse response;
        if (accessToken.getAccess_token() != null) {         
            response = new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,"SUCCESS");
        } else {
            int sub_code = ManageToken.convertError(accessToken.getError_description());
            response = new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK, sub_code, "EN", null));
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
            response = new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,"SUCCESS");
        } else {
            int sub_code = ManageToken.convertError(accessToken.getError_description());
            response = new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK, sub_code, "EN", null));
        }

        return response;
    }
    
    private static int convertError(String errorKeycloak) {
        if (errorKeycloak.equalsIgnoreCase("Unsupported grant_type")) {
            //Error in grant_type
            return QryptoConstant.SUBCODE_UNSUPPORTED_GRANT_TYPE;
        }
        if (errorKeycloak.equalsIgnoreCase("Invalid user credentials")) {
            //Error in Username, error and missing data in Password
            return QryptoConstant.SUBCODE_INVALID_USER_CREDENTIALS;
        }
        if (errorKeycloak.equalsIgnoreCase("Invalid client secret")) {
            //Error in Client Secret
            return QryptoConstant.SUBCODE_INVALID_CLIENT_SECRET;
        }
        if (errorKeycloak.equalsIgnoreCase("Invalid client credentials")) {
            //Error and missing data in Client ID
            return QryptoConstant.SUBCODE_INVALID_CLIENT_CREDENTIALS;
        }
        if(errorKeycloak.contains("grant_type")){
            //Missing data in grant_type
            return QryptoConstant.SUBCODE_MISSING_GRANT_TYPE;
        }
        if(errorKeycloak.contains("username")){
            return QryptoConstant.SUBCODE_MISSING_USER_NAME;
        }
        if(errorKeycloak.contains("Client secret")){
            //Missing client secret
            return QryptoConstant.SUBCODE_MISSING_CLIENT_SECRET;
        }
        if(errorKeycloak.contains("Token") && errorKeycloak.contains("provided")){
            return QryptoConstant.SUBCODE_TOKEN_NOT_PROVIDED;
        }
        if(errorKeycloak.contains("Invalid token")){
            return QryptoConstant.SUBCODE_INVALID_TOKEN;
        }
        if(errorKeycloak.contains("Token is invalid!")){
            return QryptoConstant.SUBCODE_INVALID_TOKEN;
        }
        if(errorKeycloak.contains("Token is expired!")){
            return QryptoConstant.SUBCODE_TOKEN_EXPIRED;
        }
        if(errorKeycloak.contains("INTERNAL ERROR")){
            return QryptoConstant.SUBCODE_INTERNAL_ERROR;
        }
        return QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE;
    }

    public InternalResponse verifyAccessToken(final HttpServletRequest request, String payload){
        KeycloakReq object = new KeycloakReq();      

        //Get Access Token
        LOG.info("Checking Header!!");
        String token = request.getHeader("Authorization");
        LOG.info("Token:"+token);
        if(token == null){
            return new InternalResponse(QryptoConstant.HTTP_CODE_UNAUTHORIZED,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            QryptoConstant.SUBCODE_MISSING_ACCESS_TOKEN, "en", null)
            );
        }
        
        KeyCloakInvocation keycloakServer = new KeyCloakInvocation(
                URL,
                Realm);

        KeycloakRes accessToken = keycloakServer.verifyToken(token);        
        
        InternalResponse response;
        if (accessToken.getStatus() == QryptoConstant.CODE_SUCCESS) {
            response = new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,"SUCCESS");
            response.setUser(accessToken.getUser());
        } else {
            int sub_code = ManageToken.convertError(accessToken.getError_description());
            response = new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK, sub_code, "EN", null));
        }

        return response;
    }         
    
    public static void main(String[] args) {
    }
}
