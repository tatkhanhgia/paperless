/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import javafx.scene.input.KeyCharacterCombination;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.KeyCloakInvocation;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InfoJSNObject;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.qrypto.kernel.QryptoGetToken;
import vn.mobileid.id.qrypto.objects.QryptoConstant;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class QryptoService {

    final private static Logger LOG = LogManager.getLogger(QryptoService.class);

    final private static Properties appInfo = Configuration.getInstance().getAppInfo();

    public static String getInfo() {
        try {
            String[] version = appInfo.getProperty("dtis.app.version").split(";");
            String name = appInfo.getProperty("dtis.app.name");
            String description = appInfo.getProperty("dtis.app.description");
            String logo = appInfo.getProperty("dtis.app.logo");
            String[] langs = appInfo.getProperty("dtis.app.langs").split(";");
            String[] methods = appInfo.getProperty("dtis.app.methods").split(";");

            String region = appInfo.getProperty("dtis.app.region");
            SimpleDateFormat sdf = new SimpleDateFormat(appInfo.getProperty("dtis.app.dateformat"));
            sdf.setTimeZone(TimeZone.getTimeZone(System.getProperty("user.timezone")));
            String serverTime = sdf.format(Calendar.getInstance().getTime());

            InfoJSNObject info = new InfoJSNObject();
            info.setVersions(version);
            info.setName(name);
            info.setDescription(description);
            info.setLogo(logo);
            info.setLangs(langs);
            info.setMethods(methods);
            info.setRegion(region);
            info.setServerTime(serverTime);

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(info);
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while getting version. Details: " + Utils.printStackTrace(e));
            }
        }
        return QryptoConstant.INTERNAL_EXP_MESS;
    }

    /**
     * Using to get Token from IAM Server
     *
     * @param request
     * @param payload : Body of request
     * @param functionId : 0-Application/JSON ; 1-Application-form-URLEncode
     * @return
     */
    public static InternalResponse getToken(final HttpServletRequest request, String payload, int functionId) {
        if (functionId == 0) {
            QryptoGetToken token = new QryptoGetToken();
            InternalResponse response = token.processJSON(request, payload);
            return response;
        } else {
            LOG.error("lOG INTO");
            QryptoGetToken token = new QryptoGetToken();
            InternalResponse response = token.processFORM(request);
            return response;
        }        
    }

//    public static String getToken(final HttpServletRequest request, String payload, int functionId) {
//        QryptoAWSAuthentication auth = new QryptoAWSAuthentication();
//        InternalResponse verifyRequestResp = auth.verifyRequest(request, payload, functionId);
//        if (verifyRequestResp.getAuthenticationJSNMessage() != QryptoConstant.AWS_CREDENTIALS_SUCCESS) {
//            return verifyRequestResp.getAuthenticationJSNMessage();
//        } else {
//            //
//            //
//            // LOGGING
//            Database db = new DatabaseImpl();
//            DatabaseResponse dr = db.getIdentityLogId();
//
//            String transactionId = Utils.generateTransactionId(
//                    verifyRequestResp.getRelyingPartyName(), dr.getIdentityLogId(), dr.getIdentityLogDt());
//
//            QryptoGetToken getToken = new QryptoGetToken();
//            InternalResponse internalResponse = getToken.processJSON(request, payload, transactionId);
//
//            ResponseCode responseCode = Resources.getResponseCodes().get(String.valueOf(internalResponse.getStatus()));
//            if (responseCode == null) {
//                Resources.reloadResponseCodes();
//                responseCode = Resources.getResponseCodes().get(String.valueOf(internalResponse.getStatus()));
//            }
//            int responseCodeId = (responseCode == null) ? (int) QryptoConstant.FEDERAL_ID : responseCode.getId();
//            Date now = Calendar.getInstance().getTime();
//            if (dr.getIdentityLogId() != 0) {
//                if (db.insertIdentityLog(dr.getIdentityLogId(),
//                        QryptoConstant.FEDERAL_ID, // subjectId
//                        null, //subjectUUID
//                        QryptoConstant.FEDERAL_ID, // processId
//                        null, //processUUID
//                        Utils.getExtraInfoInRequest(payload)[0], //extendedId
//                        payload, //requestData
//                        internalResponse.getMessage(), //responseData
//                        Utils.extractRequestHeader(request),
//                        Utils.getExtraInfoInRequest(payload)[1], //bill_code in request
//                        transactionId, //transactionId
//                        responseCodeId, //responseCodeId
//                        functionId, //functionId
//                        verifyRequestResp.getRelyingPartyId(), //relyingPartyId
//                        request.getRemoteAddr(),
//                        now,
//                        now)) {
//
//                } else {
//                    // insert log attribute
//
//                }
//                return internalResponse.getMessage();
//            } else {
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("CANNOT insert identity log due to LOG_ID is zero");
//                }
//                return QryptoMessageResponse.getMessage(
//                        QryptoConstant.CODE_UNEXPE_EXP,
//                        QryptoConstant.SUBCODE_NOSUBCODE,
//                        QryptoMessageResponse.getLangFromJson(payload),
//                        transactionId);
//            }
//        }
}
