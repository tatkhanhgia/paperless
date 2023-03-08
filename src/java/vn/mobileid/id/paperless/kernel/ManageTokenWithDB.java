/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.KeyCloakInvocation;
import vn.mobileid.id.general.keycloak.obj.Certificate;
import vn.mobileid.id.general.keycloak.obj.KeycloakReq;
import vn.mobileid.id.general.keycloak.obj.KeycloakRes;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.QryptoConstant;
import vn.mobileid.id.paperless.objects.Enterprise;
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;
import vn.mobileid.id.paperless.objects.RefreshToken;
import vn.mobileid.id.utils.Crypto;
import static vn.mobileid.id.utils.Crypto.getPrivateKeyFromString;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ManageTokenWithDB {

    final private static Logger LOG = LogManager.getLogger(ManageTokenWithDB.class);
    private static long now;

    public static InternalResponse processLogin(final HttpServletRequest request, String payload) {
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        KeycloakReq object = new KeycloakReq();
        ObjectMapper mapper = new ObjectMapper();
        try {
            object = mapper.readValue(payload, KeycloakReq.class);
        } catch (JsonProcessingException ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse payload - payload:" + payload);
                LOG.error("Details:" + ex);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Login 
        try {
            if (object.getGrant_type().contains("password")) {
                if (!checkPayload(payload)) {
                    return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                            QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                                    QryptoConstant.SUBCODE_MISSING_USER_NAME_OR_PASSWORD,
                                    "en",
                                    null));
                }
                return login(object.getUsername(), object.getPassword());
            }
            if (object.getGrant_type().contains("refresh_token")) {
                return reCreate(object.getRefreshToken());
            }
        } catch (Exception ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while Login - Detail:" + ex);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_INTERNAL_ERROR,
                            "en",
                            null));
        }
        return null;
    }

    public static InternalResponse processLoginURLEncode(final HttpServletRequest request, HashMap<String, String> map) {
        if (!map.containsKey("username") || !map.containsKey("password")) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            QryptoConstant.SUBCODE_MISSING_USER_NAME_OR_PASSWORD,
                            "en",
                            null));
        }

        //Login 
        try {
            return login(map.get("username"), map.get("password"));
        } catch (Exception ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while Login - Detail:" + ex);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_INTERNAL_ERROR,
                            "en",
                            null));
        }
    }

    public static InternalResponse processVerify(final HttpServletRequest request) {
        //Get Access Token
        String token = request.getHeader("Authorization");
        if (LogHandler.isShowDebugLog()) {
            LOG.info("Checking Header!!");
            LOG.info("Token:" + token);
        }

        if (token == null || token.contains("null")) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_UNAUTHORIZED,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            QryptoConstant.SUBCODE_MISSING_ACCESS_TOKEN, "en", null)
            );
        }

        //Verify
        try {
            return verify(token);
        } catch (Exception ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while Verify - Detail:" + ex);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_500,
                    QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse processRevoke(final HttpServletRequest request, String payload) {
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        KeycloakReq object = new KeycloakReq();
        ObjectMapper mapper = new ObjectMapper();
        try {
            object = mapper.readValue(payload, KeycloakReq.class);
        } catch (JsonProcessingException ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse payload - payload:" + payload);
                LOG.error("Details:" + ex);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Checking        
        if (object.getRefreshToken() == null || object.getRefreshToken().isEmpty()) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            QryptoConstant.SUBCODE_MISSING_REFRESH_TOKEN,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        try {
            return revoke(object.getRefreshToken());
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while Revoke - Detail:" + e);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_500,
                    QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    private static InternalResponse login(String email, String pass) {
        Database db = new DatabaseImpl();
        try {
            //Login
            DatabaseResponse res = db.login(email, pass);
            if (res.getStatus() != QryptoConstant.CODE_SUCCESS) {
                String message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                        res.getStatus(),
                        "en",
                        null);
                return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                        message);
            }

            User info = (User) res.getObject();

            InternalResponse res2 = getEnterpriseInfo(info.getEmail());
            if (res2.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
                return res2;
            }
            Enterprise enterprise = (Enterprise) res2.getData();

            String[] temp = createAccess_RefreshToken(info, enterprise);

            String accessToken = temp[0];
            String refreshtoken = temp[1];
            String sessionID = temp[2];
            String iat = temp[3];
            String exp = temp[4];

            KeycloakRes response = new KeycloakRes();
            response.setAccess_token(accessToken);
            response.setRefresh_token(refreshtoken);
            response.setExpires_in((int) QryptoConstant.expired_in);
            response.setToken_type(QryptoConstant.TOKEN_TYPE_BEARER);
            response.setRefresh_expires_in((int) QryptoConstant.refresh_token_expired_in);

            //Write refreshtoken into DB           
            InternalResponse internalResponse = WriteRefreshTokenIntoDB.write(
                    info.getEmail(),
                    sessionID,
                    1,
                    enterprise.getName(),
                    new Date(Long.parseLong(iat)),
                    new Date(Long.parseLong(exp)),
                    "HMAC",
                    info.getEmail());
            if (internalResponse.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
                return internalResponse;
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS, response);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                e.printStackTrace();
                LOG.error("Cannot create Access Token - Detail:" + e);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    private static InternalResponse verify(String token) {
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Verify accessToken");
        }
        token = token.replaceAll("Bearer ", "");

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
            header = new String(Base64.getUrlDecoder().decode(chunks[0]), "UTF-8");
            payload = new String(Base64.getUrlDecoder().decode(chunks[1]), "UTF-8");

            signature = chunks[2];
            int pos = header.indexOf("alg");
            int typ = header.indexOf("typ");
            alg = header.substring(pos + 6, typ - 3);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while decode token!" + e);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_UNAUTHORIZED,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            QryptoConstant.SUBCODE_INVALID_TOKEN,
                            "en",
                            null));
        }

        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Header:" + header);
            LOG.debug("Alg:" + alg);
            LOG.debug("Payload:" + payload);
            LOG.debug("Signature:" + signature);
        }

        //Convert to Object
        User data = null;
        try {
            data = new ObjectMapper().readValue(payload, User.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while parsing Data!" + e);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_UNAUTHORIZED,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            QryptoConstant.SUBCODE_INVALID_TOKEN,
                            "en",
                            null));
        }

        //Verify Token
        try {
            if (verifyTokenByCode(chunks[0] + "." + chunks[1], signature, getPublicKey())) {
                Date date = new Date();
                if (data.getExp() < date.getTime()) {
                    return new InternalResponse(QryptoConstant.HTTP_CODE_UNAUTHORIZED,
                            QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                                    QryptoConstant.SUBCODE_TOKEN_EXPIRED, "en", null));
                }
                //Check accessToken in DB
                InternalResponse res = checkAccessToken(data.getEmail(), data.getSid());
                if (res.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
                    return res;
                }
                InternalResponse response = new InternalResponse();
                response.setStatus(QryptoConstant.HTTP_CODE_SUCCESS);
                response.setUser(data);
                return response;
            }

            return new InternalResponse(QryptoConstant.HTTP_CODE_UNAUTHORIZED,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            QryptoConstant.SUBCODE_INVALID_TOKEN, "en", null));

        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while processing Verify AccessToken");
                e.printStackTrace();
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_500,
                    QryptoConstant.INTERNAL_EXP_MESS
            );
        }
    }

    private static InternalResponse revoke(String token) {
        try {
            String[] chunks = token.split("\\.");

            String header = null;
            String payload = null;
            String signature = null;
            String alg = null;
            User data = null;
            try {
                header = new String(Base64.getUrlDecoder().decode(chunks[0]), "UTF-8");
                payload = new String(Base64.getUrlDecoder().decode(chunks[1]), "UTF-8");

                signature = chunks[2];
                int pos = header.indexOf("alg");
                int typ = header.indexOf("typ");
                alg = header.substring(pos + 6, typ - 3);

                try {
                    data = new ObjectMapper().readValue(payload, User.class);
                } catch (Exception e) {
                    if (LogHandler.isShowErrorLog()) {
                        LOG.error("Error while parsing Data!" + e);
                    }
                    return new InternalResponse(QryptoConstant.HTTP_CODE_UNAUTHORIZED,
                            QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                                    QryptoConstant.SUBCODE_INVALID_TOKEN,
                                    "en",
                                    null));
                }
            } catch (Exception e) {
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while decode token!" + e);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_UNAUTHORIZED,
                        QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                                QryptoConstant.SUBCODE_INVALID_TOKEN,
                                "en",
                                null));
            }

            return WriteRefreshTokenIntoDB.remove(data.getSid());
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while Revoke - Detail:" + e);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_500,
                    QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    private static InternalResponse reCreate(String token) {
        System.out.println("RefreshToken:"+token);
        String[] chunks = token.split("\\.");   
        System.out.println("payload:"+chunks[1]);
        //Check expires token
        try{
            String temp = new String(Base64.getUrlDecoder().decode(chunks[1]), "UTF-8");
            User user = new ObjectMapper().readValue(temp, User.class);
            Date date = new Date();
            System.out.println("UserDate:"+user.getExp() );
            System.out.println("Now:"+date.getTime());
            if (user.getExp() < date.getTime()) {
                    revoke(token);
                    return new InternalResponse(QryptoConstant.HTTP_CODE_UNAUTHORIZED,
                            QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                                    QryptoConstant.SUBCODE_TOKEN_EXPIRED, "en", null));
                }
        } catch (Exception e){
            e.printStackTrace();
            if(LogHandler.isShowErrorLog()){
                LOG.error("Error while decode refreshtoken!"+e);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_500, QryptoConstant.INTERNAL_EXP_MESS);
        }
        Database db = new DatabaseImpl();
        try {
            //Login
            InternalResponse res = WriteRefreshTokenIntoDB.get(chunks[1]);
            if (res.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
                return res;
            }
            RefreshToken object = (RefreshToken) res.getData();
            String payload = new String(Base64.getUrlDecoder().decode(object.getRefreshtoken()), "UTF-8");
            User user = new User();
            Enterprise enterprise = new Enterprise();
            try {
                user = new ObjectMapper().readValue(payload, User.class);
            } catch (Exception ex) {
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Cannot parse data from DB !!");
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_500,
                        QryptoConstant.INTERNAL_EXP_MESS);
            }
            enterprise.setId(Integer.parseInt(user.getIss()));
            enterprise.setName(user.getAzp());

