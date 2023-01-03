/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.objects;

/**
 *
 * @author ADMIN
 */
public class RelyingPartyAttribute {
    private int relyingPartyId;
    private int attributeId;
    private String value;
    private byte[] blob;

    public int getRelyingPartyId() {
        return relyingPartyId;
    }

    public void setRelyingPartyId(int relyingPartyId) {
        this.relyingPartyId = relyingPartyId;
    }

    public int getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }
    
    
}
