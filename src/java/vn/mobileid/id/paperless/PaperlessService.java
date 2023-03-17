/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import vn.mobileid.id.eid.object.JWT_Authenticate;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.kernel.CreateWorkflow;
import vn.mobileid.id.paperless.kernel.CreateWorkflowActivity;
import vn.mobileid.id.paperless.kernel.GetAsset;
import vn.mobileid.id.paperless.kernel.GetDocument;
import vn.mobileid.id.paperless.kernel.GetWorkflow;
import vn.mobileid.id.paperless.kernel.GetWorkflowDetail_option;
import vn.mobileid.id.paperless.kernel.GetWorkflowTemplate;
import vn.mobileid.id.paperless.kernel.ManageTokenWithDB;
import vn.mobileid.id.paperless.kernel.process.ProcessEID_JWT;
import vn.mobileid.id.paperless.kernel.ProcessWorkflowActivity;
import vn.mobileid.id.paperless.kernel.UploadAsset;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.KYC;
import vn.mobileid.id.paperless.objects.ListWorkflow;
import vn.mobileid.id.paperless.objects.ProcessWorkflowActivity_JSNObject;
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.Workflow;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.general.email.SendMail;
import vn.mobileid.id.utils.Utils;
import vn.mobileid.id.utils.XSLT_PDF_Processing;

/**
 * Using for check valid of data!!
 *
 * @author GiaTK
 */
public class PaperlessService {

//    final private static Logger LOG = LogManager.getLogger(PaperlessService.class);
    final private static Properties appInfo = Configuration.getInstance().getAppInfo();

