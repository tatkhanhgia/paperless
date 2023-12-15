/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RSSPInfoProperties {
    //Business Properties 
    private String businessAgreementUUID;
    private String businessCredentialId;
    private String businessPasswordUUID;
    
    //Witness Properties
    private String witnessAgreementUUID;
    private String witnessCredentialId;
    private String witnessPasswordUUID;

    public RSSPInfoProperties() {
    }

    @JsonProperty("businessAgreementUUID")
    public String getBusinessAgreementUUID() {
        return businessAgreementUUID;
    }

    @JsonProperty("businessCredentialId")
    public String getBusinessCredentialId() {
        return businessCredentialId;
    }

    @JsonProperty("businessPasswordUUID")
    public String getBusinessPasswordUUID() {
        return businessPasswordUUID;
    }

    @JsonProperty("witnessAgreementUUID")
    public String getWitnessAgreementUUID() {
        return witnessAgreementUUID;
    }

    @JsonProperty("witnessCredentialId")
    public String getWitnessCredentialId() {
        return witnessCredentialId;
    }

    @JsonProperty("witnessPasswordUUID")
    public String getWitnessPasswordUUID() {
        return witnessPasswordUUID;
    }
    
    //===================

    public void setBusinessAgreementUUID(String businessAgreementUUID) {
        this.businessAgreementUUID = businessAgreementUUID;
    }

    public void setBusinessCredentialId(String businessCredentialId) {
        this.businessCredentialId = businessCredentialId;
    }

    public void setBusinessPasswordUUID(String businessPasswordUUID) {
        this.businessPasswordUUID = businessPasswordUUID;
    }

    public void setWitnessAgreementUUID(String witnessAgreementUUID) {
        this.witnessAgreementUUID = witnessAgreementUUID;
    }

    public void setWitnessCredentialId(String witnessCredentialId) {
        this.witnessCredentialId = witnessCredentialId;
    }

    public void setWitnessPasswordUUID(String witnessPasswordUUID) {
        this.witnessPasswordUUID = witnessPasswordUUID;
    }
    
    public static void main(String[] args) throws Exception{
        RSSPInfoProperties info = new RSSPInfoProperties();
        info.setBusinessAgreementUUID("OW06742145068696660080");
        info.setBusinessCredentialId("0d8535f8-e54a-43f0-acbf-d88c8ae02cbc");
        info.setBusinessPasswordUUID("12345678");
        
        info.setWitnessAgreementUUID("9E6FA0D0-6319-4D57-A760-99BBBECB35D0");
        info.setWitnessCredentialId("fc081a30-8ed5-40c0-93b3-7b9cbd8d40f2");
        info.setWitnessPasswordUUID("12345678");
        
        System.out.println(new ObjectMapper().writeValueAsString(info));
    }
}
