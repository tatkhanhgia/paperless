/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.database.DatabaseImpl_V2_Asset;
import vn.mobileid.id.general.database.DatabaseV2_Asset;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class DeleteAsset {
    //<editor-fold defaultstate="collapsed" desc="delete Asset">
    /**
     * Xóa một Asset
     * @param assetId
     * @param email
     * @param transactionId
     * @return
     * @throws Exception 
     */
    public static InternalResponse deleteAsset(
            int assetId,
            String email,
            String transactionId
    )throws Exception{
        DatabaseV2_Asset callDB = new DatabaseImpl_V2_Asset();
        DatabaseResponse response = callDB.deleteAsset(
                assetId,
                email,
                transactionId);
        
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        response.getStatus(),
                        "en",
                        transactionId)
            );
        }
        
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,""
        );
    }
    //</editor-fold>
    
    public static void main(String[] args) throws Exception {
        //Delete Asset
        InternalResponse response = GetAsset.getAsset(25, "trấnctionis");

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            System.out.println("Message:" + response.getMessage());
        } else {
            Asset temp = (Asset)response.getData();
            System.out.println("Asset:"+temp.getName());
            System.out.println("Data:"+temp.getBinaryData().length);
        }
    }
}
