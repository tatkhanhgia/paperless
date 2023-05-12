/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import java.io.IOException;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class UpdateFileManagement {

//    final private static Logger LOG = LogManager.getLogger(GetWorkflowActivity.class);

    public static InternalResponse updateFileManagement(
            int id,
            String UUID,
            String DBMS,
            String name,
            int pages,
            int width,
            int height,
            int status,
            String hmac,
            String created_by,
            String last_modified_by,
            byte[] data,
            boolean isSigned,
            String transactionID) throws Exception {        
            Database DB = new DatabaseImpl();                      
            InternalResponse res = GetFileManagement.getFileManagement(
                    id,
                    transactionID);            
            if(res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
                return res;
            }
            FileManagement fileOriginal = (FileManagement) res.getData();
            fileOriginal.setUUID( UUID == null ? fileOriginal.getUUID() : UUID);
            fileOriginal.setName( name == null ? fileOriginal.getName() : name);
            fileOriginal.setDBMS( DBMS == null ? fileOriginal.getDBMS() : DBMS);
            fileOriginal.setPages( pages <=0 ? fileOriginal.getPages() : pages);
            fileOriginal.setWidth( width <=0 ? fileOriginal.getWidth() : width);
            fileOriginal.setHeight( height <=0 ? fileOriginal.getHeight() : height);
            fileOriginal.setStatus( status <0 || status >1 ? fileOriginal.getStatus() : status);
            fileOriginal.setHmac( hmac == null ? fileOriginal.getHmac() : hmac);
            fileOriginal.setCreated_by( created_by == null ? fileOriginal.getCreated_by() : created_by);
            fileOriginal.setLastmodified_by(last_modified_by);            
            
            DatabaseResponse callDB = DB.updateFileManagement(
                    id,
                    fileOriginal.getUUID(),
                    fileOriginal.getDBMS(),
                    fileOriginal.getName(),
                    fileOriginal.getPages(),
                    fileOriginal.getSize(),
                    fileOriginal.getWidth(),
                    fileOriginal.getHeight(),
                    fileOriginal.getStatus(),
                    fileOriginal.getHmac(),
                    fileOriginal.getCreated_by(),
                    fileOriginal.getLastmodified_by(),
                    data,
                    isSigned,
                    transactionID);

            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                         null);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(UpdateFileManagement.class,transactionID,"Cannot update File Management - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
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
