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
public class GenerationType {
    @AnnotationORM(columnName = "ID")
    private int id;
    
    @AnnotationORM(columnName = "GENERATION_TYPE_NAME")
    private String generation_type_name;
    
    @AnnotationORM(columnName = "WORKFLOW_TYPE")
    private int workflow_type;
    
    @AnnotationORM(columnName = "REMARK_EN")
    private String remark;
    
    @AnnotationORM(columnName = "REMARK")
    private String remark_vn;

    public GenerationType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGeneration_type_name() {
        return generation_type_name;
    }

    public void setGeneration_type_name(String generation_type_name) {
        this.generation_type_name = generation_type_name;
    }

    public int getWorkflow_type() {
        return workflow_type;
    }

    public void setWorkflow_type(int workflow_type) {
        this.workflow_type = workflow_type;
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
