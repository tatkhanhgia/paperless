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
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Account {
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
    
    
}
