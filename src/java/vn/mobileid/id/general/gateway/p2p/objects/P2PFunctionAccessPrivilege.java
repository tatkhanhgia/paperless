/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.gateway.p2p.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import vn.mobileid.id.general.objects.Attributes;

/**
 *
 * @author VUDP
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class P2PFunctionAccessPrivilege extends Attributes {

    private List<Attribute> attributes;

    @JsonProperty("attributes")
    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }
    
    @JsonIgnore
    public List<String> getFunctions() {
        List<String> functions = new ArrayList<>();
        if(!attributes.isEmpty()) {
            for(Attribute attribute: attributes) {
                functions.add(attribute.getFunction());
            }
        }
        return functions;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Attribute {

        private String function;
        private String remarkEn;
        private String remark;

        @JsonProperty("function")
        public String getFunction() {
            return function;
        }

        public void setFunction(String function) {
            this.function = function;
        }

        @JsonProperty("remarkEn")
        public String getRemarkEn() {
            return remarkEn;
        }

        public void setRemarkEn(String remarkEn) {
            this.remarkEn = remarkEn;
        }

        @JsonProperty("remark")
        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
