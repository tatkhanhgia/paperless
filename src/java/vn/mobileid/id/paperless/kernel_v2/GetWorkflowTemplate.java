/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.database.DatabaseImpl_V2_WorkflowTemplate;
import vn.mobileid.id.general.database.DatabaseV2_WorkflowTemplate;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowTemplate;

/**
 *
 * @author GiaTK
 */
public class GetWorkflowTemplate {
    /**
     * Using to get all data in Workflow template with ID workflow input
     *
     * @param id
     * @param transactionID
     * @return WorkflowTemplate
     */
    public static InternalResponse getWorkflowTemplate(
            int id,
            String transactionID) throws Exception {

        DatabaseV2_WorkflowTemplate DB = new DatabaseImpl_V2_WorkflowTemplate();

        DatabaseResponse callDB = DB.getWorkflowTemplate(
                id,
                transactionID);

        try {
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

            WorkflowTemplate template = (WorkflowTemplate) callDB.getObject();

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    template);

        } catch (Exception e) {
            throw new Exception("Cannot get workflow template", e);
        }
    }
    
    public static void main(String[] args) throws Exception{
        InternalResponse response = GetWorkflowTemplate.getWorkflowTemplate(509, "transactionId");
        WorkflowTemplate template = (WorkflowTemplate)response.getData();
        System.out.println(template.getMeta_data_template());
        System.out.println(template.getType_name());
    }
}
