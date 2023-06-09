/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.objects;

import java.util.HashMap;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.utils.Utils;

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
    private Object data;
    private HashMap<String, Object> headers;
    
    public InternalResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.processId = PaperlessConstant.FEDERAL_ID;
        this.subjectId = PaperlessConstant.FEDERAL_ID;
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

    public long getProcessId() {
        if (this.processId == 0) {
            this.processId = PaperlessConstant.FEDERAL_ID;
        }
        return processId;
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

    public long getSubjectId() {
        if (this.subjectId == 0) {
            this.subjectId = PaperlessConstant.FEDERAL_ID;
        }
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public String getProcessUUID() {
        if (!Utils.isNullOrEmpty(this.processUUID)) {
            if (this.processId == 0
                    || this.processId == PaperlessConstant.FEDERAL_ID) {
                this.processUUID = null;
            }
        }
        return processUUID;
    }

    public void setProcessUUID(String processUUID) {
        this.processUUID = processUUID;
    }

    public String getSubjectUUID() {
        if (!Utils.isNullOrEmpty(this.subjectUUID)) {
            if (this.subjectId == 0
                    || this.subjectId == PaperlessConstant.FEDERAL_ID) {
                this.subjectUUID = null;
            }
        }
        return subjectUUID;
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
