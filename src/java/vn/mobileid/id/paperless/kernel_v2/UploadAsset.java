/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import java.nio.file.Files;
import java.nio.file.Paths;
import vn.mobileid.id.general.database.DatabaseImpl_V2_Asset;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class UploadAsset {

    //<editor-fold defaultstate="collapsed" desc="upload Asset">
    /**
     * Upload một Asset mới
     * @param email
     * @param enterprise_id
     * @param type
     * @param file_name
     * @param size
     * @param UUID
     * @param pDBMS_PROPERTY
     * @param metaData
     * @param fileData
     * @param hmac
     * @param createdBy
     * @param transaction_id
     * @return
     * @throws Exception 
     */
    public static InternalResponse uploadAsset(
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
    ) throws Exception {
        DatabaseResponse response = new DatabaseImpl_V2_Asset().uploadAsset(
                email, enterprise_id, 
                type, 
                file_name, 
                size, 
                UUID,
                pDBMS_PROPERTY,
                metaData, fileData, 
                hmac, 
                createdBy,
                transaction_id);
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        response.getStatus(),
                        "en",
                        transaction_id)
            );
        }
        
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                (Object)response.getObject()
        );
    }
    //</editor-fold>
    
    public static void main(String[] args) throws Exception {
        String transactionId = "transactionId";
        //Upload Asset
        InternalResponse response = UploadAsset.uploadAsset(
                "khanhpx@mobile-id.vn",
                3,
                1,
                "Asset name Test",
                10,
                "UUID", 
                "DBMS PROPERTY", 
                "META DATA",
                Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Documents\\NetBeansProjects\\INTERNAL_PROJECT\\demoElaborcontract\\LAOS-TEMPLATE.xslt")), 
                "HMAC",
                "GIATK", 
                "transactionId");
        
        if(response.getStatus() !=PaperlessConstant.HTTP_CODE_SUCCESS){System.out.println("Message:"+response.getMessage());}
        else {System.out.println("Id:"+response.getData());}  
    }
}
