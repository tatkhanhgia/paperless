/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import vn.mobileid.id.general.annotation.AnnotationORM;

/**
 *
 * @author GiaTK
 */
public class CSVTask extends DatabaseDefaultObject{
    @AnnotationORM(columnName = "ID")
    private long id;
    
    @AnnotationORM(columnName = "UUID")
    private String uuid;
    
    @AnnotationORM(columnName = "DMS_PROPERTY")
    private String dms;
    
    @AnnotationORM(columnName = "NAME")
    private String name;
    
    @AnnotationORM(columnName = "PAGES")
    private int pages;
    
    @AnnotationORM(columnName = "SIZE")
    private long size;
    
    @AnnotationORM(columnName = "STATUS")
    private int status;
    
    @AnnotationORM(columnName = "BINARY_DATA")
    private byte[] binary_data;
    
    @AnnotationORM(columnName = "META_DATA")
    private String meta_data;
    
    @AnnotationORM(columnName = "HMAC")
    private String hmac;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDms() {
        return dms;
    }

    public void setDms(String dms) {
        this.dms = dms;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public byte[] getBinary_data() {
        return binary_data;
    }

    public void setBinary_data(byte[] binary_data) {
        this.binary_data = binary_data;
    }

    public String getMeta_data() {
        return meta_data;
    }

    public void setMeta_data(String meta_data) {
        this.meta_data = meta_data;
    }

    public String getHmac() {
        return hmac;
    }

    public void setHmac(String hmac) {
        this.hmac = hmac;
    }
    
    
}
