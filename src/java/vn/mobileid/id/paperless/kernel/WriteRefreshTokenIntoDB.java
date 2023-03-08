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
import vn.mobileid.id.paperless.QryptoConstant;
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;

/**
 *
 * @author GiaTK
 */
public class WriteRefreshTokenIntoDB {

    final private static Logger LOG = LogManager.getLogger(WriteRefreshTokenIntoDB.class);

    public static InternalResponse write(
            String email,
            String session,
            int client_credentials_enabled,
            String clientID,
            Date issue_on,
            Date expires_on,
            String hmac,
            String created_by
    ) {
        try {
            Database db = new DatabaseImpl();
            DatabaseResponse res = db.writeRefreshToken(
                    email,
                    session,
                    client_credentials_enabled,
                    clientID,
                    issue_on,
                    expires_on,
                    hmac,
                    created_by);
            if (res.getStatus() != QryptoConstant.CODE_SUCCESS) {
                String message = null;
                if (LogHandler.isShowErrorLog()) {
                    message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            res.getStatus(),
                            "en",
                             null);
                    LOG.error("Cannot write RefreshToken - Detail:" + message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                    ""
            );
        } catch (Exception ex) {
            if (LogHandler.isShowErrorLog()) {
                ex.printStackTrace();
                LOG.error("Cannot write RefreshToken into DB! - Details:" + ex);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse remove(
            String sessionID
    ) {
        try {
            Database db = new DatabaseImpl();
            DatabaseResponse res = db.removeRefreshToken(sessionID);
            if (res.getStatus() != QryptoConstant.CODE_SUCCESS) {
                String message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                        res.getStatus(),
                        "en",
                        null);
                return new InternalResponse(QryptoConstant.HTTP_CODE_UNAUTHORIZED, message);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS, "");
        } catch (Exception ex) {
            if (LogHandler.isShowErrorLog()) {
                ex.printStackTrace();
                LOG.error("Cannot remove RefreshToken! - Details:" + ex);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse check(
            String email,
            String session
    ) {
        try {
            Database db = new DatabaseImpl();
            DatabaseResponse res = db.checkAccessToken(email, session);
            if (res.getStatus() != QryptoConstant.CODE_SUCCESS) {
                String message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                        res.getStatus(),
                        "en",
                        null);
                return new InternalResponse(QryptoConstant.HTTP_CODE_UNAUTHORIZED, message);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS, "");
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while checking accessToken!!");
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse get(
            String session
    ) {
        try {
            Database db = new DatabaseImpl();
            DatabaseResponse res = db.getRefreshToken(session);
            if (res.getStatus() != QryptoConstant.CODE_SUCCESS) {
                String message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                        res.getStatus(),
                        "en",
                        null);
                return new InternalResponse(QryptoConstant.HTTP_CODE_UNAUTHORIZED, message);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS, res.getObject());
        } catch (Exception ex) {
            if (LogHandler.isShowErrorLog()) {
                ex.printStackTrace();
                LOG.error("Cannot remove RefreshToken! - Details:" + ex);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse update(
            String email,
            String session,
            int client_credentials_enabled,
            Date issue_on,
            Date expires_on,
            int status,
            String hmac,
            String created_by
    ) {
        try {
            Database db = new DatabaseImpl();
            DatabaseResponse res = db.updateRefreshToken(                    
                    email,
                    session,
                    client_credentials_enabled,                    
                    issue_on,
                    expires_on,
                    1,
                    hmac,
                    created_by);
            if (res.getStatus() != QryptoConstant.CODE_SUCCESS) {
                String message = null;
                if (LogHandler.isShowErrorLog()) {
                    message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            res.getStatus(),
                            "en",
                             null);
                    LOG.error("Cannot update RefreshToken - Detail:" + message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                    ""
            );
        } catch (Exception ex) {
            if (LogHandler.isShowErrorLog()) {
                ex.printStackTrace();
                LOG.error("Cannot write RefreshToken into DB! - Details:" + ex);
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static void main(String[] args) {
        Date date = new Date();
        InternalResponse res = WriteRefreshTokenIntoDB.write(
                "giatk@mobile-id.vn",
                "SessionID",
                1,
                "clientID",
                new Date(date.getTime()),
                new Date(date.getTime()),
                "HMAC",
                "GIATK");
        System.out.println(res.getStatus());
        System.out.println(res.getMessage());
    }
}
