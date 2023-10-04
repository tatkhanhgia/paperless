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
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.annotation.AnnotationORM;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class WorkflowAttributeType {

    @AnnotationORM(columnName = "ID")
    private int id;

    @AnnotationORM(columnName = "ENABLED")
    private boolean enabled;

    @AnnotationORM(columnName = "NAME")
    private String name;

    @AnnotationORM(columnName = "REQUIRED")
    private boolean required;

    @AnnotationORM(columnName = "VALUE")
    private Object value;

    public WorkflowAttributeType() {
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("enabled")
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("required")
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public Object clone() {
        WorkflowAttributeType temp = new WorkflowAttributeType();
        temp.setEnabled(this.enabled);
        temp.setId(this.id);
        temp.setName(this.name);
        temp.setRequired(this.required);
        return temp;
    }

    public static List<WorkflowAttributeType> castTo(String json) throws Exception {
        List<WorkflowAttributeType> list = new ArrayList<>();
        Resources.reloadListWorkflowDetailAttributeTypes();
        for (String attributeName : Resources.getListWorkflowAttributeType().keySet()) {
            Object value = Utils.getFromJson_(attributeName, json);
            if (value != null) {
                WorkflowAttributeType temp = (WorkflowAttributeType) Resources.getListWorkflowAttributeType().get(attributeName).clone();
                temp.setValue(value);
                list.add(temp);
            }
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
        String temp = "{\"qr_background\":\"White\",\"qr_size\":4,\"qr_type\":\"Extended Data Code\",\"asset_template\":21}";

        List<WorkflowAttributeType> list = WorkflowAttributeType.castTo(temp);
        String temppp = "";
    }
}
