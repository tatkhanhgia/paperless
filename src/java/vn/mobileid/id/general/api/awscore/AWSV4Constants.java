package vn.mobileid.id.general.api.awscore;

public class AWSV4Constants {

    public static final String TERMINATION_STRING = "aws4_request";
    public static final char QUERY_PARAMETER_SEPARATOR = '&';
    public static final char QUERY_PARAMETER_VALUE_SEPARATOR = '=';
    public static final String AUTH_TAG = "AWS4";
    public static final String ALGORITHM = AUTH_TAG + "-HMAC-SHA256";

    //Constants for algorithm
    public static final String HMAC_SHA256 = "HmacSHA256";
    public static final String SHA_256 = "SHA-256";

    //Constants for restful header
    public static final String HOST = "Host";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String X_AMZ_DATE = "X-Amz-Date";
    public static final String AUTHORIZATION = "Authorization";

    public static final String X_API_KEY = "X-Api-Key";
    public static final String CREDENTIAL = "Credential";
    public static final String SIGNEDHEADERS = "SignedHeaders";
    public static final String SIGNATURE = "Signature";
    public static final String X_AMZ_SECURITY_TOKEN = "X-Amz-Security-Token";

    public static final String X_POLICY_FORM_DATA = "X-Policy-FormData";
    
    public static final String USER_AGENT = "User-Agent";
}
