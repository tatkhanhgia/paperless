/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestfulFactory;

import RestfulFactory.Model.DocumentDigests;
import RestfulFactory.Request.AuthorizeRequest;
import RestfulFactory.Request.CredentialInfoRequest;
import RestfulFactory.Request.CredentialListRequest;
import RestfulFactory.Request.LoginRequest;
import RestfulFactory.Request.SignHashRequest;
import RestfulFactory.Request.preLoginRequest;
import RestfulFactoryl.Response.AuthorizeResponse;
import RestfulFactoryl.Response.CredentialInfoResponse;
import RestfulFactoryl.Response.CredentialListResponse;
import RestfulFactoryl.Response.LoginResponse;
import RestfulFactoryl.Response.Response;
import RestfulFactoryl.Response.SignHashResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import restful.sdk.API.APIException;
import restful.sdk.API.BaseCertificateInfo;
import restful.sdk.API.HTTPUtils;
import restful.sdk.API.HttpResponse;
import restful.sdk.API.ICertificate;
import restful.sdk.API.IUserSession;
import restful.sdk.API.Property;
import restful.sdk.API.Types;
import restful.sdk.API.Types.UserType;
import restful.sdk.API.Utils;

/**
 *
 * @author GiaTK
 */
public class UserSession implements IUserSession {

    private String bearer;
    private String refreshToken;
    private int retryLogin = 0;

    private Property property;
    private String lang;

    private String username;
    private String password;

    public UserSession(Property prop, String lang, String username, String password) throws Throwable {
        this.property = prop;
        this.lang = lang;
        this.username = username;
        this.password = password;
        this.login();
    }

    public boolean close() throws Throwable {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void login() throws Throwable {
//        System.out.println("____________auth/login____________");
        String authHeader;
        if (refreshToken != null) {
            authHeader = refreshToken;
        } else {
            retryLogin++;
            String basic = UserType.USERNAME.toString() + ":" + username + ":" + password;
            authHeader = property.getAuthorization2() + ", Basic " + Utils.base64Encode(basic);
        }
//        System.out.println("Login-retry: " + retryLogin);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setRememberMeEnabled(true);
        loginRequest.setRelyingParty(property.getRelyingParty());
        loginRequest.setLang(this.lang);
        //Console.WriteLine(loginRequest);

        String jsonReq = Utils.gsTmp.toJson(loginRequest);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "auth/login", jsonReq, authHeader);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        LoginResponse signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), LoginResponse.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            refreshToken = null;
            if (retryLogin >= 5) {
                retryLogin = 0;
                throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
            }
            login();
        } else if (signCloudResp.getError() != 0) {
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        } else {
            this.bearer = "Bearer " + signCloudResp.getAccessToken();
            if (signCloudResp.getRefreshToken() != null) {
                this.refreshToken = "Bearer " + signCloudResp.getRefreshToken();
            }
        }
    }

    @Override
    public boolean sendUserOTP() throws Throwable {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ICertificate> listCertificates() throws Throwable {
//        System.out.println("____________credentials/list____________");
        String authHeader = bearer;
        CredentialListRequest credentialListRequest = new CredentialListRequest();
        credentialListRequest.setLang(this.lang);
        credentialListRequest.setProfile("rssp-119.432-v2.0");

        String jsonReq = Utils.gsTmp.toJson(credentialListRequest);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "credentials/list", jsonReq, authHeader);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        System.out.println("credentials/list response.getMsg() = " + response.getMsg());
        CredentialListResponse signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), CredentialListResponse.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            login();
            return listCertificates();
        } else if (signCloudResp.getError() != 0) {
//            System.out.println("Err code: " + signCloudResp.getError());
//            System.out.println("Err Desscription: " + signCloudResp.getErrorDescription());
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }
        List<BaseCertificateInfo> listCert = signCloudResp.getCerts();
        List<ICertificate> listCertificate = new ArrayList<ICertificate>();

        for (BaseCertificateInfo item : listCert) {
            UserCertificate icrt = new UserCertificate(item, this.username, this);
            listCertificate.add(icrt);
        }
        return listCertificate;
    }

    @Override
    public ICertificate certificateInfo(String credentialID) throws Throwable {
//        System.out.println("____________credentials/info____________");
        CredentialInfoRequest credentialInfoRequest = new CredentialInfoRequest();
//        credentialInfoRequest.setAgreementUUID(agreementUUID);
        credentialInfoRequest.setCredentialID(credentialID);
//        credentialInfoRequest.setCertificates(certificate);
//        credentialInfoRequest.setCertInfoEnabled(certInfoEnabled);
//        credentialInfoRequest.setAuthInfoEnabled(authInfoEnabled);
        credentialInfoRequest.setLang(this.lang);
        credentialInfoRequest.setProfile("rssp-119.432-v2.0");

        String jsonReq = Utils.gsTmp.toJson(credentialInfoRequest);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "credentials/info", jsonReq, bearer);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        System.out.println("response.getMsg() = " + response.getMsg());
        CredentialInfoResponse signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), CredentialInfoResponse.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            login();
            return certificateInfo(credentialID);
        } else if (signCloudResp.getError() != 0) {
//            System.out.println("Err code: " + signCloudResp.getError());
//            System.out.println("Err Desscription: " + signCloudResp.getErrorDescription());
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }

        ICertificate iCrt = (ICertificate) new UserCertificate(signCloudResp.getCert(), username, this);
