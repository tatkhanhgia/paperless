/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class GetUser {

    public static InternalResponse getUser(
            String email, //Truyền email get dữ liệu
            int id, //truyền id get dữ liệu
            int enterprise_id,
            String transactionID,
            boolean returnTypeUser //True trả về dạng User - ngược lại Account
    ) throws Exception {

        Database DB = new DatabaseImpl();
        InternalResponse response = null;

        DatabaseResponse callDB = DB.getUser(
                email,
                id <= 0 ? 0 : id,
                enterprise_id,
                transactionID,
                returnTypeUser);

        try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                if (LogHandler.isShowErrorLog()) {
                    message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            callDB.getStatus(),
                            "en",
                            null);
                    LogHandler.error(GetUser.class,
                            "TransactionID:" + transactionID
                            + "\nCannot get User - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            if (returnTypeUser) {
                User user = (User) callDB.getObject();
                InternalResponse res = new InternalResponse(
                        PaperlessConstant.HTTP_CODE_SUCCESS,
                        user);
                res.setUser(user);
                return res;
            } else {
                Account account = (Account) callDB.getObject();
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_SUCCESS,
                        account);
            }

        } catch (Exception e) {
            throw new Exception("Cannot get User!", e);
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse getStatusUser(
            String email,
            String transactionID
    ) throws Exception {

        Database DB = new DatabaseImpl();
        InternalResponse response = null;

        DatabaseResponse callDB = DB.getStatusUser(
                email,
                transactionID);

        try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                if (LogHandler.isShowErrorLog()) {
                    message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            callDB.getStatus(),
                            "en",
                            null);
                    LogHandler.error(GetUser.class,
                            "TransactionID:" + transactionID
                            + "\nCannot get Status User - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            Account account = (Account) callDB.getObject();
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    account);

        } catch (Exception e) {
            throw new Exception("Cannot get Status of User!", e);
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static void main(String[] args) throws Exception {
        InternalResponse res = getUser(null, 3, 3, "transactionID", true);
        System.out.println("Email:" + ((User) res.getData()).getEmail());
    }

}
