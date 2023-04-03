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
import javax.ws.rs.POST;
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
    public Response createAccount(@Context final HttpServletRequest request, String payload) {
        try {
            InternalResponse response;
            //LOG FOR TESTING
            String transactionID = debugRequestLOG("createAccount", request, payload, 0);
            
            if (request.getContentType() == null) {
                return Response.status(400).entity("Missing Content-Type").build();
            }
            response = PaperlessAdminService.createAccount(request, payload, transactionID);
            debugResponseLOG("createAccount", response);
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
                e.printStackTrace();
                LogHandler.error(this.getClass(), "Error " + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @POST
    @Path("/v1/authenticate/sso")
    public Response loginSSO(@Context final HttpServletRequest request, String payload) {
        try {
            InternalResponse response;
            //LOG FOR TESTING
            String transactionID = debugRequestLOG("loginSSO", request, payload, 0);            
            if (request.getContentType() == null) {
                return Response.status(400).entity("Missing Content-Type").build();
            }
            response = PaperlessAdminService.getTokenSSO(request, payload, transactionID);
            debugResponseLOG("loginSSO", response);
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
            if (LogHandler.isShowErrorLog()) {
                e.printStackTrace();
                LogHandler.error(this.getClass(), "Error " + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }
    
    @POST
    @Path("/v1/authenticate/verify")
    public Response verifyUser(@Context final HttpServletRequest request, String payload) {
        try {
            InternalResponse response;
            //LOG FOR TESTING
            String transactionID = debugRequestLOG("VerifyUser", request, payload, 0);            
            if (request.getContentType() == null) {
                return Response.status(400).entity("Missing Content-Type").build();
            }
            response = PaperlessAdminService.verifyEmail(request, payload, transactionID);
            debugResponseLOG("VerifyUser", response);
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
                e.printStackTrace();
                LogHandler.error(this.getClass(), "Error " + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }
    
    @POST
    @Path("/v1/admin/accounts/{var:.*}")
    public Response getAccount(@Context final HttpServletRequest request) {
        try {
            InternalResponse response;
            //LOG FOR TESTING
            String transactionID = debugRequestLOG("getAccount", request, null, 0);            
            if (request.getContentType() == null) {
                return Response.status(400).entity("Missing Content-Type").build();
            }
            response = PaperlessAdminService.getAccounts(request, transactionID);
            debugResponseLOG("getAccount", response);
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
            if (LogHandler.isShowErrorLog()) {
                e.printStackTrace();
                LogHandler.error(this.getClass(), "Error " + e);
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }
    
    public static String debugRequestLOG(String function, @Context final HttpServletRequest request, String payload, int id) {
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
        data += "\n\tBody (or ID):" + conclusionString(payload, id);

        LogHandler.request(ServicesController.class, data);
        return transaction;
    }

    public static void debugResponseLOG(String function, InternalResponse response) {
        LogHandler.request(AdminServicesController.class, "\nRESPONSE:\n" + "\tStatus:" + response.getStatus() + "\n\tMessage:" + response.getMessage());
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
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(ServicesController.class, "Error while decode token!" + ex);
                }
                return "";
            }
        }       

        try {
            payload = new String(Base64.getUrlDecoder().decode(chunks[1]), "UTF-8");
        } catch (Exception ex) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(ServicesController.class, "Error while decode token!" + ex);
            }
            return "";
        }
        int begin = payload.indexOf("email");
        int end = payload.indexOf("azp");
        return payload.substring(begin + 8, end - 3);
    }
}
