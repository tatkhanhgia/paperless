/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import vn.mobileid.id.general.annotation.AnnotationORM;
import vn.mobileid.id.paperless.object.enumration.BusinessType;
import vn.mobileid.id.paperless.serializer.CustomAccountSerializer;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize(using = CustomAccountSerializer.class)
public class Account {

    //SigningHub Attribute
    @AnnotationORM(columnName = "USER_NAME")
    private String user_name;

    @AnnotationORM(columnName = "EMAIL")
    private String user_email;

    @AnnotationORM(columnName = "MOBILE_NUMBER")
    private String mobile_number;

    private String job_title;
    private String company;
    private String service_plan;
    private String enterprise_name;
    private String user_password;
    private String security_question;
    private String security_answer;
    private String notification;
    private String service_agreement;
    private String authorization_code;

    @AnnotationORM(columnName = "VERIFIED_ENABLED")
    private boolean verified;

    //Database
    @AnnotationORM(columnName = "ID")
    private int id;

    @AnnotationORM(columnName = "ROLE_NAME_EN")
    private String role;

    @AnnotationORM(columnName = "ROLE_NAME")
    private String role_vn;

    @AnnotationORM(columnName = "BUSINESS_TYPE")
    private BusinessType businessType;

    @AnnotationORM(columnName = "ORGANIZATION_WEBSITE")
    private String organization_website;

//    private StatusOfAccount status ;
    @AnnotationORM(columnName = "STATUS")
    private int status_id;

    private String status_name_vn;

    private String status_name;

    public Account() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole_vn() {
        return role_vn;
    }

    public void setRole_vn(String role_vn) {
        this.role_vn = role_vn;
    }

    @JsonProperty("user_name")
    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    @JsonProperty("user_email")
    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    @JsonProperty("mobile_number")
    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    @JsonProperty("job_title")
    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    @JsonProperty("company")
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @JsonProperty("service_plan")
    public String getService_plan() {
        return service_plan;
    }

    public void setService_plan(String service_plan) {
        this.service_plan = service_plan;
    }

    @JsonProperty("enterprise_name")
    public String getEnterprise_name() {
        return enterprise_name;
    }

    public void setEnterprise_name(String enterprise_name) {
        this.enterprise_name = enterprise_name;
    }

    @JsonProperty("user_password")
    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    @JsonProperty("security_question")
    public String getSecurity_question() {
        return security_question;
    }

    public void setSecurity_question(String security_question) {
        this.security_question = security_question;
    }

    @JsonProperty("security_answer")
    public String getSecurity_answer() {
        return security_answer;
    }

    public void setSecurity_answer(String security_answer) {
        this.security_answer = security_answer;
    }

    @JsonProperty("notification")
    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    @JsonProperty("service_agreement")
    public String getService_agreement() {
        return service_agreement;
    }

    public void setService_agreement(String service_agreement) {
        this.service_agreement = service_agreement;
    }

    @JsonProperty("authorization_code")
    public String getAuthorization_code() {
        return authorization_code;
    }

    public void setAuthorization_code(String authorization_code) {
        this.authorization_code = authorization_code;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public String getStatus_name_vn() {
        return status_name_vn;
    }

    public void setStatus_name_vn(String status_name_vn) {
        this.status_name_vn = status_name_vn;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public void setOrganizationWebsite(String organizationWebsite) {
        this.organization_website = organizationWebsite;
    }

    public int getId() {
        return id;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    @JsonProperty("organization_website")
    public String getOrganizationWebsite() {
        return organization_website;
    }

}
