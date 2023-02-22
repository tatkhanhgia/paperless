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
import vn.mobileid.id.qrypto.objects.Asset;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.qrypto.objects.WorkflowDetail_Option;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class GetWorkflowDetail_option {
    final private static Logger LOG = LogManager.getLogger(GetWorkflowDetail_option.class);
    
    /**
     * Get Workflow Detail/Option of the workflow input
     * @param id Workflow ID
     * @return InternalReponse with WorkflowDetail_Option(Object).
     */
    public static InternalResponse getWorkflowDetail(int id){
        try {
            Database DB = new DatabaseImpl();
            //Data                        
            InternalResponse response = null;
                    
            DatabaseResponse callDB = DB.getWorkflowDetail(id);
            
            if(callDB.getStatus() != QryptoConstant.CODE_SUCCESS ){              
                String message = null;
                if(LogHandler.isShowErrorLog()){
                    message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                                callDB.getStatus(),
                                "en"
                                , null);
                    LOG.error("Cannot get Workflow Detail - Detail:"+message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            
            WorkflowDetail_Option detail = (WorkflowDetail_Option) callDB.getObject();
            
            return new InternalResponse(
                    QryptoConstant.HTTP_CODE_SUCCESS,
                    detail);
            
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
            return new InternalResponse(500,QryptoConstant.INTERNAL_EXP_MESS);
        }
    }
    
    public static void main  (String[] args){
        int id = 12;
        InternalResponse res = GetWorkflowDetail_option.getWorkflowDetail(id);
        System.out.println(res.getStatus());
        System.out.println(res.getMessage());
    }
}
