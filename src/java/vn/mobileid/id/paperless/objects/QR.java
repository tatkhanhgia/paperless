/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import vn.mobileid.id.general.annotation.AnnotationORM;
import vn.mobileid.id.paperless.serializer.CustomFileManagementSerializer;
import vn.mobileid.id.paperless.serializer.CustomQRSerializer;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonSerialize(using = CustomQRSerializer.class)
public class QR extends DatabaseDefaultObject{
    
    @AnnotationORM(columnName = "ID")
    private long id;
    
    @AnnotationORM(columnName = "META_DATA")
    private String meta_data;
    
    @AnnotationORM(columnName = "IMAGE")
    private String image;

    public QR() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMeta_data() {
        return meta_data;
    }

    public void setMeta_data(String meta_data) {
        this.meta_data = meta_data;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    
    
}
