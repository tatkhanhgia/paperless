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
}
