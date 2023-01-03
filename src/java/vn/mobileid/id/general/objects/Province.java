/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.objects;

import java.util.HashMap;

/**
 *
 * @author ADMIN
 */
public class Province {

    private HashMap<String, String> englishTable = new HashMap<>();
    private HashMap<String, String> vietnameseTable = new HashMap<>();
    private HashMap<String, String> nameToRemark = new HashMap<>();

    public HashMap<String, String> getEnglishTable() {
        return englishTable;
    }

    public void setEnglishTable(HashMap<String, String> englishTable) {
        this.englishTable = englishTable;
    }

    public HashMap<String, String> getVietnameseTable() {
        return vietnameseTable;
    }

    public void setVietnameseTable(HashMap<String, String> vietnameseTable) {
        this.vietnameseTable = vietnameseTable;
    }

    public HashMap<String, String> getNameToRemark() {
        return nameToRemark;
    }

    public void setNameToRemark(HashMap<String, String> nameToRemark) {
        this.nameToRemark = nameToRemark;
    }
}
