/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;

/**
 *
 * @author GiaTK
 */
//@JsonRootName("file")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FileManagement extends Object{
    private byte[] data;
    private String name;
    private String ID;
    private String created_by;
    private String UUID;
    private String DBMS;
    private int pages;
    private int size;
    private int width;
    private int height;
    private int status;    
    private String hmac;
    private Date created_ad;
    private String lastmodified_by;
    private Date lastmodified_at;
           

    public FileManagement(byte[] data, String name, String ID, String created_by) {
        this.data = data;
        this.name = name;
        this.ID = ID;
        this.created_by = created_by;
    }

    public FileManagement(byte[] data, String name, String ID) {
        this.data = data;
        this.name = name;
        this.ID = ID;
    }

    
    
    public FileManagement() {
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getDBMS() {
        return DBMS;
    }

    public void setDBMS(String DBMS) {
        this.DBMS = DBMS;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getHmac() {
        return hmac;
    }

    public void setHmac(String hmac) {
        this.hmac = hmac;
    }

    public Date getCreated_ad() {
        return created_ad;
    }

    public void setCreated_ad(Date created_ad) {
        this.created_ad = created_ad;
    }

    public String getLastmodified_by() {
        return lastmodified_by;
    }

    public void setLastmodified_by(String lastmodified_by) {
        this.lastmodified_by = lastmodified_by;
    }

    public Date getLastmodified_at() {
        return lastmodified_at;
    }

    public void setLastmodified_at(Date lastmodified_at) {
        this.lastmodified_at = lastmodified_at;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
                
    public static void main(String[] args) throws JsonProcessingException{
        FileManagement a = new FileManagement();
        a.setName("hello");
        a.setWidth(100);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(a));
    }
}
