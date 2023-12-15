/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.database;

import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.paperless.objects.Enterprise_SigningInfo;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Enterprise;

/**
 *
 * @author GiaTK
 */
public class DatabaseImpl_V2_Enterprise implements DatabaseV2_Enterprise{

    @Override
    public DatabaseResponse getEnterpriseInfo(
            int pENTERPRISE_ID,
            String pENTERPRISE_NAME) throws Exception {
        String nameStore = "{ CALL USP_ENTERPRISE_INFO_GET(?,?,?)}";
        
        HashMap<String, Object> input = new HashMap<>();
        input.put("pENTERPRISE_ID", pENTERPRISE_ID);
        input.put("pENTERPRISE_NAME", pENTERPRISE_NAME);
                
        
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                Enterprise.class,
                nameStore,
                input,
                null,
                "Get Enterprise Info");

        LogHandler.debug(this.getClass(), response.getDebugString());
      
        return response;
    }

    @Override
    public DatabaseResponse getEnterpriseInfoOfUser(
            String email,
            String transaction_id) throws Exception {
        String nameStore = "{ CALL USP_USER_GET_ENTERPRISE_INFO(?,?)}";
        
        HashMap<String, Object> input = new HashMap<>();
        input.put("pUSER_EMAIL", email);
                
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                Enterprise.class,
                nameStore,
                input,
                null,
                "Get Enterprise Info");

        LogHandler.debug(this.getClass(), response.getDebugString());
      
        return response;
    }

    @Override
    public DatabaseResponse getSigningInfoOfEnterprise(
            int enterprise_id,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_ENTERPRISE_GET_SIGNING_INFRO_PROPERTIES(?,?)}";
        
        HashMap<String, Object> input = new HashMap<>();
        input.put("pENTERPRISE_ID", enterprise_id);
                
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                Enterprise_SigningInfo.class,
                nameStore,
                input,
                null,
                "Get Data Signing Propeties");

        LogHandler.debug(this.getClass(), response.getDebugString());
      
        return response;
    }
    
}
