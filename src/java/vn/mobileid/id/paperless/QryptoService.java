/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.logging.Level;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyCharacterCombination;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.OutputSink;
import vn.mobileid.id.eid.object.JWT_Authenticate;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.email.Email;
import vn.mobileid.id.general.email.EmailReq;
import vn.mobileid.id.general.keycloak.KeyCloakInvocation;
import vn.mobileid.id.general.keycloak.obj.KeycloakRes;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.Attachment;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.paperless.kernel.CreateWorkflow;
import vn.mobileid.id.paperless.kernel.CreateWorkflowActivity;
import vn.mobileid.id.paperless.kernel.CreateWorkflowTemplate;
import vn.mobileid.id.paperless.kernel.GetAsset;
import vn.mobileid.id.paperless.kernel.GetDocument;
import vn.mobileid.id.paperless.kernel.GetWorkflow;
import vn.mobileid.id.paperless.kernel.GetWorkflowDetail_option;
import vn.mobileid.id.paperless.kernel.GetWorkflowTemplate;
import vn.mobileid.id.paperless.kernel.ManageTokenWithDB;
import vn.mobileid.id.paperless.kernel.ManageTokenWithIAM;
import vn.mobileid.id.paperless.kernel.process.ProcessEID_JWT;
import vn.mobileid.id.paperless.kernel.ProcessWorkflowActivity;
import vn.mobileid.id.paperless.kernel.UpdateWorkflowDetail_option;
import vn.mobileid.id.paperless.kernel.UploadAsset;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.Item_JSNObject;
import vn.mobileid.id.paperless.objects.KYC;
import vn.mobileid.id.paperless.objects.ListWorkflow;
import vn.mobileid.id.paperless.objects.ProcessWorkflowActivity_JSNObject;
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;
import vn.mobileid.id.paperless.objects.SigningProperties;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.Workflow;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.utils.SendMail;
import vn.mobileid.id.utils.Utils;
import vn.mobileid.id.utils.XSLT_PDF_Processing;

/**
 * Using for check valid of data!!
 *
 * @author GiaTK
 */
public class QryptoService {

    final private static Logger LOG = LogManager.getLogger(QryptoService.class);

    final private static Properties appInfo = Configuration.getInstance().getAppInfo();

    public static InternalResponse getToken(final HttpServletRequest request, String payload, int functionId) {
//        if (functionId == 0) {
//            ManageTokenWithIAM token = new ManageTokenWithIAM();
//            InternalResponse response = token.processJSON_getToken(request, payload);
//            return response;
//        } else {
//            ManageTokenWithIAM token = new ManageTokenWithIAM();
//            InternalResponse response = token.processFORM_getToken(request, payload);
//            return response;
//        }
        if (functionId == 0) {
            InternalResponse response = ManageTokenWithDB.processLogin(request, payload);
            return response;
        } else {
            InternalResponse response = ManageTokenWithDB.processLoginURLEncode(request, Utils.getDataFromURLEncode(payload));
            return response;
        }
    }

    public static InternalResponse revoke(final HttpServletRequest request, String payload) {
//        if (functionId == 0) {
//            ManageTokenWithIAM token = new ManageTokenWithIAM();
//            InternalResponse response = token.processJSON_revoke(request, payload);
//            return response;
//        } else {
//            ManageTokenWithIAM token = new ManageTokenWithIAM();
//            InternalResponse response = token.processFORM_revoke(request, payload);
//            return response;
//        }
        return ManageTokenWithDB.processRevoke(request, payload);
    }

