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
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class GetWorkflowDetail_option {
//    final private static Logger LOG = LogManager.getLogger(GetWorkflowDetail_option.class);
    
    /**
     * Get Workflow Detail/Option of the workflow input
     * @param id Workflow ID
     * @return InternalReponse with WorkflowDetail_Option(Object).
     */
    public static InternalResponse getWorkflowDetail(
            int id,
            String transactionID){
        try {
            Database DB = new DatabaseImpl();
            //Data                        
            InternalResponse response = null;
                    
            DatabaseResponse callDB = DB.getWorkflowDetail(id,transactionID);
            
            if(callDB.getStatus() != PaperlessConstant.CODE_SUCCESS ){              
                String message = QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                                callDB.getStatus(),
                                "en"
                                , null);
                if(LogHandler.isShowErrorLog()){                    
                    LogHandler.error(GetWorkflowDetail_option.class,transactionID,"Cannot get Workflow Detail - Detail:"+message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            
            WorkflowDetail_Option detail = (WorkflowDetail_Option) callDB.getObject();            
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    detail);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(GetWorkflowDetail_option.class,transactionID,"UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }            
            return new InternalResponse(500,PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }
    
    public static void main  (String[] args){
//        int id = 12;
//        InternalResponse res = GetWorkflowDetail_option.getWorkflowDetail(id);
//        System.out.println(res.getStatus());
//        System.out.println(res.getMessage());
    }
}
