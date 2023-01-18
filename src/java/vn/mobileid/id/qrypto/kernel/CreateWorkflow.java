/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.kernel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.qrypto.objects.Workflow;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class CreateWorkflow {

    final private static Logger LOG = LogManager.getLogger(CreateWorkflow.class);

    public static InternalResponse checkDataWorkflow(Workflow workflow) {
        if (workflow == null) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            "en",
                            null));
        }
        if (Utils.isNullOrEmpty(workflow.getLabel())) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            QryptoConstant.SUBCODE_MISSING_WORKFLOW_LABEL,
                            "en",
                            null));
        }        
        if (Utils.isNullOrEmpty(workflow.getCreated_by())) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            QryptoConstant.SUBCODE_MISSING_WORKFLOW_CREATED_BY,
                            "en",
                            null));
        }
        return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_SUCCESS,
                        QryptoConstant.SUBCODE_SUCCESS,
                        "en",
                        null));
    }

    public static InternalResponse processingCreateWorkflow(Workflow workflow, User user) {
        try {
            Database DB = new DatabaseImpl();

            DatabaseResponse createWorkflow = DB.createWorkflow(
                    workflow.getTemplate_type(),
                    workflow.getLabel(),
                    workflow.getCreated_by(),
                    user.getEmail()
            );
            
            if(createWorkflow.getStatus() != QryptoConstant.CODE_SUCCESS ){
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        QryptoMessageResponse.getErrorMessage(
                                QryptoConstant.CODE_FAIL,
                                createWorkflow.getStatus(),
                                "en"
                                , null)
                );
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                    "{\"workflow_id\":"+createWorkflow.getIDResponse()+"}"
            );
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
            return new InternalResponse(500,QryptoConstant.INTERNAL_EXP_MESS);
        }        
    }
}
