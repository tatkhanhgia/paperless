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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.sql.rowset.serial.SerialBlob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.kernel.ProcessELaborContract;
import vn.mobileid.id.qrypto.objects.Asset;
import vn.mobileid.id.qrypto.objects.Enterprise;
import vn.mobileid.id.qrypto.objects.FileManagement;
import vn.mobileid.id.qrypto.objects.Workflow;
import vn.mobileid.id.qrypto.objects.WorkflowActivity;
import vn.mobileid.id.qrypto.objects.WorkflowDetail_Option;
import vn.mobileid.id.qrypto.objects.WorkflowTemplate;
import vn.mobileid.id.qrypto.objects.WorkflowTemplateType;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author ADMIN
 */
public class DatabaseImpl implements Database {

    private static final Logger LOG = LogManager.getLogger(DatabaseImpl.class);
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
    public ResponseCode getResponse(String name) {
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
                LOG.debug("[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (rs != null) {
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
                LOG.error("Error while getting Response Code. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of getResponse in milliseconds: " + timeElapsed / 1000000);
        }
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
                LOG.debug("[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (rs != null) {
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
                LOG.error("Error while getting Response Code. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of getResponseCodes in milliseconds: " + timeElapsed / 1000000);
        }
        return responseCodes;
    }

    @Override
    public List<Integer> getVerificationFunctionIDGrantForRP(int relyingPartyId) {
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
                LOG.debug("[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (rs != null) {
                while (rs.next()) {
                    functionIds.add(rs.getInt("VERIFICATION_FUNCTION_ID"));
                }
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while getting list of function id for relying party. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of getVerificationFunctionIDGrantForRP in milliseconds: " + timeElapsed / 1000000);
        }
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
            int enterprise_id) {
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pWORKFLOW_ID"));
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while creating Workflow. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of createWorkflow in milliseconds: " + timeElapsed / 1000000);
        }
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
            String created_by) {
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (mysqlResult == 1) {
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while creating Workflow Template. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of Workflow Template in milliseconds: " + timeElapsed / 1000000);
        }
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
            String create_by) {
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pLOG_ID"));
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while creating User Activity Log. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of create User ActivityLog in milliseconds: " + timeElapsed / 1000000);
        }
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
     * @param fileData
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
            byte[] fileData,
            String HMAC,
            String created_by,
            String DBMS
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
                if (fileData != null) {
                    blob = new SerialBlob(fileData);
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pFILE_ID"));
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while creating FileManagement. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of create File Management in milliseconds: " + timeElapsed / 1000000);
        }
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
            String created_by) {
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_String(cals.getString("pTRANSACTION_ID"));
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while creating Transaction. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of create Transaction in milliseconds: " + timeElapsed / 1000000);
        }
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
            String created_by) {
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pWORKFLOW_ACTIVITY_ID"));
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while creating Workflow Activity. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of create Workflow Activity in milliseconds: " + timeElapsed / 1000000);
        }
        return databaseResponse;
    }

    /**
     * Get Data RP (information for handshake RESTful Signing)
     *
     * @param enterprise_id
     * @return
     */
    @Override
    public DatabaseResponse getDataRP(int enterprise_id) {
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (mysqlResult == 1) {
                    rs.next();
                    Enterprise temp = new Enterprise();
                    temp.setData(rs.getString("DATA_RESTFUL"));
                    temp.setFileP12_id(str);
                    databaseResponse.setObject(temp);
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while getting Data RelyingParty. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of get DataRP in milliseconds: " + timeElapsed / 1000000);
        }
        return databaseResponse;
    }

    /**
     * Create new QR for WoAc
     *
     * @param metaData
     * @param hmac
     * @param created_by
     * @return
     */
    @Override
    public DatabaseResponse createQR(
            String metaData,
            String hmac,
            String created_by) {
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
                cals.setString("pMETA_DATA", metaData);
                cals.setString("pHMAC", hmac);
                cals.setString("pCREATED_BY", created_by);

                cals.registerOutParameter("pQR_UUID", java.sql.Types.BIGINT);
                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LOG.debug("[SQL] " + cals.toString());
                }
                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pQR_UUID"));
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while create QR. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of create QR in milliseconds: " + timeElapsed / 1000000);
        }
        return databaseResponse;
    }

    /**
     * Get File Management
     *
     * @param fileID
     * @return
     */
    @Override
    public DatabaseResponse getFileManagement(int fileID) {
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (mysqlResult == 1) {
                    rs.next();
//                    Blob data = rs.getBlob("BINARY_DATA");

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

                    databaseResponse.setObject(file);
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while getting File Management. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of get File  in milliseconds: " + timeElapsed / 1000000);
        }
        return databaseResponse;
    }

    /**
     * Get An Asset
     *
     * @param assetID
     * @return
     */
    @Override
    public DatabaseResponse getAsset(int assetID) {
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
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
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while getting Asset. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of get Asset in milliseconds: " + timeElapsed / 1000000);
        }
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
     * @param metaData
     * @param fileData
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
            String metaData,
            byte[] fileData,
            String hmac,
            String createdBy) {
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
                if (fileData != null) {
                    blob = new SerialBlob(fileData);
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
                cals.setString("pMETA_DATA", metaData);
                cals.setBlob("pBINARY_DATA", blob);
                cals.setString("pHMAC", hmac);
                cals.setString("pCREATED_BY", createdBy);

                cals.registerOutParameter("pASSET_ID", java.sql.Types.BIGINT);
                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LOG.debug("[SQL] " + cals.toString());
                }
                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pASSET_ID"));
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while uploading Asset. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of upload Asset in milliseconds: " + timeElapsed / 1000000);
        }
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
                LOG.debug("[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
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
                LOG.error("Error while getting List WorkflowActivity. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of get List WA in milliseconds: " + timeElapsed / 1000000);
        }
        return list;
    }

    /**
     * Get the WorkflowDetail of the Workflow
     *
     * @param id id of the Workflow
     * @return
     */
    @Override
    public DatabaseResponse getWorkflowDetail(int id) {
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
                LOG.debug("[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            WorkflowDetail_Option detail = null;
            detail = new WorkflowDetail_Option();
            if (rs != null && Integer.parseInt(cals.getString("pRESPONSE_CODE")) == 1) {
                while (rs.next()) {
                    String name = rs.getString("ATTRIBUTE_NAME");
                    detail.set(name, rs.getString("ATTRIBUTE_VALUE"));
                }
            } else {
                res.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            }
            res.setObject(detail);
            res.setStatus(QryptoConstant.CODE_SUCCESS);

        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while getting Workflow Details - Option. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of get Workflow Details - Option in milliseconds: " + timeElapsed / 1000000);
        }
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
            String created_by) {
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                cals.execute();
                int result = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (result != 1) {
                    response.setStatus(result);
                    return response;
                }
            } catch (Exception e) {
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while creating workflow detail - option. Details: " + Utils.printStackTrace(e));
                }
                e.printStackTrace();
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
        }
        response.setStatus(QryptoConstant.CODE_SUCCESS);
        return response;
    }

    /**
     * Get Data Template Type of the Workflow
     *
     * @param id ID of the Workflow
     * @return
     */
    @Override
    public DatabaseResponse getTemplateType(int id) {
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
                LOG.debug("[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
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
                    templateType.setCreated_at(rs.getDate("CREATED_AT"));
                    templateType.setModified_by(rs.getString("LAST_MODIFIED_BY"));
                    templateType.setModified_at(rs.getDate("LAST_MODIFIED_AT"));
                    templateType.setMetadata_template(rs.getString("META_DATA_TEMPLATE_DEFAULT"));
                    templateType.setMetadata_detail(rs.getString("META_DATA_DETAIL_DEFAULT"));
                    for (int i = 1; i <= columns.getColumnCount(); i++) {
                        String name = columns.getColumnName(i);
                        if (name.contains("ENABLE")) {
                            templateType.appendData(name, rs.getInt(name));
                        }
                    }
                    response.setObject(templateType);
                    response.setStatus(QryptoConstant.CODE_SUCCESS);
                }
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while getting Workflow Template Type. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of get Workflow Template Type in milliseconds: " + timeElapsed / 1000000);
        }
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
                LOG.debug("[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (rs != null) {
                while (rs.next()) {
                    temp.put(rs.getInt("ID"), rs.getString("TYPE_NAME"));
                }
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while getting Workflow Template Type. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of get Workflow Template Type in milliseconds: " + timeElapsed / 1000000);
        }
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
            byte[] data) {
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
            String str = "{ call USP_FILE_MANAGEMENT_UPDATE(?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
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

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
            if (LogHandler.isShowDebugLog()) {
                LOG.debug("[SQL] " + cals.toString());
            }
            cals.execute();
            if (cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(QryptoConstant.CODE_SUCCESS);
            } else {
                response.setStatus(QryptoConstant.CODE_FAIL);
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while update File Management. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of update File Management in milliseconds: " + timeElapsed / 1000000);
        }
        return response;
    }

    /**
     * Get Workflow
     *
     * @param id
     * @return
     */
    @Override
    public DatabaseResponse getWorkflow(int id) {
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (mysqlResult == 1) {
                    rs.next();
                    Workflow workflow = new Workflow();
                    workflow.setWorkflow_id(rs.getInt("ID"));
                    workflow.setInitiator_id(rs.getInt("INITIATOR_ID"));
                    workflow.setStatus(rs.getInt("STATUS"));
                    workflow.setTemplate_type_name(rs.getString("TEMPLATE_TYPE"));
                    workflow.setLabel(rs.getString("LABEL"));
                    workflow.setWorkflow_type_name(rs.getString("WORKFLOW_TYPE"));
                    workflow.setTemplate_type(rs.getInt("TEMPLATE_ID"));
//                    workflow.setWorkflow_type(rs.getInt("WORKFLOW"));

                    databaseResponse.setObject(workflow);
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while getting Workflow. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of get Workflow in milliseconds: " + timeElapsed / 1000000);
        }
        return databaseResponse;
    }

    /**
     * Get all Workflow Template Type in DB
     *
     * @return
     */
    @Override
    public DatabaseResponse getAllWorkflowTemplateType() {
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();

                if (rs != null) {
                    while (rs.next()) {
                        list.put(rs.getInt("ID"), rs.getString("TYPE_NAME"));
                    }
                    databaseResponse.setObject(list);
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while getting Workflow template. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of get Workflow template in milliseconds: " + timeElapsed / 1000000);
        }
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();

                if (rs != null) {
                    while (rs.next()) {
                        list.put(rs.getString("ASSET_TYPE_NAME"), rs.getInt("ID"));
                    }
                    databaseResponse.setObject(list);
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while getting Asset type. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of get Asset type in milliseconds: " + timeElapsed / 1000000);
        }
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
            int rowcount) {
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();

                if (rs != null && cals.getString("pRESPONSE_CODE").equals("1")) {
                    while (rs.next()) {
                        Workflow workflow = new Workflow();
                        workflow.setWorkflow_id(rs.getInt("ID"));
//                        workflow.setCreated_at(rs.getDate("DATE_CREATED"));
//                        workflow.setWorkflow_type(rs.getInt("WORKFLOW_TYPE"));
                        workflow.setLabel(rs.getString("LABEL"));
//                        workflow.setNote(rs.getString("NOTE"));
//                        workflow.setMetadata(rs.getString("METADATA"));                        
                        list.add(workflow);
                    }
                    databaseResponse.setObject(list);
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while getting list Workflow. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of get list Workflow in milliseconds: " + timeElapsed / 1000000);
        }
        return databaseResponse;
    }

    /**
     * Get the Workflow Template of the Workflow
     *
     * @param id ID of the Workflow
     * @return
     */
    @Override
    public DatabaseResponse getWorkflowTemplate(int id) {
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (mysqlResult == 1 && rs != null) {
                    rs.next();
                    WorkflowTemplate wtem = new WorkflowTemplate();
                    wtem.setWorkflow_id(rs.getInt("WORKFLOW_ID"));
                    wtem.setMeta_data_template(rs.getString("META_DATA_TEMPLATE"));
                    wtem.setType_name(rs.getString("TEMPLATE_TYPE"));
                    wtem.setStatus(rs.getInt("STATUS"));

                    databaseResponse.setObject(wtem);
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while getting Workflow Template. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of get Asset in milliseconds: " + timeElapsed / 1000000);
        }
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
            String created_by
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                cals.execute();
                int result = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
                if (result != 1) {
                    response.setStatus(result);
                    return response;
                }
            } catch (Exception e) {
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while update workflow detail - option. Details: " + Utils.printStackTrace(e));
                }
                e.printStackTrace();
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
        }
        response.setStatus(QryptoConstant.CODE_SUCCESS);
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
            String pass) {
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
                LOG.debug("[SQL] " + cals.toString());
            }
            rs = cals.executeQuery();
            int result = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
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
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while login. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        response.setStatus(QryptoConstant.CODE_SUCCESS);
        return response;
    }

    /**
     * Get Workflow Activity
     *
     * @param id ID of the Workflow Activity
     * @return
     */
    @Override
    public DatabaseResponse getWorkflowActivity(int id) {
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
                    LOG.debug("[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
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
                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                numOfRetry--;
                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while getting WorkflowActivity. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of get WorkflowAtivity in milliseconds: " + timeElapsed / 1000000);
        }
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
            String email
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
                LOG.debug("[SQL] " + cals.toString());
            }
            rs = cals.executeQuery();
            int result = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
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
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while login. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        response.setStatus(QryptoConstant.CODE_SUCCESS);
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
                LOG.debug("[SQL] " + cals.toString());
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
                response.setStatus(QryptoConstant.CODE_FAIL);
                return response;
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while getting List WorkflowActivity. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of get List WA in milliseconds: " + timeElapsed / 1000000);
        }
        response.setStatus(QryptoConstant.CODE_SUCCESS);
        return response;
    }
}
