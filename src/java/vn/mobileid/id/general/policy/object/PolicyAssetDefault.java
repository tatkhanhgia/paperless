/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.policy.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import vn.mobileid.id.general.objects.Attributes;
import vn.mobileid.id.paperless.objects.Asset;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName("attributes")
public class PolicyAssetDefault extends Attributes{
    private List<Asset> attributes;
    
    public PolicyAssetDefault() {
    }

    @JsonProperty("attributes")
    public List<Asset> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Asset> attributes) {
        this.attributes = attributes;
    }
    
    public static void main(String[] args) throws Exception{
        PolicyAssetDefault policy = new PolicyAssetDefault();
        List<Asset> lists = new ArrayList<>();
        Asset asset = new Asset();
        asset.setId(3);
        asset.setName("Template for EsignCloud/ELabor.xslt");
        asset.setSize(33694);
        asset.setType(3);
        asset.setIsDefault(true);
        asset.setBase64(Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\Template Esign-Elabor.xslt"))));
        lists.add(asset);
        policy.setAttributes(lists);
        
        System.out.println("Json:"+new ObjectMapper().enable(SerializationFeature.WRAP_ROOT_VALUE).writeValueAsString(policy));
    }
}
