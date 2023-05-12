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
    
    /**
     * Using to get all data in Workflow template with ID workflow input
     * @param id
     * @param transactionID
     * @return 
     */
    public  static InternalResponse getWorkflowTemplate(
            int id,
            String transactionID) throws Exception{
        
            Database DB = new DatabaseImpl();
            //Data                        
            InternalResponse response = null;
                    
            DatabaseResponse callDB = DB.getWorkflowTemplate(
                    id,
                    transactionID);                       
            
            try {
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
                    template);
            
        } catch (Exception e) {
            throw new Exception("Cannot get workflow template",e);          
//            return new InternalResponse(500,PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }
}
