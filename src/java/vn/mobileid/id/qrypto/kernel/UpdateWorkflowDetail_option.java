/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.kernel;

import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.qrypto.objects.WorkflowDetail_Option;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class UpdateWorkflowDetail_option {

    final private static Logger LOG = LogManager.getLogger(UpdateWorkflowDetail_option.class);

    public static InternalResponse updateWorkflowDetail(int id, WorkflowDetail_Option detail, String hmac, String created_by) {
        try {
            InternalResponse response = null;
            Database DB = new DatabaseImpl();

            DatabaseResponse callDB = DB.updateWorkflowDetail(id,
                    detail.getHashMap(),
                    hmac,
                    created_by);

            if (callDB.getStatus() != QryptoConstant.CODE_SUCCESS) {
                String message = null;
                message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                            callDB.getStatus(),
                            "en",
                            null);
                if (LogHandler.isShowErrorLog()) {                    
                    LOG.error("Cannot update Workflow Detail - Detail:" + message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }            
            if (callDB.getStatus() == QryptoConstant.CODE_FAIL) {
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        "\"message\":\"Cannot update Workflow Detail - Option\""
                );
            }

            return new InternalResponse(
                    QryptoConstant.CODE_SUCCESS,
                    "");

        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    public static void main(String[] args) {
        int id = 12;
        WorkflowDetail_Option detail = new WorkflowDetail_Option();
        detail.setQr_background("WHITE");
        detail.setQr_size(4);
        detail.setQr_type(1);
        detail.setUrl_code(false);
//        detail.setPage(1);
//        detail.setStamp_in(1);
//        detail.setPage_size("A4");
//        detail.setAsset_Background(0);
        detail.setAsset_Template(7);
//        detail.setAsset_Append(0);
        detail.setDisable_CSV_task_notification_email(false);
        detail.setCSV_email(false);
        detail.setOmit_if_empty(false);
        detail.setEmail_notification(false);

        String hmac = "HMAC";
        String created_by = "GIATK";
        
        InternalResponse response = UpdateWorkflowDetail_option.updateWorkflowDetail(
                id,
                detail,
                hmac,
                created_by);
        System.out.println("Response:"+response.getStatus());
        System.out.println("Mes:"+response.getMessage());
    }
}
