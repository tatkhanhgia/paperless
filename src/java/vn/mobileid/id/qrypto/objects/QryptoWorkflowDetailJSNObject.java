/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QryptoWorkflowDetailJSNObject {
    private List<String> c ;
//    private String c;
    private boolean mandatory_enable;
//    private String mandatory_enable;
    private String t;

    public QryptoWorkflowDetailJSNObject() {
    }

    @JsonProperty("c")
    public List getC() {
        return c;
    }

    public void setC(List c) {
        this.c = c;
    }

    @JsonProperty("mandatory_enable")
    public boolean isMandatory_enable() {
        return mandatory_enable;
    }

    public void setMandatory_enable(boolean mandatory_enable) {
        this.mandatory_enable = mandatory_enable;
    }

    @JsonProperty("t")
    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

//    @JsonProperty("c")
//    public String getC() {
//        return c;
//    }
//
//    public void setC(String c) {
//        this.c = c;
//    }
//
//    @JsonProperty("mandatory_enable")
//    public String getMandatory_enable() {
//        return mandatory_enable;
//    }
//
//    public void setMandatory_enable(String mandatory_enable) {
//        this.mandatory_enable = mandatory_enable;
//    }
}
