/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowActivity;

/**
 *
 * @author GiaTK
 */
@Deprecated
public class GetWorkflowActivity {

    /**
     * Get data of workflow activity from RAM
     *
     * @param id - id of workflow activity
     * @param transactionID
     * @return WorkflowActivity
     * @throws Exception
     */
    public static InternalResponse getWorkflowActivity(
            int id,
            String transactionID) throws Exception {
        InternalResponse res = null;
        WorkflowActivity check = Resources.getWorkflowActivity(String.valueOf(id));
        if (check == null) {
            res = getWorkflowActivityFromDB(id, transactionID);
            if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return res;
            }
            check = (WorkflowActivity) res.getData();
            Resources.putIntoRAM(String.valueOf(check.getId()), check);
//            Thread temp = new Thread() {
//                public void run() {
//                    try {
//                        Resources.reloadListWorkflowActivity();
//                    } catch (Exception ex) {
//                        Logger.getLogger(GetWorkflowActivity.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            };
//            temp.start();
            return res;
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                check);
    }

    /**
     * Get data of workflow activity from DB
     *
     * @param id - id of workflow activity
     * @param transactionID
     * @return WorkflowActivity
     * @throws Exception
     */
    public static InternalResponse getWorkflowActivityFromDB(
            int id,
            String transactionID) throws Exception {
        Database db = new DatabaseImpl();
        DatabaseResponse res = db.getWorkflowActivity(id, transactionID);
        if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_FAIL,
                    res.getStatus(),
                    "en",
                    null);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message);
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                (WorkflowActivity) res.getObject());
    }

    //Plan2 get from DB
    public static InternalResponse getListWorkflowActivity(
            String email,
            int aid,
            String email_search,
            Date date,
            String g_type,
            String status,
            boolean is_test,
            boolean is_product,
            boolean is_custom_range,
            Date fromdate,
            Date todate,
            String languagename,
            int offset,
            int rowcount,
            String transactionID
    ) throws Exception {

        Database db = new DatabaseImpl();
        DatabaseResponse res = db.getListWorkflowActivityWithCondition(
                email,
                aid,
                email_search,
                date,
                g_type,
                status,
                is_test,
                is_product,
                is_custom_range,
                fromdate,
                todate,
                languagename,
                offset,
                rowcount,
                transactionID);
        if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = null;
            message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_FAIL,
                    res.getStatus(),
                    "en",
                    null);
//                LogHandler.error(GetWorkflowActivity.class,
//                        "TransactionID:" + transactionID
//                        + "\nCannot get List Workflow Activitys - Detail:" + message);
//            }
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                (List<WorkflowActivity>) res.getObject());

    }

    public static InternalResponse getRowCountWorkflowActivity(
            String email,
            int enterpriseId,
            String emailSearch,
            String date,
            String gType,
            String status,
            boolean isTest,
            boolean isProduct,
            boolean isCustomRange,
            String fromDate,
            String toDate,
            String languageName,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = new DatabaseImpl().getTotalRecordsWorkflowActivity(
                email,
                enterpriseId,
                emailSearch,
                date,
                gType,
                status,
                isTest,
                isProduct,
                isCustomRange,
                fromDate,
                toDate,
                languageName,
                transactionId);

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            null)
            );
        }
        
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                (Object)response.getObject()
        );
    }

    public static void main(String[] args) throws JsonProcessingException, Exception {
//        DatabaseImpl db = new DatabaseImpl();
//        List<WorkflowActivity> list = GetWorkflowActivity.getListWorkflowActivity(
//                3,
//                "khanhpx@mobile-id.vn",
//                "transactionID"
//        );

//        List<WorkflowActivity> list = (List<WorkflowActivity>) GetWorkflowActivity.getListWorkflowActivity(
//                "khanhpx@mobile-id.vn",
//                3, //aid
//                null, //email search
//                null, //date
//                "1,2,3,4,5,6", //generationType       
//                "1,2,3", //status
//                false,
//                false,
//                false,
//                null, //fromdate
//                null, //todate
//                "ENG",
//                "transactionID").getData();
//
//        CustomListWoAcSerializer test = new CustomListWoAcSerializer(list, 0, 0);
//        System.out.println(new ObjectMapper().writeValueAsString(test));
//        System.out.println(GetWorkflowActivity.isContains(30));
        InternalResponse res = GetWorkflowActivity.getRowCountWorkflowActivity(
                "khanhpx@mobile-id.vn", 
                3,
                null,
                null, 
                "1,2,3,4,5,6,7,8",
                "1,2,3", 
                false,
                false, 
                false, 
                null, 
                null, 
                "ENG", 
                "tran");
        if(res.getStatus() == 200){
            System.out.println("Count:"+res.getData());
        } else {
            System.out.println("Fail!");
        }
    }
    
}
