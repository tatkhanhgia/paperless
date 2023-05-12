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
public class OptionalDetails {
    private String personalNumber;
    private String fullName;
    private String birthDate;
    private String gender;
    private String nationality;
    private String ethnic;
    private String religion;
    private String placeOfOrigin;
    private String placeOfResidence;
    private String personalIdentification;
    private String issuanceDate;
    private String expiryDate;
    private String idDocument;
    private String fullNameOfParents;
    private String fullNameOfSpouse;

    public OptionalDetails() {
    }   
    
    @JsonProperty("personalNumber")
    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    @JsonProperty("fullName")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @JsonProperty("birthDat")
    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    @JsonProperty("gender")
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @JsonProperty("nationality")
    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    @JsonProperty("ethnic")
    public String getEthnic() {
        return ethnic;
    }

    public void setEthnic(String ethnic) {
        this.ethnic = ethnic;
    }

    @JsonProperty("religion")
    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    @JsonProperty("placeOfOrigin")
    public String getPlaceOfOrigin() {
        return placeOfOrigin;
    }

    public void setPlaceOfOrigin(String placeOfOrigin) {
        this.placeOfOrigin = placeOfOrigin;
    }

    @JsonProperty("placeOfResidence")
    public String getPlaceOfResidence() {
        return placeOfResidence;
    }

    public void setPlaceOfResidence(String placeOfResidence) {
        this.placeOfResidence = placeOfResidence;
    }

    @JsonProperty("personalIdentification")
    public String getPersonalIdentification() {
        return personalIdentification;
    }

    public void setPersonalIdentification(String personalIdentification) {
        this.personalIdentification = personalIdentification;
    }

    @JsonProperty("issuanceDate")
    public String getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(String issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    @JsonProperty("expiryDate")
    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    @JsonProperty("idDocument")
    public String getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(String idDocument) {
        this.idDocument = idDocument;
    }

    @JsonProperty("fullNameOfParents")
    public String getFullNameOfParents() {
        return fullNameOfParents;
    }

    public void setFullNameOfParents(String fullNameOfParents) {
        this.fullNameOfParents = fullNameOfParents;
    }

    @JsonProperty("fullNameOfSpouse")
    public String getFullNameOfSpouse() {
        return fullNameOfSpouse;
    }

    public void setFullNameOfSpouse(String fullNameOfSpouse) {
        this.fullNameOfSpouse = fullNameOfSpouse;
    }
    
    
}
