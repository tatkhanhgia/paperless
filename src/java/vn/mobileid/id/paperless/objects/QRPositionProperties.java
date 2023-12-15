/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author GiaTK
 */
public class QRPositionProperties {
    private float x;
    private float y;
    private boolean isTransparent;
    private List<Integer> pages;

    public QRPositionProperties() {
    }

    @JsonProperty("x_coordinate")
    public float getX() {
        return x;
    }

    @JsonProperty("y_coordinate")
    public float getY() {
        return y;
    } 

    @JsonProperty("transparent")
    public boolean isIsTransparent() {
        return isTransparent;
    }

    @JsonProperty("pages")
    public List<Integer> getPages() {
        return pages;
    }
    
    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public void setIsTransparent(boolean isTransparent) {
        this.isTransparent = isTransparent;
    }
}
