package RestfulFactory.Request;

public class CredentialInfoRequest extends CertificateRequest {

    private String credentialID;

    public String getCredentialID() {
        return credentialID;
    }

    public void setCredentialID(String credentialID) {
        this.credentialID = credentialID;
    }
}
