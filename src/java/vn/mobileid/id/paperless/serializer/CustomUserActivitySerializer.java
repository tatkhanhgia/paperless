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
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.paperless.objects.UserActivityLog;

/**
 *
 * @author GiaTK
 */
public class CustomUserActivitySerializer implements JsonSerializable {

    private UserActivityLog usAc;

    public CustomUserActivitySerializer(UserActivityLog list) {
        this.usAc = list;
    }

    @Override
    public void serialize(JsonGenerator jg, SerializerProvider sp) throws IOException {
//        String rootname = sp.getConfig().findRootName(Asset.class).getSimpleName();       
        DateFormat dateFormat = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        jg.writeStartObject();
        jg.writeNumberField("user_activity_log_id", usAc.getId());
        jg.writeStringField("module",usAc.getModule());
        jg.writeStringField("action",usAc.getAction());
        jg.writeStringField("info_key",usAc.getInfo_key());
        jg.writeStringField("info_value",usAc.getInfo_value());
        jg.writeStringField("detail",usAc.getDetail());
        jg.writeStringField("agent",usAc.getAgent());
        jg.writeStringField("agent_detail",usAc.getAgent_detail());
        jg.writeStringField("ip_address",usAc.getIp_address());
        jg.writeStringField("description",usAc.getDescription());
        jg.writeStringField("meta_data",usAc.getMetadata());
        jg.writeStringField("created_at", usAc.getCreated_at() != null ? dateFormat.format(usAc.getCreated_at()) : null);
        jg.writeStringField("created_by", usAc.getCreated_by());
        if (usAc.getModified_at() != null) {
            jg.writeStringField("modified_at", dateFormat.format(usAc.getModified_at()));
            jg.writeStringField("modified_by", usAc.getModified_by());
        }
        jg.writeEndObject();
    }

    @Override
    public void serializeWithType(JsonGenerator jg, SerializerProvider sp, TypeSerializer ts) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
