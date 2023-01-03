/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.keycloak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author ADMIN
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Attributes {

    private String[] dateOfBirth;
    private String[] dateOfExpiry;

    @JsonProperty("date_of_birth")
    public String[] getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String[] dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @JsonProperty("date_of_expiry")
    public String[] getDateOfExpiry() {
        return dateOfExpiry;
    }

    public void setDateOfExpiry(String[] dateOfExpiry) {
        this.dateOfExpiry = dateOfExpiry;
    }

}
