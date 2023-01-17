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
public class JWT_Authenticate {
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

    public JWT_Authenticate() {
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

    @JsonProperty("math_result")
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
    
    
}
