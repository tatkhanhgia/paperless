/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author GiaTK
 */
public class Language {

    private String name;
    private String description;

        public Language(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public Language() {
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("description")
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    
    
        
    }
