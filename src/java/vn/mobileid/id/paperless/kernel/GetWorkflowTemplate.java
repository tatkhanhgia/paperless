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
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowTemplate;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class GetWorkflowTemplate {
//    final private static Logger LOG = LogManager.getLogger(GetWorkflowTemplate.class);
    
    /**
     * Using to get all data in Workflow template with ID workflow input
     * @param id
     * @param transactionID
     * @return 
     */
    public  static InternalResponse getWorkflowTemplate(
            int id,
            String transactionID){
        try {
            Database DB = new DatabaseImpl();
            //Data                        
            InternalResponse response = null;
                    
            DatabaseResponse callDB = DB.getWorkflowTemplate(
                    id,
                    transactionID);
            
            if(callDB.getStatus() == PaperlessConstant.CODE_FAIL){
                return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                        PaperlessConstant.INTERNAL_EXP_MESS
                );
            }
            
            if(callDB.getStatus() != PaperlessConstant.CODE_SUCCESS ){              
                String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                                callDB.getStatus(),
                                "en"
                                , null);
                if(LogHandler.isShowErrorLog()){                    
                    LogHandler.error(GetWorkflowTemplate.class,transactionID,"Cannot get Workflow Template - Detail:"+message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            
            WorkflowTemplate template = (WorkflowTemplate) callDB.getObject();
            
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    template.getMeta_data_template());
            
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(GetWorkflowTemplate.class,transactionID,"UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }            
            return new InternalResponse(500,PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }
}
