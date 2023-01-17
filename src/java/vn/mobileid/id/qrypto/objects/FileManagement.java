/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.objects;

/**
 *
 * @author GiaTK
 */
public class FileManagement extends Object{
    private byte[] data;
    private String name;
    private String ID;
    private String created_by;

    public FileManagement(byte[] data, String name, String ID, String created_by) {
        this.data = data;
        this.name = name;
        this.ID = ID;
        this.created_by = created_by;
    }

    public FileManagement(byte[] data, String name, String ID) {
        this.data = data;
        this.name = name;
        this.ID = ID;
    }

    
    
    public FileManagement() {
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }
    
    
}
