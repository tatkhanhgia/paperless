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
public class IdentityProvider {

    final public static int ID_FEDERAL = 1;
    final public static int ID_FACETEC = 2;
    final public static int ID_MOBILE_ID = 3;
    final public static int ID_ICAODOC9303PROVIDER = 4;
    final public static int ID_NEUROTECH = 5;

    final public static String NAME_FEDERAL = "FEDERAL";
    final public static String NAME_FACETEC = "FACETEC";
    final public static String NAME_MOBILE_ID = "MOBILE-ID";
    final public static String NAME_ICAODOC9303PROVIDER = "ICAODOC9303PROVIDER";
    final public static String NAME_NEUROTECH = "NEUROTECH";

    public static int getID(String name) {
        switch (name) {
            case NAME_FACETEC:
                return ID_FACETEC;
            case NAME_MOBILE_ID:
                return ID_MOBILE_ID;
            case NAME_ICAODOC9303PROVIDER:
                return ID_ICAODOC9303PROVIDER;
            case NAME_NEUROTECH:
                return ID_NEUROTECH;
            default:
                return ID_FEDERAL;

        }
    }

    public static String getName(int id) {
        switch (id) {
            case ID_FACETEC:
                return NAME_FACETEC;
            case ID_MOBILE_ID:
                return NAME_MOBILE_ID;
            case ID_ICAODOC9303PROVIDER:
                return NAME_ICAODOC9303PROVIDER;
            case ID_NEUROTECH:
                return NAME_NEUROTECH;
            default:
                return NAME_FEDERAL;
        }
    }

    private int id;
    private String name;
    private IdentityProviderProperties identityProviderProperties;

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

    public IdentityProviderProperties getIdentityProviderProperties() {
        return identityProviderProperties;
    }

    public void setIdentityProviderProperties(IdentityProviderProperties identityProviderProperties) {
        this.identityProviderProperties = identityProviderProperties;
    }

}
