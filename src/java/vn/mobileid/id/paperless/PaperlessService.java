/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import vn.mobileid.id.eid.object.JWT_Authenticate;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.kernel.ManageTokenWithDB;
import vn.mobileid.id.paperless.kernel.process.ProcessEID_JWT;
import vn.mobileid.id.paperless.kernel.ProcessWorkflowActivity;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.KYC;
import vn.mobileid.id.paperless.objects.ProcessWorkflowActivity_JSNObject;
import vn.mobileid.id.paperless.objects.ProcessWorkflowActivity_CSV_JSNObject;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.Workflow;
import vn.mobileid.id.general.Configuration;
import vn.mobileid.id.general.email.SendMail;
import vn.mobileid.id.paperless.kernel.CheckWorkflowTemplate;
import vn.mobileid.id.paperless.kernel.ManageStatusWorkflow;
import vn.mobileid.id.paperless.kernel.ProcessTrustManager;
import vn.mobileid.id.paperless.kernel_v2.CreateWorkflow;
import vn.mobileid.id.paperless.kernel_v2.CreateWorkflowActivity;
import vn.mobileid.id.paperless.kernel_v2.GetAsset;
import vn.mobileid.id.paperless.kernel_v2.GetDocument;
import vn.mobileid.id.paperless.kernel_v2.GetUser;
import vn.mobileid.id.paperless.kernel_v2.GetWorkflow;
import vn.mobileid.id.paperless.kernel_v2.GetWorkflowActivity;
import vn.mobileid.id.paperless.kernel_v2.GetWorkflowDetails;
import vn.mobileid.id.paperless.kernel_v2.GetWorkflowTemplate;
import vn.mobileid.id.paperless.kernel_v2.UpdateUser;
import vn.mobileid.id.paperless.kernel_v2.UpdateWorkflow;
import vn.mobileid.id.paperless.kernel_v2.UpdateWorkflowActivity;
import vn.mobileid.id.paperless.kernel_v2.UpdateWorkflowDetail;
import vn.mobileid.id.paperless.kernel_v2.UpdateWorkflowTemplate;
import vn.mobileid.id.paperless.object.enumration.WorkflowActivityStatus;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.Enterprise;
import vn.mobileid.id.paperless.objects.Item_JSNObject;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;
import vn.mobileid.id.paperless.objects.WorkflowTemplate;
import vn.mobileid.id.paperless.objects.WorkflowTemplateType;
import vn.mobileid.id.paperless.serializer.CustomListAssetSerializer;
import vn.mobileid.id.paperless.serializer.CustomListWoAcSerializer;
import vn.mobileid.id.paperless.serializer.CustomListWorkflowSerializer;
import vn.mobileid.id.paperless.serializer.CustomWorkflowDetailsSerializer;
import vn.mobileid.id.paperless.serializer.CustomWorkflowSerializer;
import vn.mobileid.id.paperless.serializer.CustomWorkflowTemplateSerializer;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.paperless.kernel.GetTransaction;
import vn.mobileid.id.paperless.kernel.GetWorkflowTemplateType;
import vn.mobileid.id.paperless.kernel_v2.DeleteAsset;
import vn.mobileid.id.paperless.kernel_v2.GetCSVTask;
import vn.mobileid.id.paperless.kernel_v2.GetEnterpriseInfo;
import vn.mobileid.id.paperless.kernel_v2.GetFileManagement;
import vn.mobileid.id.paperless.kernel_v2.GetQR;
import vn.mobileid.id.paperless.kernel_v2.GetUserActivity;
import vn.mobileid.id.paperless.kernel_v2.GetUserActivityLog;
import vn.mobileid.id.paperless.kernel_v2.UpdateAsset;
import vn.mobileid.id.paperless.kernel_v2.UploadAsset;
import vn.mobileid.id.paperless.objects.CSVTask;
import vn.mobileid.id.paperless.objects.Category;
import vn.mobileid.id.paperless.objects.GenerationType;
import vn.mobileid.id.paperless.objects.QR;

import vn.mobileid.id.paperless.objects.Transaction;
import vn.mobileid.id.paperless.objects.UserActivity;
import vn.mobileid.id.paperless.objects.UserActivityLog;
import vn.mobileid.id.paperless.objects.WorkflowType;
import vn.mobileid.id.paperless.serializer.CustomListUserActivitySerializer;
import vn.mobileid.id.paperless.serializer.CustomUserActivitySerializer;
import vn.mobileid.id.utils.TaskV2;
import vn.mobileid.id.utils.Utils;
import vn.mobileid.id.utils.XSLT_PDF_Processing;

/**
 * Using for check valid of data!!
 *
 * @author GiaTK
 */
public class PaperlessService {

    final private static Properties appInfo = Configuration.getInstance().getAppInfo();

