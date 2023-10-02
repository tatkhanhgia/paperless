/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.database;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.object.enumration.DownloadLinkType;
import vn.mobileid.id.paperless.objects.WorkflowActivity;

/**
 *
 * @author GiaTK
 */
public class DatabaseImpl_V2_WorkflowActivity implements DatabaseV2_WorkflowActivity {

    @Override
    public DatabaseResponse createWorkflowActivity(
            int pENTERPRISE_ID,
            int pWORKFLOW_ID,
            String pUSER_EMAIL,
            String pTRANSACTION_ID,
            String pDOWNLOAD_LINK,
            DownloadLinkType pDOWNLOAD_LINK_TYPE,
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
        input.put("pDOWNLOAD_LINK_TYPE", pDOWNLOAD_LINK_TYPE.getType());
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

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return response;
        }

        return response;
    }

    @Override
    public DatabaseResponse getTotalRecordsWorkflowActivity(
            String U_EMAIL,
            int pENTERPRISE_ID,
            String EMAIL_SEARCH,
            String DATE_SEARCH,
            String G_TYPE,
            String W_A_STATUS,
            String pPRODUCTION_TYPE_LIST,
            boolean IS_CUSTOM_RANGE,
            String FROM_DATE,
            String TO_DATE,
            int pOFFSET,
            int pROW_COUNT,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_ACTIVITY_GET_ROW_COUNT(?,?,?,?,?,?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("U_EMAIL", U_EMAIL);
        input.put("pENTERPRISE_ID", pENTERPRISE_ID);
        input.put("EMAIL_SEARCH", EMAIL_SEARCH);
        input.put("DATE_SEARCH", DATE_SEARCH);
        input.put("G_TYPE", G_TYPE);
        input.put("W_A_STATUS", W_A_STATUS);
        input.put("pPRODUCTION_TYPE_LIST", pPRODUCTION_TYPE_LIST);
        input.put("IS_CUSTOM_RANGE", IS_CUSTOM_RANGE);
        input.put("FROM_DATE", FROM_DATE);
        input.put("TO_DATE", TO_DATE);
        input.put("pOFFSET", pOFFSET);
        input.put("pROW_COUNT", pROW_COUNT);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                null,
                "Get row count of Workflow Activity");

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
    public DatabaseResponse getListWorkflowActivity(
            String U_EMAIL,
            long pENTERPRISE_ID,
            String EMAIL_SEARCH,
            Date DATE_SEARCH,
            String G_TYPE,
            String W_A_STATUS,
            String pPRODUCTION_TYPE_LIST,
            boolean IS_CUSTOM_RANGE,
            Date FROM_DATE,
            Date TO_DATE,
            int pOFFSET,
            int pROW_COUNT,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_ACTIVITY_LIST(?,?,?,?,?,?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("U_EMAIL", U_EMAIL);
        input.put("pENTERPRISE_ID", pENTERPRISE_ID);
        input.put("EMAIL_SEARCH", EMAIL_SEARCH);
        input.put("DATE_SEARCH", DATE_SEARCH);
        input.put("G_TYPE", G_TYPE);
        input.put("W_A_STATUS", W_A_STATUS);
        input.put("pPRODUCTION_TYPE_LIST", pPRODUCTION_TYPE_LIST);
        input.put("IS_CUSTOM_RANGE", IS_CUSTOM_RANGE);
        input.put("FROM_DATE", FROM_DATE);
        input.put("TO_DATE", TO_DATE);
        input.put("pOFFSET", pOFFSET);
        input.put("pROW_COUNT", pROW_COUNT);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                WorkflowActivity.class,
                nameStore,
                input,
                null,
                "Get List of Workflow Activity");

        LogHandler.debug(this.getClass(), response.getDebugString());

        return response;
    }

    @Override
    public DatabaseResponse updateRequestDataOfWorkflowActivity(
            int id,
            String meta_data,
            String modified_by,
            String transactionID) throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_ACTIVITY_UPDATE_REQUEST_DATA(?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pWORKFLOW_ACTIVITY_ID", id);
        input.put("pREQUEST_DATA", meta_data);
        input.put("pLAST_MODIFIED_BY", modified_by);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                WorkflowActivity.class,
                nameStore,
                input,
                null,
                "Update Request of the Workflow Activity");

        LogHandler.debug(this.getClass(), response.getDebugString());

        return response;
    }

    @Override
    public DatabaseResponse updateStatusWorkflowActivity(
            int id, 
            String status, 
            String last_modified_by,
            String transactionID) throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_ACTIVITY_UPDATE_STATUS(?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pWORKFLOW_ACTIVITY_ID", id);
        input.put("pSTATUS_NAME", status);
        input.put("pLAST_MODIFIED_BY", last_modified_by);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                null,
                "Update Status of the Workflow Activity");

        LogHandler.debug(this.getClass(), response.getDebugString());

        return response;
    }

}
