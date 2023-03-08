/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.QryptoConstant;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;

/**
 *
 * @author GiaTK
 */
public class GetFileManagement {
    final private static Logger LOG = LogManager.getLogger(GetFileManagement.class);
    
    public static InternalResponse getFileManagement(int id){
        try {
            Database DB = new DatabaseImpl();
            //Data                        
            InternalResponse response = null;

            DatabaseResponse callDB = DB.getFileManagement(id);

            if (callDB.getStatus() != QryptoConstant.CODE_SUCCESS) {
                String message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                         null);
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Cannot get File Management - Detail:" + message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            FileManagement file = (FileManagement) callDB.getObject();
            
            return new InternalResponse(
                    QryptoConstant.HTTP_CODE_SUCCESS,
                    file);

        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
//                e.printStackTrace();
                LOG.error("UNKNOWN EXCEPTION. Details: " + e);
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }
    
    public static void main(String[] args){
        InternalResponse res = GetFileManagement.getFileManagement(27
        );
        FileManagement a = (FileManagement) res.getData();
        System.out.println("A:"+a.getData());
    }
}
