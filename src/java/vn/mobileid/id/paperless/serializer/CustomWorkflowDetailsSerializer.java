/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;
import vn.mobileid.id.utils.PolicyConfiguration;

/**
 *
 * @author GiaTK
 */
public class CustomWorkflowDetailsSerializer implements JsonSerializable {
    
    private List<WorkflowAttributeType> listAttributeType;

    public CustomWorkflowDetailsSerializer(List<WorkflowAttributeType> list) {        
        this.listAttributeType = list;
    }

    @Override
    public void serialize(JsonGenerator jg, SerializerProvider sp) throws IOException {
//        String rootname = sp.getConfig().findRootName(Asset.class).getSimpleName();
        jg.writeStartObject();
        jg.writeFieldName("workflow_option");
        DateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        jg.writeStartObject();
        for (WorkflowAttributeType ob : listAttributeType) {
            
            if(ob.getValue() instanceof String){
                jg.writeStringField(ob.getName(), (String)ob.getValue());
            }
            if(ob.getValue() instanceof Integer){
                jg.writeNumberField(ob.getName(), (int)ob.getValue());
            }
            if(ob.getValue() instanceof Long){
                jg.writeNumberField(ob.getName(), (long)ob.getValue());
            }
            if(ob.getValue() instanceof Boolean){
                jg.writeBooleanField(ob.getName(),(boolean)ob.getValue());
            }
            
        }
        jg.writeEndObject();
        jg.writeEndObject();
    }

    @Override
    public void serializeWithType(JsonGenerator jg, SerializerProvider sp, TypeSerializer ts) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
