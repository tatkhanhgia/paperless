package RestfulFactory;

import RestfulFactory.Model.MobileDisplayTemplate;
import restful.sdk.API.Types.SignAlgo;
import restful.sdk.API.Types.SignedPropertyType;
import restful.sdk.API.Types.HashAlgorithmOID;
import RestfulFactory.Model.DocumentDigests;
import restful.sdk.API.CertificateInfo;
import java.util.List;
import restful.sdk.API.BaseCertificateInfo;
import java.util.HashMap;
import restful.sdk.API.APIException;
import restful.sdk.API.ICertificate;
import restful.sdk.API.IServerSession;
import restful.sdk.API.Types;

public class Certificate implements ICertificate {

    private BaseCertificateInfo certificate;
    private String agreementUUID;
    private IServerSession serverSession;

    public Certificate() {
    }

    public Certificate(BaseCertificateInfo cert, String agreementUUID, IServerSession serverSession) {
        this.certificate = cert;
        this.agreementUUID = agreementUUID;
        this.serverSession = serverSession;
    }

    public BaseCertificateInfo baseCredentialInfo() {
        return certificate;
    }

    @Override
    public CertificateInfo credentialInfo() throws Throwable {
        ICertificate icrt = this.serverSession.certificateInfo(this.agreementUUID, certificate.getCredentialID());
        if (icrt.baseCredentialInfo() instanceof CertificateInfo) {
            return (CertificateInfo) icrt.baseCredentialInfo();
        }
        System.out.println("Type of certificate is not [CertificateInfo]");
        throw new APIException("Type of certificate is not [CertificateInfo]");
    }

    @Override
    public CertificateInfo credentialInfo(String cetificate, boolean certInfoEnabled, boolean authInfoEnabled) throws Throwable {
        //call to server
        ICertificate icrt = this.serverSession.certificateInfo(this.agreementUUID, certificate.getCredentialID(), cetificate, certInfoEnabled, authInfoEnabled);
        if (icrt.baseCredentialInfo() instanceof CertificateInfo) {
            return (CertificateInfo) icrt.baseCredentialInfo();
        }
        System.out.println("Type of certificate is not [CertificateInfo]");
        throw new APIException("Type of certificate is not [CertificateInfo]");
    }

    @Override
    public String sendOTP(String notificationTemplate, String notificationSubject) throws Throwable {
        return this.serverSession.sendOTP(this.agreementUUID, this.certificate.getCredentialID(), notificationTemplate, notificationSubject);
    }
    @Override
    public String authorize(int numSignatures, DocumentDigests doc, SignAlgo signAlgo, String authorizeCode) throws Throwable {
        return this.serverSession.authorize(this.agreementUUID, this.certificate.getCredentialID(), numSignatures, doc, signAlgo, authorizeCode);
    }

    @Override
    public String authorize(int numSignatures, DocumentDigests doc, SignAlgo signAlgo, String otpRequestID, String otp) throws Throwable {
        return this.serverSession.authorize(this.agreementUUID, this.certificate.getCredentialID(), numSignatures, doc, signAlgo, otpRequestID, otp);
    }

    @Override
    public String authorize(int numSignatures, DocumentDigests doc, SignAlgo signAlgo, MobileDisplayTemplate displayTemplate) throws Throwable {
        return this.serverSession.authorize(this.agreementUUID, this.certificate.getCredentialID(), numSignatures, doc, signAlgo, displayTemplate);
    }

    @Override
    public List<byte[]> signHash(DocumentDigests documentDigest, SignAlgo signAlgo, String SAD) throws Throwable {
        return this.serverSession.signHash(this.agreementUUID, this.certificate.getCredentialID(), documentDigest, signAlgo, SAD);
    } 

    @Override
    public List<byte[]> signDoc(HashMap<SignedPropertyType, Object> signedProps, List<byte[]> docs, HashAlgorithmOID hashAlgo, SignAlgo signAlgo, String SAD) throws Throwable {
        return this.serverSession.signDoc(this.agreementUUID, this.certificate.getCredentialID(), signedProps, docs, hashAlgo, signAlgo, SAD);
    }

}
