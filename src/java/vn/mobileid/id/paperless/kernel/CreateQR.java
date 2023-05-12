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
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class CreateQR {

//    final private static Logger LOG = LogManager.getLogger(CreateUserActivityLog.class);

    public static InternalResponse processingCreateQR(
            String metaData,
            String hmac,
            String created_by,
            String transactionID
    ) throws Exception {
        Database DB = new DatabaseImpl();
        DatabaseResponse callDB = DB.createQR(
                metaData,
                hmac,
                created_by,
                transactionID);

        if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = null;
            if (LogHandler.isShowErrorLog()) {
                message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                         null);
                LogHandler.error(CreateQR.class,"TransactionID:"+transactionID+"\nCannot create QR - Detail:" + message);
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                String.valueOf(callDB.getIDResponse()));
    }
    
    public static void main(String[] args){
//        CreateQR.processingCreateQR(
//                "meta data",
//                "HMAC",
//                "GIATK");
    }
}
