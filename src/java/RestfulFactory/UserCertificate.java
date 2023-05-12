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
import restful.sdk.API.IUserSession;
import restful.sdk.API.Types;

public class UserCertificate implements ICertificate{

    private BaseCertificateInfo certificate;
    private String agreementUUID;
    private IUserSession userSession;

    public UserCertificate() {
    }

    public UserCertificate(BaseCertificateInfo cert, String agreementUUID, IUserSession serverSession) {
        this.certificate = cert;
        this.agreementUUID = agreementUUID;
        this.userSession = serverSession;
    }

    public BaseCertificateInfo baseCredentialInfo() {
        return certificate;
    }     

    @Override
    public CertificateInfo credentialInfo() throws Throwable {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CertificateInfo credentialInfo(String cetificate, boolean certInfoEnabled, boolean authInfoEnabled) throws Throwable {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String sendOTP(String notificationTemplate, String notificationSubject) throws Throwable {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String authorize(int numSignatures, DocumentDigests doc, SignAlgo signAlgo, String authorizeCode) throws Throwable {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String authorize(int numSignatures, DocumentDigests doc, SignAlgo signAlgo, String otpRequestID, String otp) throws Throwable {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String authorize(int numSignatures, DocumentDigests doc, SignAlgo signAlgo, MobileDisplayTemplate displayTemplate) throws Throwable {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<byte[]> signHash(DocumentDigests documentDigest, SignAlgo signAlgo, String SAD) throws Throwable {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<byte[]> signDoc(HashMap<SignedPropertyType, Object> signedProps, List<byte[]> docs, HashAlgorithmOID hashAlgo, SignAlgo signAlgo, String SAD) throws Throwable {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
