/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDetails {
    private String field  ;
    private boolean mandatory_enable;
    private int type;
    private Object value;
    private String file_field;
    private String file_format;
    private String remark;
    private String group;

    public ItemDetails() {
    }

    @JsonProperty("file_format")
    public String getFile_format() {
        return file_format;
    }

    public void setFile_format(String file_format) {
        this.file_format = file_format;
    }   
    
    @JsonProperty("file_field")
    public String getFile_field() {
        return file_field;
    }

    public void setFile_field(String file_field) {
        this.file_field = file_field;
    }        
    
    @JsonProperty("mandatory_enable")
    public boolean isMandatory_enable() {
        return mandatory_enable;
    }

    public void setMandatory_enable(boolean mandatory_enable) {
        this.mandatory_enable = mandatory_enable;
    }

    @JsonProperty("field")
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @JsonProperty("type")
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @JsonProperty("value")
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public static Object checkType(int i){
        if(i == 1)
            return "String";
        if( i == 2)
            return true;
        if( i ==3 )
            return 1;
        if( i == 4)
            return new Date();
        if( i== 5)
            return new byte[1];
        return null;
    }

    @JsonProperty("remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @JsonProperty("group")
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
    
    
}
