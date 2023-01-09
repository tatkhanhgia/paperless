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
public class DatabaseResponse {            
    private int status;
    private int remainingCounter;
    private long ownerID;        
    
    private String workflowID;
    
    private int ID_Response;
    private String TransactionID;

    public int getID_Response() {
        return ID_Response;
    }

    public void setID_Response(int ID_Response) {
        this.ID_Response = ID_Response;
    }

    public String getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(String TransactionID) {
        this.TransactionID = TransactionID;
    }
    
    
    public int getIDResponse() {
        return ID_Response;
    }

    public void setIDResponse(int LOG_ID) {
        this.ID_Response = LOG_ID;
    }
    
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRemainingCounter() {
        return remainingCounter;
    }

    public void setRemainingCounter(int remainingCounter) {
        this.remainingCounter = remainingCounter;
    }

    public long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(long ownerID) {
        this.ownerID = ownerID;
    }

    public String getWorkflowID() {
        return workflowID;
    }

    public void setWorkflowID(String workflowID) {
        this.workflowID = workflowID;
    }
            
    
}
