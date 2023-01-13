/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.qrypto.apiObject.BiometricEvidenceRequest;
import vn.mobileid.id.qrypto.apiObject.DataCreateOwner;
import vn.mobileid.id.qrypto.apiObject.DataGetChallenge;
import vn.mobileid.id.qrypto.apiObject.RequireBiometricEvidence;
import vn.mobileid.id.qrypto.apiObject.TokenResponse;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class EIDService {

    private static final Logger LOG = LogManager.getLogger(Configuration.class);

    private String langVN = "vn";
    private String langEN = "en";
    private String methodName = "POST";
    private String accessKey;
    private String secretKey;
    private String regionName;
    private String serviceName;
    private static int timeOut = 3000;
    private String xApiKey;
    private String contentType = "application/json";
    private String sessionToken;
    private String bearerToken;

    private static EIDService instant;

    public static EIDService getInstant() {
        if (instant == null) {
            instant = new EIDService();
        }
        return instant;
    }

    private EIDService() {
        init();
    }

    private void init() {
        try {
            Properties prop = new Properties();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream appProperties = loader.getResourceAsStream("AWS.properties");
            if (appProperties != null) {
                prop.load(appProperties);
                if (prop.keySet() == null) {
                    String propertiesFile = Utils.getPropertiesFile("AWS.properties");
                    if (propertiesFile != null) {
                        LOG.info("Read the configuation file from " + propertiesFile);
                        InputStream in = new FileInputStream(propertiesFile);
                        prop.load(in);
                        in.close();
                    } else {
                        LOG.error("Cannot find any configuation file. This is a big problem");
                    }
                }
                appProperties.close();
            } else {
                String propertiesFile = Utils.getPropertiesFile("AWS.properties");
                if (propertiesFile != null) {
                    LOG.info("Read the configuation file from " + propertiesFile);
                    prop.load(new FileInputStream(propertiesFile));
                } else {
                    LOG.error("Cannot find any configuation file. This is a big problem");
                }
            }

            //Parse data
            accessKey = prop.getProperty("dtis.aws.accessKey") == null ? "" : prop.getProperty("dtis.aws.accessKey");
            secretKey = prop.getProperty("dtis.aws.secretKey") == null ? "" : prop.getProperty("dtis.aws.secretKey");
            regionName = prop.getProperty("dtis.aws.regionName") == null ? "" : prop.getProperty("dtis.aws.regionName");
            serviceName = prop.getProperty("dtis.aws.serviceName") == null ? "" : prop.getProperty("dtis.aws.serviceName");
            xApiKey = prop.getProperty("dtis.aws.xApiKey") == null ? "" : prop.getProperty("dtis.aws.xApiKey");
            sessionToken = prop.getProperty("dtis.aws.sessionToken") == null ? "" : prop.getProperty("dtis.aws.sessionToken");
        } catch (Exception ex) {
            System.out.println(ex);
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while getting config EID - Details:" + ex);
            }
        }
    }

    //v1/e-verification/oidc/token    
    private Object v1VeriOidcToken() {
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Get token from dtis");
        }
        try {
            AWSCall aWSCallGetToken = new AWSCall(
                    methodName,
                    accessKey,
                    secretKey,
                    regionName,
                    serviceName,
                    timeOut,
                    xApiKey,
                    contentType);

            Object abc = aWSCallGetToken.v1VeriOidcToken(EIDConstant.V1_EVERIFICATION_OIDC_TOKEN, sessionToken);

            this.bearerToken = aWSCallGetToken.bearerToken;
            return abc;
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot get Token from dtis - Details:" + e);
            }
            return null;
        }
    }

    //v1/owner/challenge
    private Object v1OwnerChallenge(DataGetChallenge dataGetChallenge) {
        try {
            AWSCall aWSCallGetChallenge = new AWSCall(
                    methodName,
                    accessKey,
                    secretKey,
                    regionName,
                    serviceName,
                    timeOut,
                    xApiKey,
                    contentType);

//        String s = "{ \"authorizationData\": { \"authorizationTitle\": \"AUTHORIZATION DATA\", \"authContentList\": [ { \"ordinary\": 0, \"title\": \"Auth Header Group 1\", \"text\": \"String Value 1\" },{ \"ordinary\": 1, \"title\": \"Auth Header Group 2\", \"text\": \"String Value 2\" } ], \"multipleSelectList\": [ { \"ordinary\": 2, \"title\": \"Multiple Header Group 1\", \"label\": \"Add Multiple Select\", \"multipleSelect\": { \"Multiple Content 1\": true, \"Multiple Content 2\": false } }, { \"ordinary\": 3, \"title\": \"Multiple Header Group 2\", \"label\": \"Add Multiple Select\", \"multipleSelect\": { \"Multiple Content 1\": true, \"Multiple Content 2\": false } } ], \"singleSelectList\": [ { \"ordinary\": 4, \"title\": \"Single Header Group 1\", \"label\": \"Add Single Select\", \"singleSelect\": { \"Single Content 1\": true, \"Single Content 2\": false } },{ \"ordinary\": 5, \"title\": \"Single Header Group 2\", \"label\": \"Add Single Select\", \"singleSelect\": { \"Single Content 1\": true, \"Single Content 2\": false } } ], \"nameValuePairList\": [ { \"ordinary\": 6, \"title\": \"Name Value Pair Header Group 1\", \"label\": \"Add Name Value Pair\", \"nameValuePair\": { \"key1\": \"value 1\", \"key2\": \"value 2\" } },{ \"ordinary\": 7, \"title\": \"Name Value Pair Header Group 2\", \"label\": \"Add Name Value Pair\", \"nameValuePair\": { \"key1\": \"value 1\", \"key2\": \"value 2\" } } ] } }";
//        String s = "{ \"authorizationTitle\": \"AUTHORIZATION DATA\", \"authContentList\": [ { \"ordinary\": 0, \"title\": \"Auth Header Group 1\", \"text\": \"String Value 1\" },{ \"ordinary\": 1, \"title\": \"Auth Header Group 2\", \"text\": \"String Value 2\" } ], \"multipleSelectList\": [ { \"ordinary\": 2, \"title\": \"Multiple Header Group 1\", \"label\": \"Add Multiple Select\", \"multipleSelect\": { \"Multiple Content 1\": true, \"Multiple Content 2\": false } }, { \"ordinary\": 3, \"title\": \"Multiple Header Group 2\", \"label\": \"Add Multiple Select\", \"multipleSelect\": { \"Multiple Content 1\": true, \"Multiple Content 2\": false } } ], \"singleSelectList\": [ { \"ordinary\": 4, \"title\": \"Single Header Group 1\", \"label\": \"Add Single Select\", \"singleSelect\": { \"Single Content 1\": true, \"Single Content 2\": false } },{ \"ordinary\": 5, \"title\": \"Single Header Group 2\", \"label\": \"Add Single Select\", \"singleSelect\": { \"Single Content 1\": true, \"Single Content 2\": false } } ], \"nameValuePairList\": [ { \"ordinary\": 6, \"title\": \"Name Value Pair Header Group 1\", \"label\": \"Add Name Value Pair\", \"nameValuePair\": { \"key1\": \"value 1\", \"key2\": \"value 2\" } },{ \"ordinary\": 7, \"title\": \"Name Value Pair Header Group 2\", \"label\": \"Add Name Value Pair\", \"nameValuePair\": { \"key1\": \"value 1\", \"key2\": \"value 2\" } } ] }"
            if (dataGetChallenge.username == null) {
                return aWSCallGetChallenge.v1OwnerChallenge(
                        EIDConstant.V1_OWNER_CHALLENGE,
                        dataGetChallenge.challenge_type,
                        dataGetChallenge.transaction_data,
                        this.bearerToken);
            } else {
                return aWSCallGetChallenge.v1OwnerChallengeHasUsername(
                        EIDConstant.V1_OWNER_CHALLENGE,
                        dataGetChallenge.challenge_type,
                        dataGetChallenge.username,
                        dataGetChallenge.transaction_data,
                        this.bearerToken);
            }
        } catch (Exception e) {
            return null;
        }
    }

    //v1/owner/create
    private Object v1OwnerCreate(DataCreateOwner dataCreateOwner) {
        try {
            AWSCall aWSCallCreateOwner = new AWSCall(
                    methodName,
                    accessKey,
                    secretKey,
                    regionName,
                    serviceName,
                    timeOut,
                    xApiKey,
                    contentType);
            System.out.println("user: " + dataCreateOwner.username);
            System.out.println("pa: " + dataCreateOwner.pa);
//        String s = "{ \"authorizationData\": { \"authorizationTitle\": \"AUTHORIZATION DATA\", \"authContentList\": [ { \"ordinary\": 0, \"title\": \"Auth Header Group 1\", \"text\": \"String Value 1\" },{ \"ordinary\": 1, \"title\": \"Auth Header Group 2\", \"text\": \"String Value 2\" } ], \"multipleSelectList\": [ { \"ordinary\": 2, \"title\": \"Multiple Header Group 1\", \"label\": \"Add Multiple Select\", \"multipleSelect\": { \"Multiple Content 1\": true, \"Multiple Content 2\": false } }, { \"ordinary\": 3, \"title\": \"Multiple Header Group 2\", \"label\": \"Add Multiple Select\", \"multipleSelect\": { \"Multiple Content 1\": true, \"Multiple Content 2\": false } } ], \"singleSelectList\": [ { \"ordinary\": 4, \"title\": \"Single Header Group 1\", \"label\": \"Add Single Select\", \"singleSelect\": { \"Single Content 1\": true, \"Single Content 2\": false } },{ \"ordinary\": 5, \"title\": \"Single Header Group 2\", \"label\": \"Add Single Select\", \"singleSelect\": { \"Single Content 1\": true, \"Single Content 2\": false } } ], \"nameValuePairList\": [ { \"ordinary\": 6, \"title\": \"Name Value Pair Header Group 1\", \"label\": \"Add Name Value Pair\", \"nameValuePair\": { \"key1\": \"value 1\", \"key2\": \"value 2\" } },{ \"ordinary\": 7, \"title\": \"Name Value Pair Header Group 2\", \"label\": \"Add Name Value Pair\", \"nameValuePair\": { \"key1\": \"value 1\", \"key2\": \"value 2\" } } ] } }";

            return aWSCallCreateOwner.v1OwnerCreate(
                    EIDConstant.V1_OWNER_CREATE,
                    dataCreateOwner.username,
                    dataCreateOwner.email,
                    dataCreateOwner.phone,
                    dataCreateOwner.pa,
                    dataCreateOwner.face_matching,
                    this.bearerToken);
        } catch (Exception e) {
            return null;
        }
    }

    //v1/e-verification/eid/verify
    private Object v1EidVerification(DataCreateOwner dataCreateOwner){
        try{
        AWSCall aWSCallEidVerification = new AWSCall(
            methodName,
            accessKey,
            secretKey,
            regionName,
            serviceName,
            timeOut,
            xApiKey,
            contentType); 
        
        if(dataCreateOwner.typeVerification == "fingerPrint"){
            return aWSCallEidVerification.v1EidVerifiFinger(
                EIDConstant.V1_EID_VERIFY,
                dataCreateOwner.username,
                dataCreateOwner.pa,
                dataCreateOwner.face_matching,
                this.bearerToken);
        }
        else{
            return aWSCallEidVerification.v1EidVerifi(
                EIDConstant.V1_EID_VERIFY,
                dataCreateOwner.username,
                dataCreateOwner.pa,
                dataCreateOwner.face_matching,
                this.bearerToken);
        }
        } catch (Exception e){
            return null;
        }
        
    } 
    
    private Object getBiometricEvidence(RequireBiometricEvidence type){
        String requestID = Utils.generateTransactionID_noRP();
        BiometricEvidenceRequest request = new BiometricEvidenceRequest();
        request.setRequestID(requestID);
        request.setCmdType("BiometricAuthentication");
        request.setTimeOutInterval(60);
        request.setBiometricType(type);
        
        String host = "";
        String port = "";
//        URI uri = "";
        
               return null;
        
    }
    
    //====================GET - SET=================================
    public String getLangVN() {
        return langVN;
    }

    public void setLangVN(String langVN) {
        this.langVN = langVN;
    }

    public String getLangEN() {
        return langEN;
    }

    public void setLangEN(String langEN) {
        this.langEN = langEN;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public static int getTimeOut() {
        return timeOut;
    }

    public static void setTimeOut(int timeOut) {
        EIDService.timeOut = timeOut;
    }

    public String getxApiKey() {
        return xApiKey;
    }

    public void setxApiKey(String xApiKey) {
        this.xApiKey = xApiKey;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public static void main(String[] args) {
        System.out.println(EIDService.getInstant().getAccessKey());
        System.out.println(EIDService.getInstant().getRegionName());
        System.out.println(EIDService.getInstant().getxApiKey());
        System.out.println(EIDService.getInstant().getSessionToken());
        System.out.println(EIDService.getInstant().getSecretKey());
        
        TokenResponse a = (TokenResponse)EIDService.getInstant().v1VeriOidcToken();
        
        String temp = "A ";
    }
}
