/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

/**
 *
 * @author GiaTK
 */
public enum Category {
    Account(1),
    Workflow(2),
    FileStamping(3),
    Asset(4),
    Setting(5),
    WorkflowActivity(6);
    
    private int id;

    private Category(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
