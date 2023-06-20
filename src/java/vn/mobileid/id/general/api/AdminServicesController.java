/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessAdminService;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
//@WebServlet("/hello")
@Path("/")
public class AdminServicesController extends HttpServlet {

    @POST
    @Path("/v1/admin/accounts")
    public Response createAccount(
            @Context final HttpServletRequest request,
            String payload) {
        String transactionID="";
        try {
            InternalResponse response;
            
            transactionID = debugRequestLOG(
                    "createAccount",
                    request,
                    payload,
                    0);
            
            if (request.getContentType() == null) {
                return Response.status(400).entity("{Missing Content-Type}").build();
            }
            response = PaperlessAdminService.createAccount(
                    request,
                    payload,
                    transactionID);
            debugResponseLOG("createAccount", response);
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
                        "Error while create Account",
                        e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }

    @POST
    @Path("/v1/authenticate/sso")
    public Response loginSSO(
            @Context final HttpServletRequest request,
            String payload) {
        String transactionID="";
        try {
            InternalResponse response;
            
            transactionID = debugRequestLOG(
                    "loginSSO",
                    request,
                    payload,
                    0);            
            if (request.getContentType() == null) {
                return Response.status(400).entity("{Missing Content-Type}").build();
            }
            response = PaperlessAdminService.getTokenSSO(
                    request,
                    payload,
                    transactionID);
            debugResponseLOG("loginSSO", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(response.getStatus())
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
                        "Error while loging SSO",
                        e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    
    @POST
    @Path("/v1/authenticate/verify")
    public Response verifyUser(
            @Context final HttpServletRequest request,
            String payload) {
        String transactionID="";
        try {
            InternalResponse response;
            
            transactionID = debugRequestLOG(
                    "VerifyUser",
                    request,
                    payload,
                    0);            
            if (request.getContentType() == null) {
                return Response.status(400).entity("{Missing Content-Type}").build();
            }
            response = PaperlessAdminService.verifyEmail(
                    request,
                    payload,
                    transactionID);
            debugResponseLOG("VerifyUser", response);
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
                        "Error while verifying user",
                        e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    
    @GET
    @Path("/v1/admin/accounts/{var:.*}")
    public Response getAccount(
            @Context final HttpServletRequest request) {
        String transactionID="";
        try {
            InternalResponse response;
            transactionID = debugRequestLOG(
                    "getAccount",
                    request,
                    null,
                    0);            
            if (request.getContentType() == null) {
                return Response.status(400).entity("{Missing Content-Type}").build();
            }
            response = PaperlessAdminService.getAccounts(
                    request,
                    transactionID);
            debugResponseLOG("getAccount", response);
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(response.getStatus())
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
                        "Error while getting accounts",
                        e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    
    @POST
    @Path("/v1/admin/activation/resend")
    public Response resendActivation(
            @Context final HttpServletRequest request,
            String payload) {
        String transactionID="";
        try {
            InternalResponse response;
            
            transactionID = debugRequestLOG(
                    "resendActivation",
                    request,
                    payload,
                    0);
            
            if (request.getContentType() == null) {
                return Response.status(400).entity("{Missing Content-Type}").build();
            }
            response = PaperlessAdminService.resendActivation(
                    request,
                    payload,
                    transactionID);
            debugResponseLOG("resendActivation", response);
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
                        "Error while resending activation",
                        e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    
    @POST
    @Path("/v1/account/password/reset")
    public Response forgotPassword(
            @Context final HttpServletRequest request,
            String payload) {
        String transactionID="";
        try {
            InternalResponse response;
            
            transactionID = debugRequestLOG(
                    "forgotPassword",
                    request,
                    payload,
                    0);
            
            if (request.getContentType() == null) {
                return Response
                        .status(400)
                        .type(MediaType.APPLICATION_JSON)
                        .entity("{Missing Content-Type}")
                        .build();
            }
            
            response = PaperlessAdminService.forgotPassword(
                    request,
                    payload,
                    transactionID);
            debugResponseLOG("forgotPassword", response);
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
                        "Error while forgotting password",
                        e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{{Internal Server Error}}")
                    .build();
        }
    }
    
    @PUT
    @Path("/v1/account/password/new")
    public Response setNewPassword(
            @Context final HttpServletRequest request,
            String payload) {
        String transactionID="";
        try {
            InternalResponse response;
            
            transactionID = debugRequestLOG(
                    "setNewPassword",
                    request,
                    payload,
                    0);
            
            if (request.getContentType() == null) {
                return Response.status(400).type(MediaType.APPLICATION_JSON).entity("Missing Content-Type").build();
            }
            
            response = PaperlessAdminService.setNewPassword(
                    request,
                    payload,
                    transactionID);
            debugResponseLOG("setNewPassword", response);
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
                        "Error while setting new password",
                        e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}")
                    .build();
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
            data += "\n\tAuthorization:"+request.getHeader("Authorization");
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
}
