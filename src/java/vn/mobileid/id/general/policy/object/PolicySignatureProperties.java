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

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName("attributes")
public class PolicySignatureProperties extends Attributes{
    private List<SignatureProperties> attributes;

    @JsonProperty("attributes")
    public List<SignatureProperties> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<SignatureProperties> attributes) {
        this.attributes = attributes;
    }        
}
