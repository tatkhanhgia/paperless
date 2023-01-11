package RestfulFactoryl.Response;

import restful.sdk.API.CertificateInfo;
import restful.sdk.API.Types.AuthMode;

public class CredentialInfoResponse extends Response {

    private CertificateInfo cert;
    private String sharedMode;
    private String createdRP;
    private String[] authModes;
    private AuthMode authMode; //doi kieu string
    private int SCAL;
    private String contractExpirationDate;
    private boolean defaultPassphraseEnabled;
    private boolean trialEnabled;
    
    private int multisign;
    private int remainingSigningCounter;
    private String authorizationEmail;
    private String authorizationPhone;

    public CertificateInfo getCert() {
        return cert;
    }

    public void setCert(CertificateInfo cert) {
        this.cert = cert;
    }

    public String getSharedMode() {
        return sharedMode;
    }

    public void setSharedMode(String sharedMode) {
        this.sharedMode = sharedMode;
    }

    public String getCreatedRP() {
        return createdRP;
    }

    public void setCreatedRP(String createdRP) {
        this.createdRP = createdRP;
    }

    public String[] getAuthModes() {
        return authModes;
    }

    public void setAuthModes(String[] authModes) {
        this.authModes = authModes;
    }

    public AuthMode getAuthMode() {
        return authMode;
    }

    public void setAuthMode(AuthMode authMode) {
        this.authMode = authMode;
    }

    public int getSCAL() {
        return SCAL;
    }

    public void setSCAL(int SCAL) {
        this.SCAL = SCAL;
    }

    public String getContractExpirationDate() {
        return contractExpirationDate;
    }

    public void setContractExpirationDate(String contractExpirationDate) {
        this.contractExpirationDate = contractExpirationDate;
    }

    public boolean isDefaultPassphraseEnabled() {
        return defaultPassphraseEnabled;
    }

    public void setDefaultPassphraseEnabled(boolean defaultPassphraseEnabled) {
        this.defaultPassphraseEnabled = defaultPassphraseEnabled;
    }

    public boolean isTrialEnabled() {
        return trialEnabled;
    }

    public void setTrialEnabled(boolean trialEnabled) {
        this.trialEnabled = trialEnabled;
    }

    public int getMultisign() {
        return multisign;
    }

    public void setMultisign(int multisign) {
        this.multisign = multisign;
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

}
