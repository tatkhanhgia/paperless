/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.database.DatabaseImpl_V2_Workflow;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.Workflow;

/**
 *
 * @author GiaTK
 */
public class UpdateWorkflow {
    /**
     * Update workflow
     *
     * @param workflow - Workflow
     * @param user - User
     * @param transactionID
     * @return String / ID of that Workflow
     * @throws Exception
     */
    public static InternalResponse updateWorkflow(
            Workflow workflow,
            User user,
            String transactionID
    ) throws Exception {
        DatabaseResponse callDB = new DatabaseImpl_V2_Workflow().updateWorkflow(
                user.getEmail(),
                user.getAid(),
                workflow.getWorkflow_id(),
                workflow.getLabel(),
                "hmac",
                user.getName() == null ? user.getEmail() : user.getName(),
                transactionID);

        if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            callDB.getStatus(),
                            "en",
                            null)
            );
        }       

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
               ""
        );
    }
    
    public static void main(String[] args) throws Exception {
        Workflow workflow = new Workflow();
        workflow.setWorkflow_id(507);
        workflow.setLabel("Label testsdsd");
        
        User user = new User();
        user.setAid(3);
        user.setEmail("khanhpx@mobile-id.vn");
        
        InternalResponse response = UpdateWorkflow.updateWorkflow(
                workflow,
                user,
                "transactionId");
        
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            System.out.println("Message:"+response.getMessage());
        } else {
            System.out.println("Message:"+response.getMessage());
        }
    }
}