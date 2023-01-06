/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QryptoWorkflowActivityJSNObject {
    private String enterprise_name;
    private int enterprise_id;
    private int workflow_id;
    private String workflow_label;
    private int workflow_template_type;
    private String user_email;
    private String transaction;
    private String remark;
    private boolean use_test_token;
    private boolean update_enable;
    private String created_by;

    @JsonProperty("enterprise_name")
    public String getEnterprise_name() {
        return enterprise_name;
    }

    public void setEnterprise_name(String enterprise_name) {
        this.enterprise_name = enterprise_name;
    }

    @JsonProperty("workflow_id")
    public int getWorkflow_id() {
        return workflow_id;
    }

    public void setWorkflow_id(int workflow_id) {
        this.workflow_id = workflow_id;
    }

    @JsonProperty("workflow_label")
    public String getWorkflow_label() {
        return workflow_label;
    }

    public void setWorkflow_label(String workflow_label) {
        this.workflow_label = workflow_label;
    }

    @JsonProperty("workflow_template_type")
    public int getWorkflow_template_type() {
        return workflow_template_type;
    }

    public void setWorkflow_template_type(int workflow_template_type) {
        this.workflow_template_type = workflow_template_type;
    }

    @JsonProperty("user_email")
    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    @JsonProperty("transaction")
    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    @JsonProperty("remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @JsonProperty("use_test_token")
    public boolean isUse_test_token() {
        return use_test_token;
    }

    public void setUse_test_token(boolean use_test_token) {
        this.use_test_token = use_test_token;
    }

    @JsonProperty("update_enable")
    public boolean isUpdate_enable() {
        return update_enable;
    }

    public void setUpdate_enable(boolean update_enable) {
        this.update_enable = update_enable;
    }

    @JsonProperty("created_by")
    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    @JsonProperty("enterprise_id")
    public int getEnterprise_id() {
        return enterprise_id;
    }

    public void setEnterprise_id(int enterprise_id) {
        this.enterprise_id = enterprise_id;
    }
    
    
}