    //<editor-fold defaultstate="collapsed" desc="Get Token">
    public static InternalResponse getToken(
            final HttpServletRequest request,
            String payload,
            int functionId,
            String transactionID) throws Exception {
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Revoke">
    public static InternalResponse revoke(
            final HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        return ManageTokenWithDB.processRevoke(
                request,
                payload,
                transactionID);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create Workflow">
    public static InternalResponse createWorkflow(
            final HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        //Check Workflow_Temlate_Type
        if (!checkTemplateTypeInRequest(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            PaperlessConstant.SUBCODE_MISSING_OR_ERROR_TEMPLATE_TYPE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        ObjectMapper mapper = new ObjectMapper();
        Workflow workflow = new Workflow();
        try {
            workflow = mapper.readValue(payload, Workflow.class);
        } catch (Exception e) {
            LogHandler.error(
                    PaperlessService.class,
                    transactionID,
                    "Cannot parse payload",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check valid data 
        InternalResponse result = null;
        result = CreateWorkflow.checkDataWorkflow(workflow);
        if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return result;
        }

        //Processing
        response = CreateWorkflow.processingCreateWorkflow(
                workflow,
                user_info,
                transactionID);
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    @Deprecated
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
//                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
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
//                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
//                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
//                            PaperlessMessageResponse.getLangFromJson(payload),
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

    //<editor-fold defaultstate="collapsed" desc="Create Workflow Activity">
    public static InternalResponse createWorkflowActivity(
            final HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        ObjectMapper mapper = new ObjectMapper();
        WorkflowActivity workflow = new WorkflowActivity();
        try {
            workflow = mapper.readValue(payload, WorkflowActivity.class);
        } catch (Exception e) {
            LogHandler.error(
                    PaperlessService.class,
                    transactionID,
                    "Cannot parse payload",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check valid data 
        InternalResponse result = null;
        result = CreateWorkflowActivity.checkDataWorkflowActivity(workflow);
        if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return result;
        }

        //Processing
        response = CreateWorkflowActivity.processingCreateWorkflowActivity(
                workflow,
                request.getRemoteAddr(),
                user_info,
                transactionID);
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create Workflow Activity for CSV">
    public static InternalResponse createWorkflowActivity_forCSV(
            final HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        ObjectMapper mapper = new ObjectMapper();
        WorkflowActivity workflow = new WorkflowActivity();
        try {
            workflow = mapper.readValue(payload, WorkflowActivity.class);
            workflow.setCsvEnabled(true);
        } catch (Exception e) {
            LogHandler.error(
                    PaperlessService.class,
                    transactionID,
                    "Cannot parse payload",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check valid data 
        InternalResponse result = null;
        result = CreateWorkflowActivity.checkDataWorkflowActivity(workflow);
        if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return result;
        }

        //Processing
        response = CreateWorkflowActivity.processingCreateWorkflowActivity(
                workflow,
                request.getRemoteAddr(),
                user_info,
                transactionID);
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Verify Token">
    public static InternalResponse verifyToken(
            final HttpServletRequest request,
            String transactionID) throws Exception {
        InternalResponse response = ManageTokenWithDB.processVerify(
                request,
                transactionID,
                false);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Assign Data into Workflow Activity">
    public static InternalResponse assignDataIntoWorkflowActivity(
            final HttpServletRequest request,
            String payload,
            int id,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Check Data
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        //Mapper Object
        ObjectMapper mapper = new ObjectMapper();
        ProcessWorkflowActivity_JSNObject data = null;
        try {
            data = mapper.readValue(payload, ProcessWorkflowActivity_JSNObject.class);
        } catch (Exception e) {
            LogHandler.error(
                    PaperlessService.class,
                    transactionID,
                    "Cannot parse payload",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check valid data 
        InternalResponse result = null;
        result = CheckWorkflowTemplate.checkItem(data);

        if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return result;
        }

        //Get Data header
        String x_file_name = Utils.getRequestHeader(request, "x-file-name");
        String headerJWT = Utils.getRequestHeader(request, "eid-jwt");
        JWT_Authenticate JWT = new JWT_Authenticate();
        if (headerJWT != null) {
            result = ProcessTrustManager.verifyTrustManager(headerJWT, transactionID);
            if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return result;
            }
            JWT = (JWT_Authenticate) ProcessEID_JWT.getInfoJWT(headerJWT).getData();
        }
        //Processing
        response = ProcessWorkflowActivity.assign(
                id,
                x_file_name,
                JWT,
                user_info,
                data,
                true,
                transactionID);
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //Processing data activity with Authenticate
    public static InternalResponse processWorkflowActivityWithAuthen(
            final HttpServletRequest request,
            String payload,
            int id,
            String transactionID) throws Exception {
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
            response = ProcessTrustManager.verifyTrustManager(headerJWT, transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            response = ProcessEID_JWT.getInfoJWT(headerJWT);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            jwt = (JWT_Authenticate) response.getData();
        }

        if (email == null) {
            email = user_info.getEmail();
        }

        //Mapper Object
        ObjectMapper mapper = new ObjectMapper();
        ProcessWorkflowActivity_JSNObject data = null;
        try {
            if (!Utils.isNullOrEmpty(payload)) {
                data = mapper.readValue(payload, ProcessWorkflowActivity_JSNObject.class);
            }
            //Check valid data 
            InternalResponse result = null;
            result = CheckWorkflowTemplate.checkFile_Data(data);
            if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return result;
            }
        } catch (Exception e) {
            LogHandler.error(
                    PaperlessService.class,
                    transactionID,
                    "Cannot parse payload",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Processing
        InternalResponse res = ProcessWorkflowActivity.processAuthen(
                user_info,
                id,
                jwt,
                data,
                headers,
                transactionID);
        if (res.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
            //Send mail
            sendMail(id,
                    user_info,
                    email,
                    jwt,
                    res.getMessage(),
                    transactionID);
        }
        res.setUser(user_info);
        return res;
    }

    //<editor-fold defaultstate="collapsed" desc="Process Workflow Activity">
    public static InternalResponse processWorkflowActivity(
            final HttpServletRequest request,
            String payload,
            int id,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Check Data
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        //Mapper Object
        ObjectMapper mapper = new ObjectMapper();
        ProcessWorkflowActivity_JSNObject data = null;
        try {
            data = mapper.readValue(payload, ProcessWorkflowActivity_JSNObject.class);
        } catch (Exception e) {
            LogHandler.error(
                    PaperlessService.class,
                    transactionID,
                    "Cannot parse payload",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
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
        response = ProcessWorkflowActivity.process(
                id,
                x_file_name,
                JWT,
                user_info,
                data,
                false,
                transactionID);
        if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
            //Send mail
            sendMail(id,
                    user_info,
                    user_info.getEmail(),
                    null,
                    "QR",
                    transactionID);
        }
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Process Workflow Activity for CSV">
    public static InternalResponse processWorkflowActivity_forCSV(
            final HttpServletRequest request,
            String payload,
            int id,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Check Data
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        //Mapper Object
        ObjectMapper mapper = new ObjectMapper();
        ProcessWorkflowActivity_CSV_JSNObject data = new ProcessWorkflowActivity_CSV_JSNObject();
        try {
            data = mapper.readValue(payload, ProcessWorkflowActivity_CSV_JSNObject.class);
        } catch (Exception e) {
            LogHandler.error(
                    PaperlessService.class,
                    transactionID,
                    "Cannot parse payload",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
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
        response = ProcessWorkflowActivity.process_forCSV(
                id,
                x_file_name,
                JWT,
                user_info,
                data,
                false,
                transactionID);
        if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
//            //Send mail
            sendMail(id,
                    user_info,
                    user_info.getEmail(),
                    null,
                    response.getMessage(),
                    transactionID);
        }
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Download Document">
    public static InternalResponse downloadsDocument(
            final HttpServletRequest request,
            int id,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        response = GetDocument.getDocument(id, transactionID);
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Document Details">
    public static InternalResponse getDocumentDetails(
            final HttpServletRequest request,
            int id,
            String transactionID) throws JsonProcessingException, Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        InternalResponse res = GetDocument.getDocumentObject(id, transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        res.setMessage(
                new ObjectMapper().writeValueAsString(
                        res.getData()));
        res.setUser(user_info);
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Workflow Details">
    public static InternalResponse getWorkflowDetail(
            final HttpServletRequest request,
            int id,
            String transactionID) throws JsonProcessingException, Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        response = GetWorkflowDetails.getWorkflowDetail(
                id,
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        List<WorkflowAttributeType> list1 = (List<WorkflowAttributeType>) response.getData();

        //Get Workflow
        response = GetWorkflow.getWorkflow(id, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        Workflow workflow = (Workflow) response.getData();

        //Get WorkflowTemplate Type
        response = GetWorkflowTemplateType.getWorkflowTemplateTypeFromDB(workflow.getWorkflowTemplate_type(), transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        WorkflowTemplateType templateType = (WorkflowTemplateType) response.getData();
        List<WorkflowAttributeType> listFinal = templateType.convertToWorkflowAttributeType(list1);

        CustomWorkflowDetailsSerializer custom = new CustomWorkflowDetailsSerializer(listFinal);

        response = new InternalResponse(
                response.getStatus(),
                new ObjectMapper()
                        .writeValueAsString(custom));
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Workflow Activity Details">
    public static InternalResponse getWorkflowAcDetail(
            final HttpServletRequest request,
            int id,
            String transactionID) throws JsonProcessingException, Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        response = GetWorkflowActivity.getWorkflowActivity(
                id,
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        response = new InternalResponse(
                response.getStatus(),
                new ObjectMapper()
                        .writeValueAsString(response.getData()));
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Download Asset">
    public static InternalResponse downloadsAsset(
            final HttpServletRequest request,
            int id,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Get Type Download
        String value = Utils.getRequestHeader(request, "x-type");

        //Processing
        response = GetAsset.getAsset(
                id,
                transactionID);

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
            response.setUser(user_info);
            return response;
        }
        if (value.equalsIgnoreCase("XSLT")) {
            response.setStatus(PaperlessConstant.HTTP_CODE_SUCCESS);
            response.setData(asset.getBinaryData());
            response.setUser(user_info);
            return response;
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_FORBIDDEN,
                "{NOT PROVIDED}");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Download Asset Base64">
    public static InternalResponse downloadsAssetBase64(
            final HttpServletRequest request,
            int id,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Get Type Download
        String value = Utils.getRequestHeader(request, "x-type");

        //Processing
//        try {
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
            Asset assetTemp = new Asset();
            assetTemp.setBase64(Base64.getEncoder().encodeToString(data));
            response.setData(assetTemp);
            response.setUser(user_info);
            return response;
        }
        if (value.equalsIgnoreCase("XSLT")) {
            response.setStatus(PaperlessConstant.HTTP_CODE_SUCCESS);
            Asset assetTemp = new Asset();
            assetTemp.setBase64(Base64.getEncoder().encodeToString(asset.getBinaryData()));
            response.setData(assetTemp);
            response.setUser(user_info);
            return response;
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_FORBIDDEN,
                "{NOT PROVIDED}");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Workflow Template Type">
    public static InternalResponse getWorkflowTemplateType(
            final HttpServletRequest request,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        HashMap<Integer, WorkflowTemplateType> hashmap = Resources.getListWorkflowTemplateType();
        if (hashmap == null || hashmap.isEmpty()) {
            Resources.reloadListWorkflowTemplateType();
            hashmap = Resources.getListWorkflowTemplateType();
        }
        ArrayNode node = new ObjectMapper().createArrayNode();
        hashmap.forEach((key, value) -> {
            ObjectNode child = new ObjectMapper().createObjectNode();
            child.put("workflow_template_type_id", key);
            child.put("workflow_template_type_name", value.getRemark());
            child.put("workflow_template_type_name_vn", value.getRemark_vn());
            node.add(child);
        });
        response = new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                node);
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Generation Type">
    public static InternalResponse getGenerationType(
            final HttpServletRequest request,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        HashMap<Integer, GenerationType> hashmap = Resources.getListGenerationType();
        if (hashmap == null || hashmap.isEmpty()) {
            Resources.reloadListWorkflowTemplateType();
            hashmap = Resources.getListGenerationType();
        }
        ArrayNode node = new ObjectMapper().createArrayNode();
        hashmap.forEach((key, value) -> {
            ObjectNode child = new ObjectMapper().createObjectNode();
            child.put("generation_type_id", key);
            child.put("generation_type_name", value.getRemark());
            child.put("generation_type_name_vn", value.getRemark_vn());
            node.add(child);
        });
        response = new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                node);
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Workflow Type">
    public static InternalResponse getWorkflowType(
            final HttpServletRequest request,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        HashMap<Integer, WorkflowType> hashmap = Resources.getListWorkflowType();
        if (hashmap == null || hashmap.isEmpty()) {
            Resources.reloadListWorkflowTemplateType();
            hashmap = Resources.getListWorkflowType();
        }
        ArrayNode node = new ObjectMapper().createArrayNode();
        hashmap.forEach((key, value) -> {
            ObjectNode child = new ObjectMapper().createObjectNode();
            child.put("workflow_type_id", key);
            child.put("workflow_type_name", value.getRemark());
            child.put("workflow_type_name_vn", value.getRemark_vn());
            node.add(child);
        });
        response = new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                node);
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Upload Asset">
    public static InternalResponse uploadAsset(
            final HttpServletRequest request,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Get header         
        String x_file_type = Utils.getRequestHeader(request, "x-file-type");
        String x_file_name = Utils.getRequestHeader(request, "x-file-name");
        ByteArrayOutputStream outputStream = null;
        try {
            final ServletInputStream input = request.getInputStream();
//            int len = request.getContentLength();
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
            LogHandler.error(
                    PaperlessService.class,
                    transactionID,
                    "Cannot upload an Asset !",
                    ex);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    "{INVALID FILE}");
        }
        //Check null
        if (outputStream == null || outputStream.toByteArray().length <= 0) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_ASSET,
                            PaperlessConstant.SUBCODE_MISSING_FILE_DATA,
                            "en",
                            null));
        }

        //Processing        
        int i = convertStringIntoAssetType(x_file_type, x_file_name);
        if (i == -1) {
            String message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_INVALID_PARAMS_ASSET,
                    PaperlessConstant.SUBCODE_INVALID_FILE_TYPE,
                    "en",
                    null);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    message);
        }

        Asset asset = new Asset(
                0,
                x_file_name,
                i,
                outputStream.toByteArray().length,
                "",
                null,
                user_info.getEmail(),
                null,
                null,
                null,
                outputStream.toByteArray(),
                null
        );
        response = UploadAsset.uploadAsset(user_info, asset, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        response.setMessage("{\"asset_id\":" + response.getData() + "}");
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Upload Asset Base64">
    public static InternalResponse uploadAssetBase64(
            final HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Get header         
        String x_file_type = Utils.getRequestHeader(request, "x-file-type");
        String x_file_name = Utils.getRequestHeader(request, "x-file-name");
        Asset temp = new Asset();
        temp = new ObjectMapper().readValue(payload, Asset.class);

        //Decode to get File data
        byte[] temp2;
        try {
            temp2 = Base64.getDecoder().decode(temp.getBase64().getBytes());
        } catch (Exception ex) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_ASSET,
                            PaperlessConstant.SUBCODE_INVALID_FILE_DATA,
                            "en",
                            transactionID));
        }

        //Check Asset Type is valid + extension in file name
        int i = convertStringIntoAssetType(x_file_type, x_file_name);
        if (i == -1) {
            String message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_INVALID_PARAMS_ASSET,
                    PaperlessConstant.SUBCODE_INVALID_FILE_TYPE,
                    "en",
                    transactionID);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    message);
        }

        Asset asset = new Asset(
                0,
                x_file_name,
                i,
                temp2.length,
                "",
                null,
                user_info.getEmail(),
                null,
                null,
                null,
                temp2,
                null
        );
        response = UploadAsset.uploadAsset(user_info, asset, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        response.setMessage("{\"asset_id\":" + response.getData() + "}");
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Update Asset">
    public static InternalResponse updateAsset(
            final HttpServletRequest request,
            int id,
            String transactionID) throws Exception {
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
            return new InternalResponse();
        }

        //Check Exist fileData and Name
        if (Utils.isNullOrEmpty(x_file_name)) {

        }

        //Processing
        int i = 0;
        if (x_file_type != null) {
            i = convertStringIntoAssetType(x_file_type, x_file_name);
            if (i == -1) {
                String message = PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_INVALID_PARAMS_ASSET,
                        PaperlessConstant.SUBCODE_INVALID_FILE_TYPE,
                        "en",
                        null);
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                        message);
            }
        }

        Asset asset = new Asset(
                id,
                null,
                i,
                outputStream == null ? 0 : Long.valueOf(outputStream.toByteArray().length),
                "",
                null,
                "",
                null,
                "",
                "",
                outputStream == null ? null : outputStream.toByteArray(),
                null
        );

        response = UpdateAsset.updateAsset(
                asset,
                user_info,
                transactionID);
        response.setUser(user_info);

        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Update Asset Base64">
    public static InternalResponse updateAssetBase64(
            final HttpServletRequest request,
            int id,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Get header
        String x_file_type = Utils.getRequestHeader(request, "x-file-type");
        String x_file_name = Utils.getRequestHeader(request, "x-file-name");
        String payload = Utils.getPayload(request);
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }
        Asset asset = new Asset();
        try {
            asset = new ObjectMapper().readValue(payload, Asset.class);
            if (asset.getBase64() != null) {
                asset.setBinaryData(Base64.getDecoder().decode(asset.getBase64()));
            }
        } catch (Exception ex) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            "en",
                            null));
        }

        //Processing
        int i = 0;
        if (x_file_type != null) {
            i = convertStringIntoAssetType(x_file_type, x_file_name);
            if (i == -1) {
                String message = PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_INVALID_PARAMS_ASSET,
                        PaperlessConstant.SUBCODE_INVALID_FILE_TYPE,
                        "en",
                        null);
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                        message);
            }
        }
        asset.setName(x_file_name);
        asset.setId(id);
        asset.setType(i);
        if (asset.getBinaryData() != null) {
            asset.setSize(asset.getBinaryData().length);
        }

        response = UpdateAsset.updateAsset(
                asset,
                user_info,
                transactionID);
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Asset Template">
    public static InternalResponse getAssetTemplate(
            final HttpServletRequest request,
            int id,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        response = GetAsset.getAsset(id, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        Asset asset = (Asset) response.getData();
        response = new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                asset.getMetadata());
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Asset Details">
    public static InternalResponse getAssetDetails(
            final HttpServletRequest request,
            int id,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        response = GetAsset.getAsset(id, transactionID);
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get List Workflow">
    public static InternalResponse getListWorkflow(
            final HttpServletRequest request,
            String transactionID) throws JsonProcessingException, Exception {
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
        String status = "0,1";
        String statusFromUrl = data[0];
        if (statusFromUrl != null) {
            switch (statusFromUrl) {
                case "INACTIVE": {
                    status = "0";
                    break;
                }
                case "ACTIVE": {
                    status = "1";
                    break;
                }
                default: {
                    status = "0,1";
                    break;
                }
            }
        }

        int page_no;
        int record;
        int numberOfRecords = PaperlessConstant.DEFAULT_ROW_COUNT;
        try {
            page_no = (data[1] == null ? 0 : Integer.parseInt(data[1]));
            record = (data[2] == null ? 0 : Integer.parseInt(data[2]));
        } catch (Exception e) {
            page_no = 0;
            record = 0;
        }
        try {
            numberOfRecords = Integer.parseInt(Utils.getRequestHeader(request, "x-number-records"));
            page_no = 0;
            record = 0;
        } catch (Exception ex) {
            numberOfRecords = PaperlessConstant.DEFAULT_ROW_COUNT;
        }

        String searchTemplate = "1,2,3,4,5,6,7,8";
        String template = Utils.getRequestHeader(request, "x-search-type");
        //Thread Pool to check template
        ExecutorService executor = Executors.newFixedThreadPool(2);
        TaskV2 templateType1 = new TaskV2(new Object[]{template}, transactionID) {
            @Override
            public Object call() {
                try {
                    String result = "";
                    String template = (String) this.get()[0];
                    if (template != null) {
                        template = new String(Base64.getDecoder().decode(template));
                        switch (template) {
                            case "QR GENERATOR": {
                                result = "1";
                                break;
                            }
                            case "PDF GENERATOR": {
                                result = "2";
                                break;
                            }
                            case "SIMPLE PDF STAMPING": {
                                result = "3";
                                break;
                            }
                            case "PDF STAMPING": {
                                result = "4";
                                break;
                            }
                            case "FILE STAMPING PROCESSING": {
                                result = "5";
                                break;
                            }
                            case "LEI PDF STAMPING": {
                                result = "6";
                                break;
                            }
                            case "E-LABOR CONTRACT": {
                                result = "7";
                                break;
                            }
                            case "ESIGNCLOUD": {
                                result = "8";
                                break;
                            }
                            default: {
                                result = "1,2,3,4,5,6,7,8";
                                break;
                            }
                        }
                    }
                    return result;
                } catch (Exception ex) {
                    return null;
                }
            }
        };

        TaskV2 templateType2 = new TaskV2(new Object[]{template}, transactionID) {
            @Override
            public Object call() {
                try {
                    String template = new String(Base64.getDecoder().decode((String) this.get()[0]));
                    String[] temps = template.split(",");
                    for (String temp : temps) {
                        Integer.parseInt(temp);
                    }
                    return template;
                } catch (Exception ex) {
                    try {
                        String[] temps = template.split(",");
                        for (String temp : temps) {
                            Integer.parseInt(temp);
                        }
                        return template;
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        };

        //Get future
        Future<?> future1 = executor.submit(templateType1);
        Future<?> future2 = executor.submit(templateType2);
        executor.shutdown();

        searchTemplate
                = (future1.get() == null)
                ? (future2.get() == null
                ? searchTemplate
                : (String) future2.get())
                : (String) future1.get();

        //Process
        InternalResponse res = GetWorkflow.getListWorkflow(
                user_info.getEmail(),
                user_info.getAid(), //enterprise_id
                status, //status
                searchTemplate.equals("") ? "1,2,3,4,5,6,7,8" : searchTemplate, //workflow_type
                false, //enable metadata
                "", //value of metadata
                (page_no <= 1) ? 0 : (page_no - 1) * record, //offset
                record == 0 ? numberOfRecords : record, //rowcount
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        List<Workflow> list = (List<Workflow>) res.getData();
        CustomListWorkflowSerializer customSerializer = new CustomListWorkflowSerializer(
                list,
                page_no,
                record);
        res.setMessage(new ObjectMapper().writeValueAsString(customSerializer));
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("x-total-records", list.size());
        res.setHeaders(hashmap);
        res.setUser(user_info);
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get List Asset">
    public static InternalResponse getListAsset(
            final HttpServletRequest request,
            String transactionID) throws JsonProcessingException, Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Process header        
        String URI = request.getRequestURI();
        URI = URI.replaceFirst(".*asset/", "");
        String[] data = URI.split("/");
        String status = "1,2";
        String statusFromUrl = data[0];
        if (statusFromUrl != null) {
            switch (statusFromUrl) {
                case "INACTIVE": {
                    status = "2";
                    break;
                }
                case "ACTIVE": {
                    status = "1";
                    break;
                }
                default: {
                    status = "1,2";
                    break;
                }
            }
        }

        int page_no;
        int record;
        int numberOfRecords = PaperlessConstant.DEFAULT_ROW_COUNT;
        try {
            page_no = (data[1] == null ? 0 : Integer.parseInt(data[1]));
            record = (data[2] == null ? 0 : Integer.parseInt(data[2]));
        } catch (Exception e) {
            page_no = 0;
            record = 0;
        }
        try {
            numberOfRecords = Integer.parseInt(Utils.getRequestHeader(request, "x-number-records"));
            page_no = 0;
            record = 0;
        } catch (Exception ex) {
            numberOfRecords = PaperlessConstant.DEFAULT_ROW_COUNT;
        }

        String filename = Utils.getRequestHeader(request, "x-search-text");
        String type = Utils.getRequestHeader(request, "x-search-type");

        //Processing
        InternalResponse res = GetAsset.getListAsset(
                user_info.getAid(),
                user_info.getEmail(),
                filename,
                type,
                status,
                (page_no <= 1) ? 0 : (page_no - 1) * record, //offset
                record == 0 ? numberOfRecords : record, //rowcount
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        CustomListAssetSerializer tempp = new CustomListAssetSerializer((List<Asset>) res.getData(), 0, 0);
        res.setMessage(new ObjectMapper().writeValueAsString(tempp));
        res.setUser(user_info);
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get List Workflow Activity">
    public static InternalResponse getListWorkflowAc(
            final HttpServletRequest request,
            String transactionID) throws JsonProcessingException, Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Process header        
        String URI = request.getRequestURI();
        URI = URI.replaceFirst(".*workflowactivity/", "");
        String[] data = URI.split("/");
        String status = data[0];
        String search = "1,2,3,4";
        if (status != null) {
            if (status.contains("ACTIVE")) {
                search = "1";
            } else if (status.contains("HIDDEN")) {
                search = "2";
            } else if (status.contains("EXPIRED")) {
                search = "3";
            } else if (status.contains("REVOKE")) {
                search = "4";
            } else if (status.contains("ALL")) {
                search = "1,2,3,4";
            }
        }

        int page_no;
        int record;
        int numberOfRecords = PaperlessConstant.DEFAULT_ROW_COUNT;
        try {
            page_no = (data[1] == null ? 0 : Integer.parseInt(data[1]));
            record = (data[2] == null ? 0 : Integer.parseInt(data[2]));
        } catch (Exception e) {
            page_no = 0;
            record = 0;
        }
        try {
            numberOfRecords = Integer.parseInt(Utils.getRequestHeader(request, "x-number-records"));
            page_no = 0;
            record = 0;
        } catch (Exception ex) {
        }

        //Search type
        String email_search = Utils.getRequestHeader(request, "x-search-email");
        int workflowId = 0;
        try {
            workflowId = Integer.parseInt(Utils.getRequestHeader(request, "x-search-workflow"));
        } catch (Exception ex) {
        }
        String start_ = Utils.getRequestHeader(request, "x-search-start");
        String stop_ = Utils.getRequestHeader(request, "x-search-stop");
        Date start = null, stop = null;
        try {
            if (start_ != null) {
                SimpleDateFormat format = new SimpleDateFormat(PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
                start = format.parse(start_);
            }
            if (stop_ != null) {
                SimpleDateFormat format = new SimpleDateFormat(PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
                stop = format.parse(stop_);
            }
        } catch (Exception ex) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"error\":\"INVALID DATE\",\"error_description\":\"Please follow this format of Date:" + PolicyConfiguration
                            .getInstant()
                            .getSystemConfig()
                            .getAttributes()
                            .get(0)
                            .getDateFormat() + "\"}"
            );
        }

        //Thread Pool to check template
        String searchTemplate = "1,2,3,4,5,6,7,8";
        String template = Utils.getRequestHeader(request, "x-search-type");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        TaskV2 templateType1 = new TaskV2(new Object[]{template}, transactionID) {
            @Override
            public Object call() {
                try {
                    String result = null;
                    String template = (String) this.get()[0];
                    if (template != null) {
                        template = new String(Base64.getDecoder().decode(template));
                        switch (template) {
                            case "QR GENERATOR": {
                                result = "1";
                                break;
                            }
                            case "PDF GENERATOR": {
                                result = "2";
                                break;
                            }
                            case "SIMPLE PDF STAMPING": {
                                result = "3";
                                break;
                            }
                            case "PDF STAMPING": {
                                result = "4";
                                break;
                            }
                            case "FILE STAMPING PROCESSING": {
                                result = "5";
                                break;
                            }
                            case "LEI PDF STAMPING": {
                                result = "6";
                                break;
                            }
                            case "E-LABOR CONTRACT": {
                                result = "7";
                                break;
                            }
                            case "ESIGNCLOUD": {
                                result = "8";
                                break;
                            }
                            default: {
                                result = "1,2,3,4,5,6,7,8";
                                break;
                            }
                        }
                    }
                    return result;
                } catch (Exception ex) {
                    return null;
                }
            }
        };

        TaskV2 templateType2 = new TaskV2(new Object[]{template}, transactionID) {
            @Override
            public Object call() {
                try {
                    String template = new String(Base64.getDecoder().decode((String) this.get()[0]));
                    String[] temps = template.split(",");
                    for (String temp : temps) {
                        Integer.parseInt(temp);
                    }
                    return template;
                } catch (Exception ex) {
                    try {
                        String[] temps = template.split(",");
                        for (String temp : temps) {
                            Integer.parseInt(temp);
                        }
                        return template;
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        };

        //Get future
        Future<?> future1 = executor.submit(templateType1);
        Future<?> future2 = executor.submit(templateType2);
        executor.shutdown();

        searchTemplate
                = (future1.get() == null)
                ? (future2.get() == null
                ? searchTemplate
                : (String) future2.get())
                : (String) future1.get();

        //Processing
        InternalResponse res = null;
        if (workflowId != 0) {
            res = GetWorkflowActivity.getListWorkflowActivity_basedOnWorkflow(
                    workflowId,
                    (page_no <= 1) ? 0 : (page_no - 1) * record,
                    record == 0 ? numberOfRecords : record,
                    transactionID);
        } else {
            res = GetWorkflowActivity.getListWorkflowActivity(user_info.getEmail(),
                    user_info.getAid(),
                    email_search,
                    null,
                    searchTemplate,
                    search,
                    "1,2,3",
                    (start != null || stop != null),
                    start,
                    stop,
                    (page_no <= 1) ? 0 : (page_no - 1) * record,
                    record == 0 ? numberOfRecords : record,
                    transactionID);
        }
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        List<WorkflowActivity> list = (List<WorkflowActivity>) res.getData();
        CustomListWoAcSerializer test = new CustomListWoAcSerializer(list, 0, 0);
        res.setMessage(new ObjectMapper().writeValueAsString(test));
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("x-total-records", list.size());
        res.setHeaders(hashmap);
        res.setUser(user_info);
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Total Records Workflow Ac">
    public static InternalResponse getTotalRecordsWorkflowAc(
            final HttpServletRequest request,
            String transactionID) throws JsonProcessingException, Exception {
        //<editor-fold defaultstate="collapsed" desc="Check valid Token">
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();
        //</editor-fold>

        //Process header        
        String URI = request.getRequestURI();
        URI = URI.replaceFirst(".*workflowactivity/gettotal/", "");
        String[] data = URI.split("/");
        String status = data[0];
        String search = "1,2,3,4";
        if (status != null) {
            if (status.contains("ACTIVE")) {
                search = "1";
            } else if (status.contains("HIDDEN")) {
                search = "2";
            } else if (status.contains("EXPIRED")) {
                search = "3";
            } else if (status.contains("REVOKE")) {
                search = "4";
            } else if (status.contains("ALL")) {
                search = "1,2,3,4";
            }
        }

        int page_no;
        int record;
        int numberOfRecords = PaperlessConstant.DEFAULT_ROW_COUNT;
        try {
            page_no = (data[1] == null ? 0 : Integer.parseInt(data[1]));
            record = (data[2] == null ? 0 : Integer.parseInt(data[2]));
        } catch (Exception e) {
            page_no = 0;
            record = 0;
        }
        try {
            numberOfRecords = Integer.parseInt(Utils.getRequestHeader(request, "x-number-records"));
            page_no = 0;
            record = 0;
        } catch (Exception ex) {
        }

        //Search type
        String email_search = Utils.getRequestHeader(request, "x-search-email");
        int workflowId = 0;
        try {
            workflowId = Integer.parseInt(Utils.getRequestHeader(request, "x-search-workflow"));
        } catch (Exception ex) {
        }
        String start_ = Utils.getRequestHeader(request, "x-search-start");
        String stop_ = Utils.getRequestHeader(request, "x-search-stop");
        Date start = null, stop = null;
        try {
            if (start_ != null) {
                SimpleDateFormat format = new SimpleDateFormat(PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
                start = format.parse(start_);
            }
            if (stop_ != null) {
                SimpleDateFormat format = new SimpleDateFormat(PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
                stop = format.parse(stop_);
            }
        } catch (Exception ex) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"error\":\"INVALID DATE\",\"error_description\":\"Please follow this format of Date:" + PolicyConfiguration
                            .getInstant()
                            .getSystemConfig()
                            .getAttributes()
                            .get(0)
                            .getDateFormat() + "\"}"
            );
        }

        //Thread Pool to check template
        String searchTemplate = "1,2,3,4,5,6,7";
        String template = Utils.getRequestHeader(request, "x-search-type");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        TaskV2 templateType1 = new TaskV2(new Object[]{template}, transactionID) {
            @Override
            public Object call() {
                try {
                    String result = null;
                    String template = (String) this.get()[0];
                    if (template != null) {
                        template = new String(Base64.getDecoder().decode(template));
                        switch (template) {
                            case "QR GENERATION": {
                                result = "1";
                                break;
                            }
                            case "PDF GENERATION": {
                                result = "2";
                                break;
                            }
                            case "PDF STAMPING": {
                                result = "3";
                                break;
                            }
                            case "SIMPLE PDF STAMPING": {
                                result = "4";
                                break;
                            }
                            case "FILE STAMPING PROCESSING": {
                                result = "5";
                                break;
                            }
                            case "E-LABOR CONTRACT - WITNESSING": {
                                result = "6";
                                break;
                            }
                            case "E-LABOR CONTRACT - ESIGNCLOUD": {
                                result = "7";
                                break;
                            }
                            default: {
                                result = "1,2,3,4,5,6,7";
                                break;
                            }
                        }
                    }
                    return result;
                } catch (Exception ex) {
                    return null;
                }
            }
        };

        TaskV2 templateType2 = new TaskV2(new Object[]{template}, transactionID) {
            @Override
            public Object call() {
                try {
                    String template = new String(Base64.getDecoder().decode((String) this.get()[0]));
                    String[] temps = template.split(",");
                    for (String temp : temps) {
                        Integer.parseInt(temp);
                    }
                    return template.isEmpty() ? null : template;
                } catch (Exception ex) {
                    try {
                        String[] temps = template.split(",");
                        for (String temp : temps) {
                            Integer.parseInt(temp);
                        }
                        return template;
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        };

        //Get future
        Future<?> future1 = executor.submit(templateType1);
        Future<?> future2 = executor.submit(templateType2);
        executor.shutdown();

        searchTemplate
                = (future1.get() == null)
                ? (future2.get() == null
                ? searchTemplate
                : (String) future2.get())
                : (String) future1.get();

        //Processing
        InternalResponse res = null;
        if (workflowId != 0) {
            res = GetWorkflowActivity.getRowCountWorkflowActivity_basedOnWorkflow(workflowId, transactionID);
        } else {
            res = GetWorkflowActivity.getRowCountWorkflowActivity(
                    user_info.getEmail(),
                    user_info.getAid(),
                    email_search,
                    null,
                    searchTemplate,
                    search,
                    "1,2,3",
                    (start != null && stop != null),
                    start != null ? start : null,
                    stop != null ? stop : null,
                    (page_no <= 1) ? 0 : (page_no - 1) * record,
                    record == 0 ? numberOfRecords : record,
                    transactionID);
        }
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        res.setMessage("{\"x-total-records\":" + res.getData() + "}");
        res.setUser(user_info);
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Workflow Template">
    public static InternalResponse getWorkflowTemplate(
            final HttpServletRequest request,
            int id,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        response = GetWorkflowTemplate.getWorkflowTemplate(id, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        CustomWorkflowTemplateSerializer serializer = new CustomWorkflowTemplateSerializer((WorkflowTemplate) response.getData());
        response.setMessage(new ObjectMapper().writeValueAsString(serializer));
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Workflow">
    public static InternalResponse getWorkflow(
            final HttpServletRequest request,
            int id,
            String transactionID) throws JsonProcessingException, Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        User user_info = response.getUser();

        //Processing
        response = GetWorkflow.getWorkflow(id, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        CustomWorkflowSerializer custom = new CustomWorkflowSerializer((Workflow) response.getData());
        response.setMessage(new ObjectMapper().writeValueAsString(custom));
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Deactive Workflow">
    public static InternalResponse deactiveWorkflow(
            final HttpServletRequest request,
            int id,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        response = ManageStatusWorkflow.deactiveWorkflow(
                id,
                user_info.getEmail(),
                user_info.getAid(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Reactive Workflow">
    public static InternalResponse reactiveWorkflow(
            final HttpServletRequest request,
            int id,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        response = ManageStatusWorkflow.reactiveWorkflow(
                id,
                user_info.getEmail(),
                user_info.getAid(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Update Workflow Details">
    public static InternalResponse updateWorkflowDetail_option(
            final HttpServletRequest request,
            int id,
            String payload,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Get data 
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        //Check format of payload
        try {
            JsonNode node = new ObjectMapper().readTree(payload);
            if (!node.fieldNames().next().equalsIgnoreCase("workflow_option")) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_FAIL,
                                PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                                PaperlessMessageResponse.getLangFromJson(payload),
                                null));
            }
        } catch (Exception ex) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Cast Data from client
        List<WorkflowAttributeType> details = new ArrayList<>();
        try {
            details = WorkflowAttributeType.castTo(payload);
        } catch (Exception e) {
            LogHandler.error(
                    PaperlessService.class,
                    transactionID,
                    "Cannot parse payload",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Processing
        response = UpdateWorkflowDetail.updateWorkflowDetail(
                id,
                user_info.getEmail(),
                user_info.getAid(),
                details,
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Update Workflow Template">
    public static InternalResponse updateWorkflowTemplate(
            final HttpServletRequest request,
            int id,
            String payload,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Get data 
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        ObjectMapper mapper = new ObjectMapper();
        Item_JSNObject workflowtemplate = new Item_JSNObject();
        try {
            workflowtemplate = mapper.readValue(payload, Item_JSNObject.class);
        } catch (Exception e) {
            LogHandler.error(
                    PaperlessService.class,
                    transactionID,
                    "Cannot parse payload",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check data
        InternalResponse res = CheckWorkflowTemplate.checkDataWorkflowTemplate(workflowtemplate);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        //Processing
        res = UpdateWorkflowTemplate.updateWorkflowTemplate(
                id,
                user_info.getEmail(),
                user_info.getAid(),
                workflowtemplate,
                "hmac",
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Update User Password">
    public static InternalResponse updateUserPassword(
            final HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        String old_password = Utils.getFromJson("user_old_password", payload);
        String new_password = Utils.getFromJson("user_new_password", payload);

        if (old_password == null || new_password == null) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_MISSING_OLD_OR_NEW_PASSWORD,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Processing
        response = UpdateUser.updateUserPassword(
                user_info.getEmail(),
                old_password,
                new_password,
                transactionID);
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Generate Hash Document">
    public static InternalResponse generateHashDocument(
            final HttpServletRequest request,
            String payload,
            int id,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Get Header anh process JWT
        HashMap<String, String> headers = Utils.getHashMapRequestHeader(request);
        String headerJWT = headers.get("eid-jwt");
        System.out.println("JWT in header (PaperlessService):" + headerJWT);
        if (headerJWT == null || headerJWT.isEmpty()) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_JWT,
                            PaperlessConstant.SUBCODE_MISSING_JWT,
                            payload,
                            transactionID)
            );
        }
        JWT_Authenticate jwt = new JWT_Authenticate();
        if (headerJWT != null) {
            InternalResponse data = ProcessEID_JWT.getInfoJWT(headerJWT);
            if (data.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return data;
            }
            jwt = (JWT_Authenticate) data.getData();
        }

        //Mapper Object
        ObjectMapper mapper = new ObjectMapper();
        ProcessWorkflowActivity_JSNObject data = null;
        try {
            if (!Utils.isNullOrEmpty(payload)) {
                data = mapper.readValue(payload, ProcessWorkflowActivity_JSNObject.class);
            }
            //Check valid data 
            InternalResponse result = null;
            result = CheckWorkflowTemplate.checkFile_Data(data);
            if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return result;
            }
        } catch (Exception e) {
            LogHandler.error(
                    PaperlessService.class,
                    transactionID,
                    "Cannot parse payload",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }
        //Processing
        InternalResponse res = ProcessWorkflowActivity.getDocumentHash(
                user_info,
                id,
                jwt,
                data,
                headers,
                transactionID);
        res.setUser(user_info);
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Total Record Workflow">
    public static InternalResponse getTotalRecordsWorkflow(
            final HttpServletRequest request,
            String transactionID) throws JsonProcessingException, Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Process header        
        String URI = request.getRequestURI();
        URI = URI.replaceFirst(".*workflow/gettotal/", "");
        String[] data = URI.split("/");
        String status = "0,1";
        String statusFromUrl = data[0];
        if (statusFromUrl != null) {
            switch (statusFromUrl) {
                case "INACTIVE": {
                    status = "0";
                    break;
                }
                case "ACTIVE": {
                    status = "1";
                    break;
                }
                default: {
                    status = "0,1";
                    break;
                }
            }
        }

        //Search type
        String email_search = Utils.getRequestHeader(request, "x-search-email");
        String start_ = Utils.getRequestHeader(request, "x-search-start");
        String stop_ = Utils.getRequestHeader(request, "x-search-stop");
        Date start = null, stop = null;
        try {
            if (start_ != null) {
                SimpleDateFormat format = new SimpleDateFormat(PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
                start = format.parse(start_);
            }
            if (stop != null) {
                SimpleDateFormat format = new SimpleDateFormat(PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
                stop = format.parse(stop_);
            }
        } catch (Exception ex) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"error\":\"INVALID DATE\",\"error_description\":\"Please follow this format of Date:" + PolicyConfiguration
                            .getInstant()
                            .getSystemConfig()
                            .getAttributes()
                            .get(0)
                            .getDateFormat() + "\"}"
            );
        }

        //Thread Pool to check template
        String searchTemplate = "1,2,3,4,5,6,7,8";
        String template = Utils.getRequestHeader(request, "x-search-type");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        TaskV2 templateType1 = new TaskV2(new Object[]{template}, transactionID) {
            @Override
            public Object call() {
                try {
                    String result = null;
                    String template = (String) this.get()[0];
                    if (template != null) {
                        template = new String(Base64.getDecoder().decode(template));
                        switch (template) {
                            case "QR GENERATOR": {
                                result = "1";
                                break;
                            }
                            case "PDF GENERATOR": {
                                result = "2";
                                break;
                            }
                            case "SIMPLE PDF STAMPING": {
                                result = "3";
                                break;
                            }
                            case "PDF STAMPING": {
                                result = "4";
                                break;
                            }
                            case "FILE STAMPING PROCESSING": {
                                result = "5";
                                break;
                            }
                            case "LEI PDF STAMPING": {
                                result = "6";
                                break;
                            }
                            case "E-LABOR CONTRACT": {
                                result = "7";
                                break;
                            }
                            case "ESIGNCLOUD": {
                                result = "8";
                                break;
                            }
                            default: {
                                result = "1,2,3,4,5,6,7,8";
                                break;
                            }
                        }
                    }
                    return result;
                } catch (Exception ex) {
                    return null;
                }
            }
        };

        TaskV2 templateType2 = new TaskV2(new Object[]{template}, transactionID) {
            @Override
            public Object call() {
                try {
                    String template = new String(Base64.getDecoder().decode((String) this.get()[0]));
                    String[] temps = template.split(",");
                    for (String temp : temps) {
                        Integer.parseInt(temp);
                    }
                    return template.isEmpty() ? null : template;
                } catch (Exception ex) {
                    try {
                        String[] temps = template.split(",");
                        for (String temp : temps) {
                            Integer.parseInt(temp);
                        }
                        return template;
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        };

        //Get future
        Future<?> future1 = executor.submit(templateType1);
        Future<?> future2 = executor.submit(templateType2);
        executor.shutdown();

        searchTemplate
                = (future1.get() == null)
                ? (future2.get() == null
                ? searchTemplate
                : (String) future2.get())
                : (String) future1.get();

        //Processing
        InternalResponse res = GetWorkflow.getRowCountOfWorkflow(
                user_info.getEmail(),
                user_info.getAid(),
                status,
                searchTemplate,
                false,
                "",
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        res.setMessage("{\"x-total-record\":" + (long) res.getData() + "}");
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("x-total-records", (long) res.getData());
        res.setHeaders(hashmap);
        res.setUser(user_info);
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Total Record Asset">
    public static InternalResponse getTotalRecordsAsset(
            final HttpServletRequest request,
            String transactionID) throws JsonProcessingException, Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Process header        
        String URI = request.getRequestURI();
        URI = URI.replaceFirst(".*asset/gettotal/", "");
        String[] data = URI.split("/");
        String status = "1,2";
        String statusFromUrl = data[0];
        if (statusFromUrl != null) {
            switch (statusFromUrl) {
                case "INACTIVE": {
                    status = "2";
                    break;
                }
                case "ACTIVE": {
                    status = "1";
                    break;
                }
                default: {
                    status = "1,2";
                    break;
                }
            }
        }

        //Search page
        int page_no;
        int record;
        int numberOfRecords = PaperlessConstant.DEFAULT_ROW_COUNT;
        try {
            page_no = (data[1] == null ? 0 : Integer.parseInt(data[1]));
            record = (data[2] == null ? 0 : Integer.parseInt(data[2]));
        } catch (Exception e) {
            page_no = 0;
            record = 0;
        }
        try {
            numberOfRecords = Integer.parseInt(Utils.getRequestHeader(request, "x-number-records"));
            page_no = 0;
            record = 0;
        } catch (Exception ex) {
        }

        //Search type
        String filename = Utils.getRequestHeader(request, "x-search-text");
        String email_search = Utils.getRequestHeader(request, "x-search-email");
        String start_ = Utils.getRequestHeader(request, "x-search-start");
        String stop_ = Utils.getRequestHeader(request, "x-search-stop");
        Date start = null, stop = null;
        try {
            if (start_ != null) {
                SimpleDateFormat format = new SimpleDateFormat(PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
                start = format.parse(start_);
            }
            if (stop != null) {
                SimpleDateFormat format = new SimpleDateFormat(PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
                stop = format.parse(stop_);
            }
        } catch (Exception ex) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"error\":\"INVALID DATE\",\"error_description\":\"Please follow this format of Date:" + PolicyConfiguration
                            .getInstant()
                            .getSystemConfig()
                            .getAttributes()
                            .get(0)
                            .getDateFormat() + "\"}"
            );
        }

        //Thread Pool to check template
        String searchTemplate = "1,2,3,4,5,6,7,8";
        String template = Utils.getRequestHeader(request, "x-search-type");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        TaskV2 templateType1 = new TaskV2(new Object[]{template}, transactionID) {
            @Override
            public Object call() {
                try {
                    String result = null;
                    String template = (String) this.get()[0];
                    if (template != null) {
                        template = new String(Base64.getDecoder().decode(template));
                        switch (template) {
                            case "QR GENERATOR": {
                                result = "1";
                                break;
                            }
                            case "PDF GENERATOR": {
                                result = "2";
                                break;
                            }
                            case "SIMPLE PDF STAMPING": {
                                result = "3";
                                break;
                            }
                            case "PDF STAMPING": {
                                result = "4";
                                break;
                            }
                            case "FILE STAMPING PROCESSING": {
                                result = "5";
                                break;
                            }
                            case "LEI PDF STAMPING": {
                                result = "6";
                                break;
                            }
                            case "E-LABOR CONTRACT": {
                                result = "7";
                                break;
                            }
                            case "ESIGNCLOUD": {
                                result = "8";
                                break;
                            }
                            default: {
                                result = "1,2,3,4,5,6,7,8";
                                break;
                            }
                        }
                    }
                    return result;
                } catch (Exception ex) {
                    return null;
                }
            }
        };

        TaskV2 templateType2 = new TaskV2(new Object[]{template}, transactionID) {
            @Override
            public Object call() {
                try {
                    String template = new String(Base64.getDecoder().decode((String) this.get()[0]));
                    String[] temps = template.split(",");
                    for (String temp : temps) {
                        Integer.parseInt(temp);
                    }
                    return template.isEmpty() ? null : template;
                } catch (Exception ex) {
                    try {
                        String[] temps = template.split(",");
                        for (String temp : temps) {
                            Integer.parseInt(temp);
                        }
                        return template;
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        };

        //Get future
        Future<?> future1 = executor.submit(templateType1);
        Future<?> future2 = executor.submit(templateType2);
        executor.shutdown();

        searchTemplate
                = (future1.get() == null)
                ? (future2.get() == null
                ? searchTemplate
                : (String) future2.get())
                : (String) future1.get();

        //Processing
        InternalResponse res = GetAsset.getTotalRecordAsset(
                user_info.getAid(),
                user_info.getEmail(),
                filename,
                searchTemplate,
                status,
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        res.setMessage("{\"x-total-record\":" + res.getData() + "}");
        res.setUser(user_info);
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Delete Asset">
    public static InternalResponse deleteAsset(
            final HttpServletRequest request,
            int id,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        response = DeleteAsset.deleteAsset(
                id,
                user_info.getEmail(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Reactive Asset">
    public static InternalResponse reactiveAsset(
            final HttpServletRequest request,
            int id,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        response = ManageStatusWorkflow.reactiveWorkflow(
                id,
                user_info.getEmail(),
                user_info.getAid(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Asset Type">
    public static InternalResponse getAssetType(
            final HttpServletRequest request,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        HashMap<String, Integer> hashmap = Resources.getListAssetType();
        if (hashmap == null || hashmap.isEmpty()) {
            Resources.reloadListAssetType();
            hashmap = Resources.getListAssetType();
        }
        ArrayNode node = new ObjectMapper().createArrayNode();
        hashmap.forEach((key, value) -> {
            ObjectNode child = new ObjectMapper().createObjectNode();
            child.put("asset_type_id", value);
            child.put("asset_type_name", key);
            node.add(child);
        });
        response = new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                node);
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Accounts/ Accoun">
    public static InternalResponse getAccounts(
            final HttpServletRequest request,
            String transactionID
    ) throws Exception {
        //Check user token                
        User user_info = null;
        try {
            InternalResponse response = verifyToken(request, transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
                return response;
            }
            user_info = response.getUser();
        } catch (Exception e) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_INVALID_TOKEN,
                            "en",
                            null));
        }

        InternalResponse response = GetUser.getUser(
                user_info.getEmail(),
                0,
                user_info.getAid(),
                transactionID,
                false);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        Account account = (Account) response.getData();

        //Get Enterprise info
        response = GetEnterpriseInfo.getEnterprise(
                null,
                user_info.getAid(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        Enterprise ent = (Enterprise) response.getData();
        account.setEnterprise_name(ent.getName());
        response.setMessage(new ObjectMapper().writeValueAsString(account));
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Organization">
    public static InternalResponse getOrganization(
            final HttpServletRequest request,
            String transactionID
    ) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        response = GetEnterpriseInfo.getEnterpriseInfo(user_info.getEmail(), transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        response = new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                new ObjectMapper().writeValueAsString(response.getData()));
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Update Workflow Activity">
    public static InternalResponse updateWorkflowActivity(
            final HttpServletRequest request,
            int id,
            String payload,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Get data 
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        ObjectMapper mapper = new ObjectMapper();
        WorkflowActivity woAc = new WorkflowActivity();
        try {
            woAc = mapper
                    .readValue(payload, WorkflowActivity.class);
        } catch (Exception e) {
            LogHandler.error(
                    PaperlessService.class,
                    transactionID,
                    "Cannot parse payload",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //<editor-fold defaultstate="collapsed" desc="Check status">
        if (woAc.getStatus_name() == null || woAc.getStatus_name().isEmpty()) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_NOT_SUPPORT_THIS_STATUS_OF_WORKFLOW_ACTIVITY,
                            "en",
                            null));
        }
        WorkflowActivityStatus status = null;
        try {
            status = WorkflowActivityStatus.valueOf(woAc.getStatus_name());
        } catch (Exception ex) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_NOT_SUPPORT_THIS_STATUS_OF_WORKFLOW_ACTIVITY,
                            "en",
                            null));
        }
        //</editor-fold>

        //Processing
        response = UpdateWorkflowActivity.updateStatus(
                id,
                status,
                user_info.getName() == null ? user_info.getEmail() : user_info.getName(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Update Workflow">
    public static InternalResponse updateWorkflow(
            final HttpServletRequest request,
            int id,
            String payload,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Get data 
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        ObjectMapper mapper = new ObjectMapper();
        Workflow workflow = new Workflow();
        try {
            workflow = mapper
                    .readValue(payload, Workflow.class);
        } catch (Exception e) {
            LogHandler.error(
                    PaperlessService.class,
                    transactionID,
                    "Cannot parse payload",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Processing
        workflow.setWorkflow_id(id);
        response = UpdateWorkflow.updateWorkflow(workflow, user_info, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Category">
    public static InternalResponse getCategory(
            final HttpServletRequest request,
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Processing
        HashMap<Integer, Category> hashmap = Resources.getListCategory();
        if (hashmap == null || hashmap.isEmpty()) {
            Resources.reloadListCategory();
            hashmap = Resources.getListCategory();
        }
        ArrayNode node = new ObjectMapper().createArrayNode();
        hashmap.forEach((key, value) -> {
            ObjectNode child = new ObjectMapper().createObjectNode();
            child.put("category_id", key);
            child.put("category_name", value.getCategory_name());
            child.put("category_name_vn", value.getRemark_vn());
            node.add(child);
        });
        response = new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                node);
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Total Records User Activity">
    public static InternalResponse getTotalRecordsUserActivity(
            final HttpServletRequest request,
            String transactionID) throws JsonProcessingException, Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Process header        
        String URI = request.getRequestURI();
        URI = URI.replaceFirst(".*useractivity/gettotal/", "");
        String[] data = URI.split("/");
        String status = data[0];
        String search = "1,2,3,4";
        if (status != null) {
            if (status.contains("ACTIVE")) {
                search = "1";
            } else if (status.contains("HIDDEN")) {
                search = "2";
            } else if (status.contains("EXPIRED")) {
                search = "3";
            } else if (status.contains("REVOKE")) {
                search = "4";
            } else if (status.contains("ALL")) {
                search = "1,2,3,4";
            }
        }

        int page_no;
        int record;
        int numberOfRecords = PaperlessConstant.DEFAULT_ROW_COUNT;
        try {
            page_no = (data[1] == null ? 0 : Integer.parseInt(data[1]));
            record = (data[2] == null ? 0 : Integer.parseInt(data[2]));
        } catch (Exception e) {
            page_no = 0;
            record = 0;
        }
        try {
            numberOfRecords = Integer.parseInt(Utils.getRequestHeader(request, "x-number-records"));
            page_no = 0;
            record = 0;
        } catch (Exception ex) {
        }

        //Search type
        String email_search = Utils.getRequestHeader(request, "x-search-email");
        String start_ = Utils.getRequestHeader(request, "x-search-start");
        String stop_ = Utils.getRequestHeader(request, "x-search-stop");
        Date start = null, stop = null;
        try {
            if (start_ != null) {
                SimpleDateFormat format = new SimpleDateFormat(PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
                start = format.parse(start_);
            }
            if (stop_ != null) {
                SimpleDateFormat format = new SimpleDateFormat(PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
                stop = format.parse(stop_);
            }
        } catch (Exception ex) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"error\":\"INVALID DATE\",\"error_description\":\"Please follow this format of Date:" + PolicyConfiguration
                            .getInstant()
                            .getSystemConfig()
                            .getAttributes()
                            .get(0)
                            .getDateFormat() + "\"}"
            );
        }

        //Thread Pool to check template
        String category = "1,2,3,4,5,6";
        String categorySearch = Utils.getRequestHeader(request, "x-search-category");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        TaskV2 templateType1 = new TaskV2(new Object[]{categorySearch}, transactionID) {
            @Override
            public Object call() {
                try {
                    String result = null;
                    String template = (String) this.get()[0];
                    if (template != null) {
                        template = new String(Base64.getDecoder().decode(template));
                        switch (template) {
                            case "Accounts": {
                                result = "1";
                                break;
                            }
                            case "Workflows": {
                                result = "2";
                                break;
                            }
                            case "File Stamping Processing": {
                                result = "3";
                                break;
                            }
                            case "Assets": {
                                result = "4";
                                break;
                            }
                            case "Settings": {
                                result = "5";
                                break;
                            }
                            case "Workflow Activity": {
                                result = "6";
                                break;
                            }
                            default: {
                                result = "1,2,3,4,5,6";
                                break;
                            }
                        }
                    }
                    return result;
                } catch (Exception ex) {
                    return null;
                }
            }
        };

        TaskV2 templateType2 = new TaskV2(new Object[]{categorySearch}, transactionID) {
            @Override
            public Object call() {
                try {
                    String template = new String(Base64.getDecoder().decode((String) this.get()[0]));
                    String[] temps = template.split(",");
                    for (String temp : temps) {
                        Integer.valueOf(temp);
                    }
                    return template.isEmpty() ? null : template;
                } catch (Exception ex) {
                    try {
                        String[] temps = categorySearch.split(",");
                        for (String temp : temps) {
                            Integer.valueOf(temp);
                        }
                        return categorySearch;
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        };

        //Get future
        Future<?> future1 = executor.submit(templateType1);
        Future<?> future2 = executor.submit(templateType2);
        executor.shutdown();

        category
                = (future1.get() == null)
                ? (future2.get() == null
                ? category
                : (String) future2.get())
                : (String) future1.get();

        //Processing
        InternalResponse res = GetUserActivity.getTotalRecordsUserActivity(
                user_info.getEmail(),
                email_search,
                user_info.getAid(),
                category,
                start,
                stop,
                transactionID);

        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        res.setMessage("{\"x-total-records\":" + res.getData() + "}");
        res.setUser(user_info);
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get List of User Activity">
    public static InternalResponse getListUserActivity(
            final HttpServletRequest request,
            String transactionID) throws JsonProcessingException, Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Process header        
        String URI = request.getRequestURI();
        URI = URI.replaceFirst(".*useractivity/", "");
        String[] data = URI.split("/");
        String status = data[0];
        String search = "1,2,3,4";
        if (status != null) {
            if (status.contains("ACTIVE")) {
                search = "1";
            } else if (status.contains("HIDDEN")) {
                search = "2";
            } else if (status.contains("EXPIRED")) {
                search = "3";
            } else if (status.contains("REVOKE")) {
                search = "4";
            } else if (status.contains("ALL")) {
                search = "1,2,3,4";
            }
        }

        int page_no;
        int record;
        int numberOfRecords = PaperlessConstant.DEFAULT_ROW_COUNT;
        try {
            page_no = (data[1] == null ? 0 : Integer.parseInt(data[1]));
            record = (data[2] == null ? 0 : Integer.parseInt(data[2]));
        } catch (Exception e) {
            page_no = 0;
            record = 0;
        }
        try {
            numberOfRecords = Integer.parseInt(Utils.getRequestHeader(request, "x-number-records"));
            page_no = 0;
            record = 0;
        } catch (Exception ex) {
        }

        //Search type
        String email_search = Utils.getRequestHeader(request, "x-search-email");
        String start_ = Utils.getRequestHeader(request, "x-search-start");
        String stop_ = Utils.getRequestHeader(request, "x-search-stop");
        Date start = null, stop = null;
        try {
            if (start_ != null) {
                SimpleDateFormat format = new SimpleDateFormat(PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
                start = format.parse(start_);
            }
            if (stop_ != null) {
                SimpleDateFormat format = new SimpleDateFormat(PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
                stop = format.parse(stop_);
            }
        } catch (Exception ex) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"error\":\"INVALID DATE\",\"error_description\":\"Please follow this format of Date:" + PolicyConfiguration
                            .getInstant()
                            .getSystemConfig()
                            .getAttributes()
                            .get(0)
                            .getDateFormat() + "\"}"
            );
        }

        //Thread Pool to check template
        String category = "1,2,3,4,5,6";
        String categorySearch = Utils.getRequestHeader(request, "x-search-category");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        TaskV2 templateType1 = new TaskV2(new Object[]{categorySearch}, transactionID) {
            @Override
            public Object call() {
                try {
                    String result = null;
                    String template = (String) this.get()[0];
                    if (template != null) {
                        template = new String(Base64.getDecoder().decode(template));
                        switch (template) {
                            case "Accounts": {
                                result = "1";
                                break;
                            }
                            case "Workflows": {
                                result = "2";
                                break;
                            }
                            case "File Stamping Processing": {
                                result = "3";
                                break;
                            }
                            case "Assets": {
                                result = "4";
                                break;
                            }
                            case "Settings": {
                                result = "5";
                                break;
                            }
                            case "Workflow Activity": {
                                result = "6";
                                break;
                            }
                            default: {
                                result = "1,2,3,4,5,6";
                                break;
                            }
                        }
                    }
                    return result;
                } catch (Exception ex) {
                    return null;
                }
            }
        };

        TaskV2 templateType2 = new TaskV2(new Object[]{categorySearch}, transactionID) {
            @Override
            public Object call() {
                try {
                    String template = new String(Base64.getDecoder().decode((String) this.get()[0]));
                    String[] temps = template.split(",");
                    for (String temp : temps) {
                        Integer.valueOf(temp);
                    }
                    return template.isEmpty() ? null : template;
                } catch (Exception ex) {
                    try {
                        String[] temps = categorySearch.split(",");
                        for (String temp : temps) {
                            Integer.valueOf(temp);
                        }
                        return categorySearch;
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        };

        //Get future
        Future<?> future1 = executor.submit(templateType1);
        Future<?> future2 = executor.submit(templateType2);
        executor.shutdown();

        category
                = (future1.get() == null)
                ? (future2.get() == null
                ? category
                : (String) future2.get())
                : (String) future1.get();

        //Processing
        InternalResponse res = GetUserActivity.getListUserActivities(
                user_info.getEmail(),
                email_search,
                user_info.getAid(),
                category,
                start,
                stop,
                (page_no <= 1) ? 0 : (page_no - 1) * record, //offset
                record == 0 ? numberOfRecords : record, //rowcount
                transactionID);

        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        CustomListUserActivitySerializer custom = new CustomListUserActivitySerializer((List<UserActivity>) res.getData(), page_no, record);
        res.setMessage(new ObjectMapper().writeValueAsString(custom));
        res.setUser(user_info);
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get User Activity Details">
    public static InternalResponse getUserActivityDetail(
            final HttpServletRequest request,
            int id,
            String transactionID) throws JsonProcessingException, Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Get User Activity Details
        response = GetUserActivity.getUserActivitiesDetails(id, transactionID);

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        UserActivity UserActivity = (UserActivity) response.getData();

        //Get User Activity Log
        response = GetUserActivityLog.getUserActivityLog(UserActivity.getLog_id(), transactionID);

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        UserActivityLog UserActivityLog = (UserActivityLog) response.getData();

        CustomUserActivitySerializer custom = new CustomUserActivitySerializer(UserActivityLog);

        response = new InternalResponse(
                response.getStatus(),
                new ObjectMapper()
                        .writeValueAsString(custom));
        response.setUser(user_info);
        return response;
    }
    //</editor-fold>

    //=================== INTERNAL FUNCTION - METHOD============================
    //<editor-fold defaultstate="collapsed" desc="Check Template Type in Request">
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
//                LogHandler.request(PaperlessService.class, "WorkflowTemplateType:" + tempp);
                return tempp.matches("^[0-" + PaperlessConstant.NUMBER_WORKFLOW_TEMPLATE_TYPE + "]$");
            }
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Send Mail">
    /**
     * Hàm gửi mail (sử dụng để tạo thread tối ưu hóa luồng hoạt động)
     */
    private static void sendMail(
            int woAcId,
            User user_info,
            String email,
            JWT_Authenticate jwt,
            String typeProcess,
            String transactionID) {
        System.out.println("Send email!");
        Thread temp = new Thread() {
            @Override
            public void run() {
                try {
                    //Get data from RAM first . If not existed => get from DB
                    byte[] file = null;
                    String nameFile = null;
                    if (Resources.getListWorkflowActivity().containsKey(String.valueOf(woAcId))) {
                        //Get WorkflowActivity from RAM
                        WorkflowActivity woAc = Resources.getWorkflowActivity(String.valueOf(woAcId));
                        //Get Transaction
                        InternalResponse response = GetTransaction.getTransaction(woAc.getTransaction(), transactionID);
                        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                            LogHandler.error(
                                    PaperlessService.class,
                                    transactionID,
                                    "Processing successfully but can't send mail!! Error while getting transaction in Workflow Activity");
                            return;
                        }
                        Transaction transaction = (Transaction) response.getData();

                        //GetFileManagement or QR or CSV 
                        switch (transaction.getObject_type().getNumber()) {
                            case 1: { //QR
                                response = GetQR.getQR(transaction.getObject_id(), transactionID);
                                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                                    LogHandler.error(
                                            PaperlessService.class,
                                            transactionID,
                                            "Processing successfully but can't send mail!! Error while getting FileManagement in Workflow Activity");
                                    return;
                                }
                                QR qr = (QR) response.getData();
                                file = Base64.getDecoder().decode(qr.getImage());
                                nameFile = "QR.png";
                                break;
                            }
                            case 2: { //CSV
                                response = GetCSVTask.getCSVTask(transaction.getObject_id(), transactionID);
                                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                                    LogHandler.error(
                                            PaperlessService.class,
                                            transactionID,
                                            "Processing successfully but can't send mail!! Error while getting FileManagement in Workflow Activity");
                                    return;
                                }
                                CSVTask csv = (CSVTask) response.getData();
                                file = csv.getBinary_data();
                                nameFile = csv.getUuid() + ".zip";
                                break;
                            }
                            case 3: { //PDF
                                response = GetFileManagement.getFileManagement(transaction.getObject_id(), transactionID);
                                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                                    LogHandler.error(
                                            PaperlessService.class,
                                            transactionID,
                                            "Processing successfully but can't send mail!! Error while getting FileManagement in Workflow Activity");
                                    return;
                                }

                                FileManagement filemanagement = (FileManagement) response.getData();
                                file = filemanagement.getData();
                                nameFile = filemanagement.getName();
                                break;
                            }
                            default: {
                                return;
                            }
                        }

                    } else {
                        try {
                            //Get WorkflowActivity from DB
                            InternalResponse response = GetWorkflowActivity.getWorkflowActivity(woAcId, transactionID);
                            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                                LogHandler.error(
                                        PaperlessService.class,
                                        transactionID,
                                        "Processing successfully but can't send mail!! Error while getting Workflow Activity");
                                return;
                            }
                            WorkflowActivity woAc = (WorkflowActivity) response.getData();
                            //Get Transaction
                            response = GetTransaction.getTransaction(woAc.getTransaction(), transactionID);
                            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                                LogHandler.error(
                                        PaperlessService.class,
                                        transactionID,
                                        "Processing successfully but can't send mail!! Error while getting transaction in Workflow Activity");
                                return;
                            }
                            Transaction transaction = (Transaction) response.getData();
                            //GetFileManagement or QR or CSV 
                            switch (transaction.getObject_type().getNumber()) {
                                case 1: { //QR
                                    response = GetQR.getQR(transaction.getObject_id(), transactionID);
                                    if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                                        LogHandler.error(
                                                PaperlessService.class,
                                                transactionID,
                                                "Processing successfully but can't send mail!! Error while getting FileManagement in Workflow Activity");
                                        return;
                                    }
                                    QR qr = (QR) response.getData();
                                    file = Base64.getDecoder().decode(qr.getImage());
                                    nameFile = "QR.png";
                                    break;
                                }
                                case 2: { //CSV
                                    response = GetCSVTask.getCSVTask(transaction.getObject_id(), transactionID);
                                    if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                                        LogHandler.error(
                                                PaperlessService.class,
                                                transactionID,
                                                "Processing successfully but can't send mail!! Error while getting FileManagement in Workflow Activity");
                                        return;
                                    }
                                    CSVTask csv = (CSVTask) response.getData();
                                    file = csv.getBinary_data();
                                    nameFile = csv.getUuid() + ".zip";
                                    break;
                                }
                                case 3: { //PDF
                                    response = GetFileManagement.getFileManagement(transaction.getObject_id(), transactionID);
                                    if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                                        LogHandler.error(
                                                PaperlessService.class,
                                                transactionID,
                                                "Processing successfully but can't send mail!! Error while getting FileManagement in Workflow Activity");
                                        return;
                                    }

                                    FileManagement filemanagement = (FileManagement) response.getData();
                                    file = filemanagement.getData();
                                    nameFile = filemanagement.getName();
                                    break;
                                }
                                default: {
                                    return;
                                }
                            }
                        } catch (Exception ex) {
                            LogHandler.error(
                                    PaperlessService.class,
                                    transactionID,
                                    "Processing successfully but can't send mail!! Error while getting file of workflow activity");
                            return;
                        }
                    }
                    System.out.println("Get Thanh cong");
                    String name = "";
                    String CCCD = "";
                    if (jwt != null
                            && jwt.getName() != null
                            && !jwt.getName().equals("")) {
                        name = jwt.getName();
                    } else {
                        name = user_info.getName();
                    }
                    if (jwt != null) {
                        CCCD = jwt.getDocument_number();
                    } else {
                        CCCD = "NonCheckJWT";
                    }

                    System.out.println("Check1");
                    LogHandler.request(
                            PaperlessService.class,
                            "SendToMail:" + email);
                    SendMail mail = new SendMail(
                            email,
                            user_info.getAid(),
                            name,
                            CCCD,
                            file,
                            nameFile
                    );
                    mail.appendTypeProcess(typeProcess);
                    mail.send();
                    System.out.println(" Gui cho " + email);
                } catch (Exception ex) {
                    LogHandler.error(
                            PaperlessService.class,
                            transactionID,
                            "Processing successfully but can't send mail!! Error while getting file of workflow activity");
                }
            }
        };
        temp.start();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Convert String into Asset Type">
    /**
     * Convert từ chuỗi asset Type đưa về dạng int của asset Type đó. Ngoài ra
     * check extension dựa trên asset Type
     *
     * @param assetType chuỗi cần đưa về
     * @return trả về số của assetType tương ứng. Nếu không tồn tại sẽ trả về -1
     */
    private static int convertStringIntoAssetType(String assetType, String name) throws Exception {
        if (Resources.getListAssetType().isEmpty()) {
            Resources.reloadListAssetType();
        }
        HashMap<String, Integer> hashmap = Resources.getListAssetType();
        for (String temp : hashmap.keySet()) {
            String type = temp.split(" ")[temp.split(" ").length - 1];
            if (type.equalsIgnoreCase(assetType) || type.equals(assetType)) {
                return hashmap.get(temp);
            }
        }
        return -1;
    }
    //</editor-fold>

}
