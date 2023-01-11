/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SignFile;

import java.util.ArrayList;
import java.util.List;
import restful.sdk.API.IServerSession;
import vn.mobileid.exsig.Profile;

/**
 *
 * @author Tuan Pham
 */
public interface ISignFile {
            
    Profile getProfile();
    
    List<byte[]> sign(String agreementUUID, String credentialId, String pin, List<byte[]> files, IServerSession session) throws Exception;
}
