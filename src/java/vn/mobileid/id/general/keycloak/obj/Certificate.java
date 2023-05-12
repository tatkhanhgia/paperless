/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.keycloak.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Certificate {
    private String kid;
    private String kty;
    private String alg;
    private String use;
    private String n;
    private String e;
    private List x5c;
    private String x5t;
    private String x5tS256;

    public Certificate() {
    }

    @JsonProperty("kid")
    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    @JsonProperty("kty")
    public String getKty() {
        return kty;
    }

    public void setKty(String kty) {
        this.kty = kty;
    }

    @JsonProperty("alg")
    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    @JsonProperty("use")
    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    @JsonProperty("n")
    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    @JsonProperty("e")
    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    @JsonProperty("x5c")
    public List getX5c() {
        return x5c;
    }

    public void setX5c(List x5c) {
        this.x5c = x5c;
    }

    @JsonProperty("x5t")
    public String getX5t() {
        return x5t;
    }

    public void setX5t(String x5t) {
        this.x5t = x5t;
    }

    @JsonProperty("x5t#S256")
    public String getX5tS256() {
        return x5tS256;
    }

    public void setX5tS256(String x5tS256) {
        this.x5tS256 = x5tS256;
    }
    
    
}
