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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.mobileid.id.qrypto.object.QRSchema;
import vn.mobileid.id.qrypto.object.QRSchema.fieldType;

/**
 *
 * @author GiaTK
 */
public class CustomQRSchemeSerializer extends JsonSerializer<QRSchema> {

    @Override
    public void serialize(
            QRSchema t,
            JsonGenerator jg,
            SerializerProvider sp) throws IOException {
        jg.writeStartObject();
        Field[] fields = QRSchema.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.get(t) instanceof String) {
                    jg.writeStringField(field.getName(), (String) field.get(t));
                } else if (field.get(t) instanceof List) {
                    jg.writeObjectFieldStart(field.getName());
                    List<?> list = (List) field.get(t);
                    Iterator s = list.iterator();
                    while (true) {
                        Object object = s.next();
                        if (object instanceof QRSchema.data) {
                            QRSchema.data temp = (QRSchema.data) object;
                            jg.writeObjectField(temp.getName(), temp.getValue());
                        }
                        if (s.hasNext()) {
                            continue;
                        } else {
                            break;
                        }
                    }
                    jg.writeEndObject();
                } else if (field.get(t) instanceof QRSchema.format) {
                    QRSchema.format tempFormat = (QRSchema.format) field.get(t);
                    jg.writeObjectFieldStart("format");
                    jg.writeArrayFieldStart("fields");
                    for (QRSchema.field tempfield : tempFormat.getFields()) {
                        jg.writeStartObject();
                        String q = tempfield.getName();
                        if (tempfield.getType().getName().equals(fieldType.t2.getName())
                                || tempfield.getType().getName().equals(fieldType.url.getName())) {
                            jg.writeObjectFieldStart(q);
                            jg.writeStringField("type", tempfield.getType().getName());
                            jg.writeStringField("kvalue", tempfield.getKvalue());
                            jg.writeEndObject();
                        } else if(tempfield.getType().getName().equals(fieldType.f1.getName())) {
                            jg.writeObjectFieldStart(q);
                            jg.writeStringField("type", tempfield.getType().getName());
                            jg.writeStringField("file_type", tempfield.getFile_type());
                            jg.writeStringField("file_field", tempfield.getFile_field());
                            jg.writeStringField("file_name", tempfield.getFile_name());
                            jg.writeNumberField("share_mode", tempfield.getShare_mode());
                            if(tempfield.getQr_meta_data() != null){
                                jg.writeObjectFieldStart("qr_meta_data");
                                    jg.writeBooleanField("isTransparent", tempfield.getQr_meta_data().isIsTransparent());
                                    jg.writeNumberField("xcoordinator", tempfield.getQr_meta_data().getxCoordinator());
                                    jg.writeNumberField("ycoordinator", tempfield.getQr_meta_data().getyCoordinator());
                                    jg.writeNumberField("qrDimension", tempfield.getQr_meta_data().getQrDimension());
                                    jg.writeArrayFieldStart("pageNumber");
                                        for(Integer temp : tempfield.getQr_meta_data().getPageNumber()){
                                            jg.writeNumber(temp);
                                        }
                                    jg.writeEndArray();
                                jg.writeEndObject();
                            }
                            jg.writeEndObject();
                        }
                        jg.writeEndObject();
                    }
                    jg.writeEndArray();
                    jg.writeStringField("version", tempFormat.getVersion());
                    jg.writeEndObject();
                }
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(CustomQRSchemeSerializer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(CustomQRSchemeSerializer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        jg.writeEndObject();
    }

}
