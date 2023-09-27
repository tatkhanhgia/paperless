/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import vn.mobileid.id.paperless.objects.ItemDetails;
import vn.mobileid.id.paperless.objects.Item_JSNObject;
import vn.mobileid.id.paperless.objects.WorkflowTemplate;

/**
 *
 * @author GiaTK
 */
public class CustomWorkflowTemplateSerializer implements  JsonSerializable {
    private WorkflowTemplate t;
    
    public CustomWorkflowTemplateSerializer(WorkflowTemplate t) {
        this.t = t;
    }
        
    @Override
    public void serialize(               
            JsonGenerator jg,
            SerializerProvider sp) throws IOException {
        jg.writeStartObject();
        jg.writeNumberField("workflow_template_type_id", t.getType_id());        
        Item_JSNObject item = new ObjectMapper().readValue(t.getMeta_data_template(), Item_JSNObject.class);
        jg.writeFieldName("items");
        jg.writeStartArray();
        for(ItemDetails detail : item.getItems()){
            jg.writeStartObject();
                jg.writeStringField("field", detail.getField());
                jg.writeBooleanField("mandatory_enable", detail.isMandatory_enable());
                jg.writeNumberField("type", detail.getType());
                if(detail.getValue() instanceof String){
                    jg.writeStringField("value",(String) detail.getValue());
                }                 
                if(detail.getRemark() != null){
                    jg.writeStringField("remark",detail.getRemark());
                }
            jg.writeEndObject();
        }
        jg.writeEndArray();
        jg.writeEndObject();
    }

    @Override
    public void serializeWithType(JsonGenerator jg, SerializerProvider sp, TypeSerializer ts) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }



}
