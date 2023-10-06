/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.database.DatabaseImpl_V2_Asset;
import vn.mobileid.id.general.database.DatabaseV2_Asset;
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
    //<editor-fold defaultstate="collapsed" desc="Update Asset (Constructor - All data of Asset)">
    /**
     * Cập nhật thông tin Asset
     * @param assetId
     * @param userEmail
     * @param fileName
     * @param assetType
     * @param size
     * @param usedBy
     * @param uuid
     * @param dms
     * @param metadata
     * @param binary
     * @param hmac
     * @param modifiedBy
     * @param transactionId
     * @return
     * @throws Exception 
     */
   public static InternalResponse updateAsset(
           int assetId,
           String userEmail,
           String fileName,
           int assetType,
           long size,
           String usedBy,
           String uuid,
           String dms,
           String metadata,
           byte[] binary,
           String hmac,
           String modifiedBy,
           String transactionId
   )throws Exception{
       DatabaseV2_Asset callDB = new DatabaseImpl_V2_Asset();
       DatabaseResponse response = callDB.updateAsset(
               assetId, 
               userEmail, 
               fileName, 
               assetType, 
               size, 
               usedBy, 
               uuid, 
               dms,
               metadata, 
               binary, 
               hmac, 
               modifiedBy,
               transactionId);
       if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            transactionId)
            );
        }

        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, "");
   } 
   //</editor-fold>
   
    //<editor-fold defaultstate="collapsed" desc="Update Asset (Constructor - Asset + User + transactionId)">
   public static InternalResponse updateAsset(
            Asset asset,
            User user,
            String transactionID
    ) throws Exception{
       return updateAsset(
               asset.getId(),
               user.getEmail(),
               asset.getName(),
               asset.getType(), 
               asset.getSize(),
               asset.getUsed_by(),
               asset.getFile_uuid(), 
               asset.getDbms(), 
               asset.getMetadata(), 
               asset.getBinaryData(), 
               asset.getHmac(), 
               user.getName() == null ? user.getEmail() : user.getName(), 
               transactionID);
   }
    //</editor-fold>
   
    public static void main(String[] args)throws Exception {
        InternalResponse response = UpdateAsset.updateAsset(
                25,
                "khanhpx@mobile-id.vn",
                null,
                1,
                1,//type
                null,//used by
                "uuid",
                "dms",
                "metadata", 
                null,
                null,
                "GiaTK",
                "transactionId");
        
        System.out.println("Status:"+response.getStatus());
        System.out.println("Mess:"+response.getMessage());
    }
}
