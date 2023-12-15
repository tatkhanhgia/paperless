/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.policy.object.PolicyConstant;
import vn.mobileid.id.general.policy.object.PolicyFrameSignatureProperties;
import vn.mobileid.id.general.policy.object.PolicyHostNameOfForgotPasswordURL;
import vn.mobileid.id.general.policy.object.PolicyPasswordExpired;
import vn.mobileid.id.general.policy.object.PolicyPasswordRule;
import vn.mobileid.id.general.policy.object.PolicyQR_ExpiredTime;
import vn.mobileid.id.general.policy.object.PolicyResponse;
import vn.mobileid.id.general.policy.object.PolicySignatureProperties;
import vn.mobileid.id.general.policy.object.PolicySystemConfiguration;
import vn.mobileid.id.general.policy.object.PolicyURLofQR;
import vn.mobileid.id.general.policy.object.PolicyUserActivityConfiguration;
import vn.mobileid.id.paperless.PaperlessConstant;

/**
 *
 * @author GiaTK
 */
public class PolicyConfiguration {

    private static PolicyConfiguration instance;

    private PolicySignatureProperties signatureProperties;
    private PolicyFrameSignatureProperties elaborContractTemplate;
    private PolicyFrameSignatureProperties esigncloudTemplate;
    private PolicySystemConfiguration systemConfig;
    private PolicyUserActivityConfiguration templateUserActivity;
    private PolicyPasswordRule passwordRule;
    private PolicyPasswordExpired passwordExpired;
    private PolicyURLofQR URLofQR;
    private PolicyHostNameOfForgotPasswordURL HostURLofForgotPassword;
    private PolicyQR_ExpiredTime qR_ExpiredTime;

    public static PolicyConfiguration getInstant() {
        if (instance == null) {
            instance = new PolicyConfiguration();
        }
        return instance;
    }

