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
    
    public static InternalResponse updateWorkflowOption(
            int id,
            WorkflowDetail_Option detail,
            String hmac,
            String created_by){
        try {
            InternalResponse response = null;
            Database DB = new DatabaseImpl();

            DatabaseResponse callDB = DB.updateWorkflowDetail_Option(
                    id,
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
    
    public static void main(String[] args){
        WorkflowDetail_Option detail = new WorkflowDetail_Option();
//        detail.setMetadata("metadata 1-2-3-4-4");
        detail.setCSV_email(true);
                
        InternalResponse res = UpdateWorkflowDetail_option.updateWorkflowOption(
                12,
                detail,
                "",
                "GIATK");
        
        System.out.println("Mes:"+res.getMessage());
    }
    
}
