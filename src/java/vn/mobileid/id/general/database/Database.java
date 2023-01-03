/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.database;

import java.util.Date;
import java.util.List;
import vn.mobileid.id.general.objects.IDXCertificate;
import vn.mobileid.id.general.objects.IdentityProcessType;
import vn.mobileid.id.general.gateway.p2p.objects.P2P;
import vn.mobileid.id.general.gateway.p2p.objects.P2PEntityAttribute;
import vn.mobileid.id.general.gateway.p2p.objects.P2PFunction;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.Entity;
import vn.mobileid.id.general.objects.IdentityDocument;
import vn.mobileid.id.general.objects.IdentityDocumentType;
import vn.mobileid.id.general.objects.IdentityProcess;
import vn.mobileid.id.general.objects.IdentityProcessStatus;
import vn.mobileid.id.general.objects.IdentitySubject;
import vn.mobileid.id.general.objects.IdentityProcessAttribute;
import vn.mobileid.id.general.objects.IdentityProvider;
import vn.mobileid.id.general.objects.Province;
import vn.mobileid.id.general.objects.RegistrationParty;
import vn.mobileid.id.general.objects.RelyingParty;
import vn.mobileid.id.general.objects.RelyingPartyAttribute;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.everification.objects.CertificationAuthority;
import vn.mobileid.id.general.objects.TSAProfile;

/**
 *
 * @author ADMIN
 */
public interface Database {

//    public List<IDXCertificate> getIDXCertificates();
//
//    public List<RelyingParty> getRelyingParties();

    public ResponseCode getResponse(String name);

//    public boolean updateRelyingPartyAttribute(int relyingPartyId, int attributeId, String value, byte[] binary);
//
//    public RelyingPartyAttribute getRelyingPartyAttribute(int relyingPartyId, int attributeId);
//
//    public List<Integer> getIdentityFunctionIDGrantForRP(int relyingPartyId);
//
//    public DatabaseResponse getIdentityLogId();
//
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
//            Date modifiedDt);
//
//    public boolean updateIdentityLogAttribute(long identityLogId, int attributeId, String value, byte[] binary);
//
//    public DatabaseResponse getIdentitySubjectId();
//
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
//            Date modifiedDt);
//
//    public List<IdentityDocumentType> getIdentityDocumentTypes();
//
//    public IdentitySubject getIdentitySubject(String uuid);
//
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
//            String address);
//
//    public List<IdentityProcessType> getProcessTypes();

    public List<Entity> getEntities();
 
//    public List<P2P> getP2Ps();
//
//    public List<P2PEntityAttribute> getP2PEntityAttributes(int p2pID);
//
//    public List<P2PFunction> getP2PFunctions();
//
//    public DatabaseResponse getP2PLogID();
//
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
//            int modifiedBy);
//
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
//            Date modifiedDt);
//
//    public DatabaseResponse getIdentityProcessId();
//
//    public boolean updateIdentityProcessAttribute(
//            long processId,
//            int attributeId,
//            String value,
//            byte[] binary);
//
//    public IdentityProcess getIdentityProcess(String uuid);
//
//    public IdentityProcessAttribute getIdentityProcessAttribute(long processId, int attributeId);
//
//    public DatabaseResponse authorizeOtp(
//            long processId,
//            String otp,
//            int maxRemainingCounter,
//            int timeoutDuration);
//
//    public boolean updateSubjectAssuranceLevel(long subjectId, int assuranceLevelId);
//
//    public boolean updateLevelBasicResult(long subjectId, Boolean emailVerified, Boolean mobileVerified);
//
//    public boolean updateIdentityProcessStatus(long processId, int statusId);
//
//    public List<IdentityProcessStatus> getIdentityProcessStatus();
//
//    public boolean updateBestIdentityProcess(long subjectId, long processId);
//
//    public DatabaseResponse getIdentityDocumentId();
//
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
//            Date modifiedDt);
//
//    public IdentityDocument getIdentityDocument(
//            long subjectId,
//            long processId,
//            int imageTypeId);
//
//    public List<IdentityProvider> getIdentityProviders();
//
//    public List<IdentityProcess> getIdentityProcesses(long subjectId, Integer processTypeId, Integer statusId);

