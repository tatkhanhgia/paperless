/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkflowDetail_Item_JSNObject {
    private List<WorkflowDetail_JSNObject> item;
//    private List<String> item;
    public WorkflowDetail_Item_JSNObject() {
    }

    @JsonProperty("items")
    public List<WorkflowDetail_JSNObject> getItem() {
        return item;
    }

    public void setItem(List<WorkflowDetail_JSNObject> item) {
        this.item = item;
    }
    
//    @JsonProperty("item")
//    public List<String> getItem() {
//        return item;
//    }
//
//    public void setItem(List<String> item) {
//        this.item = item;
//    }
    
    
}
