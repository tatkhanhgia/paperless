/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowActivity;

/**
 *
 * @author GiaTK
 */
public class GetDocument {
//    final private static Logger LOG = LogManager.getLogger(GetDocument.class);
    
    public static InternalResponse getDocument(int workflowActivityID, String transactionID){
        try {
            Database DB = new DatabaseImpl();                         
            InternalResponse response = null;

            WorkflowActivity woAc = GetWorkflowActivity.getWorkflowActivity(
                    workflowActivityID,
                    transactionID);
            if(woAc == null){
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                                PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_EXISTED,
                                "en",
                                null)
                );
            }            
            
            response = GetFileManagement.getFileManagement(Integer.parseInt(woAc.getFile().getID()), transactionID);
            
            if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
                return response;
            }
            FileManagement file = (FileManagement) response.getData();
                    
            if(file.getData() == null ){
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                                PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_PROCESS_YET,
                                "en",
                                null)
                );
            }
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    file);
            

        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
//                e.printStackTrace();
                LogHandler.error(GetDocument.class,"UNKNOWN EXCEPTION. Details: " + e);
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }
}
