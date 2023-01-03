/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.everification.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author VUDP
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CAProperties extends Attributes {

    private boolean ocspEnabled;
    private boolean crlEnabled;
    private CRL crl;
    private OCSP ocsp;
    
    private boolean autoEnrollEnabled;

    @JsonProperty("ocspEnabled")
    public boolean isOcspEnabled() {
        return ocspEnabled;
    }

    public void setOcspEnabled(boolean ocspEnabled) {
        this.ocspEnabled = ocspEnabled;
    }

    @JsonProperty("crlEnabled")
    public boolean isCrlEnabled() {
        return crlEnabled;
    }

    public void setCrlEnabled(boolean crlEnabled) {
        this.crlEnabled = crlEnabled;
    }

    @JsonProperty("crl")
    public CRL getCrl() {
        return crl;
    }

    public void setCrl(CRL crl) {
        this.crl = crl;
    }

    @JsonProperty("ocsp")
    public OCSP getOcsp() {
        return ocsp;
    }

    public void setOcsp(OCSP ocsp) {
        this.ocsp = ocsp;
    }

    @JsonProperty("autoEnrollEnabled")
    public boolean isAutoEnrollEnabled() {
        return autoEnrollEnabled;
    }

    public void setAutoEnrollEnabled(boolean autoEnrollEnabled) {
        this.autoEnrollEnabled = autoEnrollEnabled;
    }
}
