package restful.sdk.API;

import restful.sdk.API.BaseCertificateInfo;
import restful.sdk.API.Types.AuthMode;

public class CertificateInfo extends BaseCertificateInfo {

    private String sharedMode;
    private String createdRP;
    private String[] authModes;
    private AuthMode authMode;
    //public String authMode ;
    private int SCAL;
    private String contractExpirationDate;
    private boolean defaultPassphraseEnabled;
    private boolean trialEnabled;

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

    public boolean getDefaultPassphraseEnabled() {
        return defaultPassphraseEnabled;
    }

    public void setDefaultPassphraseEnabled(boolean defaultPassphraseEnabled) {
        this.defaultPassphraseEnabled = defaultPassphraseEnabled;
    }

    public boolean getTrialEnabled() {
        return trialEnabled;
    }

    public void setTrialEnabled(boolean trialEnabled) {
        this.trialEnabled = trialEnabled;
    }
}
