/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.database;

import vn.mobileid.id.general.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public interface  DatabaseV2_WorkflowActivity {
    public DatabaseResponse createWorkflowActivity(
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
    ) throws Exception;
    
     public DatabaseResponse getWorkflowActivity(
            int id,
            String transaction_id)
            throws Exception;
}
