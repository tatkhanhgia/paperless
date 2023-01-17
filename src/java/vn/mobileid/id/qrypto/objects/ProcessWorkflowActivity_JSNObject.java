/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.qrypto.kernel.CreateWorkflow;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessWorkflowActivity_JSNObject {
    private List<ItemDetails> item;    
    private List<FileDataDetails> file_data;

    public ProcessWorkflowActivity_JSNObject() {
    }

    @JsonProperty("items")
    public List<ItemDetails> getItem() {
        return item;
    }

    @JsonProperty("file_data")
    public List<FileDataDetails> getFile_data() {
        return file_data;
    }
    
    
}
