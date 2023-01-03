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
public class IdentityProcessType {
    
    final public static int TYPE_EMAILVERIFICATION = 2;
    final public static int TYPE_MOBILEVERIFICATION = 3;
    final public static int TYPE_DOCUMENT = 4;
    final public static int TYPE_DOCUMENTSELFIE = 5;
    final public static int TYPE_DOCUMENTVIDEO = 6;
    final public static int TYPE_PLUSVERIFICATION = 7;
    final public static int TYPE_LIVENESS = 8;
    final public static int TYPE_ICAODOC9303VERIFICATION = 9;

    final public static String EMAILVERIFICATION = "EMAILVERIFICATION";
    final public static String MOBILEVERIFICATION = "MOBILEVERIFICATION";
    final public static String DOCUMENT = "DOCUMENT";
    final public static String DOCUMENTSELFIE = "DOCUMENTSELFIE";
    final public static String DOCUMENTVIDEO = "DOCUMENTVIDEO";
    final public static String PLUSVERIFICATION = "PLUSVERIFICATION";
    final public static String LIVENESS = "LIVENESS";
    final public static String ICAODOC9303VERIFICATION = "ICAODOC9303VERIFICATION";

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
