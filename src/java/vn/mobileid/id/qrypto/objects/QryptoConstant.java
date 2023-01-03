/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.objects;

/**
 *
 * @author GiaTK
 */
public class QryptoConstant {
    final public static String JWT_ISSUER = "https://id.mobile-id.vn";
    final public static String SIGNING_CERTIFICATES_QUERY_PATH = "/dtis/v1/e-verification/certificates";
    
    final public static int NUMBER_OF_ACCESS_TOKEN = 68;

    final public static long FEDERAL_ID = 1;
    final public static int SERVICE_TYPE_ID_CREATION = 2;
    final public static int PKI_FORMFACTOR_ESIGNCLOUD = 8;

    final public static String AWS_CREDENTIALS_SUCCESS = null;

    final public static String INTERNAL_EXP_MESS = "[\"Internal server exception\"]";
    final public static String DEFAULT_MESS = "Message is not defined";

    final public static String TOKEN_TYPE_BEARER = "Bearer";
    final public static String TOKEN_TYPE_BASIC = "Basic";

    final public static String PROPERTY_KEY_PARTNER = "partner";
    final public static String PROPERTY_KEY_SUBJECT = "subject";
    final public static String PROPERTY_KEY_CONTENT = "content";
    
    final public static int TIMESTAMP_MODE_NO_ADDING = 0;
    final public static int TIMESTAMP_MODE_ALWAYS_ADDING = 1;
    final public static int TIMESTAMP_MODE_ADDING_IF_NO_ANY_TSS_APPLIED = 2;

    //internal process
    final public static int CODE_INTERNAL_OFFICE_INVALID_DOC = -1;
    
    //Code
    final public static int CODE_SUCCESS = 0;
    final public static int CODE_INVALID_PARAMS_KEYCLOAK = 5000;
    final public static int CODE_UNEXPE_EXP = 5001;
    final public static int CODE_INVALID_CREDENTIALS = 5002;
    final public static int CODE_INVALID_IP_ADDR = 5003;
    final public static int CODE_INVALID_AWS_V4 = 5004;
    final public static int CODE_FUNCTION_ACCESS_DENIED = 5005;
    final public static int CODE_INVALID_BEARER_TOKEN = 5006;
    final public static int CODE_EXPIRED_BEARER_TOKEN = 5007;
    final public static int CODE_DETACHED_SIGNATURE_NO_DATA = 5008;
    final public static int CODE_CADES_VERIFICATION_FAILED = 5009;
    final public static int CODE_INVALID_DOC_FORMAT = 5010;
    final public static int CODE_NO_SIGNATURE_FOUND = 5011;
    final public static int CODE_PADES_VERIFICATION_FAILED = 5012;
    final public static int CODE_USERNAME_EXISTED = 5013;
    final public static int CODE_OWNER_EXISTED = 5014;
    final public static int CODE_NO_OWNER_FOUND = 5015;
    final public static int CODE_CONFLICT_PERSONAL_ENTERPRISE_ID = 5016;
    final public static int CODE_INVALID_CERT_VALIDITY = 5017;
    final public static int CODE_UNTRUSTED_CERT = 5018;
    final public static int CODE_INVALID_REVOCATION_STATUS = 5019;
    final public static int CODE_CERTIFICATE_EXISTED = 5020;
    final public static int CODE_AGREEMENT_EXISTED = 5021;
    final public static int CODE_NO_AGREEMENT_FOUND = 5022;
    final public static int CODE_AGREEMENT_ALREADY_ASSIGNED = 5023;
    final public static int CODE_NOT_YET_OWNER_ASSIGNED = 5024;
    final public static int CODE_NO_CERTIFICATE_REGISTERED_OWNER = 5025;
    final public static int CODE_OWNER_NO_HAS_EMAIL = 5026;
    final public static int CODE_OWNER_NO_HAS_MOBILE = 5027;
    final public static int CODE_GET_CHALLENGE_REQUIRED = 5028;
    final public static int CODE_CHALLENGE_EXPIRED = 5029;
    final public static int CODE_INVALID_SIGNATURE = 5030;
    final public static int CODE_EMAIL_VERIFICATION_REQUIRED = 5031;
    final public static int CODE_MOBILE_VERIFICATION_REQUIRED = 5032;
    final public static int CODE_OTP_BLOCKED = 5033;
    final public static int CODE_OTP_EXCEEDED = 5034;
    final public static int CODE_OTP_INVALID = 5035;
    final public static int CODE_OTP_EXPIRED = 5036;
    final public static int CODE_CHALLENGE_INVALID = 5037;
    final public static int CODE_XADES_VERIFICATION_FAILED = 5038;
    final public static int CODE_UNEXPECTED_MATCH_RESULT = 5039;
    final public static int CODE_UNEXPECTED_VERIFICATION_RESULT = 5040;
    final public static int CODE_EID_VERIFICATION_FAILED = 5041;
    final public static int CODE_OFFICE_VERIFICATION_FAILED = 5042;

