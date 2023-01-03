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
public class IdentityPropertiesJSNObject {

    private int accessTokenExpireTimeConfig;
    private String idXCertifiate;
    private int otpLength;
    private int otpRemainingCounter;
    private int otpUnblockDuration;
    private int numberOfOTPAllowed;
    private int otpAllowedDuration;
    private int jwtDuration;
    private int otpExpireTimeConfig;
    private String otpPolicy;

    private float livenessThresold;
    private float matchingThresold;
    private int fingerThresold;

    private float livenessThresoldForiOS;
    private float livenessThresoldForAndroid;
    private float livenessThresoldForWin;

    private float matchingThresoldForiOS;
    private float matchingThresoldForAndroid;
    private float matchingThresoldForWin;

    private long frameCaptureTimeOutForiOS;
    private long noseTimeOutForiOS;

    private long frameCaptureTimeOutForAndroid;
    private long noseTimeOutForAndroid;

    private long frameCaptureTimeOutForWin;
    private long noseTimeOutForWin;

    private boolean sendOwnerPasswordToEndUserEnabled;
    private boolean randomOwnerPassword;
    private int defaultOwnerPasswordLength;
    private String defaultOwnerPassword;
    private boolean defaultIdentityProviderEnabled;
    private String defaultIdentityProviderName;
    private String defaultMasterListName;
    private boolean skipCheckLivenessWhileLiveMatching;
    
    private int numOfNoseBox;

    public IdentityPropertiesJSNObject() {
        accessTokenExpireTimeConfig = 1800; // default
    }

    @JsonProperty("accessTokenExpireTimeConfig")
    public int getAccessTokenExpireTimeConfig() {
        return accessTokenExpireTimeConfig;
    }

    public void setAccessTokenExpireTimeConfig(int accessTokenExpireTimeConfig) {
        this.accessTokenExpireTimeConfig = accessTokenExpireTimeConfig;
    }

    @JsonProperty("idXCertifiate")
    public String getIdXCertifiate() {
        return idXCertifiate;
    }

    public void setIdXCertifiate(String idXCertifiate) {
        this.idXCertifiate = idXCertifiate;
    }
//
//    @JsonProperty("otpExpireTimeConfig")
//    public int getOtpDuration() {
//        return otpDuration;
//    }
//
//    public void setOtpDuration(int otpDuration) {
//        this.otpDuration = otpDuration;
//    }

    @JsonProperty("otpLength")
    public int getOtpLength() {
        return otpLength;
    }

