/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import java.util.List;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Enterprise;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class GetEnterpriseInfo {

    /**
     * Get data of Enterprise (Choose 1 or 2)
     * @param enterprise_name - Enterprise name
     * @param enterprise_id - ENterprise id
     * @param transactionID
     * @return Enterprise
     * @throws Exception 
     */
    public static InternalResponse getEnterprise(
            String enterprise_name,
            int enterprise_id,
            String transactionID)
            throws Exception {
        Database DB = new DatabaseImpl();        

        DatabaseResponse callDB = DB.getEnterpriseInfo(
                enterprise_id <= 0 ? 0 : enterprise_id,
                enterprise_name);

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

            Enterprise ent = (Enterprise) callDB.getObject();
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    ent);

        } catch (Exception e) {
            throw new Exception("Cannot get enterpriseInfo!", e);
        }
    }

    /**
     * Get data of the Enterprise which belong to the User.
     * @param email - Email of User
     * @param transactionID
     * @return List of Enterprise
     * @throws Exception 
     */
    public static InternalResponse getEnterpriseInfo(
            String email,
            String transactionID) throws Exception {
        Database db = new DatabaseImpl();
        
        DatabaseResponse res = db.getEnterpriseInfoOfUser(
                email,
                transactionID);
        try {
            if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        res.getStatus(),
                        "en",
                        null);
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message);
            }

            List<Enterprise> list = (List<Enterprise>) res.getObject();
            
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    list.get(0));
        } catch (Exception e) {
            throw new Exception("Cannot get enterprise info", e);
        }
    }

    public static void main(String[] args) throws Exception {
        InternalResponse res = getEnterprise("Mobile-ID Company", 0, "tran");
        Enterprise ent = (Enterprise) res.getData();
        System.out.println("Ent:" + ent.getName());
        System.out.println("Ent:" + ent.getId());
    }

}
