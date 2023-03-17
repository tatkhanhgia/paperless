/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.sql.rowset.serial.SerialBlob;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.EmailTemplate;
import vn.mobileid.id.paperless.objects.Enterprise;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.RefreshToken;
import vn.mobileid.id.paperless.objects.Workflow;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;
import vn.mobileid.id.paperless.objects.WorkflowTemplate;
import vn.mobileid.id.paperless.objects.WorkflowTemplateType;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author ADMIN
 */
public class DatabaseImpl implements Database {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private int retryTimes = 1; // default no retry

    public DatabaseImpl() {
        retryTimes = Configuration.getInstance().getRetry();
        if (retryTimes == 0) {
            retryTimes = 1;
        }
    }

    /**
     * Using to get response code from DB
     *
     * @param name name of the response code
     * @return
     */
    @Override
    public ResponseCode getResponse(
            String name,
            String transactionID) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        ResponseCode responseCode = null;
        try {
            String str = "{ call SP_REST_IDENTITY_RESPONSE_CODE_GET_BY_NAME(?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);
            cals.setString("pRESPONSE_CODE_NAME", name);
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (rs != null) {
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "Transaction:" + transactionID + "\nCall getResponse successfull");
                }
                while (rs.next()) {
                    responseCode = new ResponseCode();
                    responseCode.setId(rs.getInt("ID"));
                    responseCode.setName(rs.getString("NAME"));
                    responseCode.setCode(rs.getString("ERROR"));
                    responseCode.setCode_description(rs.getString("ERROR_DESCRIPTION"));
                    break;
                }
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), "TransactionID:" + transactionID + "\nError while getting Response Code. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LogHandler.debug("Execution time of getResponse in milliseconds: " + timeElapsed / 1000000);
//        }
        return responseCode;
    }

    /**
     * Get All Response Code from DB
     *
     * @return
     */
    @Override
    public List<ResponseCode> getResponseCodes() {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        List<ResponseCode> responseCodes = new ArrayList<>();
        try {
            String str = "{ call USP_RESPONSE_GET_LIST() }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (rs != null) {
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "Call getResponseCodes successfull");
                }
                while (rs.next()) {
                    try {
                        int code = Integer.parseInt(rs.getString("NAME"));

                        ResponseCode responseCode = new ResponseCode();
                        responseCode.setName(rs.getString("NAME"));
                        responseCode.setCode_description(rs.getString("ERROR_DESCRIPTION"));
                        responseCodes.add(responseCode);

                    } catch (NumberFormatException ex) {

                    }
                }
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), "Error while getting Response Code. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getResponseCodes in milliseconds: " + timeElapsed / 1000000);
//        }
        return responseCodes;
    }

    @Override
    public List<Integer> getVerificationFunctionIDGrantForRP(
            int relyingPartyId) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        List<Integer> functionIds = new ArrayList<>();
        try {
            String str = "{ call SP_REST_VERIFICATION_FUNCTION_ATTR_LIST(?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);
            cals.setInt("pRELYING_PARTY_ID", relyingPartyId);
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (rs != null) {
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "Call getResponseCodes successfull");
                }
                while (rs.next()) {
                    functionIds.add(rs.getInt("VERIFICATION_FUNCTION_ID"));
                }
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), "Error while getting list of function id for relying party. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getVerificationFunctionIDGrantForRP in milliseconds: " + timeElapsed / 1000000);
//        }
        return functionIds;
    }

    /**
     * Create new Workflow
     *
     * @param template_type
     * @param label
     * @param created_by
     * @param email
     * @return
     */
    @Override
    public DatabaseResponse createWorkflow(int template_type,
            String label,
            String created_by,
            String email,
            int enterprise_id,
            String transactionID) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_WORKFLOW_ADD(?,?,?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setString("U_EMAIL", email);
                cals.setInt("pENTERPRISE_ID", enterprise_id);
                cals.setInt("pTEMPLATE_TYPE", template_type);
                cals.setString("pLABEL", label);
                cals.setString("pHMAC", "null");
                cals.setString("pCREATED_BY", created_by);

                cals.registerOutParameter("pWORKFLOW_ID", java.sql.Types.BIGINT);
                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
                }
                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + mysqlResult);
                }
                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pWORKFLOW_ID"));
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "Error while creating Workflow. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of createWorkflow in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Create new Workflow Template for the Workflow
     *
     * @param workflow_id
     * @param metadata
     * @param HMAC
     * @param created_by
     * @return
     */
    @Override
    public DatabaseResponse createWorkflowTemplate(
            int workflow_id,
            String metadata,
            String HMAC,
            String created_by,
            String transactionID) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_WORKFLOW_TEMPLATE_ADD(?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setInt("W_ID", workflow_id);
                cals.setString("pMETA_DATA_TEMPLATE", metadata);
                cals.setString("pHMAC", HMAC);
                cals.setString("pCREATED_BY", created_by);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
                }
                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + mysqlResult);
                }
                if (mysqlResult == 1) {
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "Error while creating Workflow Template. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of Workflow Template in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Create new User Activity Log
     *
     * @param email
     * @param enterprise_id
     * @param module
     * @param action
     * @param info_key
     * @param info_value
     * @param detail
     * @param agent
     * @param agent_detail
     * @param HMAC
     * @param create_by
     * @return
     */
    @Override
    public DatabaseResponse createUserActivityLog(
            String email,
            int enterprise_id,
            String module,
            String action,
            String info_key,
            String info_value,
            String detail,
            String agent,
            String agent_detail,
            String HMAC,
            String create_by,
            String transactionID) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_USER_ACTIVITY_LOG_ADD(?,?,?,?,?,?,?,?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setString("U_EMAIL", email);
                cals.setInt("pENTERPRISE_ID", enterprise_id);
                cals.setString("pMODULE", module);
                cals.setString("pACTION", action);
                cals.setString("pINFO_KEY", info_key);
                cals.setString("pINFO_VALUE", info_value);
                cals.setString("pDETAIL", detail);
                cals.setString("pAGENT", agent);
                cals.setString("pAGENT_DETAIL", agent_detail);
                cals.setString("pHMAC", HMAC);
                cals.setString("pCREATED_BY", create_by);

                cals.registerOutParameter("pLOG_ID", java.sql.Types.BIGINT);
                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "[SQL] " + cals.toString());
                }
                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + mysqlResult);
                }
                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pLOG_ID"));
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "Error while creating User Activity Log. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of create User ActivityLog in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Create new File Management and get ID append into WoAc
     *
     * @param UUID
     * @param name
     * @param pages
     * @param size
     * @param width
     * @param height
     * @param file_data
     * @param HMAC
     * @param created_by
     * @param DBMS
     * @return
     */
    @Override
    public DatabaseResponse createFileManagement(
            String UUID,
            String name,
            int pages,
            int size,
            int width,
            int height,
            byte[] file_data,
            String HMAC,
            String created_by,
            String DBMS,
            String transactionID
    ) {
        long startTime = System.nanoTime();
        if (name == null) {
            name = String.valueOf(startTime);
        }
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                Blob blob = null;
                if (file_data != null) {
                    blob = new SerialBlob(file_data);
                }
                String str = "{ call USP_FILE_MANAGEMENT_ADD(?,?,?,?,?,?,?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setString("pUUID", UUID);
                cals.setString("pDMS_PROPERTY", DBMS);
                cals.setString("pNAME", name);
                cals.setInt("PAGES", pages);
                cals.setInt("pSIZE", size);
                cals.setInt("pWIDTH", width);
                cals.setInt("pHEIGHT", height);
                cals.setBlob("pBINARY_DATA", blob);
                cals.setString("pHMAC", HMAC);
                cals.setString("pCREATED_BY", created_by);

                cals.registerOutParameter("pFILE_ID", java.sql.Types.BIGINT);
                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
                }
                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + mysqlResult);
                }
                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pFILE_ID"));
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), transactionID,"Error while creating FileManagement. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of create File Management in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Create new Transaction and get ID append into WoAc
     *
     * @param email
     * @param logID
     * @param UUID
     * @param type
     * @param IPAddress
     * @param initFile
     * @param pY
     * @param pX
     * @param pC
     * @param pS
     * @param pages
     * @param des
     * @param hmac
     * @param created_by
     * @return
     */
    @Override
    public DatabaseResponse createTransaction(
            String email,
            int logID,
            int UUID,
            int type,
            String IPAddress,
            String initFile,
            int pY,
            int pX,
            int pC,
            int pS,
            int pages,
            String des,
            String hmac,
            String created_by,
            String transactionID
    ) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_TRANSACTION_ADD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setString("U_EMAIL", email);
                cals.setInt("pLOG_ID", logID);
                cals.setInt("pOBJECT_ID", UUID);
                cals.setInt("pOBJECT_TYPE", type);
                cals.setString("pIP_ADDRESS", IPAddress);
                cals.setString("pINITIAL_FILE", initFile);
                cals.setInt("pY", pY);
                cals.setInt("pX", pX);
                cals.setInt("pC", pC);
                cals.setInt("pS", pS);
                cals.setInt("pPAGES", pages);
                cals.setString("pDESCRIPTION", des);
                cals.setString("pHMAC", hmac);
                cals.setString("pCREATED_BY", created_by);

                cals.registerOutParameter("pTRANSACTION_ID", java.sql.Types.VARCHAR);
                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
                }
                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + mysqlResult);
                }
                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_String(cals.getString("pTRANSACTION_ID"));
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "Error while creating Transaction. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of create Transaction in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Create new Workflow Activity
     *
     * @param enterprise_id
     * @param workflow_id
     * @param user_email
     * @param transaction_id
     * @param pFILE_CREATED_ID
     * @param csv
     * @param remark
     * @param use_test_token
     * @param enable_production
     * @param enable_update
     * @param workflow_type
     * @param request_data
     * @param HMAC
     * @param created_by
     * @return
     */
    @Override
    public DatabaseResponse createWorkflowActivity(
            int enterprise_id,
            int workflow_id,
            String user_email,
            String transaction_id,
            int pFILE_CREATED_ID,
            int csv,
            String remark,
            int use_test_token,
            int enable_production,
            int enable_update,
            int workflow_type,
            String request_data,
            String HMAC,
            String created_by,
            String transactionID) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_WORKFLOW_ACTIVITY_ADD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setInt("pENTERPRISE_ID", enterprise_id);
                cals.setInt("pWORKFLOW_ID", workflow_id);
                cals.setString("pUSER_EMAIL", user_email);
                cals.setString("pTRANSACTION_ID", transaction_id);
                cals.setInt("pFILE_CREATED_ID", pFILE_CREATED_ID);
                cals.setInt("pCSV", csv);
                cals.setString("pREMARK", remark);
                cals.setInt("pUSE_TEST_TOKEN", use_test_token);
                cals.setInt("pIS_PRODUCTION", enable_production);
                cals.setInt("pIS_UPDATE", enable_update);
                cals.setInt("pWORKFLOW_TYPE", workflow_type);
                cals.setString("pREQUEST_DATA", request_data);
                cals.setString("pHMAC", HMAC);
                cals.setString("pCREATED_BY", created_by);

                cals.registerOutParameter("pWORKFLOW_ACTIVITY_ID", java.sql.Types.BIGINT);
                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
                }
                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + mysqlResult);
                }
                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pWORKFLOW_ACTIVITY_ID"));
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "Error while creating Workflow Activity. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of create Workflow Activity in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Get Data RP (information for handshake RESTful Signing)
     *
     * @param enterprise_id
     * @return
     */
    @Override
    public DatabaseResponse getDataRP(
            int enterprise_id,
            String transactionID) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_ENTERPRISE_GET_DATA_RESTFUL_FILE_P12(?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setInt("pENTERPRISE_ID", enterprise_id);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + mysqlResult);
                }
                if (mysqlResult == 1) {
                    rs.next();
                    Enterprise temp = new Enterprise();
                    temp.setData(rs.getString("DATA_RESTFUL"));
                    temp.setFileP12_id(str);
                    databaseResponse.setObject(temp);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "Error while getting Data RelyingParty. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of get DataRP in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Create new QR for WoAc
     *
     * @param meta_data
     * @param hmac
     * @param created_by
     * @return
     */
    @Override
    public DatabaseResponse createQR(
            String meta_data,
            String hmac,
            String created_by,
            String transactionID) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_QR_ADD(?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setString("pMETA_DATA", meta_data);
                cals.setString("pHMAC", hmac);
                cals.setString("pCREATED_BY", created_by);

                cals.registerOutParameter("pQR_UUID", java.sql.Types.BIGINT);
                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
                }
                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + mysqlResult);
                }
                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pQR_UUID"));
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "Error while create QR. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of create QR in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Get File Management
     *
     * @param fileID
     * @return
     */
    @Override
    public DatabaseResponse getFileManagement(
            int fileID,
            String transactionID) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_FILE_MANAGEMENT_GET(?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setInt("pFILE_ID", fileID);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + mysqlResult);
                }
                if (mysqlResult == 1) {
                    rs.next();
                    FileManagement file = new FileManagement();
                    file.setID(rs.getString("ID"));
                    file.setUUID(rs.getString("UUID"));
                    file.setName(rs.getString("NAME"));
                    file.setPages(rs.getInt("PAGES"));
                    file.setSize(rs.getInt("SIZE"));
                    file.setWidth(rs.getInt("WIDTH"));
                    file.setHeight(rs.getInt("HEIGHT"));
                    file.setStatus(rs.getInt("STATUS"));
                    file.setData(rs.getBytes("BINARY_DATA"));
                    file.setIsSigned(rs.getBoolean("PROCESSED_ENABLED"));

                    databaseResponse.setObject(file);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "Error while getting File Management. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of get File  in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Get An Asset
     *
     * @param assetID
     * @return
     */
    @Override
    public DatabaseResponse getAsset(
            int assetID,
            String transactionID) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_ASSET_GET(?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setInt("pASSET_ID", assetID);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + mysqlResult);
                }
                if (mysqlResult == 1) {
                    rs.next();
                    Asset asset = new Asset(
                            rs.getInt("ID"),
                            rs.getString("FILE_NAME"),
                            rs.getInt("TYPE"),
                            rs.getLong("SIZE"),
                            rs.getString("UUID"),
                            rs.getDate("CREATED_AT"),
                            rs.getString("CREATED_BY"),
                            rs.getDate("LAST_MODIFIED_AT"),
                            rs.getString("LAST_MODIFIED_BY"),
                            rs.getString("USED_BY"),
                            rs.getBytes("BINARY_DATA"),
                            rs.getString("META_DATA")
                    );
                    databaseResponse.setObject(asset);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "Error while getting Asset. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of get Asset in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Upload Asset
     *
     * @param email
     * @param enterprise_id
     * @param type
     * @param file_name
     * @param size
     * @param UUID
     * @param pDBMS_PROPERTY
     * @param meta_data
     * @param file_data
     * @param hmac
     * @param createdBy
     * @return
     */
    @Override
    public DatabaseResponse uploadAsset(
            String email,
            int enterprise_id,
            int type,
            String file_name,
            long size,
            String UUID,
            String pDBMS_PROPERTY,
            String meta_data,
            byte[] file_data,
            String hmac,
            String createdBy,
            String transactionID) {
        long startTime = System.nanoTime();
        if (file_name == null) {
            file_name = String.valueOf(startTime);
        }
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                Blob blob = null;
                if (file_data != null) {
                    blob = new SerialBlob(file_data);
                }
                String str = "{ call USP_ASSET_ADD(?,?,?,?,?,?,?,?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setString("U_EMAIL", email);
                cals.setInt("pENTERPRISE_ID", enterprise_id);
                cals.setInt("pASSET_TYPE", type);
                cals.setString("pFILE_NAME", file_name);
                cals.setInt("pSIZE", (int) size);
                cals.setString("pUUID", UUID);
                cals.setString("pDMS_PROPERTY", pDBMS_PROPERTY);
                cals.setString("pMETA_DATA", meta_data);
                cals.setBlob("pBINARY_DATA", blob);
                cals.setString("pHMAC", hmac);
                cals.setString("pCREATED_BY", createdBy);

                cals.registerOutParameter("pASSET_ID", java.sql.Types.BIGINT);
                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "[SQL] " + cals.toString());
                }
                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + mysqlResult);
                }
                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pASSET_ID"));
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "Error while uploading Asset. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of upload Asset in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Get all Workflow Activity from DB
     *
     * @return
     */
    @Override
    public List<WorkflowActivity> getListWorkflowActivity() {
        List<WorkflowActivity> list = new ArrayList<>();
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        try {
            String str = "{ call USP_WORKFLOW_ACTIVITY_LIST(?,?,?,?,?,?,?,?,?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setString("U_EMAIL", "bcd@gmail.com");
            cals.setInt("pENTERPRISE_ID", 3);
            cals.setString("EMAIL_SEARCH", null);
            cals.setDate("DATE_SEARCH", null);
            cals.setString("G_TYPE", "1,2,3,4,5,6");
            cals.setString("W_A_STATUS", "1,2,3");
            cals.setInt("IS_TEST", 1);
            cals.setInt("IS_PRODUCT", 1);
            cals.setInt("IS_CUSTOM_RANGE", 0);
            cals.setDate("FROM_DATE", null);
            cals.setDate("TO_DATE", null);
            cals.setString("pLANGUAGE_NAME", "ENG");

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + mysqlResult);
            }
            if (mysqlResult == 1) {
                if (rs != null) {
                    while (rs.next()) {
                        WorkflowActivity wa = new WorkflowActivity();
                        wa.setId(rs.getInt("ID"));
                        wa.setWorkflow_id(rs.getInt("WORKFLOW_ID"));
                        wa.setWorkflow_label(rs.getString("WORKFLOW_LABEL"));
                        wa.setUser_email(rs.getString("USER"));
                        wa.setCreated_by(rs.getString("DATE"));
                        wa.setTransaction(rs.getString("TRANSACTION_ID"));

                        FileManagement file = new FileManagement();
                        file.setID(rs.getString("FILE_ID"));
                        file.setName(rs.getString("DOWNLOAD_LINK"));
//                        file.setData(rs.getBytes("BINARY_DATA"));                        
                        wa.setFile(file);

                        wa.setCSV_id(rs.getString("CSV"));
                        wa.setRemark(rs.getString("REMARK"));
                        wa.setStatus(rs.getString("STATUS_NAME"));

                        list.add(wa);
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }

        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), "Error while getting List WorkflowActivity. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of get List WA in milliseconds: " + timeElapsed / 1000000);
