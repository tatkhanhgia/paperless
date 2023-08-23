/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.Item_JSNObject;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.utils.XSLT_PDF_Processing;

/**
 *
 * @author GiaTK
 */
public class UploadAsset {

    /**
     * Upload Asset
     *
     * @param user
     * @param asset
     * @param transactionID
     * @return
     * @throws Exception
     */
    public static InternalResponse uploadAsset(
            User user,
            Asset asset,
            String transactionID) throws Exception {

        Database DB = new DatabaseImpl();
        //Upload Asset Template
        Item_JSNObject item = XSLT_PDF_Processing.getValueFromXSLT(asset.getBinaryData());
        
        if(item == null){
            return new InternalResponse(
                PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                "{\"Error\":\"Cannot parse XSLT from file\"}");
        }
        asset.setMetadata(new ObjectMapper().writeValueAsString(item));

        //Process file PDF
        asset.setSize(asset.getBinaryData().length);
        asset.setHmac("hmac");
        if (asset.getName() == null || asset.getName().isEmpty()) {
            asset.setName("PDF -" + user.getEmail());
        }

        //WWrite into DB        
        DatabaseResponse callDB = DB.uploadAsset(
                user.getEmail(),
                user.getAid(), //enterprise_id
                asset.getType(),
                asset.getName(),
                asset.getSize(),
                "UUID",
                "DBMS_PROP",
                asset.getMetadata(),
                asset.getBinaryData(),
                asset.getHmac(),
                user.getName(),
                transactionID);

        if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_FAIL,
                    callDB.getStatus(),
                    "en",
                    null);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "{\"asset_id\":" + callDB.getIDResponse() + "}");
    }

    public static void main(String[] ags) throws IOException {
//        User user = new User();
//        user.setEmail("giatk@mobile-id.vn");
//        user.setName("TAT KHANH GIA");
//        
//        String path = "D:\\NetBean\\qrypto\\file\\result.xslt";
//        File a = new File(path);
//        
//        Asset asset = new Asset(
//                0,
//                "Asset 20230223",
//                3,
//                (int)a.getUsableSpace(),
//                "fileUUID",
//                null,
//                user.getEmail(),
//                null,
//                null,
//                null,
//                Files.readAllBytes(new File(path).toPath()),
//                "meta data upload");
//        
//        UploadAsset.uploadAsset(user, asset);
    }
}
