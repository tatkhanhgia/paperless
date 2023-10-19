/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import vn.mobileid.id.general.annotation.AnnotationORM;
import vn.mobileid.id.paperless.object.enumration.DownloadLinkType;
import vn.mobileid.id.paperless.serializer.CustomWorkflowActivitySerializer;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonSerialize(using = CustomWorkflowActivitySerializer.class)
public class WorkflowActivity {
    
    @AnnotationORM(columnName = "ID")
    private int id;
    
    @AnnotationORM(columnName = "STATUS")
    private int statusId; 
    
    @AnnotationORM(columnName = "STATUS_NAME_EN")
    private String status_name;
    
    @AnnotationORM(columnName = "STATUS_NAME")
    private String status_name_vn;
    
    private String enterprise_name;
    
    @AnnotationORM(columnName = "ENTERPRISE_ID")
    private int enterprise_id;
    
    @AnnotationORM(columnName = "WORKFLOW_ID")
    private long workflow_id;
    
    @AnnotationORM(columnName = "WORKFLOW_LABEL")
    private String workflow_label;
    
    @AnnotationORM(columnName = "WORKFLOW_TEMPLATE_TYPE_ID")
    private int workflow_template_type;
    
    private String user_email;
    
    @AnnotationORM(columnName = "TRANSACTION_ID")
    private String transaction;
    
    @AnnotationORM(columnName = "REMARK")
    private String remark;
    
    @AnnotationORM(columnName = "REQUEST_DATA")
    private String requestData;     
    
    public  boolean use_test_token; //PENDING
    public  boolean update_enable;  //PENDING
    private boolean is_production;  //PENDING
    
    @AnnotationORM(columnName = "CREATED_BY")
    private String created_by;
    
    @AnnotationORM(columnName = "CREATED_AT")
    private Date created_at;
    
    @AnnotationORM(columnName = "LAST_MODIFIED_BY")
    private String modified_by;
    
    @AnnotationORM(columnName = "LAST_MODIFIED_AT")
    private Date modified_at;
    
    private int generation_type;
    
    @AnnotationORM(columnName = "GENERATION_TYPE_NAME_EN")
    private String generation_type_name;
    
    @AnnotationORM(columnName = "GENERATION_TYPE_NAME")
    private String generation_type_name_vn;
    
    @AnnotationORM(columnName = "DOWNLOAD_LINK")
    private String dowload_link;
    
    @AnnotationORM(columnName = "DOWNLOAD_LINK_TYPE")
    private DownloadLinkType download_link_type = DownloadLinkType.PDF;
    
    @AnnotationORM(columnName = "WORKFLOW_TEMPLATE_TYPE_NAME_EN")
    private String workflow_template_type_name;
    
    @AnnotationORM(columnName = "WORKFLOW_TEMPLATE_TYPE_NAME")
    private String workflow_template_type_name_vn;

    private FileManagement file; 
    private CSVTask csv;
    
    private String CSV_id;     
    
    private boolean csvEnabled; 
    
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
        generation_type = 0;
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
    public long getWorkflow_id() {
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
    
    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }
   
    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getModified_by() {
        return modified_by;
    }

    public void setModified_by(String modified_by) {
        this.modified_by = modified_by;
    }

    public Date getModified_at() {
        return modified_at;
    }

    public void setModified_at(Date modified_at) {
        this.modified_at = modified_at;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    @JsonProperty("download_link")
    public String getDowload_link() {
        return dowload_link;
    }

    public void setDowload_link(String dowload_link) {
        this.dowload_link = dowload_link;
    }

    @JsonProperty("download_link_type")
    public DownloadLinkType getDownload_link_type() {
        return download_link_type;
    }

    public void setDownload_link_type(DownloadLinkType download_link_type) {
        this.download_link_type = download_link_type;
    }

    public String getWorkflow_template_type_name() {
        return workflow_template_type_name;
    }

    public void setWorkflow_template_type_name(String workflow_template_type_name) {
        this.workflow_template_type_name = workflow_template_type_name;
    }

    @JsonProperty("status")
    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public String getStatus_name_vn() {
        return status_name_vn;
    }

    public void setStatus_name_vn(String status_name_vn) {
        this.status_name_vn = status_name_vn;
    }

    @JsonProperty("generation_type")
    public String getGeneration_type_name() {
        return generation_type_name;
    }

    public void setGeneration_type_name(String generation_type_name) {
        this.generation_type_name = generation_type_name;
    }

    public String getWorkflow_template_type_name_vn() {
        return workflow_template_type_name_vn;
    }

    public void setWorkflow_template_type_name_vn(String workflow_template_type_name_vn) {
        this.workflow_template_type_name_vn = workflow_template_type_name_vn;
    }

    public int getGeneration_type() {
        return generation_type;
    }

    public void setGeneration_type(int generation_type) {
        this.generation_type = generation_type;
    }

    public String getGeneration_type_name_vn() {
        return generation_type_name_vn;
    }

    public void setGeneration_type_name_vn(String generation_type_name_vn) {
        this.generation_type_name_vn = generation_type_name_vn;
    }

    @JsonProperty("csv_enabled")
    public boolean isCsvEnabled() {
        return csvEnabled;
    }

    public void setCsvEnabled(boolean csvEnabled) {
        this.csvEnabled = csvEnabled;
    }

    public CSVTask getCsv() {
        return csv;
    }

    public void setCsv(CSVTask csv) {
        this.csv = csv;
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
