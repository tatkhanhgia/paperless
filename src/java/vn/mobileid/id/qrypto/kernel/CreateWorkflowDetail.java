
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
import vn.mobileid.id.qrypto.objects.WorkflowDetail_Item_JSNObject;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.qrypto.objects.WorkflowDetail_JSNObject;
import vn.mobileid.id.qrypto.objects.Workflow_JSNObject;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class CreateWorkflowDetail {

    final private static Logger LOG = LogManager.getLogger(CreateWorkflow.class);

    public static InternalResponse checkDataWorkflowDetail(WorkflowDetail_Item_JSNObject workflow) {
        for(WorkflowDetail_JSNObject detail : workflow.getItem()){
            InternalResponse response = checkDataWorkflowDetail(detail);
            if(response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS){
                return response;
            }
        }
        
        return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_SUCCESS,
                        QryptoConstant.SUBCODE_SUCCESS,
                        "en",
                        null));
    }
    
    private static InternalResponse checkDataWorkflowDetail(WorkflowDetail_JSNObject workflow) {
        if (workflow == null) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            "en",
                            null));
        }
        if (workflow.getC() == null || workflow.getC().size() == 0) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            QryptoConstant.SUBCODE_MISSING_ARRAY_FIELD_C,
                            "en",
                            null));
        }
//        if (Utils.isNullOrEmpty(workflow.getT())) {
//            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
//                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_WORKFLOW,
//                            QryptoConstant.SUBCODE_MISSING_WORKFLOW_USER_EMAIL_OR_ID,
//                            "en",
//                            null));
//        }
        
        return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_SUCCESS,
                        QryptoConstant.SUBCODE_SUCCESS,
                        "en",
                        null));
    }

    public static InternalResponse processingCreateWorkflowDetail(int workflow_id, WorkflowDetail_Item_JSNObject workflow, String user_mail) {
        try {
            Database DB = new DatabaseImpl();

            DatabaseResponse createWorkflow = DB.createWorkflowTemplate(
                    workflow_id,
                    new ObjectMapper().writeValueAsString(workflow),
                    "HMAC",
                    user_mail
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
                    ""
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
