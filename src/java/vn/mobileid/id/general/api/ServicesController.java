/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
import vn.mobileid.id.general.email.Email;
import vn.mobileid.id.general.email.EmailReq;
import vn.mobileid.id.general.objects.Attachment;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.QryptoService;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.objects.FileManagement;
import vn.mobileid.id.qrypto.objects.ListWorkflow;
import vn.mobileid.id.qrypto.objects.Workflow;

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
    
    //Test send mail
    @GET
    @Path("/sendmail")
    public Response sendmail(@Context final HttpServletRequest requests, String payload) throws IOException {
        EmailReq request = new EmailReq();
        request.setSendTo("giatk@mobile-id.vn");
        request.setSubject("SigedFilePDF");
        List<Attachment> attachs = new ArrayList<>();
        attachs.add(new Attachment(Files.readAllBytes(new File("D:\\NetBean\\qrypto\\file\\result.pdf").toPath()), "sIGNEDpdf"));
        request.setAttachments(attachs);
        request.setContent("test Content");
        request.setEntityName("EntityName");
        
        Email email = new Email(null);
        email.send(request);
        return Response.status(200).entity("").build();
    }

    // TOKEN ==============================================================
    // AUTHENTICATE
    @POST
    @Path("/v1/authenticate")
    public Response authenticateJSON(@Context final HttpServletRequest request, String payload) {
        try {
            InternalResponse response;
            //LOG FOR TESTING
            debugRequestLOG("Authenticate",request,payload);
            if(request.getContentType() == null){
                return Response.status(400).entity("Missing Content-Type").build();
            }
            if (request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON)) {
                response = QryptoService.getToken(request, payload, 0);
            } else {
                response = QryptoService.getToken(request, payload, 1);
            }
            debugResponseLOG("Authenticate",response);
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {                
                return Response
                        .status(200)
                        .entity(new ObjectMapper().writeValueAsString(response.getData()))
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
                e.printStackTrace();
                LOG.error("Error "+e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    // REVOKE TOKEN
    @DELETE
    @Path("/v1/tokens")
    public Response revokeToken(@Context final HttpServletRequest request, String payload) {
        try {
            InternalResponse response = null;
            if (request.getContentType().equalsIgnoreCase("application/json")) {
                response = QryptoService.revoke(request, payload, 0);
            } else {
                response = QryptoService.revoke(request, payload, 1);
            }
            
            debugResponseLOG("token", response);
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
                LOG.error("Error " + e);
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
            //Test
            debugRequestLOG("CreateWorkflow",request,payload);
            
            response = QryptoService.createWorkflow(request, payload);
            
            debugResponseLOG("CreateWorkflow", response);
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
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error ");
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
            //Test
            debugRequestLOG("Create Workflow Template",request,payload);
            
            response = QryptoService.createWorkflowTemplate(request, payload, id);
            
            debugResponseLOG("createWorkflowTemplate",response);
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
                LOG.error("Error ");
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //GET WORKFLOW OPTION/DETAIL
    @GET
    @Path("/v1/workflow/{id}/option")
    public Response getWorkflowDetail(@Context final HttpServletRequest request, @PathParam("id") int id) {
        try {
            InternalResponse response;
            debugRequestLOG("GetWorkflowDetail",request,null);
            response = QryptoService.getWorkflowDetail(request, id);

            debugResponseLOG("GetWorkflowDetail",response);
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(response.getMessage())
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
                e.printStackTrace();
                LOG.error("Error - Detail:" + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //GET WORKFLOW 
    @GET
    @Path("/v1/workflow/{id}/details")
    public Response getWorkflow(@Context final HttpServletRequest request, @PathParam("id") int id) {
        try {
            InternalResponse response;
            debugRequestLOG("getWorkflow",request,null);
            response = QryptoService.getWorkflow(request, id);

            debugResponseLOG("getWorkflow",response);
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(response.getMessage())
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
                e.printStackTrace();
                LOG.error("Error - Detail:" + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //GET WORKFLOW TEMPLATE TYPE
    @GET
    @Path("/v1/settings/templates")
    public Response getWorkflowTemplateType(@Context final HttpServletRequest request) {
        try {
            InternalResponse response;
            debugRequestLOG("Get WorkflowTemplateType",request,null);
            response = QryptoService.getWorkflowTemplateType(request);

            debugResponseLOG("getWorkflowTemplateType",response);
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();

                node.put("workflow_template_type", (ObjectNode) response.getData());

                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(mapper.writeValueAsString(node))
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
                e.printStackTrace();
                LOG.error("Error - Detail:" + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //GET ALL WORKFLOW
    @GET
    @Path("/v1/workflow/{document_status}/{var:.*}")
    public Response getAllWorkflow(@Context final HttpServletRequest request) {
        try {
            InternalResponse response;
            //LOG FOR TESTING
            debugRequestLOG("GetAllWorkflow",request,null);
            
            response = QryptoService.getListWorkflow(request);

            debugResponseLOG("GetAllWorkflow",response);
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(new ObjectMapper().writeValueAsString((ListWorkflow) response.getData()))
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
                e.printStackTrace();
                LOG.error("Error - Detail:" + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //GET WORKFLOW TEMPLATE
    @GET
    @Path("/v1/workflow/{id}/template")
    public Response getWorkflowTemplate(@Context final HttpServletRequest request, @PathParam("id") int id) {
        try {
            InternalResponse response;            
            //LOG FOR TESTING
            debugRequestLOG("GetWorkflowTemplate",request,null);
            
            response = QryptoService.getWorkflowTemplate(request, id);

            debugResponseLOG("getWorkflowTemplate",response);
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(response.getMessage())
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
                e.printStackTrace();
                LOG.error("Error - Detail:" + e);
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
            //LOG FOR TESTING
            debugRequestLOG("CreateWorkflowActivity",request,payload);
            
            response = QryptoService.createWorkflowActivity(request, payload);
            
            debugResponseLOG("CreateWorkflowActivity", response);
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
                LOG.error("Error - Detail:" + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    // Process Workflow activity
    @POST
    @Path("/v1/workflowactivity/{id}/process")
    public Response processWorkflowActivity(@Context final HttpServletRequest request, @PathParam("id") int id, String payload) {
        try {
            InternalResponse response;
            //LOG FOR TESTING
            debugRequestLOG("ProcessActivity",request,payload);
            
            response = QryptoService.processWorkflowActivity(request, payload, id);
            
            debugResponseLOG("ProcessActivity", response);
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
                e.printStackTrace();
                LOG.error("Error - Detail:" + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    // Get PDF file to authenticate by user
    @POST
    @Path("/v1/workflowactivity/{id}/assign")
    public Response assignDataWorkflowActivity(@Context final HttpServletRequest request, @PathParam("id") int id, String payload) {
        try {
            InternalResponse response;
            //LOG FOR TESTING
            debugRequestLOG("Assign",request,payload);
            
            response = QryptoService.assignDataIntoWorkflowActivity(request, payload, id);
            
            debugResponseLOG("Assign", response);
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
                e.printStackTrace();
                LOG.error("Error - Detail:" + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    // Get PDF file to authenticate by user
    @POST
    @Path("/v1/workflowactivity/{id}/processAssign")
    public Response processWorkflowActivityWithAuthenticate(@Context final HttpServletRequest request, @PathParam("id") int id, String payload) {
        try {
            InternalResponse response;
            //LOG FOR TESTING
            debugRequestLOG("ProcessAssign",request,null);
            
            response = QryptoService.processWorkflowActivityWithAuthen(request, payload, id);
            
            debugResponseLOG("processAssign",response);
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
                e.printStackTrace();
                LOG.error("Error - Detail:" + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //DOWNLOAD FILE=============================================================
    @GET
    @Path("/v1/workflowactivity/{id}")
    public Response downloadDocument(@Context final HttpServletRequest request, @PathParam("id") int id) {
        try {
            InternalResponse response;
            //LOG FOR TESTING
            debugRequestLOG("Download",request,null);
            response = QryptoService.downloadsDocument(request, id);

            debugResponseLOG("downloadDocument",response);
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                FileManagement file = (FileManagement) response.getData();
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_OCTET_STREAM)
                        .entity(file.getData())
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
                e.printStackTrace();
                LOG.error("Error - Detail:" + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @GET
    @Path("/v1/workflowactivity/{id}/base64")
    public Response downloadDocumentBase64(@Context final HttpServletRequest request, @PathParam("id") int id) {
        try {
            InternalResponse response;
            //LOG FOR TESTING
            debugRequestLOG("DownloadBase64",request,null);
            
            response = QryptoService.downloadsDocument(request, id);

            debugResponseLOG("DownloadBase64",response);
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                FileManagement file = (FileManagement) response.getData();
                String temp = "{" + Base64.getEncoder().encodeToString(file.getData()) + "}";
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(temp)
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
                e.printStackTrace();
                LOG.error("Error - Detail:" + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //ASSET==========================================================
    //Get Asset Template
    @GET
    @Path("/v1/asset/{id}/template")
    public Response getAssetTemplate(@Context final HttpServletRequest request, @PathParam("id") int id) {
        try {
            InternalResponse response;
            //LOG FOR TESTING
            debugRequestLOG("GetAssetTemplate",request,null);
            response = QryptoService.getAssetTemplate(request, id);

            debugResponseLOG("getAssetTemplate",response);
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(response.getMessage())
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
                e.printStackTrace();
                LOG.error("Error - Detail:" + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //DownloadAsset
    @GET
    @Path("/v1/asset/{id}")
    public Response downloadAsset(@Context final HttpServletRequest request, @PathParam("id") int id) {
        try {
            InternalResponse response;
            //LOG FOR TESTING
            debugRequestLOG("DownloadAsset",request,null);
            response = QryptoService.downloadsAsset(request, id);

            debugResponseLOG("DownloadAsset",response);
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_OCTET_STREAM)
                        .entity(response.getData())
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
                e.printStackTrace();
                LOG.error("Error - Detail:" + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //Upload Asset
    @GET
    @Path("/v1/asset")
    public Response uploadAsset(@Context final HttpServletRequest request) {
        try {
            InternalResponse response;
            //LOG FOR TESTING
            debugRequestLOG("Upload Asset",request,null);
            response = QryptoService.uploadAsset(request);

            debugResponseLOG("uploadAsset",response);
            if (response.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(response.getMessage())
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
                e.printStackTrace();
                LOG.error("Error - Detail:" + e);
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
            response = QryptoService.verifyToken(request);
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
                LOG.error("Error - Detail:" + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    public static void debugRequestLOG(String function,@Context final HttpServletRequest request, String payload) {
        LOG.info("\n---------\n"+function+" request:\n"+"\tMETHOD:" + request.getMethod()
                +"\n\tContentType:" + request.getContentType()
                +"\n\tBody:" + payload
        );
    }
    
    public static void debugResponseLOG(String function, InternalResponse response){
        LOG.info("\nRESPONSE:\n"+"\tStatus:"+response.getStatus()+"\n\tMessage:"+response.getMessage());
    }
}
