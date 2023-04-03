/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Enterprise;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class GetEnterpriseInfo {
    public static InternalResponse getEnterprise(
            String enterprise_name,
            int enterprise_id,
            String transactionID){
        try {
            Database DB = new DatabaseImpl();                         
            InternalResponse response = null;

            DatabaseResponse callDB = DB.getEnterpriseInfo(
                    enterprise_id <=0 ? 0 : enterprise_id,
                    enterprise_name);
            
            if(callDB.getStatus() != PaperlessConstant.CODE_SUCCESS ){              
                String message = null;
                if(LogHandler.isShowErrorLog()){
                    message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                                callDB.getStatus(),
                                "en"
                                , null);
                    LogHandler.error(GetUser.class,
                            "TransactionID:"+transactionID+
                            "\nCannot get Enterprise - Detail:"+message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            
            Enterprise ent = (Enterprise) callDB.getObject();
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    ent);

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
        InternalResponse res = getEnterprise("Mobile-ID Company", 0, "tran");
        Enterprise ent = (Enterprise) res.getData();
        System.out.println("Ent:"+ent.getName());
        System.out.println("Ent:"+ent.getId());
    }

}
