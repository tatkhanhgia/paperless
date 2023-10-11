/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.api;

import java.io.IOException;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.PaperlessService;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class AccountServiceController extends HttpServlet{
    
    public static void service_(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String method = req.getMethod();
        switch (method) {
            case "GET": {
                new AccountServiceController().doGet(req, res);
                break;
            }
//            case "POST": {
//                new AccountServiceController().doPost(req, res);
//                break;
//            }
            default: {
                res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                res.addHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
            }
        }
    }
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {          
        if (req.getRequestURI().matches("^/paperless/v2/account/organization$")) {
            String transactionId = debugRequestLOG("Get Organization", req, null, 0);
            LogHandler.request(AccountServiceController.class,
                    transactionId);
//            if (!Utils.isNullOrEmpty(req.getContentType()) && req.getContentType().equalsIgnoreCase("application/octet-stream")) {
            try {
                InternalResponse response = PaperlessService.getOrganization(req, transactionId);
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

            } catch (Exception ex) {
                LogHandler.error(AccountServiceController.class,
                        transactionId,
                        ex);
                Utils.sendMessage(
                        res,
                        PaperlessConstant.HTTP_CODE_500,
                        "application/json",
                        null);
            }
//            } else {
//                Utils.sendMessage(res, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, null);
//            }                
//            } else {
//                Utils.sendMessage(res, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, null);
//            }                
//            } else {
//                Utils.sendMessage(res, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, null);
//            }                
//            } else {
//                Utils.sendMessage(res, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, null);
//            }                
        } else {
            Utils.sendMessage(
                    res,
                    PaperlessConstant.HTTP_CODE_METHOD_NOT_ALLOWED,
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