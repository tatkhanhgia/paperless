/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.database;

import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.WorkflowActivity;

/**
 *
 * @author GiaTK
 */
public class DatabaseImpl_V2_WorkflowActivity implements DatabaseV2_WorkflowActivity{

    @Override
    public DatabaseResponse createWorkflowActivity(
            int pENTERPRISE_ID,
            int pWORKFLOW_ID,
            String pUSER_EMAIL,
            String pTRANSACTION_ID,
            String pDOWNLOAD_LINK,
            int pDOWNLOAD_LINK_TYPE,
            String pREMARK,
            int pPRODUCTION_TYPE,
            int pWORKFLOW_TYPE,
            String pREQUEST_DATA,
            String pHMAC,
            String pCREATED_BY,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_ACTIVITY_ADD(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pENTERPRISE_ID", pENTERPRISE_ID);
        input.put("pWORKFLOW_ID", pWORKFLOW_ID);
        input.put("pUSER_EMAIL", pUSER_EMAIL);
        input.put("pTRANSACTION_ID", pTRANSACTION_ID);
        input.put("pDOWNLOAD_LINK", pDOWNLOAD_LINK);
        input.put("pDOWNLOAD_LINK_TYPE", pDOWNLOAD_LINK_TYPE);
        input.put("pREMARK", pREMARK);
        input.put("pPRODUCTION_TYPE", pPRODUCTION_TYPE);
        input.put("pWORKFLOW_TYPE", pWORKFLOW_TYPE);
        input.put("pREQUEST_DATA", pREQUEST_DATA);
        input.put("pHMAC", pHMAC);
        input.put("pCREATED_BY", pCREATED_BY);
        
        HashMap<String, Integer> output = new HashMap<>();
        output.put("pWORKFLOW_ACTIVITY_ID", java.sql.Types.BIGINT);
        output.put("pRESPONSE_CODE", java.sql.Types.NVARCHAR);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                output,
                "Create Workflow Activity");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            if (row.get("pWORKFLOW_ACTIVITY_ID") != null) {
                response.setObject((long) row.get("pWORKFLOW_ACTIVITY_ID"));
            }
        }
        return response;
    }

    @Override
    public DatabaseResponse getWorkflowActivity(
            int id, 
            String transaction_id) throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_ACTIVITY_GET(?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pWO_AC_ID", id);                

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                WorkflowActivity.class,
                nameStore,
                input,
                null,
                "Get Workflow Activity");

        LogHandler.debug(this.getClass(), response.getDebugString());
        
        return response;
    }
    
}
