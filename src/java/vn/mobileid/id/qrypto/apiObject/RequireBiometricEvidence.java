/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.apiObject;

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
    
    public void setFingerLeftIndex(){
        this.biometricType = "fingerLeftIndex";        
    }

    public void setFingerRightIndex(){
        this.biometricType = "fingerRightIndex";
    }    
    
    public void setFaceID(){
        this.biometricType = "faceID";
    }
    
    @JsonProperty("biometricType")
    public String getBiometricType() {
        return biometricType;
    }
    
    
}
