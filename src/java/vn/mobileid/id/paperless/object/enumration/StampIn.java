/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

/**
 *
 * @author GiaTK
 */
public enum StampIn {
    None(0),
    Last_Page(1),
    All_Page(2);
    
    private int id;

    private StampIn(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
