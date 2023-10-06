/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
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
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.general.Configuration;
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
            logIntoDB(
                    request,
                    "",
                    0, //id
                    0, //WoAc
                    response.getStatus(),
                    payload,
                    response.getMessage(),
                    "Authenticate",
                    transactionID
            );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(response.getStatus())
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
                    "Error while authentication",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{{Internal Server Error}}")
                    .build();
        }
    }

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
                    getUser(request.getHeader("Authorization")),
                    0, //id
                    0, //WoAc
                    response.getStatus(),
                    payload,
                    response.getMessage(),
                    "RevokeToken",
                    transactionID
            );
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
                    "Error while revoking token ",
                    e);
            return Response.status(PaperlessConstant.HTTP_CODE_500).entity("{Internal Server Error}").build();
        }
    }

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
                    getUser(request.getHeader("Authorization")),
                    0, //id
                    0, //WoAc
                    response.getStatus(),
                    payload,
                    response.getMessage(),
                    "CreateWorkflow",
                    transactionID
            );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(response.getStatus())
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
                    "Error while creating workflow",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}").build();
        }
    }

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

    //GET WORKFLOW OPTION/DETAIL
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
                    getUser(request.getHeader("Authorization")),
                    0, //id
                    id, //WoAc
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "GetWorkflowDetail",
                    transactionID
            );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(response.getMessage())
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
                    "Error while getting workflow detail",
                    e);

            return Response.status(
                    PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }

    //GET WORKFLOW 
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
                    getUser(request.getHeader("Authorization")),
                    0, //id
                    id, //WoAc
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "GetWorkflow",
                    transactionID
            );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(response.getStatus())
                        .type(MediaType.APPLICATION_JSON)
                        .entity(response.getMessage())
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
                    "Error while getting workflow",
                    e);
            return Response.status(PaperlessConstant.HTTP_CODE_500).entity("{Internal Server Error}").build();
        }
    }

    //GET WORKFLOW TEMPLATE TYPE
    @GET
    @Path("/v1/settings/templates")
    public Response getWorkflowTemplateType(
            @Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;
            transactionID = debugRequestLOG("Get WorkflowTemplateType", request, null, 0);
            response = PaperlessService.getWorkflowTemplateType(request, transactionID);

            debugResponseLOG("getWorkflowTemplateType", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();

                node.put("workflow_template_types", (ArrayNode) response.getData());
                logIntoDB(
                        request,
                        getUser(request.getHeader("Authorization")),
                        0, //id
                        0, //WoAc
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "GetWorkflowTemplateType",
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
                    getUser(request.getHeader("Authorization")),
                    0, //id
                    0, //WoAc
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "GetWorkflowTemplateType",
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

    //GET ALL WORKFLOW
    @GET
    @Path("/v1/workflow/{document_status}{var:.*}")
    public Response getListWorkflow(
            @Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("GetListWorkflow", request, null, 0);

            response = PaperlessService.getListWorkflow(request, transactionID);

            debugResponseLOG("GetListWorkflow", response);
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

    //GET ALL WORKFLOW with total
    @GET
    @Path("/v1/workflow/gettotal/{document_status}")
    public Response getTotalRecordsWorkflow(
            @Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("GetTotalWorkflow", request, null, 0);

            response = PaperlessService.getTotalRecordsWorkflow(request, transactionID);

            debugResponseLOG("GetTotalWorkflow", response);
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

    //GET WORKFLOW TEMPLATE
    @GET
    @Path("/v1/workflow/{id}/template")
    public Response getWorkflowTemplate(
            @Context final HttpServletRequest request,
            @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("GetWorkflowTemplate", request, String.valueOf(id), id);

            response = PaperlessService.getWorkflowTemplate(request, id, transactionID);

            debugResponseLOG("getWorkflowTemplate", response);
            logIntoDB(
                    request,
                    getUser(request.getHeader("Authorization")),
                    0, //id
                    id, //WoAc
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "GetWorkflowTemplate",
                    transactionID
            );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(response.getStatus())
                        .type(MediaType.APPLICATION_JSON)
                        .entity(response.getMessage())
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
                    "Error while getting workflow template ",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }

    // Process Workflow activity
    @POST
    @Path("/v1/workflowactivity/{id}/process")
    public Response processWorkflowActivity(
            @Context final HttpServletRequest request,
            @PathParam("id") int id,
            String payload) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("ProcessActivity", request, payload, id);

            response = PaperlessService.processWorkflowActivity(request, payload, id, transactionID);

            debugResponseLOG("ProcessActivity", response);
            logIntoDB(
                    request,
                    getUser(request.getHeader("Authorization")),
                    0, //id
                    id, //WoAc
                    response.getStatus(),
                    payload,
                    response.getMessage(),
                    "ProcessWorkflowActivity",
                    transactionID
            );
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
                    "Error while processing workflow activity",
                    e);
            return Response.status(PaperlessConstant.HTTP_CODE_500).entity("{Internal Server Error}").build();
        }
    }
        
    //Test Verify token
    @POST
    @Path("/v1/verifyToken")
    public Response verify(@Context final HttpServletRequest request, String payload) {
        String transactionID = "";
        try {
            InternalResponse response;
            response = PaperlessService.verifyToken(request, "transactionID");
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
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), transactionID, "Error " + e);
            }
            return Response.status(PaperlessConstant.HTTP_CODE_500).entity("{Internal Server Error}").build();
        }
    }

    @DELETE
    @Path("/v1/workflow/{id}/deactive")
    public Response deactiveWorkflow(
            @Context final HttpServletRequest request,
            @PathParam("id") int id) {
        String transactionID = "";
        try {
            transactionID = debugRequestLOG(
                    "deactiveWorkflow",
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
                    getUser(request.getHeader("Authorization")),
                    0, //id
                    id, //WoAc
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "DeactiveWorkflow",
                    transactionID
            );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(response.getStatus())
                        .type(MediaType.APPLICATION_JSON)
                        .entity(response.getMessage())
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
                    "Error while deactiving workflow",
                    e);
        }
        return Response
                .status(PaperlessConstant.HTTP_CODE_500)
                .entity("{Internal Server Error}")
                .build();
    }

    @PUT
    @Path("/v1/workflow/{id}/reactive")
    public Response reactiveWorkflow(
            @Context final HttpServletRequest request,
            @PathParam("id") int id) {
        String transactionID = "";
        try {
            transactionID = debugRequestLOG(
                    "reactiveWorkflow",
                    request,
                    null,
                    id);

            InternalResponse response = PaperlessService.reactiveWorkflow(
                    request,
                    id,
                    transactionID);

            debugResponseLOG("reactiveWorkflow", response);
            logIntoDB(
                    request,
                    getUser(request.getHeader("Authorization")),
                    0, //id
                    id, //WoAc
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "ReactiveWorkflow",
                    transactionID
            );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(response.getStatus())
                        .type(MediaType.APPLICATION_JSON)
                        .entity(response.getMessage())
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
                    "Error while reactiving workflow",
                    e);
        }
        return Response
                .status(PaperlessConstant.HTTP_CODE_500)
                .entity("{Internal Server Error}")
                .build();
    }

    @POST
    @Path("/v1/workflow/{id}/updateOption")
    public Response updateWorkflowOption(
            @Context final HttpServletRequest request,
            @PathParam("id") int id,
            String payload) {
        String transactionID = "";
        try {
            transactionID = debugRequestLOG(
                    "updateWorkflow",
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
                    getUser(request.getHeader("Authorization")),
                    0, //id
                    id, //WoAc
                    response.getStatus(),
                    payload,
                    response.getMessage(),
                    "UpdateWorkflowOption",
                    transactionID
            );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(response.getStatus())
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
                    "Error while updating workflow option",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }

  
    @POST
    @Path("/v1/workflow/{id}/updateTemplate")
    public Response updateWorkflowTemplate(
            @Context final HttpServletRequest request,
            @PathParam("id") int id, String payload) {
        String transactionID = "";
        try {
            transactionID = debugRequestLOG(
                    "updateWorkflowTemplate",
                    request,
                    null,
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
                    getUser(request.getHeader("Authorization")),
                    0, //id
                    id, //WoAc
                    response.getStatus(),
                    payload,
                    response.getMessage(),
                    "UpdateWorkflowTemplate",
                    transactionID
            );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(response.getStatus())
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
                    "Error while updating workflow template",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    
    @PUT
    @Path("/v2/workflow/{id}")
    public Response updateWorkflow(
            @Context final HttpServletRequest request,
            @PathParam("id") int id,
            String payload) {
        String transactionID = "";
        try {
            transactionID = debugRequestLOG(
                    "updateWorkflow",
                    request,
                    payload,
                    id);
            InternalResponse response = PaperlessService.updateWorkflow(request, id, payload, transactionID);

            debugResponseLOG(transactionID, response);
//            logIntoDB(
//                    request,
//                    getUser(request.getHeader("Authorization")),
//                    0, //id
//                    id, //WoAc
//                    response.getStatus(),
//                    payload,
//                    response.getMessage(),
//                    "UpdateWorkflow",
//                    transactionID
//            );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(response.getStatus())
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
                    "Error while updating workflow",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    
    @PUT
    @Path("/v1/settings/profile/password")
    public Response changePassword(
            @Context final HttpServletRequest request,
            String payload) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG(
                    "changePassword",
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

            debugResponseLOG("changePassword", response);
            logIntoDB(
                    request,
                    getUser(request.getHeader("Authorization")),
                    0, //id
                    0, //WoAc
                    response.getStatus(),
                    payload,
                    response.getMessage(),
                    "ChangePassword",
                    transactionID
            );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(response.getStatus())
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
                    "Error while changing user password",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }

 
    @GET
    @Path("/v1/settings/templates/asset")
    public Response getAssetTemplateType(
            @Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;
            transactionID = debugRequestLOG("GetAssetTemplateType", request, null, 0);
            response = PaperlessService.getAssetType(request, transactionID);

            debugResponseLOG("GetAssetTemplateType", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();

                node.put("asset_template_types", (ArrayNode) response.getData());
                logIntoDB(
                    request,
                    getUser(request.getHeader("Authorization")),
                    0, //id
                    0, //WoAc
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "GetAssetTemplateType",
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
                    getUser(request.getHeader("Authorization")),
                    0, //id
                    0, //WoAc
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "GetAssetTemplateType",
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

    //========================INTERNAL METHOD==========================
    private static String debugRequestLOG(
            String function,
            @Context final HttpServletRequest request,
            String payload,
            int id) {
        String data = "\n----------------------------\n" + function + " request :\n" + "\tMETHOD:" + request.getMethod()
                + "\n\tContentType : " + request.getContentType();
        String user = "@Account";

        if (request.getHeader("Authorization") != null) {
            user = getUser(request.getHeader("Authorization"));
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

    private static void logIntoDB(
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
                "2023.06.15", //version
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
    
    @GET
    @Path("/v1/settings/profile")
    public Response getAccountDetails(
            @Context final HttpServletRequest request) {
        String transactionID="";
        try {
            InternalResponse response;
            transactionID = debugRequestLOG(
                    "getAccountDetails",
                    request,
                    null,
                    0);            
            if (request.getContentType() == null) {
                return Response.status(400).entity("{Missing Content-Type}").build();
            }
            response = PaperlessService.getAccounts(
                    request,
                    transactionID);
            debugResponseLOG("getAccount", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(response.getStatus())
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
                        "Error while getting accounts",
                        e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
}