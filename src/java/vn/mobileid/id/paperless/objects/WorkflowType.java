/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import vn.mobileid.id.general.annotation.AnnotationORM;

/**
 *
 * @author GiaTK
 */
public class WorkflowType {
    @AnnotationORM(columnName = "ID")
    private int id;
    
    @AnnotationORM(columnName = "WORKFLOW_TYPE_NAME")
    private String workflow_type_name;
    
    @AnnotationORM(columnName = "REMARK_EN")
    private String remark;
    
    @AnnotationORM(columnName = "REMARK")
    private String remark_vn;

    public WorkflowType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorkflow_type_name() {
        return workflow_type_name;
    }

    public void setWorkflow_type_name(String workflow_type_name) {
        this.workflow_type_name = workflow_type_name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark_vn() {
        return remark_vn;
    }

    public void setRemark_vn(String remark_vn) {
        this.remark_vn = remark_vn;
    }
    
    
}
