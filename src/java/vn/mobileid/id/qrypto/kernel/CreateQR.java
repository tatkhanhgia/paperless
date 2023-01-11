/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.kernel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;

/**
 *
 * @author GiaTK
 */
public class CreateQR {

    final private static Logger LOG = LogManager.getLogger(CreateUserActivityLog.class);

    public static InternalResponse processingCreateQR(
            String metaData,
            String hmac,
            String created_by
    ) {
        Database DB = new DatabaseImpl();
        DatabaseResponse callDB = DB.createQR(
                metaData,
                hmac,
                created_by);

        if (callDB.getStatus() != QryptoConstant.CODE_SUCCESS) {
            String message = null;
            if (LogHandler.isShowErrorLog()) {
                message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                         null);
                LOG.error("Cannot create QR - Detail:" + message);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }
        return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                String.valueOf(callDB.getIDResponse()));
    }
    
    public static void main(String[] args){
        CreateQR.processingCreateQR(
                "meta data",
                "HMAC",
                "GIATK");
    }
}
