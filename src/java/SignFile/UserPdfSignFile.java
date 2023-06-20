/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SignFile;

import java.util.List;
import restful.sdk.API.IServerSession;
import restful.sdk.API.Types.HashAlgorithmOID;
import restful.sdk.API.Types.SignAlgo;
import vn.mobileid.exsig.Algorithm;
import vn.mobileid.exsig.PdfProfile;
import vn.mobileid.exsig.PdfProfileCMS;

/**
 *
 * @author Tuan Pham
 */
public class UserPdfSignFile extends UserRSSP_RestfulSign implements IPdfSignFile {

    private final HashAlgorithmOID hashAlgo;

    public UserPdfSignFile(PdfProfile profile, HashAlgorithmOID alg) {
        super(profile, alg);
        hashAlgo = alg;
    }

    @Override
    public PdfProfile getProfile() {
        return (PdfProfile) super._profile;
    }

    @Override
    public byte[] createBlankSignature(String agreementUUID, String credentialID, IServerSession session, List<byte[]> files) throws Exception {
        return getProfile().createTemporalFile(new RestfulSigningMethod(agreementUUID, credentialID, "", session, SignAlgo.RSA, hashAlgo), files);
    }

    @Override
    public byte[] createBlankSignature(List<byte[]> files) throws Exception {
        return ((PdfProfileCMS) getProfile()).createTemporalFile(new RestfulSigningMethod(), files);
    }

    @Override
    public byte[] addSignature(String agreementUUID, String credentialId, String pin, byte[] blankSignature, IServerSession session) throws Exception {
        RestfulSigningMethod rest = new RestfulSigningMethod(agreementUUID, credentialId, pin, session, SignAlgo.RSA, hashAlgo);
//        PdfProfileCMS a = new PdfProfileCMS(Algorithm.SHA1);
//        return a.sign(rest, blankSignature).get(0);
        return PdfProfileCMS.sign(rest, blankSignature).get(0);
    }

    @Override
    public PdfProfile getPdfProfile() {
        return (PdfProfile) super._profile;
    }

    @Override
    public PdfProfileCMS getProfileCMS() {
        return (PdfProfileCMS) super._profile;
    }

}
