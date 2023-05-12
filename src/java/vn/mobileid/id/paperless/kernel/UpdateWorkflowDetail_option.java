/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class UpdateWorkflowDetail_option {
    
    public static InternalResponse updateWorkflowOption(
            int id,
            String email,
            int aid,
            WorkflowDetail_Option detail,
            String hmac,
            String created_by,
            String transactionID) throws Exception{
        
            InternalResponse response = null;
            Database DB = new DatabaseImpl();

            DatabaseResponse callDB = DB.updateWorkflowDetail_Option(
                    id,
                    email,
                    aid,
                    detail.getHashMap(),
                    hmac,
                    created_by,
                    transactionID);

            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            callDB.getStatus(),
                            "en",
                            null);
                if (LogHandler.isShowErrorLog()) {                    
                    LogHandler.error(UpdateWorkflowDetail_option.class,transactionID,"Cannot update Workflow Detail - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }                        

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    "");      
    }
    
    public static void main(String[] args){
//        WorkflowDetail_Option detail = new WorkflowDetail_Option();
////        detail.setMetadata("metadata 1-2-3-4-4");
//        detail.setCSV_email(true);
//                
//        InternalResponse res = UpdateWorkflowDetail_option.updateWorkflowOption(
//                12,
//                detail,
//                "",
//                "GIATK");
//        
//        System.out.println("Mes:"+res.getMessage());
    }
    
}
