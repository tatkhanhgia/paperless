/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDataDetails {

    private int file_type;
    private Object value;

    public FileDataDetails() {
    }

    @JsonProperty("file_type")
    public int getFile_type() {
        return file_type;
    }

    @JsonProperty("value")
    public Object getValue() {
        return value;
    }

    public void setFile_type(int file_type) {
        this.file_type = file_type;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
