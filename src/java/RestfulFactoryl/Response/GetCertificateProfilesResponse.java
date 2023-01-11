/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestfulFactoryl.Response;

import RestfulFactory.Model.CertificateProfile;
import java.util.List;

/**
 *
 * @author Tuan Pham
 */
public class GetCertificateProfilesResponse extends Response {
    
    private List<CertificateProfile> profiles;

    public List<CertificateProfile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<CertificateProfile> profiles) {
        this.profiles = profiles;
    }
    
}
