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
import java.util.ArrayList;
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
import vn.mobileid.id.utils.Crypto;
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
//                            responseCode.setId(rs.getInt("ID"));
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
                    LOG.error("Error while inserting agreement. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of insertAgreement in milliseconds: " + timeElapsed / 1000000);
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
                    LOG.error("Error while inserting agreement. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of insertAgreement in milliseconds: " + timeElapsed / 1000000);
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
                    LOG.error("Error while inserting agreement. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of insertAgreement in milliseconds: " + timeElapsed / 1000000);
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
                    LOG.error("Error while inserting agreement. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of insertAgreement in milliseconds: " + timeElapsed / 1000000);
        }
        return databaseResponse;
    }

//    @Override
//    public DatabaseResponse addEnterpriseUser(
//            String email_owner,
//            int enterprise_id,
//            String email_user,
//            String role,
//            int status,
//            String hmac) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        boolean result = false;
//        DatabaseResponse databaseResponse = new DatabaseResponse();
//        int numOfRetry = retryTimes;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call USP_ENTERPRISE_ADD_USER(?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setString("CREATED_EMAIL", email_owner);
//                cals.setInt("pENTERPRISE_ID", enterprise_id);
//                cals.setString("U_EMAIL", email_user);
//                cals.setString("E_ROLE_NAME", role);
//                cals.setInt("E_U_STATUS", status);
//                cals.setString("E_U_HMAC", hmac);
//
//                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
//
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
//                if (mysqlResult == 1) {
//                    databaseResponse.setID_Response_int(mysqlResult);
//                    databaseResponse.setStatus(QryptoConstant.CODE_SUCCESS);
//                } else {
//                    databaseResponse.setStatus(mysqlResult);
//                }
//                break;
//            } catch (Exception e) {
//                e.printStackTrace();
//                numOfRetry--;
//                databaseResponse.setStatus(QryptoConstant.CODE_FAIL);
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while inserting agreement. Details: " + Utils.printStackTrace(e));
//                }
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of insertAgreement in milliseconds: " + timeElapsed / 1000000);
//        }
//        return databaseResponse;
//    }
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
                    LOG.error("Error while inserting agreement. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of insertAgreement in milliseconds: " + timeElapsed / 1000000);
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
                    LOG.error("Error while inserting agreement. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of insertAgreement in milliseconds: " + timeElapsed / 1000000);
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
                    LOG.error("Error while inserting agreement. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of insertAgreement in milliseconds: " + timeElapsed / 1000000);
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
                    LOG.error("Error while inserting agreement. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of insertAgreement in milliseconds: " + timeElapsed / 1000000);
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
                    LOG.error("Error while inserting agreement. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of insertAgreement in milliseconds: " + timeElapsed / 1000000);
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
                String str = "{ call USP_FILE_MANAGEMENT_GET(?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setInt("pFILE_ID", assetID);

                cals.registerOutParameter("pRESPONE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LOG.debug("[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONE_CODE"));
                if (mysqlResult == 1) {
                    rs.next();
                    databaseResponse.setObject(new Asset(
                            0,  //id
                            "name", //name
                            1, //type
                            100, //size
                            1, //file uuid
                            "created_at", //created at
                            "created by", //created by
                            "modifued at", //modified at
                            "modified by", //modified by
                            "workflow_used" //used by
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
                    LOG.error("Error while inserting agreement. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of insertAgreement in milliseconds: " + timeElapsed / 1000000);
        }
        return databaseResponse;
    }

    @Override
    public DatabaseResponse getFileAsset(int assetID) {
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
                cals.setInt("pFILE_ID", assetID);

                cals.registerOutParameter("pRESPONE_CODE", java.sql.Types.VARCHAR);

                if (LogHandler.isShowDebugLog()) {
                    LOG.debug("[SQL] " + cals.toString());
                }
                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONE_CODE"));
                if (mysqlResult == 1) {
                    rs.next();
                    databaseResponse.setObject(new Asset(
                            0,  //id
                            "name", //name
                            1, //type
                            100, //size
                            1, //file uuid
                            "created_at", //created at
                            "created by", //created by
                            "modifued at", //modified at
                            "modified by", //modified by
                            "workflow_used" //used by
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
                    LOG.error("Error while inserting agreement. Details: " + Utils.printStackTrace(e));
                }
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of insertAgreement in milliseconds: " + timeElapsed / 1000000);
        }
        return databaseResponse;
    }

}
