/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestfulFactoryl.Response;

import RestfulFactory.Model.CertificateAuthority;
import java.util.List;

/**
 *
 * @author Tuan Pham
 */
public class GetCertificateAuthoritiesResponse extends Response {
    
    private List<CertificateAuthority> certificateAuthorities;

    public List<CertificateAuthority> getCertificateAuthorities() {
        return certificateAuthorities;
    }

    public void setCertificateAuthorities(List<CertificateAuthority> certificateAuthorities) {
        this.certificateAuthorities = certificateAuthorities;
    }
    
}
