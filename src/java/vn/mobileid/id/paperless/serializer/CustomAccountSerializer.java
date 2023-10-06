/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.StatusOfAccount;

/**
 *
 * @author Admin
 */
public class CustomAccountSerializer extends JsonSerializer<Account>{

    @Override
    public void serialize(Account t, JsonGenerator jg, SerializerProvider sp) throws IOException {
        jg.writeStartObject();
            jg.writeNumberField("id", t.getId());
            jg.writeStringField("user_name", t.getUser_name());
            jg.writeStringField("user_email", t.getUser_email());
            jg.writeStringField("mobile_number", t.getMobile_number());
            jg.writeStringField("job_title", t.getJob_title());
            jg.writeStringField("company", t.getCompany());
            jg.writeStringField("service_plan", t.getService_plan());
            jg.writeStringField("enterprise_name", t.getEnterprise_name());
            jg.writeStringField("enterprise_role", t.getRole());
            jg.writeStringField("enterprise_role_vn", t.getRole_vn());
            jg.writeStringField("business_type", t.getBusinessType().name());
            jg.writeStringField("business_type_vn", t.getBusinessType().getName_vn());
            jg.writeStringField("organization_website", t.getOrganizationWebsite());
            jg.writeStringField("user_password", "********");
            jg.writeStringField("security_question", t.getSecurity_question());
            jg.writeStringField("security_answer", t.getSecurity_answer());
            jg.writeStringField("notification", t.getNotification());
            jg.writeStringField("service_agreement", t.getService_agreement());
            jg.writeNumberField("status_id", t.getStatus_id());
            StatusOfAccount status = Resources.getListStatusOfAccount().get(t.getStatus_id());
            jg.writeStringField("status_name", Resources.getListStatusOfAccount().get(t.getStatus_id()).getName());
            jg.writeStringField("status_name_vn", Resources.getListStatusOfAccount().get(t.getStatus_id()).getRemark_vn());
            jg.writeBooleanField("verified", t.isVerified());
        jg.writeEndObject();
    }
    
}
