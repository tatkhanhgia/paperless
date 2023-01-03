package vn.mobileid.id.general.api.awscore;

public enum AWSV4ResponseCode {
    MISSING_X_API_KEY("Missing x_api_key in request"),
    MISSING_AUTHENTICATION_TOKEN("Missing Authentication Token"),
    INVALID_KEY_VALUE("not a valid key=value pair"),
    UNSUPPORTED_AWS_ALGORITHM("Unsupported AWS 'algorithm'"),
    MISSING_CREDENTIAL("Missing Credential in Authorization header"),
    MISSING_SIGNEDHEADERS("Missing SignedHeaders in Authorization header"),
    MISSING_SIGNATURE("Missing Signature in Authorization header"),
    CREDENTIAL_INVALID("Credential is invalid"),
    TOKEN_REQUEST_INVALID("Token request is invalid"),
    MISSING_X_AMZ_DATE("Missing x_Amz_Date in header"),
    X_AMZ_DATE_INVALID("Date must be in ISO-8601 format"),
    DATE_CREDENTIAL_NOT_MATCH("Date in Credential does not match"),
    X_AMZ_DATE_EXPIRED("Signature is expired...."),
    MISSING_HOST("Missing Host in SignedHeader"),
    MISSING_VALUE_FOR_SIGNED_HEADERS("Missing value in SignedHeaders"),
    INVALID_SIGNATURE("Signature is invalid"),
    MISSING_POLICY_FORM_DATA("Missing X-Policy-FormData in header"),
    INVALID_POLICY_FORM_DATA("Invalid X-Policy-FormData in header"),
    VERIFY_OK("Good request");

    private final String name;

    private AWSV4ResponseCode(String s) {
        name = s;
    }

    public String getName() {
        return name;
    }

}
