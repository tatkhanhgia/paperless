/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.database;

import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;
import vn.mobileid.id.paperless.objects.WorkflowTemplate;

/**
 *
 * @author GiaTK
 */
public class DatabaseImpl_V2_WorkflowTemplate implements DatabaseV2_WorkflowTemplate{
    @Override
    public DatabaseResponse updateWorkflowTemplate(
            long pWORKFLOW_ID,
            String pUSER_EMAIL,
            long pENTERPRISE_ID, 
            String pMETA_DATA_TEMPLATE,
            String pHMAC,
            String pLAST_MODIFIED_BY,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_TEMPLATE_UPDATE(?,?,?,?,?,?,?)}";
      
        HashMap<String, Object> input = new HashMap<>();
        input.put("pWORKFLOW_ID", pWORKFLOW_ID);
        input.put("pUSER_EMAIL", pUSER_EMAIL);
        input.put("pENTERPRISE_ID", pENTERPRISE_ID);
        input.put("pMETA_DATA_TEMPLATE", pMETA_DATA_TEMPLATE );
        input.put("pHMAC", pHMAC);
        input.put("pLAST_MODIFIED_BY", pLAST_MODIFIED_BY);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                null,
                "Update Workflow Template");

        LogHandler.debug(this.getClass(), response.getDebugString());

        return response;
    }

    @Override
    public DatabaseResponse getWorkflowTemplate(
            long pWORKFLOW_ID,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_TEMPLATE_GET(?,?)}";
      
        HashMap<String, Object> input = new HashMap<>();
        input.put("pWORKFLOW_ID", pWORKFLOW_ID);        

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                WorkflowTemplate.class,
                nameStore,
                input,
                null,
                "Get Workflow Template");

        LogHandler.debug(this.getClass(), response.getDebugString());

        return response;
    }
}
