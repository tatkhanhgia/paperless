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
public class DataSignatureProperties {

    //Type
    private String type;

    private String location;
    private String reason;
    private String date_format;
    private String textContent;
    private int fontSize;

    public DataSignatureProperties() {
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }

    @JsonProperty("date_format")
    public String getDate_format() {
        return date_format;
    }

    @JsonProperty("text_content")
    public String getTextContent() {
        return textContent;
    }

    @JsonProperty("font_size")
    public int getFontSize() {
        return fontSize;
    }

    //============================
    
    public void setType(String type) {
        this.type = type;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setDate_format(String date_format) {
        this.date_format = date_format;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
}
