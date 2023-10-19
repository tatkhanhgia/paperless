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
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.paperless.objects.UserActivity;

/**
 *
 * @author GiaTK
 */
public class CustomListUserActivitySerializer implements JsonSerializable {

    private int page;
    private int record_per_page;
    private List<UserActivity> listUsAc;

    public CustomListUserActivitySerializer(List<UserActivity> list, int page, int record_per_page) {
        this.page = page;
        this.record_per_page = record_per_page;
        this.listUsAc = list;
    }

    @Override
    public void serialize(JsonGenerator jg, SerializerProvider sp) throws IOException {
//        String rootname = sp.getConfig().findRootName(Asset.class).getSimpleName();
        jg.writeStartObject();
        jg.writeFieldName("user_activities");
        jg.writeStartArray();
        DateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        for (UserActivity usAc : listUsAc) {
            jg.writeStartObject();
            jg.writeNumberField("user_activity_id", usAc.getId());
            jg.writeStringField("transaction_id", usAc.getTransaction_id());
            jg.writeNumberField("log_id", usAc.getLog_id());
            jg.writeNumberField("category_id", usAc.getCategory_id());
            jg.writeStringField("category_name", Resources.getListCategory().get(usAc.getCategory_id()).getRemark());
            jg.writeStringField("category_name_vn", Resources.getListCategory().get(usAc.getCategory_id()).getRemark_vn());
            jg.writeNumberField("user_activity_event_id", usAc.getUser_activity_event());
            jg.writeStringField("user_activity_event_name", Resources.getListEventAction().get(usAc.getUser_activity_event()).getRemark_en());
            jg.writeStringField("user_activity_event_name_vn", Resources.getListEventAction().get(usAc.getUser_activity_event()).getRemark());
            jg.writeStringField("created_at", usAc.getCreated_at()!=null?dateFormat.format(usAc.getCreated_at()):null);
            jg.writeStringField("created_by", usAc.getCreated_by());
            if (usAc.getModified_at() != null) {
                jg.writeStringField("modified_at", dateFormat.format(usAc.getModified_at()));
                jg.writeStringField("modified_by", usAc.getModified_by());
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
