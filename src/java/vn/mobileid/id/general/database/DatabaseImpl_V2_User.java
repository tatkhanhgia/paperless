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
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.StatusOfAccount;

/**
 *
 * @author GiaTK
 */
public class DatabaseImpl_V2_User implements DatabaseV2_User{

    @Override
    public DatabaseResponse createUserActivity(
            String pUSER_EMAIL,
            int pENTERPRISE_ID,
            String pTRANSACTION_ID,
            String pCATEGORY_NAME,
            String pUSER_ACTIVITY_EVENT,
            String pHMAC,
            String pCREATED_BY, 
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_USER_ACTIVITY_ADD(?,?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pUSER_EMAIL", pUSER_EMAIL);
        input.put("pENTERPRISE_ID", pENTERPRISE_ID);
        input.put("pTRANSACTION_ID", pTRANSACTION_ID);
        input.put("pCATEGORY_NAME", pCATEGORY_NAME);
        input.put("pUSER_ACTIVITY_EVENT_NAME", pUSER_ACTIVITY_EVENT);
        input.put("pHMAC", pHMAC);
        input.put("pCREATED_BY", pCREATED_BY);
        

        HashMap<String, Integer> output = new HashMap<>();
        output.put("pUSER_ACTIVITY_ID", java.sql.Types.BIGINT);
        output.put("pRESPONSE_CODE", java.sql.Types.NVARCHAR);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                output,
                "Create User Activity");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            if (row.get("pUSER_ACTIVITY_ID") != null) {
                response.setObject((long) row.get("pUSER_ACTIVITY_ID"));
            }
        }
        return response;
    }

    @Override
    public DatabaseResponse createUserActivityLog(
            String pUSER_EMAIL, 
            int pENTERPRISE_ID,
            String pMODULE,
            String pACTION, 
            String pINFO_KEY,
            String pINFO_VALUE,
            String pDETAIL, 
            String pAGENT, 
            String pAGENT_DETAIL,
            String pHMAC, 
            String pCREATED_BY, 
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_USER_ACTIVITY_LOG_ADD(?,?,?,?,?,?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pUSER_EMAIL", pUSER_EMAIL);
        input.put("pENTERPRISE_ID", pENTERPRISE_ID);
        input.put("pMODULE", pMODULE);
        input.put("pACTION", pACTION);
        input.put("pINFO_KEY", pINFO_KEY);
        input.put("pINFO_VALUE", pINFO_VALUE);
        input.put("pDETAIL", pDETAIL);
        input.put("pAGENT", pAGENT);
        input.put("pAGENT_DETAIL", pAGENT_DETAIL);
        input.put("pHMAC", pHMAC);
        input.put("pCREATED_BY", pCREATED_BY);
        
        
        HashMap<String, Integer> output = new HashMap<>();
        output.put("pUSER_ACTIVITY_LOG_ID", java.sql.Types.BIGINT);
        output.put("pRESPONSE_CODE", java.sql.Types.NVARCHAR);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                output,
                "Create User Activity Log");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            if (row.get("pUSER_ACTIVITY_LOG_ID") != null) {
                response.setObject((long) row.get("pUSER_ACTIVITY_LOG_ID"));
            }
        }
        return response;
    }

    @Override
    public DatabaseResponse deleteUser(
            String pUSER_EMAIL,
            String pDELETED_USER_EMAIL,
            long pENTERPRISE_ID,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_USER_DELETE(?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pUSER_EMAIL", pUSER_EMAIL);
        input.put("pDELETED_USER_EMAIL", pDELETED_USER_EMAIL);
        input.put("pENTERPRISE_ID", pENTERPRISE_ID);        

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                null,
                "Delete User");

        LogHandler.debug(this.getClass(), response.getDebugString());

       return response;
    }

    @Override
    public DatabaseResponse updateUser(
            String pUSER_EMAIL,
            String pUSER_NAME,
            String pMOBILE_NUMBER, 
            Date pPASSWORD_EXPIRED_AT, 
            int pREMAINING_COUNTER, 
            String pSTATUS_NAME, 
            int pCHANGE_PASSWORD,
            boolean pLOCKED_ENABLED,
            Date pLOCKED_AT, 
            int pBUSINESS_TYPE, 
            String pORGANIZATION_WEBSITE,
            String pHMAC, 
            String pLAST_MODIFIED_BY, 
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_USER_UPDATE(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pUSER_EMAIL", pUSER_EMAIL);
        input.put("pUSER_NAME", pUSER_NAME);
        input.put("pMOBILE_NUMBER", pMOBILE_NUMBER);
        input.put("pPASSWORD_EXPIRED_AT", pPASSWORD_EXPIRED_AT);
        input.put("pREMAINING_COUNTER", pREMAINING_COUNTER <0 ? null : pREMAINING_COUNTER);
        input.put("pSTATUS_NAME", pSTATUS_NAME);
        input.put("pCHANGE_PASSWORD", pCHANGE_PASSWORD <0 ? null : pCHANGE_PASSWORD);
        input.put("pLOCKED_ENABLED", pLOCKED_ENABLED);
        input.put("pLOCKED_AT", pLOCKED_AT);
        input.put("pBUSINESS_TYPE", pBUSINESS_TYPE <0 ? null : pBUSINESS_TYPE);
        input.put("pORGANIZATION_WEBSITE", pORGANIZATION_WEBSITE);
        input.put("pHMAC", pHMAC);
        input.put("pLAST_MODIFIED_BY", pLAST_MODIFIED_BY);
             

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                null,
                "Update User");

        LogHandler.debug(this.getClass(), response.getDebugString());

       return response;
    }

    @Override
    public DatabaseResponse updateRole(
            String U_EMAIL, 
            long pENTERPRISE_ID, 
            String pROLE_NAME,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_USER_UPDATE_ROLE(?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("U_EMAIL", U_EMAIL);
        input.put("pENTERPRISE_ID", pENTERPRISE_ID);
        input.put("pROLE_NAME", pROLE_NAME);        
             

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                nameStore,
                input,
                null,
                "Update Role User");

        LogHandler.debug(this.getClass(), response.getDebugString());

       return response;
    }

    @Override
    public DatabaseResponse getUser(
            String pUSER_EMAIL, 
            long pUSER_ID, 
            long pENTERPRISE_ID,
            Class clazz,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_USER_GET(?,?,?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pUSER_EMAIL", pUSER_EMAIL);
        input.put("pUSER_ID", pUSER_ID);
        input.put("pENTERPRISE_ID", pENTERPRISE_ID);

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                clazz,
                nameStore,
                input,
                null,
                "Get User");

        LogHandler.debug(this.getClass(), response.getDebugString());

       return response;
    }

    @Override
    public DatabaseResponse getStatusUser(
            String pUSER_EMAIL,
            String transactionID) throws Exception {
        String nameStore = "{ CALL USP_USER_GET_STATUS(?,?)}";

        HashMap<String, Object> input = new HashMap<>();
        input.put("pUSER_EMAIL", pUSER_EMAIL);        

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                Account.class,
                nameStore,
                input,
                null,
                "Get Status of User");

        LogHandler.debug(this.getClass(), response.getDebugString());

       return response;
    }

    @Override
    public DatabaseResponse getTypeOfStatus() throws Exception {
        String nameStore = "{ CALL USP_USER_STATUS_LIST()}";
      

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                StatusOfAccount.class,
                nameStore,
                null,
                null,
                "Get All type Status of User");

        LogHandler.debug(this.getClass(), response.getDebugString());

       return response;
    }

    @Override
    public DatabaseResponse getListUser(
            long pENTERPRISE_ID,
            int pOFFSET,
            int pROW_COUNT,
            String transactionId) throws Exception {
        String nameStore = "{ CALL USP_USER_LIST(?,?,?,?)}";
      
         HashMap<String, Object> input = new HashMap<>();
        input.put("pENTERPRISE_ID", pENTERPRISE_ID);     
        input.put("pOFFSET", pOFFSET);     
        input.put("pROW_COUNT", pROW_COUNT);     
        
        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                Account.class,
                nameStore,
                input,
                null,
                "Get All Account belongs to User");

        LogHandler.debug(this.getClass(), response.getDebugString());

       return response;
    }
    
}
