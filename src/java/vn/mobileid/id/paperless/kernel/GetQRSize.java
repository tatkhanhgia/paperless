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
public class GetQRSize {
    public static InternalResponse getQRSize(
            String qr_size_name,
            String transactionID
    ) throws Exception{
        Database callDB = new DatabaseImpl();
        DatabaseResponse responseDB = callDB.getQRSize(
                qr_size_name,
                transactionID);
        
        if( responseDB.getStatus() != PaperlessConstant.CODE_SUCCESS){
            String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                        responseDB.getStatus(),
                        "en",
                        null);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(GetAsset.class,
                            "TransactionID:" + transactionID
                            + "\nCannot get QRSize - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
        }
        
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, responseDB.getObject());
    }
}
