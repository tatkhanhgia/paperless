/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.policy.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.List;
import vn.mobileid.id.general.objects.Attributes;
import vn.mobileid.id.paperless.objects.FrameSignatureProperties;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName("attributes")
public class PolicyFrameSignatureProperties extends Attributes {

    private List<Buffer> attributes;

    @JsonProperty("attributes")
    public List<Buffer> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Buffer> attributes) {
        this.attributes = attributes;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Buffer {

        private FrameSignatureProperties signatureProperties;

        @JsonProperty("metadata_signature")
        public FrameSignatureProperties getSignatureProperties() {
            return signatureProperties;
        }

        public void setSignatureProperties(FrameSignatureProperties signatureProperties) {
            this.signatureProperties = signatureProperties;
        }

    }
}
