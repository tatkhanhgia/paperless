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
public class Category extends DatabaseDefaultObject{
    @AnnotationORM(columnName = "ID")
    private int id;
    
    @AnnotationORM(columnName = "CATEGORY_NAME")
    private String category_name;
    
    @AnnotationORM(columnName = "REMARK_EN")
    private String remark;
    
    @AnnotationORM(columnName = "REMARK")
    private String remark_vn;

    public Category() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
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
