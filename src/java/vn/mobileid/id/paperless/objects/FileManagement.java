/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import vn.mobileid.id.paperless.serializer.CustomFileManagementSerializer;

/**
 *
 * @author GiaTK
 */
//@JsonRootName("file")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonSerialize(using = CustomFileManagementSerializer.class)
public class FileManagement extends Object{
    private byte[] data;
    private String name;
    private String ID;
    private String created_by;
    private String UUID;
    private String DBMS;
    private int pages;
    private long size;
    private float width;
    private float height;
    private int status;    //Disable - enable
    private String hmac;
    private Date created_at;
    private String lastmodified_by;
    private Date lastmodified_at;
    private boolean isSigned;
    private FileType file_type;
           

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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public float getHeight() {
        return height;
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
        return created_at;
    }

    public void setCreated_ad(Date created_ad) {
        this.created_at = created_ad;
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

    public float getWidth() {
        return width;
    }   

    public boolean isIsSigned() {
        return isSigned;
    }

    public void setIsSigned(boolean isSigned) {
        this.isSigned = isSigned;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }            

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public FileType getFile_type() {
        return file_type;
    }

    public void setFile_type(FileType file_type) {
        this.file_type = file_type;
    }
    
    
    
    public static void main(String[] args) throws JsonProcessingException{
        FileManagement a = new FileManagement();
        a.setName("hello");
        a.setWidth(100);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(a));
    }
    
    public enum FileType{
        PDF("PDF"),
        WORD("DOCX"),
        XSLT("XSLT");
        
        private String name;

        private FileType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }                
    }
}
