/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Asset {
    private int id;
    private String name;
    private int type;
    private long size;
    private int file_uuid;
    private String created_at;
    private String created_by;
    private String modified_at;
    private String modified_by;
    private String used_by;

    public Asset(int id, String name, int type, long size, int file_uuid, String created_at, String created_by, String modified_at, String modified_by, String used_by) {
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
    public int getFile_uuid() {
        return file_uuid;
    }

    @JsonProperty("created_at")
    public String getCreated_at() {
        return created_at;
    }

    @JsonProperty("created_by")
    public String getCreated_by() {
        return created_by;
    }

    @JsonProperty("modified_at")
    public String getModified_at() {
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
    
    
}
