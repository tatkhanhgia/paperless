/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class ManageRefreshToken {

    public static InternalResponse write(
            String email,
            String session,
            int client_credentials_enabled,
            String clientID,
            Date issue_on,
            Date expires_on,
            String hmac,
            String created_by,
            String transactionID
    ) throws Exception {

        Database db = new DatabaseImpl();
        DatabaseResponse res = db.writeRefreshToken(
                email,
                session,
                client_credentials_enabled,
                clientID,
                issue_on,
                expires_on,
                hmac,
                created_by,
                transactionID);
        if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = null;
            if (LogHandler.isShowErrorLog()) {
                message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                        res.getStatus(),
                        "en",
                        null);
                LogHandler.error(ManageRefreshToken.class, transactionID, "Cannot write RefreshToken - Detail:" + message);
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }

    public static InternalResponse remove(
            String sessionID,
            String transactionID
    ) throws Exception {
        Database db = new DatabaseImpl();
        DatabaseResponse res = db.removeRefreshToken(
                sessionID,
                transactionID);
        if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                    res.getStatus(),
                    "en",
                    null);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED, message);
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, "");
    }

    public static InternalResponse check(
            String email,
            String session,
            String transactionID
    ) throws Exception {
        Database db = new DatabaseImpl();
        DatabaseResponse res = db.checkAccessToken(
                email,
                session,
                transactionID);
        if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                    res.getStatus(),
                    "en",
                    null);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED, message);
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, "");
    }

    public static InternalResponse get(
            String session,
            String transactionID
    ) throws Exception {
        Database db = new DatabaseImpl();
        DatabaseResponse res = db.getRefreshToken(
                session,
                transactionID);
        if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                    res.getStatus(),
                    "en",
                    null);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_UNAUTHORIZED, message);
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, res.getObject());

    }

    public static InternalResponse update(
            String email,
            String session,
            int client_credentials_enabled,
            Date issue_on,
            Date expires_on,
            int status,
            String hmac,
            String created_by,
            String transactionID
    ) throws Exception {
        Database db = new DatabaseImpl();
        DatabaseResponse res = db.updateRefreshToken(
                email,
                session,
                client_credentials_enabled,
                issue_on,
                expires_on,
                1,
                hmac,
                created_by,
                transactionID);
        if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = null;
            if (LogHandler.isShowErrorLog()) {
                message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                        res.getStatus(),
                        "en",
                        null);
                LogHandler.error(ManageRefreshToken.class, transactionID, "Cannot update RefreshToken - Detail:" + message);
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );

    }

    public static void main(String[] args) {
//        Date date = new Date();
//        InternalResponse res = ManageRefreshToken.write(
//                "giatk@mobile-id.vn",
//                "SessionID",
//                1,
//                "clientID",
//                new Date(date.getTime()),
//                new Date(date.getTime()),
//                "HMAC",
//                "GIATK");
//        System.out.println(res.getStatus());
//        System.out.println(res.getMessage());
    }
}
