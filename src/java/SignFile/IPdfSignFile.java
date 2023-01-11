/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SignFile;

import java.util.List;
import restful.sdk.API.IServerSession;
import vn.mobileid.exsig.PdfProfile;

/**
 *
 * @author Tuan Pham
 */
public interface IPdfSignFile extends ISignFile {
    
        PdfProfile getProfile();

        @Deprecated
	PdfProfile getPdfProfile();
        
        byte[] createBlankSignature(String agreementUUID, String credentialID, IServerSession session ,List<byte[]> files) throws Exception;

        byte[] createBlankSignature(List<byte[]> files) throws Exception;

        byte[] addSignature(String agreementUUID, String credentialId, String pin, byte[] blankSignature, IServerSession session) throws Exception;

}
