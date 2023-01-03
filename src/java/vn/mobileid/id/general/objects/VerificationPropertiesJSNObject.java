/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author ADMIN
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationPropertiesJSNObject {

    private int challengeLength;
    private int challengeExpireTimeConfig;
    private boolean preAuthenticatedRequired;
    private String challengeEncoding;
    private int otpRemainingCounter;
    private int otpUnblockDuration;
    private int numberOfOTPAllowed;
    private int otpAllowedDuration;
    private int otpLength;
    private int otpExpireTimeConfig;
    private String idXCertifiate;
    private int accessTokenExpireTimeConfig;
    private boolean ltvEnabled;
    /*
        0: no adding timestamp
        1: always adding timestamp
        2: if all signature have no timestanp, then add timestamp
     */
    private int addTimestampMode;
    private String tsaUrl;
    private String tsaUsername;
    private String tsaPassword;
    private boolean iamEnabled;
    private int acceptableCrlDuration; //second

    public VerificationPropertiesJSNObject() {
        accessTokenExpireTimeConfig = 1800; // default
    }

    @JsonProperty("challengeLength")
    public int getChallengeLength() {
        return challengeLength;
    }

    public void setChallengeLength(int challengeLength) {
        this.challengeLength = challengeLength;
    }

    @JsonProperty("challengeExpireTimeConfig")
    public int getChallengeExpireTimeConfig() {
        return challengeExpireTimeConfig;
    }

    public void setChallengeExpireTimeConfig(int challengeExpireTimeConfig) {
        this.challengeExpireTimeConfig = challengeExpireTimeConfig;
    }

    @JsonProperty("preAuthenticatedRequired")
    public boolean isPreAuthenticatedRequired() {
        return preAuthenticatedRequired;
    }

    public void setPreAuthenticatedRequired(boolean preAuthenticatedRequired) {
        this.preAuthenticatedRequired = preAuthenticatedRequired;
    }

    @JsonProperty("challengeEncoding")
    public String getChallengeEncoding() {
        return challengeEncoding;
    }

    public void setChallengeEncoding(String challengeEncoding) {
        this.challengeEncoding = challengeEncoding;
    }

    @JsonProperty("otpRemainingCounter")
    public int getOtpRemainingCounter() {
        return otpRemainingCounter;
    }

    public void setOtpRemainingCounter(int otpRemainingCounter) {
        this.otpRemainingCounter = otpRemainingCounter;
    }

    @JsonProperty("otpUnblockDuration")
    public int getOtpUnblockDuration() {
        return otpUnblockDuration;
    }

    public void setOtpUnblockDuration(int otpUnblockDuration) {
        this.otpUnblockDuration = otpUnblockDuration;
    }

    @JsonProperty("numberOfOTPAllowed")
    public int getNumberOfOTPAllowed() {
        return numberOfOTPAllowed;
    }

    public void setNumberOfOTPAllowed(int numberOfOTPAllowed) {
        this.numberOfOTPAllowed = numberOfOTPAllowed;
    }

    @JsonProperty("otpAllowedDuration")
    public int getOtpAllowedDuration() {
        return otpAllowedDuration;
    }

    public void setOtpAllowedDuration(int otpAllowedDuration) {
        this.otpAllowedDuration = otpAllowedDuration;
    }

    @JsonProperty("otpLength")
    public int getOtpLength() {
        return otpLength;
    }

    public void setOtpLength(int otpLength) {
        this.otpLength = otpLength;
    }

    @JsonProperty("otpExpireTimeConfig")
    public int getOtpExpireTimeConfig() {
        return otpExpireTimeConfig;
    }

    public void setOtpExpireTimeConfig(int otpExpireTimeConfig) {
        this.otpExpireTimeConfig = otpExpireTimeConfig;
    }

    @JsonProperty("idXCertifiate")
    public String getIdXCertifiate() {
        return idXCertifiate;
    }

    public void setIdXCertifiate(String idXCertifiate) {
        this.idXCertifiate = idXCertifiate;
    }

    @JsonProperty("accessTokenExpireTimeConfig")
    public int getAccessTokenExpireTimeConfig() {
        return accessTokenExpireTimeConfig;
    }

    public void setAccessTokenExpireTimeConfig(int accessTokenExpireTimeConfig) {
        this.accessTokenExpireTimeConfig = accessTokenExpireTimeConfig;
    }

    @JsonProperty("ltvEnabled")
    public boolean isLtvEnabled() {
        return ltvEnabled;
    }

    public void setLtvEnabled(boolean ltvEnabled) {
        this.ltvEnabled = ltvEnabled;
    }

    @JsonProperty("addTimestampMode")
    public int getAddTimestampMode() {
        return addTimestampMode;
    }

    public void setAddTimestampMode(int addTimestampMode) {
        this.addTimestampMode = addTimestampMode;
    }

    @JsonProperty("tsaUrl")
    public String getTsaUrl() {
        return tsaUrl;
    }

    public void setTsaUrl(String tsaUrl) {
        this.tsaUrl = tsaUrl;
    }

    @JsonProperty("tsaUsername")
    public String getTsaUsername() {
        return tsaUsername;
    }

    public void setTsaUsername(String tsaUsername) {
        this.tsaUsername = tsaUsername;
    }

    @JsonProperty("tsaPassword")
    public String getTsaPassword() {
        return tsaPassword;
    }

    public void setTsaPassword(String tsaPassword) {
        this.tsaPassword = tsaPassword;
    }

    @JsonProperty("iamEnabled")
    public boolean isIamEnabled() {
        return iamEnabled;
    }

    public void setIamEnabled(boolean iamEnabled) {
        this.iamEnabled = iamEnabled;
    }

    @JsonProperty("acceptableCrlDuration")
    public int getAcceptableCrlDuration() {
        return acceptableCrlDuration;
    }

    public void setAcceptableCrlDuration(int acceptableCrlDuration) {
        this.acceptableCrlDuration = acceptableCrlDuration;
    }
}
