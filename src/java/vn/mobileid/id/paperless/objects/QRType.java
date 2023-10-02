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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QRType {
    @AnnotationORM(columnName = "ID")
    private long id;
    
    @AnnotationORM(columnName = "QR_TYPE_NAME")
    private String qr_type_name;
    
    @AnnotationORM(columnName = "CODE")
    private String code;

    public QRType() {
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setQr_type_name(String qr_type_name) {
        this.qr_type_name = qr_type_name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("id")
    public long getId() {
        return id;
    }

    @JsonProperty("qr_type_name")
    public String getQr_type_name() {
        return qr_type_name;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }
    
    
    
}
