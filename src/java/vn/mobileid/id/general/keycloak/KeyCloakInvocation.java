/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.keycloak;

import vn.mobileid.id.general.keycloak.obj.KeycloakRes;
import vn.mobileid.id.general.keycloak.obj.Certificate;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.HttpRequest;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.gateway.p2p.objects.P2PFunction;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author ADMIN
 */
public class KeyCloakInvocation {

    final private static Logger LOG = LogManager.getLogger(KeyCloakInvocation.class);
    final private String url;
    final private String realm;
    final private String clientId;
    private String clientSecret;
    final private String grantType;

    final private static String FUNCTION_TOKEN = "/protocol/openid-connect/token";
    final private static String FUNCTION_CREATE_USER = "/users";
    final private static String FUNCTION_REVOKE = "/protocol/openid-connect/revoke";
    final private static String FUNCTION_USERINFO = "/protocol/openid-connect/userinfo";
    final private static String FUNCTION_CERTS = "/protocol/openid-connect/certs";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static KeycloakRes accessToken;

    final private String entityBillCode;
    final private String requestBillCode;
    final private String idAddr;

    final private String username;
    final private String password;
    final private String refresh_token;
    final private String token_type;

    private static Certificate certificate = null;

    /**
     * Constructor use to create a new connection to IAM, get Token
     *
     * @param url
     * @param realm
     * @param clientId
     * @param clientSecret
     * @param grantType
     * @param entityBillCode
     * @param requestBillCode
     * @param idAddr
     * @param username
     * @param password
     */
    public KeyCloakInvocation(
            String url,
            String realm,
            String clientId,
            String clientSecret,
            String grantType,
            String entityBillCode,
            String requestBillCode,
            String idAddr,
            String username,
            String password) {
        this.url = url;
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.grantType = grantType;
        this.entityBillCode = entityBillCode;
        this.requestBillCode = requestBillCode;
        this.idAddr = idAddr;
        this.username = username;
        this.password = password;
        this.refresh_token = null;
        this.token_type = null;
    }

    /**
     * Contructor use to revoke a token
     *
     * @param url
     * @param realm
     * @param clientId
     * @param clientSecret
     * @param token
     * @param tokenType
     */
    public KeyCloakInvocation(
            String url,
            String realm,
            String clientId,
            String clientSecret,
            String token,
            String tokenType
    ) {
        this.url = url;
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.refresh_token = token;
        this.token_type = tokenType;
        this.grantType = null;
        this.entityBillCode = null;
        this.requestBillCode = null;
        this.idAddr = null;
        this.username = null;
        this.password = null;
    }

    public KeyCloakInvocation(
            String url,
            String realm
    ) {
        this.url = url;
        this.realm = realm;
        this.clientId = null;
        this.clientSecret = null;
        this.grantType = null;
        this.entityBillCode = null;
        this.requestBillCode = null;
        this.idAddr = null;
        this.username = null;
        this.password = null;
        this.refresh_token = null;
        this.token_type = null;
    }

    public synchronized KeycloakRes getAccessToken(String payload, boolean renewAccessToken) {
        if (renewAccessToken) {
            if (LogHandler.isShowDebugLog()) {
                LOG.debug("Get new accessToken");
            }
            KeycloakRes act = null;
            try {
                if (payload == null) {
                    act = token();
                } else {
                    act = token(payload);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while getting accessToken. Details: " + Utils.printStackTrace(e));
                }
            }
            if (!Utils.isNullOrEmpty(act.getAccess_token())) {
                accessToken = act;
            } else {
                accessToken = null;
            }
            return act;
        } else {
            if (accessToken == null) {
                return getAccessToken(payload, true);
            } else {
                return accessToken;
            }
        }
    }

