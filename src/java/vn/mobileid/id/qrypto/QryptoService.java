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
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InfoJSNObject;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.qrypto.kernel.CreateWorkflow;
import vn.mobileid.id.qrypto.kernel.CreateWorkflowDetail;
import vn.mobileid.id.qrypto.kernel.ManageToken;
import vn.mobileid.id.qrypto.objects.QryptoItemWorkflowDetailJSNObject;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.qrypto.objects.QryptoWorkflowJSNObject;
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

    public static InternalResponse getToken(final HttpServletRequest request, String payload, int functionId) {
        if (functionId == 0) {
            ManageToken token = new ManageToken();
            InternalResponse response = token.processJSON_getToken(request, payload);
            return response;
        } else {
            ManageToken token = new ManageToken();
            InternalResponse response = token.processFORM_getToken(request, payload);
            return response;
        }
    }

    public static InternalResponse revoke(final HttpServletRequest request, String payload, int functionId) {
        if (functionId == 0) {
            ManageToken token = new ManageToken();
            InternalResponse response = token.processJSON_revoke(request, payload);
            return response;
        } else {
            ManageToken token = new ManageToken();
            InternalResponse response = token.processFORM_revoke(request, payload);
            return response;
        }
    }

    public static InternalResponse createWorkflow(final HttpServletRequest request, String payload) {
        //Thiếu check content type - accessToken - header !!!!

        if (Utils.isNullOrEmpty(payload)) {
            LOG.info("No payload found");
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        //Check Workflow_Temlate_Type
        if (!checkTemplateTypeInRequest(payload)) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getMessage(QryptoConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            QryptoConstant.SUBCODE_MISSING_OR_ERROR_TEMPLATE_TYPE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        ObjectMapper mapper = new ObjectMapper();
        QryptoWorkflowJSNObject workflow = new QryptoWorkflowJSNObject();
        try {
            workflow = mapper.readValue(payload, QryptoWorkflowJSNObject.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse payload");
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check valid data 
        InternalResponse result = null;
        result = CreateWorkflow.checkDataWorkflow(workflow);
        if (result.getStatus() != 200) {
            return result;
        }

        //Processing
        try {
            return CreateWorkflow.processingCreateWorkflow(workflow);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot create new Workflow");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse createWorkflowDetail(final HttpServletRequest request, String payload, int id) {
        //Thiếu check content type - accessToken - header !!!!

        if (Utils.isNullOrEmpty(payload)) {
            LOG.info("No payload found");
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        payload = payload.replaceAll("\n", "");
        payload = payload.replaceAll("\t", "");
        payload = payload.replaceAll("[ ]{2,10}", "");
        ObjectMapper mapper = new ObjectMapper();

        QryptoItemWorkflowDetailJSNObject workflow = new QryptoItemWorkflowDetailJSNObject();
        try {
            workflow = mapper.readValue(payload, QryptoItemWorkflowDetailJSNObject.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse payload");
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check valid data 
        InternalResponse result = null;
        result = CreateWorkflowDetail.checkDataWorkflowDetail(workflow);
        if (result.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
            return result;
        }

        //Processing
        try {
            return CreateWorkflowDetail.processingCreateWorkflowDetail(id, workflow, "bcd@gmail.com");
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot create new Workflow");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse createWorkflowActivity(final HttpServletRequest request, String payload) {
        //Check valid token
        InternalResponse response = verifyToken(request, payload);
        if(response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null){
            return response;
        }
        User user_info =(User) response.getObject();
                
        if (Utils.isNullOrEmpty(payload)) {
            LOG.info("No payload found");
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        //Check Workflow_Temlate_Type
        if (!checkTemplateTypeInRequest(payload)) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getMessage(QryptoConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            QryptoConstant.SUBCODE_MISSING_OR_ERROR_TEMPLATE_TYPE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

//        ObjectMapper mapper = new ObjectMapper();
//        QryptoWorkflowJSNObject workflow = new QryptoWorkflowJSNObject();
//        try {
//            workflow = mapper.readValue(payload, QryptoWorkflowJSNObject.class);
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Cannot parse payload");
//            }
//            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
//                    QryptoMessageResponse.getMessage(QryptoConstant.CODE_FAIL,
//                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
//                            QryptoMessageResponse.getLangFromJson(payload),
//                            null));
//        }
//
//        //Check valid data 
//        InternalResponse result = null;
//        result = CreateWorkflow.checkDataWorkflow(workflow);
//        if (result.getStatus() != 200) {
//            return result;
//        }
//
//        //Processing
//        try {
//            return CreateWorkflow.processingCreateWorkflow(workflow);
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Cannot create new Workflow");
//            }
//            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
//        }
return null;
    }
            
    public static InternalResponse verifyToken(final HttpServletRequest request, String payload) {
        ManageToken token = new ManageToken();        
        InternalResponse response = token.verifyAccessToken(request, payload);
        return response;
    }

    //=================== INTERNAL FUNCTION - METHOD============================
    private static boolean checkTemplateTypeInRequest(String payload) {
        payload = payload.replaceAll("\n", "");
        payload = payload.replaceAll("\t", "");
        boolean result = false;
        String[] temp = payload.split(",");
        for (String a : temp) {
            if (a.contains("workflow_template_type")) {
                String[] number = a.split(":");
                String tempp = number[1].trim();
                return tempp.matches("^[0-" + QryptoConstant.NUMBER_WORKFLOW_TEMPLATE_TYPE + "]$");
            }
        }
        return result;
    }

    public static void main(String[] args) {

    }
}
