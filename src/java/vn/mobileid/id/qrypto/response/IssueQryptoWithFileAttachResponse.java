/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.response;

/**
 *
 * @author GiaTK
 */
public class IssueQryptoWithFileAttachResponse extends Response{
    private String tan;
    private String qryptoBase45;
    private String qryptoBase64;
    private String ci;

    public IssueQryptoWithFileAttachResponse() {
    }

    public String getTan() {
        return tan;
    }

    public void setTan(String tan) {
        this.tan = tan;
    }

    public String getQryptoBase45() {
        return qryptoBase45;
    }

    public void setQryptoBase45(String qryptoBase45) {
        this.qryptoBase45 = qryptoBase45;
    }

    public String getQryptoBase64() {
        return qryptoBase64;
    }

    public void setQryptoBase64(String qryptoBase64) {
        this.qryptoBase64 = qryptoBase64;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }
    
    
}
