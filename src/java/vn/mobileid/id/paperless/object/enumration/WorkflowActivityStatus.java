/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

/**
 *
 * @author GiaTK
 */
public enum WorkflowActivityStatus {
    ACTIVE("ACTIVE",1),
    HIDDEN("HIDDEN",2),
    EXPIRED("EXPIRED",3),
    REVOKE("REVOKE",4);
    
    private int id;
    private String name;

    private WorkflowActivityStatus(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
