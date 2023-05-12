/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import vn.mobileid.id.paperless.objects.ProcessWorkflowActivity_JSNObject;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.Workflow;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.general.email.SendMail;
import vn.mobileid.id.paperless.kernel.CheckWorkflowTemplate;
import vn.mobileid.id.paperless.kernel.GetWorkflowActivity;
import vn.mobileid.id.paperless.kernel.ManageStatusWorkflow;
import vn.mobileid.id.paperless.kernel.UpdateUser;
import vn.mobileid.id.paperless.kernel.UpdateWorkflowDetail_option;
import vn.mobileid.id.paperless.kernel.UpdateWorkflowTemplate;
import vn.mobileid.id.paperless.objects.Item_JSNObject;
import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;
import vn.mobileid.id.paperless.objects.WorkflowTemplate;
import vn.mobileid.id.paperless.serializer.CustomListAssetSerializer;
import vn.mobileid.id.paperless.serializer.CustomListWoAcSerializer;
import vn.mobileid.id.paperless.serializer.CustomListWorkflowSerializer;
import vn.mobileid.id.paperless.serializer.CustomWorkflowTemplateSerializer;
import vn.mobileid.id.utils.Utils;
import vn.mobileid.id.utils.XSLT_PDF_Processing;

/**
 * Using for check valid of data!!
 *
 * @author GiaTK
 */
public class PaperlessService {

    final private static Properties appInfo = Configuration.getInstance().getAppInfo();

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

    public static InternalResponse revoke(
            final HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        return ManageTokenWithDB.processRevoke(request, payload, transactionID);
    }

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
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        //Check Workflow_Temlate_Type
        if (!checkTemplateTypeInRequest(payload)) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            PaperlessConstant.SUBCODE_MISSING_OR_ERROR_TEMPLATE_TYPE,
                            PaperlessMessageResponse.getLangFromJson(payload),
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
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check valid data 
        InternalResponse result = null;
        result = CreateWorkflow.checkDataWorkflow(workflow);
        if (result.getStatus() != 200) {
            return result;
        }

