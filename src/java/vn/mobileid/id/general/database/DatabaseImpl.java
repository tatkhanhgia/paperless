/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.objects.IDXCertificate;
import vn.mobileid.id.general.objects.IdentityProcessType;
import vn.mobileid.id.general.gateway.p2p.objects.P2P;
import vn.mobileid.id.general.gateway.p2p.objects.P2PEntityAttribute;
import vn.mobileid.id.general.gateway.p2p.objects.P2PFunction;
import vn.mobileid.id.general.gateway.p2p.objects.P2PFunctionAccessPrivilege;
import vn.mobileid.id.general.gateway.p2p.objects.P2PIPAccessPrivilege;
import vn.mobileid.id.general.objects.AWSV4PropertiesJSNObject;
import vn.mobileid.id.general.objects.DMSProperties;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.Entity;
import vn.mobileid.id.general.objects.EntityProperties;
import vn.mobileid.id.general.objects.IPRestrictionList;
import vn.mobileid.id.general.objects.IdentityDocument;
import vn.mobileid.id.general.objects.IdentityDocumentType;
import vn.mobileid.id.general.objects.IdentityProcess;
import vn.mobileid.id.general.objects.IdentityProcessStatus;
import vn.mobileid.id.general.objects.IdentityPropertiesJSNObject;
import vn.mobileid.id.general.objects.IdentitySubject;
import vn.mobileid.id.general.objects.IdentityProcessAttribute;
import vn.mobileid.id.general.objects.IdentityProvider;
import vn.mobileid.id.general.objects.IdentityProviderProperties;
import vn.mobileid.id.general.objects.Province;
import vn.mobileid.id.general.objects.RegistrationParty;
import vn.mobileid.id.general.objects.RelyingParty;
import vn.mobileid.id.general.objects.RelyingPartyAttribute;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.general.objects.SMSGWProperties;
import vn.mobileid.id.general.objects.SMTPProperties;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.everification.objects.CAProperties;
import vn.mobileid.id.everification.objects.CertificationAuthority;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.objects.TSAProfile;
import vn.mobileid.id.general.objects.TSAProfileProperties;
import vn.mobileid.id.general.objects.Value;
import vn.mobileid.id.general.objects.VerificationPropertiesJSNObject;
import vn.mobileid.id.license.LicenseServerData;
import vn.mobileid.id.qrypto.QryptoConstant;
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
                    responseCode.setCode(rs.getString("CODE"));
                    responseCode.setCode_description(rs.getString("CODE_DESCRIPTION"));
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
    public List<Entity> getEntities() {
        long startTime = System.nanoTime();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cals = null;
        List<Entity> entities = new ArrayList<>();
        try {
            String str = "{ call SP_FO_ENTITY_LIST() }";
            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
            cals = conn.prepareCall(str);
            if (LogHandler.isShowDebugLog()) {
                LOG.debug("[SQL] " + cals.toString());
            }
            cals.execute();
            rs = cals.getResultSet();
            if (rs != null) {
                while (rs.next()) {
                    Entity entity = new Entity();
                    entity.setEntityID(rs.getInt("ID"));
                    entity.setName(rs.getString("NAME"));
                    entity.setUri(rs.getString("URI"));
                    entity.setRemark(rs.getString("REMARK"));
                    entity.setRemarkEn(rs.getString("REMARK_EN"));
                    if (!Utils.isNullOrEmpty(rs.getString("PROPERTIES"))) {
                        try {
                            EntityProperties entityProperties = objectMapper.readValue(rs.getString("PROPERTIES"), EntityProperties.class);
                            entity.setEntityProperties(entityProperties);
                        } catch (Exception e) {
                            if (LogHandler.isShowErrorLog()) {
                                LOG.error("Error while parsing ENTITY_PROPERTIES. Details: " + e.toString());
                            }
                            e.printStackTrace();
                            entity.setEntityProperties(null);
                        }
                    }
                    entities.add(entity);
                }
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while getting Entity information. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
        } finally {
            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        if (LogHandler.isShowDebugLog()) {
            LOG.debug("Execution time of getEntities in milliseconds: " + timeElapsed / 1000000);
        }
        return entities;
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
                        responseCode.setCode(rs.getString("CODE"));
                        responseCode.setCode_description(rs.getString("CODE_DESCRIPTION"));
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
                    databaseResponse.setWorkflowID(cals.getString("pWORKFLOW_ID"));
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
                cals.setInt("pENTERPRISE_ID", numOfRetry);
                cals.setString("pMODULE", email);
                cals.setString("pACTION", email);
                cals.setString("pINFO_KEY", email);
                cals.setString("pINFO_VALUE", email);
                cals.setString("pDETAIL", email);
                cals.setString("pAGENT", email);
                cals.setString("pAGENT_DETAIL", email);
                cals.setString("pHMAC", email);
                cals.setString("pCREATED_BY", email);                
                
                cals.registerOutParameter("pLOG_ID", java.sql.Types.BIGINT);
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

}
