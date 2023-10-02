/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.database.DatabaseImpl_V2_Workflow;
import vn.mobileid.id.general.database.DatabaseV2_Workflow;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class CreateWorkflow {
    
    //<editor-fold defaultstate="collapsed" desc="Create new Workflow">
    /**
     * Create new Workflow 
     * @param U_EMAIL
     * @param pENTERPRISE_ID
     * @param pTEMPLATE_TYPE
     * @param pLABEL
     * @param pHMAC
     * @param pCREATED_BY
     * @param transactionId
     * @return long WorkflowId
     * @throws Exception 
     */
    public static InternalResponse processingCreateWorkflow(
            String U_EMAIL,
            long pENTERPRISE_ID,
            int pTEMPLATE_TYPE,
            String pLABEL, 
            String pHMAC, 
            String pCREATED_BY,
            String transactionId
    )throws Exception{
        DatabaseV2_Workflow callDb = new DatabaseImpl_V2_Workflow();
        
        DatabaseResponse response = callDb.createWorkflow(
                U_EMAIL, 
                pENTERPRISE_ID,
                pTEMPLATE_TYPE,
                pLABEL,
                pHMAC,
                pCREATED_BY, 
                transactionId);
        
        if(response.getStatus()!=PaperlessConstant.CODE_SUCCESS){
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            null)
            );
        }
        
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                response.getObject()
        );
    }
    //</editor-fold>
    
    public static void main(String[] args)throws Exception {
        InternalResponse response = CreateWorkflow.processingCreateWorkflow(
                "khanhpx@mobile-id.vn", 
                3, 
                7, 
                "Label 20231002", 
                "hmac", 
                "GiaTK", 
                "transactionId");
        
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            System.out.println("Mess:"+response.getMessage());
        } else{
            System.out.println("Id:"+response.getData());
        }
    }
}
