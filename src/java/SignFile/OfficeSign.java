/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SignFile;

import restful.sdk.API.Types.HashAlgorithmOID;
import vn.mobileid.exsig.OfficeProfile;

/**
 *
 * @author Tuan Pham
 */
public class OfficeSign extends RSSP_RestfulSign implements IOfficeSign {
    
    public OfficeSign(OfficeProfile profile, HashAlgorithmOID alg) {
        super(profile, alg);
    }

    @Override
    public OfficeProfile getProfile() {
        return (OfficeProfile) super._profile;
    }

}
