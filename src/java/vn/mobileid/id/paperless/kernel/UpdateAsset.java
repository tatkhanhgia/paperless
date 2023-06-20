/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class UpdateAsset {
    public static InternalResponse updateAsset(
            int id,
            String email,
            String file_name,
            int asset_type,
            long size,
            String used_by,
            String uuid,
            String dms,
            String meta_data,
            byte[] binary_data,
            String hmac,
            String modified_by,
            String transaction_id
    ) throws Exception {
        Database callDB = new DatabaseImpl();
        DatabaseResponse response = callDB.updateAsset(
                id,
                email,
                file_name,
                asset_type,
                size,                
                uuid,
                dms,
                meta_data,
                binary_data,
                hmac,
                modified_by,
                used_by,
                transaction_id);
        
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
            String message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_FAIL,
                    response.getStatus(),
                    "en",
                    null);            
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }
        
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }

    public static InternalResponse updateAsset(
            Asset asset,
            User user,
            String transactionID
    ) throws Exception
    {
        Database callDB = new DatabaseImpl();
        DatabaseResponse response = callDB.updateAsset(
                asset.getId(),
                user.getEmail(),
                asset.getName(),
                asset.getType(),
                asset.getSize(),
                asset.getName(),
                asset.getDbms(),
                asset.getMetadata(),
                asset.getBinaryData(),
                asset.getHmac(),
                user.getEmail(),
                asset.getUsed_by(),
                transactionID);
        
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
            String message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_FAIL,
                    response.getStatus(),
                    "en",
                    null);            
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }
        
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
}
