/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.objects;

/**
 *
 * @author ADMIN
 */
public class Entity {

    final public static String ENTITY_FEDERAL_IDP_ENTITY = "FEDERAL_IDP_ENTITY";
    final public static String ENTITY_CORE_CLOUD_ENTITY = "CORE_CLOUD_ENTITY";
    final public static String ENTITY_CORE_SMART_ID_ENTITY = "CORE_SMART_ID_ENTITY";
    final public static String ENTITY_CORE_MOBILE_ID_ENTITY = "CORE_MOBILE_ID_ENTITY";
    final public static String ENTITY_CORE_TRUSTED_HUB_ENTITY = "CORE_TRUSTED_HUB_ENTITY";
    final public static String ENTITY_REGISTRATION_ENTITY = "REGISTRATION_ENTITY";
    final public static String ENTITY_RELYLING_ENTITY = "RELYING_ENTITY";
    final public static String ENTITY_SMPP_ENTITY = "SMPP_ENTITY";
    final public static String ENTITY_SMTP_ENTITY = "SMTP_ENTITY";
    final public static String ENTITY_BACKOFFICE_ENTITY = "BACKOFFICE_ENTITY";
    final public static String ENTITY_SELFCARE_ENTITY = "SELFCARE_ENTITY";
    final public static String ENTITY_DMS_ENTITY = "DMS_ENTITY";
    final public static String ENTITY_INTERNAL_EP_ENTITY = "INTERNAL_EP_ENTITY";
    final public static String ENTITY_EXTERNAL_EP_ENTITY = "EXTERNAL_EP_ENTITY";
    final public static String ENTITY_MNO_ENTITY = "MNO_ENTITY";
    final public static String ENTITY_IDENTITY_ENTITY = "IDENTITY_ENTITY";
    final public static String ENTITY_VERIFICATION_ENTITY = "VERIFICATION_ENTITY";
    final public static String ENTITY_SYSTEM_CONFIG = "SYSTEM_CONFIG";

    private int entityID;
    private String name;
    private String uri;
    private String remark;
    private String remarkEn;

    private EntityProperties entityProperties;

    public int getEntityID() {
        return entityID;
    }

    public void setEntityID(int entityID) {
        this.entityID = entityID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemarkEn() {
        return remarkEn;
    }

    public void setRemarkEn(String remarkEn) {
        this.remarkEn = remarkEn;
    }

    public EntityProperties getEntityProperties() {
        return entityProperties;
    }

    public void setEntityProperties(EntityProperties entityProperties) {
        this.entityProperties = entityProperties;
    }

}
