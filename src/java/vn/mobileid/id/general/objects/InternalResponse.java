/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.objects;

import java.util.HashMap;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.paperless.objects.Enterprise;

/**
 *
 * @author ADMIN
 */
public class InternalResponse {

    private int status;
    private String message;
    private long processId;
    private long subjectId;
    private String processUUID;
    private String subjectUUID;

    // for verification aws
    private String relyingPartyName;
    private int relyingPartyId;
    private String authenticationJSNMessage;
    private String clientType;

    //Data backend
    private User object;
    private Enterprise enterprise;
    private Object data;
    private HashMap<String, Object> headers;
    
    public InternalResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public InternalResponse(int status, Object data) {
        this.status = status;
        this.data = data;
    }       

    public InternalResponse() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void setProcessId(long processId) {
        this.processId = processId;
    }

    public String getRelyingPartyName() {
        return relyingPartyName;
    }

    public void setRelyingPartyName(String relyingPartyName) {
        this.relyingPartyName = relyingPartyName;
    }

    public String getAuthenticationJSNMessage() {
        return authenticationJSNMessage;
    }

    public void setAuthenticationJSNMessage(String authenticationJSNMessage) {
        this.authenticationJSNMessage = authenticationJSNMessage;
    }

    public int getRelyingPartyId() {
        return relyingPartyId;
    }

    public void setRelyingPartyId(int relyingPartyId) {
        this.relyingPartyId = relyingPartyId;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }


    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }
    public void setProcessUUID(String processUUID) {
        this.processUUID = processUUID;
    }


    public void setSubjectUUID(String subjectUUID) {
        this.subjectUUID = subjectUUID;
    }

    /**
     * Get User (type User)
     * @return 
     */
    public User getUser() {
        return object;
    }

    public void setUser(User object) {
        this.object = object;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }
 
    /**
     * Get data Object from Response
     * @return 
     */
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public HashMap<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, Object> headers) {
        this.headers = headers;
    }   
    
}
