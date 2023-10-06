/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.database.DatabaseImpl_V2_User;
import vn.mobileid.id.general.database.DatabaseV2_User;
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
    //<editor-fold defaultstate="collapsed" desc="Get User">
    /**
     * Get data of User (choose email or id to get data)
     *
     * @param email - Email of User
     * @param id - ID of User
     * @param enterprise_id - Enterprise id
     * @param transactionID
     * @param returnTypeUser - true => User type ; false => Account type
     * @return Object (User or Account)
     * @throws Exception
     */
    public static InternalResponse getUser(
            String email,
            int id,
            int enterprise_id,
            String transactionID,
            boolean returnTypeUser //True trả về dạng User - ngược lại Account
    ) throws Exception {
        DatabaseV2_User DB = new DatabaseImpl_V2_User();

        DatabaseResponse callDB = DB.getUser(
                email,
                id <= 0 ? 0 : id,
                enterprise_id,
                returnTypeUser?User.class:Account.class,
                transactionID
                );

        try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                message = PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
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
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Get Status of User">
    /**
     * Get the status of User
     *
     * @param email - Email of User
     * @param transactionID
     * @return Account
     * @throws Exception
     */
    public static InternalResponse getStatusUser(
            String email,
            String transactionID
    ) throws Exception {
        DatabaseV2_User DB = new DatabaseImpl_V2_User();

        DatabaseResponse callDB = DB.getStatusUser(
                email,
                transactionID);

        try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                message = PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            Account account = (Account) callDB.getObject();
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    account);

        } catch (Exception e) {
            throw new Exception("Cannot get Status of User!", e);
        }
    }
    //</editor-fold>

    public static void main(String[] args)throws Exception {
        InternalResponse response = GetUser.getUser(
                "khanhpx@mobile-id.vn",
                0,
                3,
                "null",
                false);
                
        Account account = (Account)response.getData();
        System.out.println("Id:"+account.getStatus_id());
    }
}
