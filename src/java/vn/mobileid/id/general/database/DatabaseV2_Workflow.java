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
public interface DatabaseV2_Workflow {
    public DatabaseResponse createWorkflow(
            String U_EMAIL,
            long pENTERPRISE_ID,
            int pTEMPLATE_TYPE,
            String pLABEL,
            String pHMAC,
            String pCREATED_BY,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse getWorkflow(
            long pWORKFLOW_ID,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse getRowCountOfWorkflow(
            String pUSER_EMAIL,
            long pENTERPRISE_ID,
            String pWORKFLOW_STATUS,
            String pLIST_TYPE,
            boolean pUSE_META_DATA,
            String pMETA_DATA,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse getListOfWorkflow(
            String pUSER_EMAIL,
            long pENTERPRISE_ID,
            String pWORKFLOW_STATUS,
            String pLIST_TYPE,
            boolean pUSE_META_DATA,
            String pMETA_DATA,
            int pOFFSET,
            int pROW_COUNT,
            String transactionId
    )throws Exception;
}
