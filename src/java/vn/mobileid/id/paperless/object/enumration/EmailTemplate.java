/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

/**
 *
 * @author GiaTK
 */
public enum EmailTemplate {
    EMAIL_FORGOT_PASSWORD("email_forgot_password"),
    EMAIL_SEND_PASSWORD("email_send_password");
    
    private String name;

    private EmailTemplate(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
