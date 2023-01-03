/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author VUDP
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DMSProperties extends Attributes implements Serializable {

    final public static String DMS_TYPE_JACKRABBIT = "JACKRABBIT";
    final public static String DMS_TYPE_JACKRABBIT_MOBILEID = "JACKRABBIT_MOBILEID";
    final public static String DMS_TYPE_EFY_FMS = "EFY_FMS";
    final public static String DMS_TYPE_LOCAL = "LOCAL";

    public static final String TYPE = "TYPE";
    public static final String URI = "URI";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String MAXSESSION = "MAXSESSION";
    public static final String MAXFILEINFOLDER = "MAXFILEINFOLDER";
    public static final String WORKSPACENAME = "WORKSPACENAME";
    public static final String FOLDERPREFIX = "FOLDERPREFIX";

    // EFY
    public static final String OWNERCODE = "OWNER_CODE";
    public static final String APPLICATIONCODE = "APPLICATION_CODE";
    public static final String DOWNLOADFUNCID = "DOWNLOAD_FUNC_ID";
    public static final String UPLOADFUNCID = "UPLOAD_FUNC_ID";

    // for RP
    public static final String RESTRICTED_URI = "RESTRICTED_URI";
    public static final String RESTRICTED_USERNAME = "RESTRICTED_USERNAME";
    public static final String RESTRICTED_PASSWORD = "RESTRICTED_PASSWORD";
    public static final String DOWNLOAD_URI = "DOWNLOAD_URI";

    private Endpoint endpoint;
    private List<Attribute> attributes;

    @JsonProperty("attributes")
    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @JsonProperty("endpoint")
    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public String getAttribute(String attributeName) {
        for (Attribute attribute : attributes) {
            if (attribute.getName().compareTo(attributeName) == 0) {
                return attribute.getValue();
            }
        }
        return null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Attribute implements Serializable {

        private String name;
        private String value;
        private String mimeType;
        private String remarkEn;
        private String remark;

        @JsonProperty("remarkEn")
        public String getRemarkEn() {
            return remarkEn;
        }

        public void setRemarkEn(String remarkEn) {
            this.remarkEn = remarkEn;
        }

        @JsonProperty("remark")
        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("value")
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @JsonProperty("mimeType")
        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }
    }
}
