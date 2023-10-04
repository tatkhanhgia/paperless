/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

/**
 *
 * @author GiaTK
 */
public enum WorkflowActivityProductType {
    Production(1),
    Test(2),
    Update(3),;
    private int id;

    private WorkflowActivityProductType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
