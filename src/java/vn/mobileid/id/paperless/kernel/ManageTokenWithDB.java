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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import vn.mobileid.id.eid.object.JWT_Authenticate;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.KeycloakReq;
import vn.mobileid.id.general.keycloak.obj.KeycloakRes;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.kernel.process.ProcessEID_JWT;
import vn.mobileid.id.paperless.kernelADMIN.GetKEYAPI;
import vn.mobileid.id.paperless.objects.Enterprise;
import vn.mobileid.id.paperless.objects.JWT_Request;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.RefreshToken;
import vn.mobileid.id.utils.Crypto;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ManageTokenWithDB {

//    final private static Logger LOG = LogManager.getLogger(ManageTokenWithDB.class);
    private static long now;

    public static InternalResponse processLogin(
            final HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        KeycloakReq object = new KeycloakReq();
        ObjectMapper mapper = new ObjectMapper();
        try {
            object = mapper.readValue(payload, KeycloakReq.class);
        } catch (JsonProcessingException ex) {
            LogHandler.error(ManageTokenWithDB.class, transactionID, "Cannot parse payload!", ex);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Login 
        if (object.getGrant_type().contains("password")) {
            if (!checkPayload(payload)) {
                return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                        PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                                PaperlessConstant.SUBCODE_MISSING_USER_NAME_OR_PASSWORD,
                                "en",
                                null));
            }
            return login(object.getUsername(),
                    object.getPassword(),
                    object.isRememberMeEnable(),
                    transactionID);
        }
        if (object.getGrant_type().contains("refresh_token")) {
            return reCreate(
                    object.getRefreshToken(),
                    transactionID);
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_500, "NOT SUPPORT YET");
    }

    public static InternalResponse processLoginURLEncode(
            final HttpServletRequest request,
            HashMap<String, String> map,
            String transactionID) throws Exception {
        if (!map.containsKey("username") || !map.containsKey("password")) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_MISSING_USER_NAME_OR_PASSWORD,
                            "en",
                            null));
        }

        //Login         
        return login(
                map.get("username"),
                map.get("password"),
                Boolean.valueOf(map.get("remember_me_enabled")),
                transactionID);
    }

    public static InternalResponse processVerify(
            final HttpServletRequest request,
            String transactionID,
            boolean adminEnable) throws Exception {
        //Get Access Token
        String token = request.getHeader("Authorization");
//        if (LogHandler.isShowDebugLog()) {
//            LogHandler.debug(ManageTokenWithDB.class,transactionID,"Checking Header!!\n"+"Token:"+token);            
//        }

        if (token == null || token.contains("null")) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_MISSING_ACCESS_TOKEN, "en", null)
            );
        }

        //Verify
        return adminEnable
                ? verifyAdminMode(token, transactionID)
                : verify(token, transactionID);
    }

    public static InternalResponse processRevoke(
            final HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        KeycloakReq object = new KeycloakReq();
        ObjectMapper mapper = new ObjectMapper();
        try {
            object = mapper.readValue(payload, KeycloakReq.class);
        } catch (JsonProcessingException ex) {
            LogHandler.error(ManageTokenWithDB.class, transactionID, "Cannot parse payload!", ex);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Checking        
        if (object.getRefreshToken() == null || object.getRefreshToken().isEmpty()) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_MISSING_REFRESH_TOKEN,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        return revoke(object.getRefreshToken(),
                transactionID);
    }

    public static InternalResponse processLoginSSO(
            final HttpServletRequest request,
            String payload,
            String transactionID
    ) throws Exception {
        JWT_Request jwtdata = new JWT_Request();
        ObjectMapper mapper = new ObjectMapper();
        try {
            jwtdata = mapper.readValue(payload, JWT_Request.class);
        } catch (JsonProcessingException ex) {
            LogHandler.error(ManageTokenWithDB.class, transactionID, "Cannot parse payload!", ex);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Login         
        return loginSSO(jwtdata, transactionID);
    }

    //====================INTERNAL FUNCTION==========
    private static InternalResponse login(
            String email,
            String pass,
            boolean remember_me,
            String transactionID) throws
            Exception {
        Database db = new DatabaseImpl();
        //Login
        DatabaseResponse res = db.login(email, pass, transactionID);

        if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                    res.getStatus(),
                    "en",
                    null);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    message);
        }

        User info = (User) res.getObject();

        InternalResponse res2 = GetEnterpriseInfo.getEnterpriseInfo(
                info.getEmail(),
                transactionID);
        if (res2.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res2;
        }
        Enterprise enterprise = (Enterprise) res2.getData();

        try {
            String[] temp = createAccess_RefreshToken(info, enterprise);

            String accessToken = temp[0];
            String refreshtoken = temp[1];
            String sessionID = temp[2];
            String iat = temp[3];
            String exp = temp[4];

            KeycloakRes response = new KeycloakRes();
            response.setAccess_token(accessToken);
            if (remember_me) {
                response.setRefresh_token(refreshtoken);
                response.setRefresh_expires_in((int) PaperlessConstant.refresh_token_expired_in);
            }
            response.setExpires_in((int) PaperlessConstant.expired_in);
            response.setToken_type(PaperlessConstant.TOKEN_TYPE_BEARER);

            //Write refreshtoken into DB           
            InternalResponse internalResponse = ManageRefreshToken.write(
                    info.getEmail(),
                    sessionID,
                    1,
                    enterprise.getName(),
                    new Date(Long.parseLong(iat)),
                    new Date(Long.parseLong(exp)),
                    "HMAC",
                    info.getEmail(),
                    transactionID);
            if (internalResponse.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return internalResponse;
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, response);
        } catch (Exception e) {
            throw new Exception("Cannot create token!", e);
        }
    }

    private static InternalResponse verify(
            String token,
            String transactionID) throws Exception {
        token = token.replaceAll("Bearer ", "");

        //Decode JWT
        String[] chunks = token.split("\\.");

        String header = null;
        String payload = null;
        String signature = null;
        String alg = null;

        try {
            LogHandler.debug(ManageTokenWithDB.class,
                    "\n\tBefore decode token"
                    + "\n\tHeader:" + chunks[0]
                    + "\n\tPayload:" + chunks[1]
                    + "\n\tSignature:" + chunks[2]);
            header = new String(Base64.getUrlDecoder().decode(chunks[0]), "UTF-8");
            payload = new String(Base64.getUrlDecoder().decode(chunks[1]), "UTF-8");

            signature = chunks[2];
            int pos = header.indexOf("alg");
            int typ = header.indexOf("typ");
            alg = header.substring(pos + 6, typ - 3);
        } catch (Exception e) {
            LogHandler.error(ManageTokenWithDB.class, transactionID, "Error while decode token!", e);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_INVALID_TOKEN,
                            "en",
                            null));
        }
        LogHandler.debug(ManageTokenWithDB.class,
                "\n\tAfter decode token"
                + "\n\tHeader:" + header
                + "\n\tPayload:" + payload
                + "\n\tSignature:" + signature);

        //Convert to Object
        User data = null;
        try {
            data = new ObjectMapper().readValue(payload, User.class);
        } catch (Exception e) {
            LogHandler.error(ManageTokenWithDB.class, transactionID, "Error while parsing Data!", e);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_INVALID_TOKEN,
                            "en",
                            null));
        }

        //Verify Token        
        if (verifyTokenByCode(chunks[0] + "." + chunks[1], signature, getPublicKey())) {
            Date date = new Date();
            if (data.getExp() < date.getTime()) {
                return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                        PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                                PaperlessConstant.SUBCODE_TOKEN_EXPIRED, "en", null));
            }
            //Check accessToken in DB
            InternalResponse res = checkAccessToken(
                    data.getEmail(),
                    data.getSid(),
                    transactionID);
            if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return res;
            }
            InternalResponse response = new InternalResponse();
            response.setStatus(PaperlessConstant.HTTP_CODE_SUCCESS);
            response.setUser(data);
            return response;
        }

        return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                        PaperlessConstant.SUBCODE_INVALID_TOKEN, "en", null));

    }

    private static InternalResponse verifyAdminMode(
            String token,
            String transactionID) throws Exception {
        token = token.replaceAll("Bearer ", "");

        //Decode JWT
        try {
            token = new String(Base64.getUrlDecoder().decode(token), "UTF-8");
        } catch (Exception ex) {
            LogHandler.error(ManageTokenWithDB.class, transactionID, "Error while decode token!", ex);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_INVALID_TOKEN,
                            "en",
                            null));
        }
        String[] chunks = token.split("\\:");

        String clientID = chunks[0];
        String clientSecret = chunks[1];

        LogHandler.debug(ManageTokenWithDB.class, transactionID,
                "\tAdmin Token"
                + "\n\tClientID:" + chunks[0]
                + "\n\tClientSecret:" + chunks[1]);

        //Verify Token        
        Enterprise ent = (Enterprise) GetKEYAPI.getKEYAPI(0, clientID, transactionID).getData();
        if (!clientID.equals(ent.getClientID())) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_INVALID_CLIENT_ID, "en", null));
        }
        if (!clientSecret.equals(ent.getClientSecret())) {
//                System.out.println("ClientSe:"+clientSecret);
//                System.out.println("ClientSeDB:"+ent.getClientSecret());
            return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_INVALID_CLIENT_SECRET, "en", null));
        }
        if (ent == null) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessConstant.INTERNAL_EXP_MESS);
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                ent);
    }

    private static InternalResponse revoke(
            String token,
            String transactionID) throws Exception {
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
                LogHandler.error(ManageTokenWithDB.class, transactionID, "Error while parsing Data!", e);
                return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                        PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                                PaperlessConstant.SUBCODE_INVALID_TOKEN,
                                "en",
                                null));
            }
        } catch (Exception e) {
            LogHandler.error(ManageTokenWithDB.class, transactionID, "Error while decode token!", e);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_INVALID_TOKEN,
                            "en",
                            null));
        }

        return ManageRefreshToken.remove(data.getSid(), transactionID);
    }

    private static InternalResponse reCreate(
            String token,
            String transactionID) throws Exception {
        String[] chunks = token.split("\\.");
        User user = null;
        //Check expires token
        try {
            String temp = new String(Base64.getUrlDecoder().decode(chunks[1]), "UTF-8");
            user = new ObjectMapper().readValue(temp, User.class);
        } catch (Exception ex) {
            LogHandler.error(ManageTokenWithDB.class, transactionID, "Cannot decode/parse Token!", ex);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_INVALID_TOKEN, "en", null));
        }
        Date date = new Date();
        if (user.getExp() < date.getTime()) {
            revoke(token, transactionID);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_TOKEN_EXPIRED, "en", null));
        }

        Database db = new DatabaseImpl();

        //Login
        InternalResponse res = ManageRefreshToken.get(
                user.getSid(),
                transactionID);

        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        RefreshToken object = (RefreshToken) res.getData();

        InternalResponse res2 = GetUser.getUser(
                object.getEmail(),
                0,
                user.getAid(),
                transactionID,
                true);
        if (res2.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res2;
        }

        Enterprise enterprise = new Enterprise();
        enterprise.setId(user.getAid());
        enterprise.setName(user.getAzp());

        String[] temp = createAccess_RefreshToken((User) res2.getData(), enterprise);

        String accessToken = temp[0];
        String refreshtoken = temp[1];
        String sessionID = temp[2];
        String iat = temp[3];
        String exp = temp[4];

        KeycloakRes response = new KeycloakRes();
        response.setAccess_token(accessToken);
        response.setRefresh_token(refreshtoken);
        response.setExpires_in((int) PaperlessConstant.expired_in);
        response.setToken_type(PaperlessConstant.TOKEN_TYPE_BEARER);
        response.setRefresh_expires_in((int) PaperlessConstant.refresh_token_expired_in);

        //Write refreshtoken into DB           
        InternalResponse internalResponse = ManageRefreshToken.write(
                ((User) res2.getData()).getEmail(),
                sessionID,
                1,
                enterprise.getName(),
                new Date(Long.parseLong(iat)),
                new Date(Long.parseLong(exp)),
                "HMAC",
                ((User) res2.getData()).getEmail(),
                transactionID);
        if (internalResponse.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return internalResponse;
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, response);
    }

    private static InternalResponse loginSSO(
            JWT_Request request,
            String transactionID) throws Exception {
        Database db = new DatabaseImpl();
        JWT_Authenticate jwtdata = new JWT_Authenticate();

        InternalResponse ress = ProcessEID_JWT.getInfoJWT(request.getJwt_token());
        if (ress.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return ress;
        }
        jwtdata = (JWT_Authenticate) ress.getData();

        //Get enterprise data from JWT
        DatabaseResponse res = db.getEnterpriseInfoOfUser(jwtdata.getEmail(), transactionID);
        if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                    res.getStatus(),
                    "en",
                    null);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    message);
        }
        List<Enterprise> list_enterprise = (List<Enterprise>) res.getObject();

        //Create AccessToken
        res = db.getUser(
                jwtdata.getEmail(), 0,
                list_enterprise.get(0).getId(),
                transactionID,
                true);
        if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                    res.getStatus(),
                    "en",
                    null);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    message);
        }

        User info = (User) res.getObject();

        InternalResponse res2 = GetEnterpriseInfo.getEnterpriseInfo(
                info.getEmail(),
                transactionID);
        if (res2.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
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
        response.setExpires_in((int) PaperlessConstant.expired_in);
        response.setToken_type(PaperlessConstant.TOKEN_TYPE_BEARER);
        response.setRefresh_expires_in((int) PaperlessConstant.refresh_token_expired_in);

        //Write refreshtoken into DB           
        InternalResponse internalResponse = ManageRefreshToken.write(
                info.getEmail(),
                sessionID,
                1,
                enterprise.getName(),
                new Date(Long.parseLong(iat)),
                new Date(Long.parseLong(exp)),
                "HMAC",
                info.getEmail(),
                transactionID);
        if (internalResponse.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return internalResponse;
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, response);
    }

    private static InternalResponse checkAccessToken(
            String email,
            String sessionID,
            String transactionID) throws Exception {
        return ManageRefreshToken.check(email, sessionID, transactionID);
    }

    //===============INTERNAL METHOD===========================
    private static String createHeader() {
        String temp = "{";
        temp += "\"alg\":" + "\"" + "RS256" + "\",";
        temp += "\"typ\":" + "\"" + PaperlessConstant.typ + "\"";
        temp += "}";
        return temp;
    }

    private static String createPayload(User user) {
        try {
            return new ObjectMapper().writeValueAsString(user);
        } catch (JsonProcessingException ex) {
            LogHandler.error(ManageTokenWithDB.class, "Cannot create payload!", ex);
            return null;
        }
    }

    private static User createBasicUserData(
            User user,
            Enterprise enterprise
    ) {
        User temp = new User();
        temp.setIat(Date.from(Instant.now()).getTime()); //Issue at
        temp.setExp(Date.from(Instant.now().plusSeconds(PaperlessConstant.expired_in)).getTime()); //Expired                

//        temp.setJti(""); //JWT ID
        temp.setIss("https://paperless.mobile-id.vn");  //Issuer
//        temp.setAud("account"); //Audience
//        temp.setSub(""); //Subject
        temp.setTyp(PaperlessConstant.TOKEN_TYPE_BEARER);
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
        String signature = Crypto.sign(
                header + "." + payload,
                getPrivateKey(),
                "base64",
                PaperlessConstant.alg);
        String[] result = new String[5];
        result[0] = header + "." + payload + "." + signature;
        result[1] = createRefreshToken(temp);
        result[2] = temp.getSid();
        result[3] = String.valueOf(temp.getIat());
        Date temps = new Date(temp.getIat());
        result[4] = String.valueOf(temps.toInstant().plusSeconds(PaperlessConstant.refresh_token_expired_in).toEpochMilli());
        return result;
    }

    private static String createRefreshToken(
            User user) throws JsonProcessingException, IOException, GeneralSecurityException, Exception {
        //Create Data
        user.setName(null);
        user.setEmail(null);
        user.setTyp(PaperlessConstant.TOKEN_TYPE_REFRESH);
        long now = user.getIat();
        Date temp = new Date(now);
        user.setExp(temp.toInstant().plusSeconds(PaperlessConstant.refresh_token_expired_in).toEpochMilli());
        String payload = new ObjectMapper().writeValueAsString(user);
        String header = createHeader();
        header = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes());
        payload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes());
        String signature = Crypto.sign(
                header + "." + payload,
                getPrivateKey(),
                "base64",
                PaperlessConstant.alg);
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
            LogHandler.error(ManageTokenWithDB.class, "Cannot Create Signature for JWT!");
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

            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, "");

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
                LogHandler.error(ManageTokenWithDB.class, "Expired token!");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_TOKEN_EXPIRED,
                            "en",
                            null));
        } catch (JWTVerificationException e) {
            if (LogHandler.isShowErrorLog()) {
                e.printStackTrace();
                LogHandler.error(ManageTokenWithDB.class, "Token is invalid! - Details:" + e);
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_INVALID_TOKEN,
                            "en",
                            null));
        }
    }

    private static boolean verifyTokenByCode(String data, String signature, PublicKey pub) throws Exception {
        try {
            Security.addProvider(new BouncyCastleProvider());
            Signature sign = Signature.getInstance(PaperlessConstant.alg);
            sign.initVerify(pub);
            sign.update(data.getBytes());
            return sign.verify(Base64.getUrlDecoder().decode(signature));
        } catch (NoSuchAlgorithmException ex) {
            throw new Exception("NoSuchAlgorithm!", ex);
        } catch (InvalidKeyException ex) {
            throw new Exception("InvalidKeyException!", ex);
        } catch (SignatureException ex) {
            throw new Exception("SignatureException!", ex);
        }
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
//            if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
//                String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
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
