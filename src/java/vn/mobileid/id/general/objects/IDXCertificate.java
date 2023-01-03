/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.objects;

import java.util.Date;

/**
 *
 * @author ADMIN
 */
public class IDXCertificate {
    private int id;
    private String name;
    private Date validFrom;
    private Date validTo;
    private String certificate;
    private byte[] privateKeyEncryptedData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public byte[] getPrivateKeyEncryptedData() {
        return privateKeyEncryptedData;
    }

    public void setPrivateKeyEncryptedData(byte[] privateKeyEncryptedData) {
        this.privateKeyEncryptedData = privateKeyEncryptedData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }    
}