    public void setOtpLength(int otpLength) {
        this.otpLength = otpLength;
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

    @JsonProperty("jwtDuration")
    public int getJwtDuration() {
        return jwtDuration;
    }

    public void setJwtDuration(int jwtDuration) {
        this.jwtDuration = jwtDuration;
    }

    @JsonProperty("otpExpireTimeConfig")
    public int getOtpExpireTimeConfig() {
        return otpExpireTimeConfig;
    }

    public void setOtpExpireTimeConfig(int otpExpireTimeConfig) {
        this.otpExpireTimeConfig = otpExpireTimeConfig;
    }

    @JsonProperty("otpPolicy")
    public String getOtpPolicy() {
        return otpPolicy;
    }

    public void setOtpPolicy(String otpPolicy) {
        this.otpPolicy = otpPolicy;
    }

    @JsonProperty("livenessThresold")
    public float getLivenessThresold() {
        return livenessThresold;
    }

    public void setLivenessThresold(float livenessThresold) {
        this.livenessThresold = livenessThresold;
    }

    @JsonProperty("matchingThresold")
    public float getMatchingThresold() {
        return matchingThresold;
    }

    public void setMatchingThresold(float matchingThresold) {
        this.matchingThresold = matchingThresold;
    }

    @JsonProperty("randomOwnerPassword")
    public boolean isRandomOwnerPassword() {
        return randomOwnerPassword;
    }

    public void setRandomOwnerPassword(boolean randomOwnerPassword) {
        this.randomOwnerPassword = randomOwnerPassword;
    }

    @JsonProperty("defaultOwnerPasswordLength")
    public int getDefaultOwnerPasswordLength() {
        return defaultOwnerPasswordLength;
    }

    public void setDefaultOwnerPasswordLength(int defaultOwnerPasswordLength) {
        this.defaultOwnerPasswordLength = defaultOwnerPasswordLength;
    }

    @JsonProperty("defaultOwnerPassword")
    public String getDefaultOwnerPassword() {
        return defaultOwnerPassword;
    }

    public void setDefaultOwnerPassword(String defaultOwnerPassword) {
        this.defaultOwnerPassword = defaultOwnerPassword;
    }

    @JsonProperty("sendOwnerPasswordToEndUserEnabled")
    public boolean isSendOwnerPasswordToEndUserEnabled() {
        return sendOwnerPasswordToEndUserEnabled;
    }

    public void setSendOwnerPasswordToEndUserEnabled(boolean sendOwnerPasswordToEndUserEnabled) {
        this.sendOwnerPasswordToEndUserEnabled = sendOwnerPasswordToEndUserEnabled;
    }

    @JsonProperty("defaultIdentityProviderEnabled")
    public boolean isDefaultIdentityProviderEnabled() {
        return defaultIdentityProviderEnabled;
    }

    public void setDefaultIdentityProviderEnabled(boolean defaultIdentityProviderEnabled) {
        this.defaultIdentityProviderEnabled = defaultIdentityProviderEnabled;
    }

    @JsonProperty("defaultIdentityProviderName")
    public String getDefaultIdentityProviderName() {
        return defaultIdentityProviderName;
    }

    public void setDefaultIdentityProviderName(String defaultIdentityProviderName) {
        this.defaultIdentityProviderName = defaultIdentityProviderName;
    }

    @JsonProperty("defaultMasterListName")
    public String getDefaultMasterListName() {
        return defaultMasterListName;
    }

    public void setDefaultMasterListName(String defaultMasterListName) {
        this.defaultMasterListName = defaultMasterListName;
    }

    @JsonProperty("fingerThresold")
    public int getFingerThresold() {
        return fingerThresold;
    }

    public void setFingerThresold(int fingerThresold) {
        this.fingerThresold = fingerThresold;
    }

    @JsonProperty("livenessThresoldForiOS")
    public float getLivenessThresoldForiOS() {
        return livenessThresoldForiOS;
    }

    public void setLivenessThresoldForiOS(float livenessThresoldForiOS) {
        this.livenessThresoldForiOS = livenessThresoldForiOS;
    }

    @JsonProperty("livenessThresoldForAndroid")
    public float getLivenessThresoldForAndroid() {
        return livenessThresoldForAndroid;
    }

    public void setLivenessThresoldForAndroid(float livenessThresoldForAndroid) {
        this.livenessThresoldForAndroid = livenessThresoldForAndroid;
    }

    @JsonProperty("matchingThresoldForiOS")
    public float getMatchingThresoldForiOS() {
        return matchingThresoldForiOS;
    }

    public void setMatchingThresoldForiOS(float matchingThresoldForiOS) {
        this.matchingThresoldForiOS = matchingThresoldForiOS;
    }

    @JsonProperty("matchingThresoldForAndroid")
    public float getMatchingThresoldForAndroid() {
        return matchingThresoldForAndroid;
    }

    public void setMatchingThresoldForAndroid(float matchingThresoldForAndroid) {
        this.matchingThresoldForAndroid = matchingThresoldForAndroid;
    }

    @JsonProperty("livenessThresoldForWin")
    public float getLivenessThresoldForWin() {
        return livenessThresoldForWin;
    }

    public void setLivenessThresoldForWin(float livenessThresoldForWin) {
        this.livenessThresoldForWin = livenessThresoldForWin;
    }

    @JsonProperty("matchingThresoldForWin")
    public float getMatchingThresoldForWin() {
        return matchingThresoldForWin;
    }

    public void setMatchingThresoldForWin(float matchingThresoldForWin) {
        this.matchingThresoldForWin = matchingThresoldForWin;
    }

    @JsonProperty("frameCaptureTimeOutForiOS")
    public long getFrameCaptureTimeOutForiOS() {
        return frameCaptureTimeOutForiOS;
    }

    public void setFrameCaptureTimeOutForiOS(long frameCaptureTimeOutForiOS) {
        this.frameCaptureTimeOutForiOS = frameCaptureTimeOutForiOS;
    }

    @JsonProperty("noseTimeOutForiOS")
    public long getNoseTimeOutForiOS() {
        return noseTimeOutForiOS;
    }

    public void setNoseTimeOutForiOS(long noseTimeOutForiOS) {
        this.noseTimeOutForiOS = noseTimeOutForiOS;
    }

    @JsonProperty("frameCaptureTimeOutForAndroid")
    public long getFrameCaptureTimeOutForAndroid() {
        return frameCaptureTimeOutForAndroid;
    }

    public void setFrameCaptureTimeOutForAndroid(long frameCaptureTimeOutForAndroid) {
        this.frameCaptureTimeOutForAndroid = frameCaptureTimeOutForAndroid;
    }

    @JsonProperty("noseTimeOutForAndroid")
    public long getNoseTimeOutForAndroid() {
        return noseTimeOutForAndroid;
    }

    public void setNoseTimeOutForAndroid(long noseTimeOutForAndroid) {
        this.noseTimeOutForAndroid = noseTimeOutForAndroid;
    }

    @JsonProperty("frameCaptureTimeOutForWin")
    public long getFrameCaptureTimeOutForWin() {
        return frameCaptureTimeOutForWin;
    }

    public void setFrameCaptureTimeOutForWin(long frameCaptureTimeOutForWin) {
        this.frameCaptureTimeOutForWin = frameCaptureTimeOutForWin;
    }

    @JsonProperty("noseTimeOutForWin")
    public long getNoseTimeOutForWin() {
        return noseTimeOutForWin;
    }

    public void setNoseTimeOutForWin(long noseTimeOutForWin) {
        this.noseTimeOutForWin = noseTimeOutForWin;
    }

    @JsonProperty("numOfNoseBox")
    public int getNumOfNoseBox() {
        return numOfNoseBox;
    }

    public void setNumOfNoseBox(int numOfNoseBox) {
        this.numOfNoseBox = numOfNoseBox;
    }

    @JsonProperty("skipCheckLivenessWhileLiveMatching")
    public boolean isSkipCheckLivenessWhileLiveMatching() {
        return skipCheckLivenessWhileLiveMatching;
    }

    public void setSkipCheckLivenessWhileLiveMatching(boolean skipCheckLivenessWhileLiveMatching) {
        this.skipCheckLivenessWhileLiveMatching = skipCheckLivenessWhileLiveMatching;
    }
}
