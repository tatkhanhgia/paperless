/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.paperless.objects;

/**
 *
 * @author Mobile ID 22
 */
public class ValueSignHash {
    private String valueSignHash;
    private String hashAlgo;
    
    public String getValueSignHash() {
        return valueSignHash;
    }

    public void setValueSignHash(String valueSignHash) {
        this.valueSignHash = valueSignHash;
    }
    
    public String getHashAlgo() {
        return hashAlgo;
    }

    public void setHashAlgo(String hashAlgo) {
        this.hashAlgo = hashAlgo;
    }
}
