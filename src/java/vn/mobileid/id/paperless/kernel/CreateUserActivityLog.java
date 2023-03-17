/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowActivity;

/**
 *
 * @author GiaTK
 */
public class CreateUserActivityLog {

//    final private static Logger LOG = LogManager.getLogger(CreateUserActivityLog.class);

    public static boolean checkData(WorkflowActivity workflow) {
        if (workflow == null) {
            return false;
        }
        if (workflow.getEnterprise_id() <= 0 && workflow.getEnterprise_name() == null) {
            return false;
        }
        if (workflow.getCreated_by() == null) {
            return false;
        }
        return true;
    }

    public static InternalResponse processingCreateUserActivityLog(
            WorkflowActivity workflow,
            User user,
            String transactionID) {
        try {
            Database DB = new DatabaseImpl();

            //Create new User_Activity_log
            DatabaseResponse callDB = DB.createUserActivityLog(
                    user.getEmail(), //email 
                    user.getAid(),//enterprise_id
                    null, //module
                    null, //action
                    null, //info_key
                    null, //info_value
                    null, //detail
                    null, //agent
                    null, //agent_detail
                    null, //HMAC
                    user.getName(),
                    transactionID);//created_by                

            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                message = QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                         null);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(CreateUserActivityLog.class,"TransactionID:"+transactionID+"\nCannot create User_Activity_log - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                    String.valueOf(callDB.getIDResponse()));
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(CreateUserActivityLog.class,
                        "TransactionID:"+transactionID+
                        "\nCannot create User_Activity_log - Detail:" + e);
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    e.getMessage()
            );
        }
    }

    public static void main(String[] args) {
//        WorkflowActivity object = new WorkflowActivity();
//        object.setEnterprise_id(3);
//        object.setCreated_by("GIATK");
//        User user = new User();
//        user.setEmail("giatk@mobile-id.vn");
//
//        CreateUserActivityLog.processingCreateUserActivityLog(object, user);
    }
}
