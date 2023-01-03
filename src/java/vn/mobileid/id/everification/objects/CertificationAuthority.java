/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.everification.objects;

import java.security.cert.X509Certificate;
import java.util.Date;

/**
 *
 * @author VUDP
 * Contains information about the CA
 */
public class CertificationAuthority {

    private int certificationAuthorityID;
    private String name;
    private String uri;
    private Date effectiveDate;
    private Date expiredDate;
    private String subjectDn;
    private String pemCertificate;
    private String pemExCertificate;
    private String remark;
    private String remarkEn;
    private CAProperties caProperties;

    private String subjectKeyIdentifier;
    private String issuerKeyIdentifier;
    
    private String commonName;
    private X509Certificate x509Object;

    public int getCertificationAuthorityID() {
        return certificationAuthorityID;
    }

    public void setCertificationAuthorityID(int certificationAuthorityID) {
        this.certificationAuthorityID = certificationAuthorityID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemarkEn() {
        return remarkEn;
    }

    public void setRemarkEn(String remarkEn) {
        this.remarkEn = remarkEn;
    }

    public CAProperties getCaProperties() {
        return caProperties;
    }

    public void setCaProperties(CAProperties caProperties) {
        this.caProperties = caProperties;
    }

    public String getPemCertificate() {
        return pemCertificate;
    }

    public void setPemCertificate(String pemCertificate) {
        this.pemCertificate = pemCertificate;
    }

    public String getSubjectDn() {
        return subjectDn;
    }

    public void setSubjectDn(String subjectDn) {
        this.subjectDn = subjectDn;
    }

    public String getPemExCertificate() {
        return pemExCertificate;
    }

    public void setPemExCertificate(String pemExCertificate) {
        this.pemExCertificate = pemExCertificate;
    }

    public String getSubjectKeyIdentifier() {
        return subjectKeyIdentifier;
    }

    public void setSubjectKeyIdentifier(String subjectKeyIdentifier) {
        this.subjectKeyIdentifier = subjectKeyIdentifier;
    }

    public String getIssuerKeyIdentifier() {
        return issuerKeyIdentifier;
    }

    public void setIssuerKeyIdentifier(String issuerKeyIdentifier) {
        this.issuerKeyIdentifier = issuerKeyIdentifier;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public X509Certificate getX509Object() {
        return x509Object;
    }

    public void setX509Object(X509Certificate x509Object) {
        this.x509Object = x509Object;
    }
    
    
    
}
