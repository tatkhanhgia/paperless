package RestfulFactory;

import RestfulFactory.Model.CertificateAuthority;
import restful.sdk.API.APIException;
import RestfulFactory.Request.CredentialListRequest;
import restful.sdk.API.Types.OTPType;
import restful.sdk.API.Types.SignedPropertyType;
import restful.sdk.API.Types.AuthMode;
import restful.sdk.API.Types.SharedMode;
import restful.sdk.API.Types.UserType;
import restful.sdk.API.Types.HashAlgorithmOID;
import restful.sdk.API.Types.SignAlgo;
import static restful.sdk.API.Types.UserType.USERNAME;
import java.util.HashMap;
import java.util.List;
import RestfulFactory.Model.CertificateDetails;
import RestfulFactory.Model.CertificateProfile;
import RestfulFactory.Model.ClientInfo;
import RestfulFactory.Model.DocumentDigests;
import RestfulFactory.Model.MobileDisplayTemplate;
import RestfulFactory.Model.Province;
import RestfulFactory.Request.AgreementAssignRequest;
import RestfulFactory.Request.AuthorizeRequest;
import RestfulFactory.Request.LoginRequest;
import RestfulFactory.Model.SearchConditions;
import RestfulFactory.Request.CreateUserRequest;
import RestfulFactory.Request.CredentialInfoRequest;
import RestfulFactory.Request.CredentialSendOTPRequest;
import RestfulFactory.Request.GetCertificateProfilesRequest;
import RestfulFactory.Request.GetStateProvinceRequest;
import RestfulFactory.Request.ImportCertificateRequest;
import RestfulFactory.Request.IssueCertificateRequest;
import RestfulFactory.Request.Request;
import RestfulFactory.Request.SendOwnerOTPRequest;
import RestfulFactory.Request.SignDocRequest;
import RestfulFactory.Request.SignHashRequest;
import RestfulFactoryl.Response.AgreementAssignResponse;
import RestfulFactoryl.Response.AuthorizeResponse;
import RestfulFactoryl.Response.CreateUserResponse;
import RestfulFactoryl.Response.CredentialInfoResponse;
import RestfulFactoryl.Response.CredentialListResponse;
import RestfulFactoryl.Response.GetCertificateAuthoritiesResponse;
import RestfulFactoryl.Response.GetCertificateProfilesResponse;
import RestfulFactoryl.Response.GetStateProvinceResponse;
import RestfulFactoryl.Response.IssueCertificateResponse;
import RestfulFactoryl.Response.LoginResponse;
import RestfulFactoryl.Response.Response;
import RestfulFactoryl.Response.SignDocResponse;
import RestfulFactoryl.Response.SignHashResponse;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import restful.sdk.API.BaseCertificateInfo;
import restful.sdk.API.CertificateInfo;
import restful.sdk.API.HTTPUtils;
import restful.sdk.API.HttpResponse;
import restful.sdk.API.ICertificate;
import restful.sdk.API.IServerSession;
import restful.sdk.API.Property;
import restful.sdk.API.Utils;

public class ServerSession implements IServerSession {

    private String bearer;
    private String refreshToken;
    private Property property;
    private String lang;
    private int retryLogin = 0;

    private String sessionId;

    public ServerSession(Property prop, String lang) throws Throwable {
        this.property = prop;
        this.lang = lang;
        this.sessionId = UUID.randomUUID().toString();
        this.login();
    }

