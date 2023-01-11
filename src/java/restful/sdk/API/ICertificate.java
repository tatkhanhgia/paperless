package restful.sdk.API;

import RestfulFactory.Model.DocumentDigests;
import restful.sdk.API.Types.SignAlgo;
import restful.sdk.API.Types.SignedPropertyType;
import restful.sdk.API.Types.HashAlgorithmOID;
import RestfulFactory.Model.MobileDisplayTemplate;
import java.util.HashMap;
import java.util.List;

public interface ICertificate {

    BaseCertificateInfo baseCredentialInfo() throws Exception;

    //getCredentialInfo();
    CertificateInfo credentialInfo() throws Throwable;

    CertificateInfo credentialInfo(String cetificate, boolean certInfoEnabled, boolean authInfoEnabled) throws Throwable;

    //ask RSSP send OTP to email or phone of certificate
    String sendOTP(String notificationTemplate, String notificationSubject) throws Throwable;

    //authorize
    //if certififate has auth_mode
    //          - PIN then authorizeCode is pin-code
    //          - OTP then authorizeCode is otp
    //          - TSE then authorizeCode is null
    //validIn in seconds
    String authorize(int numSignatures, DocumentDigests doc, SignAlgo signAlgo, String authorizeCode) throws  Throwable;

    String authorize(int numSignatures, DocumentDigests doc, SignAlgo signAlgo, String otpRequestID, String otp) throws Throwable;

    String authorize(int numSignatures, DocumentDigests doc, SignAlgo signAlgo, MobileDisplayTemplate displayTemplate) throws Throwable;

    //if DocumentDigests/SignAlgo is avaiable in authorize then they can missing
    List<byte[]> signHash(DocumentDigests documentDigest, SignAlgo signAlgo, String SAD) throws Throwable;

    //sign document, now support sign pdf file
    List<byte[]> signDoc(HashMap<SignedPropertyType, Object> signedProps, List<byte[]> docs, HashAlgorithmOID hashAlgo, SignAlgo signAlgo, String SAD) throws Throwable;
}
