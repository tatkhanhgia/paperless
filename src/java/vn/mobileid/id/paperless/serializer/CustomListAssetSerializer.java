/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import vn.mobileid.id.paperless.objects.Asset;

/**
 *
 * @author GiaTK
 */
public class CustomListAssetSerializer implements JsonSerializable {

    private int page;
    private int record_per_page;
    private List<Asset> listAsset;

    public CustomListAssetSerializer(List<Asset> list, int page, int record_per_page) {
        this.page = page;
        this.record_per_page = record_per_page;
        this.listAsset = list;
    }

    @Override
    public void serialize(JsonGenerator jg, SerializerProvider sp) throws IOException {
//        String rootname = sp.getConfig().findRootName(Asset.class).getSimpleName();
        jg.writeStartObject();
        jg.writeFieldName("assets");
        jg.writeStartArray();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ssa XXX");
        for (Asset asset : listAsset) {
            jg.writeStartObject();
            jg.writeNumberField("asset_id", asset.getId());
            jg.writeStringField("asset_name", asset.getName());
            jg.writeNumberField("asset_size", asset.getSize());
            jg.writeStringField("asset_type", asset.getType_name());
            jg.writeStringField("workflow_use", "null");            
            jg.writeStringField("created_at", dateFormat.format(asset.getCreated_at()));
            jg.writeStringField("created_by", asset.getCreated_by());
            if (asset.getModified_at() != null) {
                jg.writeStringField("modified_at", dateFormat.format(asset.getModified_at()));
                jg.writeStringField("modified_by", asset.getModified_by());
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