    @Override
    public boolean close() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void login() throws Throwable {
        System.out.println("____________auth/login____________");
        String authHeader;

        if (refreshToken != null) {
            authHeader = refreshToken;
        } else {
            retryLogin++;            
            authHeader = property.getAuthorization2();            
        }
//        System.out.println("Login-retry: " + retryLogin);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setRememberMeEnabled(true);
        loginRequest.setRelyingParty(property.getRelyingParty());
//        loginRequest.setClientInfo(new ClientInfo());
//        loginRequest.getClientInfo().setInstanceUUID(this.sessionId);

        loginRequest.setLang(this.lang);

        //Test
//        System.out.println("BaseURL:"+property.getBaseUrl());
//        System.out.println("RP:"+property.getRelyingParty());
//        System.out.println("User:"+property.getRelyingPartyUser());
//        System.out.println("Pass:"+property.getRelyingPartyPassword());
//        System.out.println("PassKey:"+property.getRelyingPartyKeyStorePassword());
//        System.out.println("Token:"+authHeader);
        
        String jsonReq = Utils.gsTmp.toJson(loginRequest);
//        System.out.println("Payload:"+jsonReq);
        
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
                System.out.println("Err code: " + signCloudResp.getError());
                System.out.println("Err Desscription: " + signCloudResp.getErrorDescription());
                throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
            }
            login();
        } else if (signCloudResp.getError() != 0) {
            System.out.println("Err code: " + signCloudResp.getError());
            System.out.println("Err Desscription: " + signCloudResp.getErrorDescription());
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        } else {
            this.bearer = "Bearer " + signCloudResp.getAccessToken();

            if (signCloudResp.getRefreshToken() != null) {
                this.refreshToken = "Bearer " + signCloudResp.getRefreshToken();
//                System.out.println("Err code: " + signCloudResp.getError());
//                System.out.println("Err Desscription: " + signCloudResp.getErrorDescription());
            }
        }
    }

    @Override
    public List<ICertificate> listCertificates(String agreementUUID) throws Throwable {

        return listCertificates(agreementUUID, null, false, false, null);
    }

    @Override
    public List<ICertificate> listCertificates(String agreementUUID, String certificate, boolean certInfoEnabled, boolean authInfoEnabled, SearchConditions conditions) throws Throwable {
//        System.out.println("____________credentials/list____________");
        String authHeader = bearer;
        CredentialListRequest credentialListRequest = new CredentialListRequest();
        credentialListRequest.setAgreementUUID(agreementUUID);
        credentialListRequest.setCertificates(certificate);
        credentialListRequest.setCertInfoEnabled(certInfoEnabled);
        credentialListRequest.setAuthInfoEnabled(authInfoEnabled);
        credentialListRequest.setSearchConditions(conditions);
        credentialListRequest.setLang(this.lang);

        String jsonReq = Utils.gsTmp.toJson(credentialListRequest);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "credentials/list", jsonReq, authHeader);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        System.out.println("credentials/list response.getMsg() = "+ response.getMsg());
        CredentialListResponse signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), CredentialListResponse.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            login();
            return listCertificates(agreementUUID, certificate, certInfoEnabled, authInfoEnabled, conditions);
        } else if (signCloudResp.getError() != 0) {
            System.out.println("Err code: " + signCloudResp.getError());
            System.out.println("Err Desscription: " + signCloudResp.getErrorDescription());
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }
        List<BaseCertificateInfo> listCert = signCloudResp.getCerts();
        List<ICertificate> listCertificate = new ArrayList<ICertificate>();

        for (BaseCertificateInfo item : listCert) {
            ICertificate icrt = new Certificate(item, agreementUUID, this);
            listCertificate.add(icrt);
        }
        return listCertificate;
    }

    @Override
    public ICertificate certificateInfo(String agreementUUID, String credentialID) throws Throwable {
        return certificateInfo(agreementUUID, credentialID, null, false, false);
    }

    @Override
    public ICertificate certificateInfo(String agreementUUID, String credentialID, String certificate, boolean certInfoEnabled, boolean authInfoEnabled) throws Throwable {
        System.out.println("____________credentials/info____________");
        CredentialInfoRequest credentialInfoRequest = new CredentialInfoRequest();
        credentialInfoRequest.setAgreementUUID(agreementUUID);
        credentialInfoRequest.setCredentialID(credentialID);
        credentialInfoRequest.setCertificates(certificate);
        credentialInfoRequest.setCertInfoEnabled(certInfoEnabled);
        credentialInfoRequest.setAuthInfoEnabled(authInfoEnabled);
        credentialInfoRequest.setLang(this.lang);

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
            return certificateInfo(agreementUUID, credentialID, certificate, certInfoEnabled, authInfoEnabled);
        } else if (signCloudResp.getError() != 0) {
            System.out.println("Err code: " + signCloudResp.getError());
            System.out.println("Err Desscription: " + signCloudResp.getErrorDescription());
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }

        ICertificate iCrt = (ICertificate) new Certificate(signCloudResp.getCert(), agreementUUID, this);
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
    public boolean assignAgreement(String userName, String agreementUUID, String otpRequestID, String otp) throws Throwable {
//        System.out.println("____________agreements/assign____________");
        AgreementAssignRequest agreementAssignRequest = new AgreementAssignRequest();
        agreementAssignRequest.setAgreementUUID(agreementUUID);
        agreementAssignRequest.setUser(userName);
        agreementAssignRequest.setUserType(USERNAME);
        agreementAssignRequest.setAuthorizeCode(otp);
        agreementAssignRequest.setRequestID(otpRequestID);
        agreementAssignRequest.setLang(this.lang);

        String jsonReq = Utils.gsTmp.toJson(agreementAssignRequest);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "agreements/assign", jsonReq, bearer);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        AgreementAssignResponse signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), AgreementAssignResponse.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            login();
            return assignAgreement(userName, agreementUUID, otpRequestID, otp);
        } else if (signCloudResp.getError() == 3055) {
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription(), signCloudResp.getAgreementUUID());
        } else if (signCloudResp.getError() != 0) {
            System.err.println("err code: " + signCloudResp.getError());
            System.err.println("error description: " + signCloudResp.getErrorDescription());
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }

        return true;
    }

    @Override
    public String sendUserOTPByUserName(OTPType otpType, String username) throws Throwable {
//        System.out.println("____________owner/sendOTP____________");
        SendOwnerOTPRequest request = new SendOwnerOTPRequest();
        request.setUser(username);
        request.setUserType(USERNAME);
        request.setOtpType(otpType);
        request.setLang(this.lang);

        //request.requestID = this.requestID;
        String jsonReq = Utils.gsTmp.toJson(request);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "owner/sendOTP", jsonReq, bearer);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Response signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), Response.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            login();
            return sendUserOTPByUserName(otpType, username);
        } else if (signCloudResp.getError() != 0) {
            System.out.println("Err code: " + signCloudResp.getError());
            System.out.println("Err Desscription: " + signCloudResp.getErrorDescription());
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }
        return signCloudResp.getResponseID();
    }

    @Override
    public String sendUserOTPByAgreement(OTPType otpType, String agreementUUID) throws Throwable {
//        System.out.println("____________owner/sendOTP____________");
        SendOwnerOTPRequest request = new SendOwnerOTPRequest();
        request.setAgreementUUID(agreementUUID);
        request.setOtpType(otpType);
        request.setLang(this.lang);

        String jsonReq = Utils.gsTmp.toJson(request);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "owner/sendOTP", jsonReq, bearer);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Response signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), Response.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            login();
            return sendUserOTPByAgreement(otpType, agreementUUID);
        } else if (signCloudResp.getError() != 0) {
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }
        return signCloudResp.getResponseID();
    }

    @Override
    public String authorize(String agreementUUID, String credentialID, int numSignatures, DocumentDigests doc, SignAlgo signAlgo, String authorizeCode) throws Throwable {
        return authorize(agreementUUID, credentialID, numSignatures, doc, signAlgo, null, authorizeCode);
    }

    @Override
    public String authorize(String agreementUUID, String credentialID, int numSignatures, DocumentDigests doc, SignAlgo signAlgo, String otpRequestID, String otp) throws Throwable {
//        System.out.println("____________credentials/authorize____________");
        AuthorizeRequest request = new AuthorizeRequest();
        request.setAgreementUUID(agreementUUID);
        request.setCredentialID(credentialID);
        request.setNumSignatures(numSignatures);
        request.setDocumentDigests(doc);
        request.setSignAlgo(signAlgo);
        request.setRequestID(otpRequestID);
        request.setAuthorizeCode(otp);
        request.setLang(this.lang);

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
            return authorize(agreementUUID, credentialID, numSignatures, doc, signAlgo, otpRequestID, otp);
        } else if (signCloudResp.getError() != 0) {
            System.out.println("Err code: " + signCloudResp.getError());
            System.out.println("Err Desscription: " + signCloudResp.getErrorDescription());
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }

        return signCloudResp.getSAD();
    }

    @Override
    public String authorize(String agreementUUID, String credentialID, int numSignatures, DocumentDigests doc, SignAlgo signAlgo, MobileDisplayTemplate displayTemplate) throws Throwable {
//        System.out.println("____________credentials/authorize____________");
        AuthorizeRequest request = new AuthorizeRequest();
        request.setAgreementUUID(agreementUUID);
        request.setCredentialID(credentialID);
        request.setNumSignatures(numSignatures);
        request.setDocumentDigests(doc);
        request.setSignAlgo(signAlgo);

        request.setNotificationMessage(displayTemplate.getNotificationMessage());
        request.setMessageCaption(displayTemplate.getMessageCaption());
        request.setMessage(displayTemplate.getMessage());
        request.setLogoURI(displayTemplate.getLogoURI());
        request.setRpIconURI(displayTemplate.getRpIconURI());
        request.setBgImageURI(displayTemplate.getBgImageURI());
        request.setRpName(displayTemplate.getRpName());
        request.setScaIdentity(displayTemplate.getScaIdentity());
        request.setVcEnabled(displayTemplate.isVcEnabled());
        request.setAcEnabled(displayTemplate.isAcEnabled());
        request.setLang(this.lang);

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
            return authorize(agreementUUID, credentialID, numSignatures, doc, signAlgo, displayTemplate);
        } else if (signCloudResp.getError() != 0) {
            System.out.println("Err code: " + signCloudResp.getError());
            System.out.println("Err Desscription: " + signCloudResp.getErrorDescription());
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }
        return signCloudResp.getSAD();
    }

    @Override
    public List<byte[]> signHash(String agreementUUID, String credentialID, DocumentDigests documentDigest, SignAlgo signAlgo, String SAD) throws Throwable {
//        System.out.println("____________signatures/signHash____________");
        SignHashRequest request = new SignHashRequest();
        request.setAgreementUUID(agreementUUID);
        request.setCredentialID(credentialID);
        request.setDocumentDigests(documentDigest);
        request.setSignAlgo(signAlgo);
        request.setSAD(SAD);
        request.setLang(this.lang);

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
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            login();
            return signHash(agreementUUID, credentialID, documentDigest, signAlgo, SAD);
        } else if (signCloudResp.getError() != 0) {
            System.out.println("Err code: " + signCloudResp.getError());
            System.out.println("Err Desscription: " + signCloudResp.getErrorDescription());
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }
        return signCloudResp.getSignatures();
    }

    @Override
    public String sendOTP(String agreementUUID, String credentialID, String notificationTemplate, String notificationSubject) throws Throwable {
//        System.out.println("____________credentials/sendOTP____________");
        CredentialSendOTPRequest request = new CredentialSendOTPRequest();
        request.setAgreementUUID(agreementUUID);
        request.setCredentialID(credentialID);
        request.setNotificationSubject(notificationSubject);
        request.setNotificationTemplate(notificationTemplate);
        request.setLang(this.lang);

        String jsonReq = Utils.gsTmp.toJson(request);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "credentials/sendOTP", jsonReq, bearer);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Response signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), Response.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            login();
            return sendOTP(agreementUUID, credentialID, notificationTemplate, notificationSubject);
        } else if (signCloudResp.getError() != 0) {
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }
        return signCloudResp.getResponseID();
    }

    @Override
    public boolean createUser(String userName, String email, String phone) throws Throwable {
//        System.out.println("____________owner/create____________");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(userName);
        request.setEmail(email);
        request.setPhone(phone);

        request.setLang(this.lang);

        String jsonReq = Utils.gsTmp.toJson(request);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "owner/create", jsonReq, bearer);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        CreateUserResponse signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), CreateUserResponse.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            login();
            return createUser(userName, email, phone);
        } else if (signCloudResp.getError() != 0) {
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }
        return true;
    }

    @Override
    public List<byte[]> signDoc(String agreementUUID, String credentialID, HashMap<SignedPropertyType, Object> signedProps, List<byte[]> docs, HashAlgorithmOID hashAlgo, SignAlgo signAlgo, String SAD) throws Throwable {
//        System.out.println("____________signatures/signDoc____________");
        SignDocRequest request = new SignDocRequest();
        request.setAgreementUUID(agreementUUID);
        request.setCredentialID(credentialID);
        request.setSignedProps(signedProps);
        request.setDocuments(docs);
        request.setHashAlgorithmOID(hashAlgo);
        request.setSignAlgo(signAlgo);
        request.setSAD(SAD);
        request.setLang(this.lang);

        String jsonReq = Utils.gsTmp.toJson(request);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "signatures/signDoc", jsonReq, bearer);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        SignDocResponse signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), SignDocResponse.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            login();
            return signDoc(agreementUUID, credentialID, signedProps, docs, hashAlgo, signAlgo, SAD);
        } else if (signCloudResp.getError() != 0) {
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }
        return signCloudResp.getDocumentWithSignature();
    }

    @Override
    public List<CertificateAuthority> getCertificateAuthorities() throws Throwable {
//        System.out.println("____________systems/getCertificateAuthorities____________");
        Request request = new Request();
        request.setLang(this.lang);

        String jsonReq = Utils.gsTmp.toJson(request);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "systems/getCertificateAuthorities", jsonReq, bearer);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        GetCertificateAuthoritiesResponse signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), GetCertificateAuthoritiesResponse.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            login();
            return getCertificateAuthorities();
        } else if (signCloudResp.getError() != 0) {
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }
        List<CertificateAuthority> cas = new ArrayList<CertificateAuthority>();

        return signCloudResp.getCertificateAuthorities();
    }

    @Override
    public List<CertificateProfile> getCertificateProfies(String caName) throws Throwable {
//        System.out.println("____________systems/getCertificateProfiles____________");
        GetCertificateProfilesRequest request = new GetCertificateProfilesRequest();
        request.setCaName(caName);
        request.setLang(this.lang);

        String jsonReq = Utils.gsTmp.toJson(request);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "systems/getCertificateProfiles", jsonReq, bearer);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        GetCertificateProfilesResponse signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), GetCertificateProfilesResponse.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            login();
            return getCertificateProfies(caName);
        } else if (signCloudResp.getError() != 0) {
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }
        List<CertificateAuthority> cas = new ArrayList<CertificateAuthority>();

        return signCloudResp.getProfiles();
    }

    @Override
    public List<Province> getStatesOrProvinces() throws Throwable {
//        System.out.println("____________systems/getStatesOrProvinces____________");
        GetStateProvinceRequest request = new GetStateProvinceRequest();
        request.setCountry("VN");
        request.setLang(this.lang);

        String jsonReq = Utils.gsTmp.toJson(request);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "systems/getStatesOrProvinces", jsonReq, bearer);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        GetStateProvinceResponse signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), GetStateProvinceResponse.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            login();
            return getStatesOrProvinces();
        } else if (signCloudResp.getError() != 0) {
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }

        return signCloudResp.getProvinces();
    }

    @Override
    public IssueCertificateResponse createCertificate(String userName, String otpRequestID, String otp, String certProfile, SharedMode sharedMode, AuthMode authMode, int multiSign, CertificateDetails certDetails) throws Throwable {
//        System.out.println("____________credentials/issue____________");
        IssueCertificateRequest request = new IssueCertificateRequest();
        request.setUser(userName);
        request.setUserType(USERNAME);
        request.setCertificateProfile(certProfile);
        request.setSharedMode(sharedMode);
        request.setAuthMode(authMode);
        request.setMultisign(multiSign);
        request.setCertDetails(certDetails);
        request.setRequestID(otpRequestID);
        request.setAuthorizeCode(otp);
        request.setLang(this.lang);

        String jsonReq = Utils.gsTmp.toJson(request);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "credentials/issue", jsonReq, bearer);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        IssueCertificateResponse signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), IssueCertificateResponse.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            login();
            return createCertificate(userName, otpRequestID, otp, certProfile, sharedMode, authMode, multiSign, certDetails);
        } else if (signCloudResp.getError() != 0) {
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }
        List<CertificateAuthority> cas = new ArrayList<CertificateAuthority>();

        if (signCloudResp.getCsr() != null) {
            System.out.println("Csr = " + signCloudResp.getCsr());
        } else {
            System.out.println("Certificate = " + signCloudResp.getCertificates()[0]);
        }
        return signCloudResp;
    }

    @Override
    public ICertificate importCertificate(String agreementUUID, String credentialID, String cert) throws Throwable {
//        System.out.println("____________credentials/import____________");
        ImportCertificateRequest request = new ImportCertificateRequest();
        request.setAgreementUUID(agreementUUID);
        request.setCredentialID(credentialID);
        request.setCertificate(cert);
        request.setLang(this.lang);

        String jsonReq = Utils.gsTmp.toJson(request);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "credentials/import", jsonReq, bearer);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        IssueCertificateResponse signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), IssueCertificateResponse.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            login();
            return importCertificate(agreementUUID, credentialID, cert);
        } else if (signCloudResp.getError() != 0) {
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        }

        return new Certificate(new BaseCertificateInfo(), agreementUUID, this);
    }

}
