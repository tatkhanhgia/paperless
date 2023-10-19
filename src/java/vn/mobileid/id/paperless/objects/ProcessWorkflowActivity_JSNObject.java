/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.List;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessWorkflowActivity_JSNObject {

    private List<ItemDetails> item;
    private List<FileDataDetails> file_data;
    private FrameSignatureProperties signing_properties;

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

    @JsonProperty("metadata_signature")
    public FrameSignatureProperties getSigning_properties() {
        return signing_properties;
    }      

    public void setItem(List<ItemDetails> item) {
        this.item = item;
    }

    public void setFile_data(List<FileDataDetails> file_data) {
        this.file_data = file_data;
    }

    public void setSigning_properties(FrameSignatureProperties signing_properties) {
        this.signing_properties = signing_properties;
    }
    
    

    public static void main(String[] args) throws JsonProcessingException {
//        String body = "{\"metadata_signature\":{" +
//"\"location\":\"Quận 100\"," +
//"\"reason\":\"hello\"," +
//"\"date\":\"date\"," +
//"\"keyword\":\"word\"," +
//"\"box_coordinate\":\"bos\"" +
//"}}";
//        System.out.println("bod"+body);
//        ProcessWorkflowActivity_JSNObject object = new ObjectMapper().readValue(body, ProcessWorkflowActivity_JSNObject.class);
//        System.out.println(object.getSigningProperties().getLocation());
////        FrameSignatureProperties sig = new ObjectMapper().readValue(body, FrameSignatureProperties.class);
//        System.out.println(object.getSigningProperties());

        FrameSignatureProperties a = new FrameSignatureProperties();
        a.setLocation("hello");
        a.setKeyword("asa");
        String temp = new ObjectMapper().enable(SerializationFeature.WRAP_ROOT_VALUE).writeValueAsString(a);
        System.out.println(temp);
        
        ProcessWorkflowActivity_JSNObject object = new ObjectMapper().readValue(temp, ProcessWorkflowActivity_JSNObject.class);
        System.out.println(object.getSigning_properties().getLocation());
        
    }
}
