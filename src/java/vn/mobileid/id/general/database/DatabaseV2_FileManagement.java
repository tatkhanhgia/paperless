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
public interface  DatabaseV2_FileManagement {
    public DatabaseResponse createFileManagement(
            String name,
            int pages,
            int size,
            float width,
            float height,
            String uuid,
            String HMAC,
            String created_by,
            String DBMS,
            String file_type,
            String signing_properties,
            String hash_values,
            String transaction_id
    ) throws Exception;
    
    public DatabaseResponse getFileManagement(
            long fileManagementId,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse updateFileManagement(
            long id,
            String UUID,
            String DBMS,
            String name,
            int pages,
            long size,
            float width,
            float height,
            int status,
            String hmac,
            String created_by,
            String last_modified_by,
            boolean isSigned,
            String file_type,
            String signing_properties,
            String hash_values,
            String transactionID
    )throws Exception;
}
