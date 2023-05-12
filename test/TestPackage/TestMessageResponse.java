///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package TestPackage;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import vn.mobileid.id.general.LogHandler;
//import vn.mobileid.id.general.Resources;
//import vn.mobileid.id.general.database.Database;
//import vn.mobileid.id.general.database.DatabaseImpl;
//import vn.mobileid.id.general.objects.ResponseCode;
//import vn.mobileid.id.qrypto.objects.QryptoConstant;
//import vn.mobileid.id.qrypto.objects.QryptoGetTokenMessageJSNObject;
//import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
//import static vn.mobileid.id.qrypto.objects.QryptoMessageResponse.isVietnamese;
//import vn.mobileid.id.utils.Configuration;
//import vn.mobileid.id.utils.Utils;
//
///**
// *
// * @author Admin
// */
//public class TestMessageResponse {
//    
//    private static final Logger LOG = LogManager.getLogger(QryptoMessageResponse.class);
//    
//    final private static ObjectMapper objectMapper = new ObjectMapper();
//    
//    public static String getAccessTokenMessage(
//            int code,
//            int subCode,
//            String lang,
//            String transactionID,
//            String accessToken,
//            String tokenType,
//            int expiresIn) {
//        try {
//            QryptoGetTokenMessageJSNObject responseMessageJSNObject = new QryptoGetTokenMessageJSNObject();
//            String strCode = String.valueOf(code);
//            ResponseCode responseCode = Resources.getResponseCodes().get(strCode);
//            if (responseCode == null) {
//                Resources.reloadResponseCodes();
//                responseCode = Resources.getResponseCodes().get(strCode);
//            }
//            String descriptionKey = String.valueOf(code) + "." + lang.toLowerCase() + "." + String.valueOf(subCode);
//            if (responseCode != null) {
//                responseMessageJSNObject.setStatus(code);
//                responseMessageJSNObject.setMessage(isVietnamese(lang) ? responseCode.getRemark() : responseCode.getRemarkEn());
//                responseMessageJSNObject.setDescription(Configuration.getInstance().getQryptoDescription().getProperty(descriptionKey));
//
//                responseMessageJSNObject.setAccessToken(accessToken);
//                responseMessageJSNObject.setExpires_in(expiresIn);
//                responseMessageJSNObject.setTokenType(tokenType);
////                responseMessageJSNObject.setTransactionID(transactionID);
//                return objectMapper.writeValueAsString(responseMessageJSNObject);
////            } else {
////                Database db = new DatabaseImpl();
////                responseCode = db.getResponse(String.valueOf(code));
////                if (responseCode == null) {
//////                    if (LogHandler.isShowErrorLog()) {
//////                        LOG.error("Response code " + code + " is not defined in database.");
//////                    }
////                    responseMessageJSNObject.setStatus(code);
////                    responseMessageJSNObject.setMessage(QryptoConstant.DEFAULT_MESS);
////                    responseMessageJSNObject.setDescription(Configuration.getInstance().getIdentityDescription().getProperty(descriptionKey));
////
////                    responseMessageJSNObject.setAccessToken(accessToken);
////                    responseMessageJSNObject.setExpires_in(expiresIn);
////                    responseMessageJSNObject.setTokenType(tokenType);
////
//////                    responseMessageJSNObject.setTransactionID(transactionID);
////                    return objectMapper.writeValueAsString(responseMessageJSNObject);
////                } else {
////                    Resources.getResponseCodes().put(strCode, responseCode);
////                    responseMessageJSNObject.setStatus(code);
////                    responseMessageJSNObject.setMessage(isVietnamese(lang) ? responseCode.getRemark() : responseCode.getRemarkEn());
////                    responseMessageJSNObject.setDescription(Configuration.getInstance().getIdentityDescription().getProperty(descriptionKey));
////
////                    responseMessageJSNObject.setAccessToken(accessToken);
////                    responseMessageJSNObject.setExpires_in(expiresIn);
////                    responseMessageJSNObject.setTokenType(tokenType);
//////                responseMessageJSNObject.setTransactionID(transactionID);
////                    return objectMapper.writeValueAsString(responseMessageJSNObject);                    
////                }
//            }
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
////            if (LogHandler.isShowErrorLog()) {
////                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
////            }
//            return QryptoConstant.INTERNAL_EXP_MESS;
//        }
//    }
//    
//    public static void main(String[] args){
//        Resources.init();
//        String test =TestMessageResponse.getAccessTokenMessage(0, 1, "vn", null, "accessToken", "Bearer", 0);
//        System.out.println("Test:"+test);
//    }
//}
