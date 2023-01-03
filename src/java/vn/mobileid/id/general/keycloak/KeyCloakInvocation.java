/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.keycloak;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.HttpRequest;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.gateway.p2p.objects.P2PFunction;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.Entity;
import vn.mobileid.id.qrypto.objects.QryptoConstant;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author ADMIN
 */
public class KeyCloakInvocation {

    final public static String ENGINE_PRO_URL = "URL";
    final public static String ENGINE_PRO_REALM = "REALM";
    final public static String ENGINE_PRO_CLIENT_ID = "RSSP_CLIENT_ID";
    final public static String ENGINE_PRO_CLIENT_SECRET = "RSSP_CLIENT_SECRET";
    final public static String ENGINE_PRO_GRANT_TYPE = "RSSP_GRANT_TYPE";

    final private static Logger LOG = LogManager.getLogger(KeyCloakInvocation.class);
    final private String url;
    final private String realm;
    final private String clientId;
    private String clientSecret;
    final private String grantType;

    final private static String FUNCTION_TOKEN = "/protocol/openid-connect/token";
    final private static String FUNCTION_CREATE_USER = "/users";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static KeycloakRes accessToken;

    final private String entityBillCode;
    final private String requestBillCode;
    final private String idAddr;

    final private String username;
    final private String password;
    
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
    }

    public  synchronized KeycloakRes getAccessToken(boolean renewAccessToken) {
        if (renewAccessToken) {
            if (LogHandler.isShowDebugLog()) {
                LOG.debug("Get new accessToken");
            }
            KeycloakRes act = null;
            try {
                act = token();
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
                return getAccessToken(true);
            } else {
                return accessToken;
            }
        }
    }

    private KeycloakRes token() throws Exception {
        String tokenUrl = url + "/realms/" + realm + FUNCTION_TOKEN;

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        if(clientSecret == null){
            clientSecret = Configuration.getInstance().getKeycloakClient_secret();
        }
        
        String urlParameters = "client_id=" + URLEncoder.encode(clientId, "UTF-8") + "&client_secret=" + URLEncoder.encode(clientSecret, "UTF-8") + "&grant_type=" + URLEncoder.encode(grantType, "UTF-8")
        + "&username=" + URLEncoder.encode(username, "UTF-8")
        + "&password=" + URLEncoder.encode(password, "UTF-8");
        
        KeycloakRes token = null;        
        HttpRequest httpRequest = new HttpRequest(true, urlParameters, tokenUrl, headers);
        HttpRequest.Response response = httpRequest.sendRequest();
//        if (response.getHttpCode() == 200) {
            try {
                token = objectMapper.readValue(response.getBody(), KeycloakRes.class);                                
            } catch (IOException e) {
                e.printStackTrace();
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while parsing the json response. Details: " + Utils.printStackTrace(e));
                }
            }
//        }
//        P2PLogging p2pLogging = new P2PLogging(
//                entityBillCode,
//                requestBillCode,
//                idAddr,
//                null, //json request 
//                response.getBody(),
//                token == null ? QryptoConstant.CODE_UNEXPE_EXP : QryptoConstant.CODE_SUCCESS,
//                P2PFunction.IAM_GET_TOKEN);
//        new Thread(p2pLogging).start();
        return token;
    }

