/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.kernel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.objects.Asset;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;

/**
 *
 * @author GiaTK
 */
public class UploadAsset {
    final private static Logger LOG = LogManager.getLogger(UploadAsset.class);

    public static InternalResponse uploadAsset(User user,Asset asset){
        try {
            Database DB = new DatabaseImpl();
            //Data                        
            InternalResponse response = null;
                    
            DatabaseResponse callDB = DB.uploadAsset(
                    user.getEmail(),
                    asset.getType(),
                    asset.getName(),
                    asset.getSize(),
                    "UUID",
                    "DBMS_PROP",
                    asset.getMetadata(),
                    asset.getBinaryData(),
                    asset.getHmac(),
                    user.getName());
            
            if(callDB.getStatus() != QryptoConstant.CODE_SUCCESS ){              
                String message = null;
                if(LogHandler.isShowErrorLog()){
                    message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                                callDB.getStatus(),
                                "en"
                                , null);
                    LOG.error("Cannot get Asset - Detail:"+message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }                        
            
            return new InternalResponse(
                    QryptoConstant.CODE_SUCCESS,
                    callDB.getIDResponse());
            
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                e.printStackTrace();
                LOG.error("UNKNOWN EXCEPTION. Details: " + e);
            }            
            return new InternalResponse(500,QryptoConstant.INTERNAL_EXP_MESS);
        }  
    }

    public static void main(String[] ags) throws IOException{
//        User user = new User();
//        user.setEmail("giatk@mobile-id.vn");
//        user.setName("TAT KHANH GIA");
//        
//        String path = "D:\\NetBean\\QryptoServices\\file\\test.xslt";
//        File a = new File(path);
//        
//        Asset asset = new Asset(
//                0,
//                "Asset 1",
//                3,
//                (int)a.getUsableSpace(),
//                0,
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
