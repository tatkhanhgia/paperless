/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernelADMIN;

import java.util.List;
import vn.mobileid.id.general.database.DatabaseImpl_V2_User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.kernel_v2.GetEnterpriseInfo;
import vn.mobileid.id.paperless.kernel_v2.GetUser;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.Enterprise;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class GetAccount {

    //<editor-fold defaultstate="collapsed" desc="Get Account">
    /**
     * Get về Account 
     * @param email
     * @param mobile_number
     * @param name
     * @param enteprise_name
     * @param enterprise_id
     * @param transactionID
     * @return
     * @throws Exception 
     */
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
//            res = GetEnterpriseInfo.getEnterprise(
//                    null,
//                    enterprise_id,
//                    transactionID);
            InternalResponse res2 = GetUser.getUser(
                    email,
                    0,
                    enterprise_id,
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
        } else {
            res = GetEnterpriseInfo.getEnterprise(
                    enteprise_name,
                    0,
                    transactionID);
            if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return res;
            }
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Accounts">
    /**
     * Get về toàn bộ accoun thuộc về enterprise nào đó
     * @param enterprise_name
     * @param pENTERPRISE_ROLE_LIST
     * @param enterprise_id
     * @param offset
     * @param rowCount
     * @param transactionId
     * @return
     * @throws Exception 
     */
    public static InternalResponse getAccounts(
            int enterprise_id,
            String pENTERPRISE_ROLE_LIST,
            int offset,
            int rowCount,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = new DatabaseImpl_V2_User().getListUser(
                enterprise_id,
                pENTERPRISE_ROLE_LIST,
                offset,
                rowCount,
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
                response.getObject()
        );
    }
    //</editor-fold>
    
    public static void main(String[] agrs) throws Exception{
//        InternalResponse res = GetAccount.getAccounts(
//                "",
//                3, 
//                1,
//                3, 
//                "transactionId");
//        
//        if(res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
//            System.out.println("Error");
//        } else {
//            List<Account> accounts = (List<Account>) res.getData();
//        }
    }    
}
