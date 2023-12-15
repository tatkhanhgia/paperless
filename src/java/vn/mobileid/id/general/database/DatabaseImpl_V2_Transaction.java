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
public class DatabaseImpl_V2_Transaction implements DatabaseV2_Transaction{

    @Override
    public DatabaseResponse createTransaction(
            String U_EMAIL, 
            long pLOG_ID, 
            long pOBJECT_ID,
            int pOBJECT_TYPE, 
            String pIP_ADDRESS,
            String pMETADATA, 
            String pDESCRIPTION, 
            String pHMAC, 
            String pCREATED_BY, 
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_TRANSACTION_ADD(?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pUSER_EMAIL", U_EMAIL);
        input.put("pUSER_ACTIVITY_LOG_ID", pLOG_ID);
        input.put("pOBJECT_ID", pOBJECT_ID);
        input.put("pOBJECT_TYPE", pOBJECT_TYPE);
//        input.put("pIP_ADDRESS", pIP_ADDRESS);
//        input.put("pMETADATA", pMETADATA);
//        input.put("pDESCRIPTION", pDESCRIPTION);
        input.put("pHMAC", pHMAC);
        input.put("pCREATED_BY", pCREATED_BY);

        HashMap<String, Integer> output = new HashMap<>();
        output.put("pTRANSACTION_ID", java.sql.Types.NVARCHAR);
        output.put("pRESPONSE_CODE", java.sql.Types.NVARCHAR);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                output,
                "Create Transaction");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            if (row.get("pTRANSACTION_ID") != null) {
                response.setObject((String) row.get("pTRANSACTION_ID"));
            }
        }
        return response;
    }
    
}
