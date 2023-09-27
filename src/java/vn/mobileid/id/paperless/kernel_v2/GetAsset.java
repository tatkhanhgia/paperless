/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import java.util.HashMap;
import java.util.List;
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
public class GetAsset {

    //<editor-fold defaultstate="collapsed" desc="get Asset">
    /**
     * Lấy về giá trị một Asset thông qua Id
     *
     * @param assetId
     * @param transactionId
     * @return Asset
     * @throws Exception
     */
    public static InternalResponse getAsset(
            int assetId,
            String transactionId
    ) throws Exception {
        DatabaseV2_Asset callDB = new DatabaseImpl_V2_Asset();
        DatabaseResponse response = callDB.getAsset(assetId, transactionId);

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

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS, response.getObject()
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Total Record Asset">
    /**
     * Lấy về tổng số row các record của Asset thông qua dữ liệu truyền vào
     *
     * @param enterpriseId
     * @param email
     * @param fileName
     * @param type
     * @param status
     * @param transactionId
     * @return trả về Long number
     * @throws Exception
     */
    public static InternalResponse getTotalRecordAsset(
            int enterpriseId,
            String email,
            String fileName,
            String type,
            String status,
            String transactionId
    ) throws Exception {
        DatabaseV2_Asset callDB = new DatabaseImpl_V2_Asset();
        DatabaseResponse response = callDB.getTotalRecordsAsset(
                enterpriseId,
                email,
                fileName,
                type,
                status,
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

        List<HashMap<String, Object>> rows = response.getRows();
        long rowCount = (long) rows.get(0).get("ROW_COUNT");

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS, (Object) rowCount
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="get List Asset">
    public static InternalResponse getListAsset(
            int ent_id,
            String email,
            String file_name,
            String type,
            String status,
            int offset,
            int rowcount,
            String transactionId
    ) throws Exception {
        DatabaseV2_Asset callDb = new DatabaseImpl_V2_Asset();
        DatabaseResponse response = callDb.getListAsset(
                ent_id,
                email,
                file_name,
                type,
                status,
                offset,
                rowcount,
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

        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, response.getObject());
    }
    //</editor-fold>

    public static void main(String[] args) throws Exception {
//        //Get Total Record Asset
//        InternalResponse response = GetAsset.getTotalRecordAsset(
//                3,
//                "khanhpx@mobile-id.vn",
//                null,
//                "1,2,3",
//                "1,2",
//                "transactionId");
//
//        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//            System.out.println("Error:" + response.getMessage());
//        } else {
//            long number = (long)response.getData();
//            System.out.println("Number:"+number);
//        }

        //Get Total Record Asset
        InternalResponse response = GetAsset.getListAsset(
                3,
                "khanhpx@mobile-id.vn",
                null,
                "1,2,3",
                "1,2,3",
                0,
                100,
                "transactionUId");

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            System.out.println("Error:" + response.getMessage());
        } else {
            ((List<Asset>) response.getData()).forEach(k -> {
                System.out.println("Id:"+k.getId());
            });
        }
    }
}
