/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SignFile;

import vn.mobileid.exsig.XmlProfile;

/**
 *
 * @author Tuan Pham
 */
public interface IXmlSign extends ISignFile {
    
    @Override
    XmlProfile getProfile();
    
}
