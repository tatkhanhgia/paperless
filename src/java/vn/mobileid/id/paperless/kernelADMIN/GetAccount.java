/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernelADMIN;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.kernel.GetEnterpriseInfo;
import vn.mobileid.id.paperless.kernel.GetUser;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.Enterprise;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class GetAccount {

    public static InternalResponse getAccount(
            String email,
            String mobile_number,
            String name,
            String enteprise_name,
            int enterprise_id,
            String transactionID
    ) throws Exception {
        //Get Enterprise ID
        InternalResponse res;
        if (enterprise_id != 0) {
            res = GetEnterpriseInfo.getEnterprise(
                    null,
                    enterprise_id,
                    transactionID);
        } else {
            res = GetEnterpriseInfo.getEnterprise(
                    enteprise_name,
                    0,
                    transactionID);
        }
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        Enterprise ent = (Enterprise) res.getData();

        InternalResponse res2 = GetUser.getUser(
                email,
                0,
                ent.getId(),
                transactionID,
                false);

        if (res2.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res2;
        }

        Account account = (Account) res2.getData();
        account.setEnterprise_name(enteprise_name);
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                account);
    }
}
