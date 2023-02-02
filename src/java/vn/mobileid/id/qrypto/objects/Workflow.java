/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.objects;

import vn.mobileid.id.qrypto.QryptoConstant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Workflow {
    private String workflow_id;
    private int status; //0 - inactive  1 - active    
    private int template_type;
    private String label;
    private int workflow_type;
    private String created_by;
    private String created_at;
    private String last_modified_by;
    private String last_modified_at;
        

    public Workflow(String workflow_id, int status, int template_type, String label, int workflow_type, String created_by, String created_at, String last_modified_by, String last_modified_at ) {
        this.workflow_id = workflow_id;
        this.status = status;        
        this.template_type = template_type;
        this.label = label;
        this.workflow_type = workflow_type;
        this.created_by = created_by;
        this.created_at = created_at;
        this.last_modified_by = last_modified_by;
        this.last_modified_at = last_modified_at;        
    }

    public Workflow() {
        this.workflow_id = null;
        this.status = 0;        
//        this.template_type = QryptoConstant.TEMPLATE_TYPE_COURSE_CERTIFICATE_PDF_TEMPLATE;
        this.template_type = 0;
        this.label = null;
        this.workflow_type = QryptoConstant.WORKFLOW_TYPE_PDF_GENERATOR;
        this.created_by = null;
        this.created_at = null;
        this.last_modified_by = null;
        this.last_modified_at = null;    
    }

    @JsonProperty("workflow_id")
    public String getWorkflow_id() {
        return workflow_id;
    }

    public void setWorkflow_id(String workflow_id) {
        this.workflow_id = workflow_id;
    }

    @JsonProperty("workflow_status")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @JsonProperty("workflow_template_type")
    public int getTemplate_type() {
        return template_type;
    }

    public void setTemplate_type(int template_type) {
        this.template_type = template_type;
    }

    @JsonProperty("workflow_label")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @JsonProperty("workflow_type")
    public int getWorkflow_type() {
        return workflow_type;
    }

    public void setWorkflow_type(int workflow_type) {
        this.workflow_type = workflow_type;
    }

    @JsonProperty("created_by")
    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    @JsonProperty("created_at")
    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @JsonProperty("modified_by")
    public String getLast_modified_by() {
        return last_modified_by;
    }

    public void setLast_modified_by(String last_modified_by) {
        this.last_modified_by = last_modified_by;
    }

    @JsonProperty("modified_at")
    public String getLast_modified_at() {
        return last_modified_at;
    }

    public void setLast_modified_at(String last_modified_at) {
        this.last_modified_at = last_modified_at;
    }
    
    
}
