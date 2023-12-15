/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.process;

import vn.mobileid.id.qrypto.object.Property;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import restful.sdk.API.HTTPUtils;
import restful.sdk.API.HttpPostMultiPart2;
import restful.sdk.API.HttpResponse;
import restful.sdk.API.Utils;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.qrypto.object.Configuration;
import vn.mobileid.id.paperless.exception.LoginException;
import vn.mobileid.id.qrypto.object.QRSchema;
import vn.mobileid.id.paperless.exception.QryptoException;
import vn.mobileid.id.qrypto.request.ClaimRequest;
import vn.mobileid.id.qrypto.request.GetTokenRequest;
import vn.mobileid.id.qrypto.response.ClaimResponse;
import vn.mobileid.id.qrypto.response.DownloadFileTokenResponse;
import vn.mobileid.id.qrypto.response.GetTokenResponse;
import vn.mobileid.id.qrypto.response.IssueQryptoWithFileAttachResponse;

/**
 *
 * @author GiaTK
 */
public class QryptoSession implements ISession {

    private String bearerToken;
    private String refreshToken;
    private Property prop;
    private int retryLogin = 0;

    public QryptoSession(Property prop) {
        this.prop = prop;
    }

    @Override
    public void login() throws Exception {
//        System.out.println("____________auth/login____________");
        String authHeader = null;

//        if (refreshToken != null) {
//            authHeader = refreshToken;
//        } else {
        try {
            retryLogin++;
            authHeader = prop.getAuthorization();
        } catch (Throwable ex) {
            Logger.getLogger(QryptoSession.class.getName()).log(Level.SEVERE, null, ex);
        }
//        }
        GetTokenRequest loginRequest = new GetTokenRequest();
//        loginRequest.setRefresh_token(QryptoConstant.RefreshTokenTest);

        String jsonReq = Utils.gsTmp.toJson(loginRequest);

        HttpResponse response = HTTPUtils.sendPost(prop.getBaseUrl() + "/general/auth/login", jsonReq, authHeader);

        if (!response.isStatus()) {
            try {
                throw new QryptoException(response.getMsg());
            } catch (Exception ex) {
                LogHandler.info(QryptoSession.class, "Error - Detail:" + ex);
            }
        }

        System.out.println("Response Message:" + response.getMsg());
        GetTokenResponse qryptoResp = Utils.gsTmp.fromJson(response.getMsg(), GetTokenResponse.class);
        if (qryptoResp.getCode() == 3005 || qryptoResp.getCode() == 3006) {
            refreshToken = null;
            if (retryLogin >= 5) {
                retryLogin = 0;
                LogHandler.info(QryptoSession.class,
                        "Err code: " + qryptoResp.getCode()
                        + "\nProblem: " + qryptoResp.getProblem()
                        + "\nDetails:" + qryptoResp.getDetails());
                throw new Exception(qryptoResp.getProblem() + " - " + qryptoResp.getDetails());
            }
            login();
        } else if (qryptoResp.getCode() != 0) {
            LogHandler.info(QryptoSession.class,
                    "Err code: " + qryptoResp.getCode()
                    + "\nProblem: " + qryptoResp.getProblem()
                    + "\nDetails:" + qryptoResp.getDetails());
            throw new QryptoException(qryptoResp.getProblem() + " - " + qryptoResp.getDetails());
        } else {
            this.bearerToken = "Bearer " + qryptoResp.getAccess_token();

            if (qryptoResp.getRefresh_token() != null) {
                this.refreshToken = "Bearer " + qryptoResp.getRefresh_token();
            }
        }
    }

