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
import java.util.List;
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
import vn.mobileid.id.general.keycloak.KeycloakReq;
import vn.mobileid.id.general.keycloak.KeycloakRes;
import vn.mobileid.id.general.objects.AWSV4PropertiesJSNObject;
import vn.mobileid.id.general.objects.RelyingParty;
import vn.mobileid.id.general.objects.VerificationPropertiesJSNObject;
import vn.mobileid.id.qrypto.objects.QryptoConstant;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author ADMIN
 */
public class QryptoGetToken {

    final private static Logger LOG = LogManager.getLogger(QryptoGetToken.class);

    private String URL = Configuration.getInstance().getKeyCloakURL();
    private String Realm = Configuration.getInstance().getKeyCloakRealm();

    public InternalResponse processJSON(final HttpServletRequest request, String payload) {
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse((int) QryptoConstant.FEDERAL_ID, null,
                    QryptoMessageResponse.getMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            QryptoConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null), null);
        }

        KeycloakReq object = new KeycloakReq();
        ObjectMapper mapper = new ObjectMapper();
        try {
            object = mapper.readValue(payload, KeycloakReq.class);
        } catch (JsonProcessingException ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse payload");
            }
            return new InternalResponse((int) QryptoConstant.FEDERAL_ID, null,
                    QryptoMessageResponse.getMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null), null);
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

        KeycloakRes accessToken = keycloakServer.getAccessToken(true);

        InternalResponse response;
            if (accessToken.getAccess_token() != null) {
                response = new InternalResponse(200,
                        QryptoMessageResponse.getAccessTokenMessage(QryptoConstant.CODE_SUCCESS,
                                QryptoConstant.SUBCODE_SUCCESS,
                                "en",
                                accessToken
                        ));
            } else {
                int sub_code = QryptoGetToken.convertError(accessToken.getError_description());
                response = new InternalResponse(400,
                        QryptoMessageResponse.getMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK, sub_code, "EN", null));
            }
            
        return response;
    }

    public InternalResponse processFORM(final HttpServletRequest request) {
        Enumeration<String> a = request.getParameterNames();
        while(a.hasMoreElements()){
            System.out.println("Param:"+a.nextElement());
        }
        String client_id = request.getParameter("client_id");
//        System.out.println("Client_id:"+client_id);
//        LOG.warn("Client id:"+client_id);
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

        KeycloakRes accessToken = keycloakServer.getAccessToken(true);

        InternalResponse response;
            if (accessToken.getAccess_token() != null) {
                response = new InternalResponse(200,
                        QryptoMessageResponse.getAccessTokenMessage(QryptoConstant.CODE_SUCCESS,
                                QryptoConstant.SUBCODE_SUCCESS,
                                "en",
                                accessToken
                        ));
            } else {
                int sub_code = QryptoGetToken.convertError(accessToken.getError_description());
                response = new InternalResponse(400,
                        QryptoMessageResponse.getMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK, sub_code, "EN", null));
            }
            
        return response;
    }
    
    public static int convertError(String errorKeycloak){
        if(errorKeycloak.equalsIgnoreCase("Unsupported grant_type")){
            return QryptoConstant.SUBCODE_UNSUPPORTED_GRANT_TYPE;
        }
        if(errorKeycloak.equalsIgnoreCase("Invalid user credentials")){
            return QryptoConstant.SUBCODE_INVALID_USER_CREDENTIALS;
        }
        if(errorKeycloak.equalsIgnoreCase("Invalid client secret")){
            return QryptoConstant.SUBCODE_INVALID_CLIENT_SECRET;
        }
        if(errorKeycloak.equalsIgnoreCase("Invalid client credentials")){
            return QryptoConstant.SUBCODE_INVALID_CLIENT_CREDENTIALS;
        }
        return QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE;
    }
    
    public static void main(String[] args) {
        try {
            QryptoGetToken token = new QryptoGetToken();
            KeyCloakInvocation keycloakServer = new KeyCloakInvocation(
                    token.URL,
                    token.Realm,
                    "qryptoUser",
                    null,
                    "pasword",
                    null,
                    null,
                    null,
                    "user2",
                    "thienthan123");

            KeycloakRes accessToken = keycloakServer.getAccessToken(false);
            InternalResponse response;
            if (accessToken.getAccess_token() != null) {
                response = new InternalResponse(200,
                        QryptoMessageResponse.getAccessTokenMessage(QryptoConstant.CODE_SUCCESS,
                                QryptoConstant.SUBCODE_SUCCESS,
                                "en",
                                accessToken
                        ));
            } else {
                response = new InternalResponse(400,
                        QryptoMessageResponse.getMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK, QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE, "EN", null));
            }


            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(response.getMessage()));
        } catch (JsonProcessingException ex) {
            java.util.logging.Logger.getLogger(QryptoGetToken.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
