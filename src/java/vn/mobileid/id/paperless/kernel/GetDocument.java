/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import java.util.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.QryptoConstant;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowActivity;

/**
 *
 * @author GiaTK
 */
public class GetDocument {
    final private static Logger LOG = LogManager.getLogger(GetDocument.class);
    
    public static InternalResponse getDocument(int workflowActivityID){
        try {
            Database DB = new DatabaseImpl();                         
            InternalResponse response = null;

            WorkflowActivity woAc = GetWorkflowActivity.getWorkflowActivity(workflowActivityID);
            if(woAc == null){
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        QryptoMessageResponse.getErrorMessage(
                                QryptoConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                                QryptoConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_EXISTED,
                                "en",
                                null)
                );
            }            
            
            response = GetFileManagement.getFileManagement(Integer.parseInt(woAc.getFile().getID()));
            
            if(response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS){
                return response;
            }
            FileManagement file = (FileManagement) response.getData();
                    
            if(file.getData() == null ){
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        QryptoMessageResponse.getErrorMessage(
                                QryptoConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                                QryptoConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_PROCESS_YET,
                                "en",
                                null)
                );
            }
            return new InternalResponse(
                    QryptoConstant.HTTP_CODE_SUCCESS,
                    file);
            

        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
//                e.printStackTrace();
                LOG.error("UNKNOWN EXCEPTION. Details: " + e);
            }
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }
}