//            String temp = createAccess_RefreshToken(user, enterprise);
//
//            String[] a = temp.split(";");
//            String access = a[0];
//            String[] b = access.split("\\.");
//            String signature = b[2];
//            String created_at = a[1];
//            String expires_at = a[2];
//
//            String refreshtoken = createRefreshToken(user, enterprise, Long.parseLong(created_at));
//
//            KeycloakRes response = new KeycloakRes();
//            response.setAccess_token(access);
//            response.setRefresh_token(refreshtoken);
//            response.setExpires_in((int) QryptoConstant.expired_in);
//            response.setToken_type(QryptoConstant.TOKEN_TYPE_BEARER);
//            response.setRefresh_expires_in((int) QryptoConstant.refresh_token_expired_in);
//
//            //Write refreshtoken into DB
//            InternalResponse internalResponse = WriteRefreshTokenIntoDB.update(
//                    user.getEmail(),
//                    signature,
//                    refreshtoken.split("\\.")[1],
//                    new Date(Long.parseLong(created_at)),
//                    new Date(Long.parseLong(expires_at)),
//                    1,
//                    "HMAC",
//                    user.getEmail());
//            if (internalResponse.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
//                return internalResponse;
//            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS, response);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                e.printStackTrace();
                LOG.error("Cannot recreate Access Token - Detail:" + e);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_500, QryptoConstant.INTERNAL_EXP_MESS);
        }        
    }

    private static InternalResponse getEnterpriseInfo(String email) {
        Database db = new DatabaseImpl();
        try {
            //Login
            DatabaseResponse res = db.getEnterpriseInfoOfUser(email);
            if (res.getStatus() != QryptoConstant.CODE_SUCCESS) {
                String message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                        res.getStatus(),
                        "en",
                        null);
                return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                        message);
            }

            List<Enterprise> list = (List<Enterprise>) res.getObject();

            //Temp
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS, list.get(0));
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                e.printStackTrace();
                LOG.error("Cannot get Enterprise Info - Detail:" + e);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    private static InternalResponse checkAccessToken(String email, String sessionID) {
        return WriteRefreshTokenIntoDB.check(email, sessionID);
    }

    //===============INTERNAL FUNCTION===========================
    private static String createHeader() {
        String temp = "{";
        temp += "\"alg\":" + "\"" + QryptoConstant.alg + "\",";
        temp += "\"typ\":" + "\"" + QryptoConstant.typ + "\"";
        temp += "}";
        return temp;
    }

    private static String createPayload(User user) {
        try {
            return new ObjectMapper().writeValueAsString(user);
        } catch (JsonProcessingException ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot create payload! - detail:" + ex);
            }
            return null;
        }
    }

    private static User createBasicUserData(
            User user,
            Enterprise enterprise
    ) {
        User temp = new User();
        temp.setIat(Date.from(Instant.now()).getTime()); //Issue at
        temp.setExp(Date.from(Instant.now().plusSeconds(QryptoConstant.expired_in)).getTime()); //Expired                

//        temp.setJti(""); //JWT ID
        temp.setIss("https://paperless.mobile-id.vn");  //Issuer
//        temp.setAud("account"); //Audience
//        temp.setSub(""); //Subject
        temp.setTyp(QryptoConstant.TOKEN_TYPE_BEARER);
        temp.setAzp(enterprise.getName());
        temp.setAid(enterprise.getId());
//        temp.setSession_state("");
//        temp.setAcr(email);    //Authentication context class
//        temp.setRealm_access("");
//        temp.setResource_access(resource_access);
//        temp.setScope("");
        temp.setSid(Utils.generateTransactionID_noRP());  //Session-ID
//        temp.setEmail_verified(false);
        temp.setName(user.getName());
//        temp.setPreferred_username(enterprise_name);
//        temp.setGiven_name(name);
//        temp.setFamily_name(name);
        temp.setEmail(user.getEmail());
//        temp.setMobile(user.getMobile());
        return temp;
    }

    /**
     * Create an Access Token + RefreshToke + SessionID + Time Position of
     * parameter [0] : accessToken [1] : refreshToken [2] : sessionID [3] : Iat
     * (Issue At) [4] : expired RefreshToken
     *
     * @param user
     * @param enterprise
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws Exception
     */
    private static String[] createAccess_RefreshToken(User user, Enterprise enterprise) throws IOException, GeneralSecurityException, Exception {
        String header = createHeader();
        User temp = createBasicUserData(user, enterprise);
        String payload = createPayload(temp);
        header = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes());
        payload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes());
        String signature = Crypto.sign(header + "." + payload, getPrivateKey(), "base64");
        String[] result = new String[5];
        result[0] = header + "." + payload + "." + signature;
        result[1] = createRefreshToken(temp);
        result[2] = temp.getSid();
        result[3] = String.valueOf(temp.getIat());
        Date temps = new Date(temp.getIat());
        result[4] = String.valueOf(temps.toInstant().plusSeconds(QryptoConstant.refresh_token_expired_in).toEpochMilli());
        return result;
    }

    private static String createRefreshToken(
            User user) throws JsonProcessingException, IOException, GeneralSecurityException, Exception {
        //Create Data
        user.setName(null);
        user.setEmail(null);
        user.setTyp(QryptoConstant.TOKEN_TYPE_REFRESH);
        long now = user.getIat();
        Date temp = new Date(now);
        user.setExp(temp.toInstant().plusSeconds(QryptoConstant.refresh_token_expired_in).toEpochMilli());
        String payload = new ObjectMapper().writeValueAsString(user);
        String header = createHeader();
        header = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes());
        payload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes());
        String signature = Crypto.sign(header + "." + payload, getPrivateKey(), "base64");
        return header + "." + payload + "." + signature;
    }

    private static String hmacSha256(String data, String secret) {
        try {
            byte[] hash = secret.getBytes(StandardCharsets.UTF_8);
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
            sha256Hmac.init(secretKey);

            byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder().withoutPadding().encodeToString(signedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot Create Signature for JWT!");
            }
            return null;
        }
    }

    private static String getPrivateKey() throws IOException, GeneralSecurityException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream input = loader.getResourceAsStream("resources/config/key.key");
        String file = IOUtils.toString(input, StandardCharsets.UTF_8);
        PrivateKey pri = Crypto.getPrivateKeyFromString(file, "base64");
        return Base64.getEncoder().encodeToString(pri.getEncoded());
    }

    private static PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream input = loader.getResourceAsStream("resources/config/key.pub");
        String file = IOUtils.toString(input, StandardCharsets.UTF_8);
        String privateKeyPEM = file;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN RSA PUBLIC KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END RSA PUBLIC KEY-----", "");
        byte[] encoded = DatatypeConverter.parseBase64Binary(privateKeyPEM);
        X509EncodedKeySpec spec
                = new X509EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
