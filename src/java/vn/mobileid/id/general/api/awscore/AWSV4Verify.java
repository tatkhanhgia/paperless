package vn.mobileid.id.general.api.awscore;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import vn.mobileid.id.general.LogHandler;
//import vn.mobileid.id.qrypto.objects.FormDataBodyItem;

import vn.mobileid.id.utils.Utils;

public class AWSV4Verify {

    final private static Logger LOG = LogManager.getLogger(AWSV4Verify.class);

    final private static ObjectMapper objectMapper = new ObjectMapper();

    //private String accessKeyID;
    private String secretAccessKey;
    private String xApiKey;
    private String regionName;
    private String serviceName;

    private HttpServletRequest httpRequest;
    private String payload;
    private byte[] bodyReq;
//    private List<FormDataBodyItem> formDataBodyItemList;


    /* Other variables */
    private int timeValidSignature;
    private String xAmzDate;
    private String currentDate;
    private String credentialScope;

    private String strSignedHeader;
    private StringBuilder canonicalHeaders;
    private AWSV4Store store;

    public static class Builder {
        //private String accessKeyID;
        //private String secretAccessKey;

        private String xApiKey;
        private String regionName;
        private String serviceName;

        private HttpServletRequest httpRequest;
        private String payload;
        private byte[] bodyReq;
//        private List<FormDataBodyItem> formDataBodyItemList;

        private AWSV4Store store;

        public Builder(AWSV4Store store) {
            //this.accessKeyID = accessKeyID;
            //this.secretAccessKey = secretAccessKey;
            this.store = store;
        }

        public Builder xApiKey(String xApiKey) {
            this.xApiKey = xApiKey;
            return this;
        }

        public Builder regionName(String regionName) {
            this.regionName = regionName;
            return this;
        }

        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder httpRequest(HttpServletRequest httpRequest) {
            this.httpRequest = httpRequest;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder bodyReq(byte[] bodyReq) {
            this.bodyReq = bodyReq;
            return this;
        }

//        public Builder formDataBodyItemList(List<FormDataBodyItem> formDataBodyItemList) {
//            this.formDataBodyItemList = formDataBodyItemList;
//            return this;
//        }

        public AWSV4Verify build() {
            return new AWSV4Verify(this);
        }
    }

    private AWSV4Verify(Builder builder) {
        //accessKeyID = builder.accessKeyID;
        //secretAccessKey = builder.secretAccessKey;
        xApiKey = builder.xApiKey;
        regionName = builder.regionName;
        serviceName = builder.serviceName;

        httpRequest = builder.httpRequest;
        payload = builder.payload;
        bodyReq = builder.bodyReq;
//        formDataBodyItemList = builder.formDataBodyItemList;

        store = builder.store;

        // canonical URI without encoding
        // canonicalURI = builder.endpointURL.getPath();
        // At a minimum, you must include the host header
        // if(!awsHeaders.containsKey(AWSV4Constants.HOST))
        // awsHeaders.put(AWSV4Constants.HOST, builder.endpointURL.getHost());

        /* Get current timestamp value.(UTC) */
        // getDateTime();
    }

