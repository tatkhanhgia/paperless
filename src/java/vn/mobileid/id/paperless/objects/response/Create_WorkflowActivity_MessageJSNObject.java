/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Mobile 32
 */
@JsonIgnoreProperties("status")
public class Create_WorkflowActivity_MessageJSNObject {    
    private int status;
    private int workflow_activity_id;
    

    @JsonProperty("status")
    public int getCode() {
        return status;
    }

    public void setCode(int code) {
        this.status = code;
    }

    @JsonProperty("workflow_activity_id")
    public int getWorkflowActivityID() {
        return workflow_activity_id;
    }

    public void setWorkflowActivityID(int workflow_activity_id) {
        this.workflow_activity_id = workflow_activity_id;
    }
}
