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
import vn.mobileid.id.qrypto.objects.WorkflowActivity_JSNObject;

/**
 *
 * @author GiaTK
 */
public class CreateUserActivityLog {
    final private static Logger LOG = LogManager.getLogger(CreateUserActivityLog.class);
    
    public static boolean checkData(WorkflowActivity_JSNObject workflow){
        if(workflow == null){
            return false;
        }
        if(workflow.getEnterprise_id() <=0 && workflow.getEnterprise_name() == null){
            return false;
        }
        if(workflow.getCreated_by() == null){
            return false;
        }
        return true;
    }    
    
    public static InternalResponse processingCreateUserActivityLog(WorkflowActivity_JSNObject workflow, User user) {
    try {
            Database DB = new DatabaseImpl();
            
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
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                    String.valueOf(callDB.getIDResponse()));
    }catch(Exception e){
        if(LogHandler.isShowErrorLog()){
                    
                    LOG.error("Cannot create User_Activity_log - Detail:"+e);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        e.getMessage()
                );
    }
    }
    
    public static void main(String[] args){
        WorkflowActivity_JSNObject object = new WorkflowActivity_JSNObject();
        object.setEnterprise_id(3);
        object.setCreated_by("GIATK");      
        User user = new User();
        user.setEmail("giatk@mobile-id.vn");
        
        CreateUserActivityLog.processingCreateUserActivityLog(object, user);
    }
}
