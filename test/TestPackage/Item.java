/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;

/**
 *
 * @author GiaTK
 */
public class Item {
    private List<Language> item;

    public Item(List<Language> item) {
        this.item = item;
    }

    public Item() {
    }

    @JsonProperty("item")
    public List<Language> getItem() {
        return item;
    }

    public void setItem(List<Language> item) {
        this.item = item;
    }
    
    
}
