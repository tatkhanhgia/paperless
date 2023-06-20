/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

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
public class CreateLogAPI {

    public static InternalResponse createLogAPI(
            String email,
            int enterprise_id,
            int workflow_activity,
            String app_name,
            String api_key,
            String version,
            String service_name,
            String url,
            String http_verb,
            int status_code,
            String request,
            String response,
            String hmac,
            String created_by,
            String transactionID
    )
            throws Exception {
        Database DB = new DatabaseImpl();
        DatabaseResponse callDB = DB.logIntoDB(
                email, //email
                enterprise_id, //enterprise_id
                workflow_activity, //workflow_activity
                app_name, //app name
                api_key, //api key
                version, //version
                service_name, //service name
                url, //url
                http_verb, //http
                status_code, //status code
                request, //request
                response, //response
                hmac, //hmac
                created_by, //create by
                transactionID);

        if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = null;
            message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                    callDB.getStatus(),
                    "en",
                    null);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "");

    }
}
