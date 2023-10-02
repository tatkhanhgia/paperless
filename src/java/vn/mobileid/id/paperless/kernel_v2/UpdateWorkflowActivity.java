/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.database.DatabaseImpl_V2_WorkflowActivity;
import vn.mobileid.id.general.database.DatabaseV2_WorkflowActivity;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.object.enumration.WorkflowActivityStatus;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class UpdateWorkflowActivity {

    //<editor-fold defaultstate="collapsed" desc="Update Request Data of the WorkflowActivity">
    /**
     * Update request Data of the workflow activity
     *
     * @param id
     * @param meta_data
     * @param modified_by
     * @param transactionID
     * @return
     * @throws Exception
     */
    public static InternalResponse updateMetadata(
            int id,
            String meta_data,
            String modified_by,
            String transactionID
    ) throws Exception {
        DatabaseV2_WorkflowActivity callDb = new DatabaseImpl_V2_WorkflowActivity();

        DatabaseResponse response = callDb.updateRequestDataOfWorkflowActivity(
                id,
                meta_data,
                modified_by,
                transactionID);

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            transactionID)
            );
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Update Status of Workflow Activity">
    public static InternalResponse updateStatus(
            int id,
            WorkflowActivityStatus status,
            String last_modified_by,
            String transactionID
    ) throws Exception {
        DatabaseV2_WorkflowActivity callDb = new DatabaseImpl_V2_WorkflowActivity();
        DatabaseResponse response = callDb.updateStatusWorkflowActivity(
                id,
                status.getName(),
                last_modified_by,
                transactionID);

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            transactionID)
            );
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>

    public static void main(String[] args) throws Exception {
//        InternalResponse response = UpdateWorkflowActivity.updateMetadata(
//                200,
//                "meta data ", 
//                "GiaTK", 
//                "transactionId");

        InternalResponse response = UpdateWorkflowActivity.updateStatus(
                200,
                WorkflowActivityStatus.ACTIVE,
                "GiaTK",
                "transactionId");

        System.out.println("Mess:" + response.getMessage());
    }
}
