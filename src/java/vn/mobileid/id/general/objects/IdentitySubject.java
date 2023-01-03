/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.objects;

import java.util.Date;

/**
 *
 * @author ADMIN
 */
public class IdentitySubject {

    final public static int ATTR_ADDRESS_DETAILS = 2;
    final public static int ATTR_ETHNIC = 3;
    final public static int ATTR_RELIGION = 4;
    final public static int ATTR_DOMICILE = 5;
    final public static int ATTR_PERSONAL_IDENTIFICATION = 6;
    final public static int ATTR_FULL_NAME_OF_FATHER = 7;
    final public static int ATTR_FULL_NAME_OF_MOTHER = 8;
    final public static int ATTR_FULL_NAME_OF_SPOUSE = 9;
    final public static int ATTR_ICAO_EVIDENCE = 10;
    final public static int ATTR_DG2_IMAGE = 11;

    private long id;
    private String uuid;
    private int asuranceLevelId;
    private long processId;
    private String name;
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private boolean emailVerified;
    private String mobile;
    private boolean mobileVerified;
    private Date dob;
    private Date doe;
    private Date doi;
    private String pob;
    private String nationality;
    private int identityDocumentTypeId;
    private String identityDocumentValue;
    private String address;
    private Date createdDt;
    private Date modifiedDt;

    // attributes
    private String ethnic;
    private String religion;
    private String domicile;
    private String personalIdentification;
    private String fatherName;
    private String motherName;
    private String spouseName;
    private String icaoEvidence;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getAsuranceLevelId() {
        return asuranceLevelId;
    }

    public void setAsuranceLevelId(int asuranceLevelId) {
        this.asuranceLevelId = asuranceLevelId;
    }

    public long getProcessId() {
        return processId;
    }

    public void setProcessId(long processId) {
        this.processId = processId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isMobileVerified() {
        return mobileVerified;
    }

    public void setMobileVerified(boolean mobileVerified) {
        this.mobileVerified = mobileVerified;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public int getIdentityDocumentTypeId() {
        return identityDocumentTypeId;
    }

    public void setIdentityDocumentTypeId(int identityDocumentTypeId) {
        this.identityDocumentTypeId = identityDocumentTypeId;
    }

    public String getIdentityDocumentValue() {
        return identityDocumentValue;
    }

    public void setIdentityDocumentValue(String identityDocumentValue) {
        this.identityDocumentValue = identityDocumentValue;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    public Date getModifiedDt() {
        return modifiedDt;
    }

    public void setModifiedDt(Date modifiedDt) {
        this.modifiedDt = modifiedDt;
    }

    public String getPob() {
        return pob;
    }

    public void setPob(String pob) {
        this.pob = pob;
    }

    public String getEthnic() {
        return ethnic;
    }

    public void setEthnic(String ethnic) {
        this.ethnic = ethnic;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
        this.domicile = domicile;
    }

    public String getPersonalIdentification() {
        return personalIdentification;
    }

    public void setPersonalIdentification(String personalIdentification) {
        this.personalIdentification = personalIdentification;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public String getIcaoEvidence() {
        return icaoEvidence;
    }

    public void setIcaoEvidence(String icaoEvidence) {
        this.icaoEvidence = icaoEvidence;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDoe() {
        return doe;
    }

    public void setDoe(Date doe) {
        this.doe = doe;
    }

    public Date getDoi() {
        return doi;
    }

    public void setDoi(Date doi) {
        this.doi = doi;
    }

}
