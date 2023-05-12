/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Configuration {
    private String contextIdentifier;
    private int qryptoWidth;
    private int qryptoHeight;
    private boolean isTransparent;
    private qryptoEffectiveDate qryptoEffectiveDate;

    public Configuration() {
    }

    @JsonProperty("contextIdentifier")
    public String getContextIdentifier() {
        return contextIdentifier;
    }

    public void setContextIdentifier(String contextIdentifier) {
        this.contextIdentifier = contextIdentifier;
    }

    @JsonProperty("qryptoWidth")
    public int getQryptoWidth() {
        return qryptoWidth;
    }

    public void setQryptoWidth(int qryptoWidth) {
        this.qryptoWidth = qryptoWidth;
    }

    @JsonProperty("qryptoHeight")
    public int getQryptoHeight() {
        return qryptoHeight;
    }

    public void setQryptoHeight(int qryptoHeight) {
        this.qryptoHeight = qryptoHeight;
    }

    @JsonProperty("isTransparent")
    public boolean IsTransparent() {
        return isTransparent;
    }

    public void setIsTransparent(boolean isTransparent) {
        this.isTransparent = isTransparent;
    }

    @JsonProperty("qryptoEffectiveDate")
    public qryptoEffectiveDate getQryptoEffectiveDate() {
        return qryptoEffectiveDate;
    }

    public void setQryptoEffectiveDate(qryptoEffectiveDate qryptoEffectiveDate) {
        this.qryptoEffectiveDate = qryptoEffectiveDate;
    }
    
    
}