    private KeycloakRes token() throws Exception {
        String tokenUrl = url + "/realms/" + realm + FUNCTION_TOKEN;

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        if (clientSecret == null) {
            clientSecret = Configuration.getInstance().getKeycloakClient_secret();
        }

        String urlParameters = "";
        urlParameters += clientId == null ? "" : ("client_id=" + URLEncoder.encode(clientId, "UTF-8"));
        urlParameters += clientSecret == null ? "" : ("&client_secret=" + URLEncoder.encode(clientSecret, "UTF-8"));
        urlParameters += grantType == null ? "" : ("&grant_type=" + URLEncoder.encode(grantType, "UTF-8"));
        urlParameters += username == null ? "" : ("&username=" + URLEncoder.encode(username, "UTF-8"));
        urlParameters += password == null ? "" : ("&password=" + URLEncoder.encode(password, "UTF-8"));

        KeycloakRes token = null;
        HttpRequest httpRequest = new HttpRequest(true, urlParameters, tokenUrl, headers);
        HttpRequest.Response response = httpRequest.sendRequest();
        try {
            token = objectMapper.readValue(response.getBody(), KeycloakRes.class);
        } catch (IOException e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while parsing the json response. Details: " + Utils.printStackTrace(e));
            }
        }
        return token;
    }

    private KeycloakRes token(String payload) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("payload call IAM:" + payload);
        }
        String tokenUrl = url + "/realms/" + realm + FUNCTION_TOKEN;

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        if (clientSecret == null) {
            clientSecret = Configuration.getInstance().getKeycloakClient_secret();
        }

        payload = payload + "&client_secret=" + clientSecret;

        KeycloakRes token = null;
        HttpRequest httpRequest = new HttpRequest(true, payload, tokenUrl, headers);
        HttpRequest.Response response = httpRequest.sendRequest();
        try {
            token = objectMapper.readValue(response.getBody(), KeycloakRes.class);
        } catch (IOException e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while parsing the json response. Details: " + Utils.printStackTrace(e));
            }
        }
        return token;
    }

    public synchronized KeycloakRes revokeToken(String payload, boolean renewAccessToken) {
        if (renewAccessToken) {
            if (LogHandler.isShowDebugLog()) {
                LOG.debug("Get new accessToken");
            }
            KeycloakRes act = null;
            try {
                if (payload == null) {
                    act = revokeToken();
                } else {
                    act = revokeToken(payload);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while getting accessToken. Details: " + Utils.printStackTrace(e));
                }
            }
            if (!Utils.isNullOrEmpty(act.getAccess_token())) {
                accessToken = act;
            } else {
                accessToken = null;
            }
            return act;
        } else {
            if (accessToken == null) {
                return revokeToken(payload, true);
            } else {
                return accessToken;
            }
        }
    }

    private KeycloakRes revokeToken() throws Exception {
        String tokenUrl = url + "/realms/" + realm + FUNCTION_REVOKE;

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        if (clientSecret == null) {
            clientSecret = Configuration.getInstance().getKeycloakClient_secret();
        }

        String urlParameters = "";
        urlParameters += clientId == null ? "" : ("client_id=" + URLEncoder.encode(clientId, "UTF-8"));
        urlParameters += clientSecret == null ? "" : ("&client_secret=" + URLEncoder.encode(clientSecret, "UTF-8"));
        urlParameters += refresh_token == null ? "" : ("&token=" + URLEncoder.encode(refresh_token, "UTF-8"));
        urlParameters += token_type == null ? "" : ("&token_type_hint=" + URLEncoder.encode(token_type, "UTF-8"));

        KeycloakRes token = null;
        HttpRequest httpRequest = new HttpRequest(true, urlParameters, tokenUrl, headers);
        HttpRequest.Response response = httpRequest.sendRequest();

        if (response.getHttpCode() == 200 && response.getBody() == null) {
            token = new KeycloakRes();
            token.setAccess_token("Success");
            return token;
        } else {
            try {
                token = objectMapper.readValue(response.getBody(), KeycloakRes.class);
                return token;
            } catch (IOException e) {
                e.printStackTrace();
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while parsing the json response. Details: " + Utils.printStackTrace(e));
                }
            }
            return null;
        }
    }

    private KeycloakRes revokeToken(String payload) throws Exception {
        String tokenUrl = url + "/realms/" + realm + FUNCTION_REVOKE;

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        if (clientSecret == null) {
            clientSecret = Configuration.getInstance().getKeycloakClient_secret();
        }
        payload += "&client_secret=" + clientSecret;

        KeycloakRes token = null;
        HttpRequest httpRequest = new HttpRequest(true, payload, tokenUrl, headers);
        HttpRequest.Response response = httpRequest.sendRequest();

        if (response.getHttpCode() == 200 && response.getBody() == null) {
            token = new KeycloakRes();
            token.setAccess_token("Success");
            return token;
        } else {
            try {
                token = objectMapper.readValue(response.getBody(), KeycloakRes.class);
                return token;
            } catch (IOException e) {
                e.printStackTrace();
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while parsing the json response. Details: " + Utils.printStackTrace(e));
                }
            }
            return null;
        }
    }

    public synchronized KeycloakRes verifyToken(String token) {
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Verify accessToken");
        }
        token = token.replaceAll("Bearer ", "");
        KeycloakRes result = new KeycloakRes();

        //Decode JWT
        String[] chunks = token.split("\\.");

        String header = null;
        String payload = null;
        String signature = null;
        String alg = null;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Before decode token");
            LOG.debug("Header:" + chunks[0]);
            LOG.debug("Payload:" + chunks[1]);
            LOG.debug("Signature:" + chunks[2]);
        }
        try {
            header = new String(Base64.getDecoder().decode(chunks[0]));  //Algorithm - tokentype
            payload = new String(Base64.getDecoder().decode(chunks[1])); //Data - User
            signature = chunks[2];
            int pos = header.indexOf("alg");
            int typ = header.indexOf("typ");
            alg = header.substring(pos + 6, typ - 3);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while decode token!" + e);
            }
            result.setStatus(QryptoConstant.CODE_FAIL);
            result.setError_description("Token is invalid!");
            return result;
        }
        
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Header:" + header);
            LOG.debug("Alg:" + alg);
            LOG.debug("Payload:" + payload);
            LOG.debug("Signature:" + signature);
        }

        if (KeyCloakInvocation.certificate == null) {
            KeycloakRes response = getKeycloakCertificate();
            if (response == null) {
                result.setStatus(QryptoConstant.CODE_FAIL);
                result.setError_description("INTERNAL ERROR");
                return result;
            }
            try {
                for (Certificate cert : response.getKey()) {
                    if (cert.getAlg().equalsIgnoreCase(alg)) {
                        KeyCloakInvocation.certificate = cert;
                    }
                }
                if (KeyCloakInvocation.certificate == null) {
                    if (LogHandler.isShowErrorLog()) {
                        LOG.error("The Certificate of keycloak don't contain Algorithm of accessToken!");
                    }
                    result.setStatus(QryptoConstant.CODE_FAIL);
                    result.setError_description("INTERNAL ERROR");
                    return result;
                }
            } catch (Exception e) {
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Look like accessToken is expired. Try to get the new accessToken");
                }
                result.setStatus(QryptoConstant.CODE_FAIL);
                result.setError_description("INTERNAL ERROR");
                return result;
            }
        }
        
        //Convert to Object
        User data = null;
        try {
            data = objectMapper.readValue(payload, User.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse data JWT to object User");
            }
            result.setStatus(QryptoConstant.CODE_FAIL);
            result.setError_description("INTERNAL ERROR");
            return result;
        }

        //Verify Token
        try {
            LOG.info("Verify Token");
            RSAPublicKey pub = getPublicKeyFromString(
                    KeyCloakInvocation.certificate.getN(),
                    KeyCloakInvocation.certificate.getE()
            );
            result = verifyToken(token, pub);
            result.setUser(data);
            return result;
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while processing Verify AccessToken");
                e.printStackTrace();
            }
            result.setStatus(QryptoConstant.CODE_FAIL);
            result.setError_description("INTERNAL ERROR");
            return result;
        }
    }

    private KeycloakRes getKeycloakCertificate() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Get IAM Certificate!");
        }
        String tokenUrl = url + "/realms/" + realm + FUNCTION_CERTS;
        
        KeycloakRes token = null;
        HttpRequest httpRequest = new HttpRequest(false, null, tokenUrl, null);
        HttpRequest.Response response = httpRequest.sendRequest();

        try {
            if (response.getHttpCode() == 200 && response.getBody() != null) {
                token = new KeycloakRes();
                token = objectMapper.readValue(response.getBody(), KeycloakRes.class);
                return token;
            } else {
                try {
                    token = objectMapper.readValue(response.getBody(), KeycloakRes.class);
                    return token;
                } catch (IOException e) {
                    e.printStackTrace();
                    if (LogHandler.isShowErrorLog()) {
                        LOG.error("Error while parsing the json response. Details: " + Utils.printStackTrace(e));
                    }
                }
                return null;
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while parsing the json response. Details: " + Utils.printStackTrace(e));
            }
            return null;
        }
    }

    //INTERNAL FUNCTION / METHOD=======================
    private static KeycloakRes verifyToken(String token, RSAPublicKey publicKey) {
        try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT result = verifier.verify(token);

            KeycloakRes result2 = new KeycloakRes();
            result2.setStatus(QryptoConstant.CODE_SUCCESS);
            return result2;

            //Verify by code
//            String signedData = token.substring(0, token.lastIndexOf("."));
//            String signatureB64u = token.substring(token.lastIndexOf(".") + 1, token.length());
//            byte signature[] = Base64.getUrlDecoder().decode(signatureB64u);
//            Signature sig = Signature.getInstance("SHA256withRSA");
//            sig.initVerify(publicKey);
//            sig.update(signedData.getBytes());
//            return sig.verify(signature);
        } catch (TokenExpiredException e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Expired token!");
            }                        
            KeycloakRes result = new KeycloakRes();
            result.setStatus(QryptoConstant.CODE_FAIL);
            result.setError_description("Token is expired!");
            return result;
        } catch (JWTVerificationException e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Token is invalid!");
            }                        
            KeycloakRes result = new KeycloakRes();
            result.setStatus(QryptoConstant.CODE_FAIL);
            result.setError_description("Token is invalid!");
            return result;
        }
    }

    private static RSAPublicKey getPublicKeyFromString(String modulus, String exponent) throws
            IOException, GeneralSecurityException {
        try {
            byte[] exponentB = Base64.getUrlDecoder().decode(exponent);
            byte[] modulusB = Base64.getUrlDecoder().decode(modulus);
            BigInteger bigEx = new BigInteger(1, exponentB);
            BigInteger bigMo = new BigInteger(1, modulusB);

            PublicKey publickey;

            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new RSAPublicKeySpec(bigMo, bigEx));

            return pubKey;

        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot generate Public Key! - Detail:" + e);
            }
            return null;
        }
    }

}
