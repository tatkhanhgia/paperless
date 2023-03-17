/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernelADMIN;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.kernel.GetUser;
import vn.mobileid.id.paperless.objects.Enterprise;
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class GetKEYAPI {
    public static InternalResponse getKEYAPI(            
            int enterprise_id,
            String clientID,
            String transactionID){
        try {
            Database DB = new DatabaseImpl();                         
            InternalResponse response = null;
            
            DatabaseResponse callDB = DB.getKEYAPI(enterprise_id,clientID, transactionID);
            
            if(callDB.getStatus() != PaperlessConstant.CODE_SUCCESS ){              
                String message = null;
                if(LogHandler.isShowErrorLog()){
                    message = QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                                callDB.getStatus(),
                                "en"
                                , null);
                    LogHandler.error(GetUser.class,
                            "TransactionID:"+transactionID+
                            "\nCannot get GetKEY API - Detail:"+message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            
            Enterprise user = (Enterprise) callDB.getObject();            
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
    
    public static void main(String[] args){
        System.out.println(((Enterprise)(GetKEYAPI.getKEYAPI(0, "MI_MobileApp","transactionID").getData())).getClientID());
//        System.out.println(((Enterprise)(GetKEYAPI.getKEYAPI(3, null,"transactionID").getData())).getClientSecret());
    }
}
