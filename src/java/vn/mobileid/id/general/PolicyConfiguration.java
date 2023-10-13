/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.policy.object.PolicyConstant;
import vn.mobileid.id.general.policy.object.PolicyFrameSignatureProperties;
import vn.mobileid.id.general.policy.object.PolicyResponse;
import vn.mobileid.id.general.policy.object.PolicySignatureProperties;
import vn.mobileid.id.general.policy.object.PolicySystemConfiguration;
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

    public static PolicyConfiguration getInstant()  {
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

            if (signatureProperties == null || esigncloudTemplate == null
                    || elaborContractTemplate == null || systemConfig == null
                    || templateUserActivity == null) {
                LogHandler.fatal(
                        PolicyConfiguration.class,
                        "Init transaction",
                        "Cannot Init Policy !!!");
                
            }
        } catch (Exception ex) {
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
            object = new ObjectMapper()
                    .enable(DeserializationFeature.UNWRAP_ROOT_VALUE)                    
                    .readValue(response.getValue(), type);
            return object;
        } catch (Exception ex) {
            ex.printStackTrace();
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
    
    public PolicyUserActivityConfiguration getTemplateUserActivity(){
        return templateUserActivity;
    }
    
    
    public static void main(String[] args) throws Exception{
        
        PolicySystemConfiguration systemConfig = PolicyConfiguration
                .getInstant()
                .systemConfig;
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
    }
    
}
