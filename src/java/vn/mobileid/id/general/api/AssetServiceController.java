/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.PaperlessService;
import vn.mobileid.id.paperless.object.enumration.Category;
import vn.mobileid.id.paperless.object.enumration.EventAction;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
@Path("/")
public class AssetServiceController extends HttpServlet {

//     public void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
// 
//        // Set response content type
//        response.setContentType("text/html");
//   
//        PrintWriter out = response.getWriter();
//        String title = "Vi du doc thong tin HTTP Header";
//        String docType = "<!doctype html public \"-//w3c//dtd html 4.0 "
//                + "transitional//en\">\n";
// 
//        out.println(docType + "<html>\n" + "<head><title>" + title 
//                + "</title></head>\n"
//                + "<body bgcolor = \"#f0f0f0\">\n"
//                + "<h1 align = \"center\">" + title + "</h1>\n"
//                + "<table width = \"100%\" border = \"1\" align = \"center\">\n"
//                + "<tr bgcolor = \"#949494\">\n"
//                + "<th>Header Name</th><th>Header Value(s)</th>\n"
//                + "</tr>\n");
//   
//        // get header names
//        Enumeration headerNames = request.getHeaderNames();
// 
//        while (headerNames.hasMoreElements()) {
//            String paramName = (String) headerNames.nextElement();
//            out.print("<tr><td>" + paramName + "</td>\n");
//            String paramValue = request.getHeader(paramName);
//            out.println("<td> " + paramValue + "</td></tr>\n");
//        }
//        out.println("</table>\n</body></html>");
//    }
    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String method = req.getMethod();
        String pathInfo = req.getPathInfo();