    public static InternalResponse createWorkflow(final HttpServletRequest request, String payload) {
        //Check valid token
        InternalResponse response = verifyToken(request);
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        if (Utils.isNullOrEmpty(payload)) {
            LOG.info("No payload found");
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        //Check Workflow_Temlate_Type
        if (!checkTemplateTypeInRequest(payload)) {
            LOG.info("\tCheck Template Type false");
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            QryptoConstant.SUBCODE_MISSING_OR_ERROR_TEMPLATE_TYPE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        ObjectMapper mapper = new ObjectMapper();
        Workflow workflow = new Workflow();
        try {
            workflow = mapper.readValue(payload, Workflow.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse payload");
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
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
            return CreateWorkflow.processingCreateWorkflow(workflow, user_info);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot create new Workflow");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse createWorkflowTemplate(final HttpServletRequest request, String payload, int id) {
        //Check valid token
        InternalResponse response = verifyToken(request);
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        if (Utils.isNullOrEmpty(payload)) {
            LOG.info("No payload found");
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        payload = payload.replaceAll("\n", "");
        payload = payload.replaceAll("\t", "");
        payload = payload.replaceAll("[ ]{2,10}", "");
        ObjectMapper mapper = new ObjectMapper();

        Item_JSNObject data = new Item_JSNObject();
        try {
            data = mapper.readValue(payload, Item_JSNObject.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse payload");
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check valid data 
        InternalResponse result = null;
        result = CreateWorkflowTemplate.checkDataWorkflowTemplate(data);
        if (result.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
            return result;
        }

        //Processing
        try {
            return CreateWorkflowTemplate.processingCreateWorkflowTemplate(id, data, "bcd@gmail.com");
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot create new Workflow");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse createWorkflowActivity(final HttpServletRequest request, String payload) {
        //Check valid token
        InternalResponse response = verifyToken(request);
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        if (Utils.isNullOrEmpty(payload)) {
            LOG.info("No payload found");
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

//        //Check Workflow_Temlate_Type
//        if (!checkTemplateTypeInRequest(payload)) {
//            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
//                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_WORKFLOW,
//                            QryptoConstant.SUBCODE_MISSING_OR_ERROR_TEMPLATE_TYPE,
//                            QryptoMessageResponse.getLangFromJson(payload),
//                            null));
//        }
        ObjectMapper mapper = new ObjectMapper();
        WorkflowActivity workflow = new WorkflowActivity();
        try {
            workflow = mapper.readValue(payload, WorkflowActivity.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse payload");
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check valid data 
        InternalResponse result = null;
        result = CreateWorkflowActivity.checkDataWorkflowActivity(workflow);
        if (result.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
            return result;
        }

        //Processing
        try {
            return CreateWorkflowActivity.processingCreateWorkflowActivity(workflow, user_info);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot create new Workflow Activity");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse verifyToken(final HttpServletRequest request) {
        InternalResponse response = ManageTokenWithDB.processVerify(request);
        return response;
    }

    //Get PDF to authenticate from User
    public static InternalResponse assignDataIntoWorkflowActivity(final HttpServletRequest request, String payload, int id) {
        //Check valid token
        InternalResponse response = verifyToken(request);
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Check Token Successfully");
        }

        User user_info = response.getUser();

        //Check Data
        if (Utils.isNullOrEmpty(payload)) {
            LOG.info("No payload found");
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        //Mapper Object
        ObjectMapper mapper = new ObjectMapper();
        ProcessWorkflowActivity_JSNObject data = new ProcessWorkflowActivity_JSNObject();
        try {
            data = mapper.readValue(payload, ProcessWorkflowActivity_JSNObject.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse payload");
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check valid data 
        InternalResponse result = null;
        result = ProcessWorkflowActivity.checkItem(data);
        if (result.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
            return result;
        }

        //Get Data header
        String x_file_name = Utils.getRequestHeader(request, "x-file-name");
        String jwt = Utils.getRequestHeader(request, "eid-jwt");
        JWT_Authenticate object = new JWT_Authenticate();
        object.setMath_result(false);
        if (jwt != null) {
            object = (JWT_Authenticate) ProcessEID_JWT.getInfoJWT(jwt).getData();
        }
        //Processing
        try {
            return ProcessWorkflowActivity.process(id, x_file_name, object, user_info, data, true);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot process a new Workflow Activity");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    //Processing data activity with Authenticate
    public static InternalResponse processWorkflowActivityWithAuthen(final HttpServletRequest request, String payload, int id) {
        //Check valid token
        InternalResponse response = verifyToken(request);
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Check Token Successfully");
        }

        User user_info = response.getUser();

        //Get Header
        HashMap<String, String> headers = Utils.getHashMapRequestHeader(request);
        String email = headers.get("x-send-mail");
        String jwt = headers.get("eid-jwt");
        JWT_Authenticate jwts = new JWT_Authenticate();
        if (jwt != null) {
            InternalResponse data = ProcessEID_JWT.getInfoJWT(jwt);
            if (data.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
                return data;
            }
            jwts = (JWT_Authenticate) data.getData();
        }
        if (email == null) {
            email = user_info.getEmail();
        }

        //Mapper Object
        ObjectMapper mapper = new ObjectMapper();
        ProcessWorkflowActivity_JSNObject data = new ProcessWorkflowActivity_JSNObject();
        try {
            if (!Utils.isNullOrEmpty(payload)) {
                data = mapper.readValue(payload, ProcessWorkflowActivity_JSNObject.class);
            }
            //Check valid data 
            InternalResponse result = null;
            result = ProcessWorkflowActivity.checkFile_Data(data);
            if (result.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
                return result;
            }            
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse payload");
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }
        
        //Processing
        try {
            InternalResponse res = ProcessWorkflowActivity.processAuthen(
                    user_info,
                    id,
                    jwts,
                    data,
                    headers);
            if (res.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                //Send mail
                WorkflowActivity woAc = (WorkflowActivity) res.getData();
                InternalResponse temp = GetDocument.getDocument(woAc.getId());
                if (temp.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
                    if (LogHandler.isShowErrorLog()) {
                        LOG.error("Processing is Successfull but cannot get Document!!");
                    }
                    return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
                }
                String name = "";
                String CCCD = "";
                if (jwts != null && jwts.getName() != null && !jwts.getName().equals("")) {
                    name = jwts.getName();
                } else {
                    name = user_info.getName();
                }
                if (jwts != null){
                    CCCD = jwts.getDocument_number();
                } else {
                    CCCD = "NonCheckJWT";
                }             
                LOG.info("SendToMail:"+email);
                SendMail mail = new SendMail(
                        email,
                        name,
                        CCCD,
                        ((FileManagement) temp.getData()).getData(),
                        ((FileManagement) temp.getData()).getName());                
                mail.start();
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot process a new Workflow Activity");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    //Processing data activity
    public static InternalResponse processWorkflowActivity(final HttpServletRequest request, String payload, int id) {
        //Check valid token
        InternalResponse response = verifyToken(request);
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Check Token Successfully");
        }

        User user_info = response.getUser();

        //Check Data
        if (Utils.isNullOrEmpty(payload)) {
            LOG.info("No payload found");
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        //Mapper Object
        ObjectMapper mapper = new ObjectMapper();
        ProcessWorkflowActivity_JSNObject data = new ProcessWorkflowActivity_JSNObject();
        try {
            data = mapper.readValue(payload, ProcessWorkflowActivity_JSNObject.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse payload");
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check valid data 
        InternalResponse result = null;
        result = ProcessWorkflowActivity.checkItem(data);
        if (result.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
            return result;
        }
        result = ProcessWorkflowActivity.checkFile_Data(data);
        if (result.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
            return result;
        }

        //Get Data header
        String x_file_name = Utils.getRequestHeader(request, "x-file-name");
        String jwt = Utils.getRequestHeader(request, "eid-jwt");
        String email = Utils.getRequestHeader(request, "x-send-mail");
        JWT_Authenticate object = new JWT_Authenticate();
        object.setMath_result(false);
        if (jwt != null) {
            object = (JWT_Authenticate) ProcessEID_JWT.getInfoJWT(jwt).getData();
        }
        if (email == null) {
            email = user_info.getEmail();
        }
        //Processing
        try {
            result = ProcessWorkflowActivity.process(id, x_file_name, object, user_info, data, false);
            if (result.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                //Send mail
                WorkflowActivity woAc = (WorkflowActivity) result.getData();
                InternalResponse temp = GetDocument.getDocument(woAc.getId());
                if (temp.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
                    if (LogHandler.isShowErrorLog()) {
                        LOG.error("Processing is Successfull but cannot get Document!!");
                    }
                    return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
                }
                SendMail mail = new SendMail(
                        email,
                        "eLaborContract - @Name - @CCCD",
                        "",
                        "ELaborContract",
                        ((FileManagement) temp.getData()).getData(),
                        ((FileManagement) temp.getData()).getName());

//                if (jwts != null && jwts.getName() != null && !jwts.getName().equals("")) {
//                    mail.createMessage(jwts.getName());
//                } else {
//                    mail.createMessage(user_info.getName());
//                }
                mail.createMessage(user_info.getName());
                mail.send();
            }
            return result;
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot process a new Workflow Activity");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse downloadsDocument(final HttpServletRequest request, int id) {
        //Check valid token
        InternalResponse response = verifyToken(request);
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Check Token Successfully");
        }

        User user_info = response.getUser();

        //Processing
        try {
            return GetDocument.getDocument(id);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot download a Document of Workflow Activity");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse getWorkflowDetail(final HttpServletRequest request, int id) {
        //Check valid token
        InternalResponse response = verifyToken(request);
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Check Token Successfully");
        }

        User user_info = response.getUser();

        //Processing
        try {
            response = GetWorkflowDetail_option.getWorkflowDetail(id);
            if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            return new InternalResponse(response.getStatus(), new ObjectMapper().writeValueAsString(response.getData()));
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot process a new Workflow Activity");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse downloadsAsset(final HttpServletRequest request, int id) {
        //Check valid token
        InternalResponse response = verifyToken(request);
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Check Token Successfully");
        }

        User user_info = response.getUser();

        //Get Type Download
        String value = Utils.getRequestHeader(request, "x-type");

        //Processing
        try {
            response = GetAsset.getAsset(id);
            if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            Asset asset = (Asset) response.getData();
            KYC temp = new KYC();
            if (value == null || value.equalsIgnoreCase("PDF")) {
                byte[] xslt = XSLT_PDF_Processing.appendData(temp, asset.getBinaryData());
                byte[] data = XSLT_PDF_Processing.convertHTMLtoPDF(xslt);
                response.setStatus(QryptoConstant.HTTP_CODE_SUCCESS);
                response.setData(data);
                return response;
            }
            if (value.equalsIgnoreCase("XSLT")) {
                response.setStatus(QryptoConstant.HTTP_CODE_SUCCESS);
                response.setData(asset.getBinaryData());
                return response;
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN, "NOT PROVIDED");

        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot get an Asset");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse getWorkflowTemplateType(final HttpServletRequest request) {
        //Check valid token
        InternalResponse response = verifyToken(request);
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Check Token Successfully");
        }

        User user_info = response.getUser();

        //Processing
        try {
            HashMap<Integer, String> hashmap = Resources.getListWorkflowTemplateTypeName();
            if (hashmap == null || hashmap.isEmpty()) {
                Resources.reloadListWorkflowTemplateTypeName();
                hashmap = Resources.getListWorkflowTemplateTypeName();
            }
            ObjectNode node = new ObjectMapper().createObjectNode();
            hashmap.forEach((key, value) -> {
                node.put(String.valueOf(key), value);
            });
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS, node);

        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot get an Asset");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    //Upload Asset
    public static InternalResponse uploadAsset(final HttpServletRequest request) {
        //Check valid token
        InternalResponse response = verifyToken(request);
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Check Token Successfully");
        }

        User user_info = response.getUser();

        //Get header         
        String x_file_type = Utils.getRequestHeader(request, "x-file-type");
        String x_file_name = Utils.getRequestHeader(request, "x-file-name");
        ByteArrayOutputStream outputStream;
        try {
            final ServletInputStream input = request.getInputStream();
            int len = request.getContentLength();
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n1;
            while (-1 != (n1 = input.read(buffer))) {
                outputStream.write(buffer, 0, n1);
            }
            input.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot upload an Asset");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }

        //Processing
        try {
            int i = convertStringIntoAssetType(x_file_type);
            if (i == -1) {
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("File Type Asset is invalid");
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST, QryptoConstant.SUBCODE_INVALID_FILE_TYPE);
            }
            Asset asset = new Asset(
                    0,
                    x_file_name,
                    i,
                    0,
                    "",
                    null,
                    "",
                    null,
                    "",
                    "",
                    outputStream.toByteArray(),
                    null
            );

            return UploadAsset.uploadAsset(user_info, asset);
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot upload an Asset");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }

    }

    public static InternalResponse getAssetTemplate(final HttpServletRequest request, int id) {
        //Check valid token
        InternalResponse response = verifyToken(request);
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Check Token Successfully");
        }

        User user_info = response.getUser();

        //Processing
        try {
            response = GetAsset.getAssetTemplate(id);
            if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS, response.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot get an Asset");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse getListWorkflow(final HttpServletRequest request) {
        //Check valid token
        InternalResponse response = verifyToken(request);
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Check Token Successfully");
        }

        User user_info = response.getUser();

        //Process header        
        String URI = request.getRequestURI();
//        System.out.println("URI:" + URI);
        URI = URI.replaceFirst(".*workflow/", "");
//        System.out.println("URI:" + URI);
        String[] data = URI.split("/");
        String status = data[0];
        int page_no;
        page_no = (data[1] == null ? 0 : Integer.parseInt(data[1]));
        int record;
        record = (data[2] == null ? 0 : Integer.parseInt(data[2]));

//        System.out.println("STATUS:" + status);
//        System.out.println("PAGE:" + page_no);
//        System.out.println("record:" + record);
        //Processing
        try {
            InternalResponse res = GetWorkflow.getListWorkflow(
                    user_info.getEmail(),
                    3, //enterprise_id
                    status, //status
                    "1,2", //workflow_type
                    false, //enable metadata
                    "", //value of metadata
                    0, //offset
                    0); //rowcount
            ObjectNode node = new ObjectMapper().createObjectNode();
            List<Workflow> list = (List<Workflow>) res.getData();
            ListWorkflow listW = new ListWorkflow();
            listW.setListWorkflow(list);
            res.setData(listW);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot get list Workflow");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse getWorkflowTemplate(final HttpServletRequest request, int id) {
        //Check valid token
        InternalResponse response = verifyToken(request);
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Check Token Successfully");
        }

        User user_info = response.getUser();

        //Processing
        try {
            response = GetWorkflowTemplate.getWorkflowTemplate(id);
            if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS, response.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot get an Workflow Template");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    //GET WORKFLOW
    public static InternalResponse getWorkflow(final HttpServletRequest request, int id) {
        //Check valid token
        InternalResponse response = verifyToken(request);
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Check Token Successfully");
        }

        User user_info = response.getUser();

        //Processing
        try {
            response = GetWorkflow.getWorkflow(id);
            if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
                return response;
            }            
            return new InternalResponse(response.getStatus(), new ObjectMapper().writeValueAsString(response.getData()));
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot get a new Workflow - Detail:" + e);
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    //UPDATE WORKFLOW DETAIL/OPTION
    public static InternalResponse updateWorkflowDetail_option(final HttpServletRequest request, int id, String payload) {
        //Check valid token
        InternalResponse response = verifyToken(request);
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Check Token Successfully");
        }

        User user_info = response.getUser();

        //Get data 
        if (Utils.isNullOrEmpty(payload)) {
            LOG.info("No payload found");
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        ObjectMapper mapper = new ObjectMapper();
        Workflow workflow = new Workflow();
        try {
            workflow = mapper.readValue(payload, Workflow.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse payload");
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Processing
        try {
//            response = UpdateWorkflowDetail_option.updateWorkflowOption(
//                    id,
//                    detail,
//                    "hmac", user_info.getEmail());
            if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            return new InternalResponse(response.getStatus(), new ObjectMapper().writeValueAsString(response.getData()));
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot process a new Workflow Activity");
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    //=================== INTERNAL FUNCTION - METHOD============================
    /**
     * Hàm check workflow_template_type từ chuỗi payload truyền vào
     *
     * @param payload chuối payload truyền vào
     * @return nếu template hợp lệ sẽ trả về true. Ngược lại false
     */
    private static boolean checkTemplateTypeInRequest(String payload) {
        payload = payload.replaceAll("\n", "");
        payload = payload.replaceAll("\t", "");
        payload = payload.replaceAll("}", "");
        payload = payload.replaceAll("\\{", "");
        boolean result = false;
        String[] temp = payload.split(",");
        for (String a : temp) {
            if (a.contains("workflow_template_type")) {
                String[] number = a.split(":");
                String tempp = number[1].trim();
                if (LogHandler.isShowWarnLog()) {
                    LOG.warn("WorkflowTemplateType:" + tempp);
                }
                return tempp.matches("^[0-" + QryptoConstant.NUMBER_WORKFLOW_TEMPLATE_TYPE + "]$");
            }
        }
        return result;
    }

    /**
     * Convert từ chuỗi asset Type đưa về dạng int của asset Type đó
     *
     * @param assetType chuỗi cần đưa về
     * @return trả về số của assetType tương ứng. Nếu không tồn tại sẽ trả về -1
     */
    private static int convertStringIntoAssetType(String assetType) {
        if (Resources.getListAssetType().isEmpty()) {
            Resources.reloadListAssetType();
        }
        HashMap<String, Integer> hashmap = Resources.getListAssetType();
        for (String temp : hashmap.keySet()) {
            if (temp.contains(assetType.toLowerCase()) || temp.contains(assetType.toUpperCase()) || temp.contains(assetType)) {
                return hashmap.get(temp);
            }
        }
        return -1;
    }

}
