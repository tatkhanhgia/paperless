/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QRSize {
    private String qr_size_name;
    private int size;

    public QRSize(String qr_size_name, int size) {
        this.qr_size_name = qr_size_name;
        this.size = size;
    }

    public QRSize() {
    }

    @JsonProperty("qr_size_name")
    public String getQr_size_name() {
        return qr_size_name;
    }

    public void setQr_size_name(String qr_size_name) {
        this.qr_size_name = qr_size_name;
    }

    @JsonProperty("size")
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    
}
