/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

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

    public ItemDetails() {
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
        return null;
    }
}
