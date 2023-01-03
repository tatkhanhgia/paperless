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
public class RegistrationParty {

    private int registrationPartyID;
    private String name;
    private String uri;
    private int defaultBy;

    public int getRegistrationPartyID() {
        return registrationPartyID;
    }

    public void setRegistrationPartyID(int registrationPartyID) {
        this.registrationPartyID = registrationPartyID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getDefaultBy() {
        return defaultBy;
    }

    public void setDefaultBy(int defaultBy) {
        this.defaultBy = defaultBy;
    }
}
