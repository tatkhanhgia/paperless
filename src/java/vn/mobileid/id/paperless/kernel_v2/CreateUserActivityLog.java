/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.DatabaseImpl_V2_User;
import vn.mobileid.id.general.database.DatabaseV2_User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.object.enumration.EventAction;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class CreateUserActivityLog {

    /**
     * Create a new User Activity Log
     *
     * @param pUSER_EMAIL : Email of the User
     * @param pENTERPRISE_ID : Enterprise Id that belongs to the user
     * @param pMODULE : API Name
     * @param pACTION : Action
     * @param pINFO_KEY : None
     * @param pINFO_VALUE : None
     * @param pDETAIL : Decription of the action
     * @param pAGENT : None
     * @param pAGENT_DETAIL : None
     * @param pIP_ADDRESS
     * @param pDESCRIPTION
     * @param pMETA_DATA
     * @param pHMAC : None
     * @param pCREATED_BY : User
     * @param transactionId
     * @return long UserActivityLogID
     * @throws Exception
     */
    public static InternalResponse createUserActivityLog(
            String pUSER_EMAIL,
            int pENTERPRISE_ID,
            String pMODULE,
            EventAction pACTION,
            String pINFO_KEY,
            String pINFO_VALUE,
            String pDETAIL,
            String pAGENT,
            String pAGENT_DETAIL,
            String pIP_ADDRESS,
            String pDESCRIPTION,
            String pMETA_DATA,
            String pHMAC,
            String pCREATED_BY,
            String transactionId) throws Exception {
        DatabaseV2_User callDb = new DatabaseImpl_V2_User();

        DatabaseResponse response = callDb.createUserActivityLog(
                pUSER_EMAIL,
                pENTERPRISE_ID,
                pMODULE,
                Resources.getListEventAction().get(pACTION.getId()).getEvent_name(),
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

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            transactionId)
            );
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                response.getObject()
        );
    }

    public static void main(String[] args) throws Exception {
        InternalResponse response = CreateUserActivityLog.createUserActivityLog(
                "khanhpx@mobile-id.vn",
                3,
                "Test Module",
                EventAction.Add_user,
                "none",
                "none",
                "Testing first time",
                "agent",
                "agent detail",
                "192.168.1.1",
                "NONE",
                "Testing",
                "hmac",
                "GIATK",
                "transactionId");

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            System.out.println("Mess:" + response.getMessage());
        } else {
            System.out.println("Id:" + response.getData());
        }
    }
}
