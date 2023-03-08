/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.QryptoConstant;
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;
import vn.mobileid.id.paperless.objects.WorkflowTemplateType;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class GetWorkflowTemplateType {

    final private static Logger LOG = LogManager.getLogger(GetWorkflowTemplateType.class);

    /**
     * Get the workflow template type of the workflow input
     *
     * @param id ID of workflow
     * @return InternalResponse with WorkflowTemplateType(setObject)
     */
    public static InternalResponse getWorkflowTemplateTypeFromDB(int id) {
        try {
            Database DB = new DatabaseImpl();

            DatabaseResponse callDB = DB.getTemplateType(id);

            if (callDB.getStatus() != QryptoConstant.CODE_SUCCESS) {
                String message = null;
                if (LogHandler.isShowErrorLog()) {
                    message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            callDB.getStatus(),
                            "en",
                             null);
                    LOG.error("Cannot get Workflow Template Type  - Detail:" + message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            WorkflowTemplateType templateType = (WorkflowTemplateType) callDB.getObject();

            return new InternalResponse(
                    QryptoConstant.HTTP_CODE_SUCCESS,
                    templateType);

        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    /**
     * Get the workflow template type of the workflow template type input
     *
     * @param id ID of workflow
     * @return InternalResponse with WorkflowTemplateType(setObject)
     */
    public static InternalResponse getWorkflowTemplateType(int id) {
        if (Resources.getListWorkflowTemplateType().isEmpty()) {
            Resources.reloadListWorkflowTemplateType();
        }
        WorkflowTemplateType temp = Resources.getListWorkflowTemplateType().get(String.valueOf(id));
        if (temp != null) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS, temp);
        }

        //Read from DB
        InternalResponse res = getWorkflowTemplateTypeFromDB(id);
        if (res.getStatus() == QryptoConstant.HTTP_CODE_SUCCESS) {
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
