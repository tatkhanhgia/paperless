/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Mobile 32
 */
@JsonIgnoreProperties("error")
public class QryptoErrorMessageJSNObject {    
    private String code;
    private String code_description;
    

    @JsonProperty("error")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("error_description")
    public String getCode_description() {
        return code_description;
    }

    public void setCode_description(String code_description) {
        this.code_description = code_description;
    }
}
