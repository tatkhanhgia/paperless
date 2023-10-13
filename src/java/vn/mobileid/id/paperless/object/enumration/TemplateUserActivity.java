/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

/**
 *
 * @author GiaTK
 */
public enum TemplateUserActivity {
    createWorkflow("createWorkflow"),
    createWorkflowActivity("createWorkflowActivity");
    
    private String name;

    private TemplateUserActivity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
