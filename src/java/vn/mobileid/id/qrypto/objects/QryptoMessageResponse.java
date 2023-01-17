/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.qrypto.objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.keycloak.obj.KeycloakRes;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.objects.response.Create_WorkflowActivity_MessageJSNObject;
import vn.mobileid.id.qrypto.objects.response.QryptoErrorMessageJSNObject;
import vn.mobileid.id.qrypto.objects.response.GetToken_IAM_MessageJSNObject;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author ADMIN
 */
public class QryptoMessageResponse {

    private static final Logger LOG = LogManager.getLogger(QryptoMessageResponse.class);

    final private static ObjectMapper objectMapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true);

    public static String getLangFromJson(String json) {
        String lang = "en";
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            lang = rootNode.path("lang").asText();
            if (Utils.isNullOrEmpty(lang)) {
                lang = "en";
            }
        } catch (Exception e) {

        }
        return lang;
    }

    //Error Message
    public static String getErrorMessage(int code, int subCode, String lang, String transactionID) {
        try {
            QryptoErrorMessageJSNObject responseMessageJSNObject = new QryptoErrorMessageJSNObject();
            String strCode = String.valueOf(code) + String.valueOf(subCode);
            ResponseCode responseCode = Resources.getResponseCodes().get(strCode);
            if (responseCode == null) {
                Resources.reloadResponseCodes();
                responseCode = Resources.getResponseCodes().get(strCode);
            }

            if (responseCode != null) {
                responseMessageJSNObject.setCode(responseCode.getCode());
                responseMessageJSNObject.setCode_description(responseCode.getCode_description());
                return objectMapper.writeValueAsString(responseMessageJSNObject);
            } else {
//                Database db = new DatabaseImpl();
//                responseCode = db.getResponse(String.valueOf(code));
//                if (responseCode == null) {
                    if (LogHandler.isShowErrorLog()) {
                        LOG.error("Response code " + code + " is not defined in database.");
                    }
                    responseMessageJSNObject.setCode(responseCode.getCode());
                    responseMessageJSNObject.setCode_description(responseCode.getCode_description());
                    return objectMapper.writeValueAsString(responseMessageJSNObject);
//                } else {
//                    Resources.getResponseCodes().put(strCode, responseCode);
//                    responseMessageJSNObject.setCode(responseCode.getCode());
//                    responseMessageJSNObject.setCode_description(responseCode.getCode_description());
//                    return objectMapper.writeValueAsString(responseMessageJSNObject);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
            return QryptoConstant.INTERNAL_EXP_MESS;
        }
    }

    public static String getAccessTokenMessage(
            int code,
            int subCode,
            String lang,
            KeycloakRes result) {
        try {
            GetToken_IAM_MessageJSNObject responseMessageJSNObject = new GetToken_IAM_MessageJSNObject();
            String strCode = String.valueOf(code) + String.valueOf(subCode);
            ResponseCode responseCode = Resources.getResponseCodes().get(strCode);
            if (responseCode == null) {
                Resources.reloadResponseCodes();
                responseCode = Resources.getResponseCodes().get(strCode);
            }

            if (responseCode != null) {
                responseMessageJSNObject.setCode(responseCode.getCode());
                responseMessageJSNObject.setMessage(responseCode.getCode_description());

                responseMessageJSNObject.setAccessToken(result.getAccess_token());
                responseMessageJSNObject.setExpires_in(result.getExpires_in());
                responseMessageJSNObject.setRefreshToken(result.getRefresh_token());
                responseMessageJSNObject.setRefreshToken_expires_in(result.getRefresh_expires_in());
                responseMessageJSNObject.setTokenType(result.getToken_type());
                responseMessageJSNObject.setScope(result.getScope());

                return objectMapper.writeValueAsString(responseMessageJSNObject);
            } else {
                Database db = new DatabaseImpl();
                responseCode = db.getResponse(String.valueOf(code));
                if (responseCode == null) {
                    if (LogHandler.isShowErrorLog()) {
                        LOG.error("Response code " + code + " is not defined in database.");
                    }
                    responseMessageJSNObject.setCode(QryptoConstant.DEFAULT_MESS);
                    responseMessageJSNObject.setMessage(QryptoConstant.DEFAULT_MESS);

                    responseMessageJSNObject.setAccessToken(result.getAccess_token());
                    responseMessageJSNObject.setExpires_in(result.getExpires_in());
                    responseMessageJSNObject.setRefreshToken(result.getRefresh_token());
                    responseMessageJSNObject.setRefreshToken_expires_in(result.getRefresh_expires_in());
                    responseMessageJSNObject.setTokenType(result.getToken_type());
                    responseMessageJSNObject.setScope(result.getScope());

                    return objectMapper.writeValueAsString(responseMessageJSNObject);
                } else {
                    Resources.getResponseCodes().put(strCode, responseCode);

                    responseMessageJSNObject.setCode(responseCode.getCode());
                    responseMessageJSNObject.setMessage(responseCode.getCode_description());

                    responseMessageJSNObject.setAccessToken(result.getAccess_token());
                    responseMessageJSNObject.setExpires_in(result.getExpires_in());
                    responseMessageJSNObject.setRefreshToken(result.getRefresh_token());
                    responseMessageJSNObject.setRefreshToken_expires_in(result.getRefresh_expires_in());
                    responseMessageJSNObject.setTokenType(result.getToken_type());
                    responseMessageJSNObject.setScope(result.getScope());

                    return objectMapper.writeValueAsString(responseMessageJSNObject);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
            return QryptoConstant.INTERNAL_EXP_MESS;
        }
    }   
    
    public static boolean isVietnamese(String lang) {
        if (lang.compareToIgnoreCase("vn") == 0) {
            return true;
        }
        return false;
    }

}
