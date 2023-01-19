/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.eid.object.InfoDetails;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.everification.object.DataCreateOwner;
import vn.mobileid.id.everification.object.DataGetChallenge;
import vn.mobileid.id.everification.object.GetChallengeResponse;
import vn.mobileid.id.eid.object.RequireBiometricEvidence;
import vn.mobileid.id.eid.object.TokenResponse;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.utils.Utils;
//import vn.mobileid.id.general.WS.WebSocketEndpoint;
import vn.mobileid.id.eid.object.InterfaceCommunicationEID;
import vn.mobileid.id.eid.object.RequireInfoDetailsGet;

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
    private int timeOut = 3000;
    private String xApiKey;
    private String contentType = "application/json";
    private String sessionToken;
    private String URI = "ws://127.0.0.1:9505/ISPlugin";

    public static EIDService instant;

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
    public Object v1VeriOidcToken() {
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

            TokenResponse abc = (TokenResponse) aWSCallGetToken.v1VeriOidcToken(EIDConstant.V1_EVERIFICATION_OIDC_TOKEN, sessionToken);

            return abc;
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot get Token from dtis - Details:" + e);
            }
            return null;
        }
    }

    //v1/owner/challenge
    public Object v1OwnerChallenge(DataGetChallenge dataGetChallenge, String token) {
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
                        token);
            } else {
                return aWSCallGetChallenge.v1OwnerChallengeHasUsername(
                        EIDConstant.V1_OWNER_CHALLENGE,
                        dataGetChallenge.challenge_type,
                        dataGetChallenge.username,
                        dataGetChallenge.transaction_data,
                        token);
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot get Challenge from dtis - Details:" + e);
            }
            return null;
        }
    }

    //v1/owner/create
    public Object v1OwnerCreate(DataCreateOwner dataCreateOwner, String token) {
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
                    token);
        } catch (Exception e) {
            return null;
        }
    }

    //v1/e-verification/eid/verify
    public Object v1EidVerification(DataCreateOwner dataCreateOwner, String token) {
        try {
            AWSCall aWSCallEidVerification = new AWSCall(
                    methodName,
                    accessKey,
                    secretKey,
                    regionName,
                    serviceName,
                    timeOut,
                    xApiKey,
                    contentType);

            if (dataCreateOwner.typeVerification == "fingerPrint") {
                return aWSCallEidVerification.v1EidVerifiFinger(
                        EIDConstant.V1_EID_VERIFY,
                        dataCreateOwner.username,
                        dataCreateOwner.pa,
                        dataCreateOwner.face_matching,
                        token);
            } else {
                return aWSCallEidVerification.v1EidVerifi(
                        EIDConstant.V1_EID_VERIFY,
                        dataCreateOwner.username,
                        dataCreateOwner.pa,
                        dataCreateOwner.face_matching,
                        token);
            }
        } catch (Exception e) {
            return null;
        }

    }

    //Get evidence after authenticate - PENDING
    private InterfaceCommunicationEID getBiometricEvidence(String token, InterfaceCommunicationEID type) {
//        try {
//            WebSocketEndpoint socket = WebSocketEndpoint.createWebSocket(langEN, new URI(URI));
//
//        } catch (URISyntaxException ex) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Cannot open socket ISPlugin");
//            }
//            return null;
//        }
        return null;
    }

    //Read Identity card - PENDING
//    private InterfaceCommunicationEID<InfoDetails> getInformationDetails(String token, InterfaceCommunicationEID object) {
//        try {
//            WebSocketEndpoint socket = WebSocketEndpoint.createWebSocket(token, new URI(URI));
//            if (socket == null) {
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Cannot open socket ISPlugin");
//                }
//                return null;
//            }
//            socket.sendMessage(token, new ObjectMapper().writeValueAsString(object));
//            String json = socket.getMessage(token);
//            InterfaceCommunicationEID<InfoDetails> response = new InterfaceCommunicationEID<InfoDetails>();            
//            ObjectMapper mapper = new ObjectMapper();
//            response = mapper.readValue(json, response.getClass());
//            return response;
//        } catch (URISyntaxException ex) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Cannot open socket ISPlugin");
//            }
//            return null;
//        } catch (Exception ex) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Cannot open socket ISPlugin");
//            }
//            return null;
//        }        
//    }
  
  

    public static void main(String[] args) {
//        System.out.println(EIDService.getInstant().getAccessKey());
//        System.out.println(EIDService.getInstant().getRegionName());
//        System.out.println(EIDService.getInstant().getxApiKey());
//        System.out.println(EIDService.getInstant().getSessionToken());
//        System.out.println(EIDService.getInstant().getSecretKey());

        //Get Token
        TokenResponse response = (TokenResponse) EIDService.getInstant().v1VeriOidcToken();
        String token = "Bearer " + response.access_token;
        System.out.println("Bearer Token:" + token);

        //Get Challenge
        DataGetChallenge data = new DataGetChallenge();
        data.challenge_type = "EID";
        data.transaction_data = "transactionData";
        GetChallengeResponse response2 = (GetChallengeResponse) EIDService.getInstant().v1OwnerChallenge(data, token);
        System.out.println("====Get Challenged====");
        System.out.println("Transaction:" + response2.getTransactionId());
        System.out.println("Message:" + response2.getMessage());
        System.out.println("Card no:" + response2.getCard_no());
        System.out.println("Challenged:" + response2.getChallenge());
        //Get Challenged from a string contains it
        String temp = response2.getChallenge();
        String challenged = temp.substring(temp.indexOf("challengeValue") + 17, temp.indexOf("transactionData") - 3);
        System.out.println("Challenged:" + challenged);

        //Read Document Details (read Identity card)
        InterfaceCommunicationEID<RequireInfoDetailsGet> data2 = new InterfaceCommunicationEID();
        data2.setCmdType("GetInfoDetails");
        data2.setRequestID(Utils.generateTransactionID_noRP());
        RequireInfoDetailsGet dataDetails = new RequireInfoDetailsGet();
        dataDetails.setMrzEnabled(true);
        dataDetails.setImageEnabled(true);
        dataDetails.setDataGroupEnabled(true);
        dataDetails.setOptionDetailsEnabled(true);
        dataDetails.setCanValue("");
        dataDetails.setChallenge(challenged);
        dataDetails.setCaEnabled(true);
        dataDetails.setTaEnabled(true);
        dataDetails.setPaEnabled(true);

        // 
    }
}
