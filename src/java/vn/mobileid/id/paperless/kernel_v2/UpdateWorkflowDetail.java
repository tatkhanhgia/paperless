/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import java.util.ArrayList;
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
public class UpdateWorkflowDetail {
    //<editor-fold defaultstate="collapsed" desc="Update Workflow Detail">
    public static InternalResponse updateWorkflowDetail(
            long W_ID,
            String pUSER_EMAIL,
            long pENTERPRISE_ID,
            List<WorkflowAttributeType> types,
            String transactionId
    )throws Exception{
        DatabaseV2_WorkflowDetails callDb = new DatabaseImpl_V2_WorkflowDetails();
        for(WorkflowAttributeType temp : types){
            DatabaseResponse response = callDb.updateWorkflowDetail(
                    W_ID,
                    pUSER_EMAIL,
                    pENTERPRISE_ID, 
                    temp.getId(), 
                    temp.getValue(),
                    "Hmac",
                    pUSER_EMAIL,
                    transactionId);
            
            if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            transactionId)
            );
            }
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>
    
    public static void main(String[] args) throws Exception{
        WorkflowAttributeType attribute = new WorkflowAttributeType();
        attribute.setId(1);
        attribute.setValue(22);
        
        WorkflowAttributeType attribute2 = new WorkflowAttributeType();
        attribute2.setId(3);
        attribute2.setValue(false);
        
        List<WorkflowAttributeType> list = new ArrayList<>();
        list.add(attribute);
        list.add(attribute2);
        
        InternalResponse response = UpdateWorkflowDetail.updateWorkflowDetail(
                491,
                "khanhpx@mobile-id.vn",
                3, 
                list,
                "transactionId");
        
        System.out.println("Mess:"+response.getMessage());
    }
}
