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
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.WorkflowActivity;

/**
 *
 * @author GiaTK
 */
public class CustomListAccount implements JsonSerializable {

    private int page;
    private int record_per_page;
    private List<Account> listAccount;

    public CustomListAccount(List<Account> list, int page, int record_per_page) {
        this.page = page;
        this.record_per_page = record_per_page;
        this.listAccount = list;
    }

    @Override
    public void serialize(JsonGenerator jg, SerializerProvider sp) throws IOException {
//        String rootname = sp.getConfig().findRootName(Asset.class).getSimpleName();
        jg.writeStartObject();
        jg.writeFieldName("users");
        jg.writeStartArray();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ssa XXX");
        for (Account account : listAccount) {
            jg.writeStartObject();
            jg.writeStringField("user_name", account.getUser_name());
            jg.writeStringField("user_email", account.getUser_email());
            jg.writeStringField("mobile_number", account.getMobile_number());
            jg.writeStringField("company", account.getEnterprise_name());
            jg.writeStringField("job_title", account.getJob_title()==null?"user":account.getJob_title());
            jg.writeStringField("enterprise_name", account.getEnterprise_name());
            jg.writeStringField("role", account.getRole());            
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
