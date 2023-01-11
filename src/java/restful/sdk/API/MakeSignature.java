package restful.sdk.API;

import RestfulFactory.Model.CertificateDetails;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Base64;

public class MakeSignature {

    private String data;
    private String key;
    private String passKey;
    boolean isAliasWithPrivateKey = false;
    CertificateDetails certDetails = null;

    public MakeSignature(String data, String PriKeyPath, String PriKeyPass) {
        this.data = data;
        this.key = PriKeyPath;
        this.passKey = PriKeyPass;
    }

    public String getSignature() throws Throwable {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initSign(GetKey());
        sig.update(data.getBytes());
        return Base64.getEncoder().encodeToString(sig.sign());
    }

//    public static String Sign(String content, RSA rsa) throws Throwable {
//        //System.Security.Cryptography.RSACryptoServiceProvider crsa = rsa;
//        byte[] Data = Encoding.getUTF8().GetBytes(content);
//        byte[] signData = rsa.SignData(Data, HashAlgorithmName.getSHA1(), RSASignaturePadding.getPkcs1());
//        return Convert.ToBase64String(signData);
//    }

    private PrivateKey GetKey() throws Throwable {
        System.out.println("Key = " + this.key + ", Value = " + this.passKey);
        
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        InputStream is = new FileInputStream(this.key);
        keystore.load(is, this.passKey.toCharArray());

        Enumeration<String> e = keystore.aliases();
        PrivateKey key = null;
        String aliasName = "";
        while (e.hasMoreElements()) {
            aliasName = e.nextElement();
            key = (PrivateKey) keystore.getKey(aliasName, this.passKey.toCharArray());
            if (key != null) {
                break;
            }
        }

        return key;

//        try {
//            KeyStore ks = KeyStore.getInstance("Windows-MY");
//            ks.load(new FileInputStream("file/" + this.key), this.passKey.toCharArray());
//            Enumeration<String> al = ks.aliases();
//            while (al.hasMoreElements()) {
//                String alias = al.nextElement();
//                if (ks.containsAlias(alias)) {                    
//                    System.out.println("alias = " + alias);
//                    X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
////                    System.out.println("cert:" + cert);
//                    System.out.println("Subject:" + cert.getSubjectDN().toString());
//                }
//            }
//        } catch (Exception e) {
//            
//        }

    }
}
