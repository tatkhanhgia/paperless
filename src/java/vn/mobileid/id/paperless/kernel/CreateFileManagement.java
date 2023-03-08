/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.QryptoConstant;
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowActivity;

/**
 *
 * @author GiaTK
 */
public class CreateFileManagement {
    final private static Logger LOG = LogManager.getLogger(CreateUserActivityLog.class);
    
    public static boolean checkData(WorkflowActivity workflow){
        if(workflow == null){
            return false;
        }
        if(workflow.getEnterprise_id() <=0 && workflow.getEnterprise_name() == null){
            return false;
        }
        if(workflow.getCreated_by() == null){
            return false;
        }
        return true;
    }    
    
    
    public static InternalResponse processingCreateFileManagement(
            WorkflowActivity workflow,            
            String UUID,
            String nameFile,            
            String HMAC,
            byte[] fileData,
            User user) {
        return processingCreateFileManagement(workflow,UUID, nameFile, 0, 0, 0, 0, HMAC, fileData, user, "DBMS");
    }
    
    public static InternalResponse processingCreateFileManagement(
            WorkflowActivity workflow,                        
            String HMAC,
            byte[] fileData,
            User user) {
        return processingCreateFileManagement(workflow,"UUID", null, 0, 0, 0, 0, HMAC, fileData, user, "DBMS");
    }
    
    public static InternalResponse processingCreateFileManagement(
            WorkflowActivity workflow,                        
            int page,
            int size,
            int width,
            int height,
            String HMAC,
            byte[] fileData,
            User user) {
        return processingCreateFileManagement(workflow,"UUID", null, page, size, width, height, HMAC, fileData, user, "DBMS");
    }
    
    public static InternalResponse processingCreateFileManagement(
            WorkflowActivity workflow,            
            String nameFile,
            int page,
            int size,
            int width,
            int height,
            String HMAC,
            byte[] fileData,
            User user) {
        return processingCreateFileManagement(workflow,"UUID", nameFile, page, size, width, height, HMAC, fileData, user,"DBMS");
    }
    
    private static InternalResponse processingCreateFileManagement(
            WorkflowActivity workflow,
            String UUID,
            String nameFile,
            int page,
            int size,
            int width,
            int height,
            String HMAC,
            byte[] fileData,
            User user,
            String DBMS) {
    try {
            Database DB = new DatabaseImpl();
            
            //Create new File Management
            DatabaseResponse callDB = DB.createFileManagement(
                    UUID, //UUID
                    nameFile,   //name
                    page,      //page
                    size,      //size
                    width,      //width
                    height,      //height
                    fileData, //file data
                    HMAC, //HMAC
                    user.getName(),
                    DBMS);          
                        
            if(callDB.getStatus() != QryptoConstant.CODE_SUCCESS ){
                String message = null;
                if(LogHandler.isShowErrorLog()){
                    message = QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_FAIL,
                                callDB.getStatus(),
                                "en"
                                , null);
                    LOG.error("Cannot create File Management - Detail:"+message);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                    callDB.getIDResponse());
    }catch(Exception e){
        if(LogHandler.isShowErrorLog()){
                    
                    LOG.error("Cannot create User_Activity_log - Detail:"+e);
                }
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        e.getMessage()
                );
    }
    }
    
    public static void main(String[] args) throws IOException{
        WorkflowActivity object = new WorkflowActivity();
        object.setEnterprise_id(3);
        object.setCreated_by("GIATK");      
        User user = new User();
        user.setEmail("giatk@mobile-id.vn");
        
        String pa = "D:\\NetBean\\QryptoServices\\file\\rssp.p12";
        byte[] data = Files.readAllBytes(new File(pa).toPath());
        
//        CreateFileManagement.processingCreateFileManagement(
//                object,
//                "HMAC",
//                data, user);
        CreateFileManagement.processingCreateFileManagement(
                object,
                "RSSP",
                0,
                0,
                0,
                0,
                "HMAC",
                data,
                user);
                
    }
}
