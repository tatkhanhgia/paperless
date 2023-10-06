//
///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.paperless.kernel;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import vn.mobileid.id.general.database.Database;
//import vn.mobileid.id.general.database.DatabaseImpl;
//import vn.mobileid.id.general.keycloak.obj.User;
//import vn.mobileid.id.general.objects.DatabaseResponse;
//import vn.mobileid.id.general.objects.InternalResponse;
//import vn.mobileid.id.paperless.PaperlessConstant;
//import vn.mobileid.id.paperless.object.enumration.FileType;
//import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
//import vn.mobileid.id.paperless.objects.WorkflowActivity;
//import vn.mobileid.id.paperless.objects.Workflow;
//import vn.mobileid.id.paperless.objects.response.Create_WorkflowActivity_MessageJSNObject;
//
///**
// *
// * @author GiaTK
// */
//@Deprecated
//public class CreateWorkflowActivity {
//
//    /**
//     * Check Data in WorkflowActivity before create new.
//     *
//     * @param workflowAc - WorkflowActivity
//     * @return no Object => check status
//     */
//    public static InternalResponse checkDataWorkflowActivity(WorkflowActivity workflowAc) {
////        if (workflowAc.getEnterprise_name() == null && workflowAc.getEnterprise_id() <= 0) {
////            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
////                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
////                            PaperlessConstant.SUBCODE_MISSING_ENTERPRISE_DATA,
////                            "en",
////                            null));
////        }
//
//        if (workflowAc.getWorkflow_id() <= 0) {
//            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
//                    PaperlessMessageResponse.getErrorMessage(
//                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
//                            PaperlessConstant.SUBCODE_MISSING_WORKFLOW_ID,
//                            "en",
//                            null));
//        }
//        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
//                PaperlessMessageResponse.getErrorMessage(
//                        PaperlessConstant.CODE_SUCCESS,
//                        PaperlessConstant.SUBCODE_SUCCESS,
//                        "en",
//                        null));
//    }
//
//    /**
//     * Create a new WorkflowActivity
//     *
//     * @param woAc - WorkflowActivity
//     * @param user - User
//     * @param transaction
//     * @return String / ID of that WorkflowActivity
//     * @throws Exception
//     */
//    public static InternalResponse processingCreateWorkflowActivity(
//            WorkflowActivity woAc,
//            User user,
//            String transaction) throws Exception {
//
//        Database DB = new DatabaseImpl();
//        //Data
//        int logID = -1;
//        int fileManagementID = -1;
//        String transactionID = "";
//        int QRUUID = -1;
//
//        InternalResponse response = null;
//
//        //Get woAc type 
//        InternalResponse res = GetWorkflow.getWorkflow(
//                woAc.getWorkflow_id(),
//                transaction);
//        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//            return res;
//        }
//        Workflow temp = (Workflow) res.getData();
//
//        //Create new User Activity Log
//        response = CreateUserActivityLog.processingCreateUserActivityLog(
//                woAc,
//                user,
//                transaction);
//        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//            return response;
//        }
//        logID = Integer.parseInt(response.getMessage());
//
////            //Create new QR
//        //Create new Transaction
//        int typeTransaction = 0;
//        switch (temp.getWorkflow_type()) {
//            case 1: {
//                typeTransaction = 1;
//                response = CreateQR.processingCreateQR(
//                        "metaData",
//                        "HMAC",
//                        user.getName(),
//                        transaction);
//
//                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//                    return response;
//                }
//                QRUUID = Integer.parseInt(response.getMessage());
//                break;
//            }
//            case 2: {
//                typeTransaction = 1;
//                response = CreateQR.processingCreateQR(
//                        "metaData",
//                        "HMAC",
//                        user.getName(),
//                        transaction);
//
//                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//                    return response;
//                }
//                QRUUID = Integer.parseInt(response.getMessage());
//                break;
//            }
//            case 7:
//                typeTransaction = 3;
//                break;
//            case 8:
//                typeTransaction = 3;
//                break;
//            default:
//                typeTransaction = 3;
//        }
//        if (typeTransaction == 3) {
//            //Create new File Management
//            response = CreateFileManagement.processingCreateFileManagement(
//                    woAc,
//                    "HMAC",
//                    null,
//                    user,
//                    FileType.PDF,
//                    transaction);
//            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//                return response;
//            }
//            fileManagementID = Integer.parseInt(response.getMessage());
//        }
//        response = CreateTransaction.processingCreateTransaction(
//                logID,
//                typeTransaction != 1 ? fileManagementID : QRUUID,
//                typeTransaction, //Type QR:1 CSV:2 PDF:3
//                user,
//                user.getName(),
//                transaction);
//        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//            return response;
//        }
//        transactionID = response.getMessage();
//
//        //Create new Workflow Activity            
//        DatabaseResponse callDB = DB.createWorkflowActivity(
//                user.getAid(), //enterpriseID
//                woAc.getWorkflow_id(), //workflowID
//                user.getEmail(), //useremail
//                transactionID, //transactionid
//                fileManagementID, //file link - QR link
//                -1, //csv
//                woAc.getRemark(), //remark
//                woAc.isUse_test_token(), //use test token
//                woAc.isIs_production(), //is production
//                woAc.isUpdate_enable(), //is update
//                temp.getWorkflow_type(), //workflow type
//                "none request data", //request data
//                "hmac", //hmac
//                user.getName(), //created by
//                transaction);
//        if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
//            String message = PaperlessMessageResponse.getErrorMessage(
//                    PaperlessConstant.CODE_FAIL,
//                    callDB.getStatus(),
//                    "en",
//                    null);
//            return new InternalResponse(
//                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
//                    message
//            );
//        }
//
//        //Convert to object and return it to client
//        Create_WorkflowActivity_MessageJSNObject object = new Create_WorkflowActivity_MessageJSNObject();
//        object.setCode(PaperlessConstant.CODE_SUCCESS);
//        object.setWorkflowActivityID(callDB.getIDResponse_int());
//
//        return new InternalResponse(
//                PaperlessConstant.HTTP_CODE_SUCCESS,
//                new ObjectMapper().writeValueAsString(object));
//    }
//
//    public static void main(String[] args) {
//        WorkflowActivity object = new WorkflowActivity();
//        object.setEnterprise_id(3);
//        object.setWorkflow_id(8);
//        object.setWorkflow_template_type(3);
//        object.setRemark("Remak");
//        object.setUse_test_token(false);
//        object.setUpdate_enable(false);
//        object.setCreated_by("GIATK");
//
//        User user = new User();
//        user.setEmail("giatk@mobile-id.vn");
//
////        CreateWorkflowActivity.processingCreateWorkflowActivity(object, user);
//    }
//}
