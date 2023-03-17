/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.WorkflowActivity;

/**
 *
 * @author GiaTK
 */
public class GetWorkflowActivity {
    
    public static WorkflowActivity getWorkflowActivity(
            int id,
            String transactionID){  
//        if(Resources.getListWorkflowActivity().isEmpty()){
//                Resources.reloadListWorkflowActivity();
//        }
        WorkflowActivity check =  Resources.getListWorkflowActivity().get(String.valueOf(id));
        if(check == null){            
            WorkflowActivity get = getWorkflowActivityFromDB(id,transactionID);
            if(get == null){
                return null;
            }
            Resources.getListWorkflowActivity().put(String.valueOf(get.getId()), get);
            Thread temp = new Thread(){
                public void run(){
                    Resources.reloadListWorkflowActivity();
                }
            };
            temp.start();
            return get;
        }        
        return check;
    }
    
    public  static WorkflowActivity getWorkflowActivityFromDB(
            int id,
            String transactionID){
        try{
            Database db = new DatabaseImpl();
            DatabaseResponse res = db.getWorkflowActivity(id,transactionID);
            if(res.getStatus() != PaperlessConstant.CODE_SUCCESS){
                return null;
            }
            return (WorkflowActivity) res.getObject();
        } catch (Exception e){
            if(LogHandler.isShowErrorLog()){
                LogHandler.error(GetWorkflowActivity.class,transactionID,"Cannot get WoAC!");
            }
            return null;
        }
    }
    
//    public static boolean isContains(int id){    
//        return true;
//        try {
//            Database DB = new DatabaseImpl();
//            //Data                        
//            InternalResponse response = null;
//                    
//            DatabaseResponse callDB = DB.getAsset(id);
//            
//            if(callDB.getStatus() != PaperlessConstant.CODE_SUCCESS ){              
//                String message = null;
//                if(LogHandler.isShowErrorLog()){
//                    message = QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
//                                callDB.getStatus(),
//                                "en"
//                                , null);
//                    LOG.error("Cannot get Asset - Detail:"+message);
//                }
//                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
//                        message
//                );
//            }
//            
//            Asset asset = (Asset) callDB.getObject();
//            
//            return new InternalResponse(
//                    PaperlessConstant.CODE_SUCCESS,
//                    new ObjectMapper().writeValueAsString(asset));
//            
//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
//                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
//            }
//            e.printStackTrace();
//            return new InternalResponse(500,PaperlessConstant.INTERNAL_EXP_MESS);
//        } 
//    }
    
    public static void main(String[] args){
//        DatabaseImpl db = new DatabaseImpl();
//        List<WorkflowActivity> list = GetWorkflowActivity.getListWorkflowActivity();
//        for(WorkflowActivity temp : list){
//            System.out.println("A:"+temp.getId());
//        }
//        System.out.println(GetWorkflowActivity.isContains(30));
    }
}
