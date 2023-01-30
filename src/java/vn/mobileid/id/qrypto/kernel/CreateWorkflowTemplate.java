
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.kernel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.objects.Item_JSNObject;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.qrypto.objects.ItemDetails;
import vn.mobileid.id.qrypto.objects.Workflow;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class CreateWorkflowTemplate {

    final private static Logger LOG = LogManager.getLogger(CreateWorkflow.class);

    public static InternalResponse checkDataWorkflowTemplate(Item_JSNObject workflow) {
        for (ItemDetails detail : workflow.getItems()) {
            InternalResponse response = checkDataWorkflowTemplate(detail);
            if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
        }

        return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_SUCCESS,
                        QryptoConstant.SUBCODE_SUCCESS,
                        "en",
                        null));
    }

    public static InternalResponse checkDataWorkflowTemplate(ItemDetails workflow) {
        if (workflow == null) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            "en",
                            null));
        }
        if (workflow.getField() == null || workflow.getField().isEmpty() || workflow.getField().length() <= 0) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            QryptoConstant.SUBCODE_MISSING_INPUT_FIELD,
                            "en",
                            null));
        }
        if (workflow.getType() <= 0 || workflow.getType() > QryptoConstant.NUMBER_OF_ITEMS_TYPE) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            QryptoConstant.SUBCODE_MISSING_OR_ERROR_FIELD_TYPE,
                            "en",
                            null));
        }
        if( workflow.getValue() == null){
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            QryptoConstant.SUBCODE_MISSING_OR_ERROR_VALUE,
                            "en",
                            null));
        }

        return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_SUCCESS,
                        QryptoConstant.SUBCODE_SUCCESS,
                        "en",
                        null));
    }

    public static InternalResponse processingCreateWorkflowTemplate(int workflow_id, Item_JSNObject workflow, String user_mail) {
        try {
            Database DB = new DatabaseImpl();

            DatabaseResponse createWorkflow = DB.createWorkflowTemplate(
                    workflow_id,
                    new ObjectMapper().writeValueAsString(workflow),
                    "HMAC",
                    user_mail
            );

            if (createWorkflow.getStatus() != QryptoConstant.CODE_SUCCESS) {
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        QryptoMessageResponse.getErrorMessage(
                                QryptoConstant.CODE_FAIL,
                                createWorkflow.getStatus(),
                                "en",
                                 null)
                );
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                    ""
            );
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }
}
