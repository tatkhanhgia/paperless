/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.QryptoService;
import vn.mobileid.id.qrypto.QryptoConstant;

/**
 *
 * @author GiaTK
 */
//@WebServlet("/hello")
@Path("/")
public class ServicesController {

    final private static Logger LOG = LogManager.getLogger(ServicesController.class);

    @GET
    @Path("hello")
    public String hello() {
        return "hello";
    }

    // Test INFO
    @GET
    @Path("/info")
    public Response getInfo(@Context final HttpServletRequest request, String payload) {
        return Response.status(200).entity("").build();
    }

    // TOKEN ==============================================================
    // AUTHENTICATE
    @POST
    @Path("/authenticate")
    public Response AuthenticateJSON(@Context final HttpServletRequest request, String payload) {
        try {
            InternalResponse response;
            if (request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON)) {
                response = QryptoService.getToken(request, payload, 0);
            } else {
                response = QryptoService.getToken(request, payload, 1);
            }
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
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
                LOG.error("Error Parsing ObjectMapper");
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    // REVOKE TOKEN
    @DELETE
    @Path("/tokens")
    public Response RevokeToken(@Context final HttpServletRequest request, String payload) {
        try {
            InternalResponse response = null;
            if (request.getContentType().equalsIgnoreCase("application/json")) {
                response = QryptoService.revoke(request, payload, 0);
            } else {
                response = QryptoService.revoke(request, payload, 1);
            }
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
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
                LOG.error("Error Parsing ObjectMapper" + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    // WORKFLOW ==========================================================
    // Create workflow
    @POST
    @Path("/v1/workflow")
    public Response createWorkflow(@Context final HttpServletRequest request, String payload) {
        try {
            InternalResponse response;
            response = QryptoService.createWorkflow(request, payload);
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Request fail");
                    LOG.debug("Status:" + response.getStatus());
                    LOG.debug("Message:" + response.getMessage());
                }
                return Response
                        .status(response.getStatus())
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error Parsing ObjectMapper");
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    // Create workflow template
    @POST
    @Path("/v1/workflow/{id}")
    public Response createWorkflowTemplate(@Context final HttpServletRequest request, @PathParam("id") int id, String payload) {
        try {
            InternalResponse response;
            response = QryptoService.createWorkflowTemplate(request, payload, id);
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Request fail");
                    LOG.debug("Status:" + response.getStatus());
                    LOG.debug("Message:" + response.getMessage());
                }
                return Response
                        .status(response.getStatus())
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error Parsing ObjectMapper");
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    // WORKFLOW ACTIVITY ===============================================
    // Create workflow activity
    @POST
    @Path("/v1/workflowactivity")
    public Response createWorkflowActivity(@Context final HttpServletRequest request, String payload) {
        try {
            InternalResponse response;
            response = QryptoService.createWorkflowActivity(request, payload);
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Request fail");
                    LOG.debug("Status:" + response.getStatus());
                    LOG.debug("Message:" + response.getMessage());
                }
                return Response
                        .status(response.getStatus())
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error - Detail:"+e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }
    
    // Process Workflow activity
    @POST
    @Path("/v1/workflowactivity/{id}/process")
    public Response processWorkflowActivity(@Context final HttpServletRequest request, @PathParam("id") int id,String payload) {
        try {
            InternalResponse response;
            response = QryptoService.processWorkflowActivity(request, payload, id);
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Request fail");
                    LOG.debug("Status:" + response.getStatus());
                    LOG.debug("Message:" + response.getMessage());
                }
                return Response
                        .status(response.getStatus())
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error - Detail:"+e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }
    
    //Test Verify token
    @POST
    @Path("/v1/verifyToken")
    public Response verify(@Context final HttpServletRequest request, String payload) {
        try {
            InternalResponse response;
            response = QryptoService.verifyToken(request, payload);
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Request fail");
                    LOG.debug("Status:" + response.getStatus());
                    LOG.debug("Message:" + response.getMessage());
                }
                return Response
                        .status(response.getStatus())
                        .entity(response.getMessage())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error Parsing ObjectMapper - Detail:"+e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }
    
}