        //Processing
//        try {
        return CreateWorkflow.processingCreateWorkflow(
                workflow,
                user_info,
                transactionID);
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot create new Workflow");
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
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
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        ObjectMapper mapper = new ObjectMapper();
        WorkflowActivity workflow = new WorkflowActivity();
        try {
            workflow = mapper.readValue(payload, WorkflowActivity.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot parse payload");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
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
//        try {
        return CreateWorkflowActivity.processingCreateWorkflowActivity(
                workflow,
                user_info,
                transactionID);
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot create new Workflow Activity");
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

    public static InternalResponse verifyToken(
            final HttpServletRequest request,
            String transactionID) throws Exception {
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
            String transactionID) throws Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Check Data
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
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
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check valid data 
        InternalResponse result = null;
        try{
            result = CheckWorkflowTemplate.checkItem(data);
        } catch (Exception ex){
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }
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
//        try {
        return ProcessWorkflowActivity.assign(
                id,
                x_file_name,
                JWT,
                user_info,
                data,
                true,
                transactionID);
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogHandler.isShowErrorLog()) {
//                String message = "";
//                for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
//                    message = message + System.lineSeparator() + stackTraceElement.toString();
//                }
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot assign a new Workflow Activity - Details:"+message);
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

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
            result = CheckWorkflowTemplate.checkFile_Data(data);
            if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return result;
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot parse payload");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Processing
//        try {
        InternalResponse res = ProcessWorkflowActivity.processAuthen(
                user_info,
                id,
                jwt,
                data,
                headers,
                transactionID);
        if (res.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
            //Send mail
            sendMail((WorkflowActivity) res.getData(),
                    user_info,
                    email,
                    jwt,
                    transactionID);
        }
        return res;
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogHandler.isShowErrorLog()) {
//                String message = "";
//                for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
//                    message = message + System.lineSeparator() + stackTraceElement.toString();
//                }
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot assign a new Workflow Activity - Details:"+message);
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

    //Processing data activity
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
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
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
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
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
        JWT.setMath_result(false);
        if (headerJWT != null) {
            JWT = (JWT_Authenticate) ProcessEID_JWT.getInfoJWT(headerJWT).getData();
        }
        //Processing                
        return ProcessWorkflowActivity.process(
                id,
                x_file_name,
                JWT,
                user_info,
                data,
                true,
                transactionID);

    }

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
//        try {
        return GetDocument.getDocument(id, transactionID);
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogHandler.isShowErrorLog()) {
//                String message = "";
//                for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
//                    message = message + System.lineSeparator() + stackTraceElement.toString();
//                }
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot download a Document of Workflow Activity - Details:"+message);
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

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
//        try {
        InternalResponse res = GetDocument.getDocument(id, transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        res.setMessage(new ObjectMapper().writeValueAsString(res.getData()));
        return res;
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogHandler.isShowErrorLog()) {
//                String message = "";
//                for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
//                    message = message + System.lineSeparator() + stackTraceElement.toString();
//                }
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot get a Document Details - Details:"+message);
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

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
//        try {
        response = GetWorkflowDetail_option.getWorkflowDetail(id, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        return new InternalResponse(response.getStatus(),
                new ObjectMapper()
                        .enable(SerializationFeature.WRAP_ROOT_VALUE)
                        .writeValueAsString(response.getData()));
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogHandler.isShowErrorLog()) {
//                String message = "";
//                for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
//                    message = message + System.lineSeparator() + stackTraceElement.toString();
//                }
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot getWorkflowDetail - Details:"+message);
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

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
//        try {
        response = GetWorkflowActivity.getWorkflowActivityDetails(id, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        return new InternalResponse(response.getStatus(),
                new ObjectMapper()
                        .writeValueAsString(response.getData()));
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogHandler.isShowErrorLog()) {
//                String message = "";
//                for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
//                    message = message + System.lineSeparator() + stackTraceElement.toString();
//                }
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot getWorkflowAcDetail - Details:"+message);
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

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
            response.setData(data);
            return response;
        }
        if (value.equalsIgnoreCase("XSLT")) {
            response.setStatus(PaperlessConstant.HTTP_CODE_SUCCESS);
            response.setData(asset.getBinaryData());
            return response;
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN, "NOT PROVIDED");
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogHandler.isShowErrorLog()) {
//                String message = "";
//                for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
//                    message = message + System.lineSeparator() + stackTraceElement.toString();
//                }
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot downloadsAsset - Details:"+message);
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

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
            return response;
        }
        if (value.equalsIgnoreCase("XSLT")) {
            response.setStatus(PaperlessConstant.HTTP_CODE_SUCCESS);
            Asset assetTemp = new Asset();
            assetTemp.setBase64(Base64.getEncoder().encodeToString(asset.getBinaryData()));
            response.setData(assetTemp);
            return response;
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN, "NOT PROVIDED");
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogHandler.isShowErrorLog()) {
//                String message = "";
//                for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
//                    message = message + System.lineSeparator() + stackTraceElement.toString();
//                }
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot downloadsAsset - Details:"+message);
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

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
//        try {
        HashMap<Integer, String> hashmap = Resources.getListWorkflowTemplateTypeName();
        if (hashmap == null || hashmap.isEmpty()) {
            Resources.reloadListWorkflowTemplateTypeName();
            hashmap = Resources.getListWorkflowTemplateTypeName();
        }
        ArrayNode node = new ObjectMapper().createArrayNode();
        hashmap.forEach((key, value) -> {
            ObjectNode child = new ObjectMapper().createObjectNode();
            child.put("workflow_template_type_id", key);
            child.put("workflow_template_type_name", value);
            node.add(child);
        });
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, node);
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogHandler.isShowErrorLog()) {
//                String message = "";
//                for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
//                    message = message + System.lineSeparator() + stackTraceElement.toString();
//                }
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot getWorkflowTemplateType - Details:"+message);
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

    //Upload Asset
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
                String message = "";
                for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                    message = message + System.lineSeparator() + stackTraceElement.toString();
                }
                LogHandler.error(PaperlessService.class, transactionID, "Cannot upload an Asset - Details:" + message);
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }

        //Processing