    public List<ResponseCode> getResponseCodes();

//    public boolean insertIdXCertificate(
//            String name,
//            String uri,
//            Date validFrom,
//            Date validTo,
//            String certificate,
//            byte[] encryptedPrivateKey,
//            String properties,
//            String remarkEn,
//            String remark);
//
//    public Province getProvince();
//
//    public DatabaseResponse getVerificationLogId();
//
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
//            Date modifiedDt);
//
//    public List<CertificationAuthority> getCertificationAuthorities();
//
////    public CrlData getCrlData(int certificationAuthorityID);
//
//    public void updateCrlData(
//            int certificationAuthorityID,
//            byte[] crlData,
//            Date lastUpdateDt,
//            Date nextUpdateDt,
//            String issuerSubject,
//            String authorityKeyID,
//            int updateBy);
//
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
//            String twoFactorAuthMethod);
//
////    public Owner getOwnerByID(long id);
//
////    public Owner getOwnerByUUID(String ownerUUID);
//
//    public DatabaseResponse updateOwner(
//            long id,
//            String personalName,
//            String organization,
//            String enterpriseID,
//            String personalID,
//            String phone,
//            String email);

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
//            int duration);
//
//    public List<RegistrationParty> getRegistrationParties();
//
//    public List<CloudCertificate> getCertificateByOwnerID(long cloudOwnerCertificateID);
//
//    public Owner getOwnerByUsername(String username);
//
//    public DatabaseResponse insertAgreement(int relyingPartyID, String agreementUUID, long ownerID);
//
//    public Agreement getAgreement(String agreementUUID);
//
//    public boolean assignAgreement(String agreementUUID, long ownerID);
//
//    public CloudCertificate getCertificateByUUID(String certificateUUID);
//
//    public boolean updateCertificateState(long certificateID, int stateID);
//
//    public boolean updateOwnerAttribute(long ownerID, int ownerAttributeTypeID, String value, byte[] blobData);
//
//    public OwnerAttribute getOwnerAttribute(long ownerID, int ownerAttributeTypeID);
//
//    public VerificationLog getVerificationLog(long id);
//
//    public VerificationDatabaseResponse authorizeOtpForEVerification(
//            long ownerID,
//            String otp,
//            int maxRemainingCounter,
//            int timeoutDuration);
//
//    public VerificationLogAttribute getVerificationLogAttribute(long verificationLogID, int attributeID);
//
////    public VerificationDatabaseResponse ownerLogin(
////            String username,
////            String password);
//    public VerificationDatabaseResponse authenticateTsaUser(String username, String password);
//
//    public List<TSAProfile> getTSAProfiles();
//
//    public TSAUserAttribute getTSAUserAttribute(long tsaUserID, int tsaUserAttributeID);
//
//    public boolean updateTSAUserAttribute(long tsaUserID, int tsaUserAttributeID, String value, byte[] blob);
//
//    public void decreaseTSAUsageCounter(long tsaUserID, int remainingUsageCounter);
//
//    public TSAUser getTSAUser(String username);
//
//    public DatabaseResponse getTSALogId();
//
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
//            Date modifiedDt);
//
    public List<Integer> getVerificationFunctionIDGrantForRP(int relyingPartyId);
//
//    public boolean insertDocumentFaceFrame(
//            long subjectID,
//            long processID,
//            int identityDocumentTypeID,
//            byte[] blob,
//            int fileSize);
//
//    public List<IdentityDocumentFaceFrame> getIdentityDocumentFaceFrame(long subjectID, long processID, int identityDocumentTypeID);
//
//    public void deleteIdentityDocumentFaceFrame(long processID);
//
//    public boolean updateIdentitySubjectAttr(long subjectID, int attributeID, String value, byte[] binary);
//
//    public IdentitySubjectAttribute getIdentitySubjectAttr(long subjectID, int attributeID);
//
//    public IdentityDocument getIdentityDocumentByID(long identityDocumentID);
//
//    public Identity getIdentity(String identityUUID);
//
//    public Identity getIdentityByDocValue(int identityDocumentTypeID, String identityDocumentValue);
//
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
//            Date modifiedDt);
//
//    public DatabaseResponse getIdentityId();
//
//    public boolean updateIdentityAttr(long identityID, int attributeID, String value, byte[] binary);
//
//    public boolean updateIdentity(
//            long id,
//            long bestProcessID, // new
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
//            String address);
//
//    public IdentityAttribute getIdentityAttr(long identityID, int attributeID);
//
//    public GeneralPolicyAttribute getGeneralPolicy(int entityId, int attributeId);
//
//    public MasterListData getMasterListData(String masterListName);
//
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
//            byte[] efsod);
//
//    public boolean insertLicenceManager(
//            int type,
//            int relyingPartyID,
//            String functionName,
//            long counter);
//
//    public boolean updateLicenceManager(
//            int type,
//            int relyingPartyID,
//            String functionName,
//            long counter);
//
//    public LicenseManager getLicenseManager(int type, int relyingPartyID, String functionName);
//
//    public List<LicenseManager> getLicenseManager();
//
//    public IDCard getIDCard(String cardNo);
    
}
