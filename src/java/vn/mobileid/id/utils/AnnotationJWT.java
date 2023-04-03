/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.utils;

import vn.mobileid.id.eid.object.JWT_Authenticate;

/**
 *
 * @author GiaTK
 */
public enum AnnotationJWT {
    Name("@Name"),
    DocNumber("@DocNumber"),
    Password("@Password");
    
    private String name;

    private AnnotationJWT(String name) {
        this.name = name;
    }
    
    public String getNameAnnot(){
        return this.name;
    }
    
    public static String replaceWithJWT(String original, JWT_Authenticate jwt){
        if (original.contains(AnnotationJWT.DocNumber.getNameAnnot())) {
            original = original.replace(AnnotationJWT.DocNumber.getNameAnnot(), jwt.getDocument_number() == null ? "null" : jwt.getDocument_number());
        }
        if (original.contains(AnnotationJWT.Name.getNameAnnot())){
            original = original.replace(AnnotationJWT.Name.getNameAnnot(), jwt.getName());
        }
        if (original.contains(AnnotationJWT.Password.getNameAnnot())){
            original = original.replace(AnnotationJWT.Password.getNameAnnot(), jwt.getName());
        }
        return original;
    }
}
