/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.utils;

import vn.mobileid.id.general.annotation.AnnotationJWT;

/**
 *
 * @author GiaTK
 */
public class UserActivityDescription implements UserActivityDecscriptionBuilder{
    private String result;
    private String original;

    public UserActivityDescription(String original) {
        this.original = original;
    }
    
    @Override
    public UserActivityDecscriptionBuilder append(AnnotationJWT annotation, String data) {
        if(result == null){
            result = AnnotationJWT.replaceAnnotation(original, annotation, data);
        } else {
            result = AnnotationJWT.replaceAnnotation(result, annotation, data);
        }
        return this;
    }

    @Override
    public String build() {
        return result;
    }
    
    public static void main(String[] args) {
        String template = "@UserEmail has submitted Task @CSVID to run workflow @WorkflowID: @WorkflowTemplateTypeName";
        UserActivityDescription builder = new UserActivityDescription(template);
        String result = builder.append(AnnotationJWT.UserEmail, "giatk@mobile-id.vn")
                               .append(AnnotationJWT.CSV_id, "1")
                               .append(AnnotationJWT.Workflow_id, "2")
                               .append(AnnotationJWT.WorkflowTemplateTypeName, "Secure QR Template")
                               .build();
        
        System.out.println("Result:"+result);
    }
}
