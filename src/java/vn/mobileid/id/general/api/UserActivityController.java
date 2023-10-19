/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
//@WebServlet("/hello")
public class UserActivityController extends HttpServlet {

    public static void service_(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String method = req.getMethod();
        switch (method) {
            case "GET": {
                new UserActivityController().doGet(req, res);
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
        //<editor-fold defaultstate="collapsed" desc="Get Category">
        try{
        if (req.getRequestURI().matches("^/paperless/v2/useractivity/category$")) {
            String transactionId = debugRequestLOG("Get Category", req, null, 0);
            LogHandler.request(
                    UserActivityController.class,
                    transactionId);

            InternalResponse response = PaperlessService.getCategory(req, transactionId);

            ServicesController.logIntoDB(
                    req,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser().getAid(),
                    0,
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "Get Category",
                    transactionId
            );

            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();

                node.put("categories", (ArrayNode) response.getData());
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

        //<editor-fold defaultstate="collapsed" desc="Get total record User Activity">
        if (req.getRequestURI().matches("^/paperless/v2/useractivity/gettotal.*$")) {
            String transactionId = debugRequestLOG("Get Total record UserActivity", req, null, 0);
            LogHandler.request(
                    UserActivityController.class,
                    transactionId);
            InternalResponse response = PaperlessService.getTotalRecordsUserActivity(req, transactionId);

            ServicesController.logIntoDB(
                    req,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser().getAid(),
                    0,
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "Get total records of User Activity",
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
        
        //<editor-fold defaultstate="collapsed" desc="Get details of User Activity">
        if (req.getRequestURI().matches("^/paperless/v2/useractivity/[0-9]+/details*$")) {
            int id = Integer.parseInt(req.getRequestURI().replace("/paperless/v2/useractivity/", "").replace("/details", ""));
            String transactionId = debugRequestLOG("Get details of UserActivity", req, null, id);
            LogHandler.request(
                    UserActivityController.class,
                    transactionId);
            InternalResponse response = PaperlessService.getUserActivityDetail(req, id, transactionId);

            ServicesController.logIntoDB(
                    req,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser().getAid(),
                    0,
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "Get details of User Activity",
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
        
        //<editor-fold defaultstate="collapsed" desc="Get total record User Activity">
        if (req.getRequestURI().matches("^/paperless/v2/useractivity/.*$")) {
            String transactionId = debugRequestLOG("Get List of UserActivity", req, null, 0);
            LogHandler.request(
                    UserActivityController.class,
                    transactionId);
            InternalResponse response = PaperlessService.getListUserActivity(req, transactionId);

            ServicesController.logIntoDB(
                    req,
                    response.getUser() == null ? "anonymous" : response.getUser().getEmail(),
                    response.getUser() == null ? 0 : response.getUser().getAid(),
                    0,
                    response.getStatus(),
                    "",
                    response.getMessage(),
                    "Get list User Activity",
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
                PaperlessConstant.HTTP_CODE_METHOD_NOT_ALLOWED,
                "application/json",
                null);
        } catch(Exception ex){
            LogHandler.error(UserActivityController.class,
                    "Error while \"GET\" in User Activity Controller",
                    ex);
            Utils.sendMessage(
                    res,
                    PaperlessConstant.HTTP_CODE_500,
                    "application/json",
                    null);
        }
    }
    

//========================INTERNAL METHOD==========================   
    //<editor-fold defaultstate="collapsed" desc="Get User">
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

    //<editor-fold defaultstate="collapsed" desc="Debug Request LOG">
    private static String debugRequestLOG(String function, @Context
final HttpServletRequest request, String payload, int id) {
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
        data += "\n\tBody (or ID):" + ServicesController.conclusionString(payload, id);

        LogHandler

.request(ServicesController.class  

, data);
        return transaction;
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
}
