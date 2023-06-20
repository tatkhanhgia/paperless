/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.utils.Crypto;

/**
 *
 * @author GiaTK
 */
public class ProcessTrustManager {

    /**
     * Get Certificate of TrustManager
     *
     * @param service_name
     * @param remark
     * @param url
     * @param transactionID
     * @return
     * @throws Exception
     */
    public static InternalResponse getCertificate(
            String service_name,
            String remark,
            String url,
            String transactionID
    ) throws Exception {

        Database callDB = new DatabaseImpl();
        DatabaseResponse res = callDB.getCertificate(
                service_name,
                remark,
                url,
                transactionID);
        try {
            if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_FAIL,
                                res.getStatus(),
                                "en",
                                null)
                );
            }
            List<X509Certificate> list = Crypto.getCertificate((String) res.getObject());
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    list);
        } catch (Exception ex) {
            LogHandler.error(
                    ProcessTrustManager.class,
                    transactionID,
                    "Cannot Get Trust Manager - Details:",
                    ex);

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_500,
                    PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    /**
     * Verify infomation of TrustManager
     *
     * @param jwt
     * @param transactionID
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    public static InternalResponse verifyTrustManager(
            String jwt,
            String transactionID
    ) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException, Exception {
        String[] chunks = jwt.split("\\.");

        String header = null;
        String payload = null;
        String signature = null;

        header = new String(Base64.getUrlDecoder().decode(chunks[0]), "UTF-8");
        payload = new String(Base64.getUrlDecoder().decode(chunks[1]), "UTF-8");
        signature = chunks[2];

        User data = null;
        try {
            data = new ObjectMapper().readValue(payload, User.class);
        } catch (Exception e) {
            LogHandler.error(
                    ManageTokenWithDB.class,
                    transactionID,
                    "Error while parsing Data!",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_INVALID_TOKEN,
                            "en",
                            null));
        }

        InternalResponse res = ProcessTrustManager.getCertificate(
                null, //service name
                null, //remark - EID, PPL
                data.getIss(),
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        List<X509Certificate> listCert = (List<X509Certificate>) res.getData();
        if (listCert.isEmpty()) {            
            res.setStatus(PaperlessConstant.HTTP_CODE_FORBIDDEN);
            res.setMessage("{This Trust Manager don't containt Certificate to verify JWT!!}");
            return res;
        }
        for (int i = 0; i < listCert.size(); i++) {
            Security.addProvider(new BouncyCastleProvider());
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(listCert.get(0).getPublicKey());
            String datas = chunks[0] + "." + chunks[1];
            sign.update(datas.getBytes());
            boolean check = sign.verify(Base64.getUrlDecoder().decode(signature));
            if (check) {
                res.setStatus(PaperlessConstant.HTTP_CODE_SUCCESS);
                return res;
            }
        }
        res.setStatus(PaperlessConstant.HTTP_CODE_BAD_REQUEST);
        res.setMessage("{Cannot verify this JWT of trust manager!!}");
        return res;
    }
   
    
    public static void main(String[] args) throws Exception {
        InternalResponse res = ProcessTrustManager.getCertificate(
                null,
                "EID",
                null,
                "transactionID");
        System.out.println("Status:" + res.getStatus());
        List<X509Certificate> list = (List<X509Certificate>) res.getData();
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getSubjectDN());
        }
        X509Certificate a = list.get(0);
//        byte[] temp = Crypto.sign(data, a.getP, "base64");
    }
}
