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
public interface UserActivityDecscriptionBuilder {
    public UserActivityDecscriptionBuilder append(AnnotationJWT annotation, String data);
    public String build();
}
