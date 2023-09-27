/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.objects;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class DatabaseResponse {            
    private int status;
    private int remainingCounter;
    private long ownerID;                
    
    private int ID_Response_int;
    private String ID_Response_String;

    //Test
    private Object object;      
    private String debugString;
    private List<HashMap<String, Object>> rows;
  

    public void setID_Response_int(int ID_Response_int) {
        this.ID_Response_int = ID_Response_int;
    }

    public int getIDResponse_int(){
        return this.ID_Response_int;
    }
    
    public String getIDResponse() {
        if(ID_Response_String != null)
        {
            return ID_Response_String;
        }
        return String.valueOf(ID_Response_int);
    }

    public void setID_Response_String(String ID_Response_String) {
        this.ID_Response_String = ID_Response_String;
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

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getDebugString() {
        return debugString;
    }

    public void setDebugString(String debugString) {
        this.debugString = debugString;
    }
            
    public void setRows(List<HashMap<String, Object>> rows){
        this.rows = rows;
    }
    
    public List<HashMap<String, Object>> getRows(){
        return this.rows;
    }
}