//        signCloudResp.getCert().setAuthorizationEmail(signCloudResp.getAuthorizationEmail());
//        signCloudResp.getCert().setAuthorizationPhone(signCloudResp.getAuthorizationPhone());
//        signCloudResp.getCert().setSharedMode(signCloudResp.getSharedMode());
//        signCloudResp.getCert().setCreatedRP(signCloudResp.getCreatedRP());
//        signCloudResp.getCert().setAuthModes(signCloudResp.getAuthModes());
//
//        signCloudResp.getCert().setAuthMode(signCloudResp.getAuthMode());
//        signCloudResp.getCert().setSCAL(signCloudResp.getSCAL());
//        signCloudResp.getCert().setContractExpirationDate(signCloudResp.getContractExpirationDate());
//        signCloudResp.getCert().setDefaultPassphraseEnabled(signCloudResp.isDefaultPassphraseEnabled());
//        signCloudResp.getCert().setTrialEnabled(signCloudResp.isTrialEnabled());

        return iCrt;
    }

    @Override
    public ICertificate certificateInfo(String agreementUUID, String credentialID, String cetificate, boolean certInfoEnabled, boolean authInfoEnabled) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<byte[]> signHash(
            String credentialID,
            DocumentDigests documentDigest,
            Types.SignAlgo signAlgo,
            String SAD) throws APIException {
        SignHashRequest request = new SignHashRequest();
        request.setCredentialID(credentialID);
        request.setDocumentDigests(documentDigest);
        request.setOperationMode(Types.OperationMode.S);
        request.setSignAlgo(signAlgo);
        request.setSAD(SAD);
        request.setLang(this.lang);
        request.setProfile("rssp-119.432-v2.0");

        String jsonReq = Utils.gsTmp.toJson(request);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "signatures/signHash", jsonReq, bearer);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        SignHashResponse signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), SignHashResponse.class);
//        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
//            login();
//            return signHash(agreementUUID, credentialID, documentDigest, signAlgo, SAD);
//        } else 
        if (signCloudResp.getError() != 0) {
//            System.out.println("Err code: " + signCloudResp.getError());
//            System.out.println("Err Desscription: " + signCloudResp.getErrorDescription());
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }
        return signCloudResp.getSignatures();
    }

    @Override
    public String authorize(
            String credentialID,
            int numSignatures,
            String requestID,
            String authorizeCode) throws APIException, Throwable {
        AuthorizeRequest request = new AuthorizeRequest();
        request.setCredentialID(credentialID);
        request.setNumSignatures(numSignatures);
        request.setRequestID(requestID);
        request.setAuthorizeCode(authorizeCode);
        request.setLang(this.lang);
        request.setProfile("rssp-119.432-v2.0");

        String jsonReq = Utils.gsTmp.toJson(request);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "credentials/authorize", jsonReq, bearer);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        AuthorizeResponse signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), AuthorizeResponse.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            login();
            return authorize(credentialID, numSignatures, requestID, authorizeCode);
        } else if (signCloudResp.getError() != 0) {
//            System.out.println("Err code: " + signCloudResp.getError());
//            System.out.println("Err Desscription: " + signCloudResp.getErrorDescription());
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }

        return signCloudResp.getSAD();
    }

    @Override
    public boolean preLogin(String username) throws Throwable {
        preLoginRequest request = new preLoginRequest();
        request.setUser(username);
        request.setType(UserType.USERNAME);
        //Console.WriteLine(loginRequest);

        String jsonReq = Utils.gsTmp.toJson(request);
//        System.out.println("JSON:"+jsonReq);
//        System.out.println("URL:"+property.getBaseUrl()+ "auth/preLogin");
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "auth/preLogin", jsonReq, bearer);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Response signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), Response.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            refreshToken = null;
            if (retryLogin >= 5) {
                retryLogin = 0;
                throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
            }
            login();
        } else if (signCloudResp.getError() != 0) {
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        } else {
//            System.out.println(signCloudResp.getErrorDescription());
            if(signCloudResp.getError() == 0 && response.getHttpCode() == 200){
                return true;
            }
            return false;            
        }
//        System.out.println(signCloudResp.getErrorDescription());
        return false;
    }
}