//    private String getUser(String email, String username) throws Exception {
//        String act = getAccessToken(false);
//        if (Utils.isNullOrEmpty(act)) {
//            return null;
//        }
//
//        String urlParameters = "briefRepresentation=true&email=" + URLEncoder.encode(email, "UTF-8") + "&username=" + URLEncoder.encode(username, "UTF-8") + "&exact=true";
//
//        String usersUrl = url + "/admin/realms/" + realm + FUNCTION_CREATE_USER;
//
//        usersUrl = usersUrl + "?" + urlParameters;
//
//        HashMap<String, String> headers = new HashMap<>();
//        headers.put("Authorization", "Bearer " + act);
//
//        HttpRequest httpRequest = new HttpRequest(false, urlParameters, usersUrl, headers);
//        HttpRequest.Response response = httpRequest.sendRequest();
//
//        int p2pResponseCode = QryptoConstant.CODE_UNEXPE_EXP;
//        if (response.getHttpCode() == 200) {
//            p2pResponseCode = QryptoConstant.CODE_SUCCESS;
//        }
//
//        P2PLogging p2pLogging = new P2PLogging(
//                entityBillCode,
//                requestBillCode,
//                idAddr,
//                usersUrl, //json request 
//                response.getBody(),
//                p2pResponseCode,
//                P2PFunction.IAM_CREATE_USER);
//        new Thread(p2pLogging).start();
//
//        if (response.getHttpCode() == 200) {
//            JsonNode rootNode = objectMapper.readTree(response.getBody());
//            String userId = rootNode.get(0).path("id").asText();
//            if (rootNode.size() > 1) {
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("There are two users response in JSON. It should be checked");
//                }
//            }
//            return userId;
//        }
//        if (LogHandler.isShowErrorLog()) {
//            LOG.error("Error while getting user from keycloak. Response HTTP code: " + response.getHttpCode() + " Body: " + response.getBody());
//        }
//        return null;
//    }
//
//    public String users(int numOfRetry, KeycloakReq keycloakReq) { // return user id
//        String act = getAccessToken(false);
//        if (Utils.isNullOrEmpty(act)) {
//            return null;
//        }
//        String bodyRequest = null;
//        try {
//            bodyRequest = objectMapper.writeValueAsString(keycloakReq);
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while convert KeycloakReq object to JSON. Details: " + Utils.printStackTrace(e));
//            }
//            return null;
//        }
//
//        String usersUrl = url + "/admin/realms/" + realm + FUNCTION_CREATE_USER;
//
//        HashMap<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/json");
//        headers.put("Authorization", "Bearer " + act);
//        HttpRequest httpRequest = new HttpRequest(true, bodyRequest, usersUrl, headers);
//        HttpRequest.Response response = httpRequest.sendRequest();
//
//        int p2pResponseCode = QryptoConstant.CODE_UNEXPE_EXP;
//        String jsonResp = "";
//        if (response.getHttpCode() == 201) {
//            p2pResponseCode = QryptoConstant.CODE_SUCCESS;
//            jsonResp = "{\"responseMessage\":\"SUCCESSFULLY\"}";
//        } else if (response.getHttpCode() == 409) {
//            p2pResponseCode = QryptoConstant.CODE_SUCCESS;
//            jsonResp = "{\"responseMessage\":\"Username " + keycloakReq.getUsername() + " has already created on Keycloak\"}";
//        } else if (response.getHttpCode() == 401) {
//            p2pResponseCode = QryptoConstant.CODE_SUCCESS;
//            jsonResp = "{\"responseMessage\":\"Look like accessToken is expired. Try to get the new accessToken\"}";
//        } else {
//            jsonResp = "{\"responseMessage\":\"UNEXPECTED EXCEPTION\"}";
//        }
//
//        P2PLogging p2pLogging = new P2PLogging(
//                entityBillCode,
//                requestBillCode,
//                idAddr,
//                null, //json request 
//                jsonResp,
//                p2pResponseCode,
//                P2PFunction.IAM_CREATE_USER);
//        new Thread(p2pLogging).start();
//
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("IAM HTTP response code of users function: " + response.getHttpCode());
//        }
//
//        if (response.getHttpCode() == 201) {
//            HashMap<String, String> responseHeader = response.getResponseHeader();
//            String locationUrl = null;
//            if (responseHeader.containsKey("location")) {
//                locationUrl = responseHeader.get("location");
//            } else if (responseHeader.containsKey("Location")) {
//                locationUrl = responseHeader.get("Location");
//            } else {
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("No Location field in header response of IAM --> cannot get UserUUID");
//                }
//                return null;
//            }
//            String userId = locationUrl.substring(locationUrl.lastIndexOf("/") + 1);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("IAM userId: " + userId);
//            }
//            return userId;
//        } else if (response.getHttpCode() == 409) {
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("Username " + keycloakReq.getUsername() + " has already created on Keycloak");
//            }
//            try {
//                String userId = getUser(keycloakReq.getEmail(), keycloakReq.getUsername());
//                return userId;
//            } catch (Exception e) {
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while getting user from keycloak");
//                }
//                e.printStackTrace();
//            }
//            return null;
//        } else if (response.getHttpCode() == 401) {
//            if (numOfRetry == 0) {
//                return null;
//            }
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("Look like accessToken is expired. Try to get the new accessToken");
//            }
//            getAccessToken(true);
//            return users(--numOfRetry, keycloakReq);
//        } else {
//            return null;
//        }
//    }

    private class P2PLogging implements Runnable {

        final private String entityBillCode;
        final private String requestBillCode;
        final private String ipAddr;
        final private String jsonReq;
        final private String jsonResp;
        final private int responseCode;
        final private String p2pFunction;

        public P2PLogging(String entityBillCode,
                String requestBillCode,
                String ipAddr,
                String jsonReq,
                String jsonResp,
                int responseCode,
                String p2pFunction) {
            this.entityBillCode = entityBillCode;
            this.requestBillCode = requestBillCode;
            this.ipAddr = ipAddr;
            this.jsonReq = jsonReq;
            this.jsonResp = jsonResp;
            this.responseCode = responseCode;
            this.p2pFunction = p2pFunction;
        }

        @Override
        public void run() {
//            Database db = new DatabaseImpl();
//            DatabaseResponse dpresp = db.getP2PLogID();
//            String billCode = Utils.generateBillCode(Entity.ENTITY_IDENTITY_ENTITY, dpresp.getP2pId(), dpresp.getP2pDt());
//            db.insertP2PLog(
//                    dpresp.getP2pId(),
//                    Resources.getEntities().get(Entity.ENTITY_IDENTITY_ENTITY).getEntityID(),
//                    Resources.getEntities().get(Entity.ENTITY_IDENTITY_ENTITY).getEntityID(),
//                    Resources.getEntities().get(Entity.ENTITY_IDENTITY_ENTITY).getUri(),
//                    Resources.getEntities().get(Entity.ENTITY_IDENTITY_ENTITY).getUri(),
//                    entityBillCode,
//                    requestBillCode,
//                    billCode,
//                    Resources.getP2PFunction(p2pFunction).getP2pFunctionID(),
//                    Resources.getResponseCodes().get(String.valueOf(responseCode)).getId(),
//                    ipAddr,
//                    jsonReq,
//                    jsonResp,
//                    dpresp.getP2pDt(),
//                    dpresp.getP2pDt(),
//                    Configuration.getInstance().getAppUserDBID());
        }

    }
}
