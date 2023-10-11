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
public interface DatabaseV2_QR {
    public DatabaseResponse createQR(
            String pMETA_DATA,
            String pIMAGE,
            String pHMAC,
            String pCREATED_BY,
            String transaction_id
    ) throws Exception;
    
    public DatabaseResponse getListQRSize(
            String transactionId
    )throws Exception;
    
    public DatabaseResponse getListQRType(
            String transactionId
    )throws Exception;
    
    public DatabaseResponse updateQR(
            long pQR_ID,
            String pMETA_DATA,
            String pIMAGE,
            String pLAST_MODIFIED_BY,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse getQR(
            long pQR_ID,
            String transactionId
    )throws Exception;
}