    //------------SUB CODE
    final public static int SUBCODE_NOSUBCODE = -1;
    final public static int SUBCODE_SUCCESS = 0;    
    final public static int SUBCODE_NO_PAYLOAD_FOUND = 1;
    final public static int SUBCODE_INVALID_USER_CREDENTIALS = 2;
    final public static int SUBCODE_INVALID_CLIENT_SECRET = 3;
    final public static int SUBCODE_UNSUPPORTED_GRANT_TYPE = 4;
    final public static int SUBCODE_INVALID_CLIENT_CREDENTIALS = 5;
    final public static int SUBCODE_INVALID_PAYLOAD_STRUCTURE = 6;
    final public static int SUBCODE_DOCUMENT_MISSING = 100;
    final public static int SUBCODE_INVALID_PDF_FORMAT = 7;
    final public static int SUBCODE_AT_LEAST_PERSONAL_NAME_OR_ORGANIZATION_PROVIDED = 8;
    final public static int SUBCODE_IDENTIFICATION_MISSING = 9;
    final public static int SUBCODE_INVALID_IDENTIFICATION_TYPE = 10;
    final public static int SUBCODE_EMAIL_OWNER_MISSING = 11;
    final public static int SUBCODE_OWNER_OWNER_MISSING = 12;
    final public static int SUBCODE_INVALID_PERSONAL_ID = 13;
    final public static int SUBCODE_INVALID_ENTERPRISE_ID = 14;
    final public static int SUBCODE_PERSONAL_NAME_MISSING = 15;
    final public static int SUBCODE_ORGANIZATION_MISSING = 16;
    final public static int SUBCODE_CERTIFICATE_MISSING = 17;
    final public static int SUBCODE_INVALID_SHARED_MODE = 18;
    final public static int SUBCODE_INVALID_CERTIFICATE_DATA = 19;
    final public static int SUBCODE_MISSING_AGREEMENT_ID = 20;
    final public static int SUBCODE_MISSING_CERTIFICATE_ID = 21;
    final public static int SUBCODE_SIGNATURE_FORMAT_MISSING = 22;
    final public static int SUBCODE_INVALID_SIGNATURE_FORMAT = 23;
    final public static int SUBCODE_OTP_TYPE_MISSING = 24;
    final public static int SUBCODE_OTP_MISSING = 25;
    final public static int SUBCODE_TRANSACTION_ID_MISSING = 26;
    final public static int SUBCODE_INVALID_ENCODING = 27;
    final public static int SUBCODE_INVALID_XML_FORMAT = 28;
    final public static int SUBCODE_UNEXPECTED_MATCH_RESULT = 29;
    final public static int SUBCODE_UNEXPECTED_VERIFICATION_RESULT = 30;
    final public static int SUBCODE_PA_JWT_REQUIRED = 31;
    
    
}
