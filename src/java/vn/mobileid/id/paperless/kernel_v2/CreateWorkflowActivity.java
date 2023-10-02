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
import vn.mobileid.id.paperless.object.enumration.DownloadLinkType;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class CreateWorkflowActivity {
    public static InternalResponse createWorkflowActivity(
            int pENTERPRISE_ID,
            int pWORKFLOW_ID,
            String pUSER_EMAIL,
            String pTRANSACTION_ID,
            String pDOWNLOAD_LINK,
            int pDOWNLOAD_LINK_TYPE,
            String pREMARK,
            int pPRODUCTION_TYPE,
            int pWORKFLOW_TYPE,
            String pREQUEST_DATA,
            String pHMAC,
            String pCREATED_BY,
            String transactionId
    )throws Exception{
        DatabaseV2_WorkflowActivity callDb = new DatabaseImpl_V2_WorkflowActivity();
        
        DatabaseResponse response = callDb.createWorkflowActivity(
                pENTERPRISE_ID, 
                pWORKFLOW_ID,
                pUSER_EMAIL,
                pTRANSACTION_ID,
                pDOWNLOAD_LINK,
                pDOWNLOAD_LINK_TYPE, 
                pREMARK, 
                pPRODUCTION_TYPE,
                pWORKFLOW_TYPE,
                pREQUEST_DATA, 
                pHMAC, 
                pCREATED_BY,
                transactionId);
        
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        response.getStatus(),
                        "en",
                        transactionId)
            );
        }
        
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                response.getObject()
        );
    }
    
    public static void main(String[] args) throws Exception{
        InternalResponse response = CreateWorkflowActivity.createWorkflowActivity(
                3,
                488,
                "khanhpx@mobile-id.vn",
                "99-76",
                "Download Link Name",
                DownloadLinkType.PDF.getId(),
                "Description",
                1, 
                7,
                "Request Data",
                "hmac",
                "GiaTK",
                "transactionId");
        
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            System.out.println("Mess:"+response.getMessage());
        } else {
            System.out.println("Id:"+response.getData());
        }
    }
}
