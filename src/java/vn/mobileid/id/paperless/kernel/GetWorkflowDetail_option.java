/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;

/**
 *
 * @author GiaTK
 */
@Deprecated
public class GetWorkflowDetail_option {

    /**
     * Get Workflow Detail/Option of the workflow input
     *
     * @param id Workflow ID
     * @return InternalReponse with WorkflowDetail_Option(Object).
     */
    public static InternalResponse getWorkflowDetail(
            int id,
            String transactionID) throws Exception {

        Database DB = new DatabaseImpl();        

        DatabaseResponse callDB = DB.getWorkflowDetail(
                id,
                transactionID);

        try {
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

            WorkflowDetail_Option detail = (WorkflowDetail_Option) callDB.getObject();

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    detail);

        } catch (Exception e) {
            throw new Exception("Cannot get workflow detail/options!", e);
        }
    }

    public static void main(String[] args) {
//        int id = 12;
//        InternalResponse res = GetWorkflowDetail_option.getWorkflowDetail(id);
//        System.out.println(res.getStatus());
//        System.out.println(res.getMessage());
    }
}
