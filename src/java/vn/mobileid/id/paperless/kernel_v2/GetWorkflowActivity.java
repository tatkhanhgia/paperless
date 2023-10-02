/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.database.DatabaseImpl_V2_WorkflowActivity;
import vn.mobileid.id.general.database.DatabaseV2_WorkflowActivity;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowActivity;

/**
 *
 * @author GiaTK
 */
public class GetWorkflowActivity {
    public static InternalResponse getWorkflowActivity(
            int WorkflowActivityId,
            String transactionId
    )throws Exception{
        DatabaseV2_WorkflowActivity callDb = new DatabaseImpl_V2_WorkflowActivity();
        
        DatabaseResponse response = callDb.getWorkflowActivity(WorkflowActivityId, transactionId);
        
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            transactionId));
        }
        
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                response.getObject()
        );
    }
    
    public static void main(String[] args) throws Exception{
        InternalResponse response = GetWorkflowActivity.getWorkflowActivity(
                650,
                "transactionId");
        
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            System.out.println("Mess:"+response.getMessage());
        } else {
            WorkflowActivity object = (WorkflowActivity)response.getData();
            System.out.println(object.getWorkflow_template_type());
            System.out.println(object.getCreated_at());
            System.out.println(object.getStatus());
//            System.out.println(object.getStatus_name());
//            System.out.println(object.getStatus_remark());
//            System.out.println(object.getStatus_remark_en());
        }
    }
}
