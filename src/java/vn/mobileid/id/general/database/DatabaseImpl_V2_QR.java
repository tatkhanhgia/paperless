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
import static vn.mobileid.id.paperless.object.enumration.ObjectType.QR;
import vn.mobileid.id.paperless.objects.QR;
import vn.mobileid.id.paperless.objects.QRSize;
import vn.mobileid.id.paperless.objects.QRType;

/**
 *
 * @author GiaTK
 */
public class DatabaseImpl_V2_QR implements DatabaseV2_QR {

    @Override
    public DatabaseResponse createQR(
            String pMETA_DATA,
            String pIMAGE,
            String pHMAC,
            String pCREATED_BY,
            String transaction_id) throws Exception {
        String nameStore = "{ CALL USP_QR_ADD(?,?,?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pMETA_DATA", pMETA_DATA);
        input.put("pIMAGE", pIMAGE);
        input.put("pHMAC", pHMAC);
        input.put("pCREATED_BY", pCREATED_BY);

        HashMap<String, Integer> output = new HashMap<>();
        output.put("pQR_ID", java.sql.Types.BIGINT);
        output.put("pRESPONSE_CODE", java.sql.Types.NVARCHAR);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                output,
                "Create QR");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            if (row.get("pQR_ID") != null) {
                response.setObject((long) row.get("pQR_ID"));
            }
        }
        return response;
    }

    @Override
    public DatabaseResponse getListQRSize(String transactionId) throws Exception {
        String nameStore = "{ CALL USP_QR_SIZE_LIST()}";

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                QRSize.class,
                nameStore,
                null,
                null,
                "Get List QR Size");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
        
        return response;
    }
    
    @Override
    public DatabaseResponse getListQRType(String transactionId) throws Exception {
        String nameStore = "{ CALL USP_QR_TYPE_LIST()}";

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                QRType.class,
                nameStore,
                null,
                null,
                "Get List QR Size");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
        
        return response;
    }

    @Override
    public DatabaseResponse updateQR(
            long pQR_ID, 
            String pMETA_DATA, 
            String pIMAGE,
            String pLAST_MODIFIED_BY, 
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_QR_UPDATE(?,?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pQR_ID", pQR_ID);
        input.put("pMETA_DATA", pMETA_DATA);
        input.put("pIMAGE", pIMAGE);
        input.put("pLAST_MODIFIED_BY", pLAST_MODIFIED_BY);
        

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                null,
                "Update QR");

        LogHandler.debug(this.getClass(), response.getDebugString());
        
        return response;
    }

    @Override
    public DatabaseResponse getQR(
            long pQR_ID, 
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_QR_GET(?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pQR_ID", pQR_ID);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                QR.class,
                nameStore,
                input,
                null,
                "Get QR");

        LogHandler.debug(this.getClass(), response.getDebugString());
        
        return response;
    }

    @Override
    public DatabaseResponse getQRSize(
            String pQR_SIZE_NAME,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_QR_SIZE_GET(?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pQR_SIZE_NAME", pQR_SIZE_NAME);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                QRSize.class,
                nameStore,
                input,
                null,
                "Get QR Size");

        LogHandler.debug(this.getClass(), response.getDebugString());
        
        return response;
    }

}
