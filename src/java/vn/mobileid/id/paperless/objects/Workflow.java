/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import vn.mobileid.id.paperless.PaperlessConstant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Workflow {
    private int workflow_id;
    private int status; //0 - inactive  1 - active    
    private int template_type;
    private String template_type_name;
    private String label;
    private int workflow_type;
    private String workflow_type_name;
    private String note;
    private String created_by;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private Date created_at;
    private String last_modified_by;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private Date last_modified_at;
    private String metadata;
    private int initiator_id;

    public Workflow(int workflow_id, int status, int template_type, String label, int workflow_type, String created_by, Date created_at, String last_modified_by, Date last_modified_at ) {
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
    }

    @JsonProperty("workflow_id")
    public int getWorkflow_id() {
        return workflow_id;
    }

    public void setWorkflow_id(int workflow_id) {
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
    public int getWorkflowTemplate_type() {
        return template_type;
    }

    public void setWorkflowTemplate_type(int template_type) {
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
    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
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
    public Date getLast_modified_at() {
        return last_modified_at;
    }

    public void setLast_modified_at(Date last_modified_at) {
        this.last_modified_at = last_modified_at;
    }

    @JsonProperty("note")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @JsonProperty("metadata")
    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @JsonProperty("template_type_name")
    public String getWorkflowTemplate_type_name() {
        return template_type_name;
    }

    public void setWorkflowTemplate_type_name(String template_type_name) {
        this.template_type_name = template_type_name;
    }

    @JsonProperty("workflow_type_name")
    public String getWorkflow_type_name() {
        return workflow_type_name;
    }

    public void setWorkflow_type_name(String workflow_type_name) {
        this.workflow_type_name = workflow_type_name;
    }

    @JsonProperty("initiator_id")
    public int getInitiator_id() {
        return initiator_id;
    }

    public void setInitiator_id(int initiator_id) {
        this.initiator_id = initiator_id;
    }
    
    
    
}
