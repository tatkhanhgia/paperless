/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;

/**
 *
 * @author GiaTK
 */
public class CustomWorkflowActivitySerializer extends JsonSerializer<WorkflowActivity> {

    @Override
    public void serialize(
            WorkflowActivity t,
            JsonGenerator jg,
            SerializerProvider sp) throws IOException {
        jg.writeStartObject();
        Field[] fields = WorkflowActivity.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if(field.getName().equals("file")){
                continue;
            }
            try {
                if (field.get(t) instanceof Boolean) {
                    jg.writeBooleanField(field.getName(), field.getBoolean(t));
                } 
                if (field.get(t) instanceof String) {
                    jg.writeStringField(field.getName(), (String) field.get(t));
                }
                if (field.get(t) instanceof Integer) {
                    jg.writeNumberField(field.getName(), field.getInt(t));
                }
                if (field.get(t) instanceof Date){
                    DateFormat date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ssa X");
                    jg.writeStringField(field.getName(), date.format(((Date)field.get(t)).getTime()));
                }
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(CustomWorkflowActivitySerializer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(CustomWorkflowActivitySerializer.class.getName()).log(Level.SEVERE, null, ex);
            }          
        }
        jg.writeEndObject();
    }
}
