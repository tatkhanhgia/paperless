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
public class EventAction {
    @AnnotationORM(columnName = "ID")
    private long id;
    
    @AnnotationORM(columnName = "EVENT_NAME")
    private String event_name;
    
    @AnnotationORM(columnName = "REMARK")
    private String remark;
    
    @AnnotationORM(columnName = "REMARK_EN")
    private String remark_en;

    public EventAction() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark_en() {
        return remark_en;
    }

    public void setRemark_en(String remark_en) {
        this.remark_en = remark_en;
    }
    
    
}
