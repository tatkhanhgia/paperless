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
public interface  DatabaseV2_WorkflowDetails{
    public DatabaseResponse createWorkflowDetail(
            long W_ID,
            long pWORKFLOW_ATTRIBUTE_TYPE,
            Object pVALUE,
            String pHMAC,
            String pCREATED_BY,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse getWorkflowDetailAttributeTypes()throws Exception;

    public DatabaseResponse getWorkflowDetail(
            long pWORKFLOW_ID,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse updateWorkflowDetail(
            long W_ID,
            String pUSER_EMAIL,
            long pENTERPRISE_ID,
            long pWORKFLOW_ATTRIBUTE_TYPE,
            Object pVALUE,
            String pHMAC,
            String pLAST_MODIFIED_BY,
            String transactionId
    )throws Exception;
}

