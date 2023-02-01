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
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.objects.Asset;
import vn.mobileid.id.qrypto.objects.Enterprise;
import vn.mobileid.id.qrypto.objects.FileManagement;
import vn.mobileid.id.qrypto.objects.WorkflowActivity;
import vn.mobileid.id.qrypto.objects.WorkflowDetail_Option;
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

    @Override
    public DatabaseResponse createWorkflow(int template_type,
            String label,
            String created_by,
            String email) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_WORKFLOW_ADD(?,?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setString("U_EMAIL", email);
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
                    databaseResponse.setID_Response_String(cals.getString("pWORKFLOW_ID"));
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

    public DatabaseResponse createFileManagement(
            String UUID,
            String name,
            int pages,
            int size,
            int width,
            int height,
            byte[] fileData,
            String HMAC,
            String created_by
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
                String str = "{ call USP_FILE_MANAGEMENT_ADD(?,?,?,?,?,?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setString("pUUID", UUID);
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

    @Override
    public DatabaseResponse getFile(int fileID) {
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

                cals.registerOutParameter("pRESPONE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LOG.debug("[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONE_CODE"));
                if (mysqlResult == 1) {
                    rs.next();
//                    Blob data = cals.getBlob("");
                    databaseResponse.setObject(new FileManagement(
                            //                            data.getBytes(1, (int) data.length()),
                            null,
                            rs.getString("NAME"),
                            rs.getString("ID"),
                            //                            rs.getString("CREATED_BY") 
                            null
                    ));
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
                            rs.getString("CREATED_AT"),
                            rs.getString("CREATED_BY"),
                            rs.getString("LAST_MODIFIED_AT"),
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

    @Override
    public DatabaseResponse uploadAsset(
            String email,
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
                String str = "{ call USP_ASSET_ADD(?,?,?,?,?,?,?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setString("U_EMAIL", email);
                cals.setInt("pASSET_TYPE", type);
                cals.setString("pFILE_NAME", file_name);
                cals.setInt("pSIZE", (int) size);
                cals.setString("pUUID", UUID);
                cals.setString("pDBMS_PROPERTY", pDBMS_PROPERTY);
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
//                        file.setID(str);
                        file.setName(rs.getString("DOWNLOAD_LINK"));
                        file.setData(rs.getBytes("BINARY_DATA"));
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

    @Override
    public DatabaseResponse updateWorkflowDetail(
            int id,
            HashMap<String, Object> map,
            String hmac,
            String created_by) {
        DatabaseResponse response = new DatabaseResponse();        
        for(String temp : map.keySet()){
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

                cals.registerOutParameter("pRESPONE_CODE", java.sql.Types.VARCHAR);
                if (LogHandler.isShowDebugLog()) {
                    LOG.debug("[SQL] " + cals.toString());
                }
                cals.execute();
                int result = Integer.parseInt(cals.getString("pRESPONE_CODE"));
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

    @Override
    public DatabaseResponse getTemplateType(int id) {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        ResponseCode responseCode = null;
        try {
            String str = "{ call USP_WORKFLOW_GET_TEMPLATE_TYPE_INFO(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);            
            cals.setInt("pWORKFLOW_ID", id);
            
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
            if (LogHandler.isShowDebugLog()) {
                LOG.debug("[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (rs != null && cals.getString("pRESPONSE_CODE").equals("1")) {
                while (rs.next()) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    WorkflowTemplateType templateType = new WorkflowTemplateType();
                    templateType.setId(Integer.parseInt(rs.getString("ID")));
                    templateType.setName(rs.getString("TYPE_NAME"));
                    templateType.setWorkflowType(rs.getInt("WORKFLOW_TYPE"));
                    templateType.setStatus(rs.getInt(""));
                    templateType.setOrdinary(rs.getInt(""));
                    templateType.setCode(rs.getString("ID"));
                    templateType.setHMAC(rs.getString("HMAC"));
                    templateType.setCreated_by(rs.getString("CREATED_BY"));
                    templateType.setCreated_at(rs.getString("CREATED_AT"));
                    templateType.setModified_by(rs.getString("LAST_MODIFIED_BY"));
                    templateType.setModified_at(rs.getString("LAST_MODIFIED_AT"));
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
}
