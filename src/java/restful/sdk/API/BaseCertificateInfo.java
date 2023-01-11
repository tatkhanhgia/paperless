package restful.sdk.API;

import RestfulFactory.Model.CertificateProfile;
import RestfulFactory.Model.CertificateAuthority;

public class BaseCertificateInfo {

    private String status;
    private String statusDesc;
    private String[] certificates;
    private String csr;
    private String credentialID;
    private String issuerDN;
    private String serialNumber;
    private String thumbprint;
    private String subjectDN;
    private String validFrom;
    private String validTo;
    private String purpose;
    private int version;
    private String multisign;
    private int numSignatures;
    private int remainingSigningCounter;
    private String authorizationEmail;
    private String authorizationPhone;
    private CertificateProfile certificateProfile;
    private CertificateAuthority certificateAuthority;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String[] getCertificates() {
        return certificates;
    }

    public void setCertificates(String[] certificates) {
        this.certificates = certificates;
    }

    public String getCsr() {
        return csr;
    }

    public void setCsr(String csr) {
        this.csr = csr;
    }

    public String getCredentialID() {
        return credentialID;
    }

    public void setCredentialID(String credentialID) {
        this.credentialID = credentialID;
    }

    public String getIssuerDN() {
        return issuerDN;
    }

    public void setIssuerDN(String issuerDN) {
        this.issuerDN = issuerDN;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getThumbprint() {
        return thumbprint;
    }

    public void setThumbprint(String thumbprint) {
        this.thumbprint = thumbprint;
    }

    public String getSubjectDN() {
        return subjectDN;
    }

    public void setSubjectDN(String subjectDN) {
        this.subjectDN = subjectDN;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getMultisign() {
        return multisign;
    }

    public void setMultisign(String multisign) {
        this.multisign = multisign;
    }

    public int getNumSignatures() {
        return numSignatures;
    }

    public void setNumSignatures(int numSignatures) {
        this.numSignatures = numSignatures;
    }

    public int getRemainingSigningCounter() {
        return remainingSigningCounter;
    }

    public void setRemainingSigningCounter(int remainingSigningCounter) {
        this.remainingSigningCounter = remainingSigningCounter;
    }

    public String getAuthorizationEmail() {
        return authorizationEmail;
    }

    public void setAuthorizationEmail(String authorizationEmail) {
        this.authorizationEmail = authorizationEmail;
    }

    public String getAuthorizationPhone() {
        return authorizationPhone;
    }

    public void setAuthorizationPhone(String authorizationPhone) {
        this.authorizationPhone = authorizationPhone;
    }

    public CertificateProfile getCertificateProfile() {
        return certificateProfile;
    }

    public void setCertificateProfile(CertificateProfile certificateProfile) {
        this.certificateProfile = certificateProfile;
    }

    public CertificateAuthority getCertificateAuthority() {
        return certificateAuthority;
    }

    public void setCertificateAuthority(CertificateAuthority certificateAuthority) {
        this.certificateAuthority = certificateAuthority;
    }
}
