/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import java.util.Date;

/**
 *
 * @author GiaTK
 */
public class RefreshToken {
    private String sessionID;  
    private String client_id;
    private int client_credentials_enabled;
    private Date issued_on;
    private Date expired_on;
    private int status;

    public RefreshToken() {
    }

   

    public Date getIssued_on() {
        return issued_on;
    }

    public void setIssued_on(Date issued_on) {
        this.issued_on = issued_on;
    }

    public Date getExpired_on() {
        return expired_on;
    }

    public void setExpired_on(Date expired_on) {
        this.expired_on = expired_on;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public int getClient_credentials_enabled() {
        return client_credentials_enabled;
    }

    public void setClient_credentials_enabled(int client_credentials_enabled) {
        this.client_credentials_enabled = client_credentials_enabled;
    }
    
    
}
