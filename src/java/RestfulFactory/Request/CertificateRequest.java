package RestfulFactory.Request;

public class CertificateRequest extends Request {

    private String agreementUUID;
    private String certificates;
    private boolean certInfoEnabled;
    private boolean authInfoEnabled;

    public String getAgreementUUID() {
        return agreementUUID;
    }

    public void setAgreementUUID(String agreementUUID) {
        this.agreementUUID = agreementUUID;
    }

    public String getCertificates() {
        return certificates;
    }

    public void setCertificates(String certificates) {
        this.certificates = certificates;
    }

    public boolean getCertInfoEnabled() {
        return certInfoEnabled;
    }

    public void setCertInfoEnabled(boolean certInfoEnabled) {
        this.certInfoEnabled = certInfoEnabled;
    }

    public boolean getAuthInfoEnabled() {
        return authInfoEnabled;
    }

    public void setAuthInfoEnabled(boolean authInfoEnabled) {
        this.authInfoEnabled = authInfoEnabled;
    }
}
   