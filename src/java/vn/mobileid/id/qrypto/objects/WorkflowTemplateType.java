/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.HashMap;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class WorkflowTemplateType {
    private int id;
    private String name;
    private int status;
    private int workflowType;
    private int ordinary;
    private String code;
    private HashMap<String, Integer> enableObjectMap;
    private String HMAC;
    private String created_by;
    private String created_at;
    private String modified_by;
    private String modified_at;
    
    private String raw;
    
    public WorkflowTemplateType(int id, String name, int status, int workflowType, int ordinary, String code, HashMap<String, Integer> enableObjectMap, String HMAC, String created_by, String created_at, String modified_by, String modified_at) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.workflowType = workflowType;
        this.ordinary = ordinary;
        this.code = code;
        this.enableObjectMap = enableObjectMap;
        this.HMAC = HMAC;
        this.created_by = created_by;
        this.created_at = created_at;
        this.modified_by = modified_by;
        this.modified_at = modified_at;
    }

    public WorkflowTemplateType() {
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    public int getWorkflowType() {
        return workflowType;
    }

    public int getOrdinary() {
        return ordinary;
    }

    public String getCode() {
        return code;
    }
    
    public HashMap<String, Integer> getEnableObjectMap() {
        return enableObjectMap;
    }

    public String getHMAC() {
        return HMAC;
    }

    public String getCreated_by() {
        return created_by;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getModified_by() {
        return modified_by;
    }

    public String getModified_at() {
        return modified_at;
    }
        
    public String getRaw(){
        return null;
    }
    
    public String setRaw(){
        String temp = "{";
        
        temp += "}";
        return temp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setWorkflowType(int workflowType) {
        this.workflowType = workflowType;
    }

    public void setOrdinary(int ordinary) {
        this.ordinary = ordinary;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setEnableObjectMap(HashMap<String, Integer> enableObjectMap) {
        this.enableObjectMap = enableObjectMap;
    }

    public void setHMAC(String HMAC) {
        this.HMAC = HMAC;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setModified_by(String modified_by) {
        this.modified_by = modified_by;
    }

    public void setModified_at(String modified_at) {
        this.modified_at = modified_at;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }
    
    
}
