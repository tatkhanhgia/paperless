/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.ir.annotations.Ignore;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class WorkflowActivity {

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
    private boolean is_production;
    private int id;
    private FileManagement file;
    private String CSV_id; //pending        
    private int Generation_type;
    private String requestData;
    private String status;    

    public WorkflowActivity() {
        enterprise_name = null;
        enterprise_id = 0;
        workflow_id = 0;
        workflow_label = null;
        workflow_template_type = 0;
        user_email = null;
        transaction = null;
        remark = null;
        use_test_token = false;
        update_enable = false;
        created_by = null;
        is_production = false;
        id = 0;
        file = null;
        CSV_id = null; //pending        
        Generation_type = 0;
        requestData = null;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
    public int isUse_test_token() {
        if (use_test_token) {
            this.is_production = false;
            return 1;
        }
        this.is_production = true;
        return 0;
    }

    public void setUse_test_token(boolean use_test_token) {
        this.use_test_token = use_test_token;
    }

    @JsonProperty("update_enable")
    public int isUpdate_enable() {
        return use_test_token == true ? 1 : 0;
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
    
    public int isIs_production() {
        return is_production == true ? 1 : 0;
    }

    public void setIs_production(boolean is_production) {
        this.is_production = is_production;
    }

    @JsonProperty("file")
    public FileManagement getFile() {
        return file;
    }

    public void setFile(FileManagement file_id) {
        this.file = file_id;
    }
    
    public String getCSV_id() {
        return CSV_id;
    }

    public void setCSV_id(String CSV_id) {
        this.CSV_id = CSV_id;
    }

    @JsonProperty("generation_type")
    public int getGeneration_type() {
        return Generation_type;
    }

    public void setGeneration_type(int Generation_type) {
        this.Generation_type = Generation_type;
    }
    
    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }       
    
    public static void main(String[] args) throws JsonProcessingException {
        WorkflowActivity ac = new WorkflowActivity();
        ac.setId(123);
        ObjectMapper mapper = new ObjectMapper();
        String a = mapper.writeValueAsString(ac);
        System.out.println(a);
        
        String q = "{\"enterprise_id\":3}";
        WorkflowActivity ac2 = new WorkflowActivity();
        ac = mapper.readValue(q, WorkflowActivity.class);
        System.out.println("En:"+ac.getEnterprise_id());
        System.out.println("Boolean:"+ac.use_test_token);
        
    }
}
