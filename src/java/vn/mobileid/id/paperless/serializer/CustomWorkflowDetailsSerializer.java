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
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.object.enumration.WorkflowAttributeTypeName;

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
            if(ob.getValue()==null){
                jg.writeNumberField(ob.getName(), 0);
            }
            if(ob.getValue() instanceof String){
                try{
                    int value = Integer.parseInt((String)ob.getValue());
                    if(WorkflowAttributeTypeName.getType(ob.getId()) instanceof Boolean){
                        jg.writeBooleanField(ob.getName(), value==1);
                        continue;
                    }
//                    if(value == 1 && ob.getId() != WorkflowAttributeTypeName.ASSET_APPEND.getId()
//                            && ob.getId() != WorkflowAttributeTypeName.ASSET_BACKGROUND.getId()
//                            && ob.getId() != WorkflowAttributeTypeName.ASSET_TEMPLATE.getId()
//                            && ob.getId() != WorkflowAttributeTypeName.PAGE.getId()
//                            && ob.getId() != WorkflowAttributeTypeName.X_COORDINATE.getId()
//                            && ob.getId() != WorkflowAttributeTypeName.Y_COORDINATE.getId()
//                            && ob.getId() != WorkflowAttributeTypeName.STAMP_IN.getId()){
//                        jg.writeBooleanField(ob.getName(), true);
//                        continue;
//                    }
//                    if(value == 0 
//                            && ob.getId() != WorkflowAttributeTypeName.ASSET_APPEND.getId()
//                            && ob.getId() != WorkflowAttributeTypeName.ASSET_BACKGROUND.getId()
//                            && ob.getId() != WorkflowAttributeTypeName.ASSET_TEMPLATE.getId()
//                            && ob.getId() != WorkflowAttributeTypeName.PAGE.getId()
//                            && ob.getId() != WorkflowAttributeTypeName.X_COORDINATE.getId()
//                            && ob.getId() != WorkflowAttributeTypeName.Y_COORDINATE.getId()
//                            && ob.getId() != WorkflowAttributeTypeName.STAMP_IN.getId()){
//                        jg.writeBooleanField(ob.getName(), false);
//                        continue;
//                    }      
                    jg.writeNumberField(ob.getName(), value);
                    continue;
                }catch(Exception ex){}
            }
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
