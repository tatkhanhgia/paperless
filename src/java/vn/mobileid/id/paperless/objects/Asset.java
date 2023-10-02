/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.Date;
import vn.mobileid.id.general.annotation.AnnotationORM;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonRootName("Assets")
public class Asset {
        
    @AnnotationORM(columnName="ID")
    private int id;
    
    @AnnotationORM(columnName="FILE_NAME")
    private String name;
    
    @AnnotationORM(columnName="TYPE")
    private int type;
    
    private String type_name;
    
    @AnnotationORM(columnName="SIZE")
    private long size;
    
    @AnnotationORM(columnName="UUID")
    private String file_uuid;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    @AnnotationORM(columnName="CREATED_AT")
    private Date created_at;
    
    @AnnotationORM(columnName="CREATED_BY")
    private String created_by;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    @AnnotationORM(columnName="LAST_MODIFIED_AT")
    private Date modified_at;
    
    @AnnotationORM(columnName="LAST_MODIFIED_BY")
    private String modified_by;
    
    @AnnotationORM(columnName="USED_BY")
    private String used_by;
    
    @JsonIgnore
    @AnnotationORM(columnName="BINARY_DATA")
    private byte[] binaryData;
    
    @AnnotationORM(columnName="META_DATA")
    private String metadata;
    
    @AnnotationORM(columnName="DMS_PROPERTY")
    private String dbms;
    
    @AnnotationORM(columnName="HMAC")
    private String hmac;
    
    private String base64;

    public Asset() {
    }    
    
    public Asset(int id, String name, int type, long size, String file_uuid, Date created_at, String created_by, Date modified_at, String modified_by, String used_by) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.size = size;
        this.file_uuid = file_uuid;
        this.created_at = created_at;
        this.created_by = created_by;
        this.modified_at = modified_at;
        this.modified_by = modified_by;
        this.used_by = used_by;
    }   

    public Asset(int id, String name, int type, long size, String file_uuid, Date created_at, String created_by, Date modified_at, String modified_by, String used_by, byte[] data, String metadata) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.size = size;
        this.file_uuid = file_uuid;
        this.created_at = created_at;
        this.created_by = created_by;
        this.modified_at = modified_at;
        this.modified_by = modified_by;
        this.used_by = used_by;
        this.binaryData = data;
        this.metadata = metadata;
    }        
    
    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("type")
    public int getType() {
        return type;
    }

    @JsonProperty("size")
    public long getSize() {
        return size;
    }

//    @JsonProperty("file_uuid")
    public String getFile_uuid() {
        return file_uuid;
    }

    @JsonProperty("created_at")
    public Date getCreated_at() {
        return created_at;
    }

    @JsonProperty("created_by")
    public String getCreated_by() {
        return created_by;
    }

    @JsonProperty("modified_at")
    public Date getModified_at() {
        return modified_at;
    }

    @JsonProperty("modified_by")
    public String getModified_by() {
        return modified_by;
    }

    @JsonProperty("used_by")
    public String getUsed_by() {
        return used_by;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }        

    public byte[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }

    public String getDbms() {
        return dbms;
    }

    public void setDbms(String dbms) {
        this.dbms = dbms;
    }

    public String getHmac() {
        return hmac;
    }

    public void setHmac(String hmac) {
        this.hmac = hmac;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setFile_uuid(String file_uuid) {
        this.file_uuid = file_uuid;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public void setModified_at(Date modified_at) {
        this.modified_at = modified_at;
    }

    public void setModified_by(String modified_by) {
        this.modified_by = modified_by;
    }

    public void setUsed_by(String used_by) {
        this.used_by = used_by;
    }

    @JsonProperty("file")
    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    @JsonProperty("type_name")
    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }            
}