    public static InternalResponse getToken(
            final HttpServletRequest request,
            String payload,
            int functionId,
            String transactionID) {
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
            InternalResponse response = ManageTokenWithDB.processLogin(
                    request,
                    payload,
                    transactionID);
            return response;
        } else {
            InternalResponse response = ManageTokenWithDB.processLoginURLEncode(
                    request,
                    Utils.getDataFromURLEncode(payload),
                    transactionID);
            return response;
        }
    }

    public static InternalResponse revoke(
            final HttpServletRequest request,
            String payload,
            String transactionID) {
//        if (functionId == 0) {
//            ManageTokenWithIAM token = new ManageTokenWithIAM();
//            InternalResponse response = token.processJSON_revoke(request, payload);
//            return response;
//        } else {
//            ManageTokenWithIAM token = new ManageTokenWithIAM();
//            InternalResponse response = token.processFORM_revoke(request, payload);
//            return response;
//        }
        return ManageTokenWithDB.processRevoke(request, payload, transactionID);
    }

    public static InternalResponse createWorkflow(
            final HttpServletRequest request,
            String payload,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        if (Utils.isNullOrEmpty(payload)) {
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(PaperlessService.class, transactionID, "No Payload found!!");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        //Check Workflow_Temlate_Type
        if (!checkTemplateTypeInRequest(payload)) {
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(PaperlessService.class, transactionID, "Check Template False!!");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            PaperlessConstant.SUBCODE_MISSING_OR_ERROR_TEMPLATE_TYPE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        ObjectMapper mapper = new ObjectMapper();
        Workflow workflow = new Workflow();
        try {
            workflow = mapper.readValue(payload, Workflow.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot parse payload");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
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
            return CreateWorkflow.processingCreateWorkflow(
                    workflow,
                    user_info,
                    transactionID);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot create new Workflow");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse createWorkflowTemplate(
            final HttpServletRequest request,
            String payload,
            int id,
            String transactionID) {
//        //Check valid token
//        InternalResponse response = verifyToken(request);
//        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
//            return response;
//        }
//
//        if (Utils.isNullOrEmpty(payload)) {
//            if (LogHandler.isShowDebugLog()) {
//                LogHandler.debug(PaperlessService.class, transactionID, "No Payload found!!");
//            }
//            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
//                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
//                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
//                            "en",
//                            null));
//        }
//
//        payload = payload.replaceAll("\n", "");
//        payload = payload.replaceAll("\t", "");
//        payload = payload.replaceAll("[ ]{2,10}", "");
//        ObjectMapper mapper = new ObjectMapper();
//
//        Item_JSNObject data = new Item_JSNObject();
//        try {
//            data = mapper.readValue(payload, Item_JSNObject.class);
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LogHandler.error(PaperlessService.class,transactionID,"Cannot parse payload");
//            }
//            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
//                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
//                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
//                            QryptoMessageResponse.getLangFromJson(payload),
//                            null));
//        }
//
//        //Check valid data 
//        InternalResponse result = null;
//        result = CreateWorkflowTemplate.checkDataWorkflowTemplate(data);
//        if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//            return result;
//        }
//
//        //Processing
//        try {
//            return CreateWorkflowTemplate.processingCreateWorkflowTemplate(
//                    id,
//                    data,
//                    data.get);
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Cannot create new Workflow");
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
        return new InternalResponse(500, "NOT SUPPORT YET!!");
    }

    public static InternalResponse createWorkflowActivity(
            final HttpServletRequest request,
            String payload,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        if (Utils.isNullOrEmpty(payload)) {
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(PaperlessService.class, transactionID, "No Payload found!!");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

//        //Check Workflow_Temlate_Type
//        if (!checkTemplateTypeInRequest(payload)) {
//            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
//                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOW,
//                            PaperlessConstant.SUBCODE_MISSING_OR_ERROR_TEMPLATE_TYPE,
//                            QryptoMessageResponse.getLangFromJson(payload),
//                            null));
//        }
        ObjectMapper mapper = new ObjectMapper();
        WorkflowActivity workflow = new WorkflowActivity();
        try {
            workflow = mapper.readValue(payload, WorkflowActivity.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot parse payload");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check valid data 
        InternalResponse result = null;
        result = CreateWorkflowActivity.checkDataWorkflowActivity(workflow);
        if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return result;
        }

        //Processing
        try {
            return CreateWorkflowActivity.processingCreateWorkflowActivity(
                    workflow,
                    user_info,
                    transactionID);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot create new Workflow Activity");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse verifyToken(
            final HttpServletRequest request,
            String transactionID) {
        InternalResponse response = ManageTokenWithDB.processVerify(
                request,
                transactionID,
                false);
        return response;
    }

    //Get PDF to authenticate from User
    public static InternalResponse assignDataIntoWorkflowActivity(
            final HttpServletRequest request,
            String payload,
            int id,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Check Data
        if (Utils.isNullOrEmpty(payload)) {
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(PaperlessService.class, transactionID, "No Payload found!!");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
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
                LogHandler.error(PaperlessService.class, transactionID, "Cannot parse payload");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check valid data 
        InternalResponse result = null;
        result = ProcessWorkflowActivity.checkItem(data);
        if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return result;
        }

        //Get Data header
        String x_file_name = Utils.getRequestHeader(request, "x-file-name");
        String headerJWT = Utils.getRequestHeader(request, "eid-jwt");
        JWT_Authenticate JWT = new JWT_Authenticate();
        JWT.setMath_result(false);
        if (headerJWT != null) {
            JWT = (JWT_Authenticate) ProcessEID_JWT.getInfoJWT(headerJWT).getData();
        }
        //Processing
        try {
            return ProcessWorkflowActivity.process(
                    id,
                    x_file_name,
                    JWT,
                    user_info,
                    data,
                    true,
                    transactionID);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot process a new Workflow Activity");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    //Processing data activity with Authenticate
    public static InternalResponse processWorkflowActivityWithAuthen(
            final HttpServletRequest request,
            String payload,
            int id,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Get Header
        HashMap<String, String> headers = Utils.getHashMapRequestHeader(request);
        String email = headers.get("x-send-mail");
        String headerJWT = headers.get("eid-jwt");
        JWT_Authenticate jwt = new JWT_Authenticate();
        if (headerJWT != null) {
            InternalResponse data = ProcessEID_JWT.getInfoJWT(headerJWT);
            if (data.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return data;
            }
            jwt = (JWT_Authenticate) data.getData();
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
            if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return result;
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot parse payload");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Processing
        try {
            InternalResponse res = ProcessWorkflowActivity.processAuthen(
                    user_info,
                    id,
                    jwt,
                    data,
                    headers,
                    transactionID);
            if (res.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                //Send mail
                sendMail(
                        (WorkflowActivity) res.getData(),
                        user_info,
                        email,
                        jwt,
                        transactionID);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot process a new Workflow Activity");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    //Processing data activity
    public static InternalResponse processWorkflowActivity(
            final HttpServletRequest request,
            String payload,
            int id) {
        //Check valid token
//        InternalResponse response = verifyToken(request);
//        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
//            return response;
//        }
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Check Token Successfully");
//        }
//
//        User user_info = response.getUser();
//
//        //Check Data
//        if (Utils.isNullOrEmpty(payload)) {
//            LOG.info("No payload found");
//            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
//                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
//                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
//                            "en",
//                            null));
//        }
//
//        //Mapper Object
//        ObjectMapper mapper = new ObjectMapper();
//        ProcessWorkflowActivity_JSNObject data = new ProcessWorkflowActivity_JSNObject();
//        try {
//            data = mapper.readValue(payload, ProcessWorkflowActivity_JSNObject.class);
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Cannot parse payload");
//            }
//            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
//                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
//                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
//                            QryptoMessageResponse.getLangFromJson(payload),
//                            null));
//        }
//
//        //Check valid data 
//        InternalResponse result = null;
//        result = ProcessWorkflowActivity.checkItem(data);
//        if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//            return result;
//        }
//        result = ProcessWorkflowActivity.checkFile_Data(data);
//        if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//            return result;
//        }
//
//        //Get Data header
//        String x_file_name = Utils.getRequestHeader(request, "x-file-name");
//        String jwt = Utils.getRequestHeader(request, "eid-jwt");
//        String email = Utils.getRequestHeader(request, "x-send-mail");
//        JWT_Authenticate object = new JWT_Authenticate();
//        object.setMath_result(false);
//        if (jwt != null) {
//            object = (JWT_Authenticate) ProcessEID_JWT.getInfoJWT(jwt).getData();
//        }
//        if (email == null) {
//            email = user_info.getEmail();
//        }
//        //Processing
//        try {
//            result = ProcessWorkflowActivity.process(id, x_file_name, object, user_info, data, false);
//            if (result.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
//                //Send mail
//                WorkflowActivity woAc = (WorkflowActivity) result.getData();
//                InternalResponse temp = GetDocument.getDocument(woAc.getId());
//                if (temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//                    if (LogHandler.isShowErrorLog()) {
//                        LOG.error("Processing is Successfull but cannot get Document!!");
//                    }
//                    return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//                }
//                SendMail mail = new SendMail(
//                        email,
//                        "eLaborContract - @Name - @CCCD",
//                        "",
//                        "ELaborContract",
//                        ((FileManagement) temp.getData()).getData(),
//                        ((FileManagement) temp.getData()).getName());
//
////                if (jwt != null && jwt.getName() != null && !jwt.getName().equals("")) {
////                    mail.createMessage(jwt.getName());
////                } else {
////                    mail.createMessage(user_info.getName());
////                }
//                mail.createMessage(user_info.getName());
//                mail.send();
//            }
//            return result;
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Cannot process a new Workflow Activity");
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }

        return new InternalResponse(500, "NOT SUPPORT YET");
    }

    public static InternalResponse downloadsDocument(
            final HttpServletRequest request,
            int id,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        try {
            return GetDocument.getDocument(id, transactionID);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot download a Document of Workflow Activity");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse getWorkflowDetail(
            final HttpServletRequest request,
            int id,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        try {
            response = GetWorkflowDetail_option.getWorkflowDetail(id, transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            return new InternalResponse(response.getStatus(), new ObjectMapper().writeValueAsString(response.getData()));
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot getWorkflowDetail");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse downloadsAsset(
            final HttpServletRequest request,
            int id,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Get Type Download
        String value = Utils.getRequestHeader(request, "x-type");

        //Processing
        try {
            response = GetAsset.getAsset(id, transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            Asset asset = (Asset) response.getData();
            KYC temp = new KYC();
            if (value == null || value.equalsIgnoreCase("PDF")) {
                byte[] xslt = XSLT_PDF_Processing.appendData(temp, asset.getBinaryData());
                byte[] data = XSLT_PDF_Processing.convertHTMLtoPDF(xslt);
                response.setStatus(PaperlessConstant.HTTP_CODE_SUCCESS);
                response.setData(data);
                return response;
            }
            if (value.equalsIgnoreCase("XSLT")) {
                response.setStatus(PaperlessConstant.HTTP_CODE_SUCCESS);
                response.setData(asset.getBinaryData());
                return response;
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN, "NOT PROVIDED");

        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot downloadsAsset");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse getWorkflowTemplateType(
            final HttpServletRequest request,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
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
            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, node);

        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot getWorkflowTemplateType");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    //Upload Asset
    public static InternalResponse uploadAsset(
            final HttpServletRequest request,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
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
                LogHandler.error(PaperlessService.class, transactionID, "Cannot upload an Asset");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }

        //Processing
        try {
            int i = convertStringIntoAssetType(x_file_type);
            if (i == -1) {
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(PaperlessService.class, transactionID, "File Type Asset is invalid");
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST, PaperlessConstant.SUBCODE_INVALID_FILE_TYPE);
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

            return UploadAsset.uploadAsset(user_info, asset, transactionID);
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot upload an Asset");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }

    }

    public static InternalResponse getAssetTemplate(
            final HttpServletRequest request,
            int id,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        try {
            response = GetAsset.getAssetTemplate(id, transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, response.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot getAssetTemplate");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse getListWorkflow(
            final HttpServletRequest request,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Process header        
        String URI = request.getRequestURI();
        URI = URI.replaceFirst(".*workflow/", "");
        String[] data = URI.split("/");
        String status = data[0];
        int page_no;
        page_no = (data[1] == null ? 0 : Integer.parseInt(data[1]));
        int record;
        record = (data[2] == null ? 0 : Integer.parseInt(data[2]));

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
                    0,
                    transactionID); //rowcount            
            List<Workflow> list = (List<Workflow>) res.getData();
            ListWorkflow listW = new ListWorkflow();
            listW.setListWorkflow(list);
            res.setData(listW);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot get list Workflow" + e);
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse getWorkflowTemplate(
            final HttpServletRequest request,
            int id,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        try {
            response = GetWorkflowTemplate.getWorkflowTemplate(id, transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, response.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot get Workflow Template");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    //GET WORKFLOW
    public static InternalResponse getWorkflow(
            final HttpServletRequest request,
            int id,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        User user_info = response.getUser();

        //Processing
        try {
            response = GetWorkflow.getWorkflow(id, transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            return new InternalResponse(response.getStatus(), new ObjectMapper().writeValueAsString(response.getData()));
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot get a Workflow - Detail:" + e);
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    //UPDATE WORKFLOW DETAIL/OPTION
    public static InternalResponse updateWorkflowDetail_option(
            final HttpServletRequest request,
            int id,
            String payload,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Get data 
        if (Utils.isNullOrEmpty(payload)) {
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(PaperlessService.class, transactionID, "No Payload found!!");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        ObjectMapper mapper = new ObjectMapper();
        Workflow workflow = new Workflow();
        try {
            workflow = mapper.readValue(payload, Workflow.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot parse payload");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Processing
        try {
//            response = UpdateWorkflowDetail_option.updateWorkflowOption(
//                    id,
//                    detail,
//                    "hmac", user_info.getEmail());
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            return new InternalResponse(response.getStatus(), new ObjectMapper().writeValueAsString(response.getData()));
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot updateWorkflowDetail_option");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
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
                LogHandler.request(PaperlessService.class, "WorkflowTemplateType:" + tempp);
                return tempp.matches("^[0-" + PaperlessConstant.NUMBER_WORKFLOW_TEMPLATE_TYPE + "]$");
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

    /**
     * Hàm gửi mail (sử dụng để tạo thread tối ưu hóa luồng hoạt động)
     */
    private static void sendMail(
            WorkflowActivity woAc,
            User user_info,
            String email,
            JWT_Authenticate jwt,
            String transactionID) {
        Thread temp = new Thread() {
            @Override
            public void run() {
                //Get data from RAM first . If not existed => get from DB
                FileManagement filemanagement = Resources.getListWorkflowActivity().get(String.valueOf(woAc.getId())).getFile();
                if (!filemanagement.isIsSigned()) {
                    InternalResponse temp = GetDocument.getDocument(
                            woAc.getId(),
                            transactionID);

                    if (temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                        LogHandler.error(PaperlessService.class, transactionID, "Processing successfully but can't send mail");
                        return;
                    }
                    filemanagement = (FileManagement) temp.getData();
                }
                String name = "";
                String CCCD = "";
                if (jwt
                        != null && jwt.getName()
                        != null && !jwt.getName().equals("")) {
                    name = jwt.getName();
                } else {
                    name = user_info.getName();
                }
                if (jwt
                        != null) {
                    CCCD = jwt.getDocument_number();
                } else {
                    CCCD = "NonCheckJWT";
                }

                LogHandler.request(PaperlessService.class,
                        "SendToMail:" + email);
//                SendMail mail = new SendMail(
//                        email,
//                        name,
//                        CCCD,
//                        filemanagement.getData(),
//                        filemanagement.getName());
                SendMail mail = new SendMail(
                        email,
                        user_info.getAid(),
                        name,
                        CCCD,
                        filemanagement.getData(),
                        filemanagement.getName()
                );
                mail.send();
            }
        };
        temp.run();
    }
}
