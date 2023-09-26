/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.HttpServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowTemplateType;

/**
 *
 * @author ADMIN
 */
public class Resources extends HttpServlet {

    private static volatile Logger LOG = LogManager.getLogger(Resources.class);

    private static volatile HashMap<String, ResponseCode> responseCodes = new HashMap<>();
    private static volatile HashMap<String, WorkflowActivity> ListWorkflowActivity = new HashMap<>(100, 1f);
    private static volatile HashMap<Integer, String> listWorkflowTemplateTypeName = new HashMap<>();
    private static volatile HashMap<String, Integer> listAssetType = new HashMap();
    private static volatile HashMap<String, WorkflowTemplateType> listWoTemplateType = new HashMap<>();
    private static volatile HashMap<String, String> queueAuthorizeCode = new HashMap();
    private static volatile HashMap<String, String> queueForgotPassword = new HashMap<>();

    @Override
    public void init() {
        try {
            Resources.init_();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Resources.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void init_() throws Exception {
        System.out.println("====================START INIT RESOURCES==========");
        long start = System.currentTimeMillis();
        Database db = new DatabaseImpl();

        if (responseCodes.isEmpty()) {
            List<ResponseCode> listOfResponseCode = db.getResponseCodes();
            for (ResponseCode responseCode : listOfResponseCode) {
                responseCodes.put(responseCode.getName(), responseCode);
            }
        }

        if (ListWorkflowActivity.isEmpty()) {
//            List<WorkflowActivity> listOfWA = db.getListWorkflowActivity();
//            for (WorkflowActivity workflowAc : listOfWA) {
//                ListWorkflowActivity.put(String.valueOf(workflowAc.getId()), workflowAc);
//            }
        }

        LOG.info("Service is started up and ready to use!");
        System.out.println("\tTime init:" + (System.currentTimeMillis() - start));
        System.out.println("=================SERVICE IS STARTED AND READY TO USE=======");
    }

    public static void reloadResponseCodes() throws Exception {
        Database db = new DatabaseImpl();
        responseCodes = new HashMap<>();
        List<ResponseCode> listOfResponseCode = db.getResponseCodes();
        for (ResponseCode responseCode : listOfResponseCode) {
            responseCodes.put(responseCode.getName(), responseCode);
        }
    }

    public static void reloadListWorkflowActivity() throws Exception {
        if (ListWorkflowActivity == null) {
            ListWorkflowActivity = new HashMap<>();
        } else {
            ListWorkflowActivity.clear();
        }
//        Database db = new DatabaseImpl();
//        if (ListWorkflowActivity.isEmpty() || ListWorkflowActivity == null) {
//            ListWorkflowActivity = new HashMap();
//        }
//        List<WorkflowActivity> listOfWA = db.getListWorkflowActivity();
//        for (WorkflowActivity workflowAc : listOfWA) {
//            if (!ListWorkflowActivity.containsKey(String.valueOf(workflowAc.getId()))) {
//                ListWorkflowActivity.put(String.valueOf(workflowAc.getId()), workflowAc);
//            }
//        }
    }

    public static void main(String[] args) throws Exception {
        Resources.reloadListWorkflowActivity();
    }

    public static void reloadListWorkflowTemplateTypeName() throws Exception {
        Database db = new DatabaseImpl();
        listWorkflowTemplateTypeName = new HashMap();
        DatabaseResponse res = db.getHashMapWorkflowTemplateType();
        if (res != null) {
            listWorkflowTemplateTypeName = (HashMap<Integer, String>) res.getObject();
        }
    }

    public static void reloadListAssetType() throws Exception {
        Database db = new DatabaseImpl();
        listAssetType = new HashMap();
        DatabaseResponse res = db.getAssetType();
        if (res != null) {
            listAssetType = (HashMap<String, Integer>) res.getObject();
        }
    }

    public static void reloadListWorkflowTemplateType() throws Exception {
        Database db = new DatabaseImpl();
        listWoTemplateType = new HashMap();
        DatabaseResponse res = db.getListWorkflowTemplateType();
        if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            List<WorkflowTemplateType> list = (List<WorkflowTemplateType>) res.getObject();
            for (WorkflowTemplateType temp : list) {
                listWoTemplateType.put(String.valueOf(temp.getId()), temp);
            }
        }
    }

    public static HashMap<String, ResponseCode> getResponseCodes() {
        return responseCodes;
    }

    public static HashMap<String, WorkflowActivity> getListWorkflowActivity() {
        return ListWorkflowActivity;
    }

    public static WorkflowActivity getWorkflowActivity(String key) {
        return ListWorkflowActivity.get(key);
    }

    public static void putIntoRAM(String key, WorkflowActivity woAc) {        
        if (ListWorkflowActivity.size() == 100) {                        
            long start = System.currentTimeMillis();
            Thread clear75PercentageOfRam = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (String temp : ListWorkflowActivity.keySet()) {
                        ListWorkflowActivity.remove(temp);
                        if (ListWorkflowActivity.size() <= 25) {
                            break;
                        }
                    }
                }
            });
            clear75PercentageOfRam.start();      
            System.out.println("\n\tTime clear:"+(System.currentTimeMillis() - start));
        }
        ListWorkflowActivity.put(key, woAc);
    }

    public static HashMap<Integer, String> getListWorkflowTemplateTypeName() {
        return listWorkflowTemplateTypeName;
    }

    public static HashMap<String, Integer> getListAssetType() {
        return listAssetType;
    }

    public static HashMap<String, WorkflowTemplateType> getListWorkflowTemplateType() {
        return listWoTemplateType;
    }

    public static HashMap<String, String> getQueueAuthorizeCode() {
        return queueAuthorizeCode;
    }

    public static void setQueueAuthorizeCode(HashMap<String, String> queueAuthorizeCode) {
        Resources.queueAuthorizeCode = queueAuthorizeCode;
    }

    public static HashMap<String, String> getQueueForgotPassword() {
        return queueForgotPassword;
    }

    public static void setQueueForgotPassword(HashMap<String, String> queue) {
        Resources.queueForgotPassword = queue;
    }

}
