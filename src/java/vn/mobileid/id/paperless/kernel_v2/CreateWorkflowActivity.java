/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.DatabaseImpl_V2_WorkflowActivity;
import vn.mobileid.id.general.database.DatabaseV2_WorkflowActivity;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.object.enumration.DownloadLinkType;
import vn.mobileid.id.paperless.object.enumration.FileType;
import vn.mobileid.id.paperless.object.enumration.ObjectType;
import vn.mobileid.id.paperless.object.enumration.WorkflowActivityProductType;
import vn.mobileid.id.paperless.objects.EventAction;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.Workflow;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class CreateWorkflowActivity {

    //<editor-fold defaultstate="collapsed" desc="Check data of Workflow Activity">
    /**
     * Check Data in WorkflowActivity before create new.
     *
     * @param workflowAc - WorkflowActivity
     * @return no Object => check status
     */
    public static InternalResponse checkDataWorkflowActivity(WorkflowActivity workflowAc) {
//        if (workflowAc.getEnterprise_name() == null && workflowAc.getEnterprise_id() <= 0) {
//            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
//                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
//                            PaperlessConstant.SUBCODE_MISSING_ENTERPRISE_DATA,
//                            "en",
//                            null));
//        }

        if (workflowAc.getWorkflow_id() <= 0) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_MISSING_WORKFLOW_ID,
                            "en",
                            null));
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_SUCCESS,
                        PaperlessConstant.SUBCODE_SUCCESS,
                        "en",
                        null));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create Workflow Activity">
    public static InternalResponse createWorkflowActivity(
            int pENTERPRISE_ID,
            int pWORKFLOW_ID,
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
    ) throws Exception {
        DatabaseV2_WorkflowActivity callDb = new DatabaseImpl_V2_WorkflowActivity();

        DatabaseResponse response = callDb.createWorkflowActivity(
                pENTERPRISE_ID,
                pWORKFLOW_ID,
                pUSER_EMAIL,
                pTRANSACTION_ID,
                pDOWNLOAD_LINK,
                pDOWNLOAD_LINK_TYPE,
                pREMARK,
                pPRODUCTION_TYPE,
                pWORKFLOW_TYPE,
                pREQUEST_DATA,
                pHMAC,
                pCREATED_BY,
                transactionId);

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
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

    //<editor-fold defaultstate="collapsed" desc="Create Workflow Activity Processing">
    /**
     * Create a new WorkflowActivity
     *
     * @param woAc - WorkflowActivity
     * @param user - User
     * @param transaction
     * @return String / ID of that WorkflowActivity
     * @throws Exception
     */
    public static InternalResponse processingCreateWorkflowActivity(
            WorkflowActivity woAc,
            User user,
            String transaction) throws Exception {

        //Data
        long logID = -1;
        long fileManagementID = -1;
        String transactionID = null;
        int QRUUID = -1;

        InternalResponse response = null;

        //Get woAc type 
        InternalResponse res = GetWorkflow.getWorkflow(
                woAc.getWorkflow_id(),
                transaction);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        Workflow temp = (Workflow) res.getData();

        //<editor-fold defaultstate="collapsed" desc="Create User Activity Log">
        //Create new User Activity Log
        EventAction tempp = Resources.getListEventAction().get(4);
        String action = tempp.getEvent_name();
        response = CreateUserActivityLog.createUserActivityLog(
                user.getEmail(),
                user.getAid(),
                "Workflow Activity",
                tempp.getEvent_name(),
                null,
                null,
                transaction,
                null,
                null,
                "hmac",
                user.getName() == null ? user.getEmail() : user.getName(),
                transactionID);

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        logID = (long) response.getData();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Create Transaction">
        //Create new Transaction
        ObjectType type = ObjectType.PDF;
        switch (temp.getWorkflow_type()) {
            case 1: {
                type = ObjectType.QR;
                response = CreateQR.processingCreateQR(
                        "metaData",
                        null,
                        "HMAC",
                        user.getName(),
                        transaction);

                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return response;
                }
                QRUUID = Integer.parseInt(response.getMessage());
                break;
            }
            case 2: {
                type = ObjectType.QR;
                response = CreateQR.processingCreateQR(
                        "metaData",
                        null,
                        "HMAC",
                        user.getName(),
                        transaction);

                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return response;
                }
                QRUUID = Integer.parseInt(response.getMessage());
                break;
            }
            case 7:
                break;
            case 8:
                break;
            default:
        }
        if (type.equals(ObjectType.PDF)) {
            //Create new File Management
            response = CreateFileManagement.processingCreateFileManagement(
                    woAc,
                    "FileManagement",
                    0,
                    0,
                    0,
                    0,
                    "hmac",
                    null,
                    user,
                    "DBMS",
                    FileType.PDF,
                    null,
                    null,
                    transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            fileManagementID = (long)response.getData();
        }
        response = CreateTransaction.createTransaction(
                user.getEmail(),
                logID,
                type.equals(ObjectType.PDF) ? fileManagementID : QRUUID,
                type.getNumber(),
                "IP ADDRESS",
                "MetaData",
                "Create New Transaction",
                "hmac",
                user.getName() == null ? user.getEmail() : user.getName(),
                transactionID
        );

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        transaction = (String) response.getData();
        //</editor-fold>

        //Create new Workflow Activity            
        DatabaseV2_WorkflowActivity callDb = new DatabaseImpl_V2_WorkflowActivity();
        DatabaseResponse callDB = callDb.createWorkflowActivity(
                user.getAid(), 
                woAc.getWorkflow_id(), 
                user.getEmail(), 
                transaction, 
                Utils.generateUUID(), 
                DownloadLinkType.PDF, 
                woAc.getRemark(), 
                WorkflowActivityProductType.Production, 
                temp.getWorkflow_type(), 
                "created workflow activity", 
                "hmac", 
                user.getName() == null ? user.getEmail() : user.getName(), 
                transactionID);
        
        if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_FAIL,
                    callDB.getStatus(),
                    "en",
                    null);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }
        

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "{\"workflow_activity_id\":"+callDB.getObject()+"}");
    }

    //</editor-fold>
    public static void main(String[] args) throws Exception {
        InternalResponse response = CreateWorkflowActivity.createWorkflowActivity(
                3,
                488,
                "khanhpx@mobile-id.vn",
                "99-76",
                "Download Link Name",
                DownloadLinkType.PDF,
                "Description",
                WorkflowActivityProductType.Production,
                7,
                "Request Data",
                "hmac",
                "GiaTK",
                "transactionId");

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            System.out.println("Mess:" + response.getMessage());
        } else {
            System.out.println("Id:" + response.getData());
        }
    }
}
