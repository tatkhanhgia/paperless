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
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.PaperlessService_V2;
import vn.mobileid.id.paperless.object.enumration.Category;
import vn.mobileid.id.paperless.object.enumration.EventAction;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class UserServiceController_V2 extends HttpServlet {

    public static void service_(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String method = req.getMethod();
        switch (method) {
            case "GET": {
                new UserServiceController_V2().doGet(req, res);
                break;
            }
            case "PUT": {

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
        Utils.sendMessage(
                res,
                PaperlessConstant.HTTP_CODE_METHOD_NOT_ALLOWED,
                "application/json",
                null);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Utils.sendMessage(
                res,
                PaperlessConstant.HTTP_CODE_METHOD_NOT_ALLOWED,
                "application/json",
                null);
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (req.getRequestURI().matches("^/paperless/v2/settings/profile/general$")) {
            String transactionId = debugRequestLOG("update general profile", req, null, 0);
            LogHandler.request(UserServiceController_V2.class,
                    transactionId);
            try {
                InternalResponse response = PaperlessService_V2.updateGeneralProfile(
                        req,
                        Utils.getPayload(req),
                        transactionId);

                ServicesController.logIntoDB(
                        req,
                        response.getUser()==null?"anonymous":response.getUser().getEmail(),
                        response.getUser()==null?0:response.getUser().getAid(),
                        0,
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Update User information",
                        transactionId
                );

                if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                    ServicesController.createUserActivity(
                            req,
                            response,
                            "Update User information",
                            "User",
                            0,
                            EventAction.Update,
                            "Update User information",
                            "Update User information",
                            Category.Account);
                }

                Utils.sendMessage(
                        res,
                        response.getStatus(),
                        "application/json",
                        response.getMessage());

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

    private static void debugResponseLOG(String function, InternalResponse response) {
        LogHandler.request(AdminServicesController.class, "\nRESPONSE:\n" + "\tStatus:" + response.getStatus() + "\n\tMessage:" + response.getMessage());
    }
}
