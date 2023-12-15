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
public interface  DatabaseV2_Enterprise {
    public  DatabaseResponse getEnterpriseInfo(
            int enterprise_id,
            String enterprise_name
    )throws Exception;
    
    public DatabaseResponse getEnterpriseInfoOfUser(
            String email,
            String transaction_id) throws Exception;
    
    public DatabaseResponse getSigningInfoOfEnterprise(
            int enterprise_id,
            String transactionId
    )throws Exception;
}