//        }
        return list;
    }

    /**
     * Get the WorkflowDetail of the Workflow
     *
     * @param id id of the Workflow
     * @return
     */
    @Override
    public DatabaseResponse getWorkflowDetail(
            int id,
            String transactionID) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse res = new DatabaseResponse();

        try {
            String str = "{ call USP_WORKFLOW_DETAIL_GET(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);
            cals.setInt("pWORKFLOW_ID", id);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            WorkflowDetail_Option detail = null;
            detail = new WorkflowDetail_Option();
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
            }
            if (rs != null && Integer.parseInt(cals.getString("pRESPONSE_CODE")) == 1) {
                while (rs.next()) {
                    String name = rs.getString("ATTRIBUTE_NAME");
                    detail.set(name, rs.getString("ATTRIBUTE_VALUE"));
                }
            } else {
                res.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            }
            res.setObject(detail);
            res.setStatus(PaperlessConstant.CODE_SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), "Error while getting Workflow Details - Option. Details: " + Utils.printStackTrace(e));
            }
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of get Workflow Details - Option in milliseconds: " + timeElapsed / 1000000);
//        }
        return res;
    }

    /**
     * Create new Workflow Detail for Workflow Activity
     *
     * @param id
     * @param map
     * @param hmac
     * @param created_by
     * @return
     */
    @Override
    public DatabaseResponse createWorkflowDetail(
            int id,
            HashMap<String, Object> map,
            String hmac,
            String created_by,
            String transactionID) {
        DatabaseResponse response = new DatabaseResponse();
        for (String temp : map.keySet()) {
            long startTime = System.nanoTime();
            Connection conn = null;
            ResultSet rs = null;
            CallableStatement cals = null;
            try {
                String str = "{ call USP_WORKFLOW_DETAIL_ADD(?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
                cals = conn.prepareCall(str);

                cals.setInt("W_ID", id);
                cals.setString("pATTRIBUTE_NAME", temp);
                cals.setString("pATTRIBUTE_VALUE", map.get(temp).toString());
                cals.setString("pHMAC", hmac);
                cals.setString("pCREATED_BY", created_by);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
                }
                cals.execute();
                int result = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
                }
                if (result != 1) {
                    response.setStatus(result);
                    return response;
                }
            } catch (Exception e) {
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "Error while creating workflow detail - option. Details: " + Utils.printStackTrace(e));
                }
                e.printStackTrace();
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
//            long endTime = System.nanoTime();
//            long timeElapsed = endTime - startTime;
        }
        response.setStatus(PaperlessConstant.CODE_SUCCESS);
        return response;
    }

    /**
     * Get Data Template Type of the Workflow
     *
     * @param id ID of the Workflow
     * @return
     */
    @Override
    public DatabaseResponse getTemplateType(
            int id,
            String transactionID) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        try {
            String str = "{ call USP_TEMPLATE_TYPE_GET(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);
            cals.setInt("pTEMPLATE_TYPE_ID", id);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
            }
            if (rs != null && cals.getString("pRESPONSE_CODE").equals("1")) {
                while (rs.next()) {
                    ResultSetMetaData columns = rs.getMetaData();
                    WorkflowTemplateType templateType = new WorkflowTemplateType();
                    templateType.setId(Integer.parseInt(rs.getString("ID")));
                    templateType.setName(rs.getString("TYPE_NAME"));
                    templateType.setWorkflowType(rs.getInt("WORKFLOW_TYPE"));
                    templateType.setStatus(rs.getInt("STATUS"));
                    templateType.setOrdinary(rs.getInt("ORDINARY_ID"));
                    templateType.setCode(rs.getString("CODE"));
                    templateType.setHMAC(rs.getString("HMAC"));
                    templateType.setCreated_by(rs.getString("CREATED_BY"));
                    templateType.setCreated_at(new Date(rs.getDate("CREATED_AT").getTime()));
                    templateType.setModified_by(rs.getString("LAST_MODIFIED_BY"));
                    templateType.setModified_at(new Date(rs.getDate("LAST_MODIFIED_AT").getTime()));
                    templateType.setMetadata_template(rs.getString("META_DATA_TEMPLATE_DEFAULT"));
                    templateType.setMetadata_detail(rs.getString("META_DATA_DETAIL_DEFAULT"));
                    for (int i = 1; i <= columns.getColumnCount(); i++) {
                        String name = columns.getColumnName(i);
                        if (name.contains("ENABLE")) {
                            templateType.appendData(name, rs.getInt(name));
                        }
                    }
                    response.setObject(templateType);
                    response.setStatus(PaperlessConstant.CODE_SUCCESS);
                }
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), "Error while getting Workflow Template Type. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of get Workflow Template Type in milliseconds: " + timeElapsed / 1000000);
//        }
        return response;
    }

    @Override
    public HashMap<Integer, String> initTemplateTypeForProcessClass() {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        HashMap<Integer, String> temp = new HashMap<>();
        try {
            String str = "{ call USP_TEMPLATE_TYPE_GET() }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (rs != null) {
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
                }
                while (rs.next()) {
                    temp.put(rs.getInt("ID"), rs.getString("TYPE_NAME"));
                }
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), "Error while getting Workflow Template Type. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of get Workflow Template Type in milliseconds: " + timeElapsed / 1000000);
//        }
        return temp;
    }

    /**
     * Upload/Update File Management
     *
     * @param id
     * @param UUID
     * @param DBMS
     * @param name
     * @param pages
     * @param width
     * @param height
     * @param status
     * @param hmac
     * @param created_by
     * @param last_modified_by
     * @param data
     * @return
     */
    @Override
    public DatabaseResponse updateFileManagement(
            int id,
            String UUID,
            String DBMS,
            String name,
            int pages,
            int width,
            int height,
            int status,
            String hmac,
            String created_by,
            String last_modified_by,
            byte[] data,
            boolean is_signed,
            String transactionID) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        try {
            Blob blob = null;
            if (data != null) {
                blob = new SerialBlob(data);
            }
            String str = "{ call USP_FILE_MANAGEMENT_UPDATE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setString("pUUID", UUID);
            cals.setString("pDMS_PROPERTY", DBMS);
            cals.setString("pNAME", name);
            cals.setInt("pPAGES", pages);
            cals.setInt("pSIZE", width);
            cals.setInt("pWIDTH", width);
            cals.setInt("pHEIGHT", height);
            cals.setInt("pSTATUS", status);
            cals.setBlob("pBINARY_DATA", blob);
            cals.setString("pHMAC", hmac);
            cals.setString("pCREATED_BY", created_by);
            cals.setInt("pFILE_ID", id);
            cals.setString("pLAST_MODIFIED_BY", last_modified_by);
            cals.setBoolean("pPROCESSED_ENABLED", is_signed);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
            }
            cals.execute();
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
            }
            if (cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            } else {
                response.setStatus(PaperlessConstant.CODE_FAIL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), "TransactionUD:" + transactionID + "\nError while update File Management. Details: " + Utils.printStackTrace(e));
            }
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of update File Management in milliseconds: " + timeElapsed / 1000000);
//        }
        return response;
    }

    /**
     * Get Workflow
     *
     * @param id
     * @return
     */
    @Override
    public DatabaseResponse getWorkflow(
            int id,
            String transactionID) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_WORKFLOW_GET(?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setInt("pWORKFLOW_ID", id);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
                }
                if (mysqlResult == 1) {
                    rs.next();
                    Workflow workflow = new Workflow();
                    workflow.setWorkflow_id(rs.getInt("ID"));
                    workflow.setInitiator_id(rs.getInt("INITIATOR_ID"));
                    workflow.setStatus(rs.getInt("STATUS"));
                    workflow.setTemplate_type_name(rs.getString("TEMPLATE_TYPE"));
                    workflow.setLabel(rs.getString("LABEL"));
                    workflow.setWorkflow_type_name(rs.getString("WORKFLOW_TYPE_NAME"));
                    workflow.setTemplate_type(rs.getInt("TEMPLATE_ID"));
                    workflow.setWorkflow_type(rs.getInt("WORKFLOW_TYPE"));
                    workflow.setCreated_by(rs.getString("CREATED_BY"));
                    workflow.setCreated_at(new Date(rs.getTimestamp("CREATED_AT").getTime()));
                    workflow.setLast_modified_at(new Date(rs.getTimestamp("LAST_MODIFIED_AT").getTime()));
                    workflow.setLast_modified_by(rs.getString("LAST_MODIFIED_BY"));
//                    workflow.setWorkflow_type(rs.getInt("WORKFLOW"));

                    databaseResponse.setObject(workflow);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "TransactionID:" + transactionID + "\nError while getting Workflow. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of get Workflow in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Get all Workflow Template Type in DB
     *
     * @return
     */
    @Override
    public DatabaseResponse getHashMapWorkflowTemplateType() {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        HashMap<Integer, String> list = new HashMap<>();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_TEMPLATE_TYPE_LIST() }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();

                if (rs != null) {                    
                    while (rs.next()) {
                        list.put(rs.getInt("ID"), rs.getString("TYPE_NAME"));
                    }
                    databaseResponse.setObject(list);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "Error while getting Workflow template. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of get Workflow template in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Get All Asset Type in DB
     *
     * @return
     */
    @Override
    public DatabaseResponse getAssetType() {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        HashMap<String, Integer> list = new HashMap<>();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_ASSET_TYPE_LIST() }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();

                if (rs != null) {
                    if (LogHandler.isShowDebugLog()) {
                        LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
                    }
                    while (rs.next()) {
                        list.put(rs.getString("ASSET_TYPE_NAME"), rs.getInt("ID"));
                    }
                    databaseResponse.setObject(list);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "Error while getting Asset type. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of get Asset type in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Get all Workflow from DB
     *
     * @param email
     * @param enterprise_id
     * @param status
     * @param type
     * @param use_metadata
     * @param metadata
     * @param offset
     * @param rowcount
     * @return
     */
    @Override
    public DatabaseResponse getListWorkflow(
            String email,
            int enterprise_id,
            String status,
            String type,
            boolean use_metadata,
            String metadata,
            int offset,
            int rowcount,
            String transactionID
    ) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        List<Workflow> list = new ArrayList<>();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_WORKFLOW_LIST_GET(?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);

//                cals.setString("pUSER_EMAIL", str);
                cals.setInt("pENTERPRISE_ID", enterprise_id);
//                cals.setString("pWORKFLOW_STATUS", str);
//                cals.setString("pLIST_TYPE", str);
//                cals.setBoolean("pUSE_META_DATA", result);
//                cals.setString("pMETA_DATA", str);
//                cals.setInt("pOFFSET", offset);
//                cals.setInt("pROW_COUNT", offset);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();

                if (cals.getString("pRESPONSE_CODE").equals("1")) {
                    if (LogHandler.isShowDebugLog()) {
                        LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
                    }
                    while (rs.next()) {
                        Workflow workflow = new Workflow();
                        workflow.setWorkflow_id(rs.getInt("ID"));
                        workflow.setCreated_at(rs.getDate("CREATED_AT"));
//                        workflow.setWorkflow_type(rs.getInt("WORKFLOW_TYPE"));
                        workflow.setWorkflow_type_name(rs.getString("WORKFLOW_TYPE"));
                        workflow.setLabel(rs.getString("LABEL"));
//                        workflow.setNote(rs.getString("NOTE"));
//                        workflow.setMetadata(rs.getString("METADATA"));                        
                        list.add(workflow);
                    }
                    databaseResponse.setObject(list);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "TransactionID:" + transactionID + "\nError while getting list Workflow. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of get list Workflow in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Get the Workflow Template of the Workflow
     *
     * @param id ID of the Workflow
     * @return
     */
    @Override
    public DatabaseResponse getWorkflowTemplate(
            int id,
            String transactionID
    ) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_WORKFLOW_TEMPLATE_GET(?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setInt("pWORKFLOW_ID", id);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
                }
                if (mysqlResult == 1 && rs != null) {
                    rs.next();
                    WorkflowTemplate wtem = new WorkflowTemplate();
                    wtem.setWorkflow_id(rs.getInt("WORKFLOW_ID"));
                    wtem.setMeta_data_template(rs.getString("META_DATA_TEMPLATE"));
                    wtem.setType_name(rs.getString("TEMPLATE_TYPE"));
                    wtem.setStatus(rs.getInt("STATUS"));

                    databaseResponse.setObject(wtem);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "TransactionID:" + transactionID + "\nError while getting Workflow Template. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of get Asset in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Update the Workflow Detail/Option of the Workflow
     *
     * @param id
     * @param map
     * @param hmac
     * @param created_by
     * @return
     */
    @Override
    public DatabaseResponse updateWorkflowDetail_Option(
            int id,
            HashMap<String, Object> map,
            String hmac,
            String created_by,
            String transactionID
    ) {
        DatabaseResponse response = new DatabaseResponse();
        for (String temp : map.keySet()) {
            long startTime = System.nanoTime();
            Connection conn = null;
            ResultSet rs = null;
            CallableStatement cals = null;
            try {
                String str = "{ call USP_WORKFLOW_DETAIL_UPDATE(?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
                cals = conn.prepareCall(str);

                cals.setInt("W_ID", id);
                cals.setString("pATTRIBUTE_NAME", temp);
                cals.setString("pATTRIBUTE_VALUE", map.get(temp).toString());
                cals.setString("pLAST_MODIFIED_BY", created_by);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
                }
                cals.execute();
                int result = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
                }
                if (result != 1) {
                    response.setStatus(result);
                    return response;
                }
            } catch (Exception e) {
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "TransactionID:" + transactionID + "\nError while update workflow detail - option. Details: " + Utils.printStackTrace(e));
                }
                e.printStackTrace();
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
        }
        response.setStatus(PaperlessConstant.CODE_SUCCESS);
        return response;
    }

    /**
     * Using for login
     *
     * @param email
     * @param pass
     * @return
     */
    @Override
    public DatabaseResponse login(
            String email,
            String pass,
            String transactionID
    ) {
        DatabaseResponse response = new DatabaseResponse();
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        try {
            String str = "{ call USP_LOGIN(?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setString("U_EMAIL", email);
            cals.setString("PASS", pass);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
            cals.registerOutParameter("pSTATUS_NAME", java.sql.Types.VARCHAR);
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
            }
            rs = cals.executeQuery();
            int result = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
            }
            if (result != 1) {
                response.setStatus(result);
                return response;
            }
            rs.next();
            User user = new User();
            user.setEmail(rs.getString("EMAIL"));
            user.setMobile(rs.getString("MOBILE_NUMBER"));
            user.setId(rs.getInt("ID"));
            user.setName(rs.getString("USER_NAME"));
            response.setObject(user);
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), "TransactionID:" + transactionID + "\nError while login. Details: " + Utils.printStackTrace(e));
            }
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
        response.setStatus(PaperlessConstant.CODE_SUCCESS);
        return response;
    }

    /**
     * Get Workflow Activity
     *
     * @param id ID of the Workflow Activity
     * @return
     */
    @Override
    public DatabaseResponse getWorkflowActivity(
            int id,
            String transactionID
    ) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_WORKFLOW_ACTIVITY_GET(?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setInt("pWO_AC_ID", id);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (LogHandler.isShowDebugLog()) {
                    LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
                }
                if (mysqlResult == 1) {
                    rs.next();
                    WorkflowActivity wa = new WorkflowActivity();
                    wa.setId(rs.getInt("ID"));
                    wa.setWorkflow_id(rs.getInt("WORKFLOW_ID"));
                    wa.setWorkflow_label(rs.getString("WORKFLOW_LABEL"));
                    wa.setUser_email(rs.getString("USER"));
                    wa.setCreated_by(rs.getString("DATE"));
                    wa.setTransaction(rs.getString("TRANSACTION_ID"));
                    wa.setWorkflow_template_type(rs.getInt("WORKFLOW_TEMPLATE_TYPE_ID"));

                    FileManagement file = new FileManagement();
                    file.setID(rs.getString("FILE_ID"));
                    file.setName(rs.getString("DOWNLOAD_LINK"));
                    file.setData(rs.getBytes("BINARY_DATA"));

                    wa.setFile(file);
                    wa.setCSV_id(rs.getString("CSV"));
                    wa.setRemark(rs.getString("REMARK"));
                    wa.setStatus(rs.getString("STATUS_NAME"));

                    databaseResponse.setObject(wa);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(this.getClass(), "TransactionID:" + transactionID + "\nError while getting WorkflowActivity. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of get WorkflowAtivity in milliseconds: " + timeElapsed / 1000000);
//        }
        return databaseResponse;
    }

    /**
     * Get infomation of enterprise of User
     *
     * @param email
     * @return
     */
    @Override
    public DatabaseResponse getEnterpriseInfoOfUser(
            String email,
            String transactionID
    ) {
        DatabaseResponse response = new DatabaseResponse();
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        List<Enterprise> list = new ArrayList<>();
        try {
            String str = "{ call USP_USER_GET_ENTERPRISE_INFO(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setString("pUSER_EMAIL", email);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
            }
            rs = cals.executeQuery();
            int result = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
            }
            if (result != 1) {
                response.setStatus(result);
                return response;
            }
            while (rs.next()) {
                Enterprise data = new Enterprise();
                data.setId(rs.getInt("ID"));
                data.setName(rs.getString("NAME"));
                list.add(data);
            }
            response.setObject(list);
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), "TransactionID:" + transactionID + "\nError while login. Details: " + Utils.printStackTrace(e));
            }
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
        response.setStatus(PaperlessConstant.CODE_SUCCESS);
        return response;
    }

    /**
     * Get all Workflow Template Type from DB
     *
     * @return
     */
    @Override
    public DatabaseResponse getListWorkflowTemplateType() {
        List<WorkflowTemplateType> list = new ArrayList<>();
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        try {
            String str = "{ call USP_TEMPLATE_TYPE_LIST() }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (rs != null) {
                while (rs.next()) {
                    WorkflowTemplateType data = new WorkflowTemplateType();
                    data.setId(rs.getInt("ID"));
                    data.setName(rs.getString("TYPE_NAME"));
                    list.add(data);
                }
                response.setObject(list);
            } else {
                response.setStatus(PaperlessConstant.CODE_FAIL);
                return response;
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), "Error while getting List WorkflowActivity. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of get List WA in milliseconds: " + timeElapsed / 1000000);
//        }
        response.setStatus(PaperlessConstant.CODE_SUCCESS);
        return response;
    }

    @Override
    public DatabaseResponse writeRefreshToken(
            String email,
            String session_id,
            int client_credentials_enabled,
            String client_ID,
            Date issue_on,
            Date expires_on,
            String hmac,
            String created_by,
            String transactionID
    ) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        try {
            String str = "{ call USP_REFRESH_TOKEN_ADD(?,?,?,?,?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In
            cals.setString("pUSER_EMAIL", email);
            cals.setString("pSESSION_TOKEN", session_id);
            cals.setInt("pGRANT_TYPE", client_credentials_enabled);
            cals.setString("pCLIENT_ID", client_ID);
            cals.setTimestamp("pISSUED_AT", new Timestamp(issue_on.getTime()));
            cals.setTimestamp("pEXPIRED_AT", new Timestamp(expires_on.getTime()));
            cals.setString("pHMAC", hmac);
            cals.setString("pCREATED_BY", created_by);
            //Out
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
            }
            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), "TransactionID:" + transactionID + "\nError while writing refreshToken. Details: " + Utils.printStackTrace(e));
            }
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of get List WA in milliseconds: " + timeElapsed / 1000000);
//        }
        response.setStatus(PaperlessConstant.CODE_SUCCESS);
        return response;
    }

    @Override
    public DatabaseResponse removeRefreshToken(
            String refreshtoken,
            String transactionID
    ) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        try {
            String str = "{ call USP_REFRESH_TOKEN_DELETE(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);
            //In
            cals.setString("pSESSION_TOKEN", refreshtoken);
            //Out
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
            }
            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                return response;
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), "TransactionID:" + transactionID + "\nError while remove refreshToken. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of remove refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }
        response.setStatus(PaperlessConstant.CODE_SUCCESS);
        return response;
    }

    @Override
    public DatabaseResponse checkAccessToken(
            String email,
            String accesstoken,
            String transactionID
    ) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        try {
            String str = "{ call USP_REFRESH_TOKEN_CHECK_EXISTS(?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In
            cals.setString("pUSER_EMAIL", email);
            cals.setString("pSESSION_TOKEN", accesstoken);

            //Out
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
            }
            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), "TransactionID:" + transactionID + "\nError while writing refreshToken. Details: " + Utils.printStackTrace(e));
            }
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of get List WA in milliseconds: " + timeElapsed / 1000000);
//        }
        response.setStatus(PaperlessConstant.CODE_SUCCESS);
        return response;
    }

    @Override
    public DatabaseResponse getRefreshToken(String sessionID,
            String transactionID
    ) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        try {
            String str = "{ call USP_REFRESH_TOKEN_GET(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In
            cals.setString("pSESSION_TOKEN", sessionID);

            //Out
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (cals.getString("pRESPONSE_CODE").equals("1")) {
                rs.next();
                RefreshToken data = new RefreshToken();
                data.setSessionID(rs.getString("SESSION_TOKEN"));
                data.setClient_id(rs.getString("CLIENT_ID"));
                data.setIssued_on(new Date(rs.getTimestamp("ISSUED_AT").getTime()));
                data.setExpired_on(new Date(rs.getTimestamp("EXPIRED_AT").getTime()));
                data.setEmail(rs.getString("USER_EMAIL"));
                response.setObject(data);
            } else {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                return response;
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), "TransactionID:" + transactionID + "\nError while writing refreshToken. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LogHandler.debug(this.getClass(),"Execution time of get List WA in milliseconds: " + timeElapsed / 1000000);
//        }
        response.setStatus(PaperlessConstant.CODE_SUCCESS);
        return response;
    }

    @Override
    public DatabaseResponse updateRefreshToken(
            String email,
            String session_id,
            int client_credentials_enabled,
            Date issue_on,
            Date expires_on,
            int status,
            String hmac,
            String created_by,
            String transactionID
    ) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        try {
            String str = "{ call USP_REFRESH_TOKEN_UPDATE(?,?,?,?,?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In            
            cals.setString("pUSER_EMAIL", email);
            cals.setString("pSESSION_TOKEN", session_id);
            cals.setInt("pGRANT_TYPE", client_credentials_enabled);
            cals.setTimestamp("pISSUED_AT", new Timestamp(issue_on.getTime()));
            cals.setTimestamp("pEXPIRED_AT", new Timestamp(expires_on.getTime()));
            cals.setInt("pSTATUS", status);
            cals.setString("pHMAC", hmac);
            cals.setString("pLAST_MODIFIED_BY", created_by);
            //Out
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
            }
            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                return response;
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(), "TransactionID:" + transactionID + "\nError while update refreshToken. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of update refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }
        response.setStatus(PaperlessConstant.CODE_SUCCESS);
        return response;
    }

    @Override
    public DatabaseResponse getUser(
            String email,
            int enterprise_id,
            String transactionID
    ) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        try {
            String str = "{ call USP_USER_GET(?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In            
            cals.setString("pUSER_EMAIL", email);
            cals.setInt("pENTERPRISE_ID", enterprise_id);

            //Out
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            rs.next();
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
            }
            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                return response;
            }
            User user = new User();
            user.setEmail(rs.getString("EMAIL"));
            user.setName(rs.getString("USER_NAME"));
            response.setObject(user);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(),"TransactionID:"+transactionID+"\nError while update refreshToken. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of update refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }
        response.setStatus(PaperlessConstant.CODE_SUCCESS);
        return response;
    }

    @Override
    public DatabaseResponse getKEYAPI(
            int enterprise_id,
            String client_ID,
            String transactionID) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        try {
            String str = "{ call USP_ENTERPRISE_API_KEY_GET(?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();            
            cals = conn.prepareCall(str);

            //In                        
            cals.setInt("pENTERPRISE_ID", enterprise_id);
            cals.setString("pCLIENT_ID", client_ID);
            //Out
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
            }
            System.out.println(cals.toString());
            rs = cals.executeQuery();
            rs.next();
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
            }
            System.out.println("Code:"+cals.getString("pRESPONSE_CODE"));
            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                return response;
            }
            Enterprise ent = new Enterprise();
            ent.setClientID(rs.getString("CLIENT_ID"));
            ent.setClientSecret(rs.getString("CLIENT_SECRET"));
            response.setObject(ent);
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(),
                        transactionID,
                        "Error while getKEY API. Details: " + Utils.printStackTrace(e));
            }            
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of update refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }
        response.setStatus(PaperlessConstant.CODE_SUCCESS);
        return response;
    }  

    @Override
    public DatabaseResponse getEmailTemplate(
            int language,
            String email_noti,
            String transactionID
    ) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        try {
            String str = "{ call USP_FIND_EMAIL_TEMPLATE(?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();            
            cals = conn.prepareCall(str);

            //In                        
            cals.setInt("L_ID", language);
            cals.setString("E_KEY", email_noti);

            //Out
            cals.registerOutParameter("pSUBJECT", java.sql.Types.VARCHAR);
            cals.registerOutParameter("pBODY", java.sql.Types.VARCHAR);
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            rs.next();
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
            }
            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                return response;
            }
            EmailTemplate template = new EmailTemplate();
            template.setSubject(cals.getString("pSUBJECT"));
            template.setBody(cals.getString("pBODY"));
            response.setObject(template);
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(),
                        transactionID,
                        "Error while find email template. Details: " + Utils.printStackTrace(e));
            }            
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of update refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }
        response.setStatus(PaperlessConstant.CODE_SUCCESS);
        return response;
    }

    @Override
    public DatabaseResponse getEnterpriseInfo(
            int enterprise_id        
    ) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        try {
            String str = "{ call USP_ENTERPRISE_INFO_GET(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();            
            cals = conn.prepareCall(str);

            //In                        
            cals.setInt("pENTERPRISE_ID", enterprise_id);            

            //Out
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);           
//            if (LogHandler.isShowDebugLog()) {
//                LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
//            }
            cals.execute();
            rs = cals.getResultSet();
            rs.next();
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
            }
            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                return response;
            }
            Enterprise ent = new Enterprise();
            ent.setId(rs.getInt("ID"));
            ent.setName(rs.getString("NAME"));
            ent.setEmail_notification(rs.getString("NOTIFICATION_EMAIL"));
            response.setObject(ent);
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(),
                        "Error while getting enterprise information. Details: " + Utils.printStackTrace(e));
            }            
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of update refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }
        response.setStatus(PaperlessConstant.CODE_SUCCESS);
        return response;
    }

    @Override
    public DatabaseResponse createUser(
            String email,
            String created_user_email,
            int enterprise_id,
            String role_name,
            long pass_expired_at,
            int business_type, 
            String org_web,
            String hmac,
            String transactionID){
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        try {
            String str = "{ call USP_USER_ADD(?,?,?,?,?,?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();            
            cals = conn.prepareCall(str);

            //In                        
            cals.setString("pUSER_EMAIL", email);
            cals.setString("pCREATED_USER_EMAIL", created_user_email);
            cals.setInt("pENTERPRISE_ID", enterprise_id);
            cals.setString("pROLE_NAME", role_name);
            cals.setTimestamp("pPASSWORD_EXPIRED_AT", new Timestamp(pass_expired_at));
            cals.setInt("pBUSINESS_TYPE", business_type);
            cals.setString("pORG_WEB", org_web);
            cals.setString("pHMAC", hmac);
            

            //Out
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);           
            cals.registerOutParameter("pSTATUS_NAME", java.sql.Types.VARCHAR);           
//            if (LogHandler.isShowDebugLog()) {
//                LogHandler.debug(this.getClass(), "TransactionID:" + transactionID + "\n[SQL] " + cals.toString());
//            }
            cals.execute();            
            
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(this.getClass(), "ResponseCode get from DB:" + cals.getString("pRESPONSE_CODE"));
            }
            System.out.println(cals.getString("pRESPONSE_CODE"));
            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                return response;
            }
            String status = cals.getString("pSTATUS_NAME");
            response.setObject(status);
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(this.getClass(),
                        "Error while create User. Details: " + Utils.printStackTrace(e));
            }            
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of update refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }
        response.setStatus(PaperlessConstant.CODE_SUCCESS);
        return response;
    }
}
