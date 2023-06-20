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

    /**
     * Get Email Template in Database
     * @param language - 2:EN 1:VN
     * @param typeEmail - The unique String that presentation for the Email
     * @param transactionID
     * @return EmailTemplate
     * @throws Exception 
     */
    public static InternalResponse getEmailTemplate(
            int language,
            String typeEmail,
            String transactionID
    ) throws Exception {
        DatabaseResponse db = new DatabaseImpl().getEmailTemplate(
                (language < 1 || language > 2) ? 2 : language,
                typeEmail,
                transactionID);
        if (db.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = null;
            message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_FAIL,
                    db.getStatus(),
                    "en",
                    null);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                db.getObject());
    }
}
