/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class UpdateWorkflowActivity {

    /**
     * Update Requestdata of WorkflowActivity
     * @param id
     * @param metadata
     * @param modified_by
     * @param transactionID
     * @return
     * @throws Exception 
     */
    public static InternalResponse updateMetadata(
            int id,
            String metadata,
            String modified_by,
            String transactionID
    ) throws Exception {
        Database DB = new DatabaseImpl();
        DatabaseResponse response = DB.updateRequestDataOfWorkflowActivity(
                id,
                metadata,
                modified_by,
                transactionID);
        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_FAIL,
                    response.getStatus(),
                    "en",
                    null);
//            LogHandler.error(UpdateWorkflowDetail_option.class, transactionID, "Cannot update Request data of Workflow Activity - Detail:" + message);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "");
    }   
   
   /**
     * Update status of the workflow activity
     * @param id
     * @param status
     * @param isProcess
     * @param modified_by
     * @param transactionID
     * @return
     * @throws Exception 
     */
    public static InternalResponse updateStatus(
            int id,
            String status,
            String modified_by,
            String transactionID
    ) throws Exception{
        Database callDB = new DatabaseImpl();
        DatabaseResponse DBres = callDB.updateStatusWorkflowActivity_(
                id,
                status,
                modified_by,
                transactionID);
        if(DBres.getStatus() != PaperlessConstant.CODE_SUCCESS){
            String message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_FAIL,
                    DBres.getStatus(),
                    "en",
                    null);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "");
    }
}

