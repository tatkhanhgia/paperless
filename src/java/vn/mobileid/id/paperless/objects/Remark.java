/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import vn.mobileid.id.general.annotation.AnnotationORM;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Remark {
    @AnnotationORM(columnName = "REMARK")
    public  String remark;
    
    @AnnotationORM(columnName = "REMARK_EN")
    public  String remark_en;

    public Remark() {
    }

    @JsonProperty("remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @JsonProperty("remark_en")
    public String getRemark_en() {
        return remark_en;
    }

    public void setRemark_en(String remark_en) {
        this.remark_en = remark_en;
    }
}
