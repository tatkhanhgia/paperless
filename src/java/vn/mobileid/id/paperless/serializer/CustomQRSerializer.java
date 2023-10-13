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
import vn.mobileid.id.paperless.objects.QR;


/**
 *
 * @author GiaTK
 */
public class CustomQRSerializer extends JsonSerializer<QR> {

    @Override
    public void serialize(
            QR t,
            JsonGenerator jg,
            SerializerProvider sp) throws IOException {
        DateFormat format = new SimpleDateFormat(PolicyConfiguration.getInstant().getSystemConfig().getAttributes().get(0).getDateFormat());
        jg.writeStartObject();
        jg.writeFieldName("document_detail");
        jg.writeStartObject();
            jg.writeNumberField("id", t.getId());
            jg.writeStringField("meta_data", t.getMeta_data());
            jg.writeStringField("image", t.getImage());
            jg.writeStringField("created_by", t.getCreated_by());
            jg.writeStringField("created_at", t.getCreated_at()!=null?(format.format(t.getCreated_at())):null);
            if(t.getModified_at()!=null){
                jg.writeStringField("modified_by", t.getModified_by());
                jg.writeStringField("modified_at", t.getModified_at()!=null?(format.format(t.getModified_at())):null);
            }
        jg.writeEndObject();
        jg.writeEndObject();
    }
}
