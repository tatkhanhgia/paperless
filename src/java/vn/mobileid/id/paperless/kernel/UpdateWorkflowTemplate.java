/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Item_JSNObject;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowTemplate;

/**
 *
 * @author GiaTK
 */
@Deprecated
public class UpdateWorkflowTemplate {

    /**
     * Update data workflow template of the workflow
     *
     * @param id
     * @param email
     * @param enterprise_id
     * @param detail
     * @param hmac
     * @param transactionID
     * @return
     * @throws Exception
     */
    public static InternalResponse updateWorkflowTemplate(
            int id,
            String email,
            int enterprise_id,
            Item_JSNObject detail,
            String hmac,
            String transactionID) throws Exception {

        Database DB = new DatabaseImpl();

        DatabaseResponse callDB = DB.updateWorkflowTemplate(
                id,
                email,
                enterprise_id,
                new ObjectMapper().writeValueAsString(detail),
                hmac,
                email,
                transactionID);

        if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_FAIL,
                    callDB.getStatus(),
                    "en",
                    null);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }

        return new InternalResponse(
                PaperlessConstant.CODE_SUCCESS,
                "");
    }

    public static void main(String[] args) throws Exception {
        WorkflowTemplate template = new WorkflowTemplate();
        String json = "{\"items\":[{\"field\":\"FullName\",\"mandatory_enable\":false,\"type\":1,\"value\":\"NGUYỄN PHƯỚC VINH\"},{\"field\":\"BirthDate\",\"mandatory_enable\":false,\"type\":1,\"value\":\"31/12/2022\"},{\"field\":\"Nationality\",\"mandatory_enable\":false,\"type\":1,\"value\":\"VietNam\"},{\"field\":\"PersonalNumber\",\"mandatory_enable\":false,\"type\":1,\"value\":\"079200011188\"},{\"field\":\"IssuanceDate\",\"mandatory_enable\":false,\"type\":1,\"value\":\"31/12/2022\"},{\"field\":\"PlaceOfResidence\",\"mandatory_enable\":false,\"type\":1,\"value\":\"Quan11\"},{\"field\":\"Nationality\",\"mandatory_enable\":false,\"type\":1,\"value\":\"VietNam\"}]}";
        template.setMeta_data_template(json);
        InternalResponse res = UpdateWorkflowTemplate.updateWorkflowTemplate(
                12,
                "giatk@mobile-id.vn",
                3,
                template.getMeta_data(),
                "hmac",
                "modified by");
        System.out.println("MEs:" + res.getMessage());
    }

}
