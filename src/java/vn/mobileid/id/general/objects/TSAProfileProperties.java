/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;

/**
 *
 * @author ADMIN
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TSAProfileProperties {
    private HashMap<String,String> provider;

    @JsonProperty("provider")
    public HashMap<String, String> getProvider() {
        return provider;
    }

    public void setProvider(HashMap<String, String> provider) {
        this.provider = provider;
    }
    
}
