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
public interface DatabaseV2_Asset {

    public DatabaseResponse uploadAsset(
            String email,
            int enterprise_id,
            int type,
            String file_name,
            long size,
            String UUID,
            String pDBMS_PROPERTY,
            String metaData,
            byte[] fileData,
            String hmac,
            String createdBy,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse deleteAsset(
            int id,
            String email,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse getAsset(
            int assetID,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse getTotalRecordsAsset(
            int enterpriseId,
            String email,
            String fileName,
            String type,
            String status,
            String transactionId
    ) throws Exception;

    public DatabaseResponse getListAsset(
            int ent_id,
            String email,
            String file_name,
            String type,
            String status,
            int offset,
            int rowcount,
            String transactionID
    ) throws Exception;

    public DatabaseResponse updateAsset(
        int pASSET_ID,
	String pUSER_EMAIL,
	String pFILE_NAME ,
	int pASSET_TYPE,
	long pSIZE,
	String pUSED_BY,
	String pUUID,
	String pDMS_PROPERTY,
	String pMETA_DATA,
	byte[] pBINARY_DATA,
	String pHMAC,
	String pLAST_MODIFIED_BY,
        String transactionId
    ) throws Exception;
}
