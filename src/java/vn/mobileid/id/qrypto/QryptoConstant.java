/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto;

/**
 *
 * @author GiaTK
 */
public class QryptoConstant {    
    //System
    final public static long FEDERAL_ID = 0;
    final public static int NUMBER_OF_ACCESS_TOKEN = 63;
    final public static int NUMBER_OF_ITEMS_TYPE = 3; //String - boolean - binary
    final public static String INTERNAL_EXP_MESS = "[\"Internal server exception\"]";
    final public static String DEFAULT_MESS = "Message is not defined";

    final public static String TOKEN_TYPE_BEARER = "Bearer";
    final public static String TOKEN_TYPE_BASIC = "Basic";
    
    final public static int FLAG_FALSE_DB = 0;
    final public static int FLAG_TRUE_DB = 1;
    
    final public static int ASSET_TYPE_BACKGROUND = 1;
    final public static int ASSET_TYPE_APPEND = 2;
    final public static int ASSET_TYPE_TEMPLATE = 3;
    
    //Workflow Type    
    final public static int WORKFLOW_TYPE_QR = 1;
    final public static int WORKFLOW_TYPE_PDF_GENERATOR = 2;
    final public static int WORKFLOW_TYPE_SIMPLE_PDF_STAMPING = 3;
    final public static int WORKFLOW_TYPE_PDF_STAMPING = 4;
    final public static int WORKFLOW_TYPE_FILE_STAMPING = 5;
    final public static int WORKFLOW_TYPE_LEI_PDF_STAMPING = 6;
    
    //Template Type
    final public static int NUMBER_WORKFLOW_TEMPLATE_TYPE = 7;
    final public static int TEMPLATE_TYPE_ID_CARD_SECURE_QR_TEMPLATE = 1;
    final public static int TEMPLATE_TYPE_SECURE_QR_TEMPLATE = 2;
    final public static int TEMPLATE_TYPE_COURSE_CERTIFICATE_PDF_TEMPLATE = 3;
    final public static int TEMPLATE_TYPE_SIMPLE_PDF_STAMPING_TEMPLATE = 4;
    final public static int TEMPLATE_TYPE_PDF_STAMPING_TEMPLATE = 5;
    final public static int TEMPLATE_TYPE_FILE_sTAMPING_TEMPLATE = 6;
    final public static int TEMPLATE_TYPE_EIDCONTRACT = 7;
    
    //Internal - HTTP CODE
    final public static int HTTP_CODE_SUCCESS = 200;
    final public static int HTTP_CODE_FORBIDDEN = 403;
    final public static int HTTP_CODE_UNAUTHORIZED = 401;
    final public static int HTTP_CODE_BAD_REQUEST = 400;
    final public static int HTTP_CODE_NOT_FOUND = 404;
           
    
    //Code
    final public static int CODE_SUCCESS = 0;
    final public static int CODE_FAIL = 1;
    final public static int CODE_INVALID_PARAMS_KEYCLOAK = 5000;
    final public static int CODE_INVALID_PARAMS_WORKFLOW = 5001;
    final public static int CODE_INVALID_PARAMS_WORKFLOWACTIVITY = 5002;
    
    
    
    final public static int CODE_FUNCTION_ACCESS_DENIED = 5005;
    final public static int CODE_INVALID_BEARER_TOKEN = 5006;
    final public static int CODE_EXPIRED_BEARER_TOKEN = 5007;
            
    //-----------------------SUB CODE    -----------------------------
    final public static int SUBCODE_SUCCESS = 0;    
    final public static int SUBCODE_NO_PAYLOAD_FOUND = 1;
    final public static int SUBCODE_INVALID_PAYLOAD_STRUCTURE = 6;
    final public static int SUBCODE_INTERNAL_ERROR = -1;
    
    //SUBCODE INVALID KEYCLOAK - 5000
    final public static int SUBCODE_INVALID_USER_CREDENTIALS = 2;
    final public static int SUBCODE_INVALID_CLIENT_SECRET = 3;
    final public static int SUBCODE_UNSUPPORTED_GRANT_TYPE = 4;
    final public static int SUBCODE_INVALID_CLIENT_CREDENTIALS = 5;    
    final public static int SUBCODE_MISSING_USER_NAME = 7;
    final public static int SUBCODE_MISSING_GRANT_TYPE = 8;
    final public static int SUBCODE_MISSING_CLIENT_SECRET = 9;
    final public static int SUBCODE_TOKEN_NOT_PROVIDED = 10;
    final public static int SUBCODE_INVALID_TOKEN = 11;
    final public static int SUBCODE_TOKEN_EXPIRED = 12;
    final public static int SUBCODE_MISSING_ACCESS_TOKEN = 13;
    
    //SUBCODE INVALID WORKFLOW - 5001
    final public static int SUBCODE_MISSING_WORKFLOW_LABEL = 2;
    final public static int SUBCODE_MISSING_WORKFLOW_USER_EMAIL_OR_ID = 3;
    final public static int SUBCODE_MISSING_WORKFLOW_CREATED_BY = 4;
    final public static int SUBCODE_MISSING_OR_ERROR_TEMPLATE_TYPE = 5;
    final public static int SUBCODE_MISSING_INPUT_FIELD = 7;    
    final public static int SUBCODE_MISSING_OR_ERROR_FIELD_TYPE = 8;    
    final public static int SUBCODE_MISSING_OR_ERROR_VALUE = 9;    
    
    //SUBCODE INVALID WORKFLOW ACTIVITY - 5002
    final public static int SUBCODE_MISSING_ENTERPRISE_DATA = 2;
    final public static int SUBCODE_MISSING_WORKFLOW_ID = 3;
    final public static int SUBCODE_MISSING_OR_ERROR_FILE_TYPE = 4;
}
