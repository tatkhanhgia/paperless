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
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class GetAsset {

//    final private static Logger LOG = LogManager.getLogger(GetAsset.class);

    public static InternalResponse getAsset(int id,
            String transactionID) {
        try {
            Database DB = new DatabaseImpl();
            //Data                        
            InternalResponse response = null;

            DatabaseResponse callDB = DB.getAsset(id, transactionID);

            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                         null);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(GetAsset.class,
                            "TransactionID:"+transactionID+
                            "\nCannot get Asset - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            Asset asset = (Asset) callDB.getObject();

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    asset);

        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(GetAsset.class,
                        "TransactionID:"+transactionID+
                        "\nUNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
//            e.printStackTrace();
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }   
    
    
    public static InternalResponse getAssetTemplate(int id,
            String transactionID) {
        try {
            Database DB = new DatabaseImpl();
            //Data                        
            InternalResponse response = null;

            DatabaseResponse callDB = DB.getAsset(id, transactionID);

            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                if (LogHandler.isShowErrorLog()) {
                    message = QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            callDB.getStatus(),
                            "en",
                             null);
                    LogHandler.error(GetAsset.class,
                            "TransactionID:"+transactionID+
                            "\nCannot get Asset - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            Asset asset = (Asset) callDB.getObject();

            return new InternalResponse(
                    PaperlessConstant.CODE_SUCCESS,
                    asset.getMetadata());

        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(GetAsset.class,
                        "TransactionID:"+transactionID+
                        "\nUNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }            
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
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