    public AWSV4ResponseCode verify(int validSignatureInminutes, boolean multipartReq) {
        this.timeValidSignature = validSignatureInminutes;

//        Enumeration<String> headerNames = httpRequest.getHeaderNames();
//        if (headerNames != null) {
//            while (headerNames.hasMoreElements()) {
//                LOG.error("Header: " + httpRequest.getHeader(headerNames.nextElement()));
//            }
//        }
        if (multipartReq) {
            //check multipart from
            String userAgent = this.httpRequest.getHeader(AWSV4Constants.USER_AGENT);
            if (userAgent == null) {
                userAgent = this.httpRequest.getHeader(AWSV4Constants.USER_AGENT.toLowerCase());
            }
            if (userAgent == null) {
                userAgent = "";
            }

            if (userAgent.contains("Postman") || userAgent.contains("postman")) {

            } else {
                String xPolicyFormData = this.httpRequest.getHeader(AWSV4Constants.X_POLICY_FORM_DATA);
                if (xPolicyFormData == null) {
                    xPolicyFormData = this.httpRequest.getHeader(AWSV4Constants.X_POLICY_FORM_DATA.toLowerCase());
                    if (xPolicyFormData == null) {
                        return AWSV4ResponseCode.MISSING_POLICY_FORM_DATA;
                    }
                }
//                FormDataBodyItem[] formDataBodyItem;
//                try {
//                    formDataBodyItem = objectMapper.readValue(Base64.getDecoder().decode(xPolicyFormData), FormDataBodyItem[].class);
//                } catch (IOException e) {
//                    return AWSV4ResponseCode.INVALID_POLICY_FORM_DATA;
//                }
//                if (this.formDataBodyItemList.size() != formDataBodyItem.length) {
//                    return AWSV4ResponseCode.INVALID_POLICY_FORM_DATA;
//                }
//                for (FormDataBodyItem fdbi1 : this.formDataBodyItemList) {
//                    boolean isValidItem = false;
//                    for (FormDataBodyItem fdbi2 : formDataBodyItem) {
//                        //LOG.error(fdbi1.getSha256Content() + "/" + fdbi2.getSha256Content());
//                        if (fdbi1.getSha256Content().compareToIgnoreCase(fdbi2.getSha256Content()) == 0) {
//                            isValidItem = true;
//                        }
//                    }
//                    if (!isValidItem) {
//                        return AWSV4ResponseCode.INVALID_POLICY_FORM_DATA;
//                    }
//                }
            }
        }

        String xApiKeyRequest = this.httpRequest.getHeader(AWSV4Constants.X_API_KEY);
        if (xApiKeyRequest == null) {
            xApiKeyRequest = this.httpRequest.getHeader(AWSV4Constants.X_API_KEY.toLowerCase());
            if (xApiKeyRequest == null) {
                return AWSV4ResponseCode.MISSING_X_API_KEY;
            }
        }
        if (!xApiKeyRequest.equals(xApiKey)) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("xApiKeyRequest doesn't match. [HEADER/SYSTEM] " + xApiKeyRequest + "/" + xApiKey);
            }
            return AWSV4ResponseCode.MISSING_AUTHENTICATION_TOKEN;
        }

        String Authorization = this.httpRequest.getHeader(AWSV4Constants.AUTHORIZATION);
        if (Authorization == null) {
            Authorization = this.httpRequest.getHeader(AWSV4Constants.AUTHORIZATION.toLowerCase());
            if (Authorization == null) {
                return AWSV4ResponseCode.MISSING_AUTHENTICATION_TOKEN;
            }
        }

        AWSV4ResponseCode response = checkAuthorization(Authorization);
//		if(response != AWSV4ResponseCode.VERIFY_OK)
//			return response;		

