/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.kernel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.objects.FileDataDetails;
import vn.mobileid.id.qrypto.objects.ItemDetails;
import vn.mobileid.id.qrypto.objects.ProcessWorkflowActivity_JSNObject;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.utils.Utils;
/**
 *
 * @author GiaTK
 */
public class ProcessWorkflowActivity {
    final private static Logger LOG = LogManager.getLogger(ProcessWorkflowActivity.class);
    
    public static InternalResponse checkData(ProcessWorkflowActivity_JSNObject request){
        List<ItemDetails> listItem = request.getItem();
        List<FileDataDetails> listFile = request.getFile_data();
        InternalResponse result;
        
        //Check item details
        for(ItemDetails obj : listItem){
            result = CreateWorkflowTemplate.checkDataWorkflowDetail(obj);
            if(result.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS){
                return result;
            }
        }
        
        //Check file details
        for(FileDataDetails obj: listFile){
            boolean gate = checkFile_type(obj.getFile_type());
            if(!gate){
                return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            QryptoConstant.SUBCODE_MISSING_OR_ERROR_FILE_TYPE,
                            "en",
                            null));
            }
        }
        return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                    QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_SUCCESS,
                            QryptoConstant.SUBCODE_SUCCESS,
                            "en",
                            null));
    }
    
    public static InternalResponse process(ProcessWorkflowActivity_JSNObject request){
        try {
            Database DB = new DatabaseImpl();

            //Get file from DB
            DatabaseResponse template = DB.getFileAsset(0);
            if (template.getStatus() != QryptoConstant.CODE_SUCCESS) {
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        QryptoMessageResponse.getErrorMessage(
                                QryptoConstant.CODE_FAIL,
                                template.getStatus(),
                                "en",
                                 null)
                );
            }
            
            //Assign data into file
            
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                    ""
            );
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }
            e.printStackTrace();
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }
    
    //==================INTERAL METHOD/FUNCTION===================
    private static boolean checkFile_type(int i){
        if( i<=0 || i > 4){
            return false;
        }
        return true;
    }

    
}
