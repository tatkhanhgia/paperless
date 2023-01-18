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
public class Document {

    private int document_id;
    private String name;
    private int pages;
    private long size;
    private int width;
    private int height;
    private String status;
    private String created_by;
    private String last_modified_by;

    public Document(int document_id, String name, int pages, long size, int width, int height, String status, String created_by, String last_modified_by) {
        this.document_id = document_id;
        this.name = name;
        this.pages = pages;
        this.size = size;
        this.width = width;
        this.height = height;
        this.status = status;
        this.created_by = created_by;
        this.last_modified_by = last_modified_by;
    }

    @JsonProperty("document_id")
    public int getDocument_id() {
        return document_id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("pages")
    public int getPages() {
        return pages;
    }

    @JsonProperty("size")
    public long getSize() {
        return size;
    }

    @JsonProperty("width")
    public int getWidth() {
        return width;
    }

    @JsonProperty("height")
    public int getHeight() {
        return height;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("created_by")
    public String getCreated_by() {
        return created_by;
    }

    @JsonProperty("last_modified_by")
    public String getLast_modified_by() {
        return last_modified_by;
    }

}
