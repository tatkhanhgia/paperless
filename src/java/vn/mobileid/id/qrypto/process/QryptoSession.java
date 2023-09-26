/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.process;

import vn.mobileid.id.qrypto.object.Property;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import restful.sdk.API.HTTPUtils;
import restful.sdk.API.HttpPostMultiPart2;
import restful.sdk.API.HttpPostMultipart;
import restful.sdk.API.HttpResponse;
import restful.sdk.API.Utils;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.qrypto.object.Configuration;
import vn.mobileid.id.qrypto.object.QRSchema;
import vn.mobileid.id.qrypto.request.ClaimRequest;
import vn.mobileid.id.qrypto.request.GetTokenRequest;
import vn.mobileid.id.qrypto.request.IssueQryptoWithFileAttachRequest;
import vn.mobileid.id.qrypto.response.ClaimResponse;
import vn.mobileid.id.qrypto.response.GetTokenResponse;
import vn.mobileid.id.qrypto.response.IssueQryptoWithFileAttachResponse;

/**
 *
 * @author GiaTK
 */
public class QryptoSession implements ISession {

    public String bearerToken;
    public String refreshToken;
    public Property prop;
    public int retryLogin = 0;

    public QryptoSession(Property prop) {
        this.prop = prop;
    }

    @Override
    public void login() throws Exception {
//        System.out.println("____________auth/login____________");
        String authHeader = null;

        if (refreshToken != null) {
            authHeader = refreshToken;
        } else {
            try {
                retryLogin++;
                authHeader = prop.getAuthorization();
            } catch (Throwable ex) {
                Logger.getLogger(QryptoSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        GetTokenRequest loginRequest = new GetTokenRequest();
//        loginRequest.setRefresh_token(QryptoConstant.RefreshTokenTest);

        String jsonReq = Utils.gsTmp.toJson(loginRequest);

        HttpResponse response = HTTPUtils.sendPost(prop.getBaseUrl() + "/general/auth/login", jsonReq, authHeader);

        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                LogHandler.info(QryptoSession.class, "Error - Detail:" + ex);
            }
        }

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
            throw new Exception(qryptoResp.getProblem() + " - " + qryptoResp.getDetails());
        } else {
            this.bearerToken = "Bearer " + qryptoResp.getAccess_token();

            if (qryptoResp.getRefresh_token() != null) {
                this.refreshToken = "Bearer " + qryptoResp.getRefresh_token();
            }
        }
    }

//    @Override
//    public void issueQryptoWithFileAttach(
//            String payload,
//            List<byte[]> images,
//            List<byte[]> demos,
//            List<byte[]> landscapes,
//            Configuration configuration
//    ) throws Exception {
//        //        System.out.println("____________auth/login____________");
//        String authHeader = null;
//
//        if (bearerToken != null) {
//            authHeader = bearerToken;
//        } else {
//            try {
//                retryLogin++;
//                authHeader = prop.getAuthorization();
//            } catch (Throwable ex) {
//                Logger.getLogger(QryptoSession.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        
//        IssueQryptoWithFileAttachRequest data = new IssueQryptoWithFileAttachRequest();
//        data.setPayload(payload);
//        data.setImgs(images);
//        data.setDemo(demos);
//        data.setLandscapes(landscapes);
//        data.setConfiguration(configuration);
//
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Authorization", bearerToken);
//        
//        HttpPostMultipart multipart = new HttpPostMultipart(
//                prop.getBaseUrl() + "general/auth/login",
//                "utf-8",
//                headers);
//        multipart.addFormField("payload", payload);
//        multipart.addFormField("configuration", new ObjectMapper().writeValueAsString(configuration));
//        multipart.addFilePart("img1", "file1",images.get(0));
//        multipart.addFilePart("demo", "demo", demos.get(0));
//        multipart.addFilePart("landscapes", "landscapes", landscapes.get(0));
//        
//        String response = multipart.finish();
//        System.out.println("Response:"+response);
//       
//    }
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

//        IssueQryptoWithFileAttachRequest data = new IssueQryptoWithFileAttachRequest();
//        data.setPayload(new ObjectMapper().writeValueAsString(QR));
//        data.setMapFile(QR.getHeader());
//        data.setConfiguration(configuration);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", bearerToken);

        HttpPostMultiPart2 a = new HttpPostMultiPart2();
        Map<String, Object> bodypart = new HashMap<>();
        bodypart.put("payload", new ObjectMapper().writeValueAsString(QR));
        bodypart.put("configuration", new ObjectMapper().writeValueAsString(configuration));
        for (String key : QR.getHeader().keySet()) {
            bodypart.put(key, QR.getHeader().get(key));
        }

        org.apache.http.HttpResponse response = a.sendPost(prop.getBaseUrl() + "/issuance/qrci/issueQryptoWithFileAttach", headers, bodypart);
        StringBuilder sb = new StringBuilder();
        for (int ch; (ch = response.getEntity().getContent().read()) != -1;) {
            sb.append((char) ch);
        }
        String message = sb.toString();
        IssueQryptoWithFileAttachResponse responses = new IssueQryptoWithFileAttachResponse();
        try {
            responses = new ObjectMapper().readValue(message, IssueQryptoWithFileAttachResponse.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (response.getStatusLine().getStatusCode() == 401) {
            return null;
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception(responses.getStatus() + "\n" + responses.getData() + "\n" + responses.getMessage());
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
        System.out.println("JsonRequest:" + jsonReq);
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

}
