/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import java.util.Date;
import java.util.List;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.DatabaseImpl_V2_User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.UserActivity;

/**
 *
 * @author GiaTK
 */
public class GetUserActivity {
    //<editor-fold defaultstate="collapsed" desc="Get Total Records of User Activity">
    /**
     * Get total records of User Activity
     * @param pUSER_EMAIL
     * @param pSEARCH_EMAIL
     * @param pENTERPRISE_ID
     * @param pLIST_CATEGORY
     * @param pFROM_DATE
     * @param pTO_DATE
     * @param transactionId
     * @return long RowCount
     * @throws Exception 
     */
    public static InternalResponse getTotalRecordsUserActivity(
            String pUSER_EMAIL,
            String pSEARCH_EMAIL,
            long pENTERPRISE_ID,
            String pLIST_CATEGORY,
            Date pFROM_DATE,
            Date pTO_DATE,
            String transactionId
    )throws Exception{
        try{
            DatabaseImpl_V2_User callDb = new DatabaseImpl_V2_User();
            DatabaseResponse response = callDb.getTotalRecordOfUserActivity(
                    pUSER_EMAIL,
                    pSEARCH_EMAIL,
                    pENTERPRISE_ID, 
                    pLIST_CATEGORY,
                    pFROM_DATE,
                    pTO_DATE, 
                    transactionId);
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
        } catch(Exception ex){
            LogHandler.error(
                    GetUserActivity.class,
                    "Cannot process Get Total Records of User Activity",
                    ex);            
            return new InternalResponse(
                     PaperlessConstant.HTTP_CODE_500,
                     new PaperlessMessageResponse()
                             .sendErrorMessage("INTERNAL SERVER ERROR")
                             .sendErrorDescriptionMessage("Error while getting total records")
                             .build()
            );
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Get List of User Activity">
    /**
     * Get a list of User Activity with condition 
     * @param pUSER_EMAIL
     * @param pSEARCH_EMAIL
     * @param pENTERPRISE_ID
     * @param pLIST_CATEGORY
     * @param pFROM_DATE
     * @param pTO_DATE
     * @param pOFF_SET
     * @param pROW_COUNT
     * @param transactionId
     * @return
     * @throws Exception 
     */
    public static InternalResponse getListUserActivities(
            String pUSER_EMAIL,
            String pSEARCH_EMAIL,
            long pENTERPRISE_ID,
            String pLIST_CATEGORY,
            Date pFROM_DATE,
            Date pTO_DATE,
            int pOFF_SET,
            int pROW_COUNT,
            String transactionId
    )throws Exception{
        try{
            DatabaseImpl_V2_User callDb = new DatabaseImpl_V2_User();
            DatabaseResponse response = callDb.getListOfUserActivity(
                    pUSER_EMAIL,
                    pSEARCH_EMAIL,
                    pENTERPRISE_ID, 
                    pLIST_CATEGORY,
                    pFROM_DATE,
                    pTO_DATE, 
                    pOFF_SET,
                    pROW_COUNT,
                    transactionId);
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
        } catch(Exception ex){
            LogHandler.error(
                    GetUserActivity.class,
                    "Cannot process Get List of User Activity",
                    ex);            
            return new InternalResponse(
                     PaperlessConstant.HTTP_CODE_500,
                     new PaperlessMessageResponse()
                             .sendErrorMessage("INTERNAL SERVER ERROR")
                             .sendErrorDescriptionMessage("Error while getting list of records")
                             .build()
            );
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Get details of User Activity">
    /**
     * Get details of User Activity
     * @param pUSER_ACTIVITY_ID
     * @param transactionId
     * @return Object UserActivity
     * @throws Exception 
     */
    public static InternalResponse getUserActivitiesDetails(
            long pUSER_ACTIVITY_ID,            
            String transactionId
    )throws Exception{
        try{
            DatabaseImpl_V2_User callDb = new DatabaseImpl_V2_User();
            DatabaseResponse response = callDb.getUserActivityDetails(
                    pUSER_ACTIVITY_ID,
                    transactionId);
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
        } catch(Exception ex){
            LogHandler.error(
                    GetUserActivity.class,
                    "Cannot process Get User Activity Details",
                    ex);            
            return new InternalResponse(
                     PaperlessConstant.HTTP_CODE_500,
                     new PaperlessMessageResponse()
                             .sendErrorMessage("INTERNAL SERVER ERROR")
                             .sendErrorDescriptionMessage("Error while getting User Activity Details")
                             .build()
            );
        }
    }
    //</editor-fold>
    
    public static void main(String[] args) throws Exception{
//        InternalResponse response = GetUserActivity.getListUserActivities(
//                "giatk@mobile-id.vn",
//                null,
//                3,
//                "1,2,3,4,5,6,7,8,9",
//                null,
//                null,
//                1,
//                10,
//                "transactionId");
//        
//        System.out.println("Status:"+response.getStatus());
//        System.out.println("Object:"+((List<UserActivity>)response.getData()).size());
//        System.out.println("Acti:"+((List<UserActivity>)response.getData()).get(0).getUser_activity_event());
//        System.out.println("Log_id"+((List<UserActivity>)response.getData()).get(0).getLog_id());

        InternalResponse response = GetUserActivity.getUserActivitiesDetails(676, "transactiond");
        
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            System.out.println("Message:"+response.getMessage());
        }
        else {
            UserActivity log = (UserActivity)response.getData();
            System.out.println(log.getLog_id());
        }
    }
}
