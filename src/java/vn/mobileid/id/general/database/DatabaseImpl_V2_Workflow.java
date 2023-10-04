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
import vn.mobileid.id.paperless.objects.Workflow;

/**
 *
 * @author GiaTK
 */
public class DatabaseImpl_V2_Workflow implements DatabaseV2_Workflow {

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

    @Override
    public DatabaseResponse getWorkflow(
            long pWORKFLOW_ID,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_GET(?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pWORKFLOW_ID", pWORKFLOW_ID);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                Workflow.class,
                nameStore,
                input,
                null,
                "Get Workflow");

        LogHandler.debug(this.getClass(), response.getDebugString());

        return response;
    }

    @Override
    public DatabaseResponse getRowCountOfWorkflow(
            String pUSER_EMAIL,
            long pENTERPRISE_ID,
            String pWORKFLOW_STATUS,
            String pLIST_TYPE,
            boolean pUSE_META_DATA,
            String pMETA_DATA,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_GET_ROW_COUNT(?,?,?,?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pUSER_EMAIL", pUSER_EMAIL);
        input.put("pENTERPRISE_ID", pENTERPRISE_ID);
        input.put("pWORKFLOW_STATUS", pWORKFLOW_STATUS);
        input.put("pLIST_TYPE", pLIST_TYPE);
        input.put("pUSE_META_DATA", pUSE_META_DATA);
        input.put("pMETA_DATA", pMETA_DATA);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                null,
                "Get row count Workflow");

        LogHandler.debug(this.getClass(), response.getDebugString());
        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            if (row.get("ROW_COUNT") != null) {
                response.setObject((long) row.get("ROW_COUNT"));
            }
        }
        return response;
    }

    @Override
    public DatabaseResponse getListOfWorkflow(
            String pUSER_EMAIL, 
            long pENTERPRISE_ID,
            String pWORKFLOW_STATUS,
            String pLIST_TYPE, 
            boolean pUSE_META_DATA,
            String pMETA_DATA, 
            int pOFFSET, 
            int pROW_COUNT, 
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_LIST(?,?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pUSER_EMAIL", pUSER_EMAIL);
        input.put("pENTERPRISE_ID", pENTERPRISE_ID);
        input.put("pWORKFLOW_STATUS", pWORKFLOW_STATUS);
        input.put("pLIST_TYPE", pLIST_TYPE);
        input.put("pUSE_META_DATA", pUSE_META_DATA);
        input.put("pMETA_DATA", pMETA_DATA);
        input.put("pOFFSET", pOFFSET);
        input.put("pROW_COUNT", pROW_COUNT);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                Workflow.class,
                nameStore,
                input,
                null,
                "Get list of Workflow");

        LogHandler.debug(this.getClass(), response.getDebugString());
        
        return response;
    }

}
