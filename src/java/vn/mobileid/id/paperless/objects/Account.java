/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {

    //SigningHub Attribute
    private String user_name;
    private String user_email;
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
    private boolean verified;

    //Database
    private int id;
    private String role;
    private BusinessType businessType;
    private String organizationWebsite;
    private Status status;

    public Account() {
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

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStatus(int statusid) {
        this.status = Status.get(statusid);
    }

    public Status getStatus() {
        return this.status;
    }

    @JsonProperty("verified")
    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @JsonProperty("enterprise_role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public void setOrganizationWebsite(String organizationWebsite) {
        this.organizationWebsite = organizationWebsite;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("business_type")
    public BusinessType getBusinessType() {
        return businessType;
    }

    @JsonProperty("organization_website")
    public String getOrganizationWebsite() {
        return organizationWebsite;
    }

    public enum BusinessType {
        PERSONAL("PERSONAL", 1),
        BUSINESS("BUSINESS", 2);
        private String nameType;
        private int type;

        private BusinessType(String nameType, int type) {
            this.nameType = nameType;
            this.type = type;
        }

        public String getNameType() {
            return nameType;
        }

        public int getType() {
            return type;
        }
    }

    public enum Status {
        INVITED("INVITED", 1),
        REGISTERED("REGISTERED", 2),
        ACTIVATED("ACTIVATED", 3),
        BANNED("BANNED", 4),
        MARK_DELETED("MARK_DELETED", 5),
        GUEST("GUEST", 6),
        IN_ACTIVE("IN_ACTIVE", 7),
        APPROVAL_REQUIRED("APPROVAL_REQUIRED", 8);

        private String status_name;
        private int status;

        private Status(String status_name, int status) {
            this.status_name = status_name;
            this.status = status;
        }

        @JsonProperty("status_name")
        public String getStatus_name() {
            return status_name;
        }

        @JsonProperty("status_id")
        public int getStatus() {
            return status;
        }

        public static Status get(int statusId) {
            for (Status status : values()) {
                if (status.getStatus() == statusId) {
                    return status;
                }
            }
            return Status.BANNED;
        }
    }
}
