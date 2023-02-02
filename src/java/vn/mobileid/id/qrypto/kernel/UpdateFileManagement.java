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
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.objects.FileManagement;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;

/**
 *
 * @author GiaTK
 */
public class UpdateFileManagement {

    final private static Logger LOG = LogManager.getLogger(GetWorkflowActivity.class);

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
            byte[] data) {
        try {
            Database DB = new DatabaseImpl();                      
            InternalResponse res = GetFileManagement.getFileManagement(id);
            if(res.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS){
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
                    fileOriginal.getWidth(),
                    fileOriginal.getHeight(),
                    fileOriginal.getStatus(),
                    fileOriginal.getHmac(),
                    fileOriginal.getCreated_by(),
                    fileOriginal.getLastmodified_by(),
                    data);

            if (callDB.getStatus() != QryptoConstant.CODE_SUCCESS) {
                String message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                         null);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Cannot update File Management - Detail:" + message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            return new InternalResponse(
                    QryptoConstant.HTTP_CODE_SUCCESS,
                    "");

        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
//                e.printStackTrace();
                LOG.error("UNKNOWN EXCEPTION. Details: " + e);
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static void main(String[] arhs) throws IOException {
        String test = "D:\\NetBean\\QryptoServices\\file\\result.pdf";
        byte[] data = Files.readAllBytes(new File(test).toPath());
        int id = 23;
        InternalResponse res = UpdateFileManagement.updateFileManagement(
                id,
                "uuid",
                "dbms",                
                "name",
                1,
                0,
                0,
                1,
                "hmac",
                "GIA TK",
                "GIA TK",
                data);
        
        System.out.println(res.getStatus());
        System.out.println(res.getMessage());
    }
}
