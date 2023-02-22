/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.eid.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.reflect.Field;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class JWT_Authenticate {
    //Header
    private String kid;
    private String alg;
        
    //Payload
    private int fingerprint_confidence;
    private String transaction_id;
    private String sub;
    private String document_number;
    private String gender;
    private String transaction_data;
    private String iss;
    private String place_of_residence;
    private String certificates_query_path;
    private String city_province;
    private String place_of_origin;
    private String nationality;
    private String issuing_country;
    private boolean math_result;
    private String name;
    private int fingerprint_threshold;
    private long exp;
    private long iat;
    private String assurance_level;
    private String jti;
    private String document_type;
    private String phone_number;    

    public JWT_Authenticate() {
    }
    
    public JWT_Authenticate(JWT_Authenticate jwt) {
        this.kid = jwt.getKid();
        this.alg = jwt.getAlg();
        this.fingerprint_confidence = jwt.getFingerprint_confidence();
        this.transaction_id = jwt.getTransaction_id();
        this.sub = jwt.getSub();
        this.document_number = jwt.getDocument_number();
        this.gender = jwt.getGender();
        this.transaction_data = jwt.getTransaction_data();
        this.iss = jwt.getIss();
        this.place_of_residence = jwt.getPlace_of_residence();
        this.certificates_query_path = jwt.getCertificates_query_path();
        this.city_province = jwt.getCity_province();
        this.place_of_origin = jwt.getPlace_of_origin();
        this.nationality = jwt.getNationality();
        this.issuing_country = jwt.getIssuing_country();
        this.math_result = jwt.isMath_result();
        this.name = jwt.getName();
        this.fingerprint_threshold = jwt.getFingerprint_threshold();
        this.exp = jwt.getExp();
        this.iat = jwt.getIat();
        this.assurance_level = jwt.getAssurance_level();
        this.jti = jwt.getJti();
        this.document_type = jwt.getDocument_type();
        this.phone_number = jwt.getPhone_number();
    }
        
    @JsonProperty("fingerprint_confidence")
    public int getFingerprint_confidence() {
        return fingerprint_confidence;
    }

    @JsonProperty("transaction_id")
    public String getTransaction_id() {
        return transaction_id;
    }

    @JsonProperty("sub")
    public String getSub() {
        return sub;
    }

    @JsonProperty("document_number")
    public String getDocument_number() {
        return document_number;
    }

    @JsonProperty("gender")
    public String getGender() {
        return gender;
    }

    @JsonProperty("transaction_data")
    public String getTransaction_data() {
        return transaction_data;
    }

    @JsonProperty("iss")
    public String getIss() {
        return iss;
    }

    @JsonProperty("place_of_residence")
    public String getPlace_of_residence() {
        return place_of_residence;
    }

    @JsonProperty("certificates_query_path")
    public String getCertificates_query_path() {
        return certificates_query_path;
    }

    @JsonProperty("city_province")
    public String getCity_province() {
        return city_province;
    }

    @JsonProperty("place_of_origin")
    public String getPlace_of_origin() {
        return place_of_origin;
    }

    @JsonProperty("nationality")
    public String getNationality() {
        return nationality;
    }

    @JsonProperty("issuing_country")
    public String getIssuing_country() {
        return issuing_country;
    }

    @JsonProperty("match_result")
    public boolean isMath_result() {
        return math_result;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("fingerprint_threshold")
    public int getFingerprint_threshold() {
        return fingerprint_threshold;
    }

    @JsonProperty("exp")
    public long getExp() {
        return exp;
    }

    @JsonProperty("iat")
    public long getIat() {
        return iat;
    }

    @JsonProperty("assurance_level")
    public String getAssurance_level() {
        return assurance_level;
    }

    @JsonProperty("jti")
    public String getJti() {
        return jti;
    }

    @JsonProperty("document_type")
    public String getDocument_type() {
        return document_type;
    }
    
    public String getKid() {
        return kid;
    }
    
    public String getAlg() {
        return alg;
    }

    @JsonProperty("phone_number")
    public String getPhone_number() {
        return phone_number;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    public void setFingerprint_confidence(int fingerprint_confidence) {
        this.fingerprint_confidence = fingerprint_confidence;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public void setDocument_number(String document_number) {
        this.document_number = document_number;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setTransaction_data(String transaction_data) {
        this.transaction_data = transaction_data;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public void setPlace_of_residence(String place_of_residence) {
        this.place_of_residence = place_of_residence;
    }

    public void setCertificates_query_path(String certificates_query_path) {
        this.certificates_query_path = certificates_query_path;
    }

    public void setCity_province(String city_province) {
        this.city_province = city_province;
    }

    public void setPlace_of_origin(String place_of_origin) {
        this.place_of_origin = place_of_origin;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setIssuing_country(String issuing_country) {
        this.issuing_country = issuing_country;
    }

    public void setMath_result(boolean math_result) {
        this.math_result = math_result;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFingerprint_threshold(int fingerprint_threshold) {
        this.fingerprint_threshold = fingerprint_threshold;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    public void setAssurance_level(String assurance_level) {
        this.assurance_level = assurance_level;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
    
    public static Field[] getHashMapFieldName() {
        Field[] fieldKYC = JWT_Authenticate.class.getDeclaredFields();
        return fieldKYC;
    }
}
