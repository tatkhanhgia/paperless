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

/**
 *
 * @author GiaTK
 */
public class DatabaseImpl_V2_Workflow implements DatabaseV2_Workflow{

    @Override
    public DatabaseResponse createWorkflow(
            String U_EMAIL,
            long pENTERPRISE_ID,
            int pTEMPLATE_TYPE,
            String pLABEL, 
            String pHMAC, 
            String pCREATED_BY,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_ADD(?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("U_EMAIL", U_EMAIL);
        input.put("pENTERPRISE_ID", pENTERPRISE_ID);
        input.put("pTEMPLATE_TYPE", pTEMPLATE_TYPE);
        input.put("pLABEL", pLABEL);
        input.put("pHMAC", pHMAC);
        input.put("pCREATED_BY", pCREATED_BY);

        HashMap<String, Integer> output = new HashMap<>();
        output.put("pWORKFLOW_ID", java.sql.Types.BIGINT);
        output.put("pRESPONSE_CODE", java.sql.Types.NVARCHAR);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                output,
                "Create Workflow");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            if (row.get("pWORKFLOW_ID") != null) {
                response.setObject((long) row.get("pWORKFLOW_ID"));
            }
        }
        return response;
    }
    
}
