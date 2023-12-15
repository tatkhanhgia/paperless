/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

/**
 *
 * @author GiaTK
 */
public enum ItemsType {
    String(1),
    Boolean(2),
    Integer(3),
    Date(4),
    Binary(5);
    
    private int id;

    private ItemsType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
