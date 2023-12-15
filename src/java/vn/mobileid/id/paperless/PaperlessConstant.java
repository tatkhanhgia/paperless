 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless;

import vn.mobileid.id.general.PolicyConfiguration;

/**
 *
 * @author GiaTK
 */
public class PaperlessConstant {

    //System
    final public static int NUMBER_OF_ACCESS_TOKEN = 63;
    final public static int NUMBER_OF_ITEMS_TYPE = 5;
    final public static int NUMBER_OF_FILE_DATA = 5;
    final public static String INTERNAL_EXP_MESS = "{[\"Internal server exception\"]}";
    final public static String DEFAULT_MESS = "Message is not defined";
    final public static int DEFAULT_ROW_COUNT = PolicyConfiguration
            .getInstant()
            .getSystemConfig()
            .getAttributes().get(0)
            .getDefault_row_count();
    
    final public static String TOKEN_TYPE_BEARER = "Bearer";
    final public static String TOKEN_TYPE_BASIC = "Basic";
    final public static String TOKEN_TYPE_REFRESH = "Refresh";
    
    
    //AccessToken Data
    final public static String alg = PolicyConfiguration
            .getInstant()
            .getSystemConfig()
            .getAttributes().get(0)
            .getTokenConfig()
            .getAlg();
    
    final public static String typ = PolicyConfiguration
            .getInstant()
            .getSystemConfig()
            .getAttributes().get(0)
            .getTokenConfig()
            .getTyp();

    final public static long expired_in = PolicyConfiguration
            .getInstant()
            .getSystemConfig()
            .getAttributes().get(0)
            .getTokenConfig()
            .getAccess_token_expired_in();

    final public static long refresh_token_expired_in = PolicyConfiguration
            .getInstant()
            .getSystemConfig()
            .getAttributes().get(0)
            .getTokenConfig()
            .getRefresh_token_expired_in();

    //Default data
    final public static long password_expired_at = PolicyConfiguration
            .getInstant()
            .getSystemConfig()
            .getAttributes().get(0)
            .getTokenConfig()
            .getPassword_user_expired_at();       

    //Template Type
    final public static int NUMBER_WORKFLOW_TEMPLATE_TYPE = 8;

    //Internal - HTTP CODE
    final public static int HTTP_CODE_SUCCESS = 200;
    final public static int HTTP_CODE_FORBIDDEN = 403;
    final public static int HTTP_CODE_UNAUTHORIZED = 401;
    final public static int HTTP_CODE_BAD_REQUEST = 400;
    final public static int HTTP_CODE_NOT_FOUND = 404;
    final public static int HTTP_CODE_500 = 500;
    final public static int HTTP_CODE_METHOD_NOT_ALLOWED = 405;

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
    final public static int CODE_FMS = 5006;
    final public static int CODE_INVALID_PASSWORD = 5007;
    final public static int CODE_CANNOT_SIGNING = 5008;
    final public static int CODE_INVALID_PARAMS_WORKFLOWDETAIL = 5009;

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
    final public static int SUBCODE_CANNOT_GET_ASSET_TEMPLATE_TYPE = 10;

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
    final public static int SUBCODE_USER_ALREADY_VERIFIED = 19;
    final public static int SUBCODE_USER_NEED_TO_CHANGE_PASSWORD = 20;
    final public static int SUBCODE_CANNOT_FORGOT_PASSWORD_USER_HAS_BEEN_LOCKED = 21;

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
    final public static int SUBCODE_FILE_DATA_IS_TOO_MUCH_VALUES = 14;
    final public static int SUBCODE_THIS_TYPE_OF_WORKFLOW_DOES_NOT_SUPPORT_YET = 15;
    final public static int SUBCODE_NOT_SUPPORT_THIS_STATUS_OF_WORKFLOW_ACTIVITY = 16;
    final public static int SUBCODE_DATE_IN_ITEMS_DOES_NOT_FIT = 17;
    final public static int SUBCODE_CANNOT_DECODE_FILE_DATA = 18;

    //SUBCPDE INVALID WORKFLOW TEMPLATE - 5003
//    final public static int SUBCODE_WORKFLOW_TEMPLATE_ALREADY_EXISTED = 2;
    
    //SUBCODE INVALID ASSET - 5004
    final public static int SUBCODE_CANNOT_UPLOAD_ASSET = 2;
    final public static int SUBCODE_INVALID_FILE_TYPE = 3;
    final public static int SUBCODE_THIS_TYPE_IS_NOT_AN_ASSET = 4;
    final public static int SUBCODE_MISSING_FILE_DATA = 5;
    final public static int SUBCODE_INVALID_FILE_DATA = 6;
    final public static int SUBCODE_MISSING_ASSET_NAME = 7;
    final public static int SUBCODE_NOT_SUPPORT_EXTENSION = 8;
    final public static int SUBCODE_MISSING_METADATA_SIGNING_POSITION_IN_ASSET = 9;
    final public static int SUBCODE_MISSING_QR_POSITION_PROPERTIES_IN_PAYLOAD = 10;
    final public static int SUBCODE_MISSING_SIGNATURE_POSITION_PROPERTIES_IN_PAYLOAD = 11;

    //SUBCODE INVALID JWT - 5005
    final public static int SUBCODE_INVALID_JWT_TOKEN = 2;    
    final public static int SUBCODE_MISSING_EMAIL_IN_JWT = 3;
    final public static int SUBCODE_MISSING_JWT = 4;
    
    //SUBCODE INVALID FMS - 5006
    final public static int SUBCODE_ERROR_WHILE_UPLOADING_TO_FMS = 2;
    final public static int SUBCODE_FMS_REJECT_UPLOAD = 3;
    final public static int SUBCODE_ERROR_WHILE_DOWNLOADING_FROM_FMS = 4;
    final public static int SUBCODE_FMS_REJECT_DOWNLOAD = 5;
    
    //SUBCODE INVALID PASSWORD - 5007
    final public static int SUBCODE_LENGTH_OF_PASSWORD_TOO_SHORT = 2;
    final public static int SUBCODE_LENGTH_OF_PASSWORD_TOO_LONG = 3;
    final public static int SUBCODE_PASSWORD_NEEDS_CONTAIN_NUMERIC = 4;
    final public static int SUBCODE_PASSWORD_NEEDS_CONTAIN_LOWERCASE = 5;
    final public static int SUBCODE_PASSWORD_NEEDS_CONTAIN_UPPERCASE = 6;
    final public static int SUBCODE_PASSWORD_NEEDS_CONTAIN_SPECIALCHAR = 7;
    final public static int SUBCODE_PASSWORD_MUST_ONLY_NUMERIC = 8;
    
    //SUBCODE CANNOT SIGNING - 5008
    final public static int SUBCODE_CANNOT_GET_SIGNING_INFO_PROPERTIES = 2;
    final public static int SUBCODE_INVALID_PAGE_IN_POSITION_CONFIGURATION = 3;
    
    //SUBCODE INVALID PARAMS WORKFLOW DETAIL - 5009
    final public static int SUBCODE_CANNOT_GET_PAGES = 2;
    
}
