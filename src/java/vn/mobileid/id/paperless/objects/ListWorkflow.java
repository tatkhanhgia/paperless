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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ListWorkflow {
    private List<Workflow> listWorkflow;

    @JsonProperty("workflows")
    public List<Workflow> getListWorkflow() {
        return listWorkflow;
    }

    public void setListWorkflow(List<Workflow> listWorkflow) {
        this.listWorkflow = listWorkflow;
    }
    
    
}
