/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import java.util.List;
import vn.mobileid.id.general.database.DatabaseImpl_V2_WorkflowDetails;
import vn.mobileid.id.general.database.DatabaseV2_WorkflowDetails;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;

/**
 *
 * @author GiaTK
 */
public class GetWorkflowDetails {
    //<editor-fold defaultstate="collapsed" desc="Get Workflow Attribute Type">
    public static InternalResponse getWorkflowAttributeType()throws Exception{
        DatabaseResponse response = new DatabaseImpl_V2_WorkflowDetails().getWorkflowDetailAttributeTypes();
        
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            "hallo"));
        }
        
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                response.getObject()
        );
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Get Workflow Detail">
    /**
     * Get All Workflow Attribute Type of Workflow
     * @param workflowId
     * @param transactionId
     * @return List<WorkflowAttributeType>
     * @throws Exception 
     */
    public static InternalResponse getWorkflowDetail(
            long workflowId,
            String transactionId
    )throws Exception{
        DatabaseV2_WorkflowDetails callDb = new DatabaseImpl_V2_WorkflowDetails();
        DatabaseResponse response = callDb.getWorkflowDetail(
                workflowId,
                transactionId);
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            "hallo"));
        }
        
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                response.getObject()
        );
    }
    //</editor-fold>
    
    public static void main(String[] args) throws Exception{
//        InternalResponse response = GetWorkflowDetails.getWorkflowAttributeType();
//        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
//            System.out.println("Mess:"+response.getMessage());
//        } else {
//            for(WorkflowAttributeType temp : (List<WorkflowAttributeType>)response.getData()){
//                System.out.println("Name:"+temp.getName());
//                System.out.println("Id:"+temp.getId());
//            }
//        }

        InternalResponse response = GetWorkflowDetails.getWorkflowDetail(
                1262,
                "transactionId");
        
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            System.out.println("Mess:"+response.getMessage());
        } else {
            for(WorkflowAttributeType temp : (List<WorkflowAttributeType>)response.getData()){
                System.out.println("id:"+temp.getId());
                System.out.println("name:"+temp.getName());
                System.out.println("value:"+temp.getValue());
            }
        }
    }    
}
