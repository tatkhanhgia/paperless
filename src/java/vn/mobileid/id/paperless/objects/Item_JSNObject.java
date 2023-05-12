/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

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
public class Item_JSNObject {
    private List<ItemDetails> items;

    @JsonProperty("items")
    public List<ItemDetails> getItems() {
        return items;
    }      

    public void setItems(List<ItemDetails> items) {
        this.items = items;
    }
    
    public void appendData(ItemDetails item){
        if (items == null){
            items = new ArrayList<>();
        }
        items.add(item);
    }
}
