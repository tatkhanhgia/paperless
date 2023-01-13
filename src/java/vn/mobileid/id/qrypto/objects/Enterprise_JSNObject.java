/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.objects;

/**
 *
 * @author GiaTK
 */

public class Enterprise_JSNObject extends Object{
    private String data;
    private String fileP12_id;
    
    public Enterprise_JSNObject() {
    }   
    
    public Enterprise_JSNObject(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public Enterprise_JSNObject setData(String data) {
        this.data = data;
        return this;
    }

    public String getFileP12_id() {
        return fileP12_id;
    }

    public void setFileP12_id(String fileP12_id) {
        this.fileP12_id = fileP12_id;
    }
    
    
    public Enterprise_JSNObject build(){
        return this;
    }
}
