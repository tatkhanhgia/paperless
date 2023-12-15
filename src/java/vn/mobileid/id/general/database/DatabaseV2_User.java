/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.database;

import java.util.Date;
import vn.mobileid.id.general.objects.DatabaseResponse;

/**
 *
 * @author GiaTK
 */
public interface DatabaseV2_User {
    public DatabaseResponse createUserActivity(
            String pUSER_EMAIL,
            int pENTERPRISE_ID,
            String pTRANSACTION_ID,
            long pLOG_ID,
            String pCATEGORY_NAME,
            String pUSER_ACTIVITY_EVENT,
            String pHMAC,
            String pCREATED_BY,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse createUserActivityLog(
            String pUSER_EMAIL,
            int pENTERPRISE_ID,
            String pMODULE,
            String pACTION,
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
            String transactionId
    )throws Exception;
    
    public DatabaseResponse deleteUser(
            String pUSER_EMAIL,
            String pDELETED_USER_EMAIL,
            long pENTERPRISE_ID,
            String  transactionId
    )throws Exception;
    
    public DatabaseResponse updateUser(
            String pUSER_EMAIL,
            String pUSER_NAME,
            String pMOBILE_NUMBER ,
            Date pPASSWORD_EXPIRED_AT,
            int pREMAINING_COUNTER,
            String pSTATUS_NAME,
            int pCHANGE_PASSWORD,
            boolean pLOCKED_ENABLED,
            Date pLOCKED_AT,
            int pBUSINESS_TYPE,
            String pORGANIZATION_WEBSITE,
            String pHMAC,
            String pLAST_MODIFIED_BY,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse updateRole(
            String U_EMAIL,
            long pENTERPRISE_ID,
            String pROLE_NAME,
            String transactionId
    )throws Exception;
       
    public DatabaseResponse getUser(
            String pUSER_EMAIL,
            long pUSER_ID,
            long pENTERPRISE_ID,
            Class clazz,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse getStatusUser(
            String pUSER_EMAIL,
            String transactionID
    )throws Exception;
    
    public DatabaseResponse getTypeOfStatus()throws Exception;
    
    public DatabaseResponse getListUser(
            long pENTERPRISE_ID,
            String pENTERPRISE_ROLE_LIST,
            int pOFFSET,
            int pROW_COUNT,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse getCategory()throws Exception;
    
    public DatabaseResponse getTotalRecordOfUserActivity(
            String pUSER_EMAIL,
            String pSEARCH_EMAIL,
            long pENTERPRISE_ID,
            String pLIST_CATEGORY,
            Date pFROM_DATE,
            Date pTO_DATE,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse getListOfUserActivity(
            String pUSER_EMAIL,
            String pSEARCH_EMAIL,
            long pENTERPRISE_ID,
            String pLIST_CATEGORY,
            Date pFROM_DATE,
            Date pTO_DATE,
            int pOFF_SET,
            int pROW_COUNT,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse getUserActivityDetails(
            long pUSER_ACTIVITY_ID,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse getUserActivityLog(
            long pUSER_ACTIVITY_LOG_ID,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse getTotalRecordOfUser(
            int enterprise_id,
            String listRole,
            String transactionId
    )throws Exception;
    
    public DatabaseResponse getListRoleOfEnterprise(
            int enterprise_id,
            String transactionId
    )throws Exception;
}
