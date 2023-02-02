/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.tls.HashAlgorithm;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.qrypto.objects.WorkflowActivity;


/**
 *
 * @author ADMIN
 */
public class Resources {

    private static volatile Logger LOG = LogManager.getLogger(Resources.class);        

    private static volatile HashMap<String, ResponseCode> responseCodes = new HashMap<>();     
    private static volatile HashMap<String, WorkflowActivity> ListWorkflowActivity = new HashMap<>();         
    
    public static synchronized void init() {
        Database db = new DatabaseImpl();

        if (responseCodes.isEmpty()) {
            List<ResponseCode> listOfResponseCode = db.getResponseCodes();
            for (ResponseCode responseCode : listOfResponseCode) {
                responseCodes.put(responseCode.getName(), responseCode);   
            }
        }
        
        if (ListWorkflowActivity.isEmpty()) {
            List<WorkflowActivity> listOfWA = db.getListWorkflowActivity();
            for (WorkflowActivity workflowAc : listOfWA) {
                ListWorkflowActivity.put(String.valueOf(workflowAc.getId()), workflowAc);
            }
        }

        if (LogHandler.isShowInfoLog()) {
            LOG.info("Service is started up and ready to use!");
        }
    }

    public static void reloadResponseCodes() {
        Database db = new DatabaseImpl();
        responseCodes = new HashMap<>();
        List<ResponseCode> listOfResponseCode = db.getResponseCodes();
        for (ResponseCode responseCode : listOfResponseCode) {
            responseCodes.put(responseCode.getName(), responseCode);   
        }
    }
    
    public static void reloadListWorkflowActivity(){
        Database db = new DatabaseImpl();
        ListWorkflowActivity = new HashMap();
        List<WorkflowActivity> listOfWA = db.getListWorkflowActivity();
            for (WorkflowActivity workflowAc : listOfWA) {
                ListWorkflowActivity.put(String.valueOf(workflowAc.getId()), workflowAc);
            }
    } 

    public static HashMap<String, ResponseCode> getResponseCodes() {
        return responseCodes;
    }

    public static HashMap<String, WorkflowActivity> getListWorkflowActivity() {
        return ListWorkflowActivity;
    }
}
