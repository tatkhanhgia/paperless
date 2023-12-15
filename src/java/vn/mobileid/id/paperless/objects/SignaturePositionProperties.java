/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignaturePositionProperties {
    //Identity which Object
    private String type_Name;
    
    //Frame Signature
    private String date;
    private String keyword;
    private String boxCoordinate;
    private String page;  
    private int fontSize;

    public SignaturePositionProperties() {
    }

    @JsonProperty("type")
    public String getType_Name() {
        return type_Name;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("keyword")
    public String getKeyword() {
        return keyword;
    }

    @JsonProperty("box_coordinate")
    public String getBoxCoordinate() {
        return boxCoordinate;
    }

    @JsonProperty("page")
    public String getPage() {
        return page;
    }

    @JsonProperty("font_size")
    public int getFontSize() {
        return fontSize;
    }
    
    //=====================

    public void setType_Name(String type_Name) {
        this.type_Name = type_Name;
    }
    
    public void setDate(String date) {
        this.date = date;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setBoxCoordinate(String boxCoordinate) {
        this.boxCoordinate = boxCoordinate;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
    
    public static void main(String[] args) throws Exception{
         SignaturePositionProperties sig = new SignaturePositionProperties();
         sig.setType_Name("WORKER");
         sig.setBoxCoordinate("-20,-140,200,125");
         sig.setPage("2");
         sig.setKeyword("NGƯỜI LAO ĐỘNG");
         System.out.println(new ObjectMapper().writeValueAsString(sig));
    }
}
