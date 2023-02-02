/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.kernel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.objects.Asset;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.qrypto.objects.WorkflowActivity;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class GetWorkflowActivity {
    final private static Logger LOG = LogManager.getLogger(GetWorkflowActivity.class);

    public static List<WorkflowActivity> getListWorkflowActivity(){
        List<WorkflowActivity> list = new ArrayList<>();
        Resources.reloadListWorkflowActivity();
        Resources.getListWorkflowActivity().forEach(
                    (key, value) -> {
                    list.add(value);
                });
            return list;
    }
    
    public static WorkflowActivity getWorkflowActivity(int id){                
        Resources.reloadListWorkflowActivity();
        return Resources.getListWorkflowActivity().get(String.valueOf(id));
    }
    
    public static boolean isContains(int id){    
        return true;
//        try {
//            Database DB = new DatabaseImpl();
//            //Data                        
//            InternalResponse response = null;
//                    
//            DatabaseResponse callDB = DB.getAsset(id);
//            
//            if(callDB.getStatus() != QryptoConstant.CODE_SUCCESS ){              
//                String message = null;
//                if(LogHandler.isShowErrorLog()){
//                    message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
//                                callDB.getStatus(),
//                                "en"
//                                , null);
//                    LOG.error("Cannot get Asset - Detail:"+message);
//                }
//                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
//                        message
//                );
//            }
//            
//            Asset asset = (Asset) callDB.getObject();
//            
//            return new InternalResponse(
//                    QryptoConstant.CODE_SUCCESS,
//                    new ObjectMapper().writeValueAsString(asset));
//            
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//            return new InternalResponse(500,QryptoConstant.INTERNAL_EXP_MESS);
//        } 
    }
    
    public static void main(String[] args){
//        DatabaseImpl db = new DatabaseImpl();
        List<WorkflowActivity> list = GetWorkflowActivity.getListWorkflowActivity();
        for(WorkflowActivity temp : list){
            System.out.println("A:"+temp.getId());
        }
        System.out.println(GetWorkflowActivity.isContains(30));
    }
}
