/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.DatabaseImpl_V2_User;
import vn.mobileid.id.general.database.DatabaseV2_User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.object.enumration.Category;
import vn.mobileid.id.paperless.object.enumration.EventAction;
import vn.mobileid.id.utils.TaskV2;

/**
 *
 * @author GiaTK
 */
public class CreateUserActivity {

    //<editor-fold defaultstate="collapsed" desc="Create User Activity">
    /**
     * Create a new User Activity
     *
     * @param pUSER_EMAIL
     * @param pENTERPRISE_ID
     * @param pTRANSACTION_ID
     * @param pLOG_ID
     * @param pCATEGORY_NAME
     * @param pUSER_ACTIVITY_EVENT
     * @param pHMAC
     * @param pCREATED_BY
     * @param transactionId
     * @return long UserActivityId
     * @throws Exception
     */
    public static InternalResponse createUserActivity(
            String pUSER_EMAIL,
            int pENTERPRISE_ID,
            String pTRANSACTION_ID,
            long pLOG_ID,
            Category pCATEGORY_NAME,
            EventAction pUSER_ACTIVITY_EVENT,
            String pHMAC,
            String pCREATED_BY,
            String transactionId
    ) throws Exception {
        DatabaseV2_User callDb = new DatabaseImpl_V2_User();

        DatabaseResponse response = callDb.createUserActivity(
                pUSER_EMAIL,
                pENTERPRISE_ID,
                pTRANSACTION_ID,
                pLOG_ID,
                Resources.getListCategory().get(pCATEGORY_NAME.getId()).getCategory_name(),
                Resources.getListEventAction().get(pUSER_ACTIVITY_EVENT.getId()).getEvent_name(),
                pHMAC,
                pCREATED_BY,
                transactionId);

        LogHandler.debug(CreateUserActivity.class, response.getDebugString());
        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    ""
            );
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Processing create User activity">
    /**
     * Create user Activity Log + transaction(if) + user activity
     *
     * @param pUSER_EMAIL
     * @param pENTERPRISE_ID
     * @param pMODULE
     * @param pACTION
     * @param pDETAIL
     * @param pINFO_KEY
     * @param pINFO_VALUE
     * @param pIP_ADDRESS
     * @param pDESCRIPTION
     * @param pMETA_DATA
     * @param pCREATED_BY
     * @param pTRANSACTION_ID
     * @param pCATEGORY_NAME
     * @param pUSER_ACTIVITY_EVENT
     * @param transactionId
     * @return
     * @throws Exception
     */
    public static InternalResponse createUserActivity(
            String pUSER_EMAIL,
            int pENTERPRISE_ID,
            String pMODULE,
            EventAction pACTION,
            String pDETAIL,
            String pINFO_KEY,
            String pINFO_VALUE,
            String pIP_ADDRESS,
            String pDESCRIPTION,
            String pMETA_DATA,
            String pCREATED_BY,
            String pTRANSACTION_ID,
            Category pCATEGORY_NAME,
            EventAction pUSER_ACTIVITY_EVENT,
            String transactionId
    ) throws Exception {
        InternalResponse response = CreateUserActivityLog.createUserActivityLog(
                pUSER_EMAIL,
                pENTERPRISE_ID,
                pMODULE,
                pACTION,
                pINFO_KEY,
                pINFO_VALUE,
                pDETAIL,
                null,
                null,
                pIP_ADDRESS,
                pDESCRIPTION,
                pMETA_DATA,
                "hmac",
                pCREATED_BY,
                transactionId);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        long activityLogId = (long) response.getData();

        return createUserActivity(
                pUSER_EMAIL,
                pENTERPRISE_ID,
                pTRANSACTION_ID,
                activityLogId,
                pCATEGORY_NAME,
                pUSER_ACTIVITY_EVENT,
                "hmac",
                pCREATED_BY,
                transactionId);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Processing create User activity">
    /**
     * Create user Activity Log + transaction(if) + user activity
     *
     * @param pUSER_EMAIL
     * @param pENTERPRISE_ID
     * @param pMODULE
     * @param pACTION
     * @param pINFO_KEY
     * @param pINFO_VALUE
     * @param pDETAIL
     * @param pAGENT
     * @param pAGENT_DETAIL
     * @param pIP_ADDRESS
     * @param pDESCRIPTION
     * @param pMETA_DATA
     * @param pHMAC
     * @param pCREATED_BY
     * @param pTRANSACTION_ID
     * @param pCATEGORY_NAME
     * @param pUSER_ACTIVITY_EVENT
     * @param transactionId
     * @return
     * @throws Exception
     */
    public static InternalResponse createUserActivity(
            String pUSER_EMAIL,
            int pENTERPRISE_ID,
            String pMODULE,
            EventAction pACTION,
            String pINFO_KEY, //null
            String pINFO_VALUE, //null
            String pDETAIL,
            String pAGENT,
            String pAGENT_DETAIL,
            String pIP_ADDRESS,
            String pDESCRIPTION,
            String pMETA_DATA,
            String pHMAC,
            String pCREATED_BY,
            String pTRANSACTION_ID,
            Category pCATEGORY_NAME,
            EventAction pUSER_ACTIVITY_EVENT,
            String transactionId
    ) throws Exception {
        InternalResponse response = CreateUserActivityLog.createUserActivityLog(
                pUSER_EMAIL,
                pENTERPRISE_ID,
                pMODULE,
                pACTION,
                pINFO_KEY,
                pINFO_VALUE,
                pDETAIL,
                pAGENT,
                pAGENT_DETAIL,
                pIP_ADDRESS,
                pDESCRIPTION,
                pMETA_DATA,
                pHMAC,
                pCREATED_BY,
                transactionId);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        long activityLogId = (long) response.getData();

        return createUserActivity(
                pUSER_EMAIL,
                pENTERPRISE_ID,
                pTRANSACTION_ID,
                activityLogId,
                pCATEGORY_NAME,
                pUSER_ACTIVITY_EVENT,
                pHMAC,
                pCREATED_BY,
                transactionId);
    }
    //</editor-fold>

    public static void main(String[] args) throws Exception {
//        InternalResponse response = CreateUserActivity.createUserActivity(
//                "khanhpx@mobile-id.vn", 
//                3,
//                "675-0", 
//                "Accounts", 
//                "Testing", 
//                "hmac", 
//                "GiaTK",
//                "transactionid");
//        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
//            System.out.println("Mess:"+response.getMessage());
//        } else {
//            System.out.println("Id:"+response.getData());
//        }

    }
}