//        try {
        int i = convertStringIntoAssetType(x_file_type);
        if (i == -1) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "File Type Asset is invalid");
            }
            String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_ASSET,
                    PaperlessConstant.SUBCODE_INVALID_FILE_TYPE,
                    "en",
                    null);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST, message);
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
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogHandler.isShowErrorLog()) {
//                String message = "";
//                for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
//                    message = message + System.lineSeparator() + stackTraceElement.toString();
//                }
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot upload an Asset --Details:"+message);
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

    //Upload Asset
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
        try {
            temp = new ObjectMapper().readValue(payload, Asset.class);
        } catch (Exception ex) {

        }
        byte[] temp2 = Base64.getDecoder().decode(temp.getBase64().getBytes());

        //Processing
//        try {
        int i = convertStringIntoAssetType(x_file_type);
        if (i == -1) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "File Type Asset is invalid");
            }
            String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_ASSET,
                    PaperlessConstant.SUBCODE_INVALID_FILE_TYPE,
                    "en",
                    null);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST, message);
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
                temp2,
                null
        );

        return UploadAsset.uploadAsset(user_info, asset, transactionID);
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogHandler.isShowErrorLog()) {
//                String message = "";
//                for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
//                    message = message + System.lineSeparator() + stackTraceElement.toString();
//                }
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot upload an Asset - Details:"+message);
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

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
//        try {
        response = GetAsset.getAssetTemplate(id, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, response.getMessage());
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogHandler.isShowErrorLog()) {
//                String message = "";
//                for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
//                    message = message + System.lineSeparator() + stackTraceElement.toString();
//                }
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot getAssetTemplate - Details:"+message);
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

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
//        try {
        return GetAsset.getAsset(id, transactionID);
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogHandler.isShowErrorLog()) {
//                String message = "";
//                for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
//                    message = message + System.lineSeparator() + stackTraceElement.toString();
//                }
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot getAssetDetails - Details:"+message);
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

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
        }

        String searchTemplate = "1,2,3,4,5,6,7,8";
        String template = Utils.getRequestHeader(request, "x-template-type");
        if (template != null) {
            template = new String(Base64.getDecoder().decode(template));
            switch (template) {
                case "QR GENERATOR": {
                    searchTemplate = "1";
                    break;
                }
                case "PDF GENERATOR": {
                    searchTemplate = "2";
                    break;
                }
                case "SIMPLE PDF STAMPING": {
                    searchTemplate = "3";
                    break;
                }
                case "PDF STAMPING": {
                    searchTemplate = "4";
                    break;
                }
                case "FILE STAMPING PROCESSING": {
                    searchTemplate = "5";
                    break;
                }
                case "LEI PDF STAMPING": {
                    searchTemplate = "6";
                    break;
                }
                case "E-LABOR CONTRACT": {
                    searchTemplate = "7";
                    break;
                }
                case "ESIGNCLOUD": {
                    searchTemplate = "8";
                    break;
                }
                default: {
                    searchTemplate = "1,2,3,4,5,6,7,8";
                    break;
                }
            }
        }
        //Processing
        InternalResponse res = GetWorkflow.getListWorkflow(
                user_info.getEmail(),
                user_info.getAid(), //enterprise_id
                status, //status
                searchTemplate, //workflow_type
                false, //enable metadata
                "", //value of metadata
                (page_no <= 1) ? 0 : (page_no - 1) * record, //offset
                record == 0 ? numberOfRecords : record, //rowcount
                transactionID);
        List<Workflow> list = (List<Workflow>) res.getData();
        CustomListWorkflowSerializer customSerializer = new CustomListWorkflowSerializer(
                list,
                page_no,
                record);
        res.setMessage(new ObjectMapper().writeValueAsString(customSerializer));
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("x-total-records", list.size());
        res.setHeaders(hashmap);
        return res;
    }

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
        String status = data[0];

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

        String filename = Utils.getRequestHeader(request, "x-search-text");
        String type = Utils.getRequestHeader(request, "x-search-type");

        //Processing
        InternalResponse res = GetAsset.getListAsset(
                user_info.getAid(),
                user_info.getEmail(),
                filename,
                type,
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        CustomListAssetSerializer tempp = new CustomListAssetSerializer((List<Asset>) res.getData(), 0, 0);
        res.setMessage(new ObjectMapper().writeValueAsString(tempp));
        return res;
    }

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

        String email_search = Utils.getRequestHeader(request, "x-search-email");

        //Processing
        InternalResponse res = GetWorkflowActivity.getListWorkflowActivity(
                user_info.getEmail(),
                user_info.getAid(),
                email_search, //email search
                null, //searcg date
                "1,2,3,4,5,6", //search generation
                search, //search status
                false,
                false,
                false,
                null, //search from date
                null, //search to date                
                "ENG", //language ENG - VIE
                (page_no <= 1) ? 0 : (page_no - 1) * record, //offset
                record == 0 ? numberOfRecords : record, //rowcount
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        List<WorkflowActivity> list = (List<WorkflowActivity>) res.getData();
        CustomListWoAcSerializer test = new CustomListWoAcSerializer(list, 0, 0);
        res.setMessage(new ObjectMapper().writeValueAsString(test));
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("x-total-records", list.size());
        res.setHeaders(hashmap);
        return res;
    }

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
//        try {
        response = GetWorkflowTemplate.getWorkflowTemplate(id, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        CustomWorkflowTemplateSerializer serializer = new CustomWorkflowTemplateSerializer((WorkflowTemplate) response.getData());
        response.setMessage(new ObjectMapper().writeValueAsString(serializer));
        return response;
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogHandler.isShowErrorLog()) {
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot get Workflow Template");
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

    //GET WORKFLOW
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
//        try {
        response = GetWorkflow.getWorkflow(id, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        return new InternalResponse(response.getStatus(), new ObjectMapper().writeValueAsString(response.getData()));
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot get a Workflow - Detail:" + e);
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

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
//        try {
        response = ManageStatusWorkflow.deactiveWorkflow(
                id,
                user_info.getEmail(),
                user_info.getAid(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        return new InternalResponse(response.getStatus(), "");
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot deactive Workflow");
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

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
//        try {
        response = ManageStatusWorkflow.reactiveWorkflow(
                id,
                user_info.getEmail(),
                user_info.getAid(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        return new InternalResponse(response.getStatus(), "");
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot reactive Workflow");
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

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
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(PaperlessService.class, transactionID, "No Payload found!!");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        ObjectMapper mapper = new ObjectMapper();
        WorkflowDetail_Option workflow = new WorkflowDetail_Option();
        try {
            workflow = mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE).readValue(payload, WorkflowDetail_Option.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot parse payload");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Processing
//        try {
        response = UpdateWorkflowDetail_option.updateWorkflowOption(
                id,
                user_info.getEmail(),
                user_info.getAid(),
                workflow,
                "hmac",
                user_info.getEmail(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        return response;
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot updateWorkflowDetail_option");
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

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
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(PaperlessService.class, transactionID, "No Payload found!!");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        ObjectMapper mapper = new ObjectMapper();
        Item_JSNObject workflowtemplate = new Item_JSNObject();
        try {
            workflowtemplate = mapper.readValue(payload, Item_JSNObject.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessService.class, transactionID, "Cannot parse payload");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check data
        InternalResponse res = UpdateWorkflowTemplate.checkWorkflowTemplate(workflowtemplate);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        //Processing
//        try {
        res = UpdateWorkflowTemplate.updateWorkflowTemplate(
                id,
                user_info.getEmail(),
                user_info.getAid(),
                workflowtemplate,
                "hmac",
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        return new InternalResponse(response.getStatus(), "");
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LogHandler.error(PaperlessService.class, transactionID, "Cannot updateWorkflowDetail_option");
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }

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
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_MISSING_OLD_OR_NEW_PASSWORD,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Processing
        return UpdateUser.updateUserPassword(
                user_info.getEmail(),
                old_password,
                new_password,
                transactionID);
    }

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

        //Get Header
        HashMap<String, String> headers = Utils.getHashMapRequestHeader(request);
        String headerJWT = headers.get("eid-jwt");
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
        ProcessWorkflowActivity_JSNObject data = new ProcessWorkflowActivity_JSNObject();
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
            LogHandler.error(PaperlessService.class, transactionID, "Cannot parse payload");
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }
        //Processing\
        InternalResponse res = ProcessWorkflowActivity.getDocumentHash(
                user_info,
                id,
                jwt,
                data,
                headers,
                transactionID);

        return res;
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
    private static int convertStringIntoAssetType(String assetType) throws Exception {
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
                    InternalResponse temp = null;
                    try {
                        temp = GetDocument.getDocument(
                                woAc.getId(),
                                transactionID);
                    } catch (Exception ex) {
                        Logger.getLogger(PaperlessService.class.getName()).log(Level.SEVERE, null, ex);
                    }

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
        temp.start();
    }
}