    @Override
    public IssueQryptoWithFileAttachResponse issueQryptoWithFileAttach(
            QRSchema QR,
            Configuration configuration
    ) throws Exception {
        //        System.out.println("____________auth/login____________");
        String authHeader = null;

        if (bearerToken != null) {
            authHeader = bearerToken;
        } else {
            retryLogin++;
            this.login();
            if (retryLogin == 2) {
                throw new Exception("Cannot login again!");
            }
        }

        Map<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put("Accept-Charset", "UTF-8");
        headers.put("Authorization", bearerToken);

        String temp = new ObjectMapper().writeValueAsString(QR);
//        String temp = "{\"scheme\":\"QC1\",\"data\":{\"fd19c4db-859c-45a5-b0be-1c9e96ea53a7\":\"Tiếp Sỹ Minh Phụng\"},\"format\":{\"fields\":[{\"Tên\":{\"type\":\"t2\",\"kvalue\":\"fd19c4db-859c-45a5-b0be-1c9e96ea53a7\"}},{\"File1\":{\"type\":\"f1\",\"file_type\":\"application/pdf\",\"file_field\":\"a6fbf534-7487-4863-9ffc-94810c4038bf\",\"file_name\":\"BA_eKYC_TechnicalRequirement.pdf\",\"share_mode\":2,\"qr_meta_data\":{\"isTransparent\":false,\"xcoordinator\":50,\"ycoordinator\":50,\"qrDimension\":100,\"pageNumber\":[1,2,3]}}}],\"version\":\"2\"},\"title\":\"Demo Qrypto\",\"ci\":\"\"}";

        HttpPostMultiPart2 a = new HttpPostMultiPart2();
        Map<String, Object> bodypart = new HashMap<>();
        bodypart.put("payload", temp);

        bodypart.put("configuration", new ObjectMapper().writeValueAsString(configuration));
        System.out.println("Config:"+new ObjectMapper().writeValueAsString(configuration));
        for (String key : QR.getHeader().keySet()) {
            bodypart.put(key, QR.getHeader().get(key));
        }
        
        System.out.println("Payload:"+temp);

        org.apache.http.HttpResponse response = a.sendPost(prop.getBaseUrl() + "/issuance/qrci/issueQryptoWithAttachment", headers, bodypart);
        StringBuilder sb = new StringBuilder();
        for (int ch; (ch = response.getEntity().getContent().read()) != -1;) {
            sb.append((char) ch);
        }
        String message = sb.toString();
        IssueQryptoWithFileAttachResponse responses = new IssueQryptoWithFileAttachResponse();
        System.out.println("Message from QRYPTO:"+message);
        responses = new ObjectMapper().readValue(message, IssueQryptoWithFileAttachResponse.class);
        if (responses.getStatus() == 1009 && response.getStatusLine().getStatusCode() == 401) {
            throw new LoginException(responses.getStatus() + "\n" + responses.getData() + "\n" + responses.getMessage());
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new QryptoException(responses.getStatus() + "\n" + responses.getData() + "\n" + responses.getMessage());
        }
        return responses;
    }

    @Override
    public ClaimResponse dgci_wallet_claim(ClaimRequest request) throws Exception {
        String authHeader = null;

        if (bearerToken != null) {
            authHeader = bearerToken;
        } else {
            retryLogin++;
            this.login();
            if (retryLogin == 2) {
                throw new Exception("Cannot login again!");
            }
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", bearerToken);

        String jsonReq = new ObjectMapper().writeValueAsString(request);
        System.out.println("Qrypto Session - JsonRequest:" + jsonReq);
        HttpResponse response = HTTPUtils.sendPost(prop.getBaseUrl() + "/issuance/qrci/wallet/claim", jsonReq, authHeader);
        if (response.getHttpCode() != 0) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                ex.printStackTrace();
                LogHandler.error(QryptoSession.class, "Error - Detail:", ex);
            }
        }
//        System.out.println("credentials/list response.getMsg() = "+ response.getMsg());
        ClaimResponse responses = new ObjectMapper().readValue(response.getMsg(), ClaimResponse.class);

        return responses;
    }

    @Override
    public DownloadFileTokenResponse downloadFileToken(String fileToken) throws Exception {
        String authHeader = null;

        if (bearerToken != null) {
            authHeader = bearerToken;
        } else {
            retryLogin++;
            this.login();
            if (retryLogin == 2) {
                throw new Exception("Cannot login again!");
            }
        }

        System.out.println("URL:"+(prop.getBaseUrl() + "/verifier/" + fileToken + "/download/base64"));
        HttpResponse response = HTTPUtils.sendGet(
                prop.getBaseUrl() + "/verifier/" + fileToken + "/download/base64",
                HTTPUtils.ContentType.JSON, 
                null, 
                authHeader);
        
        System.out.println("Status:"+response.getHttpCode());
        System.out.println("Response Message:" + response.getMsg());
        DownloadFileTokenResponse qryptoResp = Utils.gsTmp.fromJson(response.getMsg(), DownloadFileTokenResponse.class);
        if (qryptoResp.getCode() == 3005 || qryptoResp.getCode() == 3006) {
            refreshToken = null;
            if (retryLogin >= 5) {
                retryLogin = 0;
                LogHandler.info(QryptoSession.class,
                        "Err code: " + qryptoResp.getCode()
                        + "\nProblem: " + qryptoResp.getProblem()
                        + "\nDetails:" + qryptoResp.getDetails());
                throw new Exception(qryptoResp.getProblem() + " - " + qryptoResp.getDetails());
            }
            login();
            return downloadFileToken(fileToken);
        } else if (qryptoResp.getCode() != 0) {
            LogHandler.info(QryptoSession.class,
                    "Err code: " + qryptoResp.getCode()
                    + "\nProblem: " + qryptoResp.getProblem()
                    + "\nDetails:" + qryptoResp.getDetails());
            throw new QryptoException(qryptoResp.getProblem() + " - " + qryptoResp.getDetails());
        } else {
            return qryptoResp;
        }
    }

}
