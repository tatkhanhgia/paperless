/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.policy.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignatureProperties {
    private String witnessAgreementUUID;
    private String witnessCredentialID;
    private String businessAgreementUUID;
    private String businessCredentialID;
    private String passwordUUID;
    private String signingTimeFormat;
    private boolean checkText;
    private boolean checkMark;
    private float fontSize;
    private String businessPassword;

    @JsonProperty("businessPasswordUUID")
    public String getBusinessPassword() {
        return businessPassword;
    }

    public void setBusinessPassword(String businessPassword) {
        this.businessPassword = businessPassword;
    }

    @JsonProperty("witnessAgreementUUID")
    public String getWitnessAgreementUUID() {
        return witnessAgreementUUID;
    }

    public void setWitnessAgreementUUID(String agreementUUID) {
        this.witnessAgreementUUID = agreementUUID;
    }

    @JsonProperty("witnessCredentialID")
    public String getWitnessCredentialID() {
        return witnessCredentialID;
    }

    public void setWitnessCredentialID(String credentialID) {
        this.witnessCredentialID = credentialID;
    }

    @JsonProperty("witnessPasswordUUID")
    public String getPasswordUUID() {
        return passwordUUID;
    }

    public void setPasswordUUID(String passwordUUID) {
        this.passwordUUID = passwordUUID;
    }

    @JsonProperty("signingTimeFormat")
    public String getSigningTimeFormat() {
        return signingTimeFormat;
    }

    public void setSigningTimeFormat(String signingTimeFormat) {
        this.signingTimeFormat = signingTimeFormat;
    }

    @JsonProperty("checkTextEnable")
    public boolean isCheckText() {
        return checkText;
    }

    public void setCheckText(boolean checkText) {
        this.checkText = checkText;
    }

    @JsonProperty("checkMarkEnable")
    public boolean isCheckMark() {
        return checkMark;
    }

    public void setCheckMark(boolean checkMark) {
        this.checkMark = checkMark;
    }

    @JsonProperty("fontSize")
    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }  
    

    @JsonProperty("businessAgreementUUID")
    public String getBusinessAgreementUUID() {
        return businessAgreementUUID;
    }

    public void setBusinessAgreementUUID(String agreementBusinessUUID) {
        this.businessAgreementUUID = agreementBusinessUUID;
    }

    @JsonProperty("businessCredentialID")
    public String getBusinessCredentialID() {
        return businessCredentialID;
    }

    public void setBusinessCredentialID(String credentialBusinessID) {
        this.businessCredentialID = credentialBusinessID;
    }
    
    
    
}
