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
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import vn.mobileid.id.paperless.objects.Workflow;
import vn.mobileid.id.utils.PolicyConfiguration;

/**
 *
 * @author GiaTK
 */
public class CustomListWorkflowSerializer implements JsonSerializable {

    private int page;
    private int record_per_page;
    private List<Workflow> listWo;

    public CustomListWorkflowSerializer(List<Workflow> list, int page, int record_per_page) {
        this.page = page;
        this.record_per_page = record_per_page;
        this.listWo = list;
    }

    @Override
    public void serialize(JsonGenerator jg, SerializerProvider sp) throws IOException {
//        String rootname = sp.getConfig().findRootName(Asset.class).getSimpleName();
        jg.writeStartObject();
        jg.writeFieldName("workflows");
        jg.writeStartArray();
        DateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        for (Workflow workflow : listWo) {
            jg.writeStartObject();
            jg.writeNumberField("workflow_id", workflow.getWorkflow_id());            
            jg.writeStringField("workflow_label", workflow.getLabel());
            jg.writeStringField("workflow_type_name", workflow.getWorkflow_type_name());   
            jg.writeStringField("workflow_template_type_name", workflow.getWorkflowTemplate_type_name());   
            jg.writeStringField("workflow_template_type_name_en", workflow.getWorkflowTemplate_type_name_en());   
            jg.writeNumberField("workflow_template_type_id", workflow.getWorkflowTemplate_type());
            jg.writeStringField("status", (workflow.getStatus()==1?"ACTIVE":"INACTIVE"));                       
            jg.writeStringField("note",workflow.getNote());
            jg.writeStringField("metadata", workflow.getMetadata());
            jg.writeStringField("created_at", dateFormat.format(workflow.getCreated_at()));
            jg.writeStringField("created_by", workflow.getCreated_by());
            if (workflow.getLast_modified_at() != null) {
                jg.writeStringField("modified_at", dateFormat.format(workflow.getLast_modified_at()));
                jg.writeStringField("modified_by", workflow.getLast_modified_by());
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
