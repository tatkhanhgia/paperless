/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import java.util.Date;

/**
 *
 * @author GiaTK
 */
public class Transaction {
    private String id;
    private int log_id;
    private int object_id;
    private ObjectType object_type; //
    private int user_id;
    private String ip_add;
    private String initial_file;
    private int Y;
    private int X;
    private int C;
    private int S;
    private int pages;
    private String description;
    private String hmac;
    private String created_by;
    private Date created_at;
    private String modified_by;
    private Date modified_at;

    public Transaction() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLog_id() {
        return log_id;
    }

    public void setLog_id(int log_id) {
        this.log_id = log_id;
    }

    public int getObject_id() {
        return object_id;
    }

    public void setObject_id(int object_id) {
        this.object_id = object_id;
    }

    public ObjectType getObject_type() {
        return object_type;
    }

    public void setObject_type(ObjectType object_type) {
        this.object_type = object_type;
    }
    
    public void setObject_type(int object_type) {
        if(this.object_type == null){
            this.object_type = ObjectType.PDF;
        }
        this.object_type.setNumber(object_type);
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getIp_add() {
        return ip_add;
    }

    public void setIp_add(String ip_add) {
        this.ip_add = ip_add;
    }

    public String getInitial_file() {
        return initial_file;
    }

    public void setInitial_file(String initial_file) {
        this.initial_file = initial_file;
    }

    public int getY() {
        return Y;
    }

    public void setY(int Y) {
        this.Y = Y;
    }

    public int getX() {
        return X;
    }

    public void setX(int X) {
        this.X = X;
    }

    public int getC() {
        return C;
    }

    public void setC(int C) {
        this.C = C;
    }

    public int getS() {
        return S;
    }

    public void setS(int S) {
        this.S = S;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHmac() {
        return hmac;
    }

    public void setHmac(String hmac) {
        this.hmac = hmac;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
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
    
    public enum ObjectType{
        QR(1),
        CSV(2),
        PDF(3);
        
        private int number;

        private ObjectType(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }     

        public void setNumber(int number) {
            this.number = number;
        }                
    }
}
