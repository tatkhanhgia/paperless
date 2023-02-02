/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.kernel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.objects.Asset;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class GetAsset {

    final private static Logger LOG = LogManager.getLogger(GetAsset.class);

    public static InternalResponse getAsset(int id) {
        try {
            Database DB = new DatabaseImpl();
            //Data                        
            InternalResponse response = null;

            DatabaseResponse callDB = DB.getAsset(id);

            if (callDB.getStatus() != QryptoConstant.CODE_SUCCESS) {
                String message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                         null);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Cannot get Asset - Detail:" + message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            Asset asset = (Asset) callDB.getObject();

            return new InternalResponse(
                    QryptoConstant.HTTP_CODE_SUCCESS,
                    new ObjectMapper().writeValueAsString(asset));

        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
//            e.printStackTrace();
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static byte[] getAssetBackground(int id) {
        return null;
    }

    public static InternalResponse getAssetTemplate(int id) {
        try {
            Database DB = new DatabaseImpl();
            //Data                        
            InternalResponse response = null;

            DatabaseResponse callDB = DB.getAsset(id);

            if (callDB.getStatus() != QryptoConstant.CODE_SUCCESS) {
                String message = null;
                if (LogHandler.isShowErrorLog()) {
                    message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            callDB.getStatus(),
                            "en",
                             null);
                    LOG.error("Cannot get Asset - Detail:" + message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            Asset asset = (Asset) callDB.getObject();

            return new InternalResponse(
                    QryptoConstant.CODE_SUCCESS,
                    new ObjectMapper().writeValueAsString(asset));

        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static byte[] getAssetX(int id) {
        return null;
    }

    public static void main(String[] args) throws IOException {
//        Asset resposne = GetAsset.getAssetTemplate(7);        
//        Files.write(new File("D:\\NetBean\\QryptoServices\\file\\asset.xslt").toPath(), resposne.getBinaryData(), StandardOpenOption.CREATE);
    }
}
