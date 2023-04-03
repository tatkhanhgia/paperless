/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernelADMIN;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.email.SendMail;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.kernel.GetEnterpriseInfo;
import vn.mobileid.id.paperless.kernel.GetUser;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.EmailTemplate;
import vn.mobileid.id.paperless.objects.Enterprise;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class GetAccount {
    
    public static InternalResponse getListAccount(
            String email,
            String mobile_number,
            String name,                        
            String enteprise_name,            
            String transactionID
    ) {
        try {
            //Get Enterprise ID
            InternalResponse res = GetEnterpriseInfo.getEnterprise(
                    enteprise_name,
                    0,
                    transactionID);
            if(res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
                return res;
            }
            
            Enterprise ent = (Enterprise) res.getData();
            
            Account account = (Account)GetUser.getUser(
                    email,
                    0,
                    ent.getId(),
                    transactionID,
                    false).getData();
            account.setEnterprise_name(enteprise_name);
            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, account);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(CreateAccount.class,
                        "TransactionID:" + transactionID
                        + "\nUNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(ex));
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }
}
