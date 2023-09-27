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
}
