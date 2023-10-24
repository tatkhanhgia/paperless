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
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.sql.rowset.serial.SerialBlob;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.general.Configuration;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.policy.object.PolicyResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.object.enumration.FileType;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.EmailTemplate;
import vn.mobileid.id.paperless.objects.Enterprise;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.QRSize;
import vn.mobileid.id.paperless.objects.RefreshToken;
import vn.mobileid.id.paperless.objects.Transaction;
import vn.mobileid.id.paperless.objects.Workflow;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;
import vn.mobileid.id.paperless.objects.WorkflowTemplate;
import vn.mobileid.id.paperless.objects.WorkflowTemplateType;

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
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        ResponseCode responseCode = null;
        String debugString = transactionID + "\n";
        try {
            String str = "{ call SP_REST_IDENTITY_RESPONSE_CODE_GET_BY_NAME(?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);
            cals.setString("pRESPONSE_CODE_NAME", name);

            debugString += "\t[SQL] " + cals.toString();

            cals.execute();
            rs = cals.getResultSet();
            if (rs != null) {
                debugString += "\n\tCall getResponse successfull\n";

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
            throw new Exception("Error while getting Response Code", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        LogHandler.debug(this.getClass(), debugString);
        return responseCode;
    }

    /**
     * Get All Response Code from DB
     *
     * @return
     */
    @Override
    public List<ResponseCode> getResponseCodes() throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        List<ResponseCode> responseCodes = new ArrayList<>();
        String debugString = "\n";
        try {
            String str = "{ call USP_RESPONSE_GET_LIST() }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            debugString += "\t[SQL] " + cals.toString();

            cals.execute();
            rs = cals.getResultSet();
            if (rs != null) {
                debugString += "\n\tCall getResponseCodes successfull";

                while (rs.next()) {
                    int code = Integer.parseInt(rs.getString("NAME"));

                    ResponseCode responseCode = new ResponseCode();
                    responseCode.setName(rs.getString("NAME"));
                    responseCode.setCode_description(rs.getString("ERROR_DESCRIPTION"));
                    responseCodes.add(responseCode);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error while getting Response Code!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        LogHandler.debug(this.getClass(), debugString);
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

            LogHandler.debug(this.getClass(), "\t[SQL] " + cals.toString());

            cals.execute();
            rs = cals.getResultSet();
            if (rs != null) {

                LogHandler.debug(this.getClass(), "\n\tCall getResponseCodes successfull");

                while (rs.next()) {
                    functionIds.add(rs.getInt("VERIFICATION_FUNCTION_ID"));
                }
            }
        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LogHandler.error(this.getClass(), "Error while getting list of function id for relying party. Details: " + Utils.printStackTrace(e));
//            }
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
//         
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
     * @param enterprise_id
     * @return
     */
    @Override
    public DatabaseResponse createWorkflow(
            int template_type,
            String label,
            String created_by,
            String email,
            int enterprise_id,
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = transactionID + "\n";
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

                debugString += "\t[SQL] " + cals.toString();

                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                debugString += "\n\tResponseCode get from DB:" + mysqlResult;

                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pWORKFLOW_ID"));
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while creating Workflow!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;        
        LogHandler.debug(this.getClass(), debugString);
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
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        boolean result = false;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = transactionID + "\n";
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

                debugString += "\t[SQL] " + cals.toString();

                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                debugString += "\n\tResponseCode get from DB:" + mysqlResult;

                if (mysqlResult == 1) {
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while creating Workflow Template!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;        
        LogHandler.debug(this.getClass(), debugString);
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
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        String debugString = transactionID + "\n";
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

                debugString += "\t[SQL] " + cals.toString();

                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                debugString += "\n\tResponseCode get from DB:" + mysqlResult;

                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pLOG_ID"));
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while creating User Activity Log!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;                
        LogHandler.debug(this.getClass(), debugString);
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
     * @param file_type
     * @param signing_properties
     * @param hash_values
     * @param transactionID
     * @return
     */
    @Override
    public DatabaseResponse createFileManagement(
            String UUID,
            String name,
            int pages,
            int size,
            float width,
            float height,
            byte[] file_data,
            String HMAC,
            String created_by,
            String DBMS,
            String file_type,
            String signing_properties,
            String hash_values,
            String transactionID
    ) throws Exception {
        long startTime = System.nanoTime();
        if (name == null) {
            name = String.valueOf(startTime);
        }
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        String debugString = transactionID + "\n";
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        while (numOfRetry > 0) {
            try {
                Blob blob = null;
                if (file_data != null) {
                    blob = new SerialBlob(file_data);
                }
                String str = "{ call USP_FILE_MANAGEMENT_ADD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
//                String str = "{ call USP_FILE_MANAGEMENT_ADD(?,?,?,?,?,?,?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setString("pUUID", UUID);
                cals.setString("pDMS_PROPERTY", DBMS);
                cals.setString("pNAME", name);
                cals.setObject("PAGES", pages <= 0 ? null : pages);
                cals.setObject("pSIZE", size <= 0 ? null : size);
                cals.setObject("pWIDTH", width <= 0 ? null : width);
                cals.setObject("pHEIGHT", height <= 0 ? null : height);
                cals.setBlob("pBINARY_DATA", blob);
                cals.setString("pHMAC", HMAC);
                cals.setString("pCREATED_BY", created_by);
                cals.setString("pFILE_TYPE", file_type);
                cals.setString("pSIGNING_PROPERTIES", signing_properties);
                cals.setString("pHASH_VALUES", hash_values);

                cals.registerOutParameter("pFILE_ID", java.sql.Types.BIGINT);
                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                debugString += "\t[SQL] " + cals.toString();

                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                debugString += "\n\tResponseCode get from DB:" + mysqlResult;

                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pFILE_ID"));
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while creating FileManagement!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of create File Management in milliseconds: " + timeElapsed / 1000000);
//        }        
        LogHandler.debug(this.getClass(), debugString);
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
    ) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = transactionID + "\n";
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

                debugString += "\t[SQL] " + cals.toString();

                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                debugString += "\n\tResponseCode get from DB:" + mysqlResult;

                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_String(cals.getString("pTRANSACTION_ID"));
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while creating Transaction!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of create Transaction in milliseconds: " + timeElapsed / 1000000);
//        }        
        LogHandler.debug(this.getClass(), debugString);
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
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = transactionID + "\n";
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

                debugString += "\t[SQL] " + cals.toString();

                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                debugString += "\n\tResponseCode get from DB:" + mysqlResult;

                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pWORKFLOW_ACTIVITY_ID"));
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while creating Workflow Activity!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of create Workflow Activity in milliseconds: " + timeElapsed / 1000000);
//        }        
        LogHandler.debug(this.getClass(), debugString);
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
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = transactionID + "\n";
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_ENTERPRISE_GET_DATA_RESTFUL_FILE_P12(?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setInt("pENTERPRISE_ID", enterprise_id);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                debugString += "\t[SQL] " + cals.toString();

                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                debugString += "\n\tResponseCode get from DB:" + mysqlResult;

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
                numOfRetry--;
                throw new Exception("Error while getting Data RelyingParty!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of get DataRP in milliseconds: " + timeElapsed / 1000000);
//        }        
        LogHandler.debug(this.getClass(), debugString);
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
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = transactionID + "\n";
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

                debugString += "\t[SQL] " + cals.toString();

                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                debugString += "\n\tResponseCode get from DB:" + mysqlResult;

                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pQR_UUID"));
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while create QR!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of create QR in milliseconds: " + timeElapsed / 1000000);
//        }        
        LogHandler.debug(this.getClass(), debugString);
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
            long fileID,
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        ResultSet rs = null;
        String debugString = transactionID + "\n";
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_FILE_MANAGEMENT_GET(?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setLong("pFILE_ID", fileID);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                debugString += "\t[SQL] " + cals.toString();

                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                debugString += "\n\tResponseCode get from DB:" + mysqlResult;

                if (mysqlResult == 1) {
                    rs.next();
                    FileManagement file = new FileManagement();
                    file.setID(rs.getLong("ID"));
                    file.setUUID(rs.getString("UUID"));
                    file.setName(rs.getString("NAME"));
                    file.setPages(rs.getInt("PAGES"));
                    file.setSize(rs.getLong("SIZE"));
                    file.setWidth(rs.getFloat("WIDTH"));
                    file.setHeight(rs.getFloat("HEIGHT"));
                    file.setStatus(rs.getInt("STATUS"));
                    file.setData(rs.getBytes("BINARY_DATA"));
                    file.setIsSigned(rs.getBoolean("PROCESSED_ENABLED"));
                    if (rs.getString("FILE_TYPE").equals(FileType.PDF.getName())) {
                        file.setFile_type(FileType.PDF);
                    }
                    if (rs.getString("FILE_TYPE").equals(FileType.WORD.getName())) {
                        file.setFile_type(FileType.WORD);
                    }
                    if (rs.getString("FILE_TYPE").equals(FileType.XSLT.getName())) {
                        file.setFile_type(FileType.XSLT);
                    }

                    databaseResponse.setObject(file);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while getting File Management!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of get File  in milliseconds: " + timeElapsed / 1000000);
//        }        
        LogHandler.debug(this.getClass(), debugString);
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
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = transactionID + "\n";
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_ASSET_GET(?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setInt("pASSET_ID", assetID);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                debugString += "\t[SQL] " + cals.toString();

                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                debugString += "\n\tResponseCode get from DB:" + mysqlResult;

                if (mysqlResult == 1) {
                    rs.next();
                    Asset asset = new Asset(
                            rs.getInt("ID"),
                            rs.getString("FILE_NAME"),
                            rs.getInt("TYPE"),
                            rs.getLong("SIZE"),
                            rs.getString("UUID"),
                            new Date(rs.getTimestamp("CREATED_AT").getTime()),
                            rs.getString("CREATED_BY"),
                            new Date(rs.getTimestamp("LAST_MODIFIED_AT").getTime()),
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
                numOfRetry--;
                throw new Exception("Error while getting Asset!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of get Asset in milliseconds: " + timeElapsed / 1000000);
//        }        
        LogHandler.debug(this.getClass(), debugString);
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
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        if (file_name == null) {
            file_name = String.valueOf(startTime);
        }
        Connection conn = null;
        CallableStatement cals = null;
        String debugString = transactionID + "\n";
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

//                    
                debugString += "\t[SQL] " + cals.toString();

                cals.execute();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                debugString += "\n\tResponseCode get from DB:" + mysqlResult;

                if (mysqlResult == 1) {
                    databaseResponse.setID_Response_int(cals.getInt("pASSET_ID"));
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while uploading Asset!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of upload Asset in milliseconds: " + timeElapsed / 1000000);
//        }       
        LogHandler.debug(this.getClass(), debugString);
        return databaseResponse;
    }

    /**
     * Get all Workflow Activity from DB
     *
     * @return
     */
    @Override
    public List<WorkflowActivity> getListWorkflowActivity() throws Exception {
        List<WorkflowActivity> list = new ArrayList<>();
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        String debugString = "\n";
        try {
            String str = "{ call USP_WORKFLOW_ACTIVITY_LIST(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
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
            cals.setInt("pOFFSET", 0);
            cals.setInt("pROW_COUNT", 100);
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();
            rs = cals.getResultSet();
            int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

            debugString += "\n\tResponseCode get from DB:" + mysqlResult;

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
                        wa.setEnterprise_id(rs.getInt("ENTERPRISE_ID"));

                        FileManagement file = new FileManagement();
                        file.setID(rs.getLong("FILE_ID"));
                        file.setName(rs.getString("DOWNLOAD_LINK"));
//                        file.setData(rs.getBytes("BINARY_DATA"));                        
                        wa.setFile(file);
                        wa.setRequestData(rs.getString("REQUEST_DATA"));
                        wa.setCSV_id(rs.getString("CSV"));
                        wa.setRemark(rs.getString("REMARK"));
                        wa.setStatus_name(rs.getString("STATUS_NAME"));
                        wa.setCreated_at(new Date(rs.getTimestamp("DATE").getTime()));
                        list.add(wa);
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new Exception("Error while getting List WorkflowActivity!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        LogHandler.debug(this.getClass(), debugString);
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
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse res = new DatabaseResponse();
        String debugString = transactionID + "\n";

        try {
            String str = "{ call USP_WORKFLOW_DETAIL_GET(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);
            cals.setInt("pWORKFLOW_ID", id);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();
            rs = cals.getResultSet();
            WorkflowDetail_Option detail = new WorkflowDetail_Option();

            debugString += "\n\tResponseCode get from DB:" + Integer.parseInt(cals.getString("pRESPONSE_CODE"));

            if (rs != null && Integer.parseInt(cals.getString("pRESPONSE_CODE")) == 1) {
                while (rs.next()) {
                    String name = rs.getString("ATTRIBUTE_NAME");
                    detail.set(name, rs.getString("ATTRIBUTE_VALUE"));
                }
                res.setObject(detail);
                res.setStatus(PaperlessConstant.CODE_SUCCESS);
            } else {
                res.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            }
        } catch (Exception e) {
            throw new Exception("Error while getting Workflow Details - Option!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of get Workflow Details - Option in milliseconds: " + timeElapsed / 1000000);
//        }        
        LogHandler.debug(this.getClass(), debugString);
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
            String transactionID) throws Exception {
        DatabaseResponse response = new DatabaseResponse();
        response.setStatus(PaperlessConstant.CODE_SUCCESS);
        String debugString = transactionID + "\n";
        for (String temp : map.keySet()) {
            long startTime = System.nanoTime();
            Connection conn = null;
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

                debugString += "\t[SQL] " + cals.toString();

                cals.execute();
                int result = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                debugString += "\tResponseCode get from DB:" + result + "\n";

                if (result != 1) {
                    response.setStatus(result);
                }
            } catch (Exception e) {
                throw new Exception("Error while creating workflow detail - option!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
//            long endTime = System.nanoTime();
//            long timeElapsed = endTime - startTime;
        }
        LogHandler.debug(this.getClass(), debugString);

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
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_TEMPLATE_TYPE_GET(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);
            cals.setInt("pTEMPLATE_TYPE_ID", id);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();
            rs = cals.getResultSet();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

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
                    templateType.setCreated_at(new Date(rs.getTimestamp("CREATED_AT").getTime()));
                    templateType.setModified_by(rs.getString("LAST_MODIFIED_BY"));
                    templateType.setModified_at(new Date(rs.getTimestamp("LAST_MODIFIED_AT").getTime()));
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
            } else {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            }
        } catch (Exception e) {
            throw new Exception("Error while getting Workflow Template Type!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of get Workflow Template Type in milliseconds: " + timeElapsed / 1000000);
//        }        
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    @Override
    public HashMap<Integer, String> initTemplateTypeForProcessClass() throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        HashMap<Integer, String> temp = new HashMap<>();
        String debugString = "\n";
        try {
            String str = "{ call USP_TEMPLATE_TYPE_GET() }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);
      
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();
            rs = cals.getResultSet();
            if (rs != null) {
                while (rs.next()) {
                    temp.put(rs.getInt("ID"), rs.getString("TYPE_NAME"));
                }
            }
        } catch (Exception e) {
            throw new Exception("Error while getting Workflow Template Type!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of get Workflow Template Type in milliseconds: " + timeElapsed / 1000000);
//        }        

        LogHandler.debug(
                this.getClass(), debugString);
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
     * @param size
     * @param width
     * @param height
     * @param status
     * @param hmac
     * @param created_by
     * @param last_modified_by
     * @param data
     * @param is_signed
     * @param file_type
     * @param signing_properties
     * @param hash_values
     * @param transactionID
     * @return
     * @throws java.lang.Exception
     */
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
            byte[] data,
            boolean is_signed,
            String file_type,
            String signing_properties,
            String hash_values,
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";
        try {
            Blob blob = null;
            if (data != null) {
                blob = new SerialBlob(data);
            }
            String str = "{ call USP_FILE_MANAGEMENT_UPDATE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
//            String str = "{ call USP_FILE_MANAGEMENT_UPDATE(?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setString("pUUID", UUID == null ? name : UUID);
            cals.setString("pDMS_PROPERTY", DBMS);
            cals.setString("pNAME", name == null ? UUID : name);
            cals.setObject("pPAGES", pages == 0 ? null : pages, Types.INTEGER);
            cals.setObject("pSIZE", size == 0 ? null : size, Types.INTEGER);
            cals.setObject("pWIDTH", width == 0 ? null : width, Types.INTEGER);
            cals.setObject("pHEIGHT", height == 0 ? null : height, Types.INTEGER);
            cals.setInt("pSTATUS", status < 0 || status > 1 ? null : status);
            cals.setBlob("pBINARY_DATA", blob);
            cals.setString("pHMAC", hmac);
            cals.setLong("pFILE_ID", id);
            cals.setString("pLAST_MODIFIED_BY", last_modified_by);
            cals.setBoolean("pPROCESSED_ENABLED", is_signed);
            cals.setString("pFILE_TYPE", file_type);
            cals.setString("pSIGNING_PROPERTIES", signing_properties);
            cals.setString("pHASH_VALUES", hash_values);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

            debugString += "\t[SQL] " + cals.toString();

            cals.execute();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            } else {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while update File Management!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of update File Management in milliseconds: " + timeElapsed / 1000000);
//        }        
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    /**
     * Get Workflow
     *
     * @param id
     * @param transactionID
     * @return
     */
    @Override
    public DatabaseResponse getWorkflow(
            int id,
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = transactionID + "\n";
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_WORKFLOW_GET(?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setInt("pWORKFLOW_ID", id);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                debugString += "\t[SQL] " + cals.toString();

                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

                if (mysqlResult == 1) {
                    rs.next();
                    Workflow workflow = new Workflow();
                    workflow.setWorkflow_id(rs.getInt("ID"));
                    workflow.setInitiator_id(rs.getInt("INITIATOR_ID"));
                    workflow.setStatus(rs.getInt("STATUS"));
                    workflow.setWorkflowTemplate_type_name(rs.getString("TEMPLATE_TYPE"));
                    workflow.setLabel(rs.getString("LABEL"));
                    workflow.setWorkflow_type_name(rs.getString("WORKFLOW_TYPE_NAME"));
                    workflow.setWorkflowTemplate_type(rs.getInt("TEMPLATE_ID"));
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
                numOfRetry--;
                throw new Exception("Error while getting Workflow!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of get Workflow in milliseconds: " + timeElapsed / 1000000);
//        }        
        LogHandler.debug(this.getClass(), debugString);
        return databaseResponse;
    }

    /**
     * Get all Workflow Template Type in DB
     *
     * @return
     */
    @Override
    public DatabaseResponse getHashMapWorkflowTemplateType() throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        HashMap<Integer, WorkflowTemplateType> list = new HashMap<>();
        int numOfRetry = retryTimes;
        String debugString = "\n";
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_TEMPLATE_TYPE_LIST() }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);

                debugString += "\t[SQL] " + cals.toString();

                rs = cals.executeQuery();

                if (rs != null) {
                    while (rs.next()) {
                        WorkflowTemplateType type = new WorkflowTemplateType();
                        type.setId(rs.getInt("ID"));
                        type.setName(rs.getString("TYPE_NAME"));
                        type.setWorkflowType(rs.getInt("WORKFLOW_TYPE"));
                        type.setRemark_vn(rs.getString("REMARK"));
                        type.setRemark(rs.getString("REMARK_EN"));
                        list.put(rs.getInt("ID"), type);
                    }
                    databaseResponse.setObject(list);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(PaperlessConstant.CODE_FAIL);
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while getting list of Workflow template type!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of get Workflow template in milliseconds: " + timeElapsed / 1000000);
//        }        

        LogHandler.debug(
                this.getClass(), debugString);
        return databaseResponse;
    }

    /**
     * Get All Asset Type in DB
     *
     * @return
     */
    @Override
    public DatabaseResponse getAssetType() throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        HashMap<String, Integer> list = new HashMap<>();
        int numOfRetry = retryTimes;
        String debugString = "\n";
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_ASSET_TYPE_LIST() }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);

//                    
                debugString += "\t[SQL] " + cals.toString();

                rs = cals.executeQuery();

                if (rs != null) {
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
                numOfRetry--;
                throw new Exception("Error while getting Asset type!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of get Asset type in milliseconds: " + timeElapsed / 1000000);
//        }        

        LogHandler.debug(
                this.getClass(), debugString);
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
    ) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        List<Workflow> list = new ArrayList<>();
        int numOfRetry = retryTimes;
        String debugString = transactionID + "\n";
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_WORKFLOW_LIST(?,?,?,?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);

                cals.setString("pUSER_EMAIL", email);
                cals.setInt("pENTERPRISE_ID", enterprise_id);
                cals.setString("pWORKFLOW_STATUS", status);
                cals.setString("pLIST_TYPE", type);
                cals.setBoolean("pUSE_META_DATA", use_metadata);
                cals.setString("pMETA_DATA", metadata);
                cals.setInt("pOFFSET", offset);
                cals.setInt("pROW_COUNT", rowcount);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                debugString += "\t[SQL] " + cals.toString();

                rs = cals.executeQuery();
                if (cals.getString("pRESPONSE_CODE").equals("1")) {
                    debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");
                    while (rs.next()) {
                        Workflow workflow = new Workflow();
                        workflow.setWorkflow_id(rs.getInt("ID"));
                        workflow.setCreated_at(new Date(rs.getTimestamp("DATE_CREATED").getTime()));
                        workflow.setCreated_by(rs.getString("CREATED_BY"));
                        workflow.setWorkflow_type(rs.getInt("TYPE"));
                        workflow.setLabel(rs.getString("LABEL"));
                        workflow.setNote(rs.getString("NOTE"));
                        workflow.setStatus(Integer.parseInt(rs.getString("STATUS")));
                        workflow.setMetadata(rs.getString("METADATA"));
                        workflow.setWorkflowTemplate_type(rs.getInt("TEMPLATE_TYPE"));
                        workflow.setWorkflowTemplate_type_name(rs.getString("WORKFLOW_TEMPLATE_TYPE_NAME"));
                        workflow.setLast_modified_at(new Date(rs.getTimestamp("LAST_MODIFIED_AT").getTime()));
                        workflow.setLast_modified_by(rs.getString("LAST_MODIFIED_BY"));
                        workflow.setWorkflow_type_name(rs.getString("WORKFLOW_TYPE_NAME"));
                        list.add(workflow);
                    }
                    databaseResponse.setObject(list);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while getting list Workflow!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        LogHandler.debug(this.getClass(), debugString);
        return databaseResponse;
    }

    /**
     * Get the Workflow Template of the Workflow
     *
     * @param id ID of the Workflow
     * @param transactionID
     * @return
     */
    @Override
    public DatabaseResponse getWorkflowTemplate(
            int id,
            String transactionID
    ) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = transactionID + "\n";
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_WORKFLOW_TEMPLATE_GET(?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setInt("pWORKFLOW_ID", id);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
                debugString += "\t[SQL] " + cals.toString();

                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

                if (mysqlResult == 1 && rs != null) {
                    rs.next();
                    WorkflowTemplate wtem = new WorkflowTemplate();
                    wtem.setWorkflow_id(rs.getInt("WORKFLOW_ID"));
                    wtem.setMeta_data_template(rs.getString("META_DATA_TEMPLATE"));
                    wtem.setType_name(rs.getString("TEMPLATE_TYPE"));
                    wtem.setType_id(rs.getInt("TYPE_ID"));
                    wtem.setStatus(rs.getInt("STATUS"));

                    databaseResponse.setObject(wtem);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(mysqlResult);
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                LogHandler.debug(this.getClass(), debugString);
                throw new Exception("Error while getting Workflow Template!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of get Asset in milliseconds: " + timeElapsed / 1000000);
//        }        
        LogHandler.debug(this.getClass(), debugString);
        return databaseResponse;
    }

    /**
     * Update the Workflow Detail/Option of the Workflow
     *
     * @param id
     * @param email
     * @param map
     * @param hmac
     * @param created_by
     * @param transactionID
     * @return
     */
    @Override
    public DatabaseResponse updateWorkflowDetail_Option(
            int id,
            String email,
            int aid,
            HashMap<String, Object> map,
            String hmac,
            String created_by,
            String transactionID
    ) throws Exception {
        List<Integer> error = new ArrayList<>();
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";
        for (String temp : map.keySet()) {
            long startTime = System.nanoTime();
            Connection conn = null;
            ResultSet rs = null;
            CallableStatement cals = null;
            try {
                String str = "{ call USP_WORKFLOW_DETAIL_UPDATE(?,?,?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
                cals = conn.prepareCall(str);

                cals.setInt("W_ID", id);
                cals.setString("pUSER_EMAIL", email);
                cals.setInt("pENTERPRISE_ID", aid);
                cals.setString("pATTRIBUTE_NAME", temp);
                cals.setString("pATTRIBUTE_VALUE", map.get(temp).toString());
                cals.setString("pHMAC", hmac);
                cals.setString("pLAST_MODIFIED_BY", created_by);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
                debugString += "\t[SQL] " + cals.toString();

                cals.execute();
                int result = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                debugString += "\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE") + "\n";

                if (result != 1) {
                    response.setStatus(result);
                    error.add(result);
                } else {
                    response.setStatus(PaperlessConstant.CODE_SUCCESS);
                }
            } catch (Exception e) {
                throw new Exception("Error while update workflow detail - option!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
        }
        LogHandler.debug(this.getClass(), debugString);
        response.setObject(error);
        return response;
    }

    /**
     * Using for login
     *
     * @param email
     * @param pass
     * @param transactionID
     * @return
     */
    @Override
    public DatabaseResponse login(
            String email,
            String pass,
            String transactionID
    ) throws Exception {
        DatabaseResponse response = new DatabaseResponse();
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_LOGIN(?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setString("U_EMAIL", email);
            cals.setString("PASS", pass);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
            cals.registerOutParameter("pSTATUS_NAME", java.sql.Types.VARCHAR);
            cals.registerOutParameter("pPASSWORD_EXPIRED_AT", java.sql.Types.TIMESTAMP);

//                    
            debugString += "\t[SQL] " + cals.toString();

            rs = cals.executeQuery();
            int result = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (result != 1) {
                response.setStatus(result);
            } else {
                rs.next();
                User user = new User();
                user.setEmail(rs.getString("EMAIL"));
//                user.setMobile(rs.getString("MOBILE_NUMBER"));
//                user.setId(rs.getInt("ID"));
//                user.setName(rs.getString("USER_NAME"));
                user.setPasswordExpiredAt(new Date(cals.getTimestamp("pPASSWORD_EXPIRED_AT").getTime()));
                response.setObject(user);
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while login!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;                
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    /**
     * Get Workflow Activity
     *
     * @param id ID of the Workflow Activity
     * @param transactionID
     * @return
     */
    @Override
    public DatabaseResponse getWorkflowActivity(
            int id,
            String transactionID
    ) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = transactionID + "\n";
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_WORKFLOW_ACTIVITY_GET(?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);
                cals.setInt("pWO_AC_ID", id);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
                debugString += "\t[SQL] " + cals.toString();

                rs = cals.executeQuery();
                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

                debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

                if (mysqlResult == 1) {
                    rs.next();
                    WorkflowActivity wa = new WorkflowActivity();
                    wa.setId(rs.getInt("ID"));
                    wa.setWorkflow_id(rs.getInt("WORKFLOW_ID"));
                    wa.setWorkflow_label(rs.getString("WORKFLOW_LABEL"));
                    wa.setUser_email(rs.getString("USER"));
                    wa.setCreated_at(new Date(rs.getTimestamp("DATE").getTime()));
                    wa.setTransaction(rs.getString("TRANSACTION_ID"));
                    wa.setWorkflow_template_type(rs.getInt("WORKFLOW_TEMPLATE_TYPE_ID"));
                    wa.setRequestData(rs.getString("REQUEST_DATA"));

                    FileManagement file = new FileManagement();
                    file.setID(rs.getLong("FILE_ID"));
                    file.setName(rs.getString("DOWNLOAD_LINK"));
                    file.setData(rs.getBytes("BINARY_DATA"));

                    wa.setFile(file);
                    wa.setCSV_id(rs.getString("CSV"));
                    wa.setRemark(rs.getString("REMARK"));
                    wa.setStatus_name(rs.getString("STATUS_NAME"));
                    wa.setCreated_by(rs.getString("CREATED_BY"));
                    wa.setModified_at(new Date(rs.getTimestamp("LAST_MODIFIED_AT").getTime()));
                    wa.setModified_by(rs.getString("LAST_MODIFIED_BY"));

                    databaseResponse.setObject(wa);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                    numOfRetry--;
                } else {
                    databaseResponse.setStatus(mysqlResult);
                    numOfRetry--;
                }
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while getting WorkflowActivity!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of get WorkflowAtivity in milliseconds: " + timeElapsed / 1000000);
//        }        
        LogHandler.debug(this.getClass(), debugString);
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
    ) throws Exception {
        DatabaseResponse response = new DatabaseResponse();
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        List<Enterprise> list = new ArrayList<>();
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_USER_GET_ENTERPRISE_INFO(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setString("pUSER_EMAIL", email);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

            debugString += "\t[SQL] " + cals.toString();

            rs = cals.executeQuery();
            int result = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (result != 1) {
                response.setStatus(result);
            } else {
                while (rs.next()) {
                    Enterprise data = new Enterprise();
                    data.setId(rs.getInt("ID"));
                    data.setName(rs.getString("NAME"));
                    list.add(data);
                }
                response.setObject(list);
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while getEnterpriseInfoOfUser!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;               
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    /**
     * Get all Workflow Template Type from DB
     *
     * @return
     */
    @Override
    public DatabaseResponse getListWorkflowTemplateType() throws Exception {
        List<WorkflowTemplateType> list = new ArrayList<>();
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = "\n";
        try {
            String str = "{ call USP_TEMPLATE_TYPE_LIST() }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();
            rs = cals.getResultSet();
            if (rs != null) {
                while (rs.next()) {
                    WorkflowTemplateType data = new WorkflowTemplateType();
                    data.setId(rs.getInt("ID"));
                    data.setName(rs.getString("TYPE_NAME"));
                    data.setRemark_vn(rs.getString("REMARK"));
                    data.setRemark(rs.getString("REMARK_EN"));
                    list.add(data);
                }
                response.setObject(list);
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            } else {
                response.setStatus(PaperlessConstant.CODE_FAIL);
                return response;
            }
        } catch (Exception e) {
            throw new Exception("Error while getting List WorkflowActivity!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of get List WA in milliseconds: " + timeElapsed / 1000000);
//        }                
        LogHandler.debug(this.getClass(), debugString);
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
    ) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";
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

            debugString += "\t[SQL] " + cals.toString();

            cals.execute();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while writing refreshToken!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of get List WA in milliseconds: " + timeElapsed / 1000000);
//        }                
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    @Override
    public DatabaseResponse removeRefreshToken(
            String refreshtoken,
            String transactionID
    ) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";

        try {
            String str = "{ call USP_REFRESH_TOKEN_DELETE(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);
            //In
            cals.setString("pSESSION_TOKEN", refreshtoken);
            //Out
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while remove refreshToken!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of remove refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }                
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    @Override
    public DatabaseResponse checkAccessToken(
            String email,
            String accesstoken,
            String transactionID
    ) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_REFRESH_TOKEN_CHECK_EXISTS(?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In
            cals.setString("pUSER_EMAIL", email);
            cals.setString("pSESSION_TOKEN", accesstoken);

            //Out
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while check refreshToken!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of get List WA in milliseconds: " + timeElapsed / 1000000);
//        }                
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    @Override
    public DatabaseResponse getRefreshToken(
            String sessionID,
            String transactionID
    ) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = "\n";
        try {
            String str = "{ call USP_REFRESH_TOKEN_GET(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In
            cals.setString("pSESSION_TOKEN", sessionID);

            //Out
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

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
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            } else {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            }
        } catch (Exception e) {
            throw new Exception("Error while getRefreshToken!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LogHandler.debug(this.getClass(),"Execution time of get List WA in milliseconds: " + timeElapsed / 1000000);
//        }                
        LogHandler.debug(this.getClass(), debugString);
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
    ) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";
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

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while update refreshToken!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of update refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }                
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    @Override
    public DatabaseResponse getUser(
            String email,
            int user_id,
            int enterprise_id,
            String transactionID,
            boolean returnTypeUser //Sử dụng để ép kiểu trả về cho phù hợp
    ) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_USER_GET(?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In            
            cals.setString("pUSER_EMAIL", email);
            cals.setInt("pUSER_ID", (user_id <= 0 ? 0 : user_id));
            cals.setInt("pENTERPRISE_ID", enterprise_id);            

            //Out
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            rs = cals.executeQuery();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                rs.next();
                if (returnTypeUser) {
                    User user = new User();
                    user.setEmail(rs.getString("EMAIL"));
                    user.setName(rs.getString("USER_NAME"));
                    response.setObject(user);
                    response.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    Account account = new Account();
                    account.setUser_email(rs.getString("EMAIL"));
                    account.setUser_name(rs.getString("USER_NAME"));
                    account.setMobile_number(rs.getString("MOBILE_NUMBER"));
                        
                    account.setVerified(rs.getBoolean("VERIFIED_ENABLED"));
                    response.setObject(account);
                    response.setStatus(PaperlessConstant.CODE_SUCCESS);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error while getting User!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of update refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }                
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    @Override
    public DatabaseResponse getKEYAPI(
            int enterprise_id,
            String client_ID,
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_ENTERPRISE_API_KEY_GET(?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In                        
            cals.setInt("pENTERPRISE_ID", enterprise_id);
            cals.setString("pCLIENT_ID", client_ID);
            //Out
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            rs = cals.executeQuery();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                rs.next();
                Enterprise ent = new Enterprise();
                ent.setId(rs.getInt("ENTERPRISE_ID"));
                ent.setClientID(rs.getString("CLIENT_ID"));
                ent.setClientSecret(rs.getString("CLIENT_SECRET"));
                response.setObject(ent);
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while getKEY API!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of update refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }                
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    @Override
    public DatabaseResponse getEmailTemplate(
            int language,
            String email_noti,
            String transactionID
    ) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_EMAIL_TEMPLATE_GET(?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In                        
            cals.setInt("pLANGUAGE_ID", language);
            cals.setString("pEMAIL_KEY", email_noti);

            //Out
            cals.registerOutParameter("pSUBJECT", java.sql.Types.VARCHAR);
            cals.registerOutParameter("pBODY", java.sql.Types.VARCHAR);
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                EmailTemplate template = new EmailTemplate();
                template.setSubject(cals.getString("pSUBJECT"));
                template.setBody(cals.getString("pBODY"));
                response.setObject(template);
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while find email template!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of update refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    @Override
    public DatabaseResponse getEnterpriseInfo(
            int enterprise_id,
            String enterprise_name
    ) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = "\n";
        try {
            String str = "{ call USP_ENTERPRISE_INFO_GET(?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In                        
            cals.setInt("pENTERPRISE_ID", enterprise_id);
            cals.setString("pENTERPRISE_NAME", enterprise_name);

            //Out
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();
            rs = cals.getResultSet();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                return response;
            } else {
                rs.next();
                Enterprise ent = new Enterprise();
                ent.setId(rs.getInt("ID"));
                ent.setName(rs.getString("NAME"));
                ent.setEmail_notification(rs.getString("NOTIFICATION_EMAIL"));
                ent.setOwner_id(rs.getInt("OWNER"));
                ent.setSigning_info_properties(rs.getString("SIGNING_INFO_PROPERTIES"));
                response.setObject(ent);
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while getting enterprise information!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of update refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    @Override
    public DatabaseResponse createUser(
            String email,
            String password,
            String mobile_number,
            String created_user_email,
            String created_user_name,
            int enterprise_id,
            String role_name,
            Date pass_expired_at,
            int business_type,
            String org_web,
            String hmac,
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_USER_ADD(?,?,?,?,?,?,?,?,?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In                        
            cals.setString("pUSER_EMAIL", email);
            cals.setString("pPASSWORD", password);
            cals.setString("pMOBILE_NUMBER", mobile_number);
            cals.setString("pCREATED_USER_EMAIL", created_user_email);
            cals.setString("pCREATED_USER_NAME", created_user_name);
            cals.setInt("pENTERPRISE_ID", enterprise_id);
            cals.setString("pROLE_NAME", role_name);
            cals.setTimestamp("pPASSWORD_EXPIRED_AT", new Timestamp(pass_expired_at.getTime()));
            cals.setInt("pBUSINESS_TYPE", business_type);
            cals.setString("pORG_WEB", org_web);
            cals.setString("pHMAC", hmac);

            //Out
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
            cals.registerOutParameter("pSTATUS_NAME", java.sql.Types.VARCHAR);
            cals.registerOutParameter("pCREATED_USER_ID", java.sql.Types.BIGINT);
//            System.out.println(cals.toString());

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

//            System.out.println(cals.getString("pRESPONSE_CODE"));
            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                String status = cals.getString("pSTATUS_NAME");
                long id = cals.getLong("pCREATED_USER_ID");
                List<Object> objects = new ArrayList<>();
                objects.add(status);
                objects.add(id);
                response.setObject(objects);
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while create User!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of update refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    @Override
    public DatabaseResponse getAuthenticatePassword(
            String email,
            int language_id,
            String email_type,
            String transactionID
    ) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_GET_EMAIL_LOGIN(?,?,?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In     
            cals.setString("pUSER_EMAIL", email);
            cals.setInt("pLANGUAGE_ID", language_id);
            cals.setString("pEMAIL_KEY", email_type);

            //Out
            cals.registerOutParameter("pSUBJECT", java.sql.Types.VARCHAR);
            cals.registerOutParameter("pBODY", java.sql.Types.VARCHAR);
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
            cals.registerOutParameter("pPASSWORD", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                EmailTemplate template = new EmailTemplate();
                template.setSubject(cals.getString("pSUBJECT"));
                template.setBody(cals.getString("pBODY"));
                template.setPassword(cals.getString("pPASSWORD"));
                response.setObject(template);
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while get Authenticate password for email!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of update refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    @Override
    public DatabaseResponse verifyEmail(
            String email,
            String password,
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_USER_VERIFIED_EMAIL(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In     
            cals.setString("pUSER_EMAIL", email);
//            cals.setString("pPASSWORD", password);

            //Out            
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while Verify email!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of update refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }                
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    @Override
    public DatabaseResponse deactiveWorkflow(
            int workflow_id,
            String email,
            int enterprise_id,
            String modified_by,
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_WORKFLOW_DEACTIVATE(?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In     
            cals.setInt("pWORKFLOW_ID", workflow_id);
            cals.setString("pUSER_EMAIL", email);
            cals.setInt("pENTERPRISE_ID", enterprise_id);
            cals.setString("pLAST_MODIFIED_BY", modified_by);

            //Out            
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while deactive Workflow!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of update refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }                
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    @Override
    public DatabaseResponse reactiveWorkflow(
            int workflow_id,
            String email,
            int enterprise_id,
            String modified_by,
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_WORKFLOW_REACTIVATE(?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In     
            cals.setInt("pWORKFLOW_ID", workflow_id);
            cals.setString("pUSER_EMAIL", email);
            cals.setInt("pENTERPRISE_ID", enterprise_id);
            cals.setString("pLAST_MODIFIED_BY", modified_by);

            //Out            
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while reactive Workflow!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//         
//            LOG.debug("Execution time of update refreshToken in milliseconds: " + timeElapsed / 1000000);
//        }                
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    @Override
    public DatabaseResponse updateWorkflowTemplate(
            int workflow_id,
            String user_email,
            int enterprise_id,
            String meta_data,
            String hmac,
            String last_modified_by,
            String transactionID
    ) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_WORKFLOW_TEMPLATE_UPDATE(?,?,?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In                        
            cals.setInt("pWORKFLOW_ID", workflow_id);
            cals.setString("pUSER_EMAIL", user_email);
            cals.setInt("pENTERPRISE_ID", enterprise_id);
            cals.setString("pMETA_DATA_TEMPLATE", meta_data);
            cals.setString("pHMAC", hmac);
            cals.setString("pLAST_MODIFIED_BY", last_modified_by);

            //Out            
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while updateWorkflowTemplate!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    @Override
    public DatabaseResponse getListAsset(
            int ent_id,
            String email,
            String file_name,
            String type,
            int offset,
            int rowcount,
            String transactionID) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        List<Asset> listAsset = new ArrayList<>();
        DatabaseResponse response = new DatabaseResponse();
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_ASSET_LIST(?,?,?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            //In                        
            cals.setInt("pENTERPRISE_ID", ent_id);
            cals.setString("pEMAIL_USER", email);
            cals.setString("pFILE_NAME", file_name);
            cals.setString("TYPE", type);
            cals.setInt("pOFFSET", offset);
            cals.setInt("pROW_COUNT", rowcount);

            //Out            
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            rs = cals.executeQuery();

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (!cals.getString("pRESPONSE_CODE").equals("1") || rs == null) {
                response.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                while (rs.next()) {
                    Asset temp = new Asset();
                    temp.setId(rs.getInt("ID"));
                    temp.setName(rs.getString("FILE_NAME"));
                    temp.setCreated_at(new Date(rs.getTimestamp("DATE_CREATED").getTime()));
                    temp.setSize(rs.getLong("SIZE"));
                    temp.setUsed_by(rs.getString("USED_BY"));
                    temp.setType_name(rs.getString("ASSET_TYPE_NAME"));
                    listAsset.add(temp);
                }
                response.setObject(listAsset);
                response.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while getListAsset!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        LogHandler.debug(this.getClass(), debugString);
        return response;
    }

    @Override
    public DatabaseResponse getListWorkflowActivityWithCondition(
            String email,
            int aid,
            String email_search,
            Date date,
            String g_type,
            String status,
            boolean is_test,
            boolean is_product,
            boolean is_custom_range,
            Date fromdate,
            Date todate,
            String languagename,
            int offset,
            int rowcount,
            String transactionID) throws Exception {
        DatabaseResponse res = new DatabaseResponse();
        List<WorkflowActivity> list = new ArrayList<>();
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_WORKFLOW_ACTIVITY_LIST(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setString("U_EMAIL", email);
            cals.setInt("pENTERPRISE_ID", aid);
            cals.setString("EMAIL_SEARCH", email_search);
            cals.setDate("DATE_SEARCH", (date != null) ? (new java.sql.Date(date.getTime())) : null);
            cals.setString("G_TYPE", g_type);
            cals.setString("W_A_STATUS", status);
            cals.setInt("IS_TEST", is_test ? 1 : 0);
            cals.setInt("IS_PRODUCT", is_product ? 1 : 0);
            cals.setInt("IS_CUSTOM_RANGE", is_custom_range ? 1 : 0);
            cals.setDate("FROM_DATE", (fromdate != null) ? (new java.sql.Date(fromdate.getTime())) : null);
            cals.setDate("TO_DATE", (todate != null) ? (new java.sql.Date(todate.getTime())) : null);
            cals.setString("pLANGUAGE_NAME", languagename == null ? "ENG" : languagename);
            cals.setInt("pOFFSET", offset);
            cals.setInt("pROW_COUNT", rowcount == 0 ? 1 : rowcount);
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();
            rs = cals.getResultSet();
            int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

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
                        wa.setEnterprise_id(rs.getInt("ENTERPRISE_ID"));

                        FileManagement file = new FileManagement();
                        file.setID(rs.getLong("FILE_ID"));
                        file.setName(rs.getString("DOWNLOAD_LINK"));
//                        file.setData(rs.getBytes("BINARY_DATA"));                        
                        wa.setFile(file);

                        wa.setCSV_id(rs.getString("CSV"));
                        wa.setRemark(rs.getString("REMARK"));
                        wa.setStatus_name(rs.getString("STATUS_NAME"));
                        wa.setCreated_at(new Date(rs.getTimestamp("DATE").getTime()));
                        list.add(wa);

                    }
                    res.setStatus(PaperlessConstant.CODE_SUCCESS);
                    res.setObject(list);
                } else {
                    res.setStatus(mysqlResult);
                }
            } else {
                res.setStatus(mysqlResult);
            }
        } catch (Exception e) {
            throw new Exception("Error while getting List WorkflowActivity!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        LogHandler.debug(this.getClass(), debugString);
        return res;
    }

    @Override
    public DatabaseResponse getCertificate(
            String service_name,
            String remark,
            String url,
            String transactionID) throws Exception {
        DatabaseResponse res = new DatabaseResponse();
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_TRUST_MANAGER_GET(?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setString("pSERVICE_NAME", service_name);
            cals.setString("pREMARK", remark);
            cals.setString("pURL", url);

            cals.registerOutParameter("pPUBLIC_KEY", java.sql.Types.LONGVARCHAR);
            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();
            int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (mysqlResult == 1) {
                res.setObject(cals.getString("pPUBLIC_KEY"));
                res.setStatus(PaperlessConstant.CODE_SUCCESS);
            } else {
                res.setStatus(mysqlResult);
            }
        } catch (Exception e) {
            throw new Exception("Error while getting getCertificate!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        LogHandler.debug(this.getClass(), debugString);
        return res;
    }

    @Override
    public DatabaseResponse getStatusUser(
            String email,
            String transactionID) throws Exception {
        DatabaseResponse res = new DatabaseResponse();
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_USER_GET_STATUS(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setString("pUSER_EMAIL", email);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();
            rs = cals.getResultSet();
            int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (mysqlResult == 1) {
                rs.next();
                Account account = new Account();
                account.setUser_email(rs.getString("EMAIL"));
                account.setUser_name(rs.getString("USER_NAME"));
//                account.setStatus(StatusOfAccount.valueOf(rs.getString("STATUS_NAME")));
                res.setObject(account);
                res.setStatus(PaperlessConstant.CODE_SUCCESS);
            } else {
                res.setStatus(mysqlResult);
            }
        } catch (Exception e) {
            throw new Exception("Error while getting Status User!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        LogHandler.debug(this.getClass(), debugString);
        return res;
    }

    @Override
    public DatabaseResponse updateUserPassword(
            String email,
            String password,
            String transactionID) throws Exception {
        DatabaseResponse res = new DatabaseResponse();
        long startTime = System.nanoTime();
        Connection conn = null;
        CallableStatement cals = null;
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_USER_UPDATE_PASSWORD(?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setString("pUSER_EMAIL", email);
            cals.setString("pPASSWORD", password);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();
            int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (mysqlResult != 1) {
                res.setStatus(mysqlResult);
            } else {
                res.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while update User Password!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        LogHandler.debug(this.getClass(), debugString);
        return res;
    }

    @Override
    public DatabaseResponse updateUserPassword(
            String email,
            String old_password,
            String new_password,
            String transactionID) throws Exception {
        DatabaseResponse res = new DatabaseResponse();
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_USER_CHANGE_PASSWORD(?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setString("pUSER_EMAIL", email);
            cals.setString("pNEW_PASSWORD", new_password);
            cals.setString("pOLD_PASSWORD", old_password);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

//                    
            debugString += "\t[SQL] " + cals.toString();

            cals.execute();
            rs = cals.getResultSet();
            int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (mysqlResult != 1) {
                res.setStatus(mysqlResult);
            } else {
                res.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while update User Password!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        LogHandler.debug(this.getClass(), debugString);
        return res;
    }

    @Override
    public DatabaseResponse updateEnterpriseInfo(
            int enterprise_id,
            String dataRP,
            byte[] fileP12,
            String transactionID) throws Exception {
//        DatabaseResponse res = new DatabaseResponse();
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        String debugString = transactionID + "\n";
//        try {
//            String str = "{ call USP_USER_CHANGE_PASSWORD(?,?,?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//
//            cals.setString("pUSER_EMAIL", email);
//            cals.setString("NEW_PASS", new_password);
//            cals.setString("OLD_PASS", old_password);
//
//            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);
//
////                    
//            debugString += "\t[SQL] " + cals.toString();
//
//            cals.execute();
//            rs = cals.getResultSet();
//            int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));
//
//
//            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");
//
//            if (mysqlResult != 1) {
//                res.setStatus(mysqlResult);
//            } else {
//                res.setStatus(PaperlessConstant.CODE_SUCCESS);
//            }
//        } catch (Exception e) {
//            throw new Exception("Error while update User Password!", e);
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        LogHandler.debug(this.getClass(), debugString);
//        return res;
        return null;
    }

    @Override
    public DatabaseResponse getQRSize(
            String qr_size_name,
            String transactionID) throws Exception {
        DatabaseResponse res = new DatabaseResponse();
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_QR_SIZE_GET(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setString("pQR_SIZE_NAME", qr_size_name);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

            debugString += "\t[SQL] " + cals.toString();

            cals.execute();
            rs = cals.getResultSet();
            int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (mysqlResult != 1) {
                res.setStatus(mysqlResult);
            } else {
                rs.next();
                QRSize qrsize = new QRSize();
                qrsize.setSize(rs.getInt("SIZE_VALUE"));
                qrsize.setQr_size_name(rs.getString("QR_SIZE_NAME"));
                res.setStatus(PaperlessConstant.CODE_SUCCESS);
                res.setObject(qrsize);
            }
        } catch (Exception e) {
            throw new Exception("Error while getting QRSize!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        LogHandler.debug(this.getClass(), debugString);
        return res;
    }

    @Override
    public DatabaseResponse updateRequestDataOfWorkflowActivity(
            int id,
            String meta_data,
            String modified_by,
            String transactionID) throws Exception {
        DatabaseResponse res = new DatabaseResponse();
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_WORKFLOW_ACTIVITY_UPDATE_REQUEST_DATA(?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setInt("pWORKFLOW_ACTIVITY_ID", id);
            cals.setString("pREQUEST_DATA", meta_data);
            cals.setString("pLAST_MODIFIED_BY", modified_by);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

            debugString += "\t[SQL] " + cals.toString();

            cals.execute();
            rs = cals.getResultSet();
            int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (mysqlResult != 1) {
                res.setStatus(mysqlResult);
            } else {
                res.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while updating metadata WorkflowActivity", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        LogHandler.debug(this.getClass(), debugString);
        return res;
    }

    @Override
    public DatabaseResponse updateStatusWorkflowActivity(
            int id,
            String status,
            boolean process_enable,
            String last_modified_by,
            String transactionID) throws Exception {
        DatabaseResponse res = new DatabaseResponse();
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        String debugString = transactionID + "\n";
        try {
            String str = "{ call USP_WORKFLOW_ACTIVITY_UPDATE_STATUS(?,?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setInt("pWORKFLOW_ACTIVITY_ID", id);
            cals.setString("pSTATUS_NAME", status);
            cals.setBoolean("pPROCESS_ENABLED", process_enable);
            cals.setString("pLAST_MODIFIED_BY", last_modified_by);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

            debugString += "\t[SQL] " + cals.toString();

            cals.execute();
            rs = cals.getResultSet();
            int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE"));

            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");

            if (mysqlResult != 1) {
                res.setStatus(mysqlResult);
            } else {
                res.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            throw new Exception("Error while updating status WorkflowActivity", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        LogHandler.debug(this.getClass(), debugString);
        return res;
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
    public DatabaseResponse getTotalRecordsWorkflow(
            String email,
            int enterprise_id,
            String status,
            String type,
            boolean use_metadata,
            String metadata,
            String transactionID
    ) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = transactionID + "\n";
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_WORKFLOW_GET_ROW_COUNT(?,?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);

                cals.setString("pUSER_EMAIL", email);
                cals.setInt("pENTERPRISE_ID", enterprise_id);
                cals.setString("pWORKFLOW_STATUS", status);
                cals.setString("pLIST_TYPE", type);
                cals.setBoolean("pUSE_META_DATA", use_metadata);
                cals.setString("pMETA_DATA", metadata);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                debugString += "\t[SQL] " + cals.toString();

                rs = cals.executeQuery();
                if (cals.getString("pRESPONSE_CODE").equals("1")) {
                    debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");
                    int total = 0;
                    while (rs.next()) {
                        total = rs.getInt("ROW_COUNT");
                    }
                    databaseResponse.setObject(total);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while getting list Workflow!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        LogHandler.debug(this.getClass(), debugString);
        return databaseResponse;
    }

    @Override
    public DatabaseResponse logIntoDB(
            String email,
            int enterprise_id,
            int workflow_activity,
            String app_name,
            String api_key,
            String version,
            String service_name,
            String url,
            String http_verb,
            int status_code,
            String request,
            String response,
            String hmac,
            String created_by,
            String transactionID)
            throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = transactionID + "\n";
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_API_LOG_ADD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);

                cals.setObject("pUSER_EMAIL", email);
                cals.setObject("pENTERPRISE_ID", enterprise_id > 0 ? enterprise_id : null);
                cals.setObject("pWORKFLOW_ACTIVITY_ID", workflow_activity > 0 ? workflow_activity : null);
                cals.setObject("pAPP_NAME", app_name);
                cals.setObject("pAPI_KEY", api_key);
                cals.setObject("pVERSION", version);
                cals.setObject("pSERVICE_NAME", service_name);
                cals.setObject("pURL", url);
                cals.setObject("pHTTP_VERB", http_verb);
                cals.setObject("pSTATUS_CODE", status_code);
                cals.setObject("pREQUEST", request);
                cals.setObject("pRESPONSE", response);
                cals.setObject("pHMAC", hmac);
                cals.setObject("pCREATED_BY", created_by);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                debugString += "\t[SQL] " + cals.toString();

                rs = cals.executeQuery();
                debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");
                if (cals.getString("pRESPONSE_CODE").equals("1")) {                    
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while writing log into DB!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        LogHandler.debug(this.getClass(), debugString);
        return databaseResponse;
    }

    @Override
    public DatabaseResponse getPolicyAttribute(
            int id) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = "";
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_GENERAL_POLICY_ATTR_GET(?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);

                cals.setInt("pATTR_ID", id);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                debugString += "\t[SQL] " + cals.toString();

                rs = cals.executeQuery();
                debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");
                if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                    databaseResponse.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                } else {
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                    rs.next();
                    PolicyResponse data = new PolicyResponse();
                    data.setId(rs.getInt("ID"));
                    data.setEnabled(rs.getBoolean("ENABLED"));
                    data.setGeneral_policy_attr_type_id(rs.getInt("GENERAL_POLICY_ATTR_TYPE_ID"));
                    data.setValue(rs.getString("VALUE"));
                    data.setBlob(rs.getBytes("BLOB"));
                    data.setHmac(rs.getString("HMAC"));
                    data.setCreated_at(new Date(rs.getTimestamp("CREATED_AT").getTime()));
                    data.setCreated_by(rs.getString("CREATED_BY"));
                    data.setModified_at(new Date(rs.getTimestamp("LAST_MODIFIED_AT").getTime()));
                    data.setModified_by(rs.getString("LAST_MODIFIED_BY"));
                    databaseResponse.setObject(data);
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while getting policy!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        LogHandler.debug(this.getClass(), debugString);
        return databaseResponse;
    }

    @Override
    public DatabaseResponse updateQR(
            int id,
            String meta_data,
            String image,
            String modified_by,
            String transaction_id) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = "";
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_QR_UPDATE(?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);

                cals.setInt("pQR_ID", id);
                cals.setString("pMETA_DATA", meta_data);
                cals.setString("pLAST_MODIFIED_BY", modified_by);
                cals.setString("pIMAGE", image);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                debugString += "\t[SQL] " + cals.toString();

                rs = cals.executeQuery();
                debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");
                if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                    databaseResponse.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                } else {
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while updating QR!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        LogHandler.debug(this.getClass(), debugString);
        return databaseResponse;
    }

    @Override
    public DatabaseResponse updateAsset(
            int id,
            String email,
            String file_name,
            int asset_type,
            long size,
            String uuid,
            String dms,
            String meta_data,
            byte[] binary_data,
            String hmac,
            String modified_by,
            String used_by,
            String transaction_id) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = "";
        while (numOfRetry > 0) {
            try {
                Blob blob = null;
                if (binary_data != null) {
                    blob = new SerialBlob(binary_data);
                }
                String str = "{ call USP_ASSET_UPDATE(?,?,?,?,?,?,?,?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);

                cals.setInt("pASSET_ID", id);
                cals.setString("U_EMAIL", email);
                cals.setString("pFILE_NAME", file_name);
                cals.setInt("pASSET_TYPE", asset_type);
                cals.setObject("pSIZE", size <= 0 ? null : size);
                cals.setObject("pUSED_BY", used_by);
                cals.setString("pUUID", uuid);
                cals.setString("pDMS_PROPERTY", dms);
                cals.setString("pMETA_DATA", meta_data);
                cals.setBlob("pBINARY_DATA", blob);
                cals.setString("pHMAC", hmac);
                cals.setString("pLAST_MODIFIED_BY", modified_by);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                debugString += "\t[SQL] " + cals.toString();

                rs = cals.executeQuery();
                debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");
                if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                    databaseResponse.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                } else {
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                e.printStackTrace();
                throw new Exception("Error while updating Asset!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        LogHandler.debug(this.getClass(), debugString);
        return databaseResponse;
    }

    @Override
    public DatabaseResponse deleteAsset(
            int id,
            String transaction_id) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = "";
        try {
            String str = "{ call USP_ASSET_DELETE(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setInt("pASSET_ID", id);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

            debugString += "\t[SQL] " + cals.toString();

            rs = cals.executeQuery();
            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");
            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                databaseResponse.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
            }
        } catch (Exception e) {
            numOfRetry--;
            throw new Exception("Error while deleting Asset!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }

        LogHandler.debug(this.getClass(), debugString);
        return databaseResponse;
    }

    @Override
    public DatabaseResponse getTransaction(
            String id, 
            String transaction_id) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = "";
        try {
            String str = "{ call USP_TRANSACTION_GET(?,?) }";
            conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setString("pTRANSACTION_ID", id);

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

            debugString += "\t[SQL] " + cals.toString();

            rs = cals.executeQuery();
            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");
            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                databaseResponse.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                rs.next();
                databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                Transaction transaction = new Transaction();
                transaction.setId(id);
                transaction.setLog_id(rs.getInt("LOG_ID"));
                transaction.setObject_id(rs.getInt("OBJECT_ID"));
                transaction.setObject_type(rs.getInt("OBJECT_TYPE"));
                transaction.setUser_id(rs.getInt("USER_ID"));
                transaction.setIp_add(rs.getString("IP_ADDRESS"));
                transaction.setHmac(rs.getString("HMAC"));
                transaction.setCreated_by(rs.getString("CREATED_BY"));
                transaction.setCreated_at(new Date(rs.getTimestamp("CREATED_AT").getTime()));
                transaction.setModified_by(rs.getString("LAST_MODIFIED_BY"));
                transaction.setModified_at(new Date(rs.getTimestamp("LAST_MODIFIED_AT").getTime()));
                databaseResponse.setObject(transaction);
            }
        } catch (Exception e) {
            numOfRetry--;
            throw new Exception("Error while getting transaction!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        LogHandler.debug(this.getClass(), debugString);
        return databaseResponse;
    }

    @Override
    public DatabaseResponse getListUser(
            String enterpriseName,
            int enterpriseId,
            int offset,
            int rowCount,
            String transactionId) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = "";
        try {
            String str = "{ call USP_USER_LIST(?,?,?,?) }";
            conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
            cals = conn.prepareCall(str);

            cals.setInt("pENTERPRISE_ID", enterpriseId);
            cals.setInt("pOFFSET", offset);
            cals.setInt("pROW_COUNT", rowCount);                

            cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

            debugString += "\t[SQL] " + cals.toString();

            rs = cals.executeQuery();
            debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");
            if (!cals.getString("pRESPONSE_CODE").equals("1")) {
                databaseResponse.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
            } else {
                List<Account> accounts = new ArrayList<>();
                while(rs.next()){
                    Account account = new Account();
                    account.setUser_email(rs.getString("EMAIL"));
                    account.setMobile_number(rs.getString("MOBILE_NUMBER"));
                    account.setEnterprise_name(rs.getString("ENTERPRISE_NAME"));
                    account.setRole(rs.getString("ROLE"));
                    accounts.add(account);
                }
                databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);                
                databaseResponse.setObject(accounts);
            }
        } catch (Exception e) {
            numOfRetry--;
            throw new Exception("Error while getting total Account!", e);
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        LogHandler.debug(this.getClass(), debugString);
        return databaseResponse;
    }

    @Override
    public DatabaseResponse getTotalRecordsAsset(
            int enterpriseId, 
            String email,
            String fileName, 
            String type, 
            String transactionId) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = transactionId + "\n";
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_ASSET_GET_ROW_COUNT(?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);

                cals.setInt("pENTERPRISE_ID", enterpriseId);
                cals.setString("pEMAIL_USER", email);               
                cals.setString("pFILE_NAME", fileName);                
                cals.setString("TYPE", type);
                

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                debugString += "\t[SQL] " + cals.toString();

                rs = cals.executeQuery();
                if (cals.getString("pRESPONSE_CODE").equals("1")) {
                    debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");
                    int total = 0;
                    while (rs.next()) {
                        total = rs.getInt("ROW_COUNT");
                    }
                    databaseResponse.setObject(total);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while getting list Workflow!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        LogHandler.debug(this.getClass(), debugString);
        return databaseResponse;
    }

    @Override
    public DatabaseResponse getTotalRecordsWorkflowActivity(
            String email,
            int enterpriseId,
            String emailSearch,
            String date,
            String gType,
            String status,
            boolean isTest,
            boolean isProduct,            
            boolean isCustomRange,
            String fromDate,
            String toDate,
            String languageName,
            String transactionId) throws Exception {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        DatabaseResponse databaseResponse = new DatabaseResponse();
        int numOfRetry = retryTimes;
        String debugString = transactionId + "\n";
        while (numOfRetry > 0) {
            try {
                String str = "{ call USP_WORKFLOW_ACTIVITY_GET_ROW_COUNT(?,?,?,?,?,?,?,?,?,?,?,?,?) }";
                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
                cals = conn.prepareCall(str);

                cals.setString("U_EMAIL", email);
                cals.setInt("pENTERPRISE_ID", enterpriseId);
                cals.setString("EMAIL_SEARCH", emailSearch);
                cals.setTimestamp("DATE_SEARCH", null);
                cals.setString("G_TYPE", gType);
                cals.setString("W_A_STATUS", status);
                cals.setBoolean("IS_TEST", isTest);
                cals.setBoolean("IS_PRODUCT", isTest);
                cals.setBoolean("IS_CUSTOM_RANGE", isTest);
                cals.setTimestamp("FROM_DATE", null);
                cals.setTimestamp("TO_DATE", null);
                cals.setString("pLANGUAGE_NAME", languageName);

                cals.registerOutParameter("pRESPONSE_CODE", java.sql.Types.VARCHAR);

                debugString += "\t[SQL] " + cals.toString();

                rs = cals.executeQuery();
                if (cals.getString("pRESPONSE_CODE").equals("1")) {
                    debugString += "\n\tResponseCode get from DB:" + cals.getString("pRESPONSE_CODE");
                    int total = 0;
                    while (rs.next()) {
                        total = rs.getInt("ROW_COUNT");
                    }
                    databaseResponse.setObject(total);
                    databaseResponse.setStatus(PaperlessConstant.CODE_SUCCESS);
                } else {
                    databaseResponse.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE")));
                }
                break;
            } catch (Exception e) {
                numOfRetry--;
                throw new Exception("Error while getting list Workflow!", e);
            } finally {
                DatabaseConnectionManager.getInstance().close(conn);
            }
        }
        LogHandler.debug(this.getClass(), debugString);
        return databaseResponse;
    }

}
