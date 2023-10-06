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
public class FrameSignatureProperties {
    private String location;    
    private String reason;
    private String date;
    private String date_format;
    private String keyword;
    private String boxCoordinate;
    private String textContent;
    private String page;        

    public FrameSignatureProperties() {
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("keyword")
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @JsonProperty("box_coordinate")
    public String getBoxCoordinate() {
        return boxCoordinate;
    }

    public void setBoxCoordinate(String boxCoordinate) {
        this.boxCoordinate = boxCoordinate;
    }

    @JsonProperty("text_content")
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    @JsonProperty("page")
    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    @JsonProperty("date_format")
    public String getFormat_date() {
        return date_format;
    }

    public void setFormat_date(String format_date) {
        this.date_format = format_date;
    }
   
    
}
