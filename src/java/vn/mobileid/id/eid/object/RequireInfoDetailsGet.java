/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.eid.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequireInfoDetailsGet {
    private boolean mrzEnabled;
    private boolean imageEnabled;
    private boolean dataGroupEnabled;
    private boolean optionDetailsEnabled;
    private String canValue;
    private String challenge;
    private boolean caEnabled;
    private boolean taEnabled;
    private boolean paEnabled;

    @JsonProperty("mrzEnabled")
    public boolean isMrzEnabled() {
        return mrzEnabled;
    }

    @JsonProperty("imageEnabled")
    public boolean isImageEnabled() {
        return imageEnabled;
    }

    @JsonProperty("dataGroupEnabled")
    public boolean isDataGroupEnabled() {
        return dataGroupEnabled;
    }

    @JsonProperty("optionDetailsEnabled")
    public boolean isOptionDetailsEnabled() {
        return optionDetailsEnabled;
    }

    @JsonProperty("canValue")
    public String getCanValue() {
        return canValue;
    }

    @JsonProperty("challenge")
    public String getChallenge() {
        return challenge;
    }

    @JsonProperty("caEnabled")
    public boolean isCaEnabled() {
        return caEnabled;
    }

    @JsonProperty("taEnabled")
    public boolean isTaEnabled() {
        return taEnabled;
    }

    @JsonProperty("paEnabled")
    public boolean isPaEnabled() {
        return paEnabled;
    }

    public void setMrzEnabled(boolean mrzEnabled) {
        this.mrzEnabled = mrzEnabled;
    }

    public void setImageEnabled(boolean imageEnabled) {
        this.imageEnabled = imageEnabled;
    }

    public void setDataGroupEnabled(boolean dataGroupEnabled) {
        this.dataGroupEnabled = dataGroupEnabled;
    }

    public void setOptionDetailsEnabled(boolean optionDetailsEnabled) {
        this.optionDetailsEnabled = optionDetailsEnabled;
    }

    public void setCanValue(String canValue) {
        this.canValue = canValue;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public void setCaEnabled(boolean caEnabled) {
        this.caEnabled = caEnabled;
    }

    public void setTaEnabled(boolean taEnabled) {
        this.taEnabled = taEnabled;
    }

    public void setPaEnabled(boolean paEnabled) {
        this.paEnabled = paEnabled;
    }
    
    
}
