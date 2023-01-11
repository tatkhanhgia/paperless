/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SignFile;

import java.util.ArrayList;
import java.util.List;
import restful.sdk.API.IServerSession;
import restful.sdk.API.Types.HashAlgorithmOID;
import restful.sdk.API.Types.SignAlgo;
import vn.mobileid.exsig.Profile;

/**
 *
 * @author Tuan Pham
 */
public class RSSP_RestfulSign implements ISignFile {

    protected final Profile _profile;
    private final HashAlgorithmOID hashAlgo;

    public RSSP_RestfulSign(Profile profile, HashAlgorithmOID alg) {
        _profile = profile;
        hashAlgo = alg;
    }

    public Profile getProfile() {
        return _profile;
    }

    public List<byte[]> sign(String agreementUUID, String credentialId, String pin, List<byte[]> files, IServerSession session) throws Exception {
        RestfulSigningMethod signingMethod = new RestfulSigningMethod(agreementUUID, credentialId, pin, session, SignAlgo.RSA, hashAlgo);
        return _profile.sign(signingMethod, files);
    }

}
