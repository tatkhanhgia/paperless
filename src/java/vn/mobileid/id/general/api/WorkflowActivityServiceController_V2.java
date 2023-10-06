/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.PaperlessService;
import vn.mobileid.id.paperless.PaperlessService_V2;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class WorkflowActivityServiceController_V2 extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String transactionId = "";
        try {
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]*$")) {
                transactionId = debugRequestLOG("Download Document", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", ""));
                InternalResponse response = PaperlessService.downloadsDocument(req, id, transactionId);
                FileManagement file = (FileManagement) response.getData();
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/octet-stream",
                            file.getData());
                } else {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                }
                return;
            }
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]*/base64$")) {
                long start = System.currentTimeMillis();
                transactionId = debugRequestLOG("Download Document", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", "").replace("/base64", ""));
                InternalResponse response = PaperlessService.downloadsDocument(req, id, transactionId);
                long stop = System.currentTimeMillis();
                System.out.println("\n\tTime process:" + (stop - start));
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    FileManagement file = (FileManagement) response.getData();
                    String temp = "{\"file_data\":\"" + Base64.getEncoder().encodeToString(file.getData()) + "\"}";
                    Utils.sendMessage(res,
                            response.getStatus(),
                            "application/json",
                            temp);
                } else {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                }
                return;
            }
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]*/documentdetails$")) {
                transactionId = debugRequestLOG("Document Details", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", "").replace("/documentdetails", ""));
                InternalResponse response = PaperlessService.getDocumentDetails(req, id, transactionId);
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                } else {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                }
                return;
            }
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/gettotal.*$")) {
                transactionId = debugRequestLOG("Get Total record woAc", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                InternalResponse response = PaperlessService.getTotalRecordsWorkflowAc(req, transactionId);
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                } else {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                }
                return;
            }
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]+/details$")) {
                transactionId = debugRequestLOG("Get details woAc", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                String temp = req.getRequestURI().replace("/paperless/v1/workflowactivity/", "");
                temp = temp.replace("/details", "");
                InternalResponse response = PaperlessService.getWorkflowAcDetail(req, Integer.parseInt(temp), transactionId);
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                } else {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                }
                return;
            }
            if (req.getRequestURI().matches("^/paperless/v2/workflowactivity/report/base64/.*$")) {
                transactionId = debugRequestLOG("Get a report of woAc", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                InternalResponse response = PaperlessService_V2.getReportofWorkflowActivity(req, transactionId);
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    String temp = "{\"file_data\":\"" + Base64.getEncoder().encodeToString((byte[])response.getData()) + "\"}";
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            temp);
                } else {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                }
                return;
            }
            if (req.getRequestURI().matches("^/paperless/v2/workflowactivity/report/.*$")) {
                transactionId = debugRequestLOG("Get a report of woAc", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                InternalResponse response = PaperlessService_V2.getReportofWorkflowActivity(req, transactionId);
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/octet-stream",
                            response.getData());
                } else {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                }
                return;
            }
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity.*$")) {
                transactionId = debugRequestLOG("Get List woAc", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                InternalResponse response = PaperlessService.getListWorkflowAc(req, transactionId);
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                } else {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                }
                return;
            }

        } catch (Exception ex) {
            LogHandler.error(WorkflowActivityServiceController_V2.class,
                    transactionId,
                    ex);
            Utils.sendMessage(
                    res,
                    PaperlessConstant.HTTP_CODE_500,
                    "application/json",
                    null);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String transactionId = "";
        try {
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity$")) {
                transactionId = debugRequestLOG("Create woAc", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                InternalResponse response = PaperlessService.createWorkflowActivity(req, Utils.getPayload(req), transactionId);
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                } else {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                }
                return;
            }
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]*/assign$")) {
                transactionId = debugRequestLOG("Assign woAc", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", "").replace("/assign", ""));
                InternalResponse response = PaperlessService.assignDataIntoWorkflowActivity(req, Utils.getPayload(req), id, transactionId);
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                } else {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                }
                return;
            }
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]*/hash$")) {
                transactionId = debugRequestLOG("Get hash", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", "").replace("/hash", ""));
                InternalResponse response = PaperlessService.generateHashDocument(req, Utils.getPayload(req), id, transactionId);
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            new ObjectMapper().writeValueAsString(response.getData()));
                } else {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                }
                return;
            }
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]*/processAssign")) {
                transactionId = debugRequestLOG("Process woAc", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", "").replace("/processAssign", ""));
                InternalResponse response = PaperlessService.processWorkflowActivityWithAuthen(req, Utils.getPayload(req), id, transactionId);
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                } else {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                }
                return;
            }
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]*/process")) {
                transactionId = debugRequestLOG("Process woAc", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", "").replace("/process", ""));
                InternalResponse response = PaperlessService.processWorkflowActivity(req, Utils.getPayload(req), id, transactionId);
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                } else {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                }
                return;
            } else {
                Utils.sendMessage(
                        res,
                        PaperlessConstant.HTTP_CODE_METHOD_NOT_ALLOWED,
                        "application/json",
                        null);
            }
        } catch (Exception ex) {
            LogHandler.error(WorkflowActivityServiceController_V2.class,
                    transactionId,
                    ex);
            Utils.sendMessage(
                    res,
                    PaperlessConstant.HTTP_CODE_500,
                    "application/json",
                    null);
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String transactionId = "";
        try {
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]+/status$")) {
                transactionId = debugRequestLOG("Udpate woAc", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", "").replace("/status", ""));
                InternalResponse response = PaperlessService.updateWorkflowActivity(req, id, Utils.getPayload(req), transactionId);
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                } else {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                }
            } else {
                Utils.sendMessage(
                        res,
                        PaperlessConstant.HTTP_CODE_METHOD_NOT_ALLOWED,
                        "application/json",
                        null);
            }
        } catch (Exception ex) {
            LogHandler.error(WorkflowActivityServiceController_V2.class,
                    transactionId,
                    ex);
            Utils.sendMessage(
                    res,
                    PaperlessConstant.HTTP_CODE_500,
                    "application/json",
                    null);
        }
    }

    //========================INTERNAL METHOD==========================
    private static String conclusionString(String payload, int id) {
        String pattern = "\"value\":.*";
        if (payload == null) {
            return String.valueOf(id);
        }
        return payload.replaceAll(pattern, "\"value\":\"base64\"}]}");
    }

    private static String getUser(String payload) {
        String[] chunks = payload.split("\\.");

        if (chunks.length == 1) {
            try {
                payload = new String(Base64.getUrlDecoder().decode(payload), "UTF-8");
                chunks = payload.split(":");
                return chunks[0];
            } catch (Exception ex) {
                return "";
            }
        }

        try {
            payload = new String(Base64.getUrlDecoder().decode(chunks[1]), "UTF-8");
        } catch (Exception ex) {
            return "";
        }
        int begin = payload.indexOf("email");
        int end = payload.indexOf("azp");
        return payload.substring(begin + 8, end - 3);
    }

    private static String debugRequestLOG(String function, @Context final HttpServletRequest request, String payload, int id) {
        String data = "\n--------------------------\n" + function + " request:\n" + "\tMETHOD:" + request.getMethod()
                + "\n\tContentType:" + request.getContentType();
        String user = "";
        if (request.getHeader("Authorization") != null) {
            data += "\n\tAuthorization:" + request.getHeader("Authorization");
            user = getUser(request.getHeader("Authorization"));
            data += "\n\tUser:" + user;
        }
        if (user.isEmpty()) {
            user = "@username";
        }
        String transaction = Utils.generateTransactionId(user);
        data += "\n\tTransactionID:" + transaction;
        if (request.getHeader("x-send-mail") != null) {
            data += "\n\tSendMail:" + request.getHeader("x-send-mail");
        }
        data += "\n\tBody (or ID):" + conclusionString(payload, id);

        LogHandler.request(ServicesController.class, data);
        return transaction;
    }

    private static void debugResponseLOG(String function, InternalResponse response) {
        LogHandler.request(AdminServicesController.class, "\nRESPONSE:\n" + "\tStatus:" + response.getStatus() + "\n\tMessage:" + response.getMessage());
    }

    private static HttpServletResponse populateHttpServletResponse(
            Response response,
            HttpServletResponse res) throws IOException {
        if (response != null) {
            MultivaluedMap<String, Object> headers = response.getHeaders();
            headers.forEach((key, value) -> {
                res.setHeader(key, String.valueOf(value));
            });
            res.setStatus(response.getStatus());
            res.getWriter().write((String) response.getEntity());
            return res;
        } else {
            res.sendError(404);
            return res;
        }
    }
}