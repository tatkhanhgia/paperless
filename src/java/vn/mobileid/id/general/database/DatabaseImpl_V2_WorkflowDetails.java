/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;

/**
 *
 * @author GiaTK
 */
public class DatabaseImpl_V2_WorkflowDetails implements DatabaseV2_WorkflowDetails {

    @Override
    public DatabaseResponse createWorkflowDetail(
            long W_ID,
            long pWORKFLOW_ATTRIBUTE_TYPE,
            Object pVALUE,
            String pHMAC,
            String pCREATED_BY,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_DETAIL_ADD(?,?,?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("W_ID", W_ID);
        input.put("pWORKFLOW_ATTRIBUTE_TYPE", pWORKFLOW_ATTRIBUTE_TYPE);
        input.put("pVALUE", pVALUE);
        input.put("pHMAC", pHMAC);
        input.put("pCREATED_BY", pCREATED_BY);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                null,
                "Add one Attribute of Workflow Details");

        LogHandler.debug(this.getClass(), response.getDebugString());

        return response;
    }

    @Override
    public DatabaseResponse getWorkflowDetailAttributeTypes() throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_ATTRIBUTE_TYPE_LIST()}";

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                WorkflowAttributeType.class,
                nameStore,
                null,
                null,
                "Get Workflow Attribute Types");

        LogHandler.debug(this.getClass(), response.getDebugString());
        
        return response;
    }

    @Override
    public DatabaseResponse getWorkflowDetail(
            long pWORKFLOW_ID,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_DETAIL_GET(?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pWORKFLOW_ID", pWORKFLOW_ID);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                WorkflowAttributeType.class,
                nameStore,
                input,
                null,
                "Get Workflow Details");

        LogHandler.debug(this.getClass(), response.getDebugString());
        
        if(response.getStatus() == PaperlessConstant.CODE_SUCCESS){
            response.setObject(CreateConnection.convertObjectToList(response.getObject()));
        }

        return response;
    }

    @Override
    public DatabaseResponse updateWorkflowDetail(
            long W_ID,
            String pUSER_EMAIL,
            long pENTERPRISE_ID,
            long pWORKFLOW_ATTRIBUTE_TYPE,
            Object pVALUE,
            String pHMAC,
            String pLAST_MODIFIED_BY,
            String transactionId
    ) throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_DETAIL_UPDATE(?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("W_ID", W_ID);
        input.put("pUSER_EMAIL", pUSER_EMAIL);
        input.put("pENTERPRISE_ID", pENTERPRISE_ID);
        input.put("pWORKFLOW_ATTRIBUTE_TYPE", pWORKFLOW_ATTRIBUTE_TYPE);
        input.put("pVALUE", pVALUE);
        input.put("pHMAC", pHMAC);
        input.put("pLAST_MODIFIED_BY", pLAST_MODIFIED_BY);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                null,
                "Update one row in Workflow Details");

        LogHandler.debug(this.getClass(), response.getDebugString());

        return response;
    }

}
