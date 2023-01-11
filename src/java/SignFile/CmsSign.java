/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SignFile;

import restful.sdk.API.Types.HashAlgorithmOID;
import vn.mobileid.exsig.CmsProfile;

/**
 *
 * @author Tuan Pham
 */
public class CmsSign extends RSSP_RestfulSign implements ICmsSign {

    public CmsSign(CmsProfile profile, HashAlgorithmOID alg) {
        super(profile, alg);
    }

    @Override
    public CmsProfile getProfile() {
        return (CmsProfile) super._profile;
    }
}
