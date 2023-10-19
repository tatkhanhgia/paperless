/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessWorkflowActivity_CSV_JSNObject {

    private List<Buffer> item;

    public ProcessWorkflowActivity_CSV_JSNObject() {
    }

    @JsonProperty("items")
    public List<Buffer> getItem() {
        return item;
    }

    public void setItem(List<Buffer> item) {
        this.item = item;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Buffer {

        private List<ItemDetails> itemDetails;

        public Buffer() {
        }

        @JsonProperty("item")
        public List<ItemDetails> getItemDetails() {
            return itemDetails;
        }

        public void setItemDetails(List<ItemDetails> itemDetails) {
            this.itemDetails = itemDetails;
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        String body = "{\"items\":[{\"item\":[{\"field\":\"Name\",\"type\":1,\"value\":\"Gia nè\"},{\"field\":\"Age\",\"type\":3,\"value\":23},{\"field\":\"DateOfBirth\",\"type\":4,\"value\":\"31/12/2022\"}]},{\"item\":[{\"field\":\"Name\",\"type\":1,\"value\":\"Vinh nè\"},{\"field\":\"Age\",\"type\":3,\"value\":22},{\"field\":\"DateOfBirth\",\"type\":4,\"value\":\"31/12/2022\"}]}]}";
        ProcessWorkflowActivity_CSV_JSNObject csv = new ObjectMapper().readValue(body, ProcessWorkflowActivity_CSV_JSNObject.class);
        System.out.println(csv.getItem().get(0).getItemDetails().get(0).getField());
        System.out.println(csv.getItem().get(0).getItemDetails().get(1).getField());
    }
}
