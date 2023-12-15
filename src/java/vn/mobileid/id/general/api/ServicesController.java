 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fmsclient.FMSClient;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST; 
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessService;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.kernel.CreateLogAPI;
import vn.mobileid.id.paperless.objects.AppInfo;
import vn.mobileid.id.general.Configuration;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.paperless.PaperlessService_V2;
import vn.mobileid.id.paperless.kernel_v2.CreateUserActivity;
import vn.mobileid.id.paperless.object.enumration.Category;
import vn.mobileid.id.paperless.object.enumration.EventAction;
import vn.mobileid.id.paperless.object.enumration.TemplateUserActivity;
import vn.mobileid.id.utils.HttpUtils;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
@Path("/")
public class ServicesController {

    @GET
    @Path("/v1/info")
    public Response getInfo(
            @Context final HttpServletRequest request,
            String payload) {
        String transactionID = "";
        try {
            //Check valid token
            String token = request.getHeader("Authorization");

            transactionID = debugRequestLOG("getInfo", request, payload, 0);

            String data = new ObjectMapper().writeValueAsString(AppInfo.cast(Configuration.getInstance().getAppInfo()));
            if (token != null) {
                InternalResponse response = PaperlessService.verifyToken(request, transactionID);
                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return Response.status(401).type(MediaType.APPLICATION_JSON).entity(response.getMessage()).build();
                }
            }

            return Response
                    .status(200)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(data)
                    .build();
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while getting Info",
                    e);
        }
        return Response
                .status(PaperlessConstant.HTTP_CODE_500)
                .entity("{{Internal Server Error}}")
                .build();
    }

    //<editor-fold defaultstate="collapsed" desc="Authenticate">
    @POST
    @Path("/v1/authenticate")
    public Response authenticateJSON(
            @Context final HttpServletRequest request,
            String payload) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG(
                    "Authenticate",
                    request,
                    payload,
                    0);

            if (request.getContentType() == null) {
                return Response
                        .status(400)
                        .entity("{Missing Content-Type}")
                        .build();
            }

            if (request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON)) {
                response = PaperlessService.getToken(
                        request,
                        payload,
                        0,
                        transactionID);
            } else {
                response = PaperlessService.getToken(
                        request,
                        payload,
                        1,
                        transactionID);
            }
            debugResponseLOG("Authenticate", response);
            String password = Utils.getFromJson("password", payload);
            logIntoDB(
                    request,
                    Utils.getFromJson("username", payload),
                    0, //id
                    0, //WoAc
                    response.getStatus(),
                    password != null ? payload.replace(password, "*****") : payload,
                    response.getMessage(),
                    "Authenticate",
                    transactionID
            );

            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                createUserActivity(
                        request,
                        response,
                        "Authorization",
                        null,
                        null,
                        EventAction.Login,
                        "Login into Service",
                        "Login",
                        Category.Account);
            }

            return Response
                    .status(response.getStatus())
                    .entity(response.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();

        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while authentication",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{{Internal Server Error}}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Revoke Token">
    // REVOKE TOKEN
    @DELETE
    @Path("/v1/tokens")
    public Response revokeToken(
            @Context final HttpServletRequest request,
            String payload) {
        String transactionID = "";
        try {
            InternalResponse response = null;
            transactionID = debugRequestLOG("Revoke", request, payload, 0);

            response = PaperlessService.revoke(request, payload, transactionID);

            debugResponseLOG("token", response);
            logIntoDB(
                    request,
                    "",
                    0,
                    0, //WoAc
                    response.getStatus(),
                    payload,
                    response.getMessage(),
                    "Revoke Token",
                    transactionID
            );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                createUserActivity(
                        request,
                        response,
                        "Revoke",
                        null,
                        null,
                        EventAction.Revoke,
                        "Revoke token",
                        "Revoke",
                        Category.Account);
            }
            return Response
                    .status(response.getStatus())
                    .entity(response.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();

        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while revoking token ",
                    e);
            return Response.status(PaperlessConstant.HTTP_CODE_500).entity("{Internal Server Error}").build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create Workflow">
    @POST
    @Path("/v1/workflow")
    public Response createWorkflow(
            @Context final HttpServletRequest request,
            String payload) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG(
                    "CreateWorkflow",
                    request,
                    payload,
                    0);

            response = PaperlessService.createWorkflow(
                    request,
                    payload,
                    transactionID);

            debugResponseLOG("CreateWorkflow", response);
            logIntoDB(
                    request,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser().getAid(), //id
                    0, //WoAc
                    response.getStatus(),
                    payload,
                    response.getMessage(),
                    "Create Workflow",
                    transactionID
            );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                createUserActivity(
                        request,
                        response,
                        "Create Workflow",
                        "Workflow",
                        Utils.getFromJson("workflow_id", response.getMessage()),
                        EventAction.New,
                        "Create new Workflow",
                        "Create new Workflow",
                        Category.Workflow);
            }
            return Response
                    .status(response.getStatus())
                    .entity(response.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while creating workflow",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}").build();
        }
    }
    //</editor-fold>

    // Create workflow template
    @POST
    @Path("/v1/workflow/{id}")
    public Response createWorkflowTemplate(
            @Context final HttpServletRequest request,
            @PathParam("id") int id,
            String payload) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("Create Workflow Template", request, payload, id);

            response = PaperlessService.createWorkflowTemplate(request, payload, id, transactionID);

            debugResponseLOG("createWorkflowTemplate", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            } else {
                return Response
                        .status(response.getStatus())
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            }
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while creating workflow template",
                    e);
            return Response.status(PaperlessConstant.HTTP_CODE_500).entity("{Internal Server Error}").build();
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Get Workflow Detail">
    @GET
    @Path("/v1/workflow/{id}/option")
    public Response getWorkflowDetail(
            @Context final HttpServletRequest request,
            @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;
            transactionID = debugRequestLOG("GetWorkflowDetail", request, null, id);
            response = PaperlessService.getWorkflowDetail(request, id, transactionID);

            debugResponseLOG("GetWorkflowDetail", response);
            logIntoDB(
                    request,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser() == null ? 0 : response.getUser().getAid(), //id
                    id, //WoAc
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "GetWorkflowDetail",
                    transactionID
            );

            return Response
                    .status(response.getStatus())
                    .entity(response.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while getting workflow detail",
                    e);

            return Response.status(
                    PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Workflow">
    @GET
    @Path("/v1/workflow/{id}/details")
    public Response getWorkflow(
            @Context final HttpServletRequest request,
            @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;
            transactionID = debugRequestLOG("getWorkflow", request, null, id);
            response = PaperlessService.getWorkflow(request, id, transactionID);

            debugResponseLOG("getWorkflow", response);
            logIntoDB(
                    request,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser().getAid(), //id
                    id, //WoAc
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "GetWorkflow",
                    transactionID
            );
            return Response
                    .status(response.getStatus())
                    .entity(response.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while getting workflow",
                    e);
            return Response.status(PaperlessConstant.HTTP_CODE_500).entity("{Internal Server Error}").build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Workflow Template Type">
    @GET
    @Path("/v1/settings/templates")
    public Response getWorkflowTemplateType(
            @Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;
            transactionID = debugRequestLOG("Get WorkflowTemplateType", request, null, 0);
            response = PaperlessService.getWorkflowTemplateType(request, transactionID);

            debugResponseLOG("Get Workflow Template Type", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();

                node.put("workflow_template_types", (ArrayNode) response.getData());
                logIntoDB(
                        request,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(), //id
                        0, //WoAc
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get Workflow Template Type",
                        transactionID
                );
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(mapper.writeValueAsString(node))
                        .build();
            } else {
                logIntoDB(
                        request,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(), //id
                        0, //WoAc
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get Workflow Template Type",
                        transactionID
                );
                return Response
                        .status(response.getStatus())
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            }
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while getting workflow template type ",
                    e);
            return Response.status(
                    PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}").build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Workflow Type">
    @GET
    @Path("/v1/workflow/type")
    public Response getWorkflowType(
            @Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;
            transactionID = debugRequestLOG("Get Workflow Type", request, null, 0);
            response = PaperlessService.getWorkflowType(request, transactionID);

            debugResponseLOG("Get Workflow Type", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();

                node.put("workflow_types", (ArrayNode) response.getData());
                logIntoDB(
                        request,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(), //id
                        0, //WoAc
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get Workflow Type",
                        transactionID
                );
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(mapper.writeValueAsString(node))
                        .build();
            } else {
                logIntoDB(
                        request,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(), //id
                        0, //WoAc
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get Workflow Type",
                        transactionID
                );
                return Response
                        .status(response.getStatus())
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            }
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while getting workflow type ",
                    e);
            return Response.status(
                    PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}").build();
        }
    }
    //</editor-fold>        

    //<editor-fold defaultstate="collapsed" desc="Get All Workflow">
    @GET
    @Path("/v1/workflow/{document_status}{var:.*}")
    public Response getListWorkflow(
            @Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("Get List Workflow", request, null, 0);

            response = PaperlessService.getListWorkflow(request, transactionID);

            debugResponseLOG("Get List Workflow", response);

            logIntoDB(
                    request,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser().getAid(), //id
                    0, //WoAc
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "Get List Workflow",
                    transactionID
            );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                ResponseBuilder res = Response.status(response.getStatus());
                res.type(MediaType.APPLICATION_JSON);
                for (String key : response.getHeaders().keySet()) {
                    res.header(key, response.getHeaders().get(key));
                }
                res.entity(response.getMessage());
                return res.build();
            } else {
                return Response
                        .status(response.getStatus())
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            }
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while getting list of workflow",
                    e);
            return Response.status(PaperlessConstant.HTTP_CODE_500).entity("{Internal Server Error}").build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Total record of Workflow">
    @GET
    @Path("/v1/workflow/gettotal/{document_status}")
    public Response getTotalRecordsWorkflow(
            @Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("Get Total Workflow", request, null, 0);

            response = PaperlessService.getTotalRecordsWorkflow(request, transactionID);

            debugResponseLOG("Get Total Workflow", response);
            logIntoDB(
                    request,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser().getAid(), //id
                    0, //WoAc
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "Get total records of Workflow",
                    transactionID
            );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                ResponseBuilder res = Response.status(response.getStatus());
                res.type(MediaType.APPLICATION_JSON);
                for (String key : response.getHeaders().keySet()) {
                    res.header(key, response.getHeaders().get(key));
                }
                res.entity(response.getMessage());
                return res.build();
            } else {
                return Response
                        .status(response.getStatus())
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            }
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while getting list of workflow",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}").build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Workflow Template">
    @GET
    @Path("/v1/workflow/{id}/template")
    public Response getWorkflowTemplate(
            @Context final HttpServletRequest request,
            @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("Get Workflow Template", request, null, id);

            response = PaperlessService.getWorkflowTemplate(request, id, transactionID);

            debugResponseLOG("Get Workflow Template", response);
            logIntoDB(
                    request,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser().getAid(), //id
                    id, //WoAc
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "Get Workflow Template",
                    transactionID
            );

            return Response
                    .status(response.getStatus())
                    .entity(response.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while getting workflow template ",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Deactive Workflow">
    @DELETE
    @Path("/v1/workflow/{id}/deactive")
    public Response deactiveWorkflow(
            @Context final HttpServletRequest request,
            @PathParam("id") int id) {
        String transactionID = "";
        try {
            transactionID = debugRequestLOG(
                    "Deactive Workflow",
                    request,
                    null,
                    id);

            InternalResponse response = PaperlessService.deactiveWorkflow(
                    request,
                    id,
                    transactionID);

            debugResponseLOG("deactiveWorkflow", response);

            logIntoDB(
                    request,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser().getAid(),
                    id, //WoAc
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "Deactive Workflow",
                    transactionID
            );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                createUserActivity(
                        request,
                        response,
                        "Deactive Workflow",
                        "Workflow",
                        id,
                        EventAction.Edit,
                        "Deactive a Workflow",
                        "Create new Workflow",
                        Category.Workflow);
            }
            return Response
                    .status(response.getStatus())
                    .entity(response.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();

        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while deactiving workflow",
                    e);
        }
        return Response
                .status(PaperlessConstant.HTTP_CODE_500)
                .entity("{Internal Server Error}")
                .build();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Reactive Workflow">
    @PUT
    @Path("/v1/workflow/{id}/reactive")
    public Response reactiveWorkflow(
            @Context final HttpServletRequest request,
            @PathParam("id") int id) {
        String transactionID = "";
        try {
            transactionID = debugRequestLOG(
                    "Reactive Workflow",
                    request,
                    null,
                    id);

            InternalResponse response = PaperlessService.reactiveWorkflow(
                    request,
                    id,
                    transactionID);

            debugResponseLOG("Reactive Workflow", response);
            logIntoDB(
                    request,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser().getAid(),
                    id, //WoAc
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "Reactive Workflow",
                    transactionID
            );

            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                createUserActivity(
                        request,
                        response,
                        "Reactive Workflow",
                        "Workflow",
                        id,
                        EventAction.Edit,
                        "Reactive a Workflow",
                        "Reactive a Workflow",
                        Category.Workflow);
            }

            return Response
                    .status(response.getStatus())
                    .entity(response.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();

        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while reactiving workflow",
                    e);
        }
        return Response
                .status(PaperlessConstant.HTTP_CODE_500)
                .entity("{Internal Server Error}")
                .build();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Update Workflow Option">
    @POST
    @Path("/v1/workflow/{id}/updateOption")
    public Response updateWorkflowOption(
            @Context final HttpServletRequest request,
            @PathParam("id") int id,
            String payload) {
        String transactionID = "";
        try {
            transactionID = debugRequestLOG(
                    "Update Workflow",
                    request,
                    payload,
                    id);
            InternalResponse response = PaperlessService.updateWorkflowDetail_option(
                    request,
                    id,
                    payload,
                    transactionID);

            debugResponseLOG(transactionID, response);
            logIntoDB(
                    request,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser().getAid(),
                    id, //WoAc
                    response.getStatus(),
                    payload,
                    response.getMessage(),
                    "Update Workflow Option",
                    transactionID
            );

            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                createUserActivity(
                        request,
                        response,
                        "Update Workflow Option",
                        "Workflow",
                        id,
                        EventAction.Update,
                        "Update a Workflow Option",
                        "Update a Workflow Option",
                        Category.Workflow);
            }

            return Response
                    .status(response.getStatus())
                    .entity(response.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while updating workflow option",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Update Workflow Template">
    @POST
    @Path("/v1/workflow/{id}/updateTemplate")
    public Response updateWorkflowTemplate(
            @Context final HttpServletRequest request,
            @PathParam("id") int id,
            String payload) {
        String transactionID = "";
        try {
            transactionID = debugRequestLOG(
                    "Update Workflow Template",
                    request,
                    payload,
                    id);
            InternalResponse response;
            response = PaperlessService.updateWorkflowTemplate(
                    request,
                    id,
                    payload,
                    transactionID);

            debugResponseLOG(transactionID, response);
            logIntoDB(
                    request,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser().getAid(),
                    id, //WoAc
                    response.getStatus(),
                    payload,
                    response.getMessage(),
                    "Update Workflow Template",
                    transactionID
            );

            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                createUserActivity(
                        request,
                        response,
                        "Update Workflow Template",
                        "Workflow",
                        id,
                        EventAction.Update,
                        "Update a Workflow Template",
                        "Update a Workflow Template",
                        Category.Workflow);
            }

            return Response
                    .status(response.getStatus())
                    .entity(response.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while updating workflow template",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Update Workflow">
    @PUT
    @Path("/v2/workflow/{id}")
    public Response updateWorkflow(
            @Context final HttpServletRequest request,
            @PathParam("id") int id,
            String payload) {
        String transactionID = "";
        try {
            transactionID = debugRequestLOG(
                    "Update Workflow",
                    request,
                    payload,
                    id);
            InternalResponse response = PaperlessService.updateWorkflow(request, id, payload, transactionID);

            debugResponseLOG(transactionID, response);
            logIntoDB(
                    request,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser().getAid(),
                    id, //WoAc
                    response.getStatus(),
                    payload,
                    response.getMessage(),
                    "Update Workflow",
                    transactionID
            );

            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                createUserActivity(
                        request,
                        response,
                        "Update Workflow",
                        "Workflow",
                        id,
                        EventAction.Update,
                        "Update a Workflow",
                        "Update a Workflow",
                        Category.Workflow);
            }

            return Response
                    .status(response.getStatus())
                    .entity(response.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while updating workflow",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Change Password">
    @PUT
    @Path("/v1/settings/profile/password")
    public Response changePassword(
            @Context final HttpServletRequest request,
            String payload) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG(
                    "Change Password",
                    request,
                    payload,
                    0);
            if (request.getContentType() == null) {
                return Response.status(400).type(MediaType.APPLICATION_JSON).entity("Missing Content-Type").build();
            }
            response = PaperlessService.updateUserPassword(
                    request,
                    payload,
                    transactionID);

            debugResponseLOG("Change Password", response);
            logIntoDB(
                    request,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser().getAid(),
                    0, //WoAc
                    response.getStatus(),
                    payload,
                    response.getMessage(),
                    "Change Password",
                    transactionID
            );

            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                createUserActivity(
                        request,
                        response,
                        "Change password of User",
                        "User",
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        EventAction.Update,
                        "Update new password for User",
                        "Update new password for User",
                        Category.Account);
            }

            return Response
                    .status(response.getStatus())
                    .entity(response.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while changing user password",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Asset Template Type">
    @GET
    @Path("/v1/settings/templates/asset")
    public Response getAssetTemplateType(
            @Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;
            transactionID = debugRequestLOG("Get Asset Template Type", request, null, 0);
            response = PaperlessService.getAssetType(request, transactionID);

            debugResponseLOG("Get Asset Template Type", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();

                node.put("asset_template_types", (ArrayNode) response.getData());
                logIntoDB(
                        request,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        0, //WoAc
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get Asset Template Type",
                        transactionID
                );
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(mapper.writeValueAsString(node))
                        .build();
            } else {
                logIntoDB(
                        request,
                        response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                        response.getUser() == null ? 0 : response.getUser().getAid(),
                        0, //WoAc
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get Asset Template Type",
                        transactionID
                );
                return Response
                        .status(response.getStatus())
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            }
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while getting asset template type ",
                    e);
            return Response.status(
                    PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}").build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Accoutn Details">
    @GET
    @Path("/v1/settings/profile")
    public Response getAccountDetails(
            @Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;
            transactionID = debugRequestLOG(
                    "Get Account Details",
                    request,
                    null,
                    0);
            if (request.getContentType() == null) {
                return Response.status(400).entity("{Missing Content-Type}").build();
            }
            response = PaperlessService.getAccounts(
                    request,
                    transactionID);
            debugResponseLOG("Get Account", response);

            return Response
                    .status(response.getStatus())
                    .entity(response.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while getting accounts",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Copy Workflow">
    @POST
    @Path("/v2/workflow/{id}/copy")
    public Response copyWorkflow(
            @Context final HttpServletRequest request,
            @PathParam("id") int id,
            String payload) {
        String transactionID = "";
        try {
            transactionID = debugRequestLOG(
                    "Copy Workflow",
                    request,
                    payload,
                    id);
            InternalResponse response = PaperlessService_V2.copyWorkflow(request, payload, id,transactionID);

            debugResponseLOG(transactionID, response);
            logIntoDB(
                    request,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser().getAid(),
                    id, //WoAc
                    response.getStatus(),
                    payload,
                    response.getMessage(),
                    "Copy Workflow",
                    transactionID
            );

            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                createUserActivity(
                        request,
                        response,
                        "Copy Workflow",
                        "Workflow",
                        id,
                        EventAction.Copy,
                        "Copy a Workflow",
                        "Copy a Workflow",
                        Category.Workflow);
            }

            return Response
                    .status(response.getStatus())
                    .entity(response.getMessage())
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while updating workflow",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>
    
    //========================INTERNAL METHOD==========================
    //<editor-fold defaultstate="collapsed" desc="Debug Request LOG">
    private static String debugRequestLOG(
            String function,
            @Context final HttpServletRequest request,
            String payload,
            int id) {
        String data = "\n----------------------------\n" + function + " request :\n" + "\tMETHOD:" + request.getMethod()
                + "\n\tContentType : " + request.getContentType();
        String user = "@Account";

        if (request.getHeader("Authorization") != null) {
//            user = getUser(request.getHeader("Authorization"));
            data += "\n\tUser : " + user;
        }

        String transaction = Utils.generateTransactionId(user);
        data += "\n\tTransactionID : " + transaction;
        if (request.getHeader("x-send-mail") != null) {
            data += "\n\tSendMail : " + request.getHeader("x-send-mail");
        }
        data += "\n\tID : " + id;
        data += "\n\tBody (or ID) : " + conclusionString(payload, id);

        LogHandler.request(ServicesController.class, data);
        return transaction;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Debug Response LOG">
    private static void debugResponseLOG(
            String function,
            InternalResponse response) {
        String message = null;
        if (response.getMessage() != null && response.getMessage().length() > 100) {
            message = "{data}";
        } else if (response.getMessage() != null) {
            message = response.getMessage();
        } else {
            message = "{none data response}";
        }
        LogHandler.request(
                ServicesController.class,
                "\nRESPONSE:\n" + "\tStatus : " + response.getStatus() + "\n\tMessage : " + message);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Conclusion String">
    public static String conclusionString(String payload, int id) {
        String pattern = "\"value\":.*";
        if (payload == null) {
            return String.valueOf(id);
        }
        return payload.replaceAll(pattern, "\"value\":\"base64\"}]}");
    }
    //</editor-fold>        

    //<editor-fold defaultstate="collapsed" desc="Log Into DB">
    public static void logIntoDB(
            HttpServletRequest requestHTTP,
            String email,
            int enterprise_id,
            int workflow_activity_id,
            int status,
            String request,
            String response,
            String servicename,
            String transactionID
    ) throws Exception {
        String http = requestHTTP.getMethod();
        String url = requestHTTP.getRequestURL().toString();
        String appname = HttpUtils.getClientBrowser(requestHTTP);
        String ip = HttpUtils.getClientIpAddr(requestHTTP);
        String os = HttpUtils.getClientOS(requestHTTP);
        CreateLogAPI.createLogAPI(
                email,
                enterprise_id,
                workflow_activity_id,
                appname + "_" + os, //app name
                ip, //api key
                "2023.10.18", //version
                servicename, //service name
                url,
                http,
                status,
                request,
                response,
                "HMAC",
                email,
                transactionID);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create User Activity + User Log">
    public static void createUserActivity(
            HttpServletRequest request,
            InternalResponse response,
            String pMODULE,
            String pINFO_KEY, //Name of Parameter
            Object pINFO_VALUE, //Value of Parameter
            EventAction action,
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
                            null,
                            category,
                            action,
                            "");
                } catch (Exception ex) {
//                    LogHandler.error(ServicesController.class,
//                            "Cannot create User Activity",
//                            ex);
                }
            }
        });
        thread.start();
    }
    //</editor-fold>

}
