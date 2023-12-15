/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.PaperlessService;
import vn.mobileid.id.paperless.PaperlessService_V2;
import vn.mobileid.id.paperless.kernel_v2.CreateUserActivity;
import vn.mobileid.id.paperless.kernel_v2.GetWorkflowActivity;
import vn.mobileid.id.paperless.object.enumration.Category;
import vn.mobileid.id.paperless.object.enumration.EventAction;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
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
            //<editor-fold defaultstate="collapsed" desc="Download Document">
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]+$")) {
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", ""));
                transactionId = debugRequestLOG("Download Document", req, null, id);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                InternalResponse response = PaperlessService.downloadsDocument(req, id, transactionId);

                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        id,
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Download workflow Activity",
                        transactionId
                );

                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    InternalResponse temp = GetWorkflowActivity.getWorkflowActivity(id, transactionId);
                    if (temp.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                        createUserActivity_withTransaction(
                                req,
                                response,
                                "Download Workflow Activity",
                                "Workflow Activity",
                                id,
                                EventAction.PDF_Download,
                                ((WorkflowActivity) temp.getData()).getTransaction(),
                                "Download Workflow Activity",
                                "Download Workflow Activity",
                                Category.WorkflowActivity);
                    } else {
                        LogHandler.error(WorkflowActivityServiceController_V2.class,
                                transactionId,
                                "Cannot get Workflow Activity => Cannot add User Activity at (WorkflowActivityServiceController.doGet.line79)");
                    }

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
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Download Document Base64">
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]+/base64$")) {
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", "").replace("/base64", ""));
                transactionId = debugRequestLOG("Download Document", req, null, id);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                InternalResponse response = PaperlessService.downloadsDocument(req, id, transactionId);

                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        id,
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Download workflow Activity",
                        transactionId
                );

                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    InternalResponse temp = GetWorkflowActivity.getWorkflowActivity(id, transactionId);
                    if (temp.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                        createUserActivity_withTransaction(
                                req,
                                response,
                                "Download Workflow Activity",
                                "Workflow Activity",
                                id,
                                EventAction.PDF_Download,
                                ((WorkflowActivity) temp.getData()).getTransaction(),
                                "Download Workflow Activity",
                                "Download Workflow Activity",
                                Category.WorkflowActivity);
                    } else {
                        LogHandler.error(WorkflowActivityServiceController_V2.class,
                                transactionId,
                                "Cannot get Workflow Activity => Cannot add User Activity at (WorkflowActivityServiceController.doGet.line136)");
                    }

                    String temp_ = "{\"file_data\":\"" + Base64.getEncoder().encodeToString((byte[]) response.getData()) + "\"}";
                    Utils.sendMessage(res,
                            response.getStatus(),
                            "application/json",
                            temp_);
                } else {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                }
                return;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Document Details">
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]+/documentdetails$")) {
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", "").replace("/documentdetails", ""));
                transactionId = debugRequestLOG("Document Details", req, null, id);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                InternalResponse response = PaperlessService.getDocumentDetails(req, id, transactionId);

                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        id,
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get Document details of Workflow Activity",
                        transactionId
                );

                Utils.sendMessage(
                        res,
                        response.getStatus(),
                        "application/json",
                        response.getMessage());
                return;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get total record WoAC">
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/gettotal.*$")) {
                transactionId = debugRequestLOG("Get Total record woAc", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                InternalResponse response = PaperlessService.getTotalRecordsWorkflowAc(req, transactionId);

                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        0,
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get total records of Workflow Activity",
                        transactionId
                );

                Utils.sendMessage(
                        res,
                        response.getStatus(),
                        "application/json",
                        response.getMessage());
                return;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get Details of WOAC">
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]+/details$")) {
                String temp = req.getRequestURI().replace("/paperless/v1/workflowactivity/", "");
                temp = temp.replace("/details", "");
                int id = Integer.parseInt(temp);
                transactionId = debugRequestLOG("Get details woAc", req, null, id);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                InternalResponse response = PaperlessService.getWorkflowAcDetail(req, id, transactionId);

                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        id,
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get details of Workflow Activity",
                        transactionId
                );

                Utils.sendMessage(
                        res,
                        response.getStatus(),
                        "application/json",
                        response.getMessage());
                return;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get a report base64 of WOAC">
            if (req.getRequestURI().matches("^/paperless/v2/workflowactivity/report/base64/.*$")) {
                transactionId = debugRequestLOG("Get a report of woAc", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                InternalResponse response = PaperlessService_V2.getReportofWorkflowActivity(req, transactionId);

                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        0,
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get Report of Workflow Activity",
                        transactionId
                );

                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    String temp = "{\"file_data\":\"" + Base64.getEncoder().encodeToString((byte[]) response.getData()) + "\"}";
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
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get a report of WOAC">
            if (req.getRequestURI().matches("^/paperless/v2/workflowactivity/report/.*$")) {
                transactionId = debugRequestLOG("Get a report of woAc", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                InternalResponse response = PaperlessService_V2.getReportofWorkflowActivity(req, transactionId);

                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        0,
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get Document details of Workflow Activity",
                        transactionId
                );

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
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get GenerationType">
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/type$")) {
                transactionId = debugRequestLOG("Get Generation Type", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                InternalResponse response = PaperlessService.getGenerationType(req, transactionId);

                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        0,
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get Generation Type",
                        transactionId
                );

                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode node = mapper.createObjectNode();

                    node.put("generation_types", (ArrayNode) response.getData());
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            mapper.writeValueAsString(node));
                } else {
                    Utils.sendMessage(
                            res,
                            response.getStatus(),
                            "application/json",
                            response.getMessage());
                }
                return;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get List WOAC">
            String matcher = "^/paperless/v1/workflowactivity/(ALL|all|ACTIVE|active|HIDDEN|hidden|EXPIRED|expired|REVOKE|revoke){1}(/){0,1}[0-9]*(/){0,1}[0-9]*$";
            if (req.getRequestURI().matches(matcher)) {
                transactionId = debugRequestLOG("Get List woAc", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                InternalResponse response = PaperlessService.getListWorkflowAc(req, transactionId);

                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        0,
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get List of Workflow Activity",
                        transactionId
                );

                Utils.sendMessage(
                        res,
                        response.getStatus(),
                        "application/json",
                        response.getMessage());
                return;
            }
            //</editor-fold>

            Utils.sendMessage(
                    res,
                    PaperlessConstant.HTTP_CODE_NOT_FOUND,
                    "application/json",
                    null);
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
            //<editor-fold defaultstate="collapsed" desc="Create Workflow Activity">
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity$")) {
                transactionId = debugRequestLOG("Create woAc", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                String payload = Utils.getPayload(req);

                InternalResponse response = PaperlessService.createWorkflowActivity(req, payload, transactionId);
                Long id = null;
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    id = (long) response.getData();
                }
                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        id == null ? 0 : id.intValue(),
                        response.getStatus(),
                        payload,
                        response.getMessage(),
                        "Create workflow Activity",
                        transactionId
                );

                Utils.sendMessage(
                        res,
                        response.getStatus(),
                        "application/json",
                        response.getMessage());
                return;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Create Workflow Activity for CSVs">
            if (req.getRequestURI().matches("^/paperless/v2/workflowactivity/csv$")) {
                transactionId = debugRequestLOG("Create woAc", req, null, 0);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                String payload = Utils.getPayload(req);
                InternalResponse response = PaperlessService.createWorkflowActivity_forCSV(req, payload, transactionId);
                Long id = 0L;
                try {
                    id = (long) response.getData();
                } catch (Exception ex) {
                }
                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        id.intValue(),
                        response.getStatus(),
                        payload,
                        response.getMessage(),
                        "Create workflow Activity",
                        transactionId
                );

                Utils.sendMessage(
                        res,
                        response.getStatus(),
                        "application/json",
                        response.getMessage());
                return;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Assign ">
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]*/assign$")) {
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", "").replace("/assign", ""));
                transactionId = debugRequestLOG("Assign woAc", req, null, id);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                String payload = Utils.getPayload(req);
                InternalResponse response = PaperlessService.assignDataIntoWorkflowActivity(req, payload, id, transactionId);

                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        id,
                        response.getStatus(),
                        payload,
                        response.getMessage(),
                        "Assign workflow Activity",
                        transactionId
                );
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    createUserActivity_withTransaction(
                            req,
                            response,
                            "Assign Workflow Activity",
                            "Workflow Activity",
                            id,
                            EventAction.Update,
                            ((WorkflowActivity) response.getData()).getTransaction(),
                            "Assign Workflow Activity",
                            "Assign Workflow Activity",
                            Category.WorkflowActivity);
                }

                Utils.sendMessage(
                        res,
                        response.getStatus(),
                        "application/json",
                        response.getMessage());
                return;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get Hash">
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]*/hash$")) {
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", "").replace("/hash", ""));
                transactionId = debugRequestLOG("Get hash", req, null, id);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                String payload = Utils.getPayload(req);
                InternalResponse response = PaperlessService.generateHashDocument(req, payload, id, transactionId);

                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        id,
                        response.getStatus(),
                        payload,
                        response.getMessage(),
                        "Get Hash workflow Activity",
                        transactionId
                );

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
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Process with Authentication">
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]*/processAssign")) {
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", "").replace("/processAssign", ""));
                transactionId = debugRequestLOG("Process woAc", req, null, id);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                String payload = Utils.getPayload(req);
                InternalResponse response = PaperlessService.processWorkflowActivityWithAuthen(req, payload, id, transactionId);

                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        id,
                        response.getStatus(),
                        payload,
                        response.getMessage(),
                        "Process workflow Activity",
                        transactionId
                );
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    createUserActivity_withTransaction(
                            req,
                            response,
                            "Process Workflow Activity",
                            "Workflow Activity",
                            id,
                            EventAction.Run,
                            ((WorkflowActivity) response.getData()).getTransaction(),
                            "Process Workflow Activity",
                            "Process Workflow Activity",
                            Category.WorkflowActivity);
                }

                Utils.sendMessage(
                        res,
                        response.getStatus(),
                        "application/json",
                        response.getMessage());

                return;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Process without Authentication For CSV">
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]*/process/csv")) {
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", "").replace("/process/csv", ""));
                transactionId = debugRequestLOG("Process woAc", req, null, id);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                String payload = Utils.getPayload(req);
                InternalResponse response = PaperlessService.processWorkflowActivity_forCSV(req, payload, id, transactionId);

                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        id,
                        response.getStatus(),
                        payload,
                        response.getMessage(),
                        "Process workflow Activity",
                        transactionId
                );
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    createUserActivity_withTransaction(
                            req,
                            response,
                            "Process Workflow Activity",
                            "Workflow Activity",
                            id,
                            EventAction.Run,
                            ((WorkflowActivity) response.getData()).getTransaction(),
                            "Process Workflow Activity",
                            "Process Workflow Activity",
                            Category.WorkflowActivity);
                }
                Utils.sendMessage(
                        res,
                        response.getStatus(),
                        "application/json",
                        response.getMessage());
                return;
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Process without Authentication">
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]*/process")) {
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", "").replace("/process", ""));
                transactionId = debugRequestLOG("Process woAc", req, null, id);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);
                String payload = Utils.getPayload(req);

                InternalResponse response = PaperlessService.processWorkflowActivity(req, payload, id, transactionId);

                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        id,
                        response.getStatus(),
                        payload,
                        response.getMessage(),
                        "Process workflow Activity",
                        transactionId
                );
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    createUserActivity_withTransaction(
                            req,
                            response,
                            "Process Workflow Activity",
                            "Workflow Activity",
                            id,
                            EventAction.Run,
                            ((WorkflowActivity) response.getData()).getTransaction(),
                            "Process Workflow Activity",
                            "Process Workflow Activity",
                            Category.WorkflowActivity);
                }
                Utils.sendMessage(
                        res,
                        response.getStatus(),
                        "application/json",
                        response.getMessage());
                return;
            } else {
                Utils.sendMessage(
                        res,
                        PaperlessConstant.HTTP_CODE_METHOD_NOT_ALLOWED,
                        "application/json",
                        null);
            }
            //</editor-fold>

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
            //<editor-fold defaultstate="collapsed" desc="Update Workflow Activity">                        
            if (req.getRequestURI().matches("^/paperless/v1/workflowactivity/[0-9]+/status$")) {
                int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v1/workflowactivity/", "").replace("/status", ""));
                transactionId = debugRequestLOG("Update woAc", req, null, id);
                LogHandler.request(WorkflowActivityServiceController_V2.class,
                        transactionId);

                InternalResponse response = PaperlessService.updateWorkflowActivity(req, id, Utils.getPayload(req), transactionId);

                ServicesController.logIntoDB(
                        req,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        id,
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Update workflow Activity",
                        transactionId
                );
                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    ServicesController.createUserActivity(
                            req,
                            response,
                            "Update workflow Activity",
                            "Workflow Activity",
                            id,
                            EventAction.Update,
                            "Update workflow Activity",
                            "Update workflow Activity",
                            Category.WorkflowActivity);
                }

                Utils.sendMessage(
                        res,
                        response.getStatus(),
                        "application/json",
                        response.getMessage());
                //</editor-fold>

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
    //<editor-fold defaultstate="collapsed" desc="Conclusion String">
    private static String conclusionString(String payload, int id) {
        String pattern = "\"value\":.*";
        if (payload == null) {
            return String.valueOf(id);
        }
        return payload.replaceAll(pattern, "\"value\":\"base64\"}]}");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get User from Payload">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Debug request LOG">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Debug Response LOG">
    private static void debugResponseLOG(String function, InternalResponse response) {
        LogHandler.request(AdminServicesController.class, "\nRESPONSE:\n" + "\tStatus:" + response.getStatus() + "\n\tMessage:" + response.getMessage());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Populate Http Servlet Response">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create User Activity + User Log">
    public static void createUserActivity_withTransaction(
            HttpServletRequest request,
            InternalResponse response,
            String pMODULE,
            String pINFO_KEY, //Name of Parameter
            Object pINFO_VALUE, //Value of Parameter
            EventAction action,
            String transaction,
            String pDetail,
            String pDescription,
            Category category
    ) {
        User user = response.getUser();
        String data = null;
        if (pINFO_VALUE instanceof String) {
            data = (String) pINFO_VALUE;
        }
        if (pINFO_VALUE instanceof Integer) {
            data = String.valueOf((int) pINFO_VALUE);
        }
        if (pINFO_VALUE instanceof Long) {
            data = String.valueOf((long) pINFO_VALUE);
        }
        final String buffer = data;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CreateUserActivity.createUserActivity(
                            user.getEmail(),
                            user.getAid(),
                            pMODULE,
                            action,
                            pDetail,
                            pINFO_KEY,
                            buffer,
                            request.getRemoteAddr(),
                            pDescription,
                            null,
                            user.getEmail(),
                            transaction,
                            category,
                            action,
                            "");
                } catch (Exception ex) {
                    LogHandler.error(ServicesController.class,
                            "Cannot create User Activity",
                            ex);
                }
            }
        });
        thread.start();
    }
    //</editor-fold>
}
