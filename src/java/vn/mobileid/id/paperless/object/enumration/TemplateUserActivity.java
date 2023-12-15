/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

import vn.mobileid.id.general.PolicyConfiguration;

/**
 *
 * @author GiaTK
 */
public enum TemplateUserActivity {
    createWorkflow("createWorkflow"),
    createWorkflowActivity("createWorkflowActivity"),
    createChildWoAcOfCSV("childCSV");

    private String name;

    private TemplateUserActivity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static String getTemplateUserActivity(TemplateUserActivity tmp) {
        switch (tmp.getName()) {
            case "createWorkflowActivity": {
                return PolicyConfiguration.getInstant().getTemplateUserActivity().getAttributes().get(0).getCreateWorkflowActivity();
            }
            case "childCSV":{
                return PolicyConfiguration.getInstant().getTemplateUserActivity().getAttributes().get(0).getCreateChildOfWorkflowActivityCSV();
            }
            default :
                return "";
        }
    }
}
