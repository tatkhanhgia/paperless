/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

/**
 *
 * @author GiaTK
 */
public enum TemplateSignature {
    Elabor("elabor"),
    EsignCloud("esigncloud");
    
    private String name;

    private TemplateSignature(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
