/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import java.util.Date;
import vn.mobileid.id.general.annotation.AnnotationORM;

/**
 *
 * @author GiaTK
 */
public class UserActivity extends DatabaseDefaultObject{
    @AnnotationORM(columnName="ID")
    private int id;
    
    @AnnotationORM(columnName="USER_ID")
    private int user_id;
    
    @AnnotationORM(columnName="ENTERPRISE_ID")
    private long enterprise_id;
    
    @AnnotationORM(columnName="DATE")
    private Date date;
    
    @AnnotationORM(columnName="TRANSACTION_ID")
    private String transaction_id;
    
    @AnnotationORM(columnName="LOG_ID")
    private int log_id;
    
    @AnnotationORM(columnName="CATEGORY_ID")
    private int category_id;
    
    @AnnotationORM(columnName="USER_ACTIVITY_EVENT_ID")
    private int user_activity_event;

    public UserActivity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public long getEnterprise_id() {
        return enterprise_id;
    }

    public void setEnterprise_id(long enterprise_id) {
        this.enterprise_id = enterprise_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public int getLog_id() {
        return log_id;
    }

    public void setLog_id(int log_id) {
        this.log_id = log_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getUser_activity_event() {
        return user_activity_event;
    }

    public void setUser_activity_event(int user_activity_event) {
        this.user_activity_event = user_activity_event;
    }
    
    
}
