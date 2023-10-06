/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.database;

import java.util.Date;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.paperless.object.enumration.DownloadLinkType;
import vn.mobileid.id.paperless.object.enumration.WorkflowActivityProductType;

/**
 *
 * @author GiaTK
 */
public interface DatabaseV2_WorkflowActivity {

    public DatabaseResponse createWorkflowActivity(
            int pENTERPRISE_ID,
            long pWORKFLOW_ID,
            String pUSER_EMAIL,
            String pTRANSACTION_ID,
            String pDOWNLOAD_LINK,
            DownloadLinkType pDOWNLOAD_LINK_TYPE,
            String pREMARK,
            WorkflowActivityProductType pPRODUCTION_TYPE,
            int pWORKFLOW_TYPE,
            String pREQUEST_DATA,
            String pHMAC,
            String pCREATED_BY,
            String transactionId
    ) throws Exception;

    public DatabaseResponse getWorkflowActivity(
            int id,
            String transaction_id)
            throws Exception;

    public DatabaseResponse getTotalRecordsWorkflowActivity(
            String U_EMAIL,
            int pENTERPRISE_ID,
            String EMAIL_SEARCH,
            String DATE_SEARCH,
            String G_TYPE,
            String W_A_STATUS,
            String pPRODUCTION_TYPE_LIST,
            boolean isCustomRange,
            Date FROM_DATE,
            Date TO_DATE,
            int pOFFSET,
            int pROW_COUNT,
            String transactionId
    ) throws Exception;

    public DatabaseResponse getListWorkflowActivity(
            String U_EMAIL,
            long pENTERPRISE_ID,
            String EMAIL_SEARCH,
            Date DATE_SEARCH,
            String G_TYPE,
            String W_A_STATUS,
            String pPRODUCTION_TYPE_LIST,
            boolean IS_CUSTOM_RANGE,
            Date FROM_DATE,
            Date TO_DATE,
            int pOFFSET,
            int pROW_COUNT,
            String transactionId
    ) throws Exception;

    public DatabaseResponse updateRequestDataOfWorkflowActivity(
            int id,
            String meta_data,
            String modified_by,
            String transactionID
    ) throws Exception;

    public DatabaseResponse updateStatusWorkflowActivity(
            int id,
            String status,
            String last_modified_by,
            String transactionID
    ) throws Exception;
}
