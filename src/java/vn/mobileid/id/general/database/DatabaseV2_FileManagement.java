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
            String UUID,
            String name,
            int pages,
            int size,
            float width,
            float height,
            int uuid,
            String HMAC,
            String created_by,
            String DBMS,
            String file_type,
            String signing_properties,
            String hash_values,
            String transaction_id
    ) throws Exception;
}
