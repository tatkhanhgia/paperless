/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

/**
 *
 * @author GiaTK
 */

public class Enterprise extends Object{
    private int id;   //Id of enterprise
    private String name; //name of enterprise
    private String data; //Data Handshake
    private String fileP12_id;
    private int owner_id;
    private String clientID;
    private String clientSecret;
    private String email_notification;
    private byte[] fileP12_data;
    
    public Enterprise() {
    }   
    
    public Enterprise(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public Enterprise setData(String data) {
        this.data = data;
        return this;
    }

    public String getFileP12_id() {
        return fileP12_id;
    }

    public void setFileP12_id(String fileP12_id) {
        this.fileP12_id = fileP12_id;
    }
    
    
    public Enterprise build(){
        return this;
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

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getEmail_notification() {
        return email_notification;
    }

    public void setEmail_notification(String email_notification) {
        this.email_notification = email_notification;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public byte[] getFileP12_data() {
        return fileP12_data;
    }

    public void setFileP12_data(byte[] fileP12_data) {
        this.fileP12_data = fileP12_data;
    }
    
    
}
