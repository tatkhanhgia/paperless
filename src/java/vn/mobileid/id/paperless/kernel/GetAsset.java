/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import java.io.IOException;
import java.util.List;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class GetAsset {

    public static InternalResponse getAsset(
            int id,
            String transactionID) throws Exception {
        
            Database DB = new DatabaseImpl();
            //Data                        
            InternalResponse response = null;

            DatabaseResponse callDB = DB.getAsset(id, transactionID);

            try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(GetAsset.class,
                            "TransactionID:" + transactionID
                            + "\nCannot get Asset - Detail:" + message);
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
            throw new Exception("Cannot get Asset!", e);
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse getAssetTemplate(
            int id,
            String transactionID) throws Exception {
        
            Database DB = new DatabaseImpl();
            //Data                        
            InternalResponse response = null;

            DatabaseResponse callDB = DB.getAsset(id, transactionID);

            try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                if (LogHandler.isShowErrorLog()) {
                    message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            callDB.getStatus(),
                            "en",
                            null);
                    LogHandler.error(GetAsset.class,
                            "TransactionID:" + transactionID
                            + "\nCannot get Asset - Detail:" + message);
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
            throw new Exception("Cannot get Asset Template!", e);
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse getListAsset(
            int ent_id,
            String email,
            String file_name,
            String type,
            String transactionID
    ) throws Exception{
        
            Database DB = new DatabaseImpl();
            //Data                        
            InternalResponse response = null;

            DatabaseResponse callDB = DB.getListAsset(
                    ent_id,
                    email,
                    file_name,
                    type,
                    0,
                    PaperlessConstant.DEFAULT_ROW_COUNT,
                    transactionID);

            try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(GetAsset.class,
                            "TransactionID:" + transactionID
                            + "\nCannot get Asset - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            List<Asset> listAsset = (List<Asset>) callDB.getObject();

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    listAsset);

        } catch (Exception e) {
            throw new Exception("Cannot get List of Asset!", e);
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static void main(String[] args) throws IOException, Exception {
//        Asset resposne = GetAsset.getAssetTemplate(7);        
//        Files.write(new File("D:\\NetBean\\QryptoServices\\file\\asset.xslt").toPath(), resposne.getBinaryData(), StandardOpenOption.CREATE);
        
        List<Asset> listAsset =( List<Asset>) GetAsset.getListAsset(
                3,
                "khanhpx@mobile-id.vn",
                null,
                null,
                "transactionID").getData();
        
//        listAsset.forEach((value)->{
//            System.out.println(value.getCreated_at());
//        });
//        CustomWorkflowActivitySerializer tempp = new CustomWorkflowActivitySerializer(listAsset,0,0);
//      
////        SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1,0,0,null));
////        simpleModule.addSerializer();
//        
//        String temp = new ObjectMapper().writeValueAsString(tempp);
//      
//        System.out.println("Temp:"+temp);
    }
}
