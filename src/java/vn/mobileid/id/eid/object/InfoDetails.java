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
public class InfoDetails {
    private boolean paceEnabled;
    private boolean bacEnabled;
    private boolean activeAuthenticationEnabled;
    private boolean chipAuthenticationEnabled;
    private boolean terminalAuthenticationEnabled;
    private boolean passiceAuthenticationEnabled;
    private String efCom;
    private String efSod;
    private String efCardAccess;
    private DataGroup dataGroup;
    private OptionalDetails optionalDetails;
    private String mrzString;
    private String image;
    private String jwt;

    public InfoDetails() {
    }   
    
    @JsonProperty("paceEnabled")
    public boolean isPaceEnabled() {
        return paceEnabled;
    }

    @JsonProperty("bacEnabled")
    public boolean isBacEnabled() {
        return bacEnabled;
    }

    @JsonProperty("activeAuthenticationEnabled")
    public boolean isActiveAuthenticationEnabled() {
        return activeAuthenticationEnabled;
    }

    @JsonProperty("chipAuthenticationEnabled")
    public boolean isChipAuthenticationEnabled() {
        return chipAuthenticationEnabled;
    }

    @JsonProperty("terminalAuthenticationEnabled")
    public boolean isTerminalAuthenticationEnabled() {
        return terminalAuthenticationEnabled;
    }

    @JsonProperty("passiveAuthenticationEnabled")
    public boolean isPassiceAuthenticationEnabled() {
        return passiceAuthenticationEnabled;
    }

    @JsonProperty("efCom")
    public String getEfCom() {
        return efCom;
    }

    @JsonProperty("efSod")
    public String getEfSod() {
        return efSod;
    }

    @JsonProperty("efCardAccess")
    public String getEfCardAccess() {
        return efCardAccess;
    }

    @JsonProperty("dataGroup")
    public DataGroup getDataGroup() {
        return dataGroup;
    }

    @JsonProperty("optionalDetails")
    public OptionalDetails getOptionalDetails() {
        return optionalDetails;
    }

    @JsonProperty("mrzString")
    public String getMrzString() {
        return mrzString;
    }

    @JsonProperty("image")
    public String getImage() {
        return image;
    }

    @JsonProperty("jwt")
    public String getJwt() {
        return jwt;
    }

    public void setPaceEnabled(boolean paceEnabled) {
        this.paceEnabled = paceEnabled;
    }

    public void setBacEnabled(boolean bacEnabled) {
        this.bacEnabled = bacEnabled;
    }

    public void setActiveAuthenticationEnabled(boolean activeAuthenticationEnabled) {
        this.activeAuthenticationEnabled = activeAuthenticationEnabled;
    }

    public void setChipAuthenticationEnabled(boolean chipAuthenticationEnabled) {
        this.chipAuthenticationEnabled = chipAuthenticationEnabled;
    }

    public void setTerminalAuthenticationEnabled(boolean terminalAuthenticationEnabled) {
        this.terminalAuthenticationEnabled = terminalAuthenticationEnabled;
    }

    public void setPassiceAuthenticationEnabled(boolean passiceAuthenticationEnabled) {
        this.passiceAuthenticationEnabled = passiceAuthenticationEnabled;
    }

    public void setEfCom(String efCom) {
        this.efCom = efCom;
    }

    public void setEfSod(String efSod) {
        this.efSod = efSod;
    }

    public void setEfCardAccess(String efCardAccess) {
        this.efCardAccess = efCardAccess;
    }

    public void setDataGroup(DataGroup dataGroup) {
        this.dataGroup = dataGroup;
    }

    public void setOptionalDetails(OptionalDetails optionalDetails) {
        this.optionalDetails = optionalDetails;
    }

    public void setMrzString(String mrzString) {
        this.mrzString = mrzString;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
    
    
}
