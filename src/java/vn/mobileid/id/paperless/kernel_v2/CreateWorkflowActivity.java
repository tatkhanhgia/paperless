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
import vn.mobileid.id.paperless.object.enumration.Category;
import vn.mobileid.id.paperless.object.enumration.DownloadLinkType;
import vn.mobileid.id.paperless.object.enumration.EventAction;
import vn.mobileid.id.paperless.object.enumration.FileType;
import vn.mobileid.id.paperless.object.enumration.ObjectType;
import vn.mobileid.id.paperless.object.enumration.TemplateUserActivity;
import vn.mobileid.id.paperless.object.enumration.WorkflowActivityProductType;
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
     * @return Message to return it to Client. Data is long of Workflow Activity
     * ID
     * @throws Exception
     */
    public static InternalResponse processingCreateWorkflowActivity(
            WorkflowActivity woAc,
            String ip_address,
            User user,
            String transaction) throws Exception {

        //Data
        long logID = -1;
        long CSVTask = -1;
        long fileManagementID = -1;
        String transactionID = null;
        long QRUUID = -1;

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
        response = CreateUserActivityLog.createUserActivityLog(
                user.getEmail(),
                user.getAid(),
                "Workflow Activity",
                EventAction.New,
                null,
                null,
                "Create new Workflow Activity",
                null,
                null,
                user.getIpAddress(),
                Utils.generateDescription_UserActivity(user, TemplateUserActivity.createWorkflowActivity),
                "",
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
        ObjectType type = null;
        switch (temp.getWorkflow_type()) {
            case 1: {
                if (woAc.isCsvEnabled()) {
                    type = ObjectType.CSV;
                    response = CreateCSV.createCSV(
                            "CSV - "+System.currentTimeMillis(),
                            null,
                            null, 
                            0, 
                            0, 
                            null, 
                            null, 
                            user.getName()==null?user.getEmail():user.getName(), 
                            transactionID);
                     if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                        return response;
                    }
                     CSVTask = (long) response.getData();
                } else {
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
                    QRUUID = (long) response.getData();
                }
                break;
            }
            case 2: {
                type = ObjectType.PDF;
                break;
            }
            case 7: {
//                System.out.println("Create file");
                type = ObjectType.PDF;
                break;
            }
            case 8: {
//                System.out.println("Create file");
                type = ObjectType.PDF;
                break;
            }
            default: {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_NOT_FOUND,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                                PaperlessConstant.SUBCODE_THIS_TYPE_OF_WORKFLOW_DOES_NOT_SUPPORT_YET,
                                "en",
                                transactionID)
                );
            }
        }
        if (type.getNumber() == 3) {
//            System.out.println("Create fileManagement");
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
            fileManagementID = (long) response.getData();
        }
//        System.out.println("ObjectType in Transaction:"+type.name());        
        response = CreateTransaction.createTransaction(
                user.getEmail(),
                logID,
                type.getNumber() == 3 ? fileManagementID : type.getNumber() == 1 ?  QRUUID : CSVTask,
                type.getNumber(),
                ip_address,
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

        //Create User Activity
        CreateUserActivity.createUserActivity(
                user.getEmail(),
                user.getAid(),
                transaction,
                logID,
                Category.WorkflowActivity,
                EventAction.New,
                "hmac",
                user.getName() == null ? user.getEmail() : user.getName(),
                transactionID);

        //Create new Workflow Activity            
        DatabaseV2_WorkflowActivity callDb = new DatabaseImpl_V2_WorkflowActivity();
        DatabaseResponse callDB = callDb.createWorkflowActivity(
                user.getAid(),
                woAc.getWorkflow_id(),
                user.getEmail(),
                transaction,
                Utils.generateUUID(),
                type.getNumber() == 3 ? DownloadLinkType.PDF : type.getNumber() ==1 ? DownloadLinkType.IMAGE : DownloadLinkType.ZIP,
                woAc.getRemark(),
                WorkflowActivityProductType.Production,
                temp.getWorkflow_type(),
                "created workflow activity",
                "hmac",
                user.getName() == null ? user.getEmail() : user.getName(),
                transactionID);

        if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            callDB.getStatus(),
                            "en",
                            null)
            );
        }

        response = new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "{\"workflow_activity_id\":" + callDB.getObject() + "}");
        response.setData(callDB.getObject());
        return response;
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
