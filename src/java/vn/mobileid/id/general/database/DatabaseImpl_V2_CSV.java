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
import vn.mobileid.id.paperless.objects.CSVTask;

/**
 *
 * @author GiaTK
 */
public class DatabaseImpl_V2_CSV implements DatabaseV2_CSV {

    @Override
    public DatabaseResponse createCSVTask(
            String pNAME,
            String pUUID,
            String pDMS_PROPERTY,
            int pPAGES,
            long pSIZE,
            String pMETA_DATA,
            String pHMAC,
            String pCREATED_BY,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_CSV_TASK_ADD(?,?,?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pNAME", pNAME);
        input.put("pUUID", pUUID);
        input.put("pDMS_PROPERTY", pDMS_PROPERTY);
        input.put("pPAGES", pPAGES);
        input.put("pSIZE", pSIZE);
        input.put("pMETA_DATA", pMETA_DATA);
        input.put("pHMAC", pHMAC);
        input.put("pCREATED_BY", pCREATED_BY);

        HashMap<String, Integer> output = new HashMap<>();
        output.put("pCSV_TASK_ID", java.sql.Types.BIGINT);
        output.put("pRESPONSE_CODE", java.sql.Types.NVARCHAR);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                output,
                "Create CSV Task");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            response.setObject((long) row.get("pCSV_TASK_ID"));
        }
        return response;
    }

    @Override
    public DatabaseResponse getCSVTask(
            long pCSV_TASK_ID,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_CSV_TASK_GET(?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pCSV_TASK_ID", pCSV_TASK_ID);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                CSVTask.class,
                nameStore,
                input,
                null,
                "Get CSV Task");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
        
        return response;
    }

    @Override
    public DatabaseResponse updateCSVTask(
            long pCSV_TASK_ID,
            String pNAME, 
            String pUUID, 
            String pDMS_PROPERTY, 
            int pPAGES,
            long pSIZE, 
            String pMETA_DATA,
            String pHMAC, 
            String pLAST_MODIFIED_BY,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_CSV_TASK_UPDATE(?,?,?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pCSV_TASK_ID", pCSV_TASK_ID);
        input.put("pNAME", pNAME);
        input.put("pUUID", pUUID);
        input.put("pDMS_PROPERTY", pDMS_PROPERTY);
        input.put("pPAGES", pPAGES);
        input.put("pSIZE", pSIZE);
        input.put("pMETA_DATA", pMETA_DATA);
        input.put("pHMAC", pHMAC);
        input.put("pLAST_MODIFIED_BY", pLAST_MODIFIED_BY);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                null,
                "Update CSV Task");

        LogHandler.debug(this.getClass(), response.getDebugString());    
        
        return response;
    }

}
