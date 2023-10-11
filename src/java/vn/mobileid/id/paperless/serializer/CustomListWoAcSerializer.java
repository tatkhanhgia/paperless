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
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.paperless.object.enumration.DownloadLinkType;
import vn.mobileid.id.paperless.objects.WorkflowActivity;

/**
 *
 * @author GiaTK
 */
public class CustomListWoAcSerializer implements JsonSerializable {

    private int page;
    private int record_per_page;
    private List<WorkflowActivity> listWoAc;

    public CustomListWoAcSerializer(List<WorkflowActivity> list, int page, int record_per_page) {
        this.page = page;
        this.record_per_page = record_per_page;
        this.listWoAc = list;
    }

    @Override
    public void serialize(JsonGenerator jg, SerializerProvider sp) throws IOException {
//        String rootname = sp.getConfig().findRootName(Asset.class).getSimpleName();
        jg.writeStartObject();
        jg.writeFieldName("workflow_activities");
        jg.writeStartArray();
        DateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        for (WorkflowActivity woAc : listWoAc) {
            jg.writeStartObject();
            jg.writeNumberField("workflow_activity_id", woAc.getId());
            jg.writeNumberField("workflow_id", woAc.getWorkflow_id());
            jg.writeStringField("workflow_label", woAc.getWorkflow_label());
            jg.writeNumberField("workflow_template_type", woAc.getWorkflow_template_type());
//            jg.writeStringField("user_email", woAc.getUser_email());
            jg.writeNumberField("status", woAc.getStatusId());
            jg.writeStringField("status_name", woAc.getStatus_name());
            jg.writeStringField("status_name_vn", woAc.getStatus_name_vn());
            jg.writeStringField("transaction_id", woAc.getTransaction());
            jg.writeStringField("remark", woAc.getRemark());
            jg.writeBooleanField("use_test_token", woAc.use_test_token);
            jg.writeBooleanField("update_enable", woAc.update_enable);
//            jg.writeNumberField("generation_type", woAc.getGeneration_type());
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
        jg.writeEndArray();
        jg.writeEndObject();
    }

    @Override
    public void serializeWithType(JsonGenerator jg, SerializerProvider sp, TypeSerializer ts) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
