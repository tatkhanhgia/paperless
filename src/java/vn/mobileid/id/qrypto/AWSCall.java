/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;
import restful.sdk.API.HTTPUtils;
import vn.mobileid.aws.client.AWSV4Auth;
import vn.mobileid.aws.client.AWSV4Constants;
import vn.mobileid.id.qrypto.apiObject.CreateOwnerRequest;
import vn.mobileid.id.qrypto.apiObject.CreateOwnerResponse;
import vn.mobileid.id.qrypto.apiObject.EidVerifiRequest;
import vn.mobileid.id.qrypto.apiObject.EidVerifiResponse;
import vn.mobileid.id.qrypto.apiObject.GetChallengeRequest;
import vn.mobileid.id.qrypto.apiObject.GetChallengeResponse;
import vn.mobileid.id.qrypto.apiObject.TokenResponse;
import vn.mobileid.id.utils.HttpUtils;

/**
 *
 * @author Mobile 22
 */
public class AWSCall {
    final protected static String FILE_DIRECTORY_PDF = "file/";
    private URL url;
    private String httpMethod;
    private String accessKey;
    private String secretKey;
    private String regionName;
    private String serviceName;
    private int timeOut;
    private String xApiKey;
    private String contentType;
    public String sessionToken;
    public String bearerToken;
    private TreeMap<String, String> awsHeaders;
    private AWSV4Auth.Builder builder;

    
    static {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
            public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
                    return true;
            }
        });
    }
    
    
    public AWSCall(String httpMethod, String accessKey,
            String secretKey, String regionName,
            String serviceName, int timeOut,
            String xApiKey, String contentType) throws MalformedURLException {
        this.httpMethod = httpMethod;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.regionName = regionName;
        this.serviceName = serviceName;
        this.timeOut = timeOut;
        this.xApiKey = xApiKey;
        this.contentType = contentType;
        this.url = new URL(EIDConstant.BASE_URL);

        this.awsHeaders = new TreeMap<>();
        awsHeaders.put(AWSV4Constants.X_API_KEY, this.xApiKey);
        awsHeaders.put(AWSV4Constants.CONTENT_TYPE, this.contentType);

        this.builder = new AWSV4Auth.Builder(accessKey, secretKey)
                .regionName(regionName)
                .serviceName(serviceName)
                .httpMethodName(httpMethod)// GET, PUT, POST, DELETE
                .queryParametes(null) // query parameters if any
                .awsHeaders(awsHeaders); // aws header parameters        
    }

    //AWS4Auth
    public Map<String, String> getAWSV4Auth(String payload, String function, String token) throws MalformedURLException {
        this.awsHeaders.put(EIDConstant.SESSION_TOKEN, token);
        AWSV4Auth aWSV4Auth = this.builder
                .endpointURI(new URL(this.url.toString())) //https://id.mobile-id.vn/dtis/v1/e-verification/oidc/token
                .payload(payload)
                .build();
        return aWSV4Auth.getHeaders();
    }

    // v1/e-verification/oidc/token
    public Object v1VeriOidcToken(String function, String token) throws MalformedURLException, IOException {
        //Send Post
        String jsonResp = HttpUtils.invokeHttpRequest(
                this.url = new URL(EIDConstant.BASE_URL + function),
                this.httpMethod,
                this.timeOut,
                getAWSV4Auth(null, function, token),
                null);
        //Response
        ObjectMapper objectMapper = new ObjectMapper();
        TokenResponse tokenResponse = objectMapper.readValue(jsonResp, TokenResponse.class);

        //Past Bearer for Step 2 (v1OwnerChallenge)
        this.bearerToken = "Bearer " + tokenResponse.access_token;
        return tokenResponse;
    }

    // v1/owner/challenge
    public Object v1OwnerChallenge(
            String function,
            String challenge_type,
            String transaction_data,
            String token) throws MalformedURLException, IOException {

        
        //Request
        GetChallengeRequest challengeRequest = new GetChallengeRequest();
        challengeRequest.setChallenge_type(challenge_type);
        challengeRequest.setTransaction_data(transaction_data);
        challengeRequest.setLang("VN");

        //Convert Request To Json
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = objectMapper.writeValueAsString(challengeRequest);

        //Send Post
        String jsonChallengeResp = HttpUtils.invokeHttpRequest(
                this.url = new URL(EIDConstant.BASE_URL + function),
                this.httpMethod,
                this.timeOut,
                getAWSV4Auth(payload, function, token),
                payload);

        
        //Response
        GetChallengeResponse getChallengeResponse = objectMapper.readValue(jsonChallengeResp, GetChallengeResponse.class);
        return getChallengeResponse;
    }
    
    // v1/owner/challenge
    public Object v1OwnerChallengeHasUsername(
            String function,
            String challenge_type,
            String username,
            String transaction_data,
            String token) throws MalformedURLException, IOException {

        
        //Request
        GetChallengeRequest challengeRequest = new GetChallengeRequest();
        challengeRequest.setChallenge_type(challenge_type);
        challengeRequest.setUsername(username);
        challengeRequest.setTransaction_data(transaction_data);
        challengeRequest.setLang("VN");

        //Convert Request To Json
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = objectMapper.writeValueAsString(challengeRequest);

        //Send Post
        String jsonChallengeResp = HttpUtils.invokeHttpRequest(
                this.url = new URL(EIDConstant.BASE_URL + function),
                this.httpMethod,
                this.timeOut,
                getAWSV4Auth(payload, function, token),
                payload);

        
        //Response
        GetChallengeResponse getChallengeResponse = objectMapper.readValue(jsonChallengeResp, GetChallengeResponse.class);
        return getChallengeResponse;
    }

    //    ---------------------------------------------------------------------------   
    // v1/owner/create
    public Object v1OwnerCreate(
            String function,
            String username,
            String email,
            String phone,
            String pa,
            String face_matching,
            String token) throws MalformedURLException, IOException {

        //Request
        CreateOwnerRequest ownerRequest = new CreateOwnerRequest();
        ownerRequest.setUsername(username);
        ownerRequest.setEmail(email);
        ownerRequest.setPhone(phone);
        ownerRequest.setPa(pa);
        ownerRequest.setFace_matching(face_matching);
//        ownerRequest.setFingerprint_verification(face_matching);

        //Convert Request To Json
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = objectMapper.writeValueAsString(ownerRequest);
        
        //Send Post
        String jsonCreateOwnerResp = HttpUtils.invokeHttpRequest(
                this.url = new URL(EIDConstant.BASE_URL + function),
                this.httpMethod,
                this.timeOut,
                getAWSV4Auth(payload, function, token),
                payload);
        //Response
        CreateOwnerResponse ownerResponse = objectMapper.readValue(jsonCreateOwnerResp, CreateOwnerResponse.class);
        return ownerResponse;
    }

    //    ---------------------------------------------------------------------------   
    // v1/e-verification/eid/verify
    public Object v1EidVerifi(
            String function,
            String username,
            String pa,
            String face_matching,
            String token) throws MalformedURLException, IOException {

        //Request
        EidVerifiRequest eidVerifyRequest = new EidVerifiRequest();
        eidVerifyRequest.setUsername(username);
        eidVerifyRequest.setPa(pa);
        eidVerifyRequest.setFace_matching(face_matching);
//        eidVerifyRequest.setFingerprint_verification(face_matching);

        //Convert Request To Json
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = objectMapper.writeValueAsString(eidVerifyRequest);
        
        //Send Post
        String jsonEidVerifyResp = HttpUtils.invokeHttpRequest(
                this.url = new URL(EIDConstant.BASE_URL + function),
                this.httpMethod,
                this.timeOut,
                getAWSV4Auth(payload, function, token),
                payload);
        //Response
        EidVerifiResponse eidVerifyResponse = objectMapper.readValue(jsonEidVerifyResp, EidVerifiResponse.class);
        return eidVerifyResponse;
    }

    // v1/e-verification/eid/verify
    public Object v1EidVerifiFinger(
            String function,
            String username,
            String pa,
            String fingerprint_verification,
            String token) throws MalformedURLException, IOException {

        //Request
        EidVerifiRequest eidVerifyRequest = new EidVerifiRequest();
        eidVerifyRequest.setUsername(username);
        eidVerifyRequest.setPa(pa);
        eidVerifyRequest.setFingerprint_verification(fingerprint_verification);

        //Convert Request To Json
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = objectMapper.writeValueAsString(eidVerifyRequest);
        
        //Send Post
        String jsonEidVerifyResp = HttpUtils.invokeHttpRequest(
                this.url = new URL(EIDConstant.BASE_URL + function),
                this.httpMethod,
                this.timeOut,
                getAWSV4Auth(payload, function, token),
                payload);
        //Response
        EidVerifiResponse eidVerifyResponse = objectMapper.readValue(jsonEidVerifyResp, EidVerifiResponse.class);
        return eidVerifyResponse;
    }
}
