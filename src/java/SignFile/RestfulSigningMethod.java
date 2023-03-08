/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SignFile;

import RestfulFactory.Model.DocumentDigests;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import restful.sdk.API.ICertificate;
import restful.sdk.API.IServerSession;
import restful.sdk.API.IUserSession;
import restful.sdk.API.Types.HashAlgorithmOID;
import restful.sdk.API.Types.SignAlgo;
import restful.sdk.API.Utils;
import vn.mobileid.exsig.SigningMethodAsync;
import vn.mobileid.exsig.SigningMethodSync;

/**
 *
 * @author GiaTK
 */
public class RestfulSigningMethod implements SigningMethodSync, SigningMethodAsync {

    // remove declare final to init constructor without parameter
    private String _agreementUUID;
    private String _credentialID;
    private String _pin;
    private List<String> _chain;
    private IServerSession _session;    
    //private readonly List<byte[]> _files;

    private SignAlgo _signAlgo;
    private HashAlgorithmOID _hashAlgo;
    
    private List<String> hashList;
    private List<String> signatures;

    public RestfulSigningMethod(String _agreementUUID, String _credentialID, String _pin, IServerSession _session, SignAlgo _signAlgo, HashAlgorithmOID _hashAlgo) {
        this._agreementUUID = _agreementUUID;
        this._credentialID = _credentialID;
        this._pin = _pin;
        this._session = _session;
        this._signAlgo = _signAlgo;
        this._hashAlgo = _hashAlgo;
    }

    // remove declare final to init constructor without parameter
    public RestfulSigningMethod() {        
    }          

    @Override
    public List<String> getCert() throws Exception {
        //if(_chain == null || _chain.Count == 0)
        //{
        //    ICertificate iCert = _session.certificateInfo(_agreementUUID, _credentialID, "chain", false, false);
        //    _chain = new List<string>( iCert.baseCredentialInfo().certificates);
        //}
        //return _chain;
        ICertificate iCert = null;
        try {
            iCert = _session.certificateInfo(_agreementUUID, _credentialID, "chain", false, false);
        } catch (Throwable ex) {            
        }
        _chain = new ArrayList<String>(Arrays.asList(iCert.baseCredentialInfo().getCertificates()));
        return _chain;
    }

    @Override
    public List<String> sign(List<String> hashList) throws Exception {
        DocumentDigests doc = new DocumentDigests();
        doc.setHashAlgorithmOID(_hashAlgo);
        doc.setHashes(new ArrayList<byte[]>());
        for (String el : hashList) {
            try {
                doc.getHashes().add(Utils.base64Decode(el));
            } catch (Throwable ex) {

            }
        }
        String sad = null;
        try {
            sad = _session.authorize(_agreementUUID, _credentialID, hashList.size(), doc, _signAlgo, _pin);
        } catch (Throwable ex) {
            Logger.getLogger(RestfulSigningMethod.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<byte[]> signatures = null;
        try {
            signatures = _session.signHash(_agreementUUID, _credentialID, doc, _signAlgo, sad);
        } catch (Throwable ex) {
            Logger.getLogger(RestfulSigningMethod.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<String> ret = new ArrayList<String>();
        for (int i = 0; i < hashList.size(); i++) {
            byte[] sig = signatures.get(i);
            try {
                ret.add(Utils.base64Encode(sig));
            } catch (Throwable ex) {

            }
            //Console.WriteLine("PKCS#1-Signature: " + Utils.Base64Encode(sig));
            ////Try verify signature
            //var cert = new X509Certificate2(Utils.Base64Decode(_chain[0]));
            //using (RSA rsa = cert.GetRSAPublicKey())
            //{
            //    //bool verify = rsa.VerifyData(Encoding.UTF8.GetBytes(data), sig, hashAlgorithmName, rsaPadding);
            //    //Console.WriteLine("Verify-Signature: " + verify);
            //    bool verify = rsa.VerifyHash(Utils.Base64Decode(hashList[i]), sig, HashAlgorithmName.SHA256, RSASignaturePadding.Pkcs1);
            //    Console.WriteLine("Verify-Signature: " + verify);
            //}
        }
        return ret;
    }

    @Override
    public List<String> pack() throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return this.signatures;
    }

    @Override
    public void generateTempFile(List<String> list) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        this.hashList = list;
    }
    
}
