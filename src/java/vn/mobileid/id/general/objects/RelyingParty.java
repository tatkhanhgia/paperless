/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.objects;

import vn.mobileid.id.license.LicenseServerData;

/**
 *
 * @author ADMIN
 */
public class RelyingParty {

    final public static int ENTITY_ID = 7;
    
    final public static int ATTR_CREDENTIAL_TOKEN1 = 27;
    final public static int ATTR_EMAIL_TEMPLATE_OWNER_PASSWORD_NOTIFICATION = 29;
    final public static int ATTR_EMAIL_TEMPLATE_IDENTITY_VERIFICATION = 43;
    final public static int ATTR_MOBILE_TEMPLATE_IDENTITY_VERIFICATION = 44;

    final public static int ATTR_EMAIL_TEMPLATE_VERIFICATION_CHALLENGE = 50;
    final public static int ATTR_MOBILE_TEMPLATE_VERIFICATION_CHALLENGE = 51;
    final public static int ATTR_EMAIL_TEMPLATE_VERIFICATION_OTP = 52;
    final public static int ATTR_MOBILE_TEMPLATE_VERIFICATION_OTP = 53;
    
    final public static int ATTR_IAM_PROVIDER = 59;
    
    final public static int ATTR_RELYING_PARTY_LICENSE = 70;

    private int id;
    private String name;
    private boolean awsEnabled;
    private AWSV4PropertiesJSNObject awsProperties;
    private boolean identityEnabled;
    private IPRestrictionList identityIPRestriction;
    private IPRestrictionList verificationIPRestriction;
//    private IdentityPropertiesJSNObject identityProperties;
//    private VerificationPropertiesJSNObject verificationProperties;

    private boolean smtpEnabled;
    private SMTPProperties smtpProperties;
    private boolean smsgatewayEnabled;
    private SMSGWProperties smsgwProperties;
    private boolean dmsEnabled;
    private DMSProperties dmsProperties;

    private int assignedBy;
    
    private LicenseServerData licenseServerData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAwsEnabled() {
        return awsEnabled;
    }

    public void setAwsEnabled(boolean awsEnabled) {
        this.awsEnabled = awsEnabled;
    }

    public AWSV4PropertiesJSNObject getAwsProperties() {
        return awsProperties;
    }

    public void setAwsProperties(AWSV4PropertiesJSNObject awsProperties) {
        this.awsProperties = awsProperties;
    }

    public boolean isIdentityEnabled() {
        return identityEnabled;
    }

    public void setIdentityEnabled(boolean identityEnabled) {
        this.identityEnabled = identityEnabled;
    }

    public IPRestrictionList getIdentityIPRestriction() {
        return identityIPRestriction;
    }

    public void setIdentityIPRestriction(IPRestrictionList identityIPRestriction) {
        this.identityIPRestriction = identityIPRestriction;
    }

//    public IdentityPropertiesJSNObject getIdentityProperties() {
//        return identityProperties;
//    }
//
//    public void setIdentityProperties(IdentityPropertiesJSNObject identityProperties) {
//        this.identityProperties = identityProperties;
//    }

    public boolean isSmtpEnabled() {
        return smtpEnabled;
    }

    public void setSmtpEnabled(boolean smtpEnabled) {
        this.smtpEnabled = smtpEnabled;
    }

    public SMTPProperties getSmtpProperties() {
        return smtpProperties;
    }

    public void setSmtpProperties(SMTPProperties smtpProperties) {
        this.smtpProperties = smtpProperties;
    }

    public boolean isSmsgatewayEnabled() {
        return smsgatewayEnabled;
    }

    public void setSmsgatewayEnabled(boolean smsgatewayEnabled) {
        this.smsgatewayEnabled = smsgatewayEnabled;
    }

    public SMSGWProperties getSmsgwProperties() {
        return smsgwProperties;
    }

    public void setSmsgwProperties(SMSGWProperties smsgwProperties) {
        this.smsgwProperties = smsgwProperties;
    }

    public boolean isDmsEnabled() {
        return dmsEnabled;
    }

    public void setDmsEnabled(boolean dmsEnabled) {
        this.dmsEnabled = dmsEnabled;
    }

    public DMSProperties getDmsProperties() {
        return dmsProperties;
    }

    public void setDmsProperties(DMSProperties dmsProperties) {
        this.dmsProperties = dmsProperties;
    }

    public int getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(int assignedBy) {
        this.assignedBy = assignedBy;
    }

//    public VerificationPropertiesJSNObject getVerificationProperties() {
//        return verificationProperties;
//    }
//
//    public void setVerificationProperties(VerificationPropertiesJSNObject verificationProperties) {
//        this.verificationProperties = verificationProperties;
//    }

    public IPRestrictionList getVerificationIPRestriction() {
        return verificationIPRestriction;
    }

    public void setVerificationIPRestriction(IPRestrictionList verificationIPRestriction) {
        this.verificationIPRestriction = verificationIPRestriction;
    }

    public LicenseServerData getLicenseServerData() {
        return licenseServerData;
    }

    public void setLicenseServerData(LicenseServerData licenseServerData) {
        this.licenseServerData = licenseServerData;
    }
    
}
