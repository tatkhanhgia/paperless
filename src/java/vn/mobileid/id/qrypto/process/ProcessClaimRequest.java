/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.process;

import COSE.HeaderKeys;
import com.upokecenter.cbor.CBORObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import java.util.zip.Inflater;
import vn.mobileid.id.qrypto.request.ClaimRequest;
import vn.mobileid.id.qrypto.request.KeyPairData;
import vn.mobileid.id.qrypto.request.PublicKeyData;
import vn.mobileid.id.qrypto.response.IssueQryptoWithFileAttachResponse;
import vn.mobileid.id.utils.Base45;
import vn.mobileid.id.utils.Crypto;

/**
 *
 * @author GiaTK
 */
public class ProcessClaimRequest {

    private static final int ECDSA_256 = -7;
    private static final int RSA_PSS_256 = -37;

    public static ClaimRequest generateClaimRequest(
            IssueQryptoWithFileAttachResponse object
    ) throws Exception {

        String withoutPrefix = object.getQryptoBase45().substring(4);
        byte[] data = Base45.getDecoder().decode(withoutPrefix);

        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            final int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }

        //Convert to COSE
        CBORObject messageObject = CBORObject.DecodeFromBytes(outputStream.toByteArray());
        byte[] coseSignature = messageObject.get(3).GetByteString();
        byte[] protectedHeader = messageObject.get(0).GetByteString();
        byte[] content = messageObject.get(2).GetByteString();
        byte[] cert = getValidationData(protectedHeader, content);

        CBORObject unprotectedHeader = messageObject.get(1);

        KeyPairData keypair = generateKeyPair(protectedHeader, unprotectedHeader);

        //Create CertHash + TanHash
        byte[] certHashByte = Crypto.hashData(cert, Crypto.HASH_SHA256);
        String certHash = Base64.getEncoder().encodeToString(certHashByte);
        
        byte[] tanHashByte = Crypto.hashData(object.getTan().getBytes(), Crypto.HASH_SHA256);
        String tanHash = Base64.getEncoder().encodeToString(tanHashByte);
                
        //Create Signature
        String temp = Base64.getEncoder().encodeToString(keypair.getKeypair().getPublic().getEncoded());
        PublicKeyData pubData = new PublicKeyData(keypair.getNameType(), temp);
        
        String signature = generateClaimSignature(
                tanHash,
                certHash,
                pubData.getValue(),
                keypair.getKeypair().getPrivate(),
                keypair.getNameSigAlg());
        
        String sigAlg = keypair.getNameSigAlg();
        
        //Convert to JSON Object
//        Message a = Encrypt0Message.DecodeFromBytes(outputStream.toByteArray());
//        CborMap cborMap = CborMap.createFromCborByteArray(a.GetContent());

        //Create ClaimRequest
        ClaimRequest response = new ClaimRequest();
        response.setTanHash(tanHash);
        response.setCertHash(certHash);
        response.setSignature(signature);
        response.setSigAlg(sigAlg);
        response.setDGCI(object.getCi());
        response.setPublicKeyData(pubData);

        return response;
    }

    private static byte[] getValidationData(byte[] protectedHeader, byte[] content) {
        return CBORObject.NewArray().
                Add("Signature1").
                Add(protectedHeader).
                Add(new byte[0]).
                Add(content).EncodeToBytes();
    }

    private static byte[] getKid(byte[] protectedHeader, CBORObject unprotectedHeader) {
        CBORObject key = HeaderKeys.KID.AsCBOR();
        CBORObject kid;
        if (protectedHeader.length != 0) {
            try {
                kid = CBORObject.DecodeFromBytes(protectedHeader).get(key);
                if (kid == null) {
                    kid = unprotectedHeader.get(key);
                }
            } catch (Exception var8) {
                kid = unprotectedHeader.get(key);
            }
        } else {
            kid = unprotectedHeader.get(key);
        }
        return kid.GetByteString();
    }

    public static PublicKey getPublicKey(String key) throws CertificateException {
        byte[] decoded = Base64.getDecoder().decode(key);
        InputStream inputStream = new ByteArrayInputStream(decoded);
        return CertificateFactory.getInstance("X.509").generateCertificate(inputStream).getPublicKey();
    }

    private static int getAlgoFromHeader(byte[] protectedHeader, CBORObject unprotectedHeader) {
        int algoNumber;
        if (protectedHeader.length != 0) {
            try {
                CBORObject algo = CBORObject.DecodeFromBytes(protectedHeader).get(1);
                algoNumber = algo != null ? algo.AsInt32Value() : unprotectedHeader.get(1).AsInt32Value();
            } catch (Exception var7) {
                algoNumber = unprotectedHeader.get(1).AsInt32Value();
            }
        } else {
            algoNumber = unprotectedHeader.get(1).AsInt32Value();
        }
        return algoNumber;
    }

    private static KeyPairData generateKeyPair(byte[] protectedHeader, CBORObject cose) {
        try {
            switch (getAlgoFromHeader(protectedHeader, cose)) {
                case ECDSA_256:
                    KeyPairGenerator keypair = KeyPairGenerator.getInstance("EC");
                    keypair.initialize(256);
                    return new KeyPairData("SHA256WithECDSA","EC", keypair.generateKeyPair());
                case RSA_PSS_256:
                    KeyPairGenerator keypair2 = KeyPairGenerator.getInstance("RSA");
                    keypair2.initialize(2048);
                    return new KeyPairData("SHA256WithRSA","RSA", keypair2.generateKeyPair());
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private static String generateClaimSignature(
            String tanHash,
            String certHash,
            String pubKey,
            PrivateKey priKey,
            String sigAlg
    ) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append(tanHash)
                .append(certHash)
                .append(pubKey);
        Signature signature = Signature.getInstance(sigAlg);
        signature.initSign(priKey);
        signature.update(builder.toString().getBytes("UTF-8"));
        byte[] signed = signature.sign();
        return Base64.getEncoder().encodeToString(signed);
    }
}
