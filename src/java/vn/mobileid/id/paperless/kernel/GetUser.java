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
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;

/**
 *
 * @author GiaTK
 */
public class GetUser {
//    final private static Logger LOG = LogManager.getLogger(GetUser.class);
    
    public static InternalResponse getUser(
            String email,
            int enterprise_id,
            String transactionID){
        try {
            Database DB = new DatabaseImpl();                         
            InternalResponse response = null;

            DatabaseResponse callDB = DB.getUser(email, enterprise_id, transactionID);
            
            if(callDB.getStatus() != PaperlessConstant.CODE_SUCCESS ){              
                String message = null;
                if(LogHandler.isShowErrorLog()){
                    message = QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                                callDB.getStatus(),
                                "en"
                                , null);
                    LogHandler.error(GetUser.class,
                            "TransactionID:"+transactionID+
                            "\nCannot get User - Detail:"+message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            
            User user = (User) callDB.getObject();            
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    user);

        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
//                e.printStackTrace();
                LogHandler.error(GetUser.class,
                        "TransactionID:"+transactionID+
                        "\nUNKNOWN EXCEPTION. Details: " + e);
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }
}
