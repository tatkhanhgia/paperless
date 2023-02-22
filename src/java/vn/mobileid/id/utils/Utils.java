/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.utils;

import vn.mobileid.id.qrypto.QryptoConstant;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;

//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
import vn.mobileid.id.general.email.EmailReq;
import vn.mobileid.id.general.email.EmailResp;
import vn.mobileid.id.general.objects.CredentialTokensJSNObject;
import vn.mobileid.id.general.sms.SmsResp;
import vn.mobileid.id.general.LogHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import com.google.gson.Gson;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import restful.sdk.API.Property;

/**
 *
 * @author ADMIN
 */
public class Utils {

    private static final Logger LOG = LogManager.getLogger(Utils.class);

    private static final Gson gson = new Gson();

    public static boolean isNullOrEmpty(String value) {
        if (value == null) {
            return true;
        }
        if (value.compareTo("") == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNullOrEmpty(String[] value) {
        if (value == null) {
            return true;
        }
        if (value.length == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNullOrEmpty(byte[] value) {
        if (value == null) {
            return true;
        }
        if (value.length == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNullOrEmpty(List value) {
        if (value == null) {
            return true;
        }
        if (value.isEmpty()) {
            return true;
        }
        return false;
    }

    public static String getPropertiesFile(String fileName) {
        /*
        File folder = new File(System.getProperty("jboss.server.base.dir"));
        File[] listOfFiles = folder.listFiles();
        int i;
        for (i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String filePath = listOfFiles[i].getAbsolutePath();
                LOG.error(filePath);
                if (filePath.contains(fileName)) {
                    return filePath;
                }
            }
        }
        return null;
         */
        return walk(System.getProperty("jboss.server.base.dir"), fileName);
    }

    /*
    public static String walk(String path, String fileName) {
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) {
            return null;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                return walk(f.getAbsolutePath(), fileName);
            } else {
                LOG.error("path: "+f.getAbsolutePath()+" file name: "+fileName);
                if (f.getAbsolutePath().contains(fileName)) {
                    return f.getAbsolutePath();
                }
            }
        }
        return null;
    }
     */
    public static String walk(String path, String fileName) {
        try ( Stream<Path> walk = Files.walk(Paths.get(path))) {

            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());

            for (String f : result) {
                if (f.contains(fileName)) {
                    return f;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] genRandomArray(int size) throws NoSuchAlgorithmException, NoSuchProviderException {
        // TODO Auto-generated method stub
        byte[] random = new byte[size];
        new Random().nextBytes(random);
        return random;
    }

    public static byte[] saveByteArrayOutputStream(InputStream body) {
        int c;
        byte[] r = null;
        try {
            ByteArrayOutputStream f = new ByteArrayOutputStream();
            while ((c = body.read()) > -1) {
                f.write(c);
            }
            r = f.toByteArray();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    public static String getRequestHeader(final HttpServletRequest request, String headerName) {
        String headerValue = null;
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            headerValue = request.getHeader(key);
            if (key.compareToIgnoreCase(headerName) == 0) {
                return headerValue;
            } else {
                headerValue = null;
            }
        }
        return headerValue;
    }
    
    public static HashMap<String,String> getHashMapRequestHeader(final HttpServletRequest request) {
        String headerValue = null;
        HashMap<String,String> hashMap = new HashMap<>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            headerValue = request.getHeader(key);
            hashMap.put(key, headerValue);
        }
        return hashMap;
    }

    public static byte[] getBinaryStream(HttpServletRequest request) {
        byte[] stream = null;
        try {
            InputStream is = request.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            for (int nChunk = is.read(buf); nChunk != -1; nChunk = is.read(buf)) {
                bos.write(buf, 0, nChunk);
            }
            stream = bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }

    public static boolean checkIPwithPattern(String ip, String pattern) {
        if (pattern.length() == 1) {
            // pattern = *
            return true;
        } else {
            String c = pattern.substring(0, pattern.indexOf("*"));
            String b = ip.substring(0, c.length());
            if (c.compareToIgnoreCase(b) == 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static String generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
    
    public static String generateTransactionID_noRP() {
        String billCode = null;
        try{
            billCode = generateOneTimePassword(4) + "-" + generateOneTimePassword(5) + "-" + generateOneTimePassword(5);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return billCode;
    }

    public static List<CredentialTokensJSNObject> cleanUpCredentialTokens(List<CredentialTokensJSNObject> listOfCredentialTokensJSNObject) {
        long tenMinutes = 0; // in miliseconds
        long now = Calendar.getInstance().getTimeInMillis();
        for (int i = 0; i < listOfCredentialTokensJSNObject.size(); i++) {
            CredentialTokensJSNObject credentialTokensJSNObject = listOfCredentialTokensJSNObject.get(i);
            long exp = credentialTokensJSNObject.getExpiresTime().getTime() + tenMinutes;
            long diviation = exp - now;
            if (diviation <= 0) {
                listOfCredentialTokensJSNObject.remove(i);
                i = -1;
            }
        }
        if (listOfCredentialTokensJSNObject.size() == QryptoConstant.NUMBER_OF_ACCESS_TOKEN) { // number of access token
            Collections.sort(listOfCredentialTokensJSNObject);
            listOfCredentialTokensJSNObject.remove(0);
        }
        return listOfCredentialTokensJSNObject;
    }

    public static String[] getExtraInfoInRequest(String payload) {
        String[] result = new String[2];
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(payload);
            result[0] = rootNode.path("extended_id").asText();
            result[1] = rootNode.path("bill_code").asText();
        } catch (Exception e) {

        }
        return result;
    }

    public static String generateTransactionId(String relyingParty, long logId, Date logDatetime) {
        String billCode = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
            sdf.setTimeZone(TimeZone.getTimeZone(System.getProperty("user.timezone")));
            String dateTime = sdf.format(logDatetime);
            billCode = relyingParty + "-" + dateTime + "-" + logId + "-" + generateOneTimePassword(6);            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return billCode;
    }

    public static String generateOneTimePassword(int len) {
        String numbers = "0123456789";
        Random rndm_method = new Random();
        char[] otp = new char[len];
        for (int i = 0; i < len; i++) {
            otp[i] = numbers.charAt(rndm_method.nextInt(numbers.length()));
        }
        return new String(otp);
    }


    public static String getDocumentDateTimeString(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        } catch (Exception e) {
            //e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse value " + date + " as format yyyy-MM-dd.");
            }
        }
        return null;
    }

    /*
    public static boolean verifyIdentityDocumentDateTime(String dob) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.parse(dob);
            return true;
        } catch (Exception e) {

        }
        return false;
    }
     */
    public static boolean verifyIdentityDocumentDateTime(final String date) {
        boolean valid = false;
        try {
            // ResolverStyle.STRICT for 30, 31 days checking, and also leap year.
            LocalDate.parse(date,
                    DateTimeFormatter.ofPattern("uuuu-MM-dd")
                            .withResolverStyle(ResolverStyle.STRICT)
            );
            valid = true;
        } catch (DateTimeParseException e) {
            valid = false;
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse value " + date + " as format yyyy-MM-dd");
            }
        }
        return valid;
    }

    public static Date getDocumentDateTimeObject(String date) {
        if (!isNullOrEmpty(date)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.parse(date);
            } catch (Exception e) {
                //e.printStackTrace();
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Cannot parse value " + date + " as format yyyy-MM-dd");
                }
            }
        }
        return null;
    }

    public static Date getDocumentDateTimeObjectFromOCR(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(date);
        } catch (Exception e) {
            //e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse value " + date + " as format yyyy-MM-dd.");
            }
        }
        return null;
    }

    public static Date getDocumentDateTimeObjectFromVietNamID(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.parse(date);
        } catch (Exception e) {
            //e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot parse value " + date + " as format dd-MM-yyyy.");
            }
        }
        return null;
    }

    public static String generateBillCode(String relyingParty, long logId, Date logDatetime) {
        String billCode = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
            sdf.setTimeZone(TimeZone.getTimeZone(System.getProperty("user.timezone")));
            String dateTime = sdf.format(logDatetime);
            billCode = relyingParty + "-" + dateTime + "-" + logId + "-" + generateOneTimePassword(6);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return billCode;
    }

    public static String generateUsername() {
        String username = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
            sdf.setTimeZone(TimeZone.getTimeZone(System.getProperty("user.timezone")));
            String dateTime = sdf.format(Calendar.getInstance().getTime());
            username = dateTime + "-" + generateOneTimePassword(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return username;
    }

//    public static String toJson(EmailReq o) {
//        String json = null;
//        byte[] byte4null = new byte[4];
//        EmailReq emailReq = (EmailReq) SerializationUtils.clone(o);
//        // process sensitive data
//        String emailContent = emailReq.getContent();
//        if (!Utils.isNullOrEmpty(emailContent)) {
//            String oneTimePassword = Utils.detectSequenceNumber(emailContent);
//            if (!Utils.isNullOrEmpty(oneTimePassword)) {
//                emailContent = emailContent.replace(oneTimePassword, IdentityConstant.SECRET_STRING);
//                emailReq.setContent(emailContent);
//            }
//        }
//        json = gson.toJson(emailReq);
//        return json;
//    }
    public static String toJson(EmailResp o) {
        String json = null;
        byte[] byte4null = new byte[4];
        EmailResp emailResp = (EmailResp) SerializationUtils.clone(o);
        json = gson.toJson(emailResp);
        return json;
    }

//    public static String toJson(SmsReq o) {
//        String json = null;
//        byte[] byte4null = new byte[4];
//        SmsReq smsReq = (SmsReq) SerializationUtils.clone(o);
//        // process sensitive data
//        String smsContent = smsReq.getContent();
//        if (!Utils.isNullOrEmpty(smsContent)) {
//            String oneTimePassword = Utils.detectSequenceNumber(smsContent);
//            if (!Utils.isNullOrEmpty(oneTimePassword)) {
//                smsContent = smsContent.replace(oneTimePassword, IdentityConstant.SECRET_STRING);
//                smsReq.setContent(smsContent);
//            }
//        }
//        json = gson.toJson(smsReq);
//        return json;
//    }
    public static String toJson(SmsResp o) {
        String json = null;
        byte[] byte4null = new byte[4];
        SmsResp smsResp = (SmsResp) SerializationUtils.clone(o);
        json = gson.toJson(smsResp);
        return json;
    }

//    public static String toJson(OCRReq o) {
//        String json = null;
//        byte[] byte4null = new byte[4];
//        OCRReq ocrReq = (OCRReq) SerializationUtils.clone(o);
//        byte[] frontData = ocrReq.getFrontImage();
//        if (frontData != null) {
//            ocrReq.setFrontImage(byte4null);
//        }
//
//        byte[] backData = ocrReq.getBackImage();
//        if (backData != null) {
//            ocrReq.setBackImage(byte4null);
//        }
//        json = gson.toJson(ocrReq);
//        return json;
//    }
//    public static String toJson(OCRResp o) {
//        String json = null;
//        byte[] byte4null = new byte[4];
//        OCRResp ocrResp = (OCRResp) SerializationUtils.clone(o);
//        json = gson.toJson(ocrResp);
//        return json;
//    }
//    public static String toJson(DocumentMatchingReq o) {
//        String json = null;
//        byte[] byte4null = new byte[4];
//        DocumentMatchingReq documentMatchingReq = (DocumentMatchingReq) SerializationUtils.clone(o);
//        byte[] faceScan = documentMatchingReq.getFaceScan();
//        if (faceScan != null) {
//            documentMatchingReq.setFaceScan(byte4null);
//        }
//        byte[] backImage = documentMatchingReq.getBackImage();
//        if (backImage != null) {
//            documentMatchingReq.setBackImage(byte4null);
//        }
//        byte[] frontImage = documentMatchingReq.getFrontImage();
//        if (frontImage != null) {
//            documentMatchingReq.setFrontImage(byte4null);
//        }
//        byte[] auditTrailImage = documentMatchingReq.getAuditTrailImage();
//        if (auditTrailImage != null) {
//            documentMatchingReq.setAuditTrailImage(byte4null);
//        }
//        byte[] lowQualityAuditTrailImage = documentMatchingReq.getLowQuailityAuditTrailImage();
//        if (lowQualityAuditTrailImage != null) {
//            documentMatchingReq.setLowQuailityAuditTrailImage(byte4null);
//        }
//        byte[] candidateFinger = documentMatchingReq.getCandidateFinger();
//        if (candidateFinger != null) {
//            documentMatchingReq.setCandidateFinger(byte4null);
//        }
//        byte[] referenceFinger = documentMatchingReq.getReferenceFinger();
//        if (referenceFinger != null) {
//            documentMatchingReq.setReferenceFinger(byte4null);
//        }
//
//        if (documentMatchingReq.getFaceFrames() != null) {
//            if (!documentMatchingReq.getFaceFrames().isEmpty()) {
//                for (int i = 0; i < documentMatchingReq.getFaceFrames().size(); i++) {
//                    if (documentMatchingReq.getFaceFrames().get(i) != null) {
//                        documentMatchingReq.getFaceFrames().get(i).setData(byte4null);
//                    }
//                }
//            }
//        }
//
//        if (documentMatchingReq.getPortrait() != null) {
//            if (!documentMatchingReq.getPortrait().isEmpty()) {
//                for (int i = 0; i < documentMatchingReq.getPortrait().size(); i++) {
//                    if (documentMatchingReq.getPortrait().get(i) != null) {
//                        documentMatchingReq.getPortrait().get(i).setData(byte4null);
//                    }
//                }
//            }
//        }
//
//        json = gson.toJson(documentMatchingReq);
//        return json;
//    }
//    public static String toJson(DocumentMatchingResp o) {
//        String json = null;
//        byte[] byte4null = new byte[4];
//        DocumentMatchingResp documentMatchingResp = (DocumentMatchingResp) SerializationUtils.clone(o);
//        if (documentMatchingResp.getEyeOpenFrame() != null) {
//            documentMatchingResp.setEyeOpenFrame(byte4null);
//        }
//        json = gson.toJson(documentMatchingResp);
//        return json;
//    }
//    public static String toJson(ValidationReq o) {
//        String json = null;
//        byte[] byte4null = new byte[4];
//        ValidationReq validationReq = (ValidationReq) SerializationUtils.clone(o);
//        if (validationReq.getOcspRequestData() != null) {
//            validationReq.setOcspRequestData(byte4null);
//        }
//
//        json = gson.toJson(validationReq);
//        return json;
//    }
//    public static String toJson(ValidationResp o) {
//        String json = null;
//        byte[] byte4null = new byte[4];
//        ValidationResp validationResp = (ValidationResp) SerializationUtils.clone(o);
//        if (validationResp.getOcspResponseData() != null) {
//            validationResp.setOcspResponseData(byte4null);
//        }
//
//        if (validationResp.getCrlResponseData() != null) {
//            validationResp.setCrlResponseData(byte4null);
//        }
//        json = gson.toJson(validationResp);
//        return json;
//    }
    public static String detectSequenceNumber(String str) {
        List<String> seqNumber = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            int dec = (int) currentChar;

            if (dec >= 48 && dec <= 57) {
                seqNumber.add(String.valueOf(currentChar));
            } else {
                if (seqNumber.size() > 3) {
                    break;
                } else {
                    seqNumber.clear();
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        for (String s : seqNumber) {
            builder.append(s);
        }
        return builder.toString();
    }

//    public static String toJson(DMSReq o) {
//        String json = null;
//        byte[] byte4null = new byte[4];
//        DMSReq dmsReq = (DMSReq) SerializationUtils.clone(o);
//        dmsReq.setDocumentBinary(byte4null);
//        json = gson.toJson(dmsReq);
//        return json;
//    }
//    public static String toJson(DMSResp o) {
//        String json = null;
//        byte[] byte4null = new byte[4];
//        DMSResp dmsResp = (DMSResp) SerializationUtils.clone(o);
//        dmsResp.setFileBinary(byte4null);
//        json = gson.toJson(dmsResp);
//        return json;
//    }
    public static String getRandomFileName(String currentYearMonth, String hashData) {
        return "local_".concat(currentYearMonth).concat(hashData);
    }

    public static void archiveFileToDisk(
            byte[] fileData,
            String docName,
            String filePath,
            String currentYearMonth, //YYMM
            String hashCode,
            String fileName) {
        String fullPath = null;
        try {

            String firstNode = hashCode.substring(0, 1);
            String secondNode = hashCode.substring(0, 2);
            String thirdNode = hashCode.substring(0, 3);

            if (filePath.lastIndexOf(File.separator) == (filePath.length() - 1)) {
                // Have separator at the end
            } else {
                filePath += File.separator;
            }

            fullPath = filePath + currentYearMonth + File.separator + firstNode + File.separator
                    + secondNode + File.separator
                    + thirdNode + File.separator;

            File outputPath = new File(fullPath);
            if (!outputPath.exists()) {
                if (!outputPath.mkdirs()) {
                    if (LogHandler.isShowErrorLog()) {
                        LOG.error("Temporary folder could not be created " + outputPath.getAbsolutePath() + ""
                                + ". Using the folder " + System.getProperty("java.io.tmpdir"));
                    }
                    fullPath = System.getProperty("java.io.tmpdir");
                }
            }
            if (fullPath.lastIndexOf(File.separator) == (fullPath.length() - 1)) {
                // Have separator at the end
            } else {
                fullPath += File.separator;
            }

            fullPath = fullPath.concat(fileName);

            try ( OutputStream os = new FileOutputStream(fullPath)) {
                IOUtils.write(fileData, os);
                if (LogHandler.isShowDebugLog()) {
                    LOG.debug("The document " + docName + " is now stored in " + fullPath);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while archiving file onto disk. Filename: " + fileName + ". Details: " + Utils.printStackTrace(e));
            }
        }
    }

    public static byte[] getFileFromDisk(String filePath, String fileName) throws Exception {
        String hashCode = fileName.substring("local_".length());

        String currentYearMonth = null;
        if (hashCode.length() > 40) {
            currentYearMonth = hashCode.substring(0, 4);
            hashCode = hashCode.substring(4);
        }

        String firstNode = hashCode.substring(0, 1);
        String secondNode = hashCode.substring(0, 2);
        String thirdNode = hashCode.substring(0, 3);

        if (filePath.lastIndexOf(File.separator) == (filePath.length() - 1)) {
            // Have separator at the end
        } else {
            filePath += File.separator;
        }

        String fullPath = null;
        if (currentYearMonth == null) {
            fullPath = filePath + firstNode + File.separator
                    + secondNode + File.separator
                    + thirdNode + File.separator + fileName;
        } else {
            fullPath = filePath + currentYearMonth + File.separator
                    + firstNode + File.separator
                    + secondNode + File.separator
                    + thirdNode + File.separator + fileName;
        }
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Finding file " + fullPath);
        }
        File f = new File(fullPath);
        if (f.exists()) {
            byte[] fileData;
            try ( InputStream is = new FileInputStream(fullPath)) {
                fileData = IOUtils.toByteArray(is);
            }
            return fileData;
        } else {
            fullPath = filePath + fileName;
            if (LogHandler.isShowDebugLog()) {
                LOG.debug("Finding file " + fullPath);
            }
            f = new File(fullPath);
            if (f.exists()) {
                byte[] fileData;
                try ( InputStream is = new FileInputStream(fullPath)) {
                    fileData = IOUtils.toByteArray(is);
                }
                return fileData;
            } else {
                fullPath = System.getProperty("java.io.tmpdir");
                if (fullPath.lastIndexOf(File.separator) == (fullPath.length() - 1)) {
                    // Have separator at the end
                } else {
                    fullPath += File.separator;
                }
                fullPath = fullPath.concat(fileName);
                if (LogHandler.isShowDebugLog()) {
                    LOG.debug("Finding file " + fullPath);
                }
                f = new File(fullPath);
                if (f.exists()) {
                    byte[] fileData;
                    try ( InputStream is = new FileInputStream(fullPath)) {
                        fileData = IOUtils.toByteArray(is);
                    }
                    return fileData;
                } else {
                    throw new RuntimeException("File not found: " + fileName);
                }
            }
        }
    }

//    public static String cutoffBigDataInJson(String json, String[] keysTooLongData, String[] keysSensitiveData) {
//        try {
//            if (isNullOrEmpty(json)) {
//                return json;
//            }
//            Object obj = new JSONParser().parse(json);
//            JSONObject jo = (JSONObject) obj;
//            for (String k : keysTooLongData) {
//                String value = (String) jo.get(k);
//                if (!isNullOrEmpty(value)) {
//                    jo.put(k, IdentityConstant.LONG_STRING);
//                }
//            }
//
//            for (String k : keysSensitiveData) {
//                String value = (String) jo.get(k);
//                if (!isNullOrEmpty(value)) {
//                    jo.put(k, IdentityConstant.SECRET_STRING);
//                }
//            }
//
//            return jo.toJSONString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return IdentityConstant.INVALID_JSON_STRING;
//    }
    public static String objectToJson(Object o, ObjectMapper mapper) {
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot convert object to json. Details: " + Utils.printStackTrace(e));
            }
        }
        return "[\"Cannot convert object to json\"]";
    }

    public static String generateExternalDBRefID(String transactionId) {
        String hash = DatatypeConverter.printHexBinary(Crypto.hashData(transactionId.getBytes(), Crypto.HASH_SHA1)).toLowerCase();
        return "identity_app_" + hash;
    }

    public static String printStackTrace(Exception e) {
        String result = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            result = sw.toString();
            pw.close();
            sw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static Date getDateFromString(String source, String format) {
        Date d = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            d = sdf.parse(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    public static long getDifferenceBetweenDatesInMinute(Date date1, Date date2) {
        long diffMs = date2.getTime() - date1.getTime();
        long diffSec = diffMs / 1000;
        long hours = diffSec / 3600;
        long min = diffSec / 60;
        long sec = diffSec % 60;
        return min;
    }

    public static long getDifferenceBetweenDatesInSecond(Date date1, Date date2) {
        long diffMs = date2.getTime() - date1.getTime();
        long diffSec = diffMs / 1000;
        return diffSec;
    }

    public static int getDifferenceBetweenDatesInDay(Date date1, Date date2) {
        return (int) ((date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24));
    }

//    public static boolean validateNumberOfOTPGeneration(long ownerID, int maxOTPAllowance, int bySeconds) {
//        Database db = new DatabaseImpl();
//        OwnerAttribute ownerAttribute = db.getOwnerAttribute(ownerID, OwnerAttribute.REMAINING_ALLOWED_OTP_GENERATION);
//        if (ownerAttribute == null) {
//            // chưa có attr này
//            db.updateOwnerAttribute(ownerID,
//                    OwnerAttribute.REMAINING_ALLOWED_OTP_GENERATION,
//                    String.valueOf(--maxOTPAllowance),
//                    null);
//            return true;
//        } else {
//            long lastUpdateDt = ownerAttribute.getModifiedDt().getTime();
//            long currentDt = Calendar.getInstance().getTimeInMillis();
//            long timeDiff = currentDt - lastUpdateDt;
//            if (timeDiff > (bySeconds * 1000)) {
//                int currentCounter = Integer.parseInt(ownerAttribute.getValue());
//                maxOTPAllowance--;
//                if (currentCounter != maxOTPAllowance) {
//                    db.updateOwnerAttribute(ownerID,
//                            OwnerAttribute.REMAINING_ALLOWED_OTP_GENERATION,
//                            String.valueOf(maxOTPAllowance),
//                            null);
//                }
//                return true;
//            } else {
//                int currentCounter = Integer.parseInt(ownerAttribute.getValue());
//                if (currentCounter == 0) {
//                    if (LogAndCacheManager.isShowDebugLog()) {
//                        LOG.debug("Do not allow to generate OTP for Owner ID " + ownerID + " due to the number of OTP generation excceed.");
//                    }
//                    return false;
//                } else {
//                    db.updateOwnerAttribute(ownerID,
//                            OwnerAttribute.REMAINING_ALLOWED_OTP_GENERATION,
//                            String.valueOf(--currentCounter),
//                            null);
//                    return true;
//                }
//            }
//        }
//    }
    public static String processOTPPolicy(String otpCode, String otpPolicy) {
        //0 number of digit in a group
        //1 character between each group
        String[] policies = otpPolicy.split(",");
        int numberOfDigitInAGroup = Integer.parseInt(policies[0]);
        String characterBetweenEachGroup = policies[1];
        if (numberOfDigitInAGroup > otpCode.length()) {
            if (LogHandler.isShowWarnLog()) {
                LOG.warn("Number Of Digit In A Group greater than OTP length. No policy applied");
            }
            return otpCode;
        }
        String tmpOTP = "";
        int index = 0;
        for (int i = 0; i <= otpCode.length(); i++) {
            if (otpCode.length() <= numberOfDigitInAGroup) {
                tmpOTP += otpCode;
                break;
            } else {
                index++;
                if (index == numberOfDigitInAGroup) {
                    tmpOTP += otpCode.substring(0, index);
                    tmpOTP += characterBetweenEachGroup;
                    i = 0;
                    otpCode = otpCode.substring(index);
                    index = 0;
                }
            }
        }
        return tmpOTP;
    }

    public static String getCurrentYearMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMM");
        String yyMM = sdf.format(Calendar.getInstance().getTime());
        return yyMM;
    }

    public static int getIntergerRandomValue(int low, int high) {
        Random r = new Random();
        return r.nextInt(high - low) + low;
    }

//    public static String preProcessPayloadWithLargeDataLiveMatching(String payload) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            LiveMatchingReqJSNObject liveMatchingReqJSNObject = objectMapper.readValue(payload, LiveMatchingReqJSNObject.class);
//            if (liveMatchingReqJSNObject.getFrames() != null) {
//                for (int i = 0; i < liveMatchingReqJSNObject.getFrames().length; i++) {
//                    liveMatchingReqJSNObject.getFrames()[i] = IdentityConstant.LONG_STRING;
//                }
//            }
//            return objectMapper.writeValueAsString(liveMatchingReqJSNObject);
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogAndCacheManager.isShowErrorLog()) {
//                LOG.error("Error while parsing payload. Details: " + Utils.printStackTrace(e));
//            }
//        }
//        return null;
//    }
//    public static String preProcessPayloadWithLargeDataLiveness(String payload) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            LiveMatchingReqJSNObject liveMatchingReqJSNObject = objectMapper.readValue(payload, LiveMatchingReqJSNObject.class);
//            if (liveMatchingReqJSNObject.getFrames() != null) {
//                for (int i = 0; i < liveMatchingReqJSNObject.getFrames().length; i++) {
//                    liveMatchingReqJSNObject.getFrames()[i] = IdentityConstant.LONG_STRING;
//                }
//            }
//            return objectMapper.writeValueAsString(liveMatchingReqJSNObject);
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogAndCacheManager.isShowErrorLog()) {
//                LOG.error("Error while parsing payload. Details: " + Utils.printStackTrace(e));
//            }
//        }
//        return null;
//    }
//    public static String preProcessPayloadWithLargeDataFaceMatch(String payload) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            LiveMatchingReqJSNObject liveMatchingReqJSNObject = objectMapper.readValue(payload, LiveMatchingReqJSNObject.class);
//            if (liveMatchingReqJSNObject.getFrames() != null) {
//                for (int i = 0; i < liveMatchingReqJSNObject.getFrames().length; i++) {
//                    liveMatchingReqJSNObject.getFrames()[i] = IdentityConstant.LONG_STRING;
//                }
//            }
//            return objectMapper.writeValueAsString(liveMatchingReqJSNObject);
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogAndCacheManager.isShowErrorLog()) {
//                LOG.error("Error while parsing payload. Details: " + Utils.printStackTrace(e));
//            }
//        }
//        return null;
//    }
//    public static String preProcessPayloadWithLargeDataEnroll(String payload) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            LiveMatchingReqJSNObject liveMatchingReqJSNObject = objectMapper.readValue(payload, LiveMatchingReqJSNObject.class);
//            if (liveMatchingReqJSNObject.getFrames() != null) {
//                for (int i = 0; i < liveMatchingReqJSNObject.getFrames().length; i++) {
//                    liveMatchingReqJSNObject.getFrames()[i] = IdentityConstant.LONG_STRING;
//                }
//            }
//            return objectMapper.writeValueAsString(liveMatchingReqJSNObject);
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (LogAndCacheManager.isShowErrorLog()) {
//                LOG.error("Error while parsing payload. Details: " + Utils.printStackTrace(e));
//            }
//        }
//        return null;
//    }
    public static Date convertToUTC(Date d) throws ParseException {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String s = isoFormat.format(d);
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = isoFormat.parse(s);
        return date;
    }

    public static String convertToGMT(Date d) throws ParseException {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
        String s = isoFormat.format(d);
        return s;
    }

    public static String getStringOfOcspResponseTimeFollowGMTTime(Date time) {
        if (Configuration.getInstance().getServerTimeType().equalsIgnoreCase("utc")) {
            try {
                String s = Utils.convertToGMT(time);
                if (LogHandler.isShowDebugLog()) {
                    LOG.debug("Convert OcspRespSignedAt time from UTC to GMT time. Result: " + s);
                }
                return s;
            } catch (ParseException e) {
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Cannot convert to UTC time. Details: " + Utils.printStackTrace(e));
                }
            }
        }
        return (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z").format(time));
    }

    public static HashMap<String, String> getDataFromURLEncode(String payload) {
        try {
            LOG.info("Payload URL Encoded:" + payload);
            HashMap<String, String> map = new HashMap<>();
            String[] temp = payload.split("&");
            for (String temp2 : temp) {
                StringTokenizer token = new StringTokenizer(temp2, "=", false);
                while (token.hasMoreElements()) {
                    String name = token.nextToken();
                    String value = URLDecoder.decode(token.nextToken(),StandardCharsets.UTF_8.toString());                    
                    map.put(name, value);
                }
            }
            return map;
        } catch (Exception e) {
//            if(LogHandler.isShowErrorLog()){
            LOG.error("Cannot parse data from URL Encode");
//            }
            return null;
        }
    }
    
    public static Property getDataRESTFromString(String dataREST, byte[] p12){
        HashMap<String, String> map = new HashMap<>();
        StringTokenizer token = new StringTokenizer(dataREST,";", false);
        while(token.hasMoreTokens()){
            String[] row = token.nextToken().split("=");
            map.put(row[0],row[1]);
        }
        String baseUrl = map.get("mobileid.rssp.baseurl");
        String relyingParty = map.get("mobileid.rssp.rp.name");
        String relyingPartyUser = map.get("mobileid.rssp.rp.user");
        String relyingPartyPassword = map.get("mobileid.rssp.rp.password");
        String relyingPartySignature = map.get("mobileid.rssp.rp.signature");
        String relyingPartyKeyStore = map.get("mobileid.rssp.rp.keystore.file");
        String relyingPartyKeyStorePassword = map.get("mobileid.rssp.rp.keystore.password");
        byte[] relyingPartyKeyStoreData = p12;
        if(p12 != null){
        return new Property(
                baseUrl,
                relyingParty,
                relyingPartyUser,
                relyingPartyPassword,
                relyingPartySignature,
                relyingPartyKeyStoreData,
                relyingPartyKeyStorePassword);
        }
        return new Property(
                baseUrl,
                relyingParty,
                relyingPartyUser,
                relyingPartyPassword,
                relyingPartySignature,
                relyingPartyKeyStore,
                relyingPartyKeyStorePassword);
    }
    
    public static Property getDataRESTFromString2(String dataREST, byte[] p12){
        ArrayList<String> list = new ArrayList();
        StringTokenizer token = new StringTokenizer(dataREST,";", false);
        while(token.hasMoreTokens()){
            list.add(token.nextToken());
        }
//        String baseUrl = map.get("mobileid.rssp.baseurl");
//        String relyingParty = map.get("mobileid.rssp.rp.name");
//        String relyingPartyUser = map.get("mobileid.rssp.rp.user");
//        String relyingPartyPassword = map.get("mobileid.rssp.rp.password");
//        String relyingPartySignature = map.get("mobileid.rssp.rp.signature");
//        String relyingPartyKeyStore = map.get("mobileid.rssp.rp.keystore.file");
//        String relyingPartyKeyStorePassword = map.get("mobileid.rssp.rp.keystore.password");
        byte[] relyingPartyKeyStoreData = p12;
        
        return new Property(
                list.get(0),
                list.get(1),
                list.get(2),
                list.get(3),
                list.get(4),
                relyingPartyKeyStoreData,
                list.get(5));                
    }
    
    public static String hashMD5(String input){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }        
    }
        
}