    private PolicyConfiguration() {
        try {
            Database db = new DatabaseImpl();
            DatabaseResponse response = db.getPolicyAttribute(
                    PolicyConstant.SIGNATUREPROPERTIES_ELABORCONTRACT_TEMPLATE);
            if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                LogHandler.fatal(
                        PolicyConfiguration.class,
                        "Init transaction",
                        "Cannot Init Policy !!!");
            }
            elaborContractTemplate = (PolicyFrameSignatureProperties) convertPolicyResponseToObject(
                    (PolicyResponse) response.getObject(),
                    PolicyFrameSignatureProperties.class);

            //<editor-fold defaultstate="collapsed" desc="Get ESignCloud Template">
            response = db.getPolicyAttribute(
                    PolicyConstant.SIGNATUREPROPERTIES_ESIGNCLOUD_TEMPLATE);
            if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                LogHandler.fatal(
                        PolicyConfiguration.class,
                        "Init transaction",
                        "Cannot Init Policy !!!");
            }
            esigncloudTemplate = (PolicyFrameSignatureProperties) convertPolicyResponseToObject(
                    (PolicyResponse) response.getObject(),
                    PolicyFrameSignatureProperties.class);
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get Signature Properties">
            response = db.getPolicyAttribute(
                    PolicyConstant.SIGNATUREPROPERTIES);
            if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                LogHandler.fatal(
                        PolicyConfiguration.class,
                        "Init transaction",
                        "Cannot Init Policy !!!");
            }
            signatureProperties = (PolicySignatureProperties) convertPolicyResponseToObject(
                    (PolicyResponse) response.getObject(),
                    PolicySignatureProperties.class);
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get System Policy">
            response = db.getPolicyAttribute(
                    PolicyConstant.SYSTEMPOLICY);
            if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                LogHandler.fatal(
                        PolicyConfiguration.class,
                        "Init transaction",
                        "Cannot Init Policy !!!");
            }

            systemConfig = (PolicySystemConfiguration) convertPolicyResponseToObject(
                    (PolicyResponse) response.getObject(),
                    PolicySystemConfiguration.class);
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get Policy of Template User Activity">
            response = db.getPolicyAttribute(
                    PolicyConstant.TEMPLATE_USER_ACTIVITY);
            if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                LogHandler.fatal(
                        PolicyConfiguration.class,
                        "Init transaction",
                        "Cannot Init Policy !!!");
            }
            templateUserActivity = (PolicyUserActivityConfiguration) convertPolicyResponseToObject(
                    (PolicyResponse) response.getObject(),
                    PolicyUserActivityConfiguration.class);
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get Complexity of Password">
            response = db.getPolicyAttribute(
                    PolicyConstant.COMPLEXITY_OF_PASSWORD);
            if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                LogHandler.fatal(
                        PolicyConfiguration.class,
                        "Init transaction",
                        "Cannot Init Policy !!!");
            }

            passwordRule = (PolicyPasswordRule) convertPolicyResponseToObject(
                    (PolicyResponse) response.getObject(),
                    PolicyPasswordRule.class);
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get Password Expired">
            response = db.getPolicyAttribute(
                    PolicyConstant.EXPIRED_TIME_OF_PASSWORD);
            if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                LogHandler.fatal(
                        PolicyConfiguration.class,
                        "Init transaction",
                        "Cannot Init Policy !!!");
            }
            passwordExpired = (PolicyPasswordExpired) convertPolicyResponseToObject(
                    (PolicyResponse) response.getObject(),
                    PolicyPasswordExpired.class);
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get URL of QR">
            response = db.getPolicyAttribute(
                    PolicyConstant.URL_OF_QR);
            if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                LogHandler.fatal(
                        PolicyConfiguration.class,
                        "Init transaction",
                        "Cannot Init Policy !!!");
            }
            URLofQR = (PolicyURLofQR) convertPolicyResponseToObject(
                    (PolicyResponse) response.getObject(),
                    PolicyURLofQR.class);
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get URL of QR">
            response = db.getPolicyAttribute(
                    PolicyConstant.URL_OF_QR);
            if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                LogHandler.fatal(
                        PolicyConfiguration.class,
                        "Init transaction",
                        "Cannot Init Policy !!!");
            }
            URLofQR = (PolicyURLofQR) convertPolicyResponseToObject(
                    (PolicyResponse) response.getObject(),
                    PolicyURLofQR.class);
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get Hostname of Forgot Password">
            response = db.getPolicyAttribute(
                    PolicyConstant.HOSTNAME_URL_FORGOT_PASSWORD);
            if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                LogHandler.fatal(
                        PolicyConfiguration.class,
                        "Init transaction",
                        "Cannot Init Policy !!!");
            }
            HostURLofForgotPassword = (PolicyHostNameOfForgotPasswordURL) convertPolicyResponseToObject(
                    (PolicyResponse) response.getObject(),
                    PolicyHostNameOfForgotPasswordURL.class);
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Get QR Expired Time">
            response = db.getPolicyAttribute(
                    PolicyConstant.QR_EXPIRED_TIME);
            if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                LogHandler.fatal(
                        PolicyConfiguration.class,
                        "Init transaction",
                        "Cannot Init Policy !!!");
            }
            qR_ExpiredTime = (PolicyQR_ExpiredTime) convertPolicyResponseToObject(
                    (PolicyResponse) response.getObject(),
                    PolicyQR_ExpiredTime.class);
            //</editor-fold>

            if (signatureProperties == null || esigncloudTemplate == null
                    || elaborContractTemplate == null || systemConfig == null
                    || templateUserActivity == null || passwordRule == null || passwordExpired == null
                    || URLofQR == null || HostURLofForgotPassword == null
                    || qR_ExpiredTime == null) {
                LogHandler.fatal(
                        PolicyConfiguration.class,
                        "Init transaction",
                        "Cannot Init Policy !!!");

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LogHandler.fatal(
                    PolicyConfiguration.class,
                    "Init transaction",
                    "Cannot Init Policy !!!");

        }
    }

    /**
     * Convert value in PolicyResposne to Object
     *
     * @param response
     * @param type
     * @return
     */
    private Object convertPolicyResponseToObject(
            PolicyResponse response,
            Class type) {
        Object object = null;
        try {
            try {
                object = new ObjectMapper()
                        .enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
                        .readValue(response.getValue(), type);
                return object;
            } catch (Exception e) {
                if (response.getValue().contains("{") && response.getValue().contains("}")) {
                    object = new ObjectMapper()
                            .readValue(response.getValue(), type);
                    return object;
                } else {
                    object = type.newInstance();
                    Field[] fields = type.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        if (field.getType() == Integer.TYPE) {
                            field.setInt(object, Integer.parseInt(response.getValue()));
                        }
                        if (field.getType() == String.class) {
                            field.set(object, response.getValue());
                        }
                        if (field.getType() == Long.TYPE){
                            field.setLong(object, Long.parseLong(response.getValue()));
                        }
                    }
                    return object;
                }
            }
        } catch (Exception ex) {
            return null;
        }
    }

    public PolicySignatureProperties getSignatureProperties() {
        return signatureProperties;
    }

    public PolicyFrameSignatureProperties getElaborContractTemplate() {
        return elaborContractTemplate;
    }

    public PolicyFrameSignatureProperties getEsigncloudTemplate() {
        return esigncloudTemplate;
    }

    public PolicySystemConfiguration getSystemConfig() {
        return systemConfig;
    }

    public PolicyUserActivityConfiguration getTemplateUserActivity() {
        return templateUserActivity;
    }

    public PolicyPasswordRule getPasswordRule() {
        return passwordRule;
    }

    public PolicyPasswordExpired getPasswordExpired() {
        return passwordExpired;
    }

    public PolicyURLofQR getURLofQR() {
        return URLofQR;
    }

    public PolicyHostNameOfForgotPasswordURL getHostURLofForgotPassword() {
        return HostURLofForgotPassword;
    }

    public PolicyQR_ExpiredTime getQR_ExpiredTime() {
        return qR_ExpiredTime;
    }

    public static void main(String[] args) throws Exception {

        PolicySystemConfiguration systemConfig = PolicyConfiguration
                .getInstant().systemConfig;
        System.out.println(systemConfig.getAttributes().get(0).getDefault_row_count());
        System.out.println(systemConfig.getRemark());

        System.out.println(PolicyConfiguration
                .getInstant()
                .getElaborContractTemplate()
                .getAttributes().get(0)
                .getSignatureProperties()
                .getReason());
        System.out.println(PolicyConfiguration
                .getInstant()
                .getSignatureProperties()
                .getAttributes().get(0)
                .getWitnessAgreementUUID());

        System.out.println(PolicyConfiguration
                .getInstant()
                .getTemplateUserActivity().getAttributes().get(0).getCreateWorkflowActivity());

        System.out.println(PolicyConfiguration
                .getInstant()
                .getPasswordRule().getMinLength());

        System.out.println(PolicyConfiguration
                .getInstant()
                .getPasswordRule().isMustContainLowerCase());

        System.out.println(PolicyConfiguration
                .getInstant()
                .getPasswordRule().isOnlyNumeric());

        System.out.println("Minute:" + PolicyConfiguration.getInstant().getPasswordExpired().getMinute_lock());

        System.out.println("URL:" + PolicyConfiguration.getInstant().getURLofQR().getUrl());

        System.out.println("Hostname:" + PolicyConfiguration.getInstant().getHostURLofForgotPassword().getUrl());
    
        System.out.println("QR Expired Time:"+ PolicyConfiguration.getInstant().getQR_ExpiredTime().getQr_expired_time());
        
        System.out.println("AccessToken Expired:"+PolicyConfiguration
            .getInstant()
            .getSystemConfig()
            .getAttributes().get(0)
            .getTokenConfig()
            .getAccess_token_expired_in());
    }

}
