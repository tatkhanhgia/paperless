package restful.sdk.API;

import RestfulFactory.Model.CertificateAuthority;
import RestfulFactory.Model.MobileDisplayTemplate;
import restful.sdk.API.Types.HashAlgorithmOID;
import restful.sdk.API.Types.OTPType;
import restful.sdk.API.Types.SignAlgo;
import restful.sdk.API.Types.SignedPropertyType;
import java.util.HashMap;
import RestfulFactory.Model.SearchConditions;
import RestfulFactory.Model.CertificateDetails;
import RestfulFactory.Model.CertificateProfile;
import RestfulFactory.Model.DocumentDigests;
import RestfulFactory.Model.Province;
import RestfulFactoryl.Response.IssueCertificateResponse;
import java.util.List;
import restful.sdk.API.Types.AuthMode;
import restful.sdk.API.Types.SharedMode;

public interface IServerSession extends ISession {

    //get certificate of user indentity by agreement-uuid
    //credentials/list
    List<ICertificate> listCertificates(String agreementUUID) throws Throwable;

    List<ICertificate> listCertificates(String agreementUUID, String certificate, boolean certInfoEnabled, boolean authInfoEnabled, SearchConditions conditions) throws Throwable;

    //get certificate-info of user indentity by agreement-uuid and certificate-uuid
    //credentials/info
    ICertificate certificateInfo(String agreementUUID, String credentialID) throws Throwable;

    ICertificate certificateInfo(String agreementUUID, String credentialID, String certificate, boolean certInfoEnabled, boolean authInfoEnabled) throws Throwable;

    //ask rssp send otp to email/sms of certificate
    String sendOTP(String agreementUUID, String credentialID, String notificationTemplate, String notificationSubject) throws Throwable;

    //authorize
    //if certififate has auth_mode
    //          - PIN then authorizeCode is pin-code
    //          - OTP then authorizeCode is otp
    //          - TSE then authorizeCode is null
    //validIn in seconds
    String authorize(String agreementUUID, String credentialID, int numSignatures, DocumentDigests doc, SignAlgo signAlgo, String authorizeCode) throws Throwable;

    String authorize(String agreementUUID, String credentialID, int numSignatures, DocumentDigests doc, SignAlgo signAlgo, String otpRequestID, String otp) throws Throwable;

    String authorize(String agreementUUID, String credentialID, int numSignatures, DocumentDigests doc, SignAlgo signAlgo, MobileDisplayTemplate displayTemplate) throws Throwable;

    List<byte[]> signHash(String agreementUUID, String credentialID, DocumentDigests documentDigest, SignAlgo signAlgo, String SAD) throws Throwable;

    List<byte[]> signDoc(String agreementUUID, String credentialID, HashMap<SignedPropertyType, Object> signedProps, List<byte[]> docs, HashAlgorithmOID hashAlgo,
            SignAlgo signAlgo, String SAD) throws Throwable;

    //ask rssp send otp to end-user identity by username
    //owner/sendOTP
    String sendUserOTPByUserName(OTPType otpType, String username) throws Throwable;

    //ask rssp send otp to end-user identity by agreement-uuid
    //owner/sendOTP
    String sendUserOTPByAgreement(OTPType otpType, String agreementUUID) throws Throwable;

    //assign agreeement to end-user identity by username
    //agreemtn-uuid is unique per relying-party
    //agreements/assign
    boolean assignAgreement(String userName, String agreementUUID, String otpRequestID, String otp) throws Throwable;

    //create end-user
    boolean createUser(String userName, String email, String phone) throws Throwable;

    //get CA
    List<CertificateAuthority> getCertificateAuthorities() throws Throwable;

    //get Profile
    List<CertificateProfile> getCertificateProfies(String caName) throws Throwable;

    List<Province> getStatesOrProvinces() throws Throwable;

    //create certificate
    IssueCertificateResponse createCertificate(String userName, String otpRequestID, String otp, String certProfile, SharedMode sharedMode,
            AuthMode authMode, int multiSign, CertificateDetails certDetails) throws Throwable;

    //import certificate
    ICertificate importCertificate(String agreementUUID, String credentialID, String cert) throws Throwable;
}
