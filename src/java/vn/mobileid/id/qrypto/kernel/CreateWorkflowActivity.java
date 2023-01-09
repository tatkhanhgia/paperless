
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
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.objects.WorkflowDetail_Item_JSNObject;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.qrypto.objects.WorkflowActivity_JSNObject;
import vn.mobileid.id.qrypto.objects.WorkflowDetail_JSNObject;
import vn.mobileid.id.qrypto.objects.Workflow_JSNObject;
import vn.mobileid.id.qrypto.objects.response.Create_WorkflowActivity_MessageJSNObject;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class CreateWorkflowActivity {

    final private static Logger LOG = LogManager.getLogger(CreateWorkflow.class);

    public static InternalResponse checkDataWorkflowActivity(WorkflowActivity_JSNObject workflowAc) {
        if(workflowAc.getEnterprise_name() == null && workflowAc.getEnterprise_id() <= 0){
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                        QryptoConstant.SUBCODE_MISSING_ENTERPRISE_DATA,
                        "en",
                        null));
        }
        
        if(workflowAc.getWorkflow_id()<=0){
            return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                        QryptoConstant.SUBCODE_MISSING_WORKFLOW_ID,
                        "en",
                        null));
        }
        return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_SUCCESS,
                        QryptoConstant.SUBCODE_SUCCESS,
                        "en",
                        null));
    }
    
    
    public static InternalResponse processingCreateWorkflowActivity(WorkflowActivity_JSNObject workflow, User user) {
        try {
            Database DB = new DatabaseImpl();
            //Data
            int logID = -1;
            int fileManagementID = -1;
            String transactionID = "";
            
            //Create new User_Activity_log
            DatabaseResponse callDB = DB.createUserActivityLog(
                    user.getEmail(), //email 
                    workflow.getEnterprise_id(),//enterprise_id
                    null, //module
                    null, //action
                    null, //info_key
                    null, //info_value
                    null, //detail
                    null, //agent
                    null, //agent_detail
                    null, //HMAC
                    workflow.getCreated_by());//created_by                
                        
            if(callDB.getStatus() != QryptoConstant.CODE_SUCCESS ){
                String message = null;
                if(LogHandler.isShowErrorLog()){
                    message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                                callDB.getStatus(),
                                "en"
                                , null);
                    LOG.error("Cannot create User_Activity_log - Detail:"+message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            logID = callDB.getIDResponse();            
            
            //Create new File Management
            callDB = DB.createFileManagement(
                    "null", //UUID
                    null,   //name
                    0,      //page
                    0,      //size
                    0,      //width
                    0,      //height
                    "HMAC", //HMAC
                    workflow.getCreated_by());
            if(callDB.getStatus() != QryptoConstant.CODE_SUCCESS ){
                String message = null;
                if(LogHandler.isShowErrorLog()){
                    message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                                callDB.getStatus(),
                                "en"
                                , null);
                    LOG.error("Cannot create File Management - Detail:"+message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            fileManagementID = callDB.getIDResponse();
            
            //Create new Transaction
            callDB = DB.createTransaction(
                    user.getEmail(),  //email
                    logID,      //logID
                    -1,      //QRUUID
                    -1,      //CSV_Task
                    0,       //enable_CSV_Task
                    null,  //IPAddress
                    null,  //initFile
                    0,      //pY
                    0,      //pX
                    0,      //pC
                    0,      //pS
                    0,      //pages
                    "No Description",  //des   
                    "HMAC",  //hmac
                    workflow.getCreated_by()   //created_by
            );
            if(callDB.getStatus() != QryptoConstant.CODE_SUCCESS ){
                String message = null;
                if(LogHandler.isShowErrorLog()){
                    message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                                callDB.getStatus(),
                                "en"
                                , null);
                    LOG.error("Cannot create Transaction - Detail:"+message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            transactionID = callDB.getTransactionID();
                    
            //Create new Workflow Activity
            callDB = DB.createWorkflowActivity(
                workflow.getEnterprise_id(), //enterpriseID
                workflow.getWorkflow_id(), //workflowID
                user.getEmail(),    //useremail
                transactionID, //transactionid
                QryptoConstant.FLAG_FALSE_DB,  //file link
                QryptoConstant.FLAG_FALSE_DB,  //csv
                workflow.getRemark(), //remark
                workflow.isUse_test_token(),  //use test token
                workflow.isIs_production(),  //is production
                workflow.isUpdate_enable(),  //is update
                workflow.getWorkflow_template_type(),  //workflow type
                "none request data",  //request data
                "hmac",   //hmac
                workflow.getCreated_by());  //created by
            if(callDB.getStatus() != QryptoConstant.CODE_SUCCESS ){              
                String message = null;
                if(LogHandler.isShowErrorLog()){
                    message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                                callDB.getStatus(),
                                "en"
                                , null);
                    LOG.error("Cannot create Workflow Activity - Detail:"+message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            
            //Convert to object and return it to client
            Create_WorkflowActivity_MessageJSNObject object = new Create_WorkflowActivity_MessageJSNObject();
            object.setCode(QryptoConstant.CODE_SUCCESS);
            object.setWorkflowActivityID(String.valueOf(callDB.getIDResponse()));
            
            return new InternalResponse(
                    QryptoConstant.CODE_SUCCESS,
                    new ObjectMapper().writeValueAsString(object));
            
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
            return new InternalResponse(500,QryptoConstant.INTERNAL_EXP_MESS);
        }        
    }
}
