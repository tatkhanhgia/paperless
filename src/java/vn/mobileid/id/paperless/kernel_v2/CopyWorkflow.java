/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Workflow;

/**
 *
 * @author GiaTK
 */
public class CopyWorkflow {
    //<editor-fold defaultstate="collapsed" desc="Copy Workflow">
    /**
     * Create new Workflow based on Old Workflow
     * @param workflowId
     * @param labelName
     * @param user
     * @param transactionId
     * @return long WorkflowId
     * @throws Exception 
     */
    public static InternalResponse copyWorkflow(
            int workflowId,
            String labelName,
            User user,
            String transactionId
    )throws Exception{
        //Get Workflow Old
        InternalResponse response = GetWorkflow.getWorkflow(workflowId, transactionId);
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){return response;}
        
        Workflow workflow = (Workflow) response.getData();
        
        //Processing create new Workflow
        Workflow newWorkflow = workflow.clone();
        newWorkflow.setLabel(labelName);
        response = CreateWorkflow.processingCreateWorkflowBasedOnWorkflowID(workflowId,newWorkflow, user, transactionId);
        
        return response;
    }
    //</editor-fold>
}
