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
public class IdentityDocumentType {

    private int id;
    private String name;

    public IdentityDocumentType() {
    }

    public IdentityDocumentType(int id, String name) {
        this.id = id;
        this.name = name;
    }

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

    final public static int ID_FEDERAL = 1;
    final public static int ID_PASSPORT = 2;
    final public static int ID_DRIVERLICENSE = 3;
    final public static int ID_IDENTITYCARD = 4;
    final public static int ID_CITIZENCARD = 5;
    final public static int ID_FACESCAN = 6;
    final public static int ID_FACEFRAME = 7;

    final public static String NAME_FEDERAL = "FEDERAL";
    final public static String NAME_PASSPORT = "PASSPORT";
    final public static String NAME_DRIVERLICENSE = "DRIVERLICENSE";
    final public static String NAME_IDENTITYCARD = "IDENTITYCARD";
    final public static String NAME_CITIZENCARD = "CITIZENCARD";
    final public static String NAME_FACESCAN = "FACESCAN";
    final public static String NAME_FACEFRAME = "FACEFRAME";

    public static int getID(String name) {
        switch (name) {
            case NAME_PASSPORT:
                return ID_PASSPORT;
            case NAME_DRIVERLICENSE:
                return ID_DRIVERLICENSE;
            case NAME_IDENTITYCARD:
                return ID_IDENTITYCARD;
            case NAME_CITIZENCARD:
                return ID_CITIZENCARD;
            case NAME_FACESCAN:
                return ID_FACESCAN;
            case NAME_FACEFRAME:
                return ID_FACEFRAME;
            default:
                return ID_FEDERAL;
        }
    }

    public static String getName(int id) {
        switch (id) {
            case ID_PASSPORT:
                return NAME_PASSPORT;
            case ID_DRIVERLICENSE:
                return NAME_DRIVERLICENSE;
            case ID_IDENTITYCARD:
                return NAME_IDENTITYCARD;
            case ID_CITIZENCARD:
                return NAME_CITIZENCARD;
            case ID_FACESCAN:
                return NAME_FACESCAN;
            case ID_FACEFRAME:
                return NAME_FACEFRAME;
            default:
                return NAME_FEDERAL;
        }
    }
}
