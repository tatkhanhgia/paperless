/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.database.DatabaseImpl_V2_User;
import vn.mobileid.id.general.database.DatabaseV2_User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class GetUserActivityLog {
    //<editor-fold defaultstate="collapsed" desc="Get User Activity Log">
    /**
     * Get User Activity Log
     * @param pUSER_ACTIVITY_LOG_ID
     * @param transactionId
     * @return Object UserActivityLog
     * @throws Exception 
     */
    public static InternalResponse getUserActivityLog(
            long pUSER_ACTIVITY_LOG_ID,
            String transactionId
    )throws Exception{
        DatabaseV2_User callDb = new DatabaseImpl_V2_User();
        DatabaseResponse response = callDb.getUserActivityLog(pUSER_ACTIVITY_LOG_ID, transactionId);
        
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_BAD_REQUEST,
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
    //</editor-fold>
}
