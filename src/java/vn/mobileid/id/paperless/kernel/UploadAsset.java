/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.taglibs.standard.tag.el.sql.UpdateTag;
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

//    final private static Logger LOG = LogManager.getLogger(UploadAsset.class);

    public static InternalResponse uploadAsset(
            User user,
            Asset asset,
            String transactionID) {
        try {
            Database DB = new DatabaseImpl();
            //Upload Asset Template
//            Files.write(new File("D:\\NetBean\\qrypto\\file\\uploaded.xslt").toPath(), asset.getBinaryData(), StandardOpenOption.CREATE);
            Item_JSNObject item = XSLT_PDF_Processing.getValueFromXSLT(asset.getBinaryData());

            asset.setMetadata(new ObjectMapper().writeValueAsString(item));

            //Process file PDF
            asset.setSize(asset.getBinaryData().length);
            asset.setHmac("hmac");
            if (asset.getName() == null || asset.getName().equals("")) {
                asset.setName("PDF -" + user.getEmail());
            }

            //WWrite into DB
            InternalResponse response = null;

            DatabaseResponse callDB = DB.uploadAsset(
                    user.getEmail(),
                    3, //enterprise_id
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
                String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                         null);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(UploadAsset.class,transactionID,"Cannot Upload Asset - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            return new InternalResponse(
                    PaperlessConstant.CODE_SUCCESS,
                    "{\"asset_id\":" + callDB.getIDResponse()+"}");

        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                e.printStackTrace();
                LogHandler.error(UploadAsset.class,transactionID,"UNKNOWN EXCEPTION. Details: " + e);
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
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
