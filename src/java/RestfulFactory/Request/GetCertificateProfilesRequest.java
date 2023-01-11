/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestfulFactory.Request;

/**
 *
 * @author Tuan Pham
 */
public class GetCertificateProfilesRequest extends Request {

    private String caName;

    public String getCaName() {
        return caName;
    }

    public void setCaName(String caName) {
        this.caName = caName;
    }

}
