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
public interface DatabaseV2_Transaction {
    public  DatabaseResponse createTransaction(
            String U_EMAIL,
            long pLOG_ID,
            long pOBJECT_ID,
            int pOBJECT_TYPE,
            String pIP_ADDRESS,
            String pMETADATA,
            String pDESCRIPTION,
            String pHMAC,
            String pCREATED_BY,
            String transactionId
    )throws Exception;
}
