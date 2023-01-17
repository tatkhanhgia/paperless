
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
import vn.mobileid.id.qrypto.objects.Item_JSNObject;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.qrypto.objects.WorkflowActivity_JSNObject;
import vn.mobileid.id.qrypto.objects.ItemDetails;
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
            String QRUUID = "";
            
            InternalResponse response = null;
            //Create new User Activity Log
//            boolean check1 = CreateUserActivityLog.checkData(workflow);
//            if(check1 == false){
//                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
//                        QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
//                                QryptoConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
//                                "en",
//                                null));
//            }           
            response = CreateUserActivityLog.processingCreateUserActivityLog(workflow, user);
            if(response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS){
                return response;
            }
            logID = Integer.parseInt(response.getMessage());
            
            //Create new File Management
            response = CreateFileManagement.processingCreateFileManagement(
                    workflow,
                    "HMAC",
                    null,
                    user);
            if(response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS){
                return response;
            }
            fileManagementID = Integer.parseInt(response.getMessage());
            
            //Create new QR
            response = CreateQR.processingCreateQR(
                    "metaData",
                    "HMAC",
                    workflow.getCreated_by());
                    
            if(response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS){
                return response;
            }
            QRUUID = response.getMessage();
            
            //Create new Transaction
            response = CreateTransaction.processingCreateTransaction(
                    logID,
                    Integer.parseInt(QRUUID),
                    1,  //Type QR:1 CSV:2
                    user,
                    workflow.getCreated_by());
            if(response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS){
                return response;
            }
            transactionID = response.getMessage();
                    
            //Create new Workflow Activity
            DatabaseResponse callDB = DB.createWorkflowActivity(
                workflow.getEnterprise_id(), //enterpriseID
                workflow.getWorkflow_id(), //workflowID
                user.getEmail(),    //useremail
                transactionID, //transactionid
                fileManagementID,  //file link
                -1,  //csv
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
    
    public static void main(String[] args){
        WorkflowActivity_JSNObject object = new WorkflowActivity_JSNObject();
        object.setEnterprise_id(3);
        object.setWorkflow_id(8);
        object.setWorkflow_template_type(3);
        object.setRemark("Remak");
        object.setUse_test_token(false);
        object.setUpdate_enable(false);
        object.setCreated_by("GIATK");
        
        User user = new User();
        user.setEmail("giatk@mobile-id.vn");
        
        CreateWorkflowActivity.processingCreateWorkflowActivity(object, user);
    }
}
