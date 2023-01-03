/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.objects;

/**
 *
 * @author ADMIN
 * Time Stamp Authority
 */
public class TSAProfile {

    private int id;
    private String name;
    private TSAProfileProperties tsaProfileProperties;

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

    public TSAProfileProperties getTsaProfileProperties() {
        return tsaProfileProperties;
    }

    public void setTsaProfileProperties(TSAProfileProperties tsaProfileProperties) {
        this.tsaProfileProperties = tsaProfileProperties;
    }

}
