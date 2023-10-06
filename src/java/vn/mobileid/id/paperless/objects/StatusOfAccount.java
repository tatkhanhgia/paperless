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
public class StatusOfAccount{
    @AnnotationORM(columnName = "ID")
    private int id;
    
    @AnnotationORM(columnName = "NAME")
    private String name;       
    
    @AnnotationORM(columnName = "REMARK_EN")
    private String remark;
    
    @AnnotationORM(columnName = "REMARK")
    private String remark_vn;

    public StatusOfAccount() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
