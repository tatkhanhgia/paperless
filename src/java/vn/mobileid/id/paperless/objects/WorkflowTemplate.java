/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 *
 * @author GiaTK
 */
public class WorkflowTemplate {
    private int workflow_id;
    private String meta_data_template;
    private String type_name;
    private int status;
    private String hmac;
    private String created_by;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private Date created_at;
    private String last_modified_by;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private Date   last_modified_at;

    public WorkflowTemplate() {
    }

    public int getWorkflow_id() {
        return workflow_id;
    }

    public void setWorkflow_id(int workflow_id) {
        this.workflow_id = workflow_id;
    }

    public String getMeta_data_template() {
        return meta_data_template;
    }

    public void setMeta_data_template(String meta_data_template) {
        this.meta_data_template = meta_data_template;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }



    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getHmac() {
        return hmac;
    }

    public void setHmac(String hmac) {
        this.hmac = hmac;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getLast_modified_by() {
        return last_modified_by;
    }

    public void setLast_modified_by(String last_modified_by) {
        this.last_modified_by = last_modified_by;
    }

    public Date getLast_modified_at() {
        return last_modified_at;
    }

    public void setLast_modified_at(Date last_modified_at) {
        this.last_modified_at = last_modified_at;
    }
    
    
}
