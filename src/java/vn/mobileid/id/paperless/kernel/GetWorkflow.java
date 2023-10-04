/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import java.util.List;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.Workflow;

/**
 *
 * @author GiaTK
 */
@Deprecated
public class GetWorkflow {

    /**
     * Get data of the Workflow
     *
     * @param id - ID of the Workflow
     * @param transactionID
     * @return Workflow
     * @throws Exception
     */
    public static InternalResponse getWorkflow(
            int id,
            String transactionID)
            throws Exception {

        Database DB = new DatabaseImpl();

        DatabaseResponse callDB = DB.getWorkflow(
                id,
                transactionID);

        try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                message = PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            Workflow workflow = (Workflow) callDB.getObject();
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    workflow);

        } catch (Exception e) {
            throw new Exception("Cannot get Workflow!", e);
        }
    }

    /**
     * Get a list of Workflow with Condition
     * @param email - Email of User
     * @param enterprise_id - Enterprise id
     * @param status - Condition status
     * @param type - Condition type
     * @param use_metadata - Condition use_metadata
     * @param metadata - Condition metadata
     * @param offset - Condition Offset (get from offset position)
     * @param rowcount - Condition Rowcount (get total Rowcount record)
     * @param transactionID
     * @return List of Workflow
     * @throws Exception 
     */
    public static InternalResponse getListWorkflow(
            String email,
            int enterprise_id,
            String status,
            String type,
            boolean use_metadata,
            String metadata,
            int offset,
            int rowcount,
            String transactionID
    ) throws Exception {

        Database DB = new DatabaseImpl();        

        DatabaseResponse callDB = DB.getListWorkflow(
                email,
                enterprise_id,
                status,
                type,
                use_metadata,
                metadata,
                offset,
                rowcount,
                transactionID);

        try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                message = PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            List<Workflow> object = (List<Workflow>) callDB.getObject();

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    object);

        } catch (Exception e) {
            throw new Exception("Cannot get list of workflow!", e);
        }
    }

    /**
     * Get total number of record (Workdlow) with meet the condition
     * @param email
     * @param enterprise_id
     * @param status
     * @param type
     * @param use_metadata
     * @param metadata
     * @param transactionID
     * @return
     * @throws Exception 
     */
    public static InternalResponse getTotalWorkflowWithCondition(
            String email,
            int enterprise_id,
            String status,
            String type,
            boolean use_metadata,
            String metadata,
            String transactionID
    ) throws Exception {

        Database DB = new DatabaseImpl();        

        DatabaseResponse callDB = DB.getTotalRecordsWorkflow(
                email,
                enterprise_id,
                status,
                type,
                use_metadata,
                metadata,
                transactionID);

        try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                message = PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
//                LogHandler.error(GetWorkflow.class, "TransactionID:" + transactionID + "\nCannot get list Workflow - Detail:" + message);
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    callDB.getObject());

        } catch (Exception e) {
            throw new Exception("Cannot get list of workflow!", e);
        }
    }

    public static void main(String[] args) throws Exception {
        List<Workflow> lis = (List<Workflow>) GetWorkflow.getListWorkflow(
                "khanhpx@mobile-id.vn",
                3,
                "1,2",
                "1,2,3,4,5,6,7,8",
                false,
                "",
                0,
                10,
                "transactionID").getData();
        for (Workflow a : lis) {
            System.out.println(a.getWorkflow_id());
        }

//        Workflow a = (Workflow)GetWorkflow.getWorkflow(30).getData();
//        System.out.println("ID:"+a.getWorkflow_id());
//        System.out.println("Template:"+a.getTemplate_type_name());
//
//        System.out.println("Workflow type:"+a.getWorkflow_type_name());
    }
}
