/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.keycloak.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import vn.mobileid.id.general.annotation.AnnotationORM;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class User {
    //Format JWT
    private long exp; //expired time
    private long iat; //issued at
    private String jti;
    private String iss; //Issure
    private String aud; //Audience
    private String sub; //Subject
    private String typ; //Type
    private String session_state;
    private String acr;
    private Role realm_access;
    private ResourceAccess resource_access;
    private String scope;
    private String sid; //Session id
    private boolean email_verified;
    private String name;
    private String preferred_username;
    private String given_name;
    private String family_name;
    
    @AnnotationORM(columnName = "EMAIL")
    private String email;
    private String azp; //Authorize Party
    private String nbf; //Not before time
    
    //DB
    @AnnotationORM(columnName = "MOBILE_NUMBER")
    private String mobile;
    private int id;
    
    @AnnotationORM(columnName = "ROLE_NAME_EN")
    private String role_name;
    private String role_id;
    private int aid; //authorized party ID
    private Date passwordExpiredAt;
    
    @AnnotationORM(columnName = "QR_EXPIRED_DURATION")
    private long qr_expired_time;
    
    //Internal Service
    private String ipAddress;
    
    @JsonProperty("exp")
    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    @JsonProperty("aid")
    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    
    @JsonProperty("iat")
    public long getIat() {
        return iat;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    @JsonProperty("jti")
    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    @JsonProperty("iss")
    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    @JsonProperty("aud")
    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    @JsonProperty("sub")
    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    @JsonProperty("typ")
    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    @JsonProperty("session_state")
    public String getSession_state() {
        return session_state;
    }

    public void setSession_state(String session_state) {
        this.session_state = session_state;
    }

    @JsonProperty("acr")
    public String getAcr() {
        return acr;
    }

    public void setAcr(String acr) {
        this.acr = acr;
    }

    @JsonProperty("realm_access")
    public Role getRealm_access() {
        return realm_access;
    }

    public void setRealm_access(Role realm_access) {
        this.realm_access = realm_access;
    }

    @JsonProperty("resource_access")
    public ResourceAccess getResource_access() {
        return resource_access;
    }

    public void setResource_access(ResourceAccess resource_access) {
        this.resource_access = resource_access;
    }

    @JsonProperty("scope")
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @JsonProperty("sid")
    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    @JsonProperty("email_verified")
    public boolean isEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(boolean email_verified) {
        this.email_verified = email_verified;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("preferred_username")
    public String getPreferred_username() {
        return preferred_username;
    }

    public void setPreferred_username(String preferred_username) {
        this.preferred_username = preferred_username;
    }

    @JsonProperty("given_name")
    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    @JsonProperty("family_name")
    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("azp")
    public String getAzp() {
        return azp;
    }

    public void setAzp(String azp) {
        this.azp = azp;
    }

    @JsonProperty("mobile")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    public Date getPasswordExpiredAt() {
        return passwordExpiredAt;
    }

    public void setPasswordExpiredAt(Date passwordExpiredAt) {
        this.passwordExpiredAt = passwordExpiredAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @JsonProperty("qr_ext")
    public long getQr_expired_time() {
        return qr_expired_time;
    }

    public void setQr_expired_time(long qr_expired_time) {
        this.qr_expired_time = qr_expired_time;
    }
}