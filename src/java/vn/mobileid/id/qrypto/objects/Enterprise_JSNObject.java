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

    //Data RSSP
    private String RP_name ;
    private String RP_user;
    private String RP_pass;
    private String RP_signature;
    private String RP_keystorefile;
    private String RP_keystorePass;
    
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
    
    public Enterprise_JSNObject build(){
        return this;
    }
}
