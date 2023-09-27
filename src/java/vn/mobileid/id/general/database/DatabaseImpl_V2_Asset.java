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
import vn.mobileid.id.paperless.objects.Asset;

/**
 *
 * @author GiaTK
 */
public class DatabaseImpl_V2_Asset implements DatabaseV2_Asset {

    @Override
    public DatabaseResponse uploadAsset(
            String email, 
            int enterprise_id, 
            int type,
            String file_name,
            long size,
            String UUID, 
            String pDBMS_PROPERTY,
            String metaData, 
            byte[] fileData,
            String hmac,
            String createdBy,
            String transaction_id) throws Exception {
        String nameStore = "{ CALL USP_ASSET_ADD(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        
        HashMap<String, Object> input = new HashMap<>();
        input.put("pUSER_EMAIL", email);
        input.put("pENTERPRISE_ID", enterprise_id);
        input.put("pASSET_TYPE", type);
        input.put("pFILE_NAME", file_name);
        input.put("pSIZE", size);
        input.put("pUUID", UUID);
        input.put("pDMS_PROPERTY", pDBMS_PROPERTY);
        input.put("pMETA_DATA", metaData);
        input.put("pBINARY_DATA", fileData);
        input.put("pHMAC", hmac);
        input.put("pCREATED_BY", createdBy);
        
        HashMap<String, Integer> output = new HashMap<>();
        output.put("pASSET_ID", java.sql.Types.BIGINT);
        output.put("pRESPONSE_CODE", java.sql.Types.NVARCHAR);
        
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                output,
                "Upload Asset");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
        List<HashMap<String,Object>> rows = response.getRows();
        for(HashMap<String,Object> row : rows){
            response.setObject((long)row.get("pASSET_ID"));
        }
        return response;
    }

    @Override
    public DatabaseResponse deleteAsset(
            int id, 
            String email,
            String transaction_id) throws Exception {
        String nameStore = "{ CALL USP_ASSET_DELETE(?,?,?)}";
        
        HashMap<String, Object> input = new HashMap<>();
        input.put("pUSER_EMAIL", email);
        input.put("pASSET_ID", id);
        
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                null,
                "Delete Asset");

        LogHandler.debug(this.getClass(), response.getDebugString());

        return response;
    }

    @Override
    public DatabaseResponse getAsset(
            int assetID, 
            String transaction_id) throws Exception {
        String nameStore = "{ CALL USP_ASSET_GET(?,?)}";
        
        HashMap<String, Object> input = new HashMap<>();
        input.put("pASSET_ID", assetID);
        
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                Asset.class,
                nameStore,
                input,
                null,
                "Get Asset");

        LogHandler.debug(this.getClass(), response.getDebugString());
        
        return response;
    }

    @Override
    public DatabaseResponse getTotalRecordsAsset(
            int enterpriseId,
            String email, 
            String fileName, 
            String type, 
            String status,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_ASSET_GET_ROW_COUNT(?,?,?,?,?,?)}";
        
        HashMap<String, Object> input = new HashMap<>();
        input.put("pENTERPRISE_ID", enterpriseId);
        input.put("pUSER_EMAIL", email);
        input.put("pFILE_NAME", fileName);
        input.put("pTYPE", type);
        input.put("pSTATUS", status);
        
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                null,
                "Get record Asset");

        LogHandler.debug(this.getClass(), response.getDebugString());
        
        return response;
    }

    @Override
    public DatabaseResponse getListAsset(
            int ent_id, 
            String email, 
            String file_name, 
            String type,
            String status,
            int offset,
            int rowcount,
            String transactionID) throws Exception {
        String nameStore = "{ CALL USP_ASSET_LIST(?,?,?,?,?,?,?,?)}";
        
        HashMap<String, Object> input = new HashMap<>();
        input.put("pENTERPRISE_ID", ent_id);
        input.put("pUSER_EMAIL", email);
        input.put("pFILE_NAME", file_name);
        input.put("pTYPE", type);
        input.put("pSTATUS", status);
        input.put("pOFFSET", offset);
        input.put("pROW_COUNT", rowcount);
        
        
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                Asset.class,
                nameStore,
                input,
                null,
                "Get record Asset");

        LogHandler.debug(this.getClass(), response.getDebugString());
        
        if(response.getStatus() == PaperlessConstant.CODE_SUCCESS){
            response.setObject(CreateConnection.convertObjectToList(response.getObject()));
        }
        
        return response;
    }

    @Override
    public DatabaseResponse updateAsset(
            int pASSET_ID, 
            String pUSER_EMAIL,
            String pFILE_NAME, 
            int pASSET_TYPE, 
            int pSIZE,
            String pUSED_BY,
            String pUUID, 
            String pDMS_PROPERTY, 
            String pMETA_DATA, 
            byte[] pBINARY_DATA,
            String pHMAC, 
            String pLAST_MODIFIED_BY,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_ASSET_UPDATE(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        
        HashMap<String, Object> input = new HashMap<>();
        input.put("pASSET_ID", pASSET_ID);
        input.put("pUSER_EMAIL", pUSER_EMAIL);
        input.put("pFILE_NAME", pFILE_NAME);
        input.put("pASSET_TYPE", pASSET_TYPE==0?null:pASSET_TYPE);
        input.put("pSIZE", pSIZE<=0?null:pSIZE);
        input.put("pUSED_BY", pUSED_BY);
        input.put("pUUID", pUUID);
        input.put("pDMS_PROPERTY", pDMS_PROPERTY);
        input.put("pMETA_DATA", pMETA_DATA);
        input.put("pBINARY_DATA", pBINARY_DATA);
        input.put("pHMAC", pHMAC);
        input.put("pLAST_MODIFIED_BY", pLAST_MODIFIED_BY);
        
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                null,
                "Get record Asset");

        LogHandler.debug(this.getClass(), response.getDebugString());
        
        return response;
    }
    
}
