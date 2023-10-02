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
import vn.mobileid.id.paperless.objects.FileManagement;

/**
 *
 * @author GiaTK
 */
public class DatabaseImpl_V2_FileManagement implements DatabaseV2_FileManagement{
    
    public DatabaseResponse createFileManagement(
            String UUID,
            String name,
            int pages,
            int size,
            float width,
            float height,
            int uuid,
            String HMAC,
            String created_by,
            String DBMS,
            String file_type,
            String signing_properties,
            String hash_values,
            String transaction_id
    ) throws Exception{
        String nameStore = "{ CALL USP_FILE_MANAGEMENT_ADD(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        
        HashMap<String, Object> input = new HashMap<>();
        input.put("pUUID", UUID);
        input.put("pDMS_PROPERTY", DBMS);
        input.put("pNAME", name);
        input.put("PAGES", pages);
        input.put("pSIZE", size);
        input.put("pWIDTH", width);
        input.put("pHEIGHT", height);
        input.put("pHMAC", HMAC);
        input.put("pFILE_TYPE", file_type);
        input.put("pSIGNING_PROPERTIES", signing_properties);
        input.put("pHASH_VALUES", hash_values);
        input.put("pCREATED_BY", created_by);
        
        HashMap<String, Integer> output = new HashMap<>();
        output.put("pFILE_ID", java.sql.Types.BIGINT);
        output.put("pRESPONSE_CODE", java.sql.Types.NVARCHAR);
        
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                output,
                "Create FileManagement");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
        List<HashMap<String,Object>> rows = response.getRows();
        for(HashMap<String,Object> row : rows){
            response.setObject((long)row.get("pFILE_ID"));
        }
        return response;
    }

    @Override
    public DatabaseResponse getFileManagement(
            long fileManagementId,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_FILE_MANAGEMENT_GET(?,?)}";
        
        HashMap<String, Object> input = new HashMap<>();
        input.put("pFILE_ID", fileManagementId);
        
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                FileManagement.class,
                nameStore,
                input,
                null,
                "Get FileManagement");

        LogHandler.debug(this.getClass(), response.getDebugString());
       
        return response;
    }

    @Override
    public DatabaseResponse updateFileManagement(
            long id,
            String UUID,  
            String DBMS, 
            String name, 
            int pages, 
            long size,
            float width, 
            float height,
            int status,
            String hmac,
            String created_by,
            String last_modified_by,
            boolean isSigned,
            String file_type,
            String signing_properties, 
            String hash_values, 
            String transactionID) throws Exception {
        String nameStore = "{ CALL USP_FILE_MANAGEMENT_UPDATE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        
        HashMap<String, Object> input = new HashMap<>();
        input.put("pUUID", UUID);
        input.put("pDMS_PROPERTY", DBMS);
        input.put("pNAME", name);
        input.put("pPAGES", pages<0 ? null : pages);
        input.put("pSIZE", size<0 ? null : size);
        input.put("pWIDTH", width<0 ? null: width);
        input.put("pHEIGHT", height <0 ? null : height);
        input.put("pSTATUS", status<0 ? null : status);
        input.put("pHMAC", hmac);
        input.put("pFILE_ID", id);
        input.put("pLAST_MODIFIED_BY", last_modified_by);
        input.put("pPROCESSED_ENABLED", isSigned);
        input.put("pFILE_TYPE", file_type);
        input.put("pSIGNING_PROPERTIES", signing_properties);
        input.put("pHASH_VALUES", hash_values);
        
        DatabaseResponse response = CreateConnection.executeStoreProcedure(                
                nameStore,
                input,
                null,
                "Update FileManagement");

        LogHandler.debug(this.getClass(), response.getDebugString());
       
        return response;
    }

}