//        return Base64.getEncoder().encodeToString(kf.generatePublic(spec).getEncoded());
        return kf.generatePublic(spec);
    }

    private static InternalResponse verifyTokenByOAUTH(String token, PublicKey publicKey) {
        try {
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT result = verifier.verify(token);

            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS, "");

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
            return new InternalResponse(QryptoConstant.HTTP_CODE_UNAUTHORIZED,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            QryptoConstant.SUBCODE_TOKEN_EXPIRED,
                            "en",
                            null));
        } catch (JWTVerificationException e) {
            if (LogHandler.isShowErrorLog()) {
                e.printStackTrace();
                LOG.error("Token is invalid! - Details:" + e);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_UNAUTHORIZED,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            QryptoConstant.SUBCODE_INVALID_TOKEN,
                            "en",
                            null));
        }
    }

    private static boolean verifyTokenByCode(String data, String signature, PublicKey pub) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            Signature sign = Signature.getInstance("SHA1withRSA");
            sign.initVerify(pub);
            sign.update(data.getBytes());
            return sign.verify(Base64.getUrlDecoder().decode(signature));
        } catch (NoSuchAlgorithmException ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("NosuchAlgorithmException!");
            }
        } catch (InvalidKeyException ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("InvalidKey!");
            }
        } catch (SignatureException ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("SignatureException!");
            }
        }
        return false;
    }

    private static boolean checkPayload(String payload) {
        if (!payload.contains("username") || !payload.contains("password")) {
            return false;
        }

        String[] count = payload.split("password");
        if (count.length <= 2) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, FileNotFoundException, IOException, GeneralSecurityException, Exception {
//        System.out.println(createHeader());
//        User a = new User();
//        a.setEmail("giatk@mobile-id.vn");
//        System.out.println(createPayload(a));
//        String a = createAccess_RefreshToken("Tất Khánh Gia", "giatk@mobile-id.vn", "1", "mobile-id");
//        System.out.println(a);
//        String signature = hmacSha256(a, getPrivateKey());
//        System.out.println("Signature:"+signature);
//        a += "." + signature;
//        System.out.println("JWT:"+a);
//        KeyPair kp = Crypto.generateRSAKey(100);
//        Key pub = kp.getPublic();
//        Key pri = kp.getPrivate();
//        String outFile = "key";
//        Writer out = new FileWriter("file/"+outFile + ".key");
//        out.write("-----BEGIN RSA PRIVATE KEY-----\n");
//        out.write(Base64.getEncoder().encodeToString(pri.getEncoded()));
//        out.write("\n-----END RSA PRIVATE KEY-----\n");
//        out.close();
//
//        out = new FileWriter("file/"+outFile + ".pub");
//        out.write("-----BEGIN RSA PUBLIC KEY-----\n");
//        out.write(Base64.getEncoder().encodeToString(pub.getEncoded()));
//        out.write("\n-----END RSA PUBLIC KEY-----\n");
//        out.close();
//        System.out.println(hmacSha256("khanhgia1234567889", pri.getEncoded().));

//Login
//        String email = "giatk@mobile-id.vn";
//        String pass = "thienthan123";
//        Database db = new DatabaseImpl();
//        try {
//            //Login
//            DatabaseResponse res = db.login(email, pass);
//            if (res.getStatus() != QryptoConstant.CODE_SUCCESS) {
//                String message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
//                        res.getStatus(),
//                        "en",
//                        null);
//
//            }
//
//            User info = (User) res.getObject();
//            String access = createAccess_RefreshToken(info);
//            System.out.println("Ace:" + access);
//        } catch (Exception e) {
//            System.out.println("Ex" + e);
//
//        }
//Get publickey
//    verifyTokenByOAUTH("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NzY0MjY3ODk5MDksImlhdCI6MTY3NjQyNjc4OTYwOSwiaXNzIjoiZW50ZXJwcmlzZWlkIiwiYXVkIjoiYWNjb3VudCIsInR5cCI6IkJlYXJlciIsIm5hbWUiOiJUP3QgS2jhbmggR2lhIiwiZW1haWwiOiJnaWF0a0Btb2JpbGUtaWQudm4iLCJhenAiOiJlbnRlcnByaXNlTmFtZSIsIm1vYmlsZSI6IjA1NjY0Nzc4NDcifQ.J9B-U5ZX2mWj6SUiCUgf4_p1BMHMx77tA_QgQmKbVlYkBHquXIL7jceKI7P4ffeEV_l3mQGJAl4p_3p87Vf_4mj6XVFtkQVm3N905tRbp4kKc7Ay7TQkxx3zZSt3c2kPRXxbVs16GF0FhILbnnItIqOCAN8ouMw7g8Lk-2T0SaOPmsKSyiNoMg6wRqzccfwcOi3PpZbXgQ95yRCHU3PKIJeX6dzpkt4ziNCrvVNXgOcl-zjjtevnQwjNeoIxkl8Nf3Rn0N_V_vmJOMPw770KBq1ifWquuDn8KAc8i1Xq7onZVKC_mCsSka7BzLMNYSIxOfhnwKEfi65hgSL3fwTJlQ", getPublicKey());
//        String data = "tatkhanhgia";
//        
//        
//        Security.addProvider(new BouncyCastleProvider());
//
//        String privateKeyPEM = "";
//        List<String> fata = Files.readAllLines(new File("D:\\NetBean\\qrypto\\src\\java\\key.key").toPath());
//        for (String temp : fata) {
//            privateKeyPEM += temp + "\n";
//        }
//
//        privateKeyPEM = privateKeyPEM.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
//        privateKeyPEM = privateKeyPEM.replace("-----END RSA PRIVATE KEY-----", "");
//        
//        byte[] encoded = DatatypeConverter.parseBase64Binary(privateKeyPEM);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
//        PrivateKey privKey = (PrivateKey) kf.generatePrivate(keySpec);
//        Signature sig = Signature.getInstance("SHA1withRSA");
//        sig.initSign(privKey);
//        sig.update(data.getBytes());
//        
//        byte[] signature =sig.sign();
//        String signature2= Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
//        
//        
//        
//        
//        byte[] decodeSig = Base64.getUrlDecoder().decode(signature2);
//        String publicPEM = "";
//        List<String>fata2 = Files.readAllLines(new File("D:\\NetBean\\qrypto\\src\\java\\key.pub").toPath());
//        for (String temp : fata2) {
//            publicPEM += temp + "\n";
//        }
//        publicPEM = publicPEM.replace("-----BEGIN RSA PUBLIC KEY-----\n", "");
//        publicPEM = publicPEM.replace("-----END RSA PUBLIC KEY-----", "");
//        publicPEM = publicPEM.trim();
//        System.out.println("Public:"+publicPEM);
//        byte[]encoded2 = DatatypeConverter.parseBase64Binary(publicPEM);
//        X509EncodedKeySpec spec
//                = new X509EncodedKeySpec(encoded2);
//        kf = KeyFactory.getInstance("RSA");
//        X509EncodedKeySpec spec2
//                = new X509EncodedKeySpec(encoded2);        
//        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(spec2);
//        sig.initVerify(pubKey);
//        sig.update(data.getBytes());
//        System.out.println("Boo:"+sig.verify(decodeSig));
        //Test
//    String signature = Crypto.sign("tatkhanhgia", getPrivateKey(), "base64");
//        System.out.println("Ve:"+verifyTokenByCode("tatkhanhgia",Base64.getUrlDecoder().decode(signature), getPublicKey()));
    }
}
