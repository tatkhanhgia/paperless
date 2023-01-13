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
public class BiometricEvidenceRequest {
    private String cmdType;
    private String requestID;
    private int timeOutInterval;
    private RequireBiometricEvidence biometricType;

    @JsonProperty("cmdType")
    public String getCmdType() {
        return cmdType;
    }

    public void setCmdType(String cmdType) {
        this.cmdType = cmdType;
    }

    @JsonProperty("requestID")
    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    @JsonProperty("timeOutInterval")
    public int getTimeOutInterval() {
        return timeOutInterval;
    }

    public void setTimeOutInterval(int timeOutInterval) {
        this.timeOutInterval = timeOutInterval;
    }

    @JsonProperty("data")
    public RequireBiometricEvidence getBiometricType() {
        return biometricType;
    }

    public void setBiometricType(RequireBiometricEvidence biometricType) {
        this.biometricType = biometricType;
    }
    
    
}
