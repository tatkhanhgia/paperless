/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import vn.mobileid.id.paperless.object.enumration.FileType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import vn.mobileid.id.general.annotation.AnnotationORM;
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
    
    @AnnotationORM(columnName = "NAME")
    private String name;
    
    @AnnotationORM(columnName = "ID")
    private long ID;
    
    @AnnotationORM(columnName = "CREATED_BY")
    private String created_by;
    
    @AnnotationORM(columnName = "UUID")
    private String UUID;
    
    @AnnotationORM(columnName = "DMS_PROPERTY")
    private String DBMS;
    
    @AnnotationORM(columnName = "PAGES")
    private int pages;
    
    @AnnotationORM(columnName = "SIZE")
    private long size;
    
    @AnnotationORM(columnName = "WIDTH")
    private float width;
    
    @AnnotationORM(columnName = "HEIGHT")
    private float height;
    
    @AnnotationORM(columnName = "STATUS")
    private int status;    //Disable - enable
    
    @AnnotationORM(columnName = "HMAC")
    private String hmac;
    
    @AnnotationORM(columnName = "CREATED_AT")
    private Date created_at;
    
    @AnnotationORM(columnName = "LAST_MODIFIED_BY")
    private String lastmodified_by;
    
    @AnnotationORM(columnName = "LAST_MODIFIED_AT")
    private Date lastmodified_at;
    
    @AnnotationORM(columnName = "PROCESSED_ENABLED")
    private boolean isSigned;
    
    @AnnotationORM(columnName = "FILE_TYPE")
    private FileType file_type = FileType.PDF;
    
    @AnnotationORM(columnName = "SIGNING_PROPERTIES")
    private String signingProperties;
           

    public FileManagement(byte[] data, String name, long ID, String created_by) {
        this.data = data;
        this.name = name;
        this.ID = ID;
        this.created_by = created_by;
    }

    public FileManagement(byte[] data, String name, long ID) {
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

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
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
    
    public void setFile_type(String file_type){
        this.file_type = FileType.valueOf(file_type);
    }

    public String getSigningProperties() {
        return signingProperties;
    }

    public void setSigningProperties(String signingProperties) {
        this.signingProperties = signingProperties;
    }
    
    public static void main(String[] args) throws JsonProcessingException{
        FileManagement a = new FileManagement();
        a.setName("hello");
        a.setWidth(100);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(a));
    }
    
    
}
