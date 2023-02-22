/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.kernel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.qrypto.objects.WorkflowDetail_Option;
import vn.mobileid.id.qrypto.objects.WorkflowTemplate;
import vn.mobileid.id.qrypto.objects.WorkflowTemplateType;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class GetWorkflowTemplate {
    final private static Logger LOG = LogManager.getLogger(GetWorkflowTemplate.class);
    
    public  static InternalResponse getWorkflowTemplate(int id){
        try {
            Database DB = new DatabaseImpl();
            //Data                        
            InternalResponse response = null;
                    
            DatabaseResponse callDB = DB.getWorkflowTemplate(id);
            
            if(callDB.getStatus() == QryptoConstant.CODE_FAIL){
                return new InternalResponse(QryptoConstant.HTTP_CODE_500,
                        QryptoConstant.INTERNAL_EXP_MESS
                );
            }
            
            if(callDB.getStatus() != QryptoConstant.CODE_SUCCESS ){              
                String message = null;
                if(LogHandler.isShowErrorLog()){
                    message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                                callDB.getStatus(),
                                "en"
                                , null);
                    LOG.error("Cannot get Workflow Template - Detail:"+message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            
            WorkflowTemplate template = (WorkflowTemplate) callDB.getObject();
            
            return new InternalResponse(
                    QryptoConstant.HTTP_CODE_SUCCESS,
                    template.getMeta_data_template());
            
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
            return new InternalResponse(500,QryptoConstant.INTERNAL_EXP_MESS);
        }
    }
}