        String delete_Regex = "/[0-9]+/deactive";
        String getTemplate_Regex = "/[0-9]+/template";
        String getDetail_Regex = "/[0-9]+/details";
        String download_Regex = "/[0-9]+";
        String downloadBase64_Regex = "/[0-9]+/base64";
        String upload_Regex = "";
        String uploadBase64_Regex = "/base64";
        String getList_Regex = "^/(ALL|INACTIVE|ACTIVE)[0-9/]*$";
        String getTotal_Regex = "^/gettotal/(ALL|INACTIVE|ACTIVE)[0-9/]*$";
        String getTemplateType_Regex = "";
        Response response = null;
        switch (method) {
            case "GET": {
                if (pathInfo.matches(getTemplate_Regex)) {
                    int id = Integer.parseInt(pathInfo
                            .replace("/template", "")
                            .substring(1));
                    response = this.getAssetTemplate(req, id);
                    break;
                }
                if (pathInfo.matches(getDetail_Regex)) {
                    int id = Integer.parseInt(pathInfo
                            .replace("/details", "")
                            .substring(1));
                    response = this.getAssetDetails(req, id);
                    break;
                }
                if (pathInfo.matches(download_Regex)) {
                    int id = Integer.parseInt(pathInfo.substring(1));
                    response = this.downloadAsset(req, id);
                    break;
                }
                if (pathInfo.matches(downloadBase64_Regex)) {
                    int id = Integer.parseInt(pathInfo
                            .replace("/base64", "")
                            .substring(1));
                    response = this.downloadAssetBase64(req, id);
                    break;
                }
                if (pathInfo.matches(getTotal_Regex)) {
                    response = this.getTotalRecordsAsset(req);
                    break;
                }
                if (pathInfo.matches(getList_Regex)) {
                    response = this.getListAsset(req);
                    break;
                }
                break;
            }
            case "DELETE": {
                if (pathInfo.matches(delete_Regex)) {
                    String temp = pathInfo.replaceAll("/deactive", "");
                    int id = Integer.parseInt(temp.substring(1));
                    response = this.deleteAsset(req, id);
                }
                break;
            }
            case "POST": {
                if (pathInfo == null) {
                    response = this.uploadAsset(req);
                    break;
                }
                if (pathInfo.matches(uploadBase64_Regex)) {
//                    int id = Integer.parseInt(pathInfo
//                            .replace("/base64", "")
//                            .substring(1));
                    response = this.uploadAssetBase64(req, getBody(req));
                }
                break;
            }
            case "PUT": {
                if (pathInfo != null && pathInfo.matches("/[0-9]+")) {
                    int id = Integer.parseInt(pathInfo.substring(1));
                    response = this.updateAsset(req,id);
                    break;
                }
                if (pathInfo != null && pathInfo.matches("/[0-9]+/base64")) {
                    int id = Integer.parseInt(pathInfo
                            .replace("/base64", "")
                            .substring(1));
                    response = this.updateAssetBase64(req,id);
                    break;
                }
            }
        }
        res = populateHttpServletResponse(response, res);
    }

    //<editor-fold defaultstate="collapsed" desc="Delete Asset">
    public Response deleteAsset(
            @Context final HttpServletRequest request,
            @PathParam("id") int id) {
        String transactionID = "";
        try {
            transactionID = debugRequestLOG(
                    "Delete Asset",
                    request,
                    null,
                    id);

            InternalResponse response = PaperlessService.deleteAsset(
                    request,
                    id,
                    transactionID);

            debugResponseLOG("Delete Asset", response);

            ServicesController.logIntoDB(
                        request,
                        response.getUser()==null?"anonymous":response.getUser().getEmail(),
                        response.getUser()==null?0:response.getUser().getAid(),
                        id, 
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Delete Asset",
                        transactionID
                );
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                ServicesController.createUserActivity(
                        request,
                        response,
                        "Delete Asset",
                        "Asset",
                        id,
                        EventAction.Edit,
                        "Delete an Asset",
                        "Delete an Asset",
                        Category.Asset);
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
                    "Error while deleting asset",
                    e);
        }
        return Response
                .status(PaperlessConstant.HTTP_CODE_500)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity("{Internal Server Error}")
                .build();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Asset Template">
    public Response getAssetTemplate(
            @Context final HttpServletRequest request,
            @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG(
                    "Get Asset Template",
                    request,
                    null,
                    id);

            response = PaperlessService.getAssetTemplate(
                    request,
                    id,
                    transactionID);

            debugResponseLOG("Get Asset Template", response);

            ServicesController.logIntoDB(
                        request,
                        response.getUser()==null?"anonymous":response.getUser().getEmail(),
                        response.getUser()==null?0:response.getUser().getAid(),
                        0, 
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get Asset Template",
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
                    "Error while getting asset template",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="get Asset Details">
    public Response getAssetDetails(
            @Context final HttpServletRequest request,
            @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG(
                    "Get Asset Details",
                    request,
                    null,
                    id);

            response = PaperlessService.getAssetDetails(
                    request,
                    id,
                    transactionID);

            debugResponseLOG("Get Asset Details", response);
            
            ServicesController.logIntoDB(
                        request,
                        response.getUser()==null?"anonymous":response.getUser().getEmail(),
                        response.getUser()==null?0:response.getUser().getAid(),
                        id,
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get Asset Details",
                        transactionID
                );
            
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(response.getStatus())
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
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Download">
    public Response downloadAsset(
            @Context final HttpServletRequest request,
            @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG(
                    "Download Asset",
                    request,
                    null,
                    id);

            response = PaperlessService.downloadsAsset(
                    request,
                    id,
                    transactionID);

            debugResponseLOG("Download Asset", response);
            
            ServicesController.logIntoDB(
                        request,
                        response.getUser()==null?"anonymous":response.getUser().getEmail(),
                        response.getUser()==null?0:response.getUser().getAid(),
                        id, 
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Download Asset",
                        transactionID
                );
            
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(response.getStatus())
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
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Download Base64">
    public Response downloadAssetBase64(
            @Context final HttpServletRequest request,
            @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG(
                    "Download Asset",
                    request,
                    null,
                    id);

            response = PaperlessService.downloadsAssetBase64(
                    request,
                    id,
                    transactionID);

            debugResponseLOG("Download Asset", response);
            
            ServicesController.logIntoDB(
                        request,
                        response.getUser()==null?"anonymous":response.getUser().getEmail(),
                        response.getUser()==null?0:response.getUser().getAid(),
                        id, 
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Download Asset base64",
                        transactionID
                );
            
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                return Response
                        .status(response.getStatus())
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
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Upload Asset">
    public Response uploadAsset(
            @Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG(
                    "Upload Asset",
                    request,
                    null,
                    0);

            response = PaperlessService.uploadAsset(
                    request,
                    transactionID);

            debugResponseLOG("Upload Asset", response);
            Long id = 0L;
            try{
                id = (long)response.getData();
            } catch(Exception ex){}
            ServicesController.logIntoDB(
                        request,
                        response.getUser()==null?"anonymous":response.getUser().getEmail(),
                        response.getUser()==null?0:response.getUser().getAid(),
                        id.intValue(), 
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Upload Asset",
                        transactionID
                );
            
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                
                ServicesController.createUserActivity(
                        request,
                        response,
                        "Upload Asset",
                        "Asset",
                        (long) response.getData(),
                        EventAction.Upload,
                        "Upload an Asset",
                        "Upload an Asset",
                        Category.Asset);
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
                    "Error while uploading asset",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Upload Base64">
    public Response uploadAssetBase64(
            @Context final HttpServletRequest request,
            String payload) {
        String transactionID = "";
        try {
            InternalResponse response;
            transactionID = debugRequestLOG(
                    "Upload Asset",
                    request,
                    null,
                    0);

            response = PaperlessService.uploadAssetBase64(
                    request,
                    payload,
                    transactionID);

            debugResponseLOG("Upload Asset", response);
            
            Long id = 0L;
            try{
                id = (long)response.getData();
            } catch(Exception ex){}
            
            ServicesController.logIntoDB(
                        request,
                        response.getUser()==null?"anonymous":response.getUser().getEmail(),
                        response.getUser()==null?0:response.getUser().getAid(),
                        id.intValue(), 
                        response.getStatus(),
                        payload,
                        response.getMessage(),
                        "Upload Asset Base64",
                        transactionID
                );
            
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                ServicesController.createUserActivity(
                        request,
                        response,
                        "Upload Asset",
                        "Asset",
                        (long) response.getData(),
                        EventAction.Upload,
                        "Upload an Asset",
                        "Upload an Asset",
                        Category.Asset);
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
                    "Error while uploading asset",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Update Asset">
    public Response updateAsset(
            @Context final HttpServletRequest request,
            @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG(
                    "Update Asset",
                    request,
                    null,
                    0);

            response = PaperlessService.updateAsset(request, id, transactionID);

            debugResponseLOG("Update Asset", response);

            ServicesController.logIntoDB(
                        request,
                        response.getUser()==null?"anonymous":response.getUser().getEmail(),
                        response.getUser()==null?0:response.getUser().getAid(),
                        id, 
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Update Asset",
                        transactionID
                );
            
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                ServicesController.createUserActivity(
                        request,
                        response,
                        "Update Asset",
                        "Asset",
                        id,
                        EventAction.Edit,
                        "Edit an Asset",
                        "Edit an Asset",
                        Category.Asset);
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
                    "Error while updating asset",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Update Asset Base64">
    public Response updateAssetBase64(
            @Context final HttpServletRequest request,
            @PathParam("id") int id) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG(
                    "Update Asset Base64",
                    request,
                    null,
                    0);

            response = PaperlessService.updateAssetBase64(request, id, transactionID);

            debugResponseLOG("Update Asset", response);

            ServicesController.logIntoDB(
                        request,
                        response.getUser()==null?"anonymous":response.getUser().getEmail(),
                        response.getUser()==null?0:response.getUser().getAid(),
                        id, 
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Update Asset Base64",
                        transactionID
                );
            
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                ServicesController.createUserActivity(
                        request,
                        response,
                        "Update Asset",
                        "Asset",
                        id,
                        EventAction.Edit,
                        "Update an Asset",
                        "Update an Asset",
                        Category.Asset);
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
                    "Error while updating asset",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get List Asset">
    public Response getListAsset(
            @Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG(
                    "Get List Asset",
                    request,
                    null,
                    0);

            response = PaperlessService.getListAsset(
                    request,
                    transactionID);

            debugResponseLOG("Get List Asset", response);

            ServicesController.logIntoDB(
                        request,
                        response.getUser()==null?"anonymous":response.getUser().getEmail(),
                        response.getUser()==null?0:response.getUser().getAid(),
                        0, 
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get list Asset",
                        transactionID
                );
            
            return Response
                    .status(response.getStatus())
                    .entity(response.getMessage())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            LogHandler.error(
                    this.getClass(),
                    transactionID,
                    "Error while getting list of assets",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Record of Asset">
    public Response getTotalRecordsAsset(
            @Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;

            transactionID = debugRequestLOG(
                    "Get List Asset",
                    request,
                    null,
                    0);

            response = PaperlessService.getTotalRecordsAsset(
                    request,
                    transactionID);

            debugResponseLOG("Get List Asset", response);

            ServicesController.logIntoDB(
                        request,
                        response.getUser()==null?"anonymous":response.getUser().getEmail(),
                        response.getUser()==null?0:response.getUser().getAid(),
                        0, 
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get total record Asset",
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
                    "Error while getting list of assets",
                    e);
            return Response
                    .status(PaperlessConstant.HTTP_CODE_500)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity("{Internal Server Error}")
                    .build();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Asset Template Type">
    public Response getAssetTemplateType(
            @Context final HttpServletRequest request) {
        String transactionID = "";
        try {
            InternalResponse response;
            transactionID = debugRequestLOG("Get Asset Template Type", request, null, 0);
            response = PaperlessService.getAssetType(request, transactionID);

            debugResponseLOG("Get Asset Template Type", response);
            
            ServicesController.logIntoDB(
                        request,
                        response.getUser()==null?"anonymous":response.getUser().getEmail(),
                        response.getUser()==null?0:response.getUser().getAid(),
                        0, 
                        response.getStatus(),
                        "",
                        response.getMessage(),
                        "Get Asset Template Type",
                        transactionID
                );
            
            if (response.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();

                node.put("asset_template_types", (ArrayNode) response.getData());

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
            return Response.status(
                    PaperlessConstant.HTTP_CODE_500)
                    .entity("{Internal Server Error}").build();
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
    private static String conclusionString(String payload, int id) {
        String pattern = "\"value\":.*";
        if (payload == null) {
            return String.valueOf(id);
        }
        return payload.replaceAll(pattern, "\"value\":\"base64\"}]}");
    }
    //</editor-fold>
    
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
    
    //<editor-fold defaultstate="collapsed" desc="get Body">
    public static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        body = stringBuilder.toString();
        return body;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Populate HTTP Servlet Response">
    private static HttpServletResponse populateHttpServletResponse(
            Response response,
            HttpServletResponse res) throws IOException {
        if (response != null) {
            MultivaluedMap<String, Object> headers = response.getHeaders();
            headers.forEach((key, value) -> {
                res.setHeader(key, String.valueOf(value));
            });
            res.setStatus(response.getStatus());
            MediaType type = response.getMediaType();
            res.setContentType(type.getType() + "/" + type.getSubtype());
            if (response.getEntity() instanceof String) {
                res.getWriter().write((String) response.getEntity());
                return res;
            }
            if (response.getEntity() instanceof byte[]) {
                res.getOutputStream().write((byte[]) response.getEntity());
            }
            return res;
        } else {
            res.sendError(404);
            return res;
        }
    }
    //</editor-fold>
    
}
