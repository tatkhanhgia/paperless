/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class GetEmailTemplate {

    public static InternalResponse getEmailTemplate(
            int language,
            String typeEmail,
            String transactionID
    ) throws Exception {
        DatabaseResponse db = new DatabaseImpl().getEmailTemplate(
                (language <1 || language >2) ? 2 : language,
                typeEmail,
                transactionID);
        if (db.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = null;
            if (LogHandler.isShowErrorLog()) {
                message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                        db.getStatus(),
                        "en",
                        null);
                LogHandler.error(GetEmailTemplate.class,
                        "TransactionID:" + transactionID
                        + "\nCannot get Email Template - Detail:" + message);
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, db.getObject());
    }
}
