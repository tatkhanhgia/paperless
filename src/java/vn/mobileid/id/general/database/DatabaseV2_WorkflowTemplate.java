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
public interface DatabaseV2_WorkflowTemplate {
    public DatabaseResponse updateWorkflowTemplate(
            long pWORKFLOW_ID,
            String pUSER_EMAIL,
            long pENTERPRISE_ID,
            String pMETA_DATA_TEMPLATE,
            String pHMAC,
            String pLAST_MODIFIED_BY,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse getWorkflowTemplate(
            long pWORKFLOW_ID,
            String transactionId
    )throws Exception;
}
