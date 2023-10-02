/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import java.io.IOException;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class UpdateFileManagement {

    /**
     * Update date of FileManagement
     *
     * @param id
     * @param UUID
     * @param DBMS
     * @param name
     * @param pages
     * @param size
     * @param width
     * @param height
     * @param status
     * @param hmac
     * @param created_by
     * @param last_modified_by
     * @param data
     * @param isSigned
     * @param file_type
     * @param signing_properties
     * @param hash_values
     * @param transactionID
     * @return
     * @throws Exception
     */
    public static InternalResponse updateFileManagement(
            long id,
            String UUID,
            String DBMS,
            String name,
            int pages,
            long size,
            float width,
            float height,
            int status,
            String hmac,
            String created_by,
            String last_modified_by,
            byte[] data,
            boolean isSigned,
            String file_type,
            String signing_properties,
            String hash_values,
            String transactionID) throws Exception {
        Database DB = new DatabaseImpl();
//        InternalResponse res = GetFileManagement.getFileManagement(
//                id,
//                transactionID);
//        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//            return res;
//        }
//        FileManagement fileOriginal = (FileManagement) res.getData();
//        fileOriginal.setUUID(UUID == null ? fileOriginal.getUUID() : UUID);
//        fileOriginal.setName(name == null ? fileOriginal.getName() : name);
//        fileOriginal.setDBMS(DBMS == null ? fileOriginal.getDBMS() : DBMS);
//        fileOriginal.setPages(pages <= 0 ? fileOriginal.getPages() : pages);
//        fileOriginal.setWidth(width <= 0 ? fileOriginal.getWidth() : width);
//        fileOriginal.setHeight(height <= 0 ? fileOriginal.getHeight() : height);
//        fileOriginal.setStatus(status < 0 || status > 1 ? fileOriginal.getStatus() : status);
//        fileOriginal.setHmac(hmac == null ? fileOriginal.getHmac() : hmac);
//        fileOriginal.setCreated_by(created_by == null ? fileOriginal.getCreated_by() : created_by);
//        fileOriginal.setLastmodified_by(last_modified_by);
////
//        DatabaseResponse callDB = DB.updateFileManagement(
//                id,
//                fileOriginal.getUUID(),
//                fileOriginal.getDBMS(),
//                fileOriginal.getName(),
//                fileOriginal.getPages(),
//                fileOriginal.getSize(),
//                fileOriginal.getWidth(),
//                fileOriginal.getHeight(),
//                fileOriginal.getStatus(),
//                fileOriginal.getHmac(),
//                fileOriginal.getCreated_by(),
//                fileOriginal.getLastmodified_by(),
//                data,
//                isSigned,
//                transactionID);
        
        DatabaseResponse callDB = DB.updateFileManagement(
                id,
                UUID,
                DBMS,
                name,
                pages,
                size,
                width,
                height,
                status,
                hmac,
                created_by,
                last_modified_by,
                data,
                isSigned,
                file_type,
                signing_properties,
                hash_values,
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
                "");
    }

    public static void main(String[] arhs) throws IOException {
//        String test = "D:\\NetBean\\QryptoServices\\file\\result.pdf";
//        byte[] data = Files.readAllBytes(new File(test).toPath());
//        int id = 23;
//        InternalResponse res = UpdateFileManagement.updateFileManagement(
//                id,
//                "uuid",
//                "dbms",                
//                "name",
//                1,
//                0,
//                0,
//                1,
//                "hmac",
//                "GIA TK",
//                "GIA TK",
//                data, true);
//        
//        System.out.println(res.getStatus());
//        System.out.println(res.getMessage());
    }
}
