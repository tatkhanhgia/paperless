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
 * @author ADMIN
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdentityProviderProperties extends Attributes implements Serializable {

    private Endpoint endpoint;
    private List<Attribute> ocrEngine;
    private List<Attribute> identityEngine;

    @JsonProperty("ocr_engine")
    public List<Attribute> getOcrEngine() {
        return ocrEngine;
    }

    public void setOcrEngine(List<Attribute> ocrEngine) {
        this.ocrEngine = ocrEngine;
    }

    @JsonProperty("endpoint")
    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public String getOCRAttribute(String attributeName) {
        for (Attribute attribute : ocrEngine) {
            if (attribute.getName().compareTo(attributeName) == 0) {
                return attribute.getValue();
            }
        }
        return null;
    }

    @JsonProperty("identity_engine")
    public List<Attribute> getIdentityEngine() {
        return identityEngine;
    }

    public void setIdentityEngine(List<Attribute> identityEngine) {
        this.identityEngine = identityEngine;
    }

    public String getIdentityAttribute(String attributeName) {
        for (Attribute attribute : identityEngine) {
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