        return response;
    }

    private AWSV4ResponseCode checkAuthorization(String Authorization) {
        if (Authorization == null) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Authorization is NULL or EMPTY");
            }
            return AWSV4ResponseCode.MISSING_AUTHENTICATION_TOKEN;
        }
        try {
            Authorization = Authorization.trim();

            String[] arrayStringTmp = Authorization.split(",");
            ArrayList<String> listKeyValue = new ArrayList<String>();
            for (String keyValueS : arrayStringTmp) {
                String[] keyValuePairs = keyValueS.split(" ");
                for (String keyValueF : keyValuePairs) {
                    if (!keyValueF.isEmpty());
                    listKeyValue.add(keyValueF);
                }
            }

            HashMap<String, String> authorizationMap = new HashMap<String, String>();

            for (int idx = 1; idx < listKeyValue.size(); idx++) {
                String strKeyValuePair = listKeyValue.get(idx);

                if (!strKeyValuePair.isEmpty()) {
                    Map.Entry<String, String> keyValuePair = checkKeyValue(strKeyValuePair);
                    if (keyValuePair == null) {
                        return AWSV4ResponseCode.INVALID_KEY_VALUE;
                    } else {
                        authorizationMap.put(keyValuePair.getKey(), keyValuePair.getValue());
                    }
                }
            }

            // firt is alg		
            String awsTagAlg = listKeyValue.get(0);
            String awsTag = awsTagAlg.substring(0, awsTagAlg.indexOf('-'));
            if (!awsTag.equals(AWSV4Constants.AUTH_TAG)) {
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Invalid Authorization TAG: " + awsTag);
                }
                return AWSV4ResponseCode.MISSING_AUTHENTICATION_TOKEN;
            }
            if (!awsTagAlg.equals(AWSV4Constants.ALGORITHM)) {
                return AWSV4ResponseCode.UNSUPPORTED_AWS_ALGORITHM;
            }

            if (!authorizationMap.containsKey(AWSV4Constants.CREDENTIAL)) {
                return AWSV4ResponseCode.MISSING_CREDENTIAL;
            }
            if (!authorizationMap.containsKey(AWSV4Constants.SIGNATURE)) {
                return AWSV4ResponseCode.MISSING_SIGNATURE;
            }
            if (!authorizationMap.containsKey(AWSV4Constants.SIGNEDHEADERS)) {
                return AWSV4ResponseCode.MISSING_SIGNEDHEADERS;
            }

            //check Credential
            AWSV4ResponseCode response = checkCredential(authorizationMap.get(AWSV4Constants.CREDENTIAL));
            if (response != AWSV4ResponseCode.VERIFY_OK) {
                return response;
            }

            //check amz date
            String strXAmzDate = this.httpRequest.getHeader(AWSV4Constants.X_AMZ_DATE);
            if (strXAmzDate == null) {
                strXAmzDate = this.httpRequest.getHeader(AWSV4Constants.X_AMZ_DATE.toLowerCase());
                if (strXAmzDate == null) {
                    return AWSV4ResponseCode.MISSING_X_AMZ_DATE;
                }
            }
            response = checkDate(strXAmzDate);
            if (response != AWSV4ResponseCode.VERIFY_OK) {
                return response;
            }

            //check SignedHeaders			
            response = checkSignedHeaders(authorizationMap.get(AWSV4Constants.SIGNEDHEADERS));
            if (response != AWSV4ResponseCode.VERIFY_OK) {
                return response;
            }

            //compute CanonicalRequest
            String strCanonicalRequest = prepareCanonicalRequest();

            String stringToSign = prepareStringToSign(strCanonicalRequest);

            String signature = calculateSignature(stringToSign);

            String signatureInRequest = authorizationMap.get(AWSV4Constants.SIGNATURE);
            if (!signatureInRequest.equalsIgnoreCase(signature)) {
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Calculated signature: " + signature
                            + "\nSignature: " + signatureInRequest);
                }
                return AWSV4ResponseCode.INVALID_SIGNATURE;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while checking authorization. Details: " + Utils.printStackTrace(ex));
            }
            return AWSV4ResponseCode.MISSING_AUTHENTICATION_TOKEN;
        }

        return AWSV4ResponseCode.VERIFY_OK;
    }

    private AWSV4ResponseCode checkSignedHeaders(String signedHeaders) {
        canonicalHeaders = new StringBuilder("");
        this.strSignedHeader = signedHeaders;

        String[] headers = signedHeaders.split(";");

        //Enumeration<String> headersRequest = httpRequest.getHeaderNames();
        //Enumeration<String> attributesRequest = httpRequest.getHeaders("Authorization");
        //this.signedHeaders = new TreeMap<>();
        //LOG.error("checkSignedHeaders...");
        for (String header : headers) {
            String val = httpRequest.getHeader(header);
            if (val == null) {
                val = httpRequest.getHeader(header.toLowerCase());
            }
            if (header.equalsIgnoreCase(AWSV4Constants.CONTENT_LENGTH)) {
                val = String.valueOf(httpRequest.getContentLength());
            } else if (header.equalsIgnoreCase(AWSV4Constants.CONTENT_TYPE)) {
                val = httpRequest.getContentType();
            }
            /*else if(header.equalsIgnoreCase(AWSV4Constants.HOST))
				val = httpRequest.getLocalAddr();*/

            if (val == null) {
                return AWSV4ResponseCode.MISSING_VALUE_FOR_SIGNED_HEADERS;
            }
            //LOG.error("\t header key=: " + header + ", val=" + val);
            canonicalHeaders.append(header).append(":").append(val).append("\n");
        }
        if (this.strSignedHeader.indexOf(AWSV4Constants.HOST.toLowerCase()) < 0) {
            return AWSV4ResponseCode.MISSING_HOST;
        }

        return AWSV4ResponseCode.VERIFY_OK;
    }

    private AWSV4ResponseCode checkDate(String strXAmzDate) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date dtXAmzDate = dateFormat.parse(strXAmzDate);

            dateFormat = new SimpleDateFormat("yyyyMMdd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateInAmz = dateFormat.format(dtXAmzDate);

            if (!dateInAmz.equals(currentDate)) {
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Comparison: " + dateInAmz + " and " + currentDate + ". strXAmzDate: " + strXAmzDate);
                }
                return AWSV4ResponseCode.DATE_CREDENTIAL_NOT_MATCH;
            }

            Date now = new Date();
            Calendar after = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            after.setTime(now);
            Calendar before = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            before.setTime(now);
            after.add(Calendar.MINUTE, this.timeValidSignature);
            before.add(Calendar.MINUTE, -this.timeValidSignature);

            Date dtBefore = before.getTime();
            Date dtAfter = after.getTime();

            if (dtXAmzDate.before(dtBefore) || dtXAmzDate.after(dtAfter)) {
                return AWSV4ResponseCode.X_AMZ_DATE_EXPIRED;
            }

            this.xAmzDate = strXAmzDate;

        } catch (ParseException e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while checking date. Details: " + Utils.printStackTrace(e));
            }
            return AWSV4ResponseCode.X_AMZ_DATE_INVALID;
        }

        return AWSV4ResponseCode.VERIFY_OK;

    }

    private AWSV4ResponseCode checkCredential(String Credential) {
        String[] credentialParams = Credential.split("/");

        if (credentialParams.length != 5) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("[checkCredential] Credential length should be 5. Current: " + credentialParams.length);
            }
            return AWSV4ResponseCode.CREDENTIAL_INVALID;
        }

        secretAccessKey = store.getAccessKey(credentialParams[0]);

        if (!credentialParams[2].equals(this.regionName)) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("[checkCredential] Invalid regionName: " + credentialParams[2]);
            }
            return AWSV4ResponseCode.CREDENTIAL_INVALID;
        }
        if (!credentialParams[3].equals(this.serviceName)) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("[checkCredential] Invalid serviceName: " + credentialParams[3]);
            }
            return AWSV4ResponseCode.CREDENTIAL_INVALID;
        }
        if (!credentialParams[4].equals(AWSV4Constants.TERMINATION_STRING)) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("[checkCredential] Invalid TERMINATION_STRING: " + credentialParams[4]);
            }
            return AWSV4ResponseCode.CREDENTIAL_INVALID;
        }

        this.currentDate = credentialParams[1];

        this.credentialScope = Credential;

        return AWSV4ResponseCode.VERIFY_OK;
    }

    private Map.Entry<String, String> checkKeyValue(String keyValue) {
        try {
            int indexEquals = keyValue.indexOf("=");
            if (indexEquals <= 0) {
                return null;
            }
            String key = keyValue.substring(0, indexEquals);
            String value = keyValue.substring(indexEquals + 1, keyValue.length());
            Map.Entry<String, String> keyValuePair = new AbstractMap.SimpleEntry<String, String>(key, value);

            return keyValuePair;

        } catch (Exception ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while checking KeyValue. Details: " + Utils.printStackTrace(ex));
            }
            ex.printStackTrace();
            return null;
        }

    }

    private String prepareCanonicalRequest() {
        StringBuilder canonicalRequest = new StringBuilder("");

        /* Step 1.1 Start with the HTTP request method (GET, PUT, POST, etc.), followed by a newline character. */
        canonicalRequest.append(httpRequest.getMethod()).append("\n");

        /* Step 1.2 Add the canonical URI parameter, followed by a newline character. */
        String canonicalURI = httpRequest.getRequestURI();
        canonicalURI = canonicalURI == null || canonicalURI.trim().isEmpty() ? "/" : canonicalURI;
        canonicalRequest.append(canonicalURI).append("\n");

        /* Step 1.3 Add the canonical query string, followed by a newline character. */
        String queryString = httpRequest.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            canonicalRequest.append(queryString);
        }
        canonicalRequest.append("\n");

        /* Step 1.4 Add the canonical headers, followed by a newline character. */
        canonicalRequest.append(this.canonicalHeaders).append("\n");

        /* Step 1.5 Add the signed headers, followed by a newline character. */
        canonicalRequest.append(strSignedHeader).append("\n");

        /* Step 1.6 Use a hash (digest) function like SHA256 to create a hashed value from the payload in the body of the HTTP or HTTPS. */
