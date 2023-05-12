/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowTemplateType;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class GetWorkflowTemplateType {

    /**
     * Get the workflow template type of the workflow input
     *
     * @param id ID of workflow
     * @return InternalResponse with WorkflowTemplateType(setObject)
     */
    public static InternalResponse getWorkflowTemplateTypeFromDB(
            int id,
            String transactionID) throws Exception {
        
            Database DB = new DatabaseImpl();

            DatabaseResponse callDB = DB.getTemplateType(
                    id,
                    transactionID);

            try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            callDB.getStatus(),
                            "en",
                             null);
                if (LogHandler.isShowErrorLog()) {                    
                    LogHandler.error(GetWorkflowTemplateType.class,transactionID,"Cannot get Workflow Template Type  - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            WorkflowTemplateType templateType = (WorkflowTemplateType) callDB.getObject();

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    templateType);

        } catch (Exception e) {
            throw new Exception("Cannot get workflow template type!", e);           
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    /**
     * Get the workflow template type of the workflow template type input
     *
     * @param id ID of workflow
     * @return InternalResponse with WorkflowTemplateType(setObject)
     */
    public static InternalResponse getWorkflowTemplateType(
            int id,
            String transactionID
    ) throws Exception {
        if (Resources.getListWorkflowTemplateType().isEmpty()) {
            Resources.reloadListWorkflowTemplateType();
        }
        WorkflowTemplateType temp = Resources.getListWorkflowTemplateType().get(String.valueOf(id));
        if (temp != null) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, temp);
        }

        //Read from DB
        InternalResponse res = getWorkflowTemplateTypeFromDB(
                id,
                transactionID);
        if (res.getStatus() == PaperlessConstant.HTTP_CODE_SUCCESS) {
            Resources.reloadListWorkflowTemplateType();
            return res;
        }
        return res;
    }

    public static void main(String[] args) {
//        InternalResponse a = GetWorkflowTemplateType.getWorkflowTemplateType(12);
//        WorkflowTemplateType b = (WorkflowTemplateType) a.getData();
//        System.out.println(b.getName());
    }
}
