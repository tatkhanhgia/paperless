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
public interface  DatabaseV2_CSV {
    public DatabaseResponse createCSVTask(
            String pNAME,
            String pUUID,
            String pDMS_PROPERTY,
            int pPAGES,
            long pSIZE,
            String pMETA_DATA,
            String pHMAC,
            String pCREATED_BY,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse getCSVTask(
            long pCSV_TASK_ID,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse updateCSVTask(
            long pCSV_TASK_ID,
            String pNAME,
            String pUUID,
            String pDMS_PROPERTY,
            int pPAGES,
            long pSIZE,
            String pMETA_DATA,
            String pHMAC,
            String pLAST_MODIFIED_BY,
            String transactionId
    )throws Exception;
}
