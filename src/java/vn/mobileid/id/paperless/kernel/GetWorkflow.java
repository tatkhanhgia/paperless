/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.QryptoConstant;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;
import vn.mobileid.id.paperless.objects.Workflow;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class GetWorkflow {
    final private static Logger LOG = LogManager.getLogger(GetWorkflow.class);
    
    public static InternalResponse getWorkflow(int id){
        try {
            Database DB = new DatabaseImpl();                         
            InternalResponse response = null;
                    
            DatabaseResponse callDB = DB.getWorkflow(id);
            
            if(callDB.getStatus() != QryptoConstant.CODE_SUCCESS ){              
                String message = null;
                if(LogHandler.isShowErrorLog()){
                    message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                                callDB.getStatus(),
                                "en"
                                , null);
                    LOG.error("Cannot get Workflow - Detail:"+message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            
            Workflow workflow = (Workflow) callDB.getObject();            
            return new InternalResponse(
                    QryptoConstant.HTTP_CODE_SUCCESS,
                    workflow);
            
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
            return new InternalResponse(500,QryptoConstant.INTERNAL_EXP_MESS);
        }  
    }
    
    public static InternalResponse getListWorkflow(
            String email,
            int enterprise_id,
            String status,
            String type,
            boolean use_metadata,
            String metadata,
            int offset,
            int rowcount
    ){
        try {
            Database DB = new DatabaseImpl();                
            InternalResponse response = null;
                    
            DatabaseResponse callDB = DB.getListWorkflow(email, enterprise_id, status, type, use_metadata, metadata, offset, rowcount);
            
            if(callDB.getStatus() != QryptoConstant.CODE_SUCCESS ){              
                String message = null;
                if(LogHandler.isShowErrorLog()){
                    message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                                callDB.getStatus(),
                                "en"
                                , null);
                    LOG.error("Cannot get list Workflow - Detail:"+message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            
            List<Workflow> object = (List<Workflow>) callDB.getObject();
            
            return new InternalResponse(
                    QryptoConstant.HTTP_CODE_SUCCESS,
                    object);
            
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
            return new InternalResponse(500,QryptoConstant.INTERNAL_EXP_MESS);
        }  
    }
    
    public static void main(String[] args){
//          List<Workflow> lis = ( List<Workflow>)GetWorkflow.getListWorkflow(
//                 "giatk@mobile-id.vn",
//                 3,
//                 "1,2",
//                 "12",                 
//                 false,
//                 "",
//                 0,
//                 0).getData();
//          for(Workflow a : lis){
//              System.out.println(a.getWorkflow_id());
//          }

        Workflow a = (Workflow)GetWorkflow.getWorkflow(30).getData();
        System.out.println("ID:"+a.getWorkflow_id());
        System.out.println("Template:"+a.getTemplate_type_name());

        System.out.println("Workflow type:"+a.getWorkflow_type_name());
    }
}
