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
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;

/**
 *
 * @author GiaTK
 */
public class CustomWorkflowDetailSerializer extends JsonSerializer<WorkflowDetail_Option> {

    @Override
    public void serialize(
            WorkflowDetail_Option t,
            JsonGenerator jg,
            SerializerProvider sp) throws IOException {
        jg.writeStartObject();
        Field[] fields = WorkflowDetail_Option.class.getDeclaredFields();
        int flag = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            if (flag == 2) {
                flag = 0;
                continue;
            }
            if (flag == 1) {
                try {
                    if (field.get(t) instanceof Boolean) {
                        jg.writeBooleanField(field.getName(), field.getBoolean(t));
                    } else {
                        jg.writeNumberField(field.getName(), field.getInt(t));
                    }
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(CustomWorkflowDetailSerializer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(CustomWorkflowDetailSerializer.class.getName()).log(Level.SEVERE, null, ex);
                }
                flag = 0;
                continue;
            }
            if (field.getName().endsWith("string")) {
                try {
                    if (((String) field.get(t)) == null) {
                        flag = 2;
                        continue;
                    }
                } catch (Exception ex) {
                    continue;
                }
                flag = 1;
                continue;
            }
            try {
                if (field.get(t) instanceof String) {
                    jg.writeStringField(field.getName(), (String) field.get(t));
                }
                if (field.get(t) instanceof Integer) {
                    jg.writeNumberField(field.getName(), field.getInt(t));
                }
            } catch (Exception ex) {

            }
        }
        jg.writeEndObject();
    }

}
