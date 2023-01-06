
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
import vn.mobileid.id.qrypto.objects.QryptoItemWorkflowDetailJSNObject;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.qrypto.objects.QryptoWorkflowActivityJSNObject;
import vn.mobileid.id.qrypto.objects.QryptoWorkflowDetailJSNObject;
import vn.mobileid.id.qrypto.objects.QryptoWorkflowJSNObject;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class CreateWorkflowActivity {

    final private static Logger LOG = LogManager.getLogger(CreateWorkflow.class);

    public static InternalResponse checkDataWorkflowActivity(QryptoWorkflowActivityJSNObject workflowAc) {
        
        
        return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                QryptoMessageResponse.getMessage(QryptoConstant.CODE_SUCCESS,
                        QryptoConstant.SUBCODE_SUCCESS,
                        "en",
                        null));
    }
    
    
    public static InternalResponse processingCreateWorkflowActivity(QryptoWorkflowActivityJSNObject workflow, String user_mail) {
        try {
            Database DB = new DatabaseImpl();
            
            //Create new User_Activity_log
            DatabaseResponse createWorkflowAc = DB.createUserActivityLog(
                    user_mail, //email
                    workflow.getEnterprise_id(),         //enterprise_id
                    null, //module
                    null, //action
                    null, //info_key
                    null, //info_value
                    null, //detail
                    null, //agent
                    null, //agent_detail
                    null, //HMAC
                    workflow.getCreated_by());//created_by                
            
            if(createWorkflowAc.getStatus() != QryptoConstant.CODE_SUCCESS ){
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        QryptoMessageResponse.getMessage(
                                QryptoConstant.CODE_FAIL,
                                createWorkflowAc.getStatus(),
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
