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
}
