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
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.paperless.objects.WorkflowActivity;

/**
 *
 * @author GiaTK
 */
public class CustomWorkflowActivitySerializer extends JsonSerializer<WorkflowActivity> {

//    @Override
//    public void serialize(
//            WorkflowActivity t,
//            JsonGenerator jg,
//            SerializerProvider sp) throws IOException {
//        jg.writeStartObject();
//        Field[] fields = WorkflowActivity.class.getDeclaredFields();
//        for (Field field : fields) {
//            field.setAccessible(true);
//            if(field.getName().equals("file")){
//                continue;
//            }
//            try {
//                if (field.get(t) instanceof Boolean) {
//                    jg.writeBooleanField(field.getName(), field.getBoolean(t));
//                } 
//                if (field.get(t) instanceof String) {
//                    jg.writeStringField(field.getName(), (String) field.get(t));
//                }
//                if (field.get(t) instanceof Integer) {
//                    jg.writeNumberField(field.getName(), field.getInt(t));
//                }
//                if (field.get(t) instanceof Date){
//                    DateFormat date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ssa X");
//                    jg.writeStringField(field.getName(), date.format(((Date)field.get(t)).getTime()));
//                }
//            } catch (IllegalArgumentException ex) {
//                Logger.getLogger(CustomWorkflowActivitySerializer.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IllegalAccessException ex) {
//                Logger.getLogger(CustomWorkflowActivitySerializer.class.getName()).log(Level.SEVERE, null, ex);
//            }          
//        }
//        jg.writeEndObject();
//    }
    @Override
    public void serialize(
            WorkflowActivity woAc,
            JsonGenerator jg,
            SerializerProvider sp) throws IOException {
        jg.writeStartObject();
        DateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        jg.writeNumberField("workflow_activity_id", woAc.getId());
        jg.writeNumberField("workflow_id", woAc.getWorkflow_id());
        jg.writeStringField("workflow_label", woAc.getWorkflow_label());
        jg.writeNumberField("workflow_template_type", woAc.getWorkflow_template_type());
//        jg.writeStringField("workflow_template_type_vn", woAc.getWorkflow_template_type_name_vn());
//        jg.writeStringField("user_email", woAc.getUser_email());
        jg.writeNumberField("status", woAc.getStatusId());
        jg.writeStringField("status_name", woAc.getStatus_name());
        jg.writeStringField("status_name_vn", woAc.getStatus_name_vn());
        jg.writeStringField("transaction_id", woAc.getTransaction());
        jg.writeStringField("remark", woAc.getRemark());
        jg.writeBooleanField("use_test_token", woAc.use_test_token);
        jg.writeBooleanField("update_enable", woAc.update_enable);
        jg.writeStringField("generation_type_name", woAc.getGeneration_type_name());
        jg.writeStringField("generation_type_name_vn", woAc.getGeneration_type_name_vn());
            jg.writeStringField("created_at", woAc.getCreated_at()!=null?dateFormat.format(woAc.getCreated_at()):null);
        jg.writeStringField("created_by", woAc.getCreated_by());
        if (woAc.getModified_at() != null) {
            jg.writeStringField("modified_at", dateFormat.format(woAc.getModified_at()));
            jg.writeStringField("modified_by", woAc.getModified_by());
        }
        jg.writeStringField("download_link", woAc.getDowload_link());
        jg.writeStringField("download_link_type", woAc.getDownload_link_type().getType());
        jg.writeEndObject();
    }
}
