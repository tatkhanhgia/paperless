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
public class IdentityProcess {

    final public static int STATUS_FEDERAL = 1;
    final public static int STATUS_PENDING = 2;
    final public static int STATUS_PROCESSING = 3;
    final public static int STATUS_ACCEPTED = 4;
    final public static int STATUS_REJECTED = 5;
    final public static int STATUS_INCONCLUSIVE = 6;
    final public static int STATUS_CANCELED = 7;
    final public static int STATUS_FINISHED = 8;

    final public static String STATUS_DESC_PENDING = "PENDING";
    final public static String STATUS_DESC_PROCESSING = "PROCESSING";
    final public static String STATUS_DESC_ACCEPTED = "ACCEPTED";
    final public static String STATUS_DESC_REJECTED = "REJECTED";
    final public static String STATUS_DESC_INCONCLUSIVE = "INCONCLUSIVE";
    final public static String STATUS_DESC_CANCELED = "CANCELED";
    final public static String STATUS_DESC_FINISHED = "FINISHED";

    final public static int ATTR_OTP_PASSCODE = 2;
    final public static int ATTR_OTP_PASSCODE_PROCESSING = 3;
    final public static int ATTR_NEW_OTP_PASSCODE = 4;
    final public static int ATTR_NEW_OTP_PASSCODE_PROCESSING = 5;
    final public static int ATTR_OTP_REMAINING_COUNTER = 6;
    final public static int ATTR_REMAINING_ALLOWED_OTP_GENERATION = 7;
    final public static int ATTR_VERIFICATION_RESULT = 8;
    final public static int ATTR_EXTERNAL_DB_REF_ID = 9;
    final public static int ATTR_SELF_REVISED_INFO = 10;

    private long id;
    private String uuid;
    private int typeId;
    private int statusId;
    private int providerId;
    private long subjectId;
    private int documentTypeId;
    private int relyingPartyId; // created RP
    private String newEmail;
    private String newMobile;
    private String name;
    private String gender;
    private Date dob;
    private String pob;
    private String nationality;
    private String documentValue;
    private String address;
    private Date createdDt;
    private Date modifiedDt;

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

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public int getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(int documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public int getRelyingPartyId() {
        return relyingPartyId;
    }

    public void setRelyingPartyId(int relyingPartyId) {
        this.relyingPartyId = relyingPartyId;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewMobile() {
        return newMobile;
    }

    public void setNewMobile(String newMobile) {
        this.newMobile = newMobile;
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

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getPob() {
        return pob;
    }

    public void setPob(String pob) {
        this.pob = pob;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getDocumentValue() {
        return documentValue;
    }

    public void setDocumentValue(String documentValue) {
        this.documentValue = documentValue;
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

}
