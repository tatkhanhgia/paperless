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

//    @Override
//    public List<IDXCertificate> getIDXCertificates() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<IDXCertificate> idxCertificates = new ArrayList<>();
//        try {
//            String str = "{ call SP_FO_IDENTITY_IDX_CERTIFICATES_LIST() }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    IDXCertificate idxCertificate = new IDXCertificate();
//                    idxCertificate.setId(rs.getInt("ID"));
//                    idxCertificate.setName(rs.getString("NAME"));
//                    idxCertificate.setValidFrom(rs.getTimestamp("EFFECTIVE_DT") != null ? new Date(rs.getTimestamp("EFFECTIVE_DT").getTime()) : null);
//                    idxCertificate.setValidTo(rs.getTimestamp("EXPIRATION_DT") != null ? new Date(rs.getTimestamp("EXPIRATION_DT").getTime()) : null);
//
//                    byte[] data = null;
//                    Blob blobData = rs.getBlob("PRIVATE_KEY");
//                    if (blobData != null) {
//                        data = Utils.saveByteArrayOutputStream(blobData.getBinaryStream());
//                    }
//                    idxCertificate.setPrivateKeyEncryptedData(data);
//                    idxCertificate.setCertificate(rs.getString("CERTIFICATE"));
//                    idxCertificates.add(idxCertificate);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting idxCertificate. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIDXCertificates in milliseconds: " + timeElapsed / 1000000);
//        }
//        return idxCertificates;
//    }
//
//    @Override
//    public List<RelyingParty> getRelyingParties() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<RelyingParty> relyingParties = new ArrayList<>();
//        try {
//            String str = "{ call SP_FO_RELYING_PARTY_LIST() }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    RelyingParty relyingParty = new RelyingParty();
//                    relyingParty.setId(rs.getInt("ID"));
//                    relyingParty.setName(rs.getString("NAME"));
//                    relyingParty.setAssignedBy(rs.getInt("ASSIGNED_BY"));
//                    relyingParty.setIdentityEnabled(true);
//                    relyingParty.setAwsEnabled(rs.getBoolean("AWS_V4_ENABLED"));
//
//                    AWSV4PropertiesJSNObject AwsV4PropertiesJSNObject = null;
//                    if (rs.getBoolean("AWS_V4_ENABLED")) {
//                        if (!Utils.isNullOrEmpty(rs.getString("AWS_V4_PROPERTIES"))) {
//                            AwsV4PropertiesJSNObject = objectMapper.readValue(rs.getString("AWS_V4_PROPERTIES"), AWSV4PropertiesJSNObject.class);
//                        } else {
//                            if (LogHandler.isShowErrorLog()) {
//                                LOG.error("RelyingParty " + rs.getString("NAME") + " AWS_V4_ENABLED=True but AWS_V4_PROPERTIES is NULL or EMPTY");
//                            }
//                        }
//                    }
//                    relyingParty.setAwsProperties(AwsV4PropertiesJSNObject);
//
//                    boolean identityEnabled = rs.getBoolean("IDENTITY_ENABLED");
//                    if (identityEnabled) {
//                        String identityPropertiesJson = rs.getString("IDENTITY_PROPERTIES");
//                        IdentityPropertiesJSNObject identityPropertiesJSNObject = null;
//                        if (!Utils.isNullOrEmpty(identityPropertiesJson)) {
//                            identityPropertiesJSNObject = objectMapper.readValue(identityPropertiesJson, IdentityPropertiesJSNObject.class);
//                        } else {
//                            identityPropertiesJSNObject = new IdentityPropertiesJSNObject();
//                        }
//                        relyingParty.setIdentityProperties(identityPropertiesJSNObject);
//
//                        String ipList = rs.getString("IDENTITY_IP_ACCESS_PRIVILEGE");
//                        IPRestrictionList ipRestrictionList = null;
//                        if (!Utils.isNullOrEmpty(ipList)) {
//                            ipRestrictionList = objectMapper.readValue(ipList, IPRestrictionList.class);
//                        }
//                        relyingParty.setIdentityIPRestriction(ipRestrictionList);
//                    } else {
//                        if (LogHandler.isShowInfoLog()) {
//                            LOG.info("Warning! IDENTITY_ENABLED is False");
//                        }
//                    }
//
//                    boolean verificationEnabled = rs.getBoolean("E_VERIFICATION_ENABLED");
//                    if (verificationEnabled) {
//                        String verificationPropertiesJson = rs.getString("E_VERIFICATION_PROPERTIES");
//                        VerificationPropertiesJSNObject verificationPropertiesJSNObject = null;
//                        if (!Utils.isNullOrEmpty(verificationPropertiesJson)) {
//                            verificationPropertiesJSNObject = objectMapper.readValue(verificationPropertiesJson, VerificationPropertiesJSNObject.class);
//                        } else {
//                            verificationPropertiesJSNObject = new VerificationPropertiesJSNObject();
//                        }
//                        relyingParty.setVerificationProperties(verificationPropertiesJSNObject);
//
//                        String ipList = rs.getString("E_VERIFICATION_IP_ACCESS_PRIVILEGE");
//                        IPRestrictionList ipRestrictionList = null;
//                        if (!Utils.isNullOrEmpty(ipList)) {
//                            ipRestrictionList = objectMapper.readValue(ipList, IPRestrictionList.class);
//                        }
//                        relyingParty.setVerificationIPRestriction(ipRestrictionList);
//                    } else {
//                        if (LogHandler.isShowInfoLog()) {
//                            LOG.info("Warning! E_VERIFICATION_ENABLED is False");
//                        }
//                    }
//
//                    boolean smtpEnabled = rs.getBoolean("SMTP_ENABLED");
//                    if (smtpEnabled) {
//                        String smtpProJson = rs.getString("SMTP_PROPERTIES");
//                        if (!Utils.isNullOrEmpty(smtpProJson)) {
//                            SMTPProperties smtpProperties = objectMapper.readValue(smtpProJson, SMTPProperties.class);
//                            relyingParty.setSmtpEnabled(smtpEnabled);
//                            relyingParty.setSmtpProperties(smtpProperties);
//                        }
//                    }
//                    boolean smsgwEnabled = rs.getBoolean("SMSGW_ENABLED");
//                    if (smsgwEnabled) {
//                        String smsgwProJson = rs.getString("SMSGW_PROPERTIES");
//                        if (!Utils.isNullOrEmpty(smsgwProJson)) {
//                            SMSGWProperties smsgwProperties = objectMapper.readValue(smsgwProJson, SMSGWProperties.class);
//                            relyingParty.setSmsgatewayEnabled(smsgwEnabled);
//                            relyingParty.setSmsgwProperties(smsgwProperties);
//                        }
//                    }
//                    boolean dmsEnabled = rs.getBoolean("DMS_ENABLED");
//                    if (dmsEnabled) {
//                        String dmsProJson = rs.getString("DMS_PROPERTIES");
//                        if (!Utils.isNullOrEmpty(dmsProJson)) {
//                            DMSProperties dmsProperties = objectMapper.readValue(dmsProJson, DMSProperties.class);
//                            relyingParty.setDmsEnabled(dmsEnabled);
//                            relyingParty.setDmsProperties(dmsProperties);
//                        }
//                    }
//
//                    //process license
//                    if (AwsV4PropertiesJSNObject != null) {
//                        RelyingPartyAttribute licenseData = getRelyingPartyAttribute(rs.getInt("ID"), RelyingParty.ATTR_RELYING_PARTY_LICENSE);
//                        if (licenseData != null) {
//                            try {
//                                String licData = licenseData.getValue();
//                                if (!Utils.isNullOrEmpty(licData)) {
//                                    String basicAuth = rs.getString("NAME") + ":" + AwsV4PropertiesJSNObject.getSecretKey();
//                                    byte[] keyToDecryptLicense = Crypto.hashData(basicAuth.getBytes(), Crypto.HASH_MD5);
//                                    LicenseGenerator licenseGenerator = new LicenseGenerator();
//                                    licenseGenerator.setKeyData(keyToDecryptLicense);
//                                    licenseGenerator.load(licData);
//
//                                    LicenseServerData licenseServerData = new LicenseServerData();
//                                    licenseServerData.setCreatedDt(licenseGenerator.getCreatedDt());
//                                    licenseServerData.setExpiredDt(licenseGenerator.getExpiredDt());
//                                    licenseServerData.setLiveMatchingEnabled(licenseGenerator.isLiveMatchingEnabled());
//                                    licenseServerData.setLiveMatchingMaxTransactionCounter(licenseGenerator.getLiveMatchingCounter());
//
//                                    licenseServerData.setFacialFeatureAreaEnabled(licenseGenerator.isFacialFeatureAreaEnabled());
//                                    licenseServerData.setFacialFeatureAreaMaxTransactionCounter(licenseGenerator.getFacialFeatureAreaCounter());
//
//                                    licenseServerData.setPaEnabled(licenseGenerator.isPaEnabled());
//                                    licenseServerData.setPaMaxTransactionCounter(licenseGenerator.getPaCounter());
//
//                                    licenseServerData.setCadesVerificationEnabled(licenseGenerator.isCadesEnabled());
//                                    licenseServerData.setCadesVerificationMaxTransactionCounter(licenseGenerator.getCadesCounter());
//
//                                    licenseServerData.setPadesVerificationEnabled(licenseGenerator.isPadesEnabled());
//                                    licenseServerData.setPadesVerificationMaxTransactionCounter(licenseGenerator.getPadesCounter());
//
//                                    licenseServerData.setXadesVerificationEnabled(licenseGenerator.isXadesEnabled());
//                                    licenseServerData.setXadesVerificationMaxTransactionCounter(licenseGenerator.getXadesCounter());
//
//                                    relyingParty.setLicenseServerData(licenseServerData);
//
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                if (LogHandler.isShowErrorLog()) {
//                                    LOG.error("Error while loading license data of RelyingParty " + rs.getString("NAME") + ". Details: " + Utils.printStackTrace(e));
//                                }
//                            }
//                        } else {
//                            if (LogHandler.isShowErrorLog()) {
//                                LOG.error("Cannot load license data for RelyingParty " + rs.getString("NAME") + " due to RELYING_PARTY_LICENSE is NULL or EMPTY");
//                            }
//                        }
//                    } else {
//                        if (LogHandler.isShowErrorLog()) {
//                            LOG.error("Cannot load license data for RelyingParty " + rs.getString("NAME") + " due to AwsV4PropertiesJSNObject is NULL or EMPTY");
//                        }
//                    }
//
//                    relyingParties.add(relyingParty);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting Relying Parties. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getRelyingParties in milliseconds: " + timeElapsed / 1000000);
//        }
//        return relyingParties;
//    }
//
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
//
//    @Override
//    public boolean updateRelyingPartyAttribute(int relyingPartyId, int attributeId, String value, byte[] binary) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_RELYING_PARTY_ATTR_UPDATE(?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setInt("pRELYING_PARTY_ID", relyingPartyId);
//                cals.setInt("pRELYING_PARTY_ATTR_TYPE_ID", attributeId);
//                cals.setString("pVALUE", value);
//                InputStream in = null;
//                if (binary != null) {
//                    in = new ByteArrayInputStream(binary);
//                    cals.setBlob("pBLOB", in);
//                    in.close();
//                } else {
//                    cals.setBlob("pBLOB", in);
//                }
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//                if (LogHandler.isShowDebugLog()) {
//                    //LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while updating relying party attributes. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateRelyingPartyAttribute in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public RelyingPartyAttribute getRelyingPartyAttribute(int relyingPartyId, int attributeId) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        RelyingPartyAttribute relyingPartyAttribute = null;
//        try {
//            String str = "{ call SP_FO_RELYING_PARTY_ATTR_LIST(?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setInt("pRELYING_PARTY_ID", relyingPartyId);
//            cals.setInt("pRELYING_PARTY_ATTR_TYPE_ID", attributeId);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    relyingPartyAttribute = new RelyingPartyAttribute();
//                    relyingPartyAttribute.setRelyingPartyId(rs.getInt("RELYING_PARTY_ID"));
//                    relyingPartyAttribute.setAttributeId(rs.getInt("RELYING_PARTY_ATTR_TYPE_ID"));
//                    relyingPartyAttribute.setValue(rs.getString("VALUE"));
//                    Blob blobData = rs.getBlob("BLOB");
//                    byte[] data = null;
//                    if (blobData != null) {
//                        data = Utils.saveByteArrayOutputStream(blobData.getBinaryStream());
//                    }
//                    relyingPartyAttribute.setBlob(data);
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting RelyingPartyAttribute. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getRelyingPartyAttribute in milliseconds: " + timeElapsed / 1000000);
//        }
//        return relyingPartyAttribute;
//    }
//
//    @Override
//    public List<Integer> getIdentityFunctionIDGrantForRP(int relyingPartyId) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<Integer> functionIds = new ArrayList<>();
//        try {
//            String str = "{ call SP_REST_IDENTITY_FUNCTION_ATTR_LIST(?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setInt("pRELYING_PARTY_ID", relyingPartyId);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    functionIds.add(rs.getInt("IDENTITY_FUNCTION_ID"));
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting list of function id for relying party. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentityFunctionIDGrantForRP in milliseconds: " + timeElapsed / 1000000);
//        }
//        return functionIds;
//    }
//
//    @Override
//    public DatabaseResponse getIdentityLogId() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        DatabaseResponse dr = null;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_SYS_IDENTITY_LOG_INSERT(?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.registerOutParameter("pID", java.sql.Types.INTEGER);
//                cals.registerOutParameter("pCREATED_DT", java.sql.Types.TIMESTAMP);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                dr = new DatabaseResponse();
//                dr.setIdentityLogId(cals.getLong("pID"));
//                dr.setIdentityLogDt(new Date(cals.getTimestamp("pCREATED_DT").getTime()));
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while getting identity log id. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentityLogId in milliseconds: " + timeElapsed / 1000000);
//        }
//        return dr;
//    }
//
//    @Override
//    public boolean insertIdentityLog(
//            long id,
//            long subjectId,
//            String subjectUUID,
//            long processId,
//            String processUUID,
//            String extendedId,
//            String requestData,
//            String responseData,
//            String requestHeader,
//            String requestBillCode,
//            String transactionId,
//            int responseCodeId,
//            int functionId,
//            int relyingPartyId,
//            String requestIp,
//            Date createdDt,
//            Date modifiedDt) {
//        long startTime = System.nanoTime();
//        // pre-process requestData
//        requestData = Utils.cutoffBigDataInJson(requestData, IdentityConstant.KEY_TOO_LONG, IdentityConstant.KEY_SENSITIVE);
//        responseData = Utils.cutoffBigDataInJson(responseData, IdentityConstant.KEY_TOO_LONG, IdentityConstant.KEY_SENSITIVE);
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_LOG_INSERT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pID", id);
//                cals.setLong("pIDENTITY_SUBJECT_ID", subjectId);
//                cals.setString("pSUBJECT_UUID", subjectUUID);
//                cals.setLong("pIDENTITY_PROCESS_ID", processId);
//                cals.setString("pPROCESS_UUID", processUUID);
//                cals.setString("pREQUEST_DATA", requestData);
//                cals.setString("pRESPONSE_DATA", responseData);
//                cals.setInt("pRELYING_PARTY_ID", relyingPartyId);
//                cals.setString("pRELYING_PARTY_BILLCODE", extendedId);
//                cals.setString("pREQUEST_BILLCODE", requestBillCode);
//                cals.setString("pRESPONSE_BILLCODE", transactionId);
//                cals.setInt("pRESPONSE_CODE_ID", responseCodeId);
//                cals.setInt("pIDENTITY_FUNCTION_ID", functionId);
//                cals.setString("pREQUEST_IP", requestIp);
//                cals.setTimestamp("pCREATED_DT", new Timestamp(createdDt.getTime()));
//                cals.setInt("pCREATED_BY", Configuration.getInstance().getAppUserDBID());
//                cals.setTimestamp("pMODIFIED_DT", new Timestamp(modifiedDt.getTime()));
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while inserting identity log. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of insertIdentityLog in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public boolean updateIdentityLogAttribute(long identityLogId, int attributeId, String value, byte[] binary) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call (?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pIDENTITY_LOG_ID", identityLogId);
//                cals.setInt("pIDENTITY_LOG_ATTR_TYPE_ID", attributeId);
//                cals.setString("pVALUE", value);
//                InputStream in = null;
//                if (binary != null) {
//                    in = new ByteArrayInputStream(binary);
//                    cals.setBlob("pBLOB", in);
//                    in.close();
//                } else {
//                    cals.setBlob("pBLOB", in);
//                }
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while updating identity log attributes. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateIdentityLogAttribute in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public DatabaseResponse getIdentitySubjectId() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        DatabaseResponse dr = null;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_SYS_IDENTITY_SUBJECT_INSERT(?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.registerOutParameter("pID", java.sql.Types.INTEGER);
//                cals.registerOutParameter("pCREATED_DT", java.sql.Types.TIMESTAMP);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                dr = new DatabaseResponse();
//                dr.setIdentitySubjectId(cals.getLong("pID"));
//                dr.setIdentitySubjectDt(new Date(cals.getTimestamp("pCREATED_DT").getTime()));
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while getting identity subject id. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentitySubjectId in milliseconds: " + timeElapsed / 1000000);
//        }
//        return dr;
//    }
//
//    @Override
//    public boolean insertIdentitySubject(
//            long id,
//            String subjectUUID,
//            String fullName,
//            String gender,
//            String email,
//            boolean emailVerified,
//            String mobile,
//            boolean mobileVerified,
//            Date dob,
//            Date doe,
//            Date doi,
//            String pob,
//            String nationality,
//            IdentityDocumentType identityDocumentType,
//            String identityDocumentValue,
//            String address,
//            Date createdDt,
//            Date modifiedDt) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_SUBJECT_INSERT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pID", id);
//                cals.setString("pSUBJECT_UUID", subjectUUID);
//                cals.setString("pFULL_NAME", fullName);
//                cals.setString("pGENDER", gender);
//                cals.setString("pEMAIL", email);
//                cals.setBoolean("pEMAIL_VERIFIED_ENABLED", emailVerified);
//                cals.setString("pMOBILE", mobile);
//                cals.setBoolean("pMOBILE_VERIFIED_ENABLED", mobileVerified);
//                cals.setDate("pDOB", dob != null ? new java.sql.Date(dob.getTime()) : null);
//                cals.setDate("pDOE", doe != null ? new java.sql.Date(doe.getTime()) : null);
//                cals.setDate("pDOI", doi != null ? new java.sql.Date(doi.getTime()) : null);
//                cals.setString("pPOB", pob);
//                cals.setString("pNATIONALITY", nationality);
//                if (identityDocumentType != null) {
//                    cals.setInt("pIDENTITY_DOCUMENT_TYPE_ID", identityDocumentType.getId());
//                    cals.setString("pIDENTITY_DOCUMENT_VALUE", identityDocumentValue);
//                } else {
//                    cals.setObject("pIDENTITY_DOCUMENT_TYPE_ID", null);
//                    cals.setObject("pIDENTITY_DOCUMENT_VALUE", null);
//                }
//                cals.setString("pADDRESS", address);
//                cals.setTimestamp("pCREATED_DT", new Timestamp(createdDt.getTime()));
//                cals.setInt("pCREATED_BY", Configuration.getInstance().getAppUserDBID());
//                cals.setTimestamp("pMODIFIED_DT", new Timestamp(modifiedDt.getTime()));
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while inserting subject. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of insertIdentitySubject in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public List<IdentityDocumentType> getIdentityDocumentTypes() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<IdentityDocumentType> identityDocTypes = new ArrayList<>();
//        try {
//            String str = "{ call SP_REST_IDENTITY_DOCUMENT_TYPE_LIST() }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    IdentityDocumentType idt = new IdentityDocumentType();
//                    idt.setId(rs.getInt("ID"));
//                    idt.setName(rs.getString("NAME"));
//                    identityDocTypes.add(idt);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting list of identity document types. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentityDocumentTypes in milliseconds: " + timeElapsed / 1000000);
//        }
//        return identityDocTypes;
//    }
//
//    @Override
//    public IdentitySubject getIdentitySubject(String uuid) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        IdentitySubject identitySubject = null;
//        try {
//            String str = "{ call SP_REST_IDENTITY_SUBJECT_GET_BY_SUBJECT_UUID(?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.setString("pSUBJECT_UUID", uuid);
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    identitySubject = new IdentitySubject();
//                    identitySubject.setId(rs.getLong("ID"));
//                    identitySubject.setUuid(rs.getString("SUBJECT_UUID"));
//                    identitySubject.setAsuranceLevelId(rs.getInt("IDENTITY_ASSURANCE_LEVEL_ID"));
//                    identitySubject.setProcessId(rs.getLong("BEST_IDENTITY_PROCESS_ID"));
//                    identitySubject.setName(rs.getString("FULL_NAME"));
//                    identitySubject.setFirstName(rs.getString("FIRST_NAME"));
//                    identitySubject.setLastName(rs.getString("LAST_NAME"));
//                    identitySubject.setGender(rs.getString("GENDER"));
//                    identitySubject.setEmail(rs.getString("EMAIL"));
//                    identitySubject.setEmailVerified(rs.getBoolean("EMAIL_VERIFIED_ENABLED"));
//                    identitySubject.setMobile(rs.getString("MOBILE"));
//                    identitySubject.setMobileVerified(rs.getBoolean("MOBILE_VERIFIED_ENABLED"));
//                    identitySubject.setDob(rs.getDate("DOB"));
//                    identitySubject.setDoe(rs.getDate("DOE"));
//                    identitySubject.setDoi(rs.getDate("DOI"));
//                    identitySubject.setPob(rs.getString("POB"));
//                    identitySubject.setNationality(rs.getString("NATIONALITY"));
//                    identitySubject.setIdentityDocumentTypeId(rs.getInt("IDENTITY_DOCUMENT_TYPE_ID"));
//                    identitySubject.setIdentityDocumentValue(rs.getString("IDENTITY_DOCUMENT_VALUE"));
//                    identitySubject.setAddress(rs.getString("ADDRESS"));
//                    identitySubject.setCreatedDt(new Date(rs.getTimestamp("CREATED_DT").getTime()));
//                    identitySubject.setModifiedDt(new Date(rs.getTimestamp("MODIFIED_DT").getTime()));
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting identity subject uuid " + uuid + ". Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentitySubject in milliseconds: " + timeElapsed / 1000000);
//        }
//        return identitySubject;
//    }
//
//    @Override
//    public boolean updateIdentitySubject(
//            long id,
//            String fullName,
//            String firstName,
//            String lastName,
//            String gender,
//            String email,
//            Boolean emailVerified,
//            String mobile,
//            Boolean mobileVerified,
//            Date dob,
//            Date doe,
//            Date doi,
//            String pob,
//            String nationality,
//            IdentityDocumentType identityDocumentType,
//            String identityDocumentValue,
//            String identityDocumentExValue,
//            String address) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_SUBJECT_UPDATE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pID", id);
//                cals.setString("pFULL_NAME", fullName);
//                cals.setString("pFIRST_NAME", firstName);
//                cals.setString("pLAST_NAME", lastName);
//                cals.setString("pGENDER", gender);
//                cals.setString("pEMAIL", email);
//                if (emailVerified != null) {
//                    cals.setBoolean("pEMAIL_VERIFIED_ENABLED", emailVerified);
//                } else {
//                    cals.setObject("pEMAIL_VERIFIED_ENABLED", null);
//                }
//                cals.setString("pMOBILE", mobile);
//                if (mobileVerified != null) {
//                    cals.setBoolean("pMOBILE_VERIFIED_ENABLED", mobileVerified);
//                } else {
//                    cals.setObject("pMOBILE_VERIFIED_ENABLED", null);
//                }
//                cals.setDate("pDOB", dob != null ? new java.sql.Date(dob.getTime()) : null);
//                cals.setDate("pDOE", doe != null ? new java.sql.Date(doe.getTime()) : null);
//                cals.setDate("pDOI", doi != null ? new java.sql.Date(doi.getTime()) : null);
//                cals.setString("pPOB", pob);
//                cals.setString("pNATIONALITY", nationality);
//                if (identityDocumentType != null) {
//                    cals.setInt("pIDENTITY_DOCUMENT_TYPE_ID", identityDocumentType.getId());
//                    cals.setString("pIDENTITY_DOCUMENT_VALUE", identityDocumentValue);
//                    cals.setString("pEX_IDENTITY_DOCUMENT_VALUE", identityDocumentExValue);
//                } else {
//                    cals.setObject("pIDENTITY_DOCUMENT_TYPE_ID", null);
//                    cals.setObject("pIDENTITY_DOCUMENT_VALUE", null);
//                    cals.setObject("pEX_IDENTITY_DOCUMENT_VALUE", null);
//                }
//                cals.setString("pADDRESS", address);
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while updating subject. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateIdentitySubject in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public List<IdentityProcessType> getProcessTypes() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<IdentityProcessType> processTypes = new ArrayList<>();
//        try {
//            String str = "{ call SP_REST_IDENTITY_PROCESS_TYPE_LIST() }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    IdentityProcessType processType = new IdentityProcessType();
//                    processType.setId(rs.getInt("ID"));
//                    processType.setName(rs.getString("NAME"));
//                    processTypes.add(processType);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting process types. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getProcessTypes in milliseconds: " + timeElapsed / 1000000);
//        }
//        return processTypes;
//    }
//
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

//    @Override
//    public List<P2P> getP2Ps() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<P2P> p2ps = new ArrayList<>();
//        try {
//            String str = "{ call SP_FO_P2P_LIST() }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    P2P p2p = new P2P();
//                    p2p.setP2pID(rs.getInt("ID"));
//                    p2p.setName(rs.getString("NAME"));
//                    p2p.setUri(rs.getString("URI"));
//                    String uuid = rs.getString("UUID");
//                    if (Utils.isNullOrEmpty(uuid)) {
//                        uuid = "";
//                    }
//                    p2p.setUuid(uuid);
//
////                    boolean ssl2Enabled = rs.getBoolean("SSL2_ENABLED");
////                    p2p.setSsl2Enabled(ssl2Enabled);
////                    if (ssl2Enabled) {
////                        Ssl2Properties ssl2Properties = objectMapper.readValue(rs.getString("SSL2_PROPERTIES"), Ssl2Properties.class);
////                        p2p.setSsl2Properties(ssl2Properties);
////                    }
////                    p2p.setAwsEnabled(rs.getBoolean("AWS_V4_ENABLED"));
////                    p2p.setEtsi102204Enabled(rs.getBoolean("ETSI_102204_ENABLED"));
////
////                    boolean cacheEnabled = rs.getBoolean("CACHE_ENABLED");
////                    p2p.setCacheEnable(cacheEnabled);
////                    if (cacheEnabled) {
////                        CacheProperties cacheProperties = objectMapper.readValue(rs.getString("CACHE_PROPERTIES"), CacheProperties.class);
////                        p2p.setCacheProperties(cacheProperties);
////                    }
//                    String engineJson = rs.getString("ENGINE_PROPERTIES");
//                    if (!Utils.isNullOrEmpty(engineJson)) {
//                        TypeReference<HashMap<String, Value>> typeRef
//                                = new TypeReference<HashMap<String, Value>>() {
//                        };
//                        HashMap<String, Value> engineProperties = objectMapper.readValue(engineJson, typeRef);
//                        p2p.setEngineProperties(engineProperties);
//                    }
//
//                    List<P2PEntityAttribute> p2pEntityAttribute = getP2PEntityAttributes(rs.getInt("ID"));
//                    p2p.setP2pEntityAttribute(p2pEntityAttribute);
//                    p2ps.add(p2p);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting list of P2P. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getP2Ps in milliseconds: " + timeElapsed / 1000000);
//        }
//        return p2ps;
//    }
//
//    @Override
//    public List<P2PEntityAttribute> getP2PEntityAttributes(int p2pID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<P2PEntityAttribute> p2pEntityAttributes = new ArrayList<>();
//        try {
//            String str = "{ call SP_FO_P2P_ENTITY_ATTR_LIST(?) }";
//
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//
//            cals = conn.prepareCall(str);
//            cals.setInt("pP2P_ID", p2pID);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    P2PEntityAttribute p2pEntityAttribute = new P2PEntityAttribute();
//
//                    P2PFunctionAccessPrivilege p2pFunctionAccessPrivilege = null;
//                    if (!Utils.isNullOrEmpty(rs.getString("P2P_FUNCTION_ACCESS_PRIVILEGE"))) {
//                        p2pFunctionAccessPrivilege = objectMapper.readValue(rs.getString("P2P_FUNCTION_ACCESS_PRIVILEGE"), P2PFunctionAccessPrivilege.class);
//                    }
//
//                    p2pEntityAttribute.setP2pFunctionAccessPrivilege(p2pFunctionAccessPrivilege);
//
//                    P2PIPAccessPrivilege p2pIPAccessPrivilege = null;
//                    if (!Utils.isNullOrEmpty(rs.getString("IP_ACCESS_PRIVILEGE"))) {
//                        p2pIPAccessPrivilege = objectMapper.readValue(rs.getString("IP_ACCESS_PRIVILEGE"), P2PIPAccessPrivilege.class);
//                    }
//                    p2pEntityAttribute.setP2pIPAccessPrivilege(p2pIPAccessPrivilege);
//                    p2pEntityAttribute.setP2pID(rs.getInt("P2P_ID"));
//                    p2pEntityAttribute.setEntityID(rs.getInt("ENTITY_ID"));
//
//                    p2pEntityAttributes.add(p2pEntityAttribute);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting list p2p entity attributes. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getP2PEntityAttributes in milliseconds: " + timeElapsed / 1000000);
//        }
//        return p2pEntityAttributes;
//    }
//
//    @Override
//    public List<P2PFunction> getP2PFunctions() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<P2PFunction> p2pFunctions = new ArrayList<>();
//        try {
//            String str = "{ call SP_FO_P2P_FUNCTION_LIST() }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    P2PFunction p2pFunction = new P2PFunction();
//                    p2pFunction.setP2pFunctionID(rs.getInt("ID"));
//                    p2pFunction.setName(rs.getString("NAME"));
//                    p2pFunctions.add(p2pFunction);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting p2p function. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getP2PFunctions in milliseconds: " + timeElapsed / 1000000);
//        }
//        return p2pFunctions;
//    }
//
//    @Override
//    public DatabaseResponse getP2PLogID() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        DatabaseResponse databaseResponse = null;
//        int numOfRetry = retryTimes;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_SYS_P2P_LOG_INSERT(?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.registerOutParameter("pID", java.sql.Types.INTEGER);
//                cals.registerOutParameter("pCREATED_DT", java.sql.Types.TIMESTAMP);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                databaseResponse = new DatabaseResponse();
//                databaseResponse.setP2pId(cals.getLong("pID"));
//                databaseResponse.setP2pDt(new Date(cals.getTimestamp("pCREATED_DT").getTime()));
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while getting P2P Log ID. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getP2PLogID in milliseconds: " + timeElapsed / 1000000);
//        }
//        return databaseResponse;
//    }
//
//    @Override
//    public void insertP2PLog(
//            long p2pLogID,
//            int srcEntityID,
//            int desEntityID,
//            String srcUri,
//            String desUri,
//            String entityBillCode,
//            String requestBillCode,
//            String responseBillCode,
//            int p2pFunctionID,
//            int responseCodeID,
//            String requestIP,
//            String requestData,
//            String responseData,
//            Date createdDt,
//            Date modifiedDt,
//            int modifiedBy) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_FO_P2P_LOG_INSERT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pID", p2pLogID);
//                cals.setInt("pSOURCE_ENTITY_ID", srcEntityID);
//                cals.setInt("pDESTINATION_ENTITY_ID", desEntityID);
//                cals.setString("pSOURCE_URI", srcUri);
//                cals.setString("pDESTINATION_URI", desUri);
//                cals.setString("pENTITY_BILLCODE", entityBillCode);
//                cals.setString("pREQUEST_BILLCODE", requestBillCode);
//                cals.setString("pRESPONSE_BILLCODE", responseBillCode);
//                cals.setInt("pP2P_FUNCTION_ID", p2pFunctionID);
//                cals.setInt("pRESPONSE_CODE_ID", responseCodeID);
//                cals.setString("pREQUEST_IP", requestIP);
//                cals.setString("pREQUEST_DATA", requestData);
//                cals.setString("pRESPONSE_DATA", responseData);
//                cals.setTimestamp("pCREATED_DT", new Timestamp(createdDt.getTime()));
//                cals.setInt("pCREATED_BY", Configuration.getInstance().getAppUserDBID());
//                cals.setTimestamp("pMODIFIED_DT", new Timestamp(modifiedDt.getTime()));
//                cals.setInt("pMODIFIED_BY", modifiedBy);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while inserting P2P Log ID. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of insertP2PLog in milliseconds: " + timeElapsed / 1000000);
//        }
//    }
//
//    @Override
//    public boolean insertIdentityProcess(
//            long id,
//            String uuid,
//            int typeId,
//            int statusId,
//            int providerId,
//            long subjectId,
//            IdentityDocumentType identityDocumentType,
//            int relyingPartyId,
//            String newEmail,
//            String newMobile,
//            String name,
//            String gender,
//            Date dob,
//            String pob,
//            String nationality,
//            String documentValue,
//            String address,
//            Date createdDt,
//            Date modifiedDt) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_PROCESS_INSERT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pID", id);
//                cals.setString("pPROCESS_UUID", uuid);
//                cals.setInt("pIDENTITY_PROCESS_TYPE_ID", typeId);
//                cals.setInt("pIDENTITY_PROCESS_STATUS_ID", statusId);
//                cals.setInt("pIDENTITY_PROVIDER_ID", providerId);
//                cals.setLong("pIDENTITY_SUBJECT_ID", subjectId);
//
//                if (identityDocumentType != null) {
//                    cals.setInt("pIDENTITY_DOCUMENT_TYPE_ID", identityDocumentType.getId());
//                    cals.setString("pIDENTITY_DOCUMENT_VALUE", documentValue);
//                } else {
//                    cals.setObject("pIDENTITY_DOCUMENT_TYPE_ID", null);
//                    cals.setObject("pIDENTITY_DOCUMENT_VALUE", null);
//                }
//
//                cals.setInt("pRELYING_PARTY_ID", relyingPartyId);
//                cals.setString("pNEW_MAIL", newEmail);
//                cals.setString("pNEW_MOBILE", newMobile);
//                cals.setString("pFULL_NAME", name);
//                cals.setString("pGENDER", gender);
//                cals.setDate("pDOB", dob != null ? new java.sql.Date(dob.getTime()) : null);
//                cals.setString("pPOB", pob);
//                cals.setString("pNATIONALITY", nationality);
//                cals.setString("pADDRESS", address);
//
//                cals.setString("pNOTIFICATION_URL", null);
//                cals.setString("pAUTHORIZATION_HEADER", null);
//                cals.setString("pPROCESS_PARAMETERS", null);
//                cals.setString("pAUTHORIZATION", null);
//                cals.setString("pCHALLENGE", null);
//
//                cals.setTimestamp("pCREATED_DT", new Timestamp(createdDt.getTime()));
//                cals.setInt("pCREATED_BY", Configuration.getInstance().getAppUserDBID());
//                cals.setTimestamp("pMODIFIED_DT", new Timestamp(modifiedDt.getTime()));
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while inserting process. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of insertIdentityProcess in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public DatabaseResponse getIdentityProcessId() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        DatabaseResponse dr = null;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_SYS_IDENTITY_PROCESS_INSERT(?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.registerOutParameter("pID", java.sql.Types.INTEGER);
//                cals.registerOutParameter("pCREATED_DT", java.sql.Types.TIMESTAMP);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                dr = new DatabaseResponse();
//                dr.setIdentityProcessId(cals.getLong("pID"));
//                dr.setIdentityProcessDt(new Date(cals.getTimestamp("pCREATED_DT").getTime()));
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while getting identity process id. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentityProcessId in milliseconds: " + timeElapsed / 1000000);
//        }
//        return dr;
//    }
//
//    @Override
//    public boolean updateIdentityProcessAttribute(
//            long processId,
//            int attributeId,
//            String value,
//            byte[] binary) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_PROCESS_ATTR_INSERT(?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pIDENTITY_PROCESS_ID", processId);
//                cals.setInt("pIDENTITY_PROCESS_ATTR_TYPE_ID", attributeId);
//                cals.setString("pVALUE", value);
//                InputStream in = null;
//                if (binary != null) {
//                    in = new ByteArrayInputStream(binary);
//                    cals.setBlob("pBLOB", in);
//                    in.close();
//                } else {
//                    cals.setBlob("pBLOB", in);
//                }
//                cals.setInt("pCREATED_BY", Configuration.getInstance().getAppUserDBID());
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while updating identity process attributes. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateIdentityProcessAttribute in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public IdentityProcess getIdentityProcess(String uuid) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        IdentityProcess identityProcess = null;
//        try {
//            String str = "{ call SP_REST_IDENTITY_PROCESS_GET(?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.setString("pPROCESS_UUID", uuid);
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    identityProcess = new IdentityProcess();
//                    identityProcess.setId(rs.getLong("ID"));
//                    identityProcess.setUuid(rs.getString("PROCESS_UUID"));
//                    identityProcess.setTypeId(rs.getInt("IDENTITY_PROCESS_TYPE_ID"));
//                    identityProcess.setStatusId(rs.getInt("IDENTITY_PROCESS_STATUS_ID"));
//                    identityProcess.setProviderId(rs.getInt("IDENTITY_PROVIDER_ID"));
//                    identityProcess.setSubjectId(rs.getLong("IDENTITY_SUBJECT_ID"));
//                    identityProcess.setDocumentTypeId(rs.getInt("IDENTITY_DOCUMENT_TYPE_ID"));
//                    identityProcess.setRelyingPartyId(rs.getInt("RELYING_PARTY_ID"));
//                    identityProcess.setNewEmail(rs.getString("NEW_MAIL"));
//                    identityProcess.setNewMobile(rs.getString("NEW_MOBILE"));
//                    identityProcess.setName(rs.getString("FULL_NAME"));
//                    identityProcess.setGender(rs.getString("GENDER"));
//                    identityProcess.setDob(rs.getDate("DOB"));
//                    identityProcess.setPob(rs.getString("POB"));
//                    identityProcess.setNationality(rs.getString("NATIONALITY"));
//                    identityProcess.setDocumentValue(rs.getString("IDENTITY_DOCUMENT_VALUE"));
//                    identityProcess.setAddress(rs.getString("ADDRESS"));
//                    identityProcess.setCreatedDt(rs.getTimestamp("CREATED_DT") != null ? new Date(rs.getTimestamp("CREATED_DT").getTime()) : null);
//                    identityProcess.setModifiedDt(rs.getTimestamp("MODIFIED_DT") != null ? new Date(rs.getTimestamp("MODIFIED_DT").getTime()) : null);
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting identity process uuid " + uuid + ". Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentityProcess in milliseconds: " + timeElapsed / 1000000);
//        }
//        return identityProcess;
//    }
//
//    @Override
//    public IdentityProcessAttribute getIdentityProcessAttribute(long processId, int attributeId) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        IdentityProcessAttribute processAttribute = null;
//        try {
//            String str = "{ call SP_REST_IDENTITY_PROCESS_ATTR_GET(?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setLong("pIDENTITY_PROCESS_ID", processId);
//            cals.setInt("pIDENTITY_PROCESS_ATTR_TYPE_ID", attributeId);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    processAttribute = new IdentityProcessAttribute();
//                    processAttribute.setProcessId(rs.getLong("IDENTITY_PROCESS_ID"));
//                    processAttribute.setAttributeId(rs.getInt("IDENTITY_PROCESS_ATTR_TYPE_ID"));
//                    processAttribute.setValue(rs.getString("VALUE"));
//                    Blob blobData = rs.getBlob("BLOB");
//                    byte[] data = null;
//                    if (blobData != null) {
//                        data = Utils.saveByteArrayOutputStream(blobData.getBinaryStream());
//                    }
//                    processAttribute.setBlob(data);
//                    processAttribute.setCreatedDt(new Date(rs.getTimestamp("CREATED_DT").getTime()));
//                    processAttribute.setModifiedDt(new Date(rs.getTimestamp("MODIFIED_DT").getTime()));
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting IdentityProcessAttribute. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentityProcessAttribute in milliseconds: " + timeElapsed / 1000000);
//        }
//        return processAttribute;
//    }
//
//    @Override
//    public DatabaseResponse authorizeOtp(long processId, String otp, int maxRemainingCounter, int timeoutDuration) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        DatabaseResponse databaseResponse = new DatabaseResponse();
//        int numOfRetry = retryTimes;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_PROCESS_CHECK_OTP_PASSCODE(?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pIDENTITY_PROCESS_ID", processId);
//                cals.setString("pOTP_PASSCODE", otp);
//                cals.setInt("pMAX_COUNTER_FOR_PASSCODE", maxRemainingCounter);
//                cals.setInt("pTIMEOUT_DURATION_PASSCODE", timeoutDuration);
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//                cals.registerOutParameter("pREMAINING_COUNTER", java.sql.Types.INTEGER);
//                cals.registerOutParameter("pRESPONSE_CODE_NAME", java.sql.Types.VARCHAR);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                databaseResponse.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE_NAME")));
//                databaseResponse.setRemainingCounter(cals.getInt("pREMAINING_COUNTER"));
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while validating otp. Details: " + Utils.printStackTrace(e));
//                }
//                databaseResponse.setStatus(IdentityConstant.CODE_UNEXPE_EXP);
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of authorizeOtp in milliseconds: " + timeElapsed / 1000000);
//        }
//        return databaseResponse;
//    }
//
//    @Override
//    public boolean updateSubjectAssuranceLevel(long subjectId, int assuranceLevelId) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_SUBJECT_UPDATE_ASSURANCE_LEVEL(?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pID", subjectId);
//                cals.setInt("pIDENTITY_ASSURANCE_LEVEL_ID", assuranceLevelId);
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while updating subject assurance level. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateSubjectAssuranceLevel in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public boolean updateLevelBasicResult(long subjectId, Boolean emailVerified, Boolean mobileVerified) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_SUBJECT_UPDATE_VERIFIED_ENABLED(?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pID", subjectId);
//                if (emailVerified != null) {
//                    cals.setBoolean("pEMAIL_VERIFIED_ENABLED", emailVerified);
//                } else {
//                    cals.setObject("pEMAIL_VERIFIED_ENABLED", null);
//                }
//
//                if (mobileVerified != null) {
//                    cals.setBoolean("pMOBILE_VERIFIED_ENABLED", mobileVerified);
//                } else {
//                    cals.setObject("pMOBILE_VERIFIED_ENABLED", null);
//                }
//
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while updating subject level BASIC result. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateLevelBasicResult in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public boolean updateIdentityProcessStatus(long processId, int statusId) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_PROCESS_UPDATE_STATUS(?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pID", processId);
//                cals.setInt("pIDENTITY_PROCESS_STATUS_ID", statusId);
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while updating process status. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateIdentityProcessStatus in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public List<IdentityProcessStatus> getIdentityProcessStatus() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<IdentityProcessStatus> identityProcessStatuses = new ArrayList<>();
//        try {
//            String str = "{ call SP_REST_IDENTITY_PROCESS_STATUS_LIST() }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    IdentityProcessStatus identityProcessStatus = new IdentityProcessStatus();
//                    identityProcessStatus.setId(rs.getInt("ID"));
//                    identityProcessStatus.setName(rs.getString("NAME"));
//                    identityProcessStatuses.add(identityProcessStatus);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting identity process status. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentityProcessStatus in milliseconds: " + timeElapsed / 1000000);
//        }
//        return identityProcessStatuses;
//    }
//
//    @Override
//    public boolean updateBestIdentityProcess(long subjectId, long processId) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_SUBJECT_UPDATE_BEST_IDENTITY_PROCESS_ID(?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pID", subjectId);
//                cals.setLong("pBEST_IDENTITY_PROCESS_ID", processId);
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while updating the best identity process for a subject. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateBestIdentityProcess in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public DatabaseResponse getIdentityDocumentId() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        DatabaseResponse dr = null;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_SYS_IDENTITY_DOCUMENT_INSERT(?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.registerOutParameter("pID", java.sql.Types.INTEGER);
//                cals.registerOutParameter("pCREATED_DT", java.sql.Types.TIMESTAMP);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                dr = new DatabaseResponse();
//                dr.setIdentityDocumentId(cals.getLong("pID"));
//                dr.setIdentityDocumentDt(new Date(cals.getTimestamp("pCREATED_DT").getTime()));
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while getting identity document id. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentityDocumentId in milliseconds: " + timeElapsed / 1000000);
//        }
//        return dr;
//    }
//
//    @Override
//    public boolean insertIdentityDocument(
//            long id,
//            int docTypeId,
//            int providerId,
//            long subjectId,
//            int imageTypeId,
//            String uuid,
//            String dmsProperties,
//            String digest,
//            String fileName,
//            long fileSize,
//            long processId,
//            Date createdDt,
//            Date modifiedDt) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_DOCUMENT_INSERT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pID", id);
//                cals.setInt("pIDENTITY_DOCUMENT_TYPE_ID", docTypeId);
//                cals.setInt("pIDENTITY_PROVIDER_ID", providerId);
//                cals.setLong("pIDENTITY_SUBJECT_ID", subjectId);
//                cals.setInt("pIDENTITY_IMAGE_TYPE_ID", imageTypeId);
//                cals.setString("pUUID", uuid);
//                cals.setString("pDMS_PROPERTIES", dmsProperties);
//                cals.setString("pDIGEST", digest);
//                cals.setString("pFILE_NAME", fileName);
//                cals.setLong("pFILE_SIZE", fileSize);
//                cals.setLong("pIDENTITY_PROCESS_ID", processId);
//                cals.setTimestamp("pCREATED_DT", new Timestamp(createdDt.getTime()));
//                cals.setLong("pCREATED_BY", Configuration.getInstance().getAppUserDBID());
//                cals.setTimestamp("pMODIFIED_DT", new Timestamp(modifiedDt.getTime()));
//                cals.setLong("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while inserting identity document. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of insertIdentityDocument in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public IdentityDocument getIdentityDocument(long subjectId, long processId, int imageTypeId) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        IdentityDocument identityDocument = null;
//        try {
//            String str = "{ call SP_REST_IDENTITY_DOCUMENT_LIST(?,?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setLong("pIDENTITY_SUBJECT_ID", subjectId);
//            cals.setLong("pIDENTITY_PROCESS_ID", processId);
//            cals.setInt("pIDENTITY_IMAGE_TYPE_ID", imageTypeId);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    identityDocument = new IdentityDocument();
//                    identityDocument.setId(rs.getLong("ID"));
//                    identityDocument.setDocumentTypeId(rs.getInt("IDENTITY_DOCUMENT_TYPE_ID"));
//                    identityDocument.setProviderId(rs.getInt("IDENTITY_PROVIDER_ID"));
//                    identityDocument.setSubjectId(rs.getLong("IDENTITY_SUBJECT_ID"));
//                    identityDocument.setImageTypeId(rs.getInt("IDENTITY_IMAGE_TYPE_ID"));
//                    identityDocument.setUuid(rs.getString("UUID"));
//                    String dmsProJson = rs.getString("DMS_PROPERTIES");
//                    if (!Utils.isNullOrEmpty(dmsProJson)) {
//                        DMSProperties dmsProperties = objectMapper.readValue(dmsProJson, DMSProperties.class);
//                        identityDocument.setDmsProperties(dmsProperties);
//                    }
//                    identityDocument.setDigest(rs.getString("DIGEST"));
//                    identityDocument.setFileName(rs.getString("FILE_NAME"));
//                    identityDocument.setFileSize(rs.getLong("FILE_SIZE"));
//                    identityDocument.setProcessId(rs.getLong("IDENTITY_PROCESS_ID"));
//                    identityDocument.setCreatedDt(new Date(rs.getTimestamp("CREATED_DT").getTime()));
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting identity document. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentityDocument in milliseconds: " + timeElapsed / 1000000);
//        }
//        return identityDocument;
//    }
//
//    @Override
//    public List<IdentityProvider> getIdentityProviders() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<IdentityProvider> identityProviders = new ArrayList<>();
//        try {
//            String str = "{ call SP_REST_IDENTITY_PROVIDER_LIST() }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    IdentityProvider identityProvider = new IdentityProvider();
//                    identityProvider.setId(rs.getInt("ID"));
//                    identityProvider.setName(rs.getString("NAME"));
//                    String identityProviderPropertiesJsn = rs.getString("PROPERTIES");
//                    if (!Utils.isNullOrEmpty(identityProviderPropertiesJsn)) {
//                        IdentityProviderProperties identityProviderProperties
//                                = objectMapper.readValue(identityProviderPropertiesJsn, IdentityProviderProperties.class);
//                        identityProvider.setIdentityProviderProperties(identityProviderProperties);
//                    }
//                    identityProviders.add(identityProvider);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting identity process provider. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentityProviders in milliseconds: " + timeElapsed / 1000000);
//        }
//        return identityProviders;
//    }
//
//    @Override
//    public List<IdentityProcess> getIdentityProcesses(long subjectId, Integer processTypeId, Integer statusId) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<IdentityProcess> identityProcesses = new ArrayList<>();
//        try {
//            String str = "{ call SP_REST_IDENTITY_PROCESS_GET_BY_SUBJECT_ID(?,?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setLong("pIDENTITY_SUBJECT_ID", subjectId);
//            cals.setObject("pIDENTITY_PROCESS_TYPE_ID", processTypeId);
//            cals.setObject("pIDENTITY_PROCESS_STATUS_ID", statusId);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    IdentityProcess identityProcess = new IdentityProcess();
//                    identityProcess.setId(rs.getLong("ID"));
//                    identityProcess.setUuid(rs.getString("PROCESS_UUID"));
//                    identityProcess.setTypeId(rs.getInt("IDENTITY_PROCESS_TYPE_ID"));
//                    identityProcess.setStatusId(rs.getInt("IDENTITY_PROCESS_STATUS_ID"));
//                    identityProcess.setProviderId(rs.getInt("IDENTITY_PROVIDER_ID"));
//                    identityProcess.setSubjectId(rs.getLong("IDENTITY_SUBJECT_ID"));
//                    identityProcess.setDocumentTypeId(rs.getInt("IDENTITY_DOCUMENT_TYPE_ID"));
//                    identityProcess.setRelyingPartyId(rs.getInt("RELYING_PARTY_ID"));
//                    identityProcess.setNewEmail(rs.getString("NEW_MAIL"));
//                    identityProcess.setNewMobile(rs.getString("NEW_MOBILE"));
//                    identityProcess.setName(rs.getString("FULL_NAME"));
//                    identityProcess.setGender(rs.getString("GENDER"));
//                    identityProcess.setDob(rs.getDate("DOB"));
//                    identityProcess.setPob(rs.getString("POB"));
//                    identityProcess.setNationality(rs.getString("NATIONALITY"));
//                    identityProcess.setDocumentValue(rs.getString("IDENTITY_DOCUMENT_VALUE"));
//                    identityProcess.setAddress(rs.getString("ADDRESS"));
//                    identityProcess.setCreatedDt(rs.getTimestamp("CREATED_DT") != null ? new Date(rs.getTimestamp("CREATED_DT").getTime()) : null);
//                    identityProcess.setModifiedDt(rs.getTimestamp("MODIFIED_DT") != null ? new Date(rs.getTimestamp("MODIFIED_DT").getTime()) : null);
//                    identityProcesses.add(identityProcess);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting identity process provider. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentityProcesses in milliseconds: " + timeElapsed / 1000000);
//        }
//        return identityProcesses;
//    }
//
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
                        if (code >= 4000 || code == 0) {
                            ResponseCode responseCode = new ResponseCode();
//                            responseCode.setId(rs.getInt("ID"));
                            responseCode.setName(rs.getString("NAME"));
                            responseCode.setCode(rs.getString("CODE"));
                            responseCode.setCode_description(rs.getString("CODE_DESCRIPTION"));
                            responseCodes.add(responseCode);
                        }
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

//    @Override
//    public boolean insertIdXCertificate(
//            String name,
//            String uri,
//            Date validFrom,
//            Date validTo,
//            String certificate,
//            byte[] encryptedPrivateKey,
//            String properties,
//            String remarkEn,
//            String remark) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_BO_IDENTITY_IDX_CERTIFICATES_INSERT(?,?,?,?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//
//                cals.setString("pNAME", name);
//                cals.setString("pURI", uri);
//                cals.setTimestamp("pEFFECTIVE_DT", new Timestamp(validFrom.getTime()));
//                cals.setTimestamp("pEXPIRATION_DT", new Timestamp(validTo.getTime()));
//                cals.setString("pCERTIFICATE", certificate);
//                InputStream in = null;
//                if (encryptedPrivateKey != null) {
//                    in = new ByteArrayInputStream(encryptedPrivateKey);
//                    cals.setBlob("pPRIVATE_KEY", in);
//                    in.close();
//                } else {
//                    cals.setBlob("pPRIVATE_KEY", in);
//                }
//                cals.setString("pPROPERTIES", properties);
//                cals.setString("pREMARK_EN", remarkEn);
//                cals.setString("pREMARK", remark);
//                cals.setInt("pCREATED_BY", 1);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while inserting IdXCertificate. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of insertIdXCertificate in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public Province getProvince() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        Province province = new Province();
//        HashMap<String, String> englishTable = new HashMap<>();
//        HashMap<String, String> vietnameseTable = new HashMap<>();
//        HashMap<String, String> nameToRemark = new HashMap<>();
//        try {
//            String str = "{ call SP_FO_PROVINCE_LIST() }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    englishTable.put(rs.getString("REMARK_EN"), rs.getString("NAME"));
//                    vietnameseTable.put(rs.getString("REMARK"), rs.getString("NAME"));
//                    nameToRemark.put(rs.getString("NAME"), rs.getString("REMARK"));
//                }
//                province.setEnglishTable(englishTable);
//                province.setVietnameseTable(vietnameseTable);
//                province.setNameToRemark(nameToRemark);
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while Province. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getProvince in milliseconds: " + timeElapsed / 1000000);
//        }
//        return province;
//    }
//
//    @Override
//    public DatabaseResponse getVerificationLogId() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        DatabaseResponse dr = null;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_SYS_VERIFICATION_LOG_INSERT(?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.registerOutParameter("pID", java.sql.Types.INTEGER);
//                cals.registerOutParameter("pCREATED_DT", java.sql.Types.TIMESTAMP);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                dr = new DatabaseResponse();
//                dr.setVerificationLogId(cals.getLong("pID"));
//                dr.setVerificationLogDt(new Date(cals.getTimestamp("pCREATED_DT").getTime()));
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while getting verification log id. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getVerificationLogId in milliseconds: " + timeElapsed / 1000000);
//        }
//        return dr;
//    }
//
//    @Override
//    public boolean insertVerificationLog(
//            long id,
//            String extendedId,
//            String requestData,
//            String responseData,
//            String requestHeader,
//            String requestBillCode,
//            String transactionId,
//            int responseCodeId,
//            int functionId,
//            int relyingPartyId,
//            String requestIp,
//            Date createdDt,
//            Date modifiedDt) {
//        long startTime = System.nanoTime();
//        // pre-process requestData
//        requestData = Utils.cutoffBigDataInJson(requestData, IdentityConstant.KEY_TOO_LONG, IdentityConstant.KEY_SENSITIVE);
//        responseData = Utils.cutoffBigDataInJson(responseData, IdentityConstant.KEY_TOO_LONG, IdentityConstant.KEY_SENSITIVE);
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_VERIFICATION_LOG_INSERT(?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pID", id);
//                cals.setString("pREQUEST_DATA", requestData);
//                cals.setString("pRESPONSE_DATA", responseData);
//                cals.setInt("pRELYING_PARTY_ID", relyingPartyId);
//                cals.setString("pRELYING_PARTY_BILLCODE", extendedId);
//                cals.setString("pREQUEST_BILLCODE", requestBillCode);
//                cals.setString("pRESPONSE_BILLCODE", transactionId);
//                cals.setInt("pRESPONSE_CODE_ID", responseCodeId);
//                cals.setInt("pVERIFICATION_FUNCTION_ID", functionId);
//                cals.setString("pREQUEST_IP", requestIp);
//                cals.setTimestamp("pCREATED_DT", new Timestamp(createdDt.getTime()));
//                cals.setInt("pCREATED_BY", Configuration.getInstance().getAppUserDBID());
//                cals.setTimestamp("pMODIFIED_DT", new Timestamp(modifiedDt.getTime()));
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                //#todo
////                if (LogHandler.isShowErrorLog()) {
////                    LOG.error("Error while inserting verification log. Details: " + Utils.printStackTrace(e));
////                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of insertVerificationLog in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public List<CertificationAuthority> getCertificationAuthorities() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<CertificationAuthority> certificationAuthorities = new ArrayList<>();
//        try {
//            String str = "{ call SP_FO_CERTIFICATE_AUTHORITY_LIST() }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    CertificationAuthority certificationAuthority = new CertificationAuthority();
//                    certificationAuthority.setCertificationAuthorityID(rs.getInt("ID"));
//                    certificationAuthority.setName(rs.getString("NAME"));
//                    certificationAuthority.setUri(rs.getString("URI"));
//                    if (rs.getTimestamp("EFFECTIVE_DT") != null) {
//                        certificationAuthority.setEffectiveDate(new Date(rs.getTimestamp("EFFECTIVE_DT").getTime()));
//                    }
//                    if (rs.getTimestamp("EXPIRATION_DT") != null) {
//                        certificationAuthority.setExpiredDate(new Date(rs.getTimestamp("EXPIRATION_DT").getTime()));
//                    }
//                    if (Utils.isNullOrEmpty(rs.getString("CERTIFICATE"))) {
//                        continue;
//                    }
//                    certificationAuthority.setPemCertificate(rs.getString("CERTIFICATE"));
//                    certificationAuthority.setPemExCertificate(rs.getString("EX_CERTIFICATE"));
//
//                    X509Certificate x509Certificate = Crypto.getX509Object(rs.getString("CERTIFICATE"));
//
//                    if (x509Certificate == null) {
//                        if (LogHandler.isShowErrorLog()) {
//                            LOG.error("Cannot get X509 Certificate object of CA " + rs.getString("NAME"));
//                        }
//                        continue;
//                    }
//                    certificationAuthority.setX509Object(x509Certificate);
//                    certificationAuthority.setSubjectDn(x509Certificate.getSubjectDN().toString());
//                    certificationAuthority.setRemark(rs.getString("REMARK"));
//                    certificationAuthority.setRemarkEn(rs.getString("REMARK_EN"));
//                    /*
//                    if (LogHandler.isShowDebugLog()) {
//                        LOG.debug("CA: " + rs.getString("NAME")
//                                + " SubjectKeyIdentifier: " + Crypto.getSubjectKeyIdentifier(x509Certificate));
//                    }
//                     */
//                    certificationAuthority.setSubjectKeyIdentifier(Crypto.getSubjectKeyIdentifier(x509Certificate));
//                    certificationAuthority.setIssuerKeyIdentifier(Crypto.getIssuerKeyIdentifier(x509Certificate));
//                    certificationAuthority.setCommonName(CertificatePolicy.getCommonName(x509Certificate.getSubjectDN().toString()));
//
//                    if (rs.getString("PROPERTIES") != null) {
//                        CAProperties caProperties = objectMapper.readValue(rs.getString("PROPERTIES"), CAProperties.class);
//                        certificationAuthority.setCaProperties(caProperties);
//                    }
//                    certificationAuthorities.add(certificationAuthority);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting Certification Authority information. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getCertificationAuthorities in milliseconds: " + timeElapsed / 1000000);
//        }
//        return certificationAuthorities;
//    }
//
//    @Override
//    public CrlData getCrlData(int certificationAuthorityID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        CrlData crlData = null;
//        try {
//            String str = "{ call SP_FO_CERTIFICATE_AUTHORITY_CRLDATA_GET(?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setInt("pCERTIFICATE_AUTHORITY_ID", certificationAuthorityID);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    crlData = new CrlData();
//                    crlData.setCrlDataID(rs.getInt("ID"));
//                    crlData.setCertificationAuthorityID(rs.getInt("CERTIFICATE_AUTHORITY_ID"));
//                    byte[] data = null;
//                    Blob blobData = rs.getBlob("BLOB");
//                    if (blobData != null) {
//                        data = Utils.saveByteArrayOutputStream(blobData.getBinaryStream());
//                    }
//                    crlData.setBlob(data);
//                    crlData.setLastUpdateDt(rs.getTimestamp("LAST_UPDATED_DT") != null
//                            ? new Date(rs.getTimestamp("LAST_UPDATED_DT").getTime()) : null);
//                    crlData.setNextUpdateDt(rs.getTimestamp("NEXT_UPDATED_DT") != null
//                            ? new Date(rs.getTimestamp("NEXT_UPDATED_DT").getTime()) : null);
//                    crlData.setIssuerSubject(rs.getString("ISSUER_SUBJECT"));
//                    crlData.setAuthorityKeyID(rs.getString("AUTHORITY_KEY_ID"));
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting CRL data. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getCrlData in milliseconds: " + timeElapsed / 1000000);
//        }
//        return crlData;
//    }
//
//    @Override
//    public void updateCrlData(int certificationAuthorityID, byte[] crlData, Date lastUpdateDt, Date nextUpdateDt, String issuerSubject, String authorityKeyID, int updateBy) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_FO_CERTIFICATE_AUTHORITY_CRLDATA_INSERT(?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setInt("pCERTIFICATE_AUTHORITY_ID", certificationAuthorityID);
//                cals.setBlob("pBLOB", crlData != null ? new ByteArrayInputStream(crlData) : null);
//                cals.setTimestamp("pLAST_UPDATED_DT", new Timestamp(lastUpdateDt.getTime()));
//                cals.setTimestamp("pNEXT_UPDATED_DT", new Timestamp(nextUpdateDt.getTime()));
//                cals.setString("pISSUER_SUBJECT", issuerSubject);
//                cals.setString("pAUTHORITY_KEY_ID", authorityKeyID);
//                cals.setInt("pUPDATED_BY", updateBy);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while updating CRL Data. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateCrlData in milliseconds: " + timeElapsed / 1000000);
//        }
//    }
//
//    @Override
//    public DatabaseResponse insertCloudCertificateOwner(
//            String ownerUUID,
//            int clientManagerID,
//            int ownerStateID,
//            int ownerTypeID,
//            String personalName,
//            String organization,
//            String enterpriseID,
//            String personalID,
//            String phone,
//            String email,
//            String username,
//            String oath2Username,
//            String password,
//            String twoFactorAuthMethod) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        DatabaseResponse databaseResponse = new DatabaseResponse();
//        int numOfRetry = retryTimes;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_CLOUD_CERTIFICATE_OWNER_INSERT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setString("pCERTIFICATE_OWNER_UUID", ownerUUID);
//                cals.setInt("pCLOUD_CLIENT_MANAGER_ID", clientManagerID);
//                cals.setInt("pCLOUD_CERTIFICATE_OWNER_STATE_ID", ownerStateID);
//                cals.setInt("pCLOUD_CERTIFICATE_OWNER_TYPE_ID", ownerTypeID);
//                cals.setString("pPERSONAL_NAME", personalName);
//                cals.setString("pCOMPANY_NAME", organization);
//                cals.setString("pENTERPRISE_ID", enterpriseID);
//                cals.setString("pPERSONAL_ID", personalID);
//                cals.setString("pPHONE_CONTRACT", phone);
//                cals.setString("pEMAIL_CONTRACT", email);
//                cals.setString("pUSERNAME", username);
//                cals.setString("pPASSWORD", password);
//                cals.setObject("pOAUTH2_USERNAME", oath2Username);
//                cals.setInt("pREMAINING_COUNTER", 5); // ahihi
//                cals.setInt("pCREATED_BY", Configuration.getInstance().getAppUserDBID());
//                cals.setString("pTWO_FACTOR_METHOD_NAME", twoFactorAuthMethod);
//                cals.registerOutParameter("pCLOUD_CERTIFICATE_OWNER_ID", java.sql.Types.BIGINT);
//                cals.registerOutParameter("pRESPONSE_CODE_NAME", java.sql.Types.VARCHAR);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE_NAME"));
//                if (mysqlResult == 0) {
//                    databaseResponse.setStatus(VerificationConstant.CODE_SUCCESS);
//                    databaseResponse.setOwnerID(cals.getLong("pCLOUD_CERTIFICATE_OWNER_ID"));
//                } else if (mysqlResult == 1034
//                        || mysqlResult == 1035) {
//                    databaseResponse.setStatus(VerificationConstant.CODE_OWNER_EXISTED);
//                    databaseResponse.setOwnerID(cals.getLong("pCLOUD_CERTIFICATE_OWNER_ID"));
//                } else {
//                    if (LogHandler.isShowErrorLog()) {
//                        LOG.error("Error while inserting cloud certificate owner. Code: " + mysqlResult);
//                    }
//                    databaseResponse.setStatus(VerificationConstant.CODE_UNEXPE_EXP);
//                }
//                break;
//            } catch (Exception e) {
//                e.printStackTrace();
//                if (e.getMessage().contains("UQ_CLOUD_CERTIFICATE_OWNER_USERNAME")) {
//                    if (LogHandler.isShowErrorLog()) {
//                        LOG.error("Username has been registered for an owner");
//                    }
//                    databaseResponse.setStatus(VerificationConstant.CODE_USERNAME_EXISTED);
//                    break;
//                } else {
//                    numOfRetry--;
//                    if (LogHandler.isShowErrorLog()) {
//                        LOG.error("Error while inserting cloud certificate owner. Details: " + Utils.printStackTrace(e));
//                    }
//                    databaseResponse.setStatus(VerificationConstant.CODE_UNEXPE_EXP);
//                    e.printStackTrace();
//                }
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of insertCloudCertificateOwner in milliseconds: " + timeElapsed / 1000000);
//        }
//        return databaseResponse;
//    }
//
//    @Override
//    public Owner getOwnerByID(long id) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        Owner owner = null;
//        try {
//            String str = "{ call SP_FO_CLOUD_CERTIFICATE_OWNER_GET_BY_ID(?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setLong("pCLOUD_CERTIFICATE_OWNER_ID", id);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    owner = new Owner();
//                    owner.setId(rs.getLong("ID"));
//                    owner.setOwnerUUID(rs.getString("CERTIFICATE_OWNER_UUID"));
//                    owner.setPersonalName(rs.getString("PERSONAL_NAME"));
//                    owner.setOrganization(rs.getString("COMPANY_NAME"));
//
//                    String enterpriseID = rs.getString("ENTERPRISE_ID");
//                    String personalID = rs.getString("PERSONAL_ID");
//
//                    owner.setOwnerTypeID(rs.getInt("CLOUD_CERTIFICATE_OWNER_TYPE_ID"));
//
//                    if (!Utils.isNullOrEmpty(personalID)) {
//                        String[] parts = personalID.split(":");
//                        String prefix = parts[0] + ":";
//                        owner.setPersonalID(parts[1]);
//                        owner.setOwnerTypeDescription(IdentificationType.getType(personalID));
//                    }
//
//                    if (!Utils.isNullOrEmpty(enterpriseID)) {
//                        String[] parts = enterpriseID.split(":");
//                        String prefix = parts[0] + ":";
//                        owner.setEnterpriseID(parts[1]);
//                        owner.setOwnerTypeDescription(IdentificationType.getType(enterpriseID));
//                    }
//
//                    owner.setPhone(rs.getString("PHONE_CONTRACT"));
//                    owner.setEmail(rs.getString("EMAIL_CONTRACT"));
//                    owner.setUsername(rs.getString("USERNAME"));
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting owner info. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getOwnerByID in milliseconds: " + timeElapsed / 1000000);
//        }
//        return owner;
//    }
//
//    @Override
//    public Owner getOwnerByUUID(String ownerUUID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        Owner owner = null;
//        try {
//            String str = "{ call SP_FO_CLOUD_CERTIFICATE_OWNER_GET(?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setString("pCERTIFICATE_OWNER_UUID", ownerUUID);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    owner = new Owner();
//                    owner.setId(rs.getLong("ID"));
//                    owner.setOwnerUUID(rs.getString("CERTIFICATE_OWNER_UUID"));
//                    owner.setPersonalName(rs.getString("PERSONAL_NAME"));
//                    owner.setOrganization(rs.getString("COMPANY_NAME"));
//
//                    String enterpriseID = rs.getString("ENTERPRISE_ID");
//                    String personalID = rs.getString("PERSONAL_ID");
//
//                    owner.setOwnerTypeID(rs.getInt("CLOUD_CERTIFICATE_OWNER_TYPE_ID"));
//
//                    if (!Utils.isNullOrEmpty(personalID)) {
//                        String[] parts = personalID.split(":");
//                        String prefix = parts[0] + ":";
//                        owner.setPersonalID(parts[1]);
//                        owner.setOwnerTypeDescription(IdentificationType.getType(personalID));
//                    }
//
//                    if (!Utils.isNullOrEmpty(enterpriseID)) {
//                        String[] parts = enterpriseID.split(":");
//                        String prefix = parts[0] + ":";
//                        owner.setEnterpriseID(parts[1]);
//                        owner.setOwnerTypeDescription(IdentificationType.getType(enterpriseID));
//                    }
//
//                    owner.setPhone(rs.getString("PHONE_CONTRACT"));
//                    owner.setEmail(rs.getString("EMAIL_CONTRACT"));
//                    owner.setUsername(rs.getString("USERNAME"));
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting owner info. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getOwnerByUUID in milliseconds: " + timeElapsed / 1000000);
//        }
//        return owner;
//    }
//
//    @Override
//    public DatabaseResponse updateOwner(
//            long id,
//            String personalName,
//            String organization,
//            String enterpriseID,
//            String personalID,
//            String phone,
//            String email) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        DatabaseResponse databaseResponse = new DatabaseResponse();
//        int numOfRetry = retryTimes;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_CLOUD_CERTIFICATE_OWNER_UPDATE(?,?,?,?,?,?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pCLOUD_CERTIFICATE_OWNER_ID", id);
//                cals.setString("pPERSONAL_NAME", personalName);
//                cals.setString("pCOMPANY_NAME", organization);
//                cals.setString("pENTERPRISE_ID", enterpriseID);
//                cals.setString("pPERSONAL_ID", personalID);
//                cals.setString("pPHONE_CONTRACT", phone);
//                cals.setString("pEMAIL_CONTRACT", email);
//                cals.setObject("pTWO_FACTOR_METHOD_NAME", null);
//                cals.setObject("pLOA_ID", null);
//                cals.setObject("pKYC_EVIDENCE", null);
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//                cals.registerOutParameter("pRESPONSE_CODE_NAME", java.sql.Types.VARCHAR);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE_NAME"));
//                if (mysqlResult == 0) {
//                    databaseResponse.setStatus(VerificationConstant.CODE_SUCCESS);
//                } else if (mysqlResult == 1034 || mysqlResult == 1035) {
//                    databaseResponse.setStatus(VerificationConstant.CODE_CONFLICT_PERSONAL_ENTERPRISE_ID);
//                } else {
//                    if (LogHandler.isShowErrorLog()) {
//                        LOG.error("Error while updating cloud certificate owner. Code: " + mysqlResult);
//                    }
//                    databaseResponse.setStatus(VerificationConstant.CODE_UNEXPE_EXP);
//                }
//                break;
//            } catch (Exception e) {
//                e.printStackTrace();
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while updating owner. Details: " + Utils.printStackTrace(e));
//                }
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateOwner in milliseconds: " + timeElapsed / 1000000);
//        }
//        return databaseResponse;
//    }
//
//    @Override
//    public VerificationDatabaseResponse insertCertificate(
//            int certificateAuthorityID,
//            long ownerID,
//            String certificateUUID,
//            String personalName,
//            String organization,
//            String personalID,
//            String enterpriseID,
//            String certificate,
//            String serialNumber,
//            String thumbprint,
//            String subjectDN,
//            String issuerDN,
//            String phone,
//            String email,
//            int sharedModeID,
//            int responsibleBy,
//            int registrationPartyID,
//            int relyingPartyID,
//            String commonName,
//            String title,
//            String organizationUnit,
//            String locality,
//            String stateOrProvince,
//            String country,
//            long signingProfileID,
//            long cloudAgreementID,
//            Date validFrom,
//            Date validTo,
//            int duration) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        VerificationDatabaseResponse result = new VerificationDatabaseResponse();
//        int numOfRetry = retryTimes;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_VERIFICATION_CLOUD_CERTIFICATE_INSERT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pCERTIFICATE_PROFILE_ID", VerificationConstant.FEDERAL_ID);
//                cals.setInt("pCERTIFICATE_AUTHORITY_ID", certificateAuthorityID);
//                cals.setLong("pCERTIFICATE_TYPE_ID", VerificationConstant.FEDERAL_ID);
//                cals.setInt("pSERVICE_TYPE_ID", VerificationConstant.SERVICE_TYPE_ID_CREATION);
//                cals.setInt("pPKI_FORMFACTOR_ID", VerificationConstant.PKI_FORMFACTOR_ESIGNCLOUD);
//                cals.setLong("pCLOUD_CERTIFICATE_OWNER_ID", ownerID);
//                cals.setInt("pAMOUNT", 0);
//                cals.setInt("pGOVERNMENT_AMOUNT", 0);
//                cals.setString("pPERSONAL_NAME", personalName);
//                cals.setString("pCOMPANY_NAME", organization);
//                cals.setString("pPERSONAL_ID", personalID);
//                cals.setString("pENTERPRISE_ID", enterpriseID);
//                cals.setString("pSUBJECT", subjectDN);
//                cals.setString("pAUTHORISATION_PHONE", phone);
//                cals.setString("pAUTHORISATION_EMAIL", email);
//                cals.setInt("pMULTI_SIGNATURE", 0);
//                cals.setString("pSIGNER_PROPERTIES", null);
//                cals.setLong("pEX_CERTIFICATE_ID", VerificationConstant.FEDERAL_ID);
//                cals.setLong("pEX_CERTIFICATE_STATE_ID", VerificationConstant.FEDERAL_ID);
//                cals.setInt("pSHARED_MODE_ID", sharedModeID);
//                cals.setLong("pAUTH_MODE_ID", VerificationConstant.FEDERAL_ID);
//                cals.setInt("pSCAL", 1);
//                cals.setBoolean("pMULTIPLE_ACCESS_ENABLED", false);
//                cals.setInt("pENTITY_ID", Resources.getEntities().get(Entity.ENTITY_VERIFICATION_ENTITY).getEntityID());
//                cals.setLong("pRESPONSIBLED_BY", responsibleBy);
//                cals.setInt("pREGISTRATION_PARTY_ID", registrationPartyID);
//                cals.setInt("pCREATED_RELYING_PARTY_ID", relyingPartyID);
//                cals.setInt("pREMAINING_COUNTER", 5); // ahihi
//                cals.setString("pCOMMON_NAME", commonName);
//                cals.setString("pTITLE", title);
//                cals.setString("pORGANIZATIONAL_UNIT", organizationUnit);
//                cals.setString("pORGANIZATION", organization);
//                cals.setString("pLOCALITY", locality);
//                cals.setString("pSTATE_OR_PROVINCE_NAME", stateOrProvince);
//                cals.setString("pCOUNTRY_NAME", country);
//                cals.setString("pEMAIL", email);
//                cals.setString("pTELEPHONE", phone);
//                cals.setString("pISSUER_SUBJECT", issuerDN);
//                cals.setLong("pCERTIFICATE_SIGNING_PROFILE_ID", signingProfileID);
//                cals.setInt("pREMAINING_SIGNING_COUNTER", 5);
//                cals.setInt("pCREATED_BY", Configuration.getInstance().getAppUserDBID());
//                cals.setInt("pSAD_REMAINING_SIGNING_COUNTER", 5);
//                cals.setString("pCERTIFICATE_UUID", certificateUUID);
//                cals.setLong("pCREATED_CLOUD_AGREEMENT_ID", cloudAgreementID);
//                cals.setTimestamp("pEFFECTIVE_DT", validFrom != null ? new Timestamp(validFrom.getTime()) : null);
//                cals.setTimestamp("pEXPIRATION_DT", validTo != null ? new Timestamp(validTo.getTime()) : null);
//                cals.setInt("pDURATION", duration);
//                cals.setString("pCERTIFICATE", certificate);
//                cals.setString("pCERTIFICATE_SN", serialNumber);
//                cals.setString("pCERTIFICATE_THUMBPRINT", thumbprint);
//                cals.registerOutParameter("pCLOUD_CERTIFICATE_ID", java.sql.Types.BIGINT);
//                cals.registerOutParameter("pRESPONSE_CODE_NAME", java.sql.Types.VARCHAR);
//
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE_NAME"));
//                long certificateID = cals.getLong("pCLOUD_CERTIFICATE_ID");
//                result.setStatus(VerificationConstant.CODE_SUCCESS);
//                result.setCertificateID(certificateID);
//                break;
//            } catch (Exception e) {
//                e.printStackTrace();
//                if (e.getMessage().contains("UQ_CLOUD_CERTIFICATE_THUMBPRINT")) {
//                    result.setStatus(VerificationConstant.CODE_CERTIFICATE_EXISTED);
//                    break;
//                } else {
//                    numOfRetry--;
//                    result.setStatus(VerificationConstant.CODE_UNEXPE_EXP);
//                    if (LogHandler.isShowErrorLog()) {
//                        LOG.error("Error while inserting certificate. Details: " + Utils.printStackTrace(e));
//                    }
//                }
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of insertCertificate in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public List<RegistrationParty> getRegistrationParties() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<RegistrationParty> registrationParties = new ArrayList<>();
//        try {
//            String str = "{ call SP_FO_REGISTRATION_PARTY_LIST() }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    RegistrationParty registrationParty = new RegistrationParty();
//                    registrationParty.setRegistrationPartyID(rs.getInt("ID"));
//                    registrationParty.setName(rs.getString("NAME"));
//                    registrationParty.setUri(rs.getString("URI"));
//                    registrationParty.setDefaultBy(rs.getInt("DEFAULT_BY"));
//                    registrationParties.add(registrationParty);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting Registration Parties information. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getRegistrationParties in milliseconds: " + timeElapsed / 1000000);
//        }
//        return registrationParties;
//    }
//
//    @Override
//    public List<CloudCertificate> getCertificateByOwnerID(long cloudOwnerCertificateID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<CloudCertificate> listOfCloudCertificate = new ArrayList<>();
//        try {
//            String str = "{ call SP_FO_CLOUD_CERTIFICATE_GET_BY_OWNER_ID(?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setLong("pCLOUD_CERTIFICATE_OWNER_ID", cloudOwnerCertificateID);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    CloudCertificate cloudCertificate = new CloudCertificate();
//                    cloudCertificate.setId(rs.getLong("ID"));
//                    cloudCertificate.setCertificateStateID(rs.getInt("CERTIFICATE_STATE_ID"));
//                    cloudCertificate.setCertificateProfileID(rs.getInt("CERTIFICATE_PROFILE_ID"));
//                    cloudCertificate.setCertificateAuthorityID(rs.getInt("CERTIFICATE_AUTHORITY_ID"));
//                    cloudCertificate.setCertificateTypeID(rs.getInt("CERTIFICATE_TYPE_ID"));
//                    cloudCertificate.setCertificateUUID(rs.getString("CERTIFICATE_UUID"));
//                    cloudCertificate.setAmount(rs.getLong("AMOUNT"));
//                    cloudCertificate.setGovernmentAmount(rs.getLong("GOVERNMENT_AMOUNT"));
//                    cloudCertificate.setEffectiveDt(rs.getTimestamp("EFFECTIVE_DT") != null ? new Date(rs.getTimestamp("EFFECTIVE_DT").getTime()) : null);
//                    cloudCertificate.setExpirationDt(rs.getTimestamp("EXPIRATION_DT") != null ? new Date(rs.getTimestamp("EXPIRATION_DT").getTime()) : null);
//                    cloudCertificate.setAgreementDt(rs.getTimestamp("AGREEMENT_DT") != null ? new Date(rs.getTimestamp("AGREEMENT_DT").getTime()) : null);
//                    cloudCertificate.setDuration(rs.getInt("DURATION"));
//                    cloudCertificate.setCsr(rs.getString("CSR"));
//                    cloudCertificate.setCertificate(rs.getString("CERTIFICATE"));
//                    cloudCertificate.setCertificateSn(rs.getString("CERTIFICATE_SN"));
//                    cloudCertificate.setCertificateThumbprint(rs.getString("CERTIFICATE_THUMBPRINT"));
//                    cloudCertificate.setPersonalName(rs.getString("PERSONAL_NAME"));
//                    cloudCertificate.setCompanyName(rs.getString("COMPANY_NAME"));
//
//                    String personalID = rs.getString("PERSONAL_ID");
//                    String enterpriseID = rs.getString("ENTERPRISE_ID");
//
//                    if (!Utils.isNullOrEmpty(personalID)) {
//                        String[] parts = personalID.split(":");
//                        String prefix = parts[0] + ":";
//                        if (prefix.equals(CertificatePolicy.PREFIX_OWNER_PERSONAL_CODE)) {
//                            cloudCertificate.setPersonalID(parts[1]);
//                        } else if (prefix.equals(CertificatePolicy.PREFIX_OWNER_PERSONAL_PASSPORT_CODE)) {
//                            cloudCertificate.setPassportID(parts[1]);
//                        } else {
//                            cloudCertificate.setCitizenID(parts[1]);
//                        }
//                    }
//
//                    if (!Utils.isNullOrEmpty(enterpriseID)) {
//                        String[] parts = enterpriseID.split(":");
//                        String prefix = parts[0] + ":";
//                        if (prefix.equals(CertificatePolicy.PREFIX_OWNER_ENTERPRISE_TAX_CODE)) {
//                            cloudCertificate.setTaxID(parts[1]);
//                        } else {
//                            cloudCertificate.setBudgetID(parts[1]);
//                        }
//                    }
//
//                    cloudCertificate.setSubject(rs.getString("SUBJECT"));
//                    cloudCertificate.setAuthorisationPhone(rs.getString("AUTHORISATION_PHONE"));
//                    cloudCertificate.setAuthorisationEmail(rs.getString("AUTHORISATION_EMAIL"));
//                    cloudCertificate.setPastCloudCertificateID(rs.getInt("EX_CERTIFICATE_ID"));
//                    cloudCertificate.setCreatedCloudAgreementID(rs.getInt("CREATED_CLOUD_AGREEMENT_ID"));
//                    cloudCertificate.setCreatedRelyingPartyID(rs.getInt("CREATED_RELYING_PARTY_ID"));
//                    cloudCertificate.setShareModeID(rs.getInt("SHARED_MODE_ID"));
//                    String createdAgreementUUID = rs.getString("AGREEMENT_UUID");
//                    String[] parts = createdAgreementUUID.split("#");
//                    if (parts.length == 2) {
//                        cloudCertificate.setAgreementUUID(parts[1]);
//                    } else {
//                        cloudCertificate.setAgreementUUID(createdAgreementUUID);
//                    }
//                    listOfCloudCertificate.add(cloudCertificate);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting list of cloud certificate by owner id. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getCertificateByOwnerID in milliseconds: " + timeElapsed / 1000000);
//        }
//        return listOfCloudCertificate;
//    }
//
//    @Override
//    public Owner getOwnerByUsername(String username) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        Owner owner = null;
//        try {
//            String str = "{ call SP_FO_CLOUD_CERTIFICATE_OWNER_GET_BY_USERNAME(?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setString("pUSERNAME", username);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    owner = new Owner();
//                    owner.setId(rs.getLong("ID"));
//                    owner.setOwnerUUID(rs.getString("CERTIFICATE_OWNER_UUID"));
//                    owner.setPersonalName(rs.getString("PERSONAL_NAME"));
//                    owner.setOrganization(rs.getString("COMPANY_NAME"));
//
//                    String enterpriseID = rs.getString("ENTERPRISE_ID");
//                    String personalID = rs.getString("PERSONAL_ID");
//
//                    owner.setOwnerTypeID(rs.getInt("CLOUD_CERTIFICATE_OWNER_TYPE_ID"));
//
//                    if (!Utils.isNullOrEmpty(personalID)) {
//                        String[] parts = personalID.split(":");
//                        String prefix = parts[0] + ":";
//                        owner.setPersonalID(parts[1]);
//                        owner.setOwnerTypeDescription(IdentificationType.getType(personalID));
//                    }
//
//                    if (!Utils.isNullOrEmpty(enterpriseID)) {
//                        String[] parts = enterpriseID.split(":");
//                        String prefix = parts[0] + ":";
//                        owner.setEnterpriseID(parts[1]);
//                        owner.setOwnerTypeDescription(IdentificationType.getType(enterpriseID));
//                    }
//
//                    owner.setPhone(rs.getString("PHONE_CONTRACT"));
//                    owner.setEmail(rs.getString("EMAIL_CONTRACT"));
//                    owner.setUsername(rs.getString("USERNAME"));
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting owner info. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getOwnerByUsername in milliseconds: " + timeElapsed / 1000000);
//        }
//        return owner;
//    }
//
//    @Override
//    public DatabaseResponse insertAgreement(int relyingPartyID, String agreementUUID, long ownerID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        boolean result = false;
//        DatabaseResponse databaseResponse = new DatabaseResponse();
//        int numOfRetry = retryTimes;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_CLOUD_AGREEMENT_INSERT(?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setInt("pRELYING_PARTY_ID", relyingPartyID);
//                cals.setString("pAGREEMENT_UUID", agreementUUID);
//                cals.setLong("pCLOUD_CERTIFICATE_OWNER_ID", ownerID);
//                cals.setInt("pCREATED_BY", Configuration.getInstance().getAppUserDBID());
//                cals.registerOutParameter("pCLOUD_AGREEMENT_ID", java.sql.Types.BIGINT);
//                cals.registerOutParameter("pRESPONSE_CODE_NAME", java.sql.Types.VARCHAR);
//
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                int mysqlResult = Integer.parseInt(cals.getString("pRESPONSE_CODE_NAME"));
//                if (mysqlResult == 1010) {
//                    databaseResponse.setStatus(VerificationConstant.CODE_AGREEMENT_EXISTED);
//                } else {
//                    databaseResponse.setStatus(VerificationConstant.CODE_SUCCESS);
//                }
//                break;
//            } catch (Exception e) {
//                e.printStackTrace();
//                numOfRetry--;
//                databaseResponse.setStatus(VerificationConstant.CODE_UNEXPE_EXP);
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
//
//    @Override
//    public Agreement getAgreement(String agreementUUID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        Agreement agreement = null;
//        try {
//            String str = "{ call SP_REST_CLOUD_AGREEMENT_GET_INFO(?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setString("pAGREEMENT_UUID", agreementUUID);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    agreement = new Agreement();
//                    agreement.setId(rs.getLong("ID"));
//                    agreement.setAgreementUUID(rs.getString("AGREEMENT_UUID"));
//                    agreement.setOwnerID(rs.getLong("CLOUD_CERTIFICATE_OWNER_ID"));
//                    agreement.setRelyingPartyID(rs.getInt("RELYING_PARTY_ID"));
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting agreement. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getAgreement in milliseconds: " + timeElapsed / 1000000);
//        }
//        return agreement;
//    }
//
//    @Override
//    public boolean assignAgreement(String agreementUUID, long ownerID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        boolean result = false;
//        try {
//            String str = "{ call SP_REST_CLOUD_AGREEMENT_UPDATE(?,?,?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setString("pAGREEMENT_UUID", agreementUUID);
//            cals.setObject("pCLOUD_AGREEMENT_STATE_ID", null);
//            cals.setLong("pCLOUD_CERTIFICATE_OWNER_ID", ownerID);
//            cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            result = true;
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while assigining agreement. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of assignAgreement in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public CloudCertificate getCertificateByUUID(String certificateUUID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        CloudCertificate cloudCertificate = null;
//        try {
//            String str = "{ call SP_REST_CLOUD_CERTIFICATE_GET_BY_UUID(?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setString("pCERTIFICATE_UUID", certificateUUID);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    cloudCertificate = new CloudCertificate();
//                    cloudCertificate.setId(rs.getLong("ID"));
//                    cloudCertificate.setCertificateStateID(rs.getInt("CERTIFICATE_STATE_ID"));
//                    cloudCertificate.setCertificateProfileID(rs.getInt("CERTIFICATE_PROFILE_ID"));
//                    cloudCertificate.setCertificateAuthorityID(rs.getInt("CERTIFICATE_AUTHORITY_ID"));
//                    cloudCertificate.setCertificateTypeID(rs.getInt("CERTIFICATE_TYPE_ID"));
//                    cloudCertificate.setCertificateUUID(rs.getString("CERTIFICATE_UUID"));
//                    cloudCertificate.setAmount(rs.getLong("AMOUNT"));
//                    cloudCertificate.setGovernmentAmount(rs.getLong("GOVERNMENT_AMOUNT"));
//                    cloudCertificate.setEffectiveDt(rs.getTimestamp("EFFECTIVE_DT") != null ? new Date(rs.getTimestamp("EFFECTIVE_DT").getTime()) : null);
//                    cloudCertificate.setExpirationDt(rs.getTimestamp("EXPIRATION_DT") != null ? new Date(rs.getTimestamp("EXPIRATION_DT").getTime()) : null);
//                    cloudCertificate.setAgreementDt(rs.getTimestamp("AGREEMENT_DT") != null ? new Date(rs.getTimestamp("AGREEMENT_DT").getTime()) : null);
//                    cloudCertificate.setDuration(rs.getInt("DURATION"));
//                    cloudCertificate.setCsr(rs.getString("CSR"));
//                    cloudCertificate.setCertificate(rs.getString("CERTIFICATE"));
//                    cloudCertificate.setCertificateSn(rs.getString("CERTIFICATE_SN"));
//                    cloudCertificate.setCertificateThumbprint(rs.getString("CERTIFICATE_THUMBPRINT"));
//                    cloudCertificate.setPersonalName(rs.getString("PERSONAL_NAME"));
//                    cloudCertificate.setCompanyName(rs.getString("COMPANY_NAME"));
//
//                    String personalID = rs.getString("PERSONAL_ID");
//                    String enterpriseID = rs.getString("ENTERPRISE_ID");
//
//                    if (!Utils.isNullOrEmpty(personalID)) {
//                        String[] parts = personalID.split(":");
//                        String prefix = parts[0] + ":";
//                        if (prefix.equals(CertificatePolicy.PREFIX_OWNER_PERSONAL_CODE)) {
//                            cloudCertificate.setPersonalID(parts[1]);
//                        } else if (prefix.equals(CertificatePolicy.PREFIX_OWNER_PERSONAL_PASSPORT_CODE)) {
//                            cloudCertificate.setPassportID(parts[1]);
//                        } else {
//                            cloudCertificate.setCitizenID(parts[1]);
//                        }
//                    }
//
//                    if (!Utils.isNullOrEmpty(enterpriseID)) {
//                        String[] parts = enterpriseID.split(":");
//                        String prefix = parts[0] + ":";
//                        if (prefix.equals(CertificatePolicy.PREFIX_OWNER_ENTERPRISE_TAX_CODE)) {
//                            cloudCertificate.setTaxID(parts[1]);
//                        } else {
//                            cloudCertificate.setBudgetID(parts[1]);
//                        }
//                    }
//
//                    cloudCertificate.setSubject(rs.getString("SUBJECT"));
//                    cloudCertificate.setAuthorisationPhone(rs.getString("AUTHORISATION_PHONE"));
//                    cloudCertificate.setAuthorisationEmail(rs.getString("AUTHORISATION_EMAIL"));
//                    cloudCertificate.setPastCloudCertificateID(rs.getInt("EX_CERTIFICATE_ID"));
//                    cloudCertificate.setCreatedCloudAgreementID(rs.getInt("CREATED_CLOUD_AGREEMENT_ID"));
//                    cloudCertificate.setCreatedRelyingPartyID(rs.getInt("CREATED_RELYING_PARTY_ID"));
//                    cloudCertificate.setShareModeID(rs.getInt("SHARED_MODE_ID"));
//                    String createdAgreementUUID = rs.getString("AGREEMENT_UUID");
//                    if (!Utils.isNullOrEmpty(createdAgreementUUID)) {
//                        String[] parts = createdAgreementUUID.split("#");
//                        if (parts.length == 2) {
//                            cloudCertificate.setAgreementUUID(parts[1]);
//                        } else {
//                            cloudCertificate.setAgreementUUID(createdAgreementUUID);
//                        }
//                    }
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting cloud certificate by certificate uuid. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getCertificateByUUID in milliseconds: " + timeElapsed / 1000000);
//        }
//        return cloudCertificate;
//    }
//
//    @Override
//    public boolean updateCertificateState(long certificateID, int stateID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        boolean result = false;
//        try {
//            String str = "{ call SP_REST_CLOUD_CERTIFICATE_UPDATE_STATE(?,?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setLong("pCLOUD_CERTIFICATE_ID", certificateID);
//            cals.setInt("pCERTIFICATE_STATE_ID", stateID);
//            cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            result = true;
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while updating cloud certificate state. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateCertificateState in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public boolean updateOwnerAttribute(long ownerID, int ownerAttributeTypeID, String value, byte[] blobData) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        boolean result = false;
//        try {
//            String str = "{ call SP_REST_CLOUD_CERTIFICATE_OWNER_ATTR_UPDATE(?,?,?,?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setLong("pCLOUD_CERTIFICATE_OWNER_ID", ownerID);
//            cals.setInt("pCLOUD_CERTIFICATE_OWNER_ATTR_TYPE_ID", ownerAttributeTypeID);
//            cals.setString("pVALUE", value);
//            cals.setBlob("pBLOB", blobData != null ? new ByteArrayInputStream(blobData) : null);
//            cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            result = true;
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while updating owner attribute. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateOwnerAttribute in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public OwnerAttribute getOwnerAttribute(long ownerID, int ownerAttributeTypeID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        OwnerAttribute ownerAttribute = null;
//        try {
//            String str = "{ call SP_REST_CLOUD_CERTIFICATE_OWNER_ATTR_GET(?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setLong("pCLOUD_CERTIFICATE_OWNER_ID", ownerID);
//            cals.setInt("pCLOUD_CERTIFICATE_OWNER_ATTR_TYPE_ID", ownerAttributeTypeID);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    ownerAttribute = new OwnerAttribute();
//                    ownerAttribute.setValue(rs.getString("VALUE"));
//                    Blob blobData = rs.getBlob("BLOB");
//                    if (blobData != null) {
//                        ownerAttribute.setBlob(Utils.saveByteArrayOutputStream(blobData.getBinaryStream()));
//                    }
//                    ownerAttribute.setModifiedDt(new Date(rs.getTimestamp("MODIFIED_DT").getTime()));
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting owner attribute. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getOwnerAttribute in milliseconds: " + timeElapsed / 1000000);
//        }
//        return ownerAttribute;
//    }
//
//    @Override
//    public VerificationLog getVerificationLog(long id) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        VerificationLog verificationLog = null;
//        try {
//            String str = "{ call SP_REST_VERIFICATION_LOG_GET(?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setLong("pVERIFICATION_LOG_ID", id);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    verificationLog = new VerificationLog();
//                    verificationLog.setRelyingPartyID(rs.getInt("RELYING_PARTY_ID"));
//                    verificationLog.setResponseCodeID(rs.getInt("RESPONSE_CODE_ID"));
//                    verificationLog.setFunctionID(rs.getInt("VERIFICATION_FUNCTION_ID"));
//                    verificationLog.setResponseBillCode(rs.getString("RESPONSE_BILLCODE"));
//                    if (rs.getTimestamp("CREATED_DT") != null) {
//                        verificationLog.setCreatedDt(new Date(rs.getTimestamp("CREATED_DT").getTime()));
//                    }
//                    if (rs.getTimestamp("MODIFIED_DT") != null) {
//                        verificationLog.setModifiedDt(new Date(rs.getTimestamp("MODIFIED_DT").getTime()));
//                    }
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting verification log. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getVerificationLog in milliseconds: " + timeElapsed / 1000000);
//        }
//        return verificationLog;
//    }
//
//    @Override
//    public VerificationDatabaseResponse authorizeOtpForEVerification(long ownerID, String otp, int maxRemainingCounter, int timeoutDuration) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        VerificationDatabaseResponse verificationDatabaseResponse = new VerificationDatabaseResponse();
//        int numOfRetry = retryTimes;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_CLOUD_CERTIFICATE_OWNER_CHECK_OTP_PASSCODE(?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pCLOUD_CERTIFICATE_OWNER_ID", ownerID);
//                cals.setString("pOTP_PASSCODE", otp);
//                cals.setInt("pMAX_COUNTER_FOR_PASSCODE", maxRemainingCounter);
//                cals.setInt("pTIMEOUT_DURATION_PASSCODE", timeoutDuration);
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//                cals.registerOutParameter("pREMAINING_COUNTER", java.sql.Types.INTEGER);
//                cals.registerOutParameter("pRESPONSE_CODE_NAME", java.sql.Types.VARCHAR);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                verificationDatabaseResponse.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE_NAME")));
//                verificationDatabaseResponse.setRemainingCounter(cals.getInt("pREMAINING_COUNTER"));
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while validating otp. Details: " + Utils.printStackTrace(e));
//                }
//                verificationDatabaseResponse.setStatus(IdentityConstant.CODE_UNEXPE_EXP);
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of authorizeOtpForEVerification in milliseconds: " + timeElapsed / 1000000);
//        }
//        return verificationDatabaseResponse;
//    }
//
//    @Override
//    public VerificationLogAttribute getVerificationLogAttribute(long verificationLogID, int attributeID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        VerificationLogAttribute verificationLogAttribute = null;
//        try {
//            String str = "{ call SP_REST_VERIFICATION_LOG_ATTR_GET(?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setLong("pVERIFICATION_LOG_ID", verificationLogID);
//            cals.setInt("pVERIFICATION_LOG_ATTR_TYPE_ID", attributeID);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    verificationLogAttribute = new VerificationLogAttribute();
//                    verificationLogAttribute.setId(rs.getLong("ID"));
//                    verificationLogAttribute.setVerificationLogID(rs.getLong("VERIFICATION_LOG_ID"));
//                    verificationLogAttribute.setValue(rs.getString("VALUE"));
//                    byte[] data = null;
//                    Blob blobData = rs.getBlob("BLOB");
//                    if (blobData != null) {
//                        data = Utils.saveByteArrayOutputStream(blobData.getBinaryStream());
//                        verificationLogAttribute.setBlobData(data);
//                    }
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting verification log attribute. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getVerificationLogAttribute in milliseconds: " + timeElapsed / 1000000);
//        }
//        return verificationLogAttribute;
//    }
//
//    @Override
//    public VerificationDatabaseResponse authenticateTsaUser(String username, String password) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        VerificationDatabaseResponse verificationDatabaseResponse = new VerificationDatabaseResponse();
//        int numOfRetry = retryTimes;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_TSA_USER_LOGIN(?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setString("pUSERNAME", username);
//                cals.setString("pPASSWORD", password);
//                cals.setInt("pMAX_COUNTER_FOR_PASSWORD", RSSPNoRPConstant.REMAINING_COUNTER);
//                cals.registerOutParameter("pREMAINING_COUNTER", java.sql.Types.INTEGER);
//                cals.registerOutParameter("pRESPONSE_CODE_NAME", java.sql.Types.VARCHAR);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                verificationDatabaseResponse.setStatus(Integer.parseInt(cals.getString("pRESPONSE_CODE_NAME")));
//                verificationDatabaseResponse.setRemainingCounter(cals.getInt("pREMAINING_COUNTER"));
//                rs = cals.getResultSet();
//                if (rs != null) {
//                    while (rs.next()) {
//                        verificationDatabaseResponse.setTsaUserID(rs.getLong("ID"));
//                        verificationDatabaseResponse.setTsaProfileID(rs.getInt("TSA_PROFILE_ID"));
//                        break;
//                    }
//                }
//
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while validating user tsa password. Details: " + Utils.printStackTrace(e));
//                }
//                verificationDatabaseResponse.setStatus(IdentityConstant.CODE_UNEXPE_EXP);
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of authenticateTsaUser in milliseconds: " + timeElapsed / 1000000);
//        }
//        return verificationDatabaseResponse;
//    }
//
//    @Override
//    public List<TSAProfile> getTSAProfiles() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<TSAProfile> tsaProfiles = new ArrayList<>();
//        try {
//            String str = "{ call SP_REST_TSA_PROFILE_LIST() }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    TSAProfile tsaProfile = new TSAProfile();
//                    tsaProfile.setId(rs.getInt("ID"));
//                    tsaProfile.setName(rs.getString("NAME"));
//
//                    String tsaProfileJson = rs.getString("TSA_PROFILE_PROPERTIES");
//                    if (!Utils.isNullOrEmpty(tsaProfileJson)) {
//                        TSAProfileProperties tsaProfileProperties = objectMapper.readValue(tsaProfileJson, TSAProfileProperties.class);
//                        tsaProfile.setTsaProfileProperties(tsaProfileProperties);
//                    }
//                    tsaProfiles.add(tsaProfile);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting TSAProfile. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getTSAProfiles in milliseconds: " + timeElapsed / 1000000);
//        }
//        return tsaProfiles;
//    }
//
//    @Override
//    public TSAUserAttribute getTSAUserAttribute(long tsaUserID, int tsaUserAttributeID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        TSAUserAttribute tsaUserAttribute = null;
//        try {
//            String str = "{ call SP_REST_TSA_USER_ATTR_LIST(?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setLong("pTSA_USER_ID", tsaUserID);
//            cals.setInt("pTSA_USER_ATTR_TYPE_ID", tsaUserAttributeID);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    tsaUserAttribute = new TSAUserAttribute();
//                    tsaUserAttribute.setTsaUserID(rs.getLong("TSA_USER_ID"));
//                    tsaUserAttribute.setTsaUserAttributeID(rs.getInt("TSA_USER_ATTR_TYPE_ID"));
//                    tsaUserAttribute.setValue(rs.getString("VALUE"));
//                    tsaUserAttribute.setModifiedDt(new Date(rs.getTimestamp("MODIFIED_DT").getTime()));
//                    Blob blobData = rs.getBlob("BLOB");
//                    byte[] data = null;
//                    if (blobData != null) {
//                        data = Utils.saveByteArrayOutputStream(blobData.getBinaryStream());
//                    }
//                    tsaUserAttribute.setBlob(data);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting TSAProfile. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getTSAUserAttribute in milliseconds: " + timeElapsed / 1000000);
//        }
//        return tsaUserAttribute;
//    }
//
//    @Override
//    public boolean updateTSAUserAttribute(long tsaUserID, int tsaUserAttributeID, String value, byte[] blob) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        boolean result = false;
//        try {
//            String str = "{ call SP_REST_TSA_USER_ATTR_UPDATE(?,?,?,?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setLong("pTSA_USER_ID", tsaUserID);
//            cals.setInt("pTSA_USER_ATTR_TYPE_ID", tsaUserAttributeID);
//            cals.setString("pVALUE", value);
//            cals.setBlob("pBLOB", blob != null ? new ByteArrayInputStream(blob) : null);
//            cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            result = true;
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while updating tsa user attribute. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateTSAUserAttribute in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public void decreaseTSAUsageCounter(final long tsaUserID, final int remainingUsageCounter) {
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                updateTSAUserAttribute(
//                        tsaUserID,
//                        TSAUserAttribute.REMAINING_USAGE_COUNTER,
//                        String.valueOf(remainingUsageCounter),
//                        null);
//            }
//        });
//        t.start();
//    }
//
//    @Override
//    public TSAUser getTSAUser(String username) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        TSAUser tsaUser = null;
//        try {
//            String str = "{ call SP_REST_TSA_USER_GET_INFO(?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setString("pUSERNAME", username);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    tsaUser = new TSAUser();
//                    tsaUser.setId(rs.getLong("ID"));
//                    tsaUser.setUsername(rs.getString("USERNAME"));
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting TSA User. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getTSAUser in milliseconds: " + timeElapsed / 1000000);
//        }
//        return tsaUser;
//    }
//
//    @Override
//    public DatabaseResponse getTSALogId() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        DatabaseResponse dr = null;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_SYS_TSA_LOG_INSERT(?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.registerOutParameter("pID", java.sql.Types.INTEGER);
//                cals.registerOutParameter("pCREATED_DT", java.sql.Types.TIMESTAMP);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                dr = new DatabaseResponse();
//                dr.setTsaLogId(cals.getLong("pID"));
//                dr.setTsaLogDt(new Date(cals.getTimestamp("pCREATED_DT").getTime()));
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while getting tsa log id. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getTSALogId in milliseconds: " + timeElapsed / 1000000);
//        }
//        return dr;
//    }
//
//    @Override
//    public boolean insertTSALog(
//            long id,
//            String httpCode,
//            String httpMessage,
//            String username,
//            long tsaUserID,
//            String userAgent,
//            int tsaProfileID,
//            String requestIp,
//            Date createdDt,
//            Date modifiedDt) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_TSA_LOG_INSERT(?,?,?,?,?,?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pID", id);
//                cals.setString("pTSA_RESPONSE_CODE", httpCode);
//                cals.setString("pTSA_RESPONSE_DESCRIPTION", httpMessage);
//                cals.setString("pUSERNAME", username);
//                cals.setLong("pTSA_USER_ID", tsaUserID);
//                cals.setString("pUSER_AGENT", userAgent);
//                cals.setInt("pTSA_PROFILE_ID", tsaProfileID);
//                cals.setString("pREQUEST_IP", requestIp);
//                cals.setTimestamp("pCREATED_DT", new Timestamp(createdDt.getTime()));
//                cals.setInt("pCREATED_BY", Configuration.getInstance().getAppUserDBID());
//                cals.setTimestamp("pMODIFIED_DT", new Timestamp(modifiedDt.getTime()));
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while inserting tsa log. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of insertTSALog in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
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
//
//    @Override
//    public boolean insertDocumentFaceFrame(
//            long subjectID,
//            long processID,
//            int identityDocumentTypeID,
//            byte[] blob,
//            int fileSize) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_DOCUMENT_FACEFRAME_INSERT(?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pIDENTITY_SUBJECT_ID", subjectID);
//                cals.setLong("pIDENTITY_PROCESS_ID", processID);
//                cals.setInt("pIDENTITY_DOCUMENT_TYPE_ID", identityDocumentTypeID);
//                cals.setBlob("pBLOB", blob != null ? new ByteArrayInputStream(blob) : null);
//                cals.setInt("pFILE_SIZE", fileSize);
//
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while inserting face frame. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of insertDocumentFaceFrame in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public List<IdentityDocumentFaceFrame> getIdentityDocumentFaceFrame(long subjectID, long processID, int identityDocumentTypeID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<IdentityDocumentFaceFrame> IdentityDocumentFaceFrames = new ArrayList<>();
//        try {
//            String str = "{ call SP_REST_IDENTITY_DOCUMENT_FACEFRAME_GET(?,?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setLong("pIDENTITY_SUBJECT_ID", subjectID);
//            cals.setLong("pIDENTITY_PROCESS_ID", processID);
//            cals.setInt("pIDENTITY_DOCUMENT_TYPE_ID", identityDocumentTypeID);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    IdentityDocumentFaceFrame identityDocumentFaceFrame = new IdentityDocumentFaceFrame();
//                    identityDocumentFaceFrame.setSubjectID(rs.getLong("IDENTITY_SUBJECT_ID"));
//                    identityDocumentFaceFrame.setProcessID(rs.getLong("IDENTITY_PROCESS_ID"));
//                    identityDocumentFaceFrame.setIdentityDocumentTypeID(rs.getInt("IDENTITY_DOCUMENT_TYPE_ID"));
//                    Blob blobData = rs.getBlob("BLOB");
//                    byte[] data = null;
//                    if (blobData != null) {
//                        data = Utils.saveByteArrayOutputStream(blobData.getBinaryStream());
//                    }
//                    identityDocumentFaceFrame.setData(data);
//                    IdentityDocumentFaceFrames.add(identityDocumentFaceFrame);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting face frame. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentityDocumentFaceFrame in milliseconds: " + timeElapsed / 1000000);
//        }
//        return IdentityDocumentFaceFrames;
//    }
//
//    @Override
//    public void deleteIdentityDocumentFaceFrame(long processID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_DOCUMENT_FACEFRAME_DELETE(?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pIDENTITY_PROCESS_ID", processID);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while deleting Identity Document FaceFrame. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of deleteIdentityDocumentFaceFrame in milliseconds: " + timeElapsed / 1000000);
//        }
//    }
//
//    @Override
//    public boolean updateIdentitySubjectAttr(long subjectID, int attributeID, String value, byte[] binary) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_SUBJECT_ATTR_UPDATE(?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pIDENTITY_SUBJECT_ID", subjectID);
//                cals.setInt("pIDENTITY_SUBJECT_ATTR_TYPE_ID", attributeID);
//                cals.setString("pVALUE", value);
//                InputStream in = null;
//                if (binary != null) {
//                    in = new ByteArrayInputStream(binary);
//                    cals.setBlob("pBLOB", in);
//                    in.close();
//                } else {
//                    cals.setBlob("pBLOB", in);
//                }
//                cals.setBoolean("pENABLED", Boolean.TRUE);
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while updating Identity Subject Attr attributes. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateIdentitySubjectAttr in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public IdentitySubjectAttribute getIdentitySubjectAttr(long subjectID, int attributeID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        IdentitySubjectAttribute identitySubjectAttribute = null;
//        try {
//            String str = "{ call SP_REST_IDENTITY_SUBJECT_ATTR_GET(?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setLong("pIDENTITY_SUBJECT_ID", subjectID);
//            cals.setInt("pIDENTITY_SUBJECT_ATTR_TYPE_ID", attributeID);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    identitySubjectAttribute = new IdentitySubjectAttribute();
//                    identitySubjectAttribute.setSubjectID(rs.getLong("IDENTITY_SUBJECT_ID"));
//                    identitySubjectAttribute.setAttributeID(rs.getInt("IDENTITY_SUBJECT_ATTR_TYPE_ID"));
//                    identitySubjectAttribute.setValue(rs.getString("VALUE"));
//                    Blob blobData = rs.getBlob("BLOB");
//                    byte[] data = null;
//                    if (blobData != null) {
//                        data = Utils.saveByteArrayOutputStream(blobData.getBinaryStream());
//                    }
//                    identitySubjectAttribute.setBlob(data);
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting subject attribute. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentitySubjectAttr in milliseconds: " + timeElapsed / 1000000);
//        }
//        return identitySubjectAttribute;
//    }
//
//    @Override
//    public IdentityDocument getIdentityDocumentByID(long identityDocumentID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        IdentityDocument identityDocument = null;
//        try {
//            String str = "{ call SP_REST_IDENTITY_DOCUMENT_DETAIL(?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setLong("pIDENTITY_DOCUMENT_ID", identityDocumentID);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    identityDocument = new IdentityDocument();
//                    identityDocument.setId(rs.getLong("ID"));
//                    identityDocument.setDocumentTypeId(rs.getInt("IDENTITY_DOCUMENT_TYPE_ID"));
//                    identityDocument.setProviderId(rs.getInt("IDENTITY_PROVIDER_ID"));
//                    identityDocument.setSubjectId(rs.getLong("IDENTITY_SUBJECT_ID"));
//                    identityDocument.setImageTypeId(rs.getInt("IDENTITY_IMAGE_TYPE_ID"));
//                    identityDocument.setUuid(rs.getString("UUID"));
//                    String dmsProJson = rs.getString("DMS_PROPERTIES");
//                    if (!Utils.isNullOrEmpty(dmsProJson)) {
//                        DMSProperties dmsProperties = objectMapper.readValue(dmsProJson, DMSProperties.class);
//                        identityDocument.setDmsProperties(dmsProperties);
//                    }
//                    identityDocument.setDigest(rs.getString("DIGEST"));
//                    identityDocument.setFileName(rs.getString("FILE_NAME"));
//                    identityDocument.setFileSize(rs.getLong("FILE_SIZE"));
//                    identityDocument.setProcessId(rs.getLong("IDENTITY_PROCESS_ID"));
//                    identityDocument.setCreatedDt(new Date(rs.getTimestamp("CREATED_DT").getTime()));
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting identity document. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentityDocument in milliseconds: " + timeElapsed / 1000000);
//        }
//        return identityDocument;
//    }
//
//    @Override
//    public Identity getIdentityByDocValue(int identityDocumentTypeID, String identityDocumentValue) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        Identity identity = null;
//        List<Identity> identities = new ArrayList<>();
//        try {
//            String str = "{ call SP_REST_IDENTITY_GET_BY_DOCUMENT_VALUE(?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setInt("pIDENTITY_DOCUMENT_TYPE_ID", identityDocumentTypeID);
//            cals.setString("pIDENTITY_DOCUMENT_VALUE", identityDocumentValue);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    identity = new Identity();
//                    identity.setId(rs.getLong("ID"));
//                    identity.setUuid(rs.getString("IDENTITY_UUID"));
//                    identity.setAsuranceLevelId(rs.getInt("IDENTITY_ASSURANCE_LEVEL_ID"));
//                    identity.setProcessId(rs.getLong("BEST_IDENTITY_PROCESS_ID"));
//                    identity.setName(rs.getString("FULL_NAME"));
//                    identity.setFirstName(rs.getString("FIRST_NAME"));
//                    identity.setLastName(rs.getString("LAST_NAME"));
//                    identity.setGender(rs.getString("GENDER"));
//                    identity.setEmail(rs.getString("EMAIL"));
//                    identity.setEmailVerified(rs.getBoolean("EMAIL_VERIFIED_ENABLED"));
//                    identity.setMobile(rs.getString("MOBILE"));
//                    identity.setMobileVerified(rs.getBoolean("MOBILE_VERIFIED_ENABLED"));
//                    identity.setDob(rs.getDate("DOB"));
//                    identity.setDoe(rs.getDate("DOE"));
//                    identity.setDoi(rs.getDate("DOI"));
//                    identity.setPob(rs.getString("POB"));
//                    identity.setNationality(rs.getString("NATIONALITY"));
//                    identity.setIdentityDocumentTypeId(rs.getInt("IDENTITY_DOCUMENT_TYPE_ID"));
//                    identity.setIdentityDocumentValue(rs.getString("IDENTITY_DOCUMENT_VALUE"));
//                    identity.setAddress(rs.getString("ADDRESS"));
//                    identity.setIdentityType(rs.getInt("IDENTITY_TYPE_ID"));
//                    identity.setCreatedDt(new Date(rs.getTimestamp("CREATED_DT").getTime()));
//                    identity.setModifiedDt(new Date(rs.getTimestamp("MODIFIED_DT").getTime()));
//                    identities.add(identity);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting Identity. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentityByDocValue in milliseconds: " + timeElapsed / 1000000);
//        }
//        if (identities.isEmpty()) {
//            return null;
//        } else if (identities.size() == 1) {
//            return identity;
//        } else {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("There are two Identity found in system: TypeID="
//                        + identityDocumentTypeID + " Value=" + identityDocumentValue + ". Duplication is not allowed.");
//            }
//            return identities.get(0);
//        }
//    }
//
//    @Override
//    public boolean insertIdentity(
//            long id,
//            String identityUUID,
//            int assuranceLevel,
//            long bestProcessID,
//            String fullName,
//            String firstName,
//            String lastName,
//            String gender,
//            String email,
//            boolean emailVerified,
//            String mobile,
//            boolean mobileVerified,
//            Date dob,
//            Date doe,
//            Date doi,
//            String pob,
//            String nationality,
//            IdentityDocumentType identityDocumentType,
//            String identityDocumentValue,
//            String identityDocumentExValue,
//            String address,
//            int identityTypeID,
//            Date createdDt,
//            Date modifiedDt) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_INSERT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pID", id);
//                cals.setString("pIDENTITY_UUID", identityUUID);
//                cals.setInt("pIDENTITY_ASSURANCE_LEVEL_ID", assuranceLevel);
//                cals.setLong("pBEST_IDENTITY_PROCESS_ID", bestProcessID);
//                cals.setString("pFULL_NAME", fullName);
//                cals.setString("pFIRST_NAME", firstName);
//                cals.setString("pLAST_NAME", lastName);
//                cals.setString("pGENDER", gender);
//                cals.setString("pEMAIL", email);
//                cals.setBoolean("pEMAIL_VERIFIED_ENABLED", emailVerified);
//                cals.setString("pMOBILE", mobile);
//                cals.setBoolean("pMOBILE_VERIFIED_ENABLED", mobileVerified);
//                cals.setDate("pDOB", dob != null ? new java.sql.Date(dob.getTime()) : null);
//                cals.setDate("pDOE", doe != null ? new java.sql.Date(doe.getTime()) : null);
//                cals.setDate("pDOI", doi != null ? new java.sql.Date(doi.getTime()) : null);
//                cals.setString("pPOB", pob);
//                cals.setString("pNATIONALITY", nationality);
//                if (identityDocumentType != null) {
//                    cals.setInt("pIDENTITY_DOCUMENT_TYPE_ID", identityDocumentType.getId());
//                    cals.setString("pIDENTITY_DOCUMENT_VALUE", identityDocumentValue);
//                } else {
//                    cals.setObject("pIDENTITY_DOCUMENT_TYPE_ID", null);
//                    cals.setObject("pIDENTITY_DOCUMENT_VALUE", null);
//                }
//                cals.setString("pEX_IDENTITY_DOCUMENT_VALUE", identityDocumentExValue);
//                cals.setString("pADDRESS", address);
//                cals.setInt("pIDENTITY_TYPE_ID", identityTypeID);
//                cals.setTimestamp("pCREATED_DT", new Timestamp(createdDt.getTime()));
//                cals.setInt("pCREATED_BY", Configuration.getInstance().getAppUserDBID());
//                cals.setTimestamp("pMODIFIED_DT", new Timestamp(modifiedDt.getTime()));
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while inserting identity. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of insertIdentity in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public DatabaseResponse getIdentityId() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        DatabaseResponse dr = null;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_SYS_IDENTITY_INSERT(?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.registerOutParameter("pID", java.sql.Types.INTEGER);
//                cals.registerOutParameter("pCREATED_DT", java.sql.Types.TIMESTAMP);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                dr = new DatabaseResponse();
//                dr.setIdentityID(cals.getLong("pID"));
//                dr.setIdentityDT(new Date(cals.getTimestamp("pCREATED_DT").getTime()));
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while getting identity id. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentityId in milliseconds: " + timeElapsed / 1000000);
//        }
//        return dr;
//    }
//
//    @Override
//    public boolean updateIdentityAttr(long identityID, int attributeID, String value, byte[] binary) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_ATTR_UPDATE(?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pIDENTITY_ID", identityID);
//                cals.setInt("pIDENTITY_ATTR_TYPE_ID", attributeID);
//                cals.setString("pVALUE", value);
//                InputStream in = null;
//                if (binary != null) {
//                    in = new ByteArrayInputStream(binary);
//                    cals.setBlob("pBLOB", in);
//                    in.close();
//                } else {
//                    cals.setBlob("pBLOB", in);
//                }
//                cals.setBoolean("pENABLED", Boolean.TRUE);
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while updating Identity Attr attributes. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateIdentityAttr in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public boolean updateIdentity(
//            long id,
//            long bestProcessID,
//            String fullName,
//            String firstName,
//            String lastName,
//            String gender,
//            String email,
//            Boolean emailVerified,
//            String mobile,
//            Boolean mobileVerified,
//            Date dob,
//            Date doe,
//            Date doi,
//            String pob,
//            String nationality,
//            IdentityDocumentType identityDocumentType,
//            String identityDocumentValue,
//            String identityDocumentExValue,
//            String address) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_UPDATE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setLong("pID", id);
//                cals.setString("pFULL_NAME", fullName);
//                cals.setString("pFIRST_NAME", firstName);
//                cals.setString("pLAST_NAME", lastName);
//                cals.setString("pGENDER", gender);
//                cals.setString("pEMAIL", email);
//                if (emailVerified != null) {
//                    cals.setBoolean("pEMAIL_VERIFIED_ENABLED", emailVerified);
//                } else {
//                    cals.setObject("pEMAIL_VERIFIED_ENABLED", null);
//                }
//                cals.setString("pMOBILE", mobile);
//                if (mobileVerified != null) {
//                    cals.setBoolean("pMOBILE_VERIFIED_ENABLED", mobileVerified);
//                } else {
//                    cals.setObject("pMOBILE_VERIFIED_ENABLED", null);
//                }
//                cals.setDate("pDOB", dob != null ? new java.sql.Date(dob.getTime()) : null);
//                cals.setDate("pDOE", doe != null ? new java.sql.Date(doe.getTime()) : null);
//                cals.setDate("pDOI", doi != null ? new java.sql.Date(doi.getTime()) : null);
//                cals.setString("pPOB", pob);
//                cals.setString("pNATIONALITY", nationality);
//                if (identityDocumentType != null) {
//                    cals.setInt("pIDENTITY_DOCUMENT_TYPE_ID", identityDocumentType.getId());
//                    cals.setString("pIDENTITY_DOCUMENT_VALUE", identityDocumentValue);
//                } else {
//                    cals.setObject("pIDENTITY_DOCUMENT_TYPE_ID", null);
//                    cals.setObject("pIDENTITY_DOCUMENT_VALUE", null);
//                }
//                cals.setString("pEX_IDENTITY_DOCUMENT_VALUE", identityDocumentExValue);
//                cals.setString("pADDRESS", address);
//                cals.setLong("pBEST_IDENTITY_PROCESS_ID", bestProcessID);
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while updating identity. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateIdentity in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public Identity getIdentity(String identityUUID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        Identity identity = null;
//        try {
//            String str = "{ call SP_REST_IDENTITY_GET_BY_UUID(?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setString("pIDENTITY_UUID", identityUUID);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    identity = new Identity();
//                    identity.setId(rs.getLong("ID"));
//                    identity.setUuid(rs.getString("IDENTITY_UUID"));
//                    identity.setAsuranceLevelId(rs.getInt("IDENTITY_ASSURANCE_LEVEL_ID"));
//                    identity.setProcessId(rs.getLong("BEST_IDENTITY_PROCESS_ID"));
//                    identity.setName(rs.getString("FULL_NAME"));
//                    identity.setFirstName(rs.getString("FIRST_NAME"));
//                    identity.setLastName(rs.getString("LAST_NAME"));
//                    identity.setGender(rs.getString("GENDER"));
//                    identity.setEmail(rs.getString("EMAIL"));
//                    identity.setEmailVerified(rs.getBoolean("EMAIL_VERIFIED_ENABLED"));
//                    identity.setMobile(rs.getString("MOBILE"));
//                    identity.setMobileVerified(rs.getBoolean("MOBILE_VERIFIED_ENABLED"));
//                    identity.setDob(rs.getDate("DOB"));
//                    identity.setDoe(rs.getDate("DOE"));
//                    identity.setDoi(rs.getDate("DOI"));
//                    identity.setPob(rs.getString("POB"));
//                    identity.setNationality(rs.getString("NATIONALITY"));
//                    identity.setIdentityDocumentTypeId(rs.getInt("IDENTITY_DOCUMENT_TYPE_ID"));
//                    identity.setIdentityDocumentValue(rs.getString("IDENTITY_DOCUMENT_VALUE"));
//                    identity.setAddress(rs.getString("ADDRESS"));
//                    identity.setIdentityType(rs.getInt("IDENTITY_TYPE_ID"));
//                    identity.setCreatedDt(new Date(rs.getTimestamp("CREATED_DT").getTime()));
//                    identity.setModifiedDt(new Date(rs.getTimestamp("MODIFIED_DT").getTime()));
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting Identity. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentity in milliseconds: " + timeElapsed / 1000000);
//        }
//        return identity;
//    }
//
//    @Override
//    public IdentityAttribute getIdentityAttr(long identityID, int attributeID) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        IdentityAttribute identityAttribute = null;
//        try {
//            String str = "{ call SP_REST_IDENTITY_ATTR_GET(?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setLong("pIDENTITY_ID", identityID);
//            cals.setInt("pIDENTITY_ATTR_TYPE_ID", attributeID);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    identityAttribute = new IdentityAttribute();
//                    identityAttribute.setIdentityID(rs.getLong("IDENTITY_ID"));
//                    identityAttribute.setAttributeID(rs.getInt("IDENTITY_ATTR_TYPE_ID"));
//                    identityAttribute.setValue(rs.getString("VALUE"));
//                    Blob blobData = rs.getBlob("BLOB");
//                    byte[] data = null;
//                    if (blobData != null) {
//                        data = Utils.saveByteArrayOutputStream(blobData.getBinaryStream());
//                    }
//                    identityAttribute.setBlob(data);
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting identity attribute. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIdentityAttr in milliseconds: " + timeElapsed / 1000000);
//        }
//        return identityAttribute;
//    }
//
//    @Override
//    public GeneralPolicyAttribute getGeneralPolicy(int entityId, int attributeId) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        GeneralPolicyAttribute generalPolicyAttribute = null;
//        try {
//            String str = "{ call SP_REST_GENERAL_POLICY_ATTR_GET(?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setInt("pENTITY_ID", entityId);
//            cals.setInt("pGENERAL_POLICY_ATTR_TYPE_ID", attributeId);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    generalPolicyAttribute = new GeneralPolicyAttribute();
//                    generalPolicyAttribute.setEntityId(rs.getInt("ENTITY_ID"));
//                    generalPolicyAttribute.setAttributeId(rs.getInt("GENERAL_POLICY_ATTR_TYPE_ID"));
//                    generalPolicyAttribute.setValue(rs.getString("VALUE"));
//                    Blob blobData = rs.getBlob("BLOB");
//                    byte[] data = null;
//                    if (blobData != null) {
//                        data = Utils.saveByteArrayOutputStream(blobData.getBinaryStream());
//                    }
//                    generalPolicyAttribute.setBlob(data);
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting getGeneralPolicy. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getGeneralPolicy in milliseconds: " + timeElapsed / 1000000);
//        }
//        return generalPolicyAttribute;
//    }
//
//    @Override
//    public MasterListData getMasterListData(String masterListName) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        MasterListData masterListData = null;
//        try {
//            String str = "{ call SP_REST_MASTER_LIST_BY_NAME(?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setString("pMASTER_LIST_NAME", masterListName);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    masterListData = new MasterListData();
//                    masterListData.setName(rs.getString("NAME"));
//
//                    Blob blobData = rs.getBlob("MASTER_LIST_DATA");
//                    byte[] data = null;
//                    if (blobData != null) {
//                        data = Utils.saveByteArrayOutputStream(blobData.getBinaryStream());
//                    }
//                    masterListData.setData(data);
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting Master List Data. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getMasterListData in milliseconds: " + timeElapsed / 1000000);
//        }
//        return masterListData;
//    }
//
//    @Override
//    public boolean insertIdentityCard(String idNO,
//            String name,
//            byte[] dg1,
//            byte[] dg2,
//            byte[] dg3,
//            byte[] dg13,
//            byte[] dg14,
//            byte[] dg15,
//            byte[] faceIso197945,
//            byte[] leftIndexFinger,
//            byte[] rightIndexFinger,
//            byte[] efCardAccess,
//            byte[] efcom,
//            byte[] efsod) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String str = "{ call SP_REST_IDENTITY_CARD_ADD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setString("pID_CARD_NUMBER", idNO);
//                cals.setString("pFULLNAME", name);
//                cals.setBlob("pDG1", dg1 != null ? new ByteArrayInputStream(dg1) : null);
//                cals.setBlob("pDG2", dg2 != null ? new ByteArrayInputStream(dg2) : null);
//                cals.setBlob("pDG3", dg3 != null ? new ByteArrayInputStream(dg3) : null);
//                cals.setBlob("pDG13", dg13 != null ? new ByteArrayInputStream(dg13) : null);
//                cals.setBlob("pDG14", dg14 != null ? new ByteArrayInputStream(dg14) : null);
//                cals.setBlob("pEFSOD", efsod != null ? new ByteArrayInputStream(efsod) : null);
//                cals.setBlob("pEF_CARDACCESS", efCardAccess != null ? new ByteArrayInputStream(efCardAccess) : null);
//                cals.setBlob("pEF_COM", efcom != null ? new ByteArrayInputStream(efcom) : null);
//                cals.setBlob("pDG15", dg15 != null ? new ByteArrayInputStream(dg15) : null);
//                cals.setBlob("pFACE_ISO_19794_5", faceIso197945 != null ? new ByteArrayInputStream(faceIso197945) : null);
//                cals.setBlob("pLEFT_INDEX_FINGER", leftIndexFinger != null ? new ByteArrayInputStream(leftIndexFinger) : null);
//                cals.setBlob("pRIGTH_INDEX_FINGER", rightIndexFinger != null ? new ByteArrayInputStream(rightIndexFinger) : null);
//                cals.setInt("pCREATED_BY", Configuration.getInstance().getAppUserDBID());
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while inserting identity card. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of insertIdentityCard in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public boolean insertLicenceManager(int type, int relyingPartyID, String functionName, long counter) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String hmac = DatatypeConverter.printHexBinary(
//                        Crypto.calcHmacSha256(LicenseGenerator.HMAC_KEY,
//                                (String.valueOf(type)
//                                        + String.valueOf(relyingPartyID)
//                                        + functionName
//                                        + String.valueOf(counter)).getBytes()));
//                String str = "{ call SP_REST_LICENSE_MANAGER_INSERT(?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setInt("pLICENSE_TYPE_ID", type);
//                cals.setInt("pRELYING_PARTY_ID", relyingPartyID);
//                cals.setString("pFUNCTION_NAME", functionName);
//                cals.setLong("pCOUNTER", counter);
//                cals.setString("pHMAC", hmac);
//                cals.setInt("pCREATED_BY", Configuration.getInstance().getAppUserDBID());
//                cals.registerOutParameter("pRESPONSE_CODE_NAME", java.sql.Types.VARCHAR);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while inserting license manager. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of insertLicenceManager in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public LicenseManager getLicenseManager(int type, int relyingPartyID, String functionName) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        LicenseManager licenseManager = null;
//        try {
//            String str = "{ call SP_REST_LICENSE_MANAGER_GET(?,?,?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setInt("pLICENSE_TYPE_ID", type);
//            cals.setInt("pRELYING_PARTY_ID", relyingPartyID);
//            cals.setString("pFUNCTION_NAME", functionName);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    licenseManager = new LicenseManager();
//                    licenseManager.setCounter(rs.getLong("COUNTER"));
//                    licenseManager.setHmac(rs.getString("HMAC"));
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting License Manager. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getLicenseManager in milliseconds: " + timeElapsed / 1000000);
//        }
//        return licenseManager;
//    }
//
//    @Override
//    public boolean updateLicenceManager(int type, int relyingPartyID, String functionName, long counter) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        int numOfRetry = retryTimes;
//        boolean result = false;
//        while (numOfRetry > 0) {
//            try {
//                String hmac = DatatypeConverter.printHexBinary(Crypto.calcHmacSha256(LicenseGenerator.HMAC_KEY, (String.valueOf(type)
//                        + String.valueOf(relyingPartyID) + functionName + String.valueOf(counter)).getBytes()));
//                String str = "{ call SP_REST_LICENSE_MANAGER_UPDATE(?,?,?,?,?,?,?) }";
//                conn = DatabaseConnectionManager.getInstance().openWriteOnlyConnection();
//                cals = conn.prepareCall(str);
//                cals.setInt("pLICENSE_TYPE_ID", type);
//                cals.setInt("pRELYING_PARTY_ID", relyingPartyID);
//                cals.setString("pFUNCTION_NAME", functionName);
//                cals.setLong("pCOUNTER", counter);
//                cals.setString("pHMAC", hmac);
//                cals.setInt("pMODIFIED_BY", Configuration.getInstance().getAppUserDBID());
//                cals.registerOutParameter("pRESPONSE_CODE_NAME", java.sql.Types.VARCHAR);
//                if (LogHandler.isShowDebugLog()) {
//                    LOG.debug("[SQL] " + cals.toString());
//                }
//                cals.execute();
//                result = true;
//                break;
//            } catch (Exception e) {
//                numOfRetry--;
//                if (LogHandler.isShowErrorLog()) {
//                    LOG.error("Error while updating license manager. Details: " + Utils.printStackTrace(e));
//                }
//                e.printStackTrace();
//            } finally {
//                DatabaseConnectionManager.getInstance().close(conn);
//            }
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of updateLicenceManager in milliseconds: " + timeElapsed / 1000000);
//        }
//        return result;
//    }
//
//    @Override
//    public IDCard getIDCard(String cardNo) {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        IDCard idCard = null;
//        try {
//            String str = "{ call SP_REST_IDENTITY_CARD_GET_BY_ID_CARD_NUMBER(?) }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//            cals.setString("pID_CARD_NUMBER", cardNo);
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    idCard = new IDCard();
//                    idCard.setName(rs.getString("FULLNAME"));
//
//                    if (rs.getBlob("DG1") != null) {
//                        idCard.setDg1(Utils.saveByteArrayOutputStream(rs.getBlob("DG1").getBinaryStream()));
//                    }
//                    if (rs.getBlob("DG2") != null) {
//                        idCard.setDg2(Utils.saveByteArrayOutputStream(rs.getBlob("DG2").getBinaryStream()));
//                    }
//                    if (rs.getBlob("DG3") != null) {
//                        idCard.setDg3(Utils.saveByteArrayOutputStream(rs.getBlob("DG3").getBinaryStream()));
//                    }
//                    if (rs.getBlob("DG13") != null) {
//                        idCard.setDg13(Utils.saveByteArrayOutputStream(rs.getBlob("DG13").getBinaryStream()));
//                    }
//                    if (rs.getBlob("DG14") != null) {
//                        idCard.setDg14(Utils.saveByteArrayOutputStream(rs.getBlob("DG14").getBinaryStream()));
//                    }
//                    if (rs.getBlob("DG15") != null) {
//                        idCard.setDg15(Utils.saveByteArrayOutputStream(rs.getBlob("DG15").getBinaryStream()));
//                    }
//                    if (rs.getBlob("EFSOD") != null) {
//                        idCard.setEfSOD(Utils.saveByteArrayOutputStream(rs.getBlob("EFSOD").getBinaryStream()));
//                    }
//                    if (rs.getBlob("EF_CARDACCESS") != null) {
//                        idCard.setEfCardAccess(Utils.saveByteArrayOutputStream(rs.getBlob("EF_CARDACCESS").getBinaryStream()));
//                    }
//                    if (rs.getBlob("EF_COM") != null) {
//                        idCard.setEfCom(Utils.saveByteArrayOutputStream(rs.getBlob("EF_COM").getBinaryStream()));
//                    }
//                    if (rs.getBlob("FACE_ISO_19794_5") != null) {
//                        idCard.setFaceIso197945(Utils.saveByteArrayOutputStream(rs.getBlob("FACE_ISO_19794_5").getBinaryStream()));
//                    }
//                    if (rs.getBlob("LEFT_INDEX_FINGER") != null) {
//                        idCard.setLeftIndexFinger(Utils.saveByteArrayOutputStream(rs.getBlob("LEFT_INDEX_FINGER").getBinaryStream()));
//                    }
//                    if (rs.getBlob("RIGTH_INDEX_FINGER") != null) {
//                        idCard.setRightIndexFinger(Utils.saveByteArrayOutputStream(rs.getBlob("RIGTH_INDEX_FINGER").getBinaryStream()));
//                    }
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting ID Card. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of getIDCard in milliseconds: " + timeElapsed / 1000000);
//        }
//        return idCard;
//    }
//
//    @Override
//    public List<LicenseManager> getLicenseManager() {
//        long startTime = System.nanoTime();
//        Connection conn = null;
//        ResultSet rs = null;
//        CallableStatement cals = null;
//        List<LicenseManager> licenseManagers = new ArrayList<>();
//        try {
//            String str = "{ call SP_REST_LICENSE_MANAGER_GET_ALL() }";
//            conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
//            cals = conn.prepareCall(str);
//
//            if (LogHandler.isShowDebugLog()) {
//                LOG.debug("[SQL] " + cals.toString());
//            }
//            cals.execute();
//            rs = cals.getResultSet();
//            if (rs != null) {
//                while (rs.next()) {
//                    LicenseManager licenseManager = new LicenseManager();
//                    licenseManager.setCounter(rs.getLong("COUNTER"));
//                    licenseManager.setHmac(rs.getString("HMAC"));
//                    licenseManager.setType(rs.getInt("LICENSE_TYPE_ID"));
//                    licenseManager.setRelyingPartyID(rs.getInt("RELYING_PARTY_ID"));
//                    licenseManager.setFunctionName(rs.getString("FUNCTION_NAME"));
//                    licenseManagers.add(licenseManager);
//                }
//            }
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("Error while getting License Managers. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
//        }
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        if (LogHandler.isShowDebugLog()) {
//            LOG.debug("Execution time of List<LicenseManager> getLicenseManager in milliseconds: " + timeElapsed / 1000000);
//        }
//        return licenseManagers;
//    }  
}