//        payload = payload == null ? "" : payload;
//        canonicalRequest.append(generateHex(payload));
        if (bodyReq == null) {
            payload = payload == null ? "" : payload;
            //LOG.error("payload len: " + payload.length());
            canonicalRequest.append(generateHex(payload));
        } else {
            //LOG.error("bodyReq len: " + bodyReq.length);
            canonicalRequest.append(generateHex(bodyReq));
        }

        return canonicalRequest.toString();
    }

    private String prepareStringToSign(String canonicalURL) {
        //LOG.error("canonicalURL: " + canonicalURL);
        String stringToSign = "";

        /* Step 2.1 Start with the algorithm designation, followed by a newline character. */
        stringToSign = AWSV4Constants.ALGORITHM + "\n";

        /* Step 2.2 Append the request date value, followed by a newline character. */
        stringToSign += xAmzDate + "\n";

        /* Step 2.3 Append the credential scope value, followed by a newline character. */
        credentialScope = currentDate + "/" + regionName + "/" + serviceName + "/" + AWSV4Constants.TERMINATION_STRING;
        stringToSign += credentialScope + "\n";
        //LOG.error("stringToSign (before append hash): " + stringToSign);
        /* Step 2.4 Append the hash of the canonical request that you created in Task 1: Create a Canonical Request for Signature Version 4. */
        stringToSign += generateHex(canonicalURL);
        //LOG.error("stringToSign (after append hash): " + stringToSign);
        return stringToSign;
    }

    /**
     * Task 3: Calculate the AWS Signature Version 4.
     *
     * @param stringToSign
     * @return
     */
    private String calculateSignature(String stringToSign) {
        try {
            /* Step 3.1 Derive your signing key */
            byte[] signatureKey = getSignatureKey(secretAccessKey, currentDate, regionName, serviceName);
            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("AWS signature calculation:\n\t secretAccessKey: " + secretAccessKey
//                        + "\n\t currentDate: " + currentDate
//                        + "\n\t regionName: " + regionName
//                        + "\n\t serviceName: " + serviceName
//                        + "\n\t stringToSign: " + stringToSign);
            }
            /* Step 3.2 Calculate the signature. */
            byte[] signature = HmacSHA256(signatureKey, stringToSign);

            /* Step 3.2.1 Encode signature (byte[]) to Hex */
            String strHexSignature = bytesToHex(signature);
            return strHexSignature;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Apply HmacSHA256 on data using given key.
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     * @reference:
     * http://docs.aws.amazon.com/general/latest/gr/signature-v4-examples.html#signature-v4-examples-java
     */
    private byte[] HmacSHA256(byte[] key, String data) throws Exception {
        String algorithm = AWSV4Constants.HMAC_SHA256;
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes("UTF8"));
    }

    /**
     * Generate AWS signature key.
     *
     * @param key
     * @param date
     * @param regionName
     * @param serviceName
     * @return
     * @throws Exception
     * @reference
     * http://docs.aws.amazon.com/general/latest/gr/signature-v4-examples.html#signature-v4-examples-java
     */
    private byte[] getSignatureKey(String key, String date, String regionName, String serviceName) throws Exception {
        byte[] kSecret = (AWSV4Constants.AUTH_TAG + key).getBytes("UTF8");
        byte[] kDate = HmacSHA256(kSecret, date);
        byte[] kRegion = HmacSHA256(kDate, regionName);
        byte[] kService = HmacSHA256(kRegion, serviceName);
        byte[] kSigning = HmacSHA256(kService, AWSV4Constants.TERMINATION_STRING);

        return kSigning;
    }

    /**
     * Generate Hex code of String.
     *
     * @param data
     * @return
     */
    private String generateHex(String data) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(AWSV4Constants.SHA_256);
            messageDigest.update(data.getBytes("UTF-8"));
            byte[] digest = messageDigest.digest();
            return String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private String generateHex(byte[] data) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(AWSV4Constants.SHA_256);
            messageDigest.update(data);
            byte[] digest = messageDigest.digest();
            return String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Convert byte array to Hex
     *
     * @param bytes
     * @return
     */
    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars).toLowerCase();
    }

}
