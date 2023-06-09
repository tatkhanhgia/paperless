 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless;

/**
 *
 * @author GiaTK
 */
public class PaperlessConstant {

    //System
    final public static long FEDERAL_ID = 0;
    final public static int NUMBER_OF_ACCESS_TOKEN = 63;
    final public static int NUMBER_OF_ITEMS_TYPE = 5;
    final public static int NUMBER_OF_FILE_DATA = 5;
    final public static String INTERNAL_EXP_MESS = "[\"Internal server exception\"]";
    final public static String DEFAULT_MESS = "Message is not defined";
    final public static int DEFAULT_ROW_COUNT = 100;
    
    final public static String TOKEN_TYPE_BEARER = "Bearer";
    final public static String TOKEN_TYPE_BASIC = "Basic";
    final public static String TOKEN_TYPE_REFRESH = "Refresh";

    final public static int LANGUAGE_VN = 1;
    final public static int LANGUAGE_EN = 2;
    
    final public static String EMAIL_SEND_PASSWORD = "email_send_password";
    final public static String EMAIL_FORGOT_PASSWORD = "email_forgot_password";
    
    //AccessToken Data
    final public static String alg = "SHA256withRSA";
    final public static String typ = "JWT";
    final public static long expired_in = 3600;
    final public static long refresh_token_expired_in = 36000;

    //Default data
    final public static long password_expired_at = 4800;
    final public static int BUSINESSTYPE_PERSONAL = 1;
    final public static int BUSINESSTYPE_BUSINESS = 2;

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
    final public static int NUMBER_WORKFLOW_TEMPLATE_TYPE = 8;
//    final public static int TEMPLATE_TYPE_ID_CARD_SECURE_QR_TEMPLATE = 1;
//    final public static int TEMPLATE_TYPE_SECURE_QR_TEMPLATE = 2;
//    final public static int TEMPLATE_TYPE_COURSE_CERTIFICATE_PDF_TEMPLATE = 3;
//    final public static int TEMPLATE_TYPE_SIMPLE_PDF_STAMPING_TEMPLATE = 4;
//    final public static int TEMPLATE_TYPE_PDF_STAMPING_TEMPLATE = 5;
//    final public static int TEMPLATE_TYPE_FILE_sTAMPING_TEMPLATE = 6;
//    final public static int TEMPLATE_TYPE_EIDCONTRACT = 7;

    //Internal - HTTP CODE
    final public static int HTTP_CODE_SUCCESS = 200;
    final public static int HTTP_CODE_FORBIDDEN = 403;
    final public static int HTTP_CODE_UNAUTHORIZED = 401;
    final public static int HTTP_CODE_BAD_REQUEST = 400;
    final public static int HTTP_CODE_NOT_FOUND = 404;
    final public static int HTTP_CODE_500 = 500;

    //Code
    final public static int CODE_SUCCESS = 0;
    final public static int CODE_FAIL = 1;
    final public static int CODE_INVALID_PARAMS_KEYCLOAK = 5000;
    final public static int CODE_INVALID_PARAMS_WORKFLOW = 5001;
    final public static int CODE_INVALID_PARAMS_WORKFLOWACTIVITY = 5002;
    final public static int CODE_INVALID_PARAMS_WORKFLOW_TEMPLATE = 5003;
    final public static int CODE_INVALID_PARAMS_ASSET = 5004;
    final public static int CODE_INVALID_PARAMS_JWT = 5005;
//    final public static int CODE_INVALID_PARAMS_WORKFLOW_TEMPLATE = 5003;

    final public static int CODE_FUNCTION_ACCESS_DENIED = 5005;
    final public static int CODE_INVALID_BEARER_TOKEN = 5006;
    final public static int CODE_EXPIRED_BEARER_TOKEN = 5007;

    //-----------------------SUB CODE    -----------------------------
    final public static int SUBCODE_SUCCESS = 0;
    final public static int SUBCODE_NO_PAYLOAD_FOUND = 1;
    final public static int SUBCODE_INVALID_PAYLOAD_STRUCTURE = 6;
    final public static int SUBCODE_INTERNAL_ERROR = -1;
    final public static int SUBCODE_MISSING_IMAGE = 2;
    final public static int SUBCODE_SIGNING_ERROR = 3;
    final public static int SUBCODE_INVALID_AUTHORIZED_CODE = 4;
    final public static int SUBCODE_RESEND_ACTIVATION_EMAIL = 5;
    final public static int SUBCODE_RESET_PASSWORD_ACCOUNT_AGAIN = 7;
    final public static int SUBCODE_MISSING_X_SECURITY_CODE = 8;
    final public static int SUBCODE_MISSING_OLD_OR_NEW_PASSWORD = 9;

    //SUBCODE INVALID KEYCLOAK - 5000
    final public static int SUBCODE_INVALID_USER_CREDENTIALS = 2;
    final public static int SUBCODE_INVALID_CLIENT_SECRET = 3;
    final public static int SUBCODE_UNSUPPORTED_GRANT_TYPE = 4;
    final public static int SUBCODE_INVALID_CLIENT_CREDENTIALS = 5;
    final public static int SUBCODE_MISSING_USER_NAME_OR_PASSWORD = 7;
    final public static int SUBCODE_MISSING_GRANT_TYPE = 8;
    final public static int SUBCODE_MISSING_CLIENT_SECRET = 9;
    final public static int SUBCODE_TOKEN_NOT_PROVIDED = 10;
    final public static int SUBCODE_INVALID_TOKEN = 11;
    final public static int SUBCODE_TOKEN_EXPIRED = 12;
    final public static int SUBCODE_MISSING_ACCESS_TOKEN = 13;
    final public static int SUBCODE_UNAUTHORIZED_USER = 14;
    final public static int SUBCODE_MISSING_REFRESH_TOKEN = 15;
    final public static int SUBCODE_INVALID_CLIENT_ID = 16;
    final public static int SUBCODE_MISSING_USER_EMAIL = 17;
    final public static int SUBCODE_MISSING_AUTHORIZATION_CODE = 18;

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
    final public static int SUBCODE_EXISTED_WORKFLOW_ACTIVITY = 5;
    final public static int SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_EXISTED = 7;
    final public static int SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_PROCESS_YET = 8;
    final public static int SUBCODE_FILE_DOCUMENT_DOES_NOT_EXISTED = 9;
    final public static int SUBCODE_WORKFLOW_ACTIVITY_ALREADY_PROCESS = 10;
    final public static int SUBCODE_MISSING_VALUE_OF_FILE_DATA = 11;
    final public static int SUBCODE_MISSING_FILE_FIELD_IN_FILE_DATA = 12;
    final public static int SUBCODE_MISSING_FILE_FIELD_IN_ITEMS = 13;

    //SUBCPDE INVALID WORKFLOW TEMPLATE - 5003
//    final public static int SUBCODE_WORKFLOW_TEMPLATE_ALREADY_EXISTED = 2;
    
    //SUBCODE INVALID ASSET - 5004
    final public static int SUBCODE_CANNOT_UPLOAD_ASSET = 2;
    final public static int SUBCODE_INVALID_FILE_TYPE = 3;

    //SUBCODE INVALID JWT - 5005
    final public static int SUBCODE_INVALID_JWT_TOKEN = 2;
    final public static int SUBCODE_MISSING_EMAIL_IN_JWT = 3;
}
