/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SignFile;

import restful.sdk.API.Types.HashAlgorithmOID;
import vn.mobileid.exsig.XmlProfile;

/**
 *
 * @author Tuan Pham
 */
public class XmlSign extends RSSP_RestfulSign implements IXmlSign {

    public XmlSign(XmlProfile profile, HashAlgorithmOID alg) {
        super(profile, alg);
    }

    @Override
    public XmlProfile getProfile() {
        return (XmlProfile) super._profile;
    }
}
