/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.everification.object;

/**
 *
 * @author GiaTK
 */
public class CreateOwnerRequest {
    private String username;
    private String email;
    private String phone;
    private String pa;
    private String face_matching;
    private String fingerprint_verification;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getPa() {
        return pa;
    }

    public void setPa(String pa) {
        this.pa = pa;
    }
    
    public String getFace_matching() {
        return face_matching;
    }

    public void setFace_matching(String face_matching) {
        this.face_matching = face_matching;
    }
    
    public String getFingerprint_verification() {
        return fingerprint_verification;
    }

    public void setFingerprint_verification(String fingerprint_verification) {
        this.fingerprint_verification = fingerprint_verification;
    }
}
