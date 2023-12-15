/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import vn.mobileid.id.general.annotation.AnnotationORM;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonRootName("Assets")
public class Asset extends DatabaseDefaultObject{
        
    @AnnotationORM(columnName="ID")
    private int id;
    
    @AnnotationORM(columnName="FILE_NAME")
    private String name;
    
    @AnnotationORM(columnName="TYPE")
    private int type;
    
    @AnnotationORM(columnName="ASSET_TYPE_NAME")
    private String type_name;
    
    @AnnotationORM(columnName="SIZE")
    private long size;
    
    @AnnotationORM(columnName="UUID")
    private String file_uuid;
    
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
    
    @AnnotationORM(columnName = "DEFAULT_ENABLED")
    private boolean isDefault;
    
    @AnnotationORM(columnName = "REMARK_EN")
    private String type_name_en;
    
    @AnnotationORM(columnName = "REMARK")
    private String type_name_vn;

    public Asset() {
    }    
    
    public Asset(int id, String name, int type, long size, String file_uuid, Date created_at, String created_by, Date modified_at, String modified_by, String used_by) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.size = size;
        this.file_uuid = file_uuid;
        super.setCreated_at(created_at);
        super.setCreated_by(created_by);
        super.setModified_at(modified_at);
        super.setModified_by(modified_by);        
        this.used_by = used_by;
    }   

    public Asset(int id, String name, int type, long size, String file_uuid, Date created_at, String created_by, Date modified_at, String modified_by, String used_by, byte[] data, String metadata) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.size = size;
        this.file_uuid = file_uuid;
        super.setCreated_at(created_at);
        super.setCreated_by(created_by);
        super.setModified_at(modified_at);
        super.setModified_by(modified_by);
        this.used_by = used_by;
        this.binaryData = data;
        this.metadata = metadata;
    }        
    
    //=================================GET======================================
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

    @JsonProperty("used_by")
    public String getUsed_by() {
        return used_by;
    }

    public String getMetadata() {
        return metadata;
    }
    
    public String getDbms() {
        return dbms;
    }
    
    public String getHmac() {
        return hmac;
    }
    
    @JsonProperty("file")
    public String getBase64() {
        return base64;
    }
    
    @JsonProperty("type_name")
    public String getType_name() {
        return type_name;
    }

    public byte[] getBinaryData() {
        return binaryData;
    }
    
    public boolean IsDefault() {
        return isDefault;
    }

    @JsonProperty("type_name_en")
    public String getType_name_en() {
        return type_name_en;
    }

    @JsonProperty("type_name_vn")
    public String getType_name_vn() {
        return type_name_vn;
    }
    
    //=========================SET==============================================
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }        

    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }

    public void setDbms(String dbms) {
        this.dbms = dbms;
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

    public void setUsed_by(String used_by) {
        this.used_by = used_by;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }          

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void setType_name_en(String type_name_en) {
        this.type_name_en = type_name_en;
    }

    public void setType_name_vn(String type_name_vn) {
        this.type_name_vn = type_name_vn;
    }
    
    
    public static void main(String[] args) throws Exception{
        Asset asset = new Asset();
        asset.setId(2);
        asset.setName("Background for Course.pdf");
        asset.setSize(241204);
        asset.setType(1);
        asset.setIsDefault(true);
        asset.setBase64(Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\bằng.pdf"))));
        
        String json = new ObjectMapper().writeValueAsString(asset);
        System.out.println("Json:"+json);
    }
}
