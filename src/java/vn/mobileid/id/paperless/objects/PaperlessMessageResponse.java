/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.keycloak.obj.KeycloakRes;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.object.enumration.Language;
import vn.mobileid.id.paperless.objects.QryptoErrorMessageJSNObject.Lock;
import vn.mobileid.id.paperless.objects.QryptoErrorMessageJSNObject.RemarkLanguage;
import vn.mobileid.id.paperless.objects.response.GetToken_IAM_MessageJSNObject;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class PaperlessMessageResponse implements ErrorMessageBuilder {

    private StringBuilder builder = new StringBuilder();
    private boolean isHaveDescription = false;

    private static final Logger LOG = LogManager.getLogger(PaperlessMessageResponse.class);

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
                try {
                    DatabaseResponse vn = new DatabaseImpl().getRemarkLanguage(
                            "RESPONSE_CODE",
                            responseCode.getRemark_Name(),
                            Language.Vietnamese.getName(),
                            transactionID);

                    DatabaseResponse eng = new DatabaseImpl().getRemarkLanguage(
                            "RESPONSE_CODE",
                            responseCode.getRemark_Name(),
                            Language.English.getName(),
                            transactionID);

                    RemarkLanguage remark = new RemarkLanguage();
                    remark.setRemark_VN((String) vn.getObject());
                    remark.setRemark_EN((String) eng.getObject());
                    responseMessageJSNObject.setRemark(remark);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return objectMapper.writeValueAsString(responseMessageJSNObject);
            } else {
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Response code " + code + " is not defined in database.");
                }
                responseMessageJSNObject.setCode(responseCode.getCode());
                responseMessageJSNObject.setCode_description(responseCode.getCode_description());
                return objectMapper.writeValueAsString(responseMessageJSNObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
            return PaperlessConstant.INTERNAL_EXP_MESS;
        }
    }

    public static QryptoErrorMessageJSNObject getErrorMessage_(
            int code,
            int subCode,
            int remaining_counter,
            int minute,
            int second,
            String lang,
            String transactionID) {
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
                try {
                    DatabaseResponse vn = new DatabaseImpl().getRemarkLanguage(
                            "RESPONSE_CODE",
                            responseCode.getRemark_Name(),
                            Language.Vietnamese.getName(),
                            transactionID);

                    DatabaseResponse eng = new DatabaseImpl().getRemarkLanguage(
                            "RESPONSE_CODE",
                            responseCode.getRemark_Name(),
                            Language.English.getName(),
                            transactionID);

                    RemarkLanguage remark = new RemarkLanguage();
                    remark.setRemark_VN((String) vn.getObject());
                    remark.setRemark_EN((String) eng.getObject());
                    responseMessageJSNObject.setRemark(remark);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    responseMessageJSNObject.setRemaining_counter(remaining_counter);
                    if (minute != -1 || second != -1) {
                        Lock lock = new Lock();
                        lock.setMinute(minute==-1?0:minute);
                        lock.setSecond(second==-1?0:second);
                        responseMessageJSNObject.setLock(lock);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return responseMessageJSNObject;
            } else {
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Response code " + code + " is not defined in database.");
                }
                responseMessageJSNObject.setCode(responseCode.getCode());
                responseMessageJSNObject.setCode_description(responseCode.getCode_description());
                return responseMessageJSNObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
            return null;
        }
    }

    public static String getAccessTokenMessage(
            int code,
            int subCode,
            String lang,
            KeycloakRes result,
            String transactionID) {
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
                responseCode = db.getResponse(String.valueOf(code),
                        transactionID);
                if (responseCode == null) {
                    if (LogHandler.isShowErrorLog()) {
                        LOG.error("Response code " + code + " is not defined in database.");
                    }
                    responseMessageJSNObject.setCode(PaperlessConstant.DEFAULT_MESS);
                    responseMessageJSNObject.setMessage(PaperlessConstant.DEFAULT_MESS);

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
            return PaperlessConstant.INTERNAL_EXP_MESS;
        }
    }

    public static boolean isVietnamese(String lang) {
        if (lang.compareToIgnoreCase("vn") == 0) {
            return true;
        }
        return false;
    }

    @Override
    public ErrorMessageBuilder sendErrorMessage(String message) {
        builder.append("{");
        builder.append("\"error\":\"");
        builder.append(message).append("\",");
        return this;
    }

    @Override
    public ErrorMessageBuilder sendErrorDescriptionMessage(String description) {
        builder.append("\"error_description\":\"");
        builder.append(description).append("\"");
        isHaveDescription = true;
        return this;
    }

    @Override
    public String build() {
        builder.append("}");
        if (!isHaveDescription) {
            builder.deleteCharAt(builder.lastIndexOf(","));
        }
        return builder.toString();
    }

    public static void main(String[] args) throws Exception {
        DatabaseResponse response = new DatabaseImpl().getRemarkLanguage(
                "RESPONSE_CODE",
                "10",
                "VN",
                "transactionId");
        System.out.println("Response:" + response.getObject());
    }

}
