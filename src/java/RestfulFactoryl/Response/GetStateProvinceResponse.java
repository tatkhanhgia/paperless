/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestfulFactoryl.Response;

import RestfulFactory.Model.Province;
import java.util.List;

/**
 *
 * @author Tuan Pham
 */
public class GetStateProvinceResponse extends Response {
    
    private List<Province> provinces;

    public List<Province> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<Province> provinces) {
        this.provinces = provinces;
    }
    
}
