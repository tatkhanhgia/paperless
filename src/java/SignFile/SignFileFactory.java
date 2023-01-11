/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SignFile;

import restful.sdk.API.APIException;
import restful.sdk.API.Types.HashAlgorithmOID;
import vn.mobileid.exsig.Algorithm;
import vn.mobileid.exsig.CmsProfile;
import vn.mobileid.exsig.OfficeForm;
import vn.mobileid.exsig.OfficeProfile;
import vn.mobileid.exsig.PKCS7Form;
import vn.mobileid.exsig.PdfForm;
import vn.mobileid.exsig.PdfProfile;
import vn.mobileid.exsig.PdfProfileCMS;
import vn.mobileid.exsig.XmlForm;
import vn.mobileid.exsig.XmlProfile;

/**
 *
 * @author Tuan Pham
 */
public class SignFileFactory {

    public IPdfSignFile createPdfSignFile(SignType signType, Algorithm alg, PdfForm form) throws APIException {
        PdfProfile profile;
        switch (signType) {
            case CMS:
                profile = new PdfProfileCMS(alg);
                break;
            case PAdES:
                profile = new PdfProfile(form, alg);
                break;
            default:
                throw new APIException("Not support SignType [" + signType + "]");
        }
        return new PdfSignFile(profile, getHashAlgorithmOID(alg));
    }

    public enum SignType {
        CMS,
        PAdES
    }

    // using CAdESForm form but library not implements
    public ICmsSign createCmsSign(Algorithm alg, PKCS7Form form) throws APIException {
        return new CmsSign(new CmsProfile(form, alg), getHashAlgorithmOID(alg));
    }

    public IXmlSign createXmlSign(Algorithm alg, XmlForm form) throws APIException {
        return new XmlSign(new XmlProfile(form, alg), getHashAlgorithmOID(alg));
    }

    public IOfficeSign createOfficeSign(Algorithm alg, OfficeForm form) throws APIException, Exception {
        return new OfficeSign(new OfficeProfile(form, alg), getHashAlgorithmOID(alg));
    }
            
    private HashAlgorithmOID getHashAlgorithmOID(Algorithm alg) throws APIException {
        switch (alg) {
            case SHA1:
                return HashAlgorithmOID.SHA_1;
            case SHA256:
                return HashAlgorithmOID.SHA_256;
            default:
                throw new APIException("Not support HashAlgorithmOID [" + alg + "]");
        }
    }
}
