package restful.sdk.API;

import RestfulFactory.Model.DocumentDigests;
import java.util.List;

/**
 *
 * @author GiaTK
 */
public interface IUserSession {
    boolean sendUserOTP() throws Throwable;
    
    public List<ICertificate> listCertificates() throws Throwable;

    public ICertificate certificateInfo( String credentialID) throws Throwable;

    public ICertificate certificateInfo(String agreementUUID, String credentialID, String cetificate, boolean certInfoEnabled, boolean authInfoEnabled) throws Throwable;

    public List<byte[]> signHash( String credentialID, DocumentDigests documentDigest, Types.SignAlgo signAlgo, String SAD) throws Throwable;

    public String authorize( String credentialID, int numSignatures, String requestID, String authorizeCode) throws Throwable;

    public boolean preLogin(String username) throws Throwable;
}
