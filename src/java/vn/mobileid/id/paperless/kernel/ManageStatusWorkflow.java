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

/**
 *
 * @author GiaTK
 */
public class ManageStatusWorkflow {

    public static InternalResponse deactiveWorkflow(
            int workflow_id,
            String email,
            int enterprise_id,
            String transactionID
    ) {
        try{
            Database db = new DatabaseImpl();
            DatabaseResponse res = db.deactiveWorkflow(
                    workflow_id,
                    email,
                    enterprise_id,
                    email,
                    transactionID);
            if( res.getStatus() != PaperlessConstant.CODE_SUCCESS){
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                                res.getStatus(),
                                "en",
                                null)
                );
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, "");
        } catch(Exception ex){
            if(LogHandler.isShowErrorLog()){
                ex.printStackTrace();
                LogHandler.error(ManageStatusWorkflow.class, transactionID,
                        "Error - Detail: "+ ex);
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }
    
    public static InternalResponse reactiveWorkflow(
            int workflow_id,
            String email,
            int enterprise_id,
            String transactionID
    ) {
        try{
            Database db = new DatabaseImpl();
            DatabaseResponse res = db.reactiveWorkflow(
                    workflow_id,
                    email,
                    enterprise_id,
                    email,
                    transactionID);
            if( res.getStatus() != PaperlessConstant.CODE_SUCCESS){
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                                res.getStatus(),
                                "en",
                                null)
                );
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, "");
        } catch(Exception ex){
            if(LogHandler.isShowErrorLog()){
                ex.printStackTrace();
                LogHandler.error(ManageStatusWorkflow.class, transactionID,
                        "Error - Detail: "+ ex);
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }
}
