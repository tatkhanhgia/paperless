/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import java.util.Date;
import java.util.List;
import vn.mobileid.id.general.database.DatabaseImpl_V2_WorkflowActivity;
import vn.mobileid.id.general.database.DatabaseV2_WorkflowActivity;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowActivity;

/**
 *
 * @author GiaTK
 */
public class GetWorkflowActivity {

    //<editor-fold defaultstate="collapsed" desc="Get Workflow Activity">
    public static InternalResponse getWorkflowActivity(
            int WorkflowActivityId,
            String transactionId
    ) throws Exception {
        DatabaseV2_WorkflowActivity callDb = new DatabaseImpl_V2_WorkflowActivity();

        DatabaseResponse response = callDb.getWorkflowActivity(WorkflowActivityId, transactionId);

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            transactionId));
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                response.getObject()
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Row Count of Workflow Activity">
    public static InternalResponse getRowCountWorkflowActivity(
            String U_EMAIL,
            int pENTERPRISE_ID,
            String EMAIL_SEARCH,
            String DATE_SEARCH,
            String G_TYPE,
            String W_A_STATUS,
            String pPRODUCTION_TYPE_LIST,
            boolean IS_CUSTOM_RANGE,
            Date FROM_DATE,
            Date TO_DATE,
            int pOFFSET,
            int pROW_COUNT,
            String transactionId
    ) throws Exception {
        DatabaseV2_WorkflowActivity callDb = new DatabaseImpl_V2_WorkflowActivity();
        DatabaseResponse response = callDb.getTotalRecordsWorkflowActivity(
                U_EMAIL,
                pENTERPRISE_ID,
                EMAIL_SEARCH,
                DATE_SEARCH,
                G_TYPE,
                W_A_STATUS,
                pPRODUCTION_TYPE_LIST,
                IS_CUSTOM_RANGE,
                FROM_DATE,
                TO_DATE,
                pOFFSET,
                pROW_COUNT,
                transactionId);

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            transactionId));
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                (Object) response.getObject()
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get List of Workflow Activity">
    public static InternalResponse getListWorkflowActivity(
            String U_EMAIL,
            int pENTERPRISE_ID,
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
    ) throws Exception {
        DatabaseV2_WorkflowActivity callDb = new DatabaseImpl_V2_WorkflowActivity();
        DatabaseResponse response = callDb.getListWorkflowActivity(
                U_EMAIL,
                pENTERPRISE_ID,
                EMAIL_SEARCH,
                DATE_SEARCH,
                G_TYPE,
                W_A_STATUS,
                pPRODUCTION_TYPE_LIST,
                IS_CUSTOM_RANGE,
                FROM_DATE,
                TO_DATE,
                pOFFSET,
                pROW_COUNT,
                transactionId);

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            transactionId));
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                (Object) response.getObject()
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Row count of Workflow Activity based on Workflow Id">
    /**
     * Get row count of Workflow Activity based on Workflow Id
     * @param workflowId
     * @param transactionId
     * @return long Row Count
     * @throws Exception 
     */
    public static InternalResponse getRowCountWorkflowActivity_basedOnWorkflow(
            int workflowId,
            String transactionId
    )throws Exception{
        DatabaseV2_WorkflowActivity callDb = new DatabaseImpl_V2_WorkflowActivity();
        DatabaseResponse response = callDb.getTotalRecordsWorkflowActivity_basedOnWorkflow(workflowId, transactionId);
        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            transactionId));
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                (Object) response.getObject()
        );
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Get Lists of Workflow Activity based on Workflow Id">
    /**
     * Get Lists of Workflow Activity based on Workflow Id
     * @param workflowId
     * @param offset
     * @param rowcount
     * @param transactionId
     * @return List<WorkflowActivity>
     * @throws Exception 
     */
    public static InternalResponse getListWorkflowActivity_basedOnWorkflow(
            int workflowId,
            int offset,
            int rowcount,
            String transactionId
    )throws Exception{
        DatabaseV2_WorkflowActivity callDb = new DatabaseImpl_V2_WorkflowActivity();
        DatabaseResponse response = callDb.getListsWorkflowActivity_basedOnWorkflow(
                workflowId,
                offset,
                rowcount,
                transactionId);
        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            transactionId));
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                (Object) response.getObject()
        );
    }
    //</editor-fold>
    
    public static void main(String[] args) throws Exception {
//        InternalResponse response = GetWorkflowActivity.getWorkflowActivity(
//                650,
//                "transactionId");
//        
//        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
//            System.out.println("Mess:"+response.getMessage());
//        } else {
//            WorkflowActivity object = (WorkflowActivity)response.getData();
//            System.out.println(object.getWorkflow_template_type());
//            System.out.println(object.getCreated_at());
//            System.out.println(object.getStatus());
////            System.out.println(object.getStatus_name());
////            System.out.println(object.getStatus_remark());
////            System.out.println(object.getStatus_remark_en());
//        }

        //======================================================================
//        InternalResponse response = GetWorkflowActivity.getRowCountWorkflowActivity(
//                "khanhpx@mobile-id.vn",
//                3, 
//                null,
//                null,
//                "1,2,3,4,5,6,7,8", 
//                "1,2", 
//                "1,2,3", 
//                false, 
//                null, 
//                null, 
//                0, 
//                100, 
//                "transactionId");
//        
//        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
//            System.out.println("Mess:"+response.getMessage());
//        } else {
//            System.out.println("Row:"+response.getData());
//        }
        //======================================================================
//        InternalResponse response = GetWorkflowActivity.getListWorkflowActivity(
//                "khanhpx@mobile-id.vn",
//                3,
//                null,
//                null,
//                "1,2,3,4,5,6,7,8",
//                "1,2",
//                "1,2,3",
//                false,
//                null,
//                null,
//                0,
//                100,
//                "transactionId");
//
//        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//            System.out.println("Mess:" + response.getMessage());
//        } else {
//            for (WorkflowActivity wo : (List<WorkflowActivity>) response.getData()) {
//                System.out.println("Id:" + wo.getWorkflow_label());
//            }
//        }

        //======================================================================
        InternalResponse response = GetWorkflowActivity.getListWorkflowActivity_basedOnWorkflow(507, 0,100,"transactionId");
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            System.out.println("Mess:"+response.getMessage());
        } else { 
            List<WorkflowActivity> lists = (List<WorkflowActivity>)response.getData();
            for(WorkflowActivity a : lists){
                System.out.println("Id:"+a.getId());
            }
        }
    }
}
