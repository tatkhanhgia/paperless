/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;
import sun.misc.BASE64Encoder;
import sun.security.provider.X509Factory;
import vn.mobileid.id.utils.Crypto;

/**
 *
 * @author GiaTK
 */
public class TestCreateCertificate {

    public static void main(String[] arhs) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, GeneralSecurityException, Exception {
        PublicKey pub = getPublicKey();
        PrivateKey pri = getPrivateKey();

        FileOutputStream fos = new FileOutputStream("D:\\NetBean\\paperless\\file\\temp.cer");
        X509Certificate temp = Crypto.generateSelfSignCertificate(
                "CN=Paperless Service, O=MOBILE-ID TECHNOLOGIES AND SERVICES JOINT STOCK COMPANY, L=Quận 2,C=VN",
                pub.getEncoded(),
                pri);
        BASE64Encoder encoder = new BASE64Encoder();
            fos.write(X509Factory.BEGIN_CERT.getBytes());   
            fos.write("\n".getBytes());
            encoder.encodeBuffer(temp.getEncoded(), fos);
            fos.write(X509Factory.END_CERT.getBytes());
        

    }

    private static PrivateKey getPrivateKey() throws IOException, GeneralSecurityException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream input = loader.getResourceAsStream("resources/config/key.key");
        String file = IOUtils.toString(input, StandardCharsets.UTF_8);
        PrivateKey pri = Crypto.getPrivateKeyFromString(file, "base64");
        return pri;
    }

    private static PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream input = loader.getResourceAsStream("resources/config/key.pub");
        String file = IOUtils.toString(input, StandardCharsets.UTF_8);
        String privateKeyPEM = file;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN RSA PUBLIC KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END RSA PUBLIC KEY-----", "");
        byte[] encoded = DatatypeConverter.parseBase64Binary(privateKeyPEM);
        X509EncodedKeySpec spec
                = new X509EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
//        return Base64.getEncoder().encodeToString(kf.generatePublic(spec).getEncoded());
        return kf.generatePublic(spec);
    }
}
