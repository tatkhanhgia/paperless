/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Variant;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.email.Email;
import vn.mobileid.id.general.email.EmailReq;
import vn.mobileid.id.general.objects.Attachment;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessService;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.AppInfo;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
//@WebServlet("/hello")
@Path("/")
public class ServicesController {

//    final private static Logger LOG = LogManager.getLogger(ServicesController.class);
    @GET
    @Path("hello")
    public String hello() {
        return "hello";
    }

    // Test INFO
    @GET
    @Path("/v1/info")
    public Response getInfo(@Context final HttpServletRequest request, String payload) {
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
            return Response.status(200).type(MediaType.APPLICATION_JSON).entity(data).build();
        } catch (Exception e) {            
                LogHandler.error(
                        this.getClass(),
                        transactionID,
                        "Error while getting Info",
                        e);
        }
        return Response.status(500).entity("Internal Server Error").build();
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

    @POST
    @Path("/v1/authenticate")
    public Response authenticateJSON(@Context final HttpServletRequest request, String payload) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("Authenticate", request, payload, 0);
            if (request.getContentType() == null) {
                return Response.status(400).entity("Missing Content-Type").build();
            }
            if (request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON)) {
                response = PaperlessService.getToken(request, payload, 0, transactionID);
            } else {
                response = PaperlessService.getToken(request, payload, 1, transactionID);
            }
            debugResponseLOG("Authenticate", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
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
            LogHandler.error(
                        this.getClass(),
                        transactionID,
                        "Error while authentication",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    // REVOKE TOKEN
    @DELETE
    @Path("/v1/tokens")
    public Response revokeToken(@Context final HttpServletRequest request, String payload) {
        String transactionID = "";
        try {
            InternalResponse response = null;
            transactionID = debugRequestLOG("Revoke", request, payload, 0);
//            if (request.getContentType().equalsIgnoreCase("application/json")) {
            response = PaperlessService.revoke(request, payload, transactionID);
//            }
//            } else {
//                response = PaperlessService.revoke(request, payload, 1);
//            }

            debugResponseLOG("token", response);
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
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @POST
    @Path("/v1/workflow")
    public Response createWorkflow(@Context final HttpServletRequest request, String payload) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("CreateWorkflow", request, payload, 0);

            response = PaperlessService.createWorkflow(request, payload, transactionID);

            debugResponseLOG("CreateWorkflow", response);
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
                        "Error while creating workflow",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    // Create workflow template
    @POST
    @Path("/v1/workflow/{id}")
    public Response createWorkflowTemplate(@Context final HttpServletRequest request, @PathParam("id") int id, String payload) {
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
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //GET WORKFLOW OPTION/DETAIL
    @GET
    @Path("/v1/workflow/{id}/option")
    public Response getWorkflowDetail(@Context final HttpServletRequest request, @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;
            transactionID = debugRequestLOG("GetWorkflowDetail", request, null, id);
            response = PaperlessService.getWorkflowDetail(request, id, transactionID);

            debugResponseLOG("GetWorkflowDetail", response);
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
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //GET WORKFLOW 
    @GET
    @Path("/v1/workflow/{id}/details")
    public Response getWorkflow(@Context final HttpServletRequest request, @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;
            transactionID = debugRequestLOG("getWorkflow", request, null, id);
            response = PaperlessService.getWorkflow(request, id, transactionID);

            debugResponseLOG("getWorkflow", response);
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
                        "Error while getting workflow",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //GET WORKFLOW TEMPLATE TYPE
    @GET
    @Path("/v1/settings/templates")
    public Response getWorkflowTemplateType(@Context final HttpServletRequest request) {
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

                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(mapper.writeValueAsString(node))
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
                        "Error while getting workflow template type ",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //GET ALL WORKFLOW
    @GET
    @Path("/v1/workflow/{document_status}{var:.*}")
    public Response getListWorkflow(@Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("GetListWorkflow", request, null, 0);

            response = PaperlessService.getListWorkflow(request, transactionID);

            debugResponseLOG("GetListWorkflow", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                ResponseBuilder res = Response.status(200);
                res.type(MediaType.APPLICATION_JSON);
                for(String key : response.getHeaders().keySet()){
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
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //GET WORKFLOW TEMPLATE
    @GET
    @Path("/v1/workflow/{id}/template")
    public Response getWorkflowTemplate(@Context final HttpServletRequest request, @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("GetWorkflowTemplate", request, String.valueOf(id), id);

            response = PaperlessService.getWorkflowTemplate(request, id, transactionID);

            debugResponseLOG("getWorkflowTemplate", response);
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
                        "Error while getting workflow template ",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    // Create workflow activity
    @POST
    @Path("/v1/workflowactivity")
    public Response createWorkflowActivity(@Context final HttpServletRequest request, String payload) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("CreateWorkflowActivity", request, payload, 0);

            response = PaperlessService.createWorkflowActivity(request, payload, transactionID);

            debugResponseLOG("CreateWorkflowActivity", response);
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
                        "Error while getting workflow activity",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    // Process Workflow activity
    @POST
    @Path("/v1/workflowactivity/{id}/process")
    public Response processWorkflowActivity(@Context final HttpServletRequest request, @PathParam("id") int id, String payload) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("ProcessActivity", request, payload, id);

            response = PaperlessService.processWorkflowActivity(request, payload, id, transactionID);

            debugResponseLOG("ProcessActivity", response);
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
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    // Get PDF file to authenticate by user
    @POST
    @Path("/v1/workflowactivity/{id}/assign")
    public Response assignDataWorkflowActivity(@Context final HttpServletRequest request, @PathParam("id") int id, String payload) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("Assign", request, payload, id);

            response = PaperlessService.assignDataIntoWorkflowActivity(request, payload, id, transactionID);

            debugResponseLOG("Assign", response);
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
                        "Error while assigning data",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    // Get PDF file to authenticate by user
    @POST
    @Path("/v1/workflowactivity/{id}/processAssign")
    public Response processWorkflowActivityWithAuthenticate(@Context final HttpServletRequest request, @PathParam("id") int id, String payload) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("ProcessAssign", request, String.valueOf(id), id);

            response = PaperlessService.processWorkflowActivityWithAuthen(request, payload, id, transactionID);

            debugResponseLOG("processAssign", response);
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
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @GET
    @Path("/v1/workflowactivity/{id}")
    public Response downloadDocument(@Context final HttpServletRequest request, @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("Download", request, String.valueOf(id), id);
            response = PaperlessService.downloadsDocument(request, id, transactionID);

            debugResponseLOG("downloadDocument", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                FileManagement file = (FileManagement) response.getData();
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_OCTET_STREAM)
                        .entity(file.getData())
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
                        "Error while downloading document",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @GET
    @Path("/v1/workflowactivity/{id}/base64")
    public Response downloadDocumentBase64(@Context final HttpServletRequest request, @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("DownloadBase64", request, String.valueOf(id), id);

            response = PaperlessService.downloadsDocument(request, id, transactionID);

            debugResponseLOG("DownloadBase64", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                FileManagement file = (FileManagement) response.getData();
                String temp = "{" + Base64.getEncoder().encodeToString(file.getData()) + "}";
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(temp)
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
                        "Error while downloading document",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //Get Asset Template
    @GET
    @Path("/v1/asset/{id}/template")
    public Response getAssetTemplate(@Context final HttpServletRequest request, @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("GetAssetTemplate", request, String.valueOf(id), id);
            response = PaperlessService.getAssetTemplate(request, id, transactionID);

            debugResponseLOG("getAssetTemplate", response);
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
                        "Error while getting asset template",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @GET
    @Path("/v1/asset/{id}/details")
    public Response getAssetDetails(@Context final HttpServletRequest request, @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("GetAssetDetails", request, String.valueOf(id), id);
            response = PaperlessService.getAssetDetails(request, id, transactionID);

            debugResponseLOG("getAssetDetails", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(new ObjectMapper().writeValueAsString(response.getData()))
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
                        "Error while getting asset detail",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //DownloadAsset
    @GET
    @Path("/v1/asset/{id}")
    public Response downloadAsset(@Context final HttpServletRequest request, @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("DownloadAsset", request, String.valueOf(id), id);
            response = PaperlessService.downloadsAsset(request, id, transactionID);

            debugResponseLOG("DownloadAsset", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_OCTET_STREAM)
                        .entity(response.getData())
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
                        "Error while downloading asset",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @GET
    @Path("/v1/asset/{id}/base64")
    public Response downloadAssetBase64(@Context final HttpServletRequest request, @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("DownloadAsset", request, String.valueOf(id), id);
            response = PaperlessService.downloadsAssetBase64(request, id, transactionID);

            debugResponseLOG("DownloadAsset", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(new ObjectMapper().writeValueAsString(response.getData()))
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
                        "Error while downloading asset",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    //Upload Asset
    @POST
    @Path("/v1/asset")
    public Response uploadAsset(@Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("Upload Asset", request, null, 0);
            response = PaperlessService.uploadAsset(request, transactionID);

            debugResponseLOG("uploadAsset", response);
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
                        "Error while uploading asset",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @POST
    @Path("/v1/asset/base64")
    public Response uploadAssetBase64(@Context final HttpServletRequest request, String payload) {
        String transactionID = "";
        try {
            InternalResponse response;
            transactionID = debugRequestLOG("Upload Asset", request, null, 0);
            response = PaperlessService.uploadAssetBase64(request, payload, transactionID);

            debugResponseLOG("uploadAsset", response);
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
                        "Error while uploading asset",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
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
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @DELETE
    @Path("/v1/workflow/{id}/deactive")
    public Response deactiveWorkflow(@Context final HttpServletRequest request, String payload, @PathParam("id") int id) {
        String transactionID = "";
        try {
            transactionID = debugRequestLOG("deactiveWorkflow", request, payload, 0);

            InternalResponse response = PaperlessService.deactiveWorkflow(request, id, transactionID);

            debugResponseLOG("deactiveWorkflow", response);
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
                        "Error while deactiving workflow",
                        e);
        }
        return Response.status(500).entity("Internal Server Error").build();
    }

    @PATCH
    @Path("/v1/workflow/{id}/reactive")
    public Response reactiveWorkflow(@Context final HttpServletRequest request, String payload, @PathParam("id") int id) {
        String transactionID = "";
        try {
            transactionID = debugRequestLOG("reactiveWorkflow", request, payload, 0);

            InternalResponse response = PaperlessService.reactiveWorkflow(request, id, transactionID);

            debugResponseLOG("reactiveWorkflow", response);
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
                        "Error while reactiving workflow",
                        e);
        }
        return Response.status(500).entity("Internal Server Error").build();
    }

    @POST
    @Path("/v1/workflow/{id}/updateOption")
    public Response updateWorkflowOption(@Context final HttpServletRequest request, @PathParam("id") int id, String payload) {
        String transactionID = "";
        try {
            transactionID = debugRequestLOG("updateWorkflow", request, payload, id);
            InternalResponse response;
            response = PaperlessService.updateWorkflowDetail_option(request, id, payload, transactionID);

            debugResponseLOG(transactionID, response);
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
                        "Error while updating workflow option",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @POST
    @Path("/v1/workflow/{id}/updateTemplate")
    public Response updateWorkflowTemplate(@Context final HttpServletRequest request, @PathParam("id") int id, String payload) {
        String transactionID = "";
        try {
            transactionID = debugRequestLOG("updateWorkflowTemplate", request, null, id);
            InternalResponse response;
            response = PaperlessService.updateWorkflowTemplate(request, id, payload, transactionID);

            debugResponseLOG(transactionID, response);
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
                        "Error while updating workflow template",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @GET
    @Path("/v1/asset/{document_status}/{var:.*}")
    public Response getListAsset(@Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("GetListAsset", request, null, 0);

            response = PaperlessService.getListAsset(request, transactionID);

            debugResponseLOG("GetListAsset", response);
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
                        "Error while getting list of assets",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @GET
    @Path("/v1/workflowactivity/{document_status}/{var:.*}")
    public Response getListWorkflowActivity(@Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("GetListWorkflowAc", request, null, 0);

            response = PaperlessService.getListWorkflowAc(request, transactionID);

            debugResponseLOG("GetListWorkflowAc", response);
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
                        "Error while getting list of workflow activity",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @GET
    @Path("/v1/workflowactivity/{id}/details")
    public Response getWoAcDetails(@Context final HttpServletRequest request, @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("GetWoAcDetails", request, String.valueOf(id), id);
            response = PaperlessService.getWorkflowAcDetail(request, id, transactionID);

            debugResponseLOG("getWoAcDetails", response);
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
                        "Error while getting workflow activity details",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @GET
    @Path("/v1/workflowactivity/{id}/documentdetails")
    public Response getDocumentDetail(@Context final HttpServletRequest request, @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;
            transactionID = debugRequestLOG("GetDocumentDetail", request, null, id);
            response = PaperlessService.getDocumentDetails(request, id, transactionID);

            debugResponseLOG("GetDocumentDetail", response);
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
                        "Error while getting document details",
                        e);
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @PUT
    @Path("/v1/settings/profile/password")
    public Response changePassword(@Context final HttpServletRequest request, String payload) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("changePassword", request, payload, 0);
            if (request.getContentType() == null) {
                return Response.status(400).type(MediaType.APPLICATION_JSON).entity("Missing Content-Type").build();
            }
            response = PaperlessService.updateUserPassword(request, payload, transactionID);

            debugResponseLOG("changePassword", response);
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
                        "Error while changing user password",
                        e);
            return Response
                    .status(500)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("Internal Server Error")
                    .build();
        }
    }

    @POST
    @Path("/v1/workflowactivity/{id}/hash")
    public Response generateHashDocument(@Context final HttpServletRequest request, String payload, @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG("generateHashDocument", request, payload, id);

            response = PaperlessService.generateHashDocument(request, payload, id,transactionID);

            debugResponseLOG("generateHashDocument", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(200)
                        .entity(new ObjectMapper()
                                .configure(SerializationFeature.WRAP_ROOT_VALUE, true)
                                .writeValueAsString(response.getData()))
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
            return Response.status(500).entity("Internal Server Error").build();
        }
    }
    
    //========================INTERNAL METHOD==========================
    private static String debugRequestLOG(String function, @Context final HttpServletRequest request, String payload, int id) {
        String data = "\n------------------\n" + function + " request:\n" + "\tMETHOD:" + request.getMethod()
                + "\n\tContentType:" + request.getContentType();
        String user = "";
        if (request.getHeader("Authorization") != null) {
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
        data += "\n\tID:" + id;
        data += "\n\tBody (or ID):" + conclusionString(payload, id);

        LogHandler.request(ServicesController.class, data);
        return transaction;
    }

    private static void debugResponseLOG(String function, InternalResponse response) {
        LogHandler.request(ServicesController.class, "\nRESPONSE:\n" + "\tStatus:" + response.getStatus() + "\n\tMessage:" + response.getMessage());
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
}
