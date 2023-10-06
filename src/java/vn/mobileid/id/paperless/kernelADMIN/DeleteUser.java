/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernelADMIN;

import vn.mobileid.id.general.database.DatabaseImpl_V2_User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class DeleteUser {

    /**
     * Delete User
     *
     * @param emailAdmin
     * @param emailToBeDelete
     * @param enterpriseId
     * @param transactionId
     * @return
     * @throws java.lang.Exception
     */
    public static InternalResponse deleteUser(
            String emailAdmin,
            String emailToBeDelete,
            int enterpriseId,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = new DatabaseImpl_V2_User().deleteUser(
                emailAdmin,
                emailToBeDelete,
                enterpriseId,
                transactionId);
        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = null;
            message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_FAIL,
                    response.getStatus(),
                    "en",
                    null);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    message
            );
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
}