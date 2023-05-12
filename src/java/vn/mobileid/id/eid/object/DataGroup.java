/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.eid.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataGroup {
    private String dg1;
    private String dg2;
    private String dg3;
    private String dg13;

    @JsonProperty("dg1")
    public String getDg1() {
        return dg1;
    }

    public void setDg1(String dg1) {
        this.dg1 = dg1;
    }

    @JsonProperty("dg2")
    public String getDg2() {
        return dg2;
    }

    public void setDg2(String dg2) {
        this.dg2 = dg2;
    }

    @JsonProperty("dg3")
    public String getDg3() {
        return dg3;
    }

    public void setDg3(String dg3) {
        this.dg3 = dg3;
    }

    @JsonProperty("dg13")
    public String getDg13() {
        return dg13;
    }

    public void setDg13(String dg13) {
        this.dg13 = dg13;
    }
    
    
}
