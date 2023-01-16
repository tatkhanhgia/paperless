/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.eid.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequireBiometricEvidence {
    private String biometricType;
    
    public RequireBiometricEvidence setFingerLeftIndex(){
        this.biometricType = "fingerLeftIndex";       
        return this;
    }

    public RequireBiometricEvidence setFingerRightIndex(){
        this.biometricType = "fingerRightIndex";
        return this;
    }    
    
    public RequireBiometricEvidence setFaceID(){
        this.biometricType = "faceID";
        return this;
    }
    
    @JsonProperty("biometricType")
    public String getBiometricType() {
        return biometricType;
    }
    
    
}
