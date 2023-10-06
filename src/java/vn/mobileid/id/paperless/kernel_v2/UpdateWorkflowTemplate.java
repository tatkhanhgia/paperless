/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.database.DatabaseImpl_V2_WorkflowTemplate;
import vn.mobileid.id.general.database.DatabaseV2_WorkflowTemplate;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Item_JSNObject;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class UpdateWorkflowTemplate {
    
    //<editor-fold defaultstate="collapsed" desc="Update Workflow Template">
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

        DatabaseV2_WorkflowTemplate DB = new DatabaseImpl_V2_WorkflowTemplate();

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
    //</editor-fold>
    
}
