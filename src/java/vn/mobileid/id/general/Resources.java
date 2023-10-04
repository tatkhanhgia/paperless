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
import vn.mobileid.id.general.database.DatabaseImpl_V2;
import vn.mobileid.id.general.database.DatabaseImpl_V2_WorkflowDetails;
import vn.mobileid.id.general.database.DatabaseV2;
import vn.mobileid.id.general.database.DatabaseV2_WorkflowDetails;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.EventAction;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;
import vn.mobileid.id.paperless.objects.WorkflowTemplateType;

/**
 *
 * @author ADMIN
 */
public class Resources extends HttpServlet {

    private static volatile Logger LOG = LogManager.getLogger(Resources.class);

    //Save response code
    private static volatile HashMap<String, ResponseCode> responseCodes = new HashMap<>();

    //Save temporal Workflow Activity
    private static volatile HashMap<String, WorkflowActivity> listWorkflowActivity = new HashMap<>(100, 1f);

    //Save  Workflow Template Type Name
    private static volatile HashMap<Integer, String> listWorkflowTemplateTypeName = new HashMap<>();

    //Save  AssetType
    private static volatile HashMap<String, Integer> listAssetType = new HashMap();

    //Save Workflow Template Type
    private static volatile HashMap<String, WorkflowTemplateType> listWoTemplateType = new HashMap<>();

    //Save AuthorizeCode when user has been created
    private static volatile HashMap<String, String> queueAuthorizeCode = new HashMap();

    //Save ForgotPassword when user using API "Forgot Password"
    private static volatile HashMap<String, String> queueForgotPassword = new HashMap<>();

    //Save Workflow Detail Attribute Type in DB <=> mapping with Workflow Detail
    private static volatile HashMap<String, WorkflowAttributeType> listWorkflowDetailAttributeType = new HashMap<>();

    //Save Event Action in DB <=> Mapping with Action, API 
    private static volatile HashMap<Integer, EventAction> listEventAction = new HashMap<>();

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

        reloadListWorkflowDetailAttributeTypes();

        reloadListEventAction();

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

    public static void main(String[] args) throws Exception {
    }

    public static void reloadListWorkflowTemplateTypeName() throws Exception {
        Database db = new DatabaseImpl();
        listWorkflowTemplateTypeName = new HashMap();
        DatabaseResponse res = db.getHashMapWorkflowTemplateType();
        if (res != null) {
            listWorkflowTemplateTypeName = (HashMap<Integer, String>) res.getObject();
        }
    }

    public static void reloadListEventAction() throws Exception {
        DatabaseV2 db = new DatabaseImpl_V2();
        listEventAction = new HashMap<>();
        DatabaseResponse response = db.getEventActions();
        if (response != null) {
            List<EventAction> list = (List<EventAction>) response.getObject();
            for (EventAction element : list) {
                Long temp = element.getId();
                listEventAction.put(temp.intValue(), element);
            }
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

    public static void reloadListWorkflowDetailAttributeTypes() throws Exception {
        DatabaseV2_WorkflowDetails callDb = new DatabaseImpl_V2_WorkflowDetails();
        DatabaseResponse response = callDb.getWorkflowDetailAttributeTypes();
        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            throw new Exception("Cannot load list Workflow Detail Attribute Types!!");
        }
        List<WorkflowAttributeType> temps = (List<WorkflowAttributeType>) response.getObject();
        listWorkflowDetailAttributeType.clear();
        for (WorkflowAttributeType temp : temps) {
            listWorkflowDetailAttributeType.put(temp.getName(), temp);
        }
    }

    public static HashMap<String, ResponseCode> getResponseCodes() {
        return responseCodes;
    }

    public static HashMap<String, WorkflowActivity> getListWorkflowActivity() {
        return listWorkflowActivity;
    }

    public static WorkflowActivity getWorkflowActivity(String key) {
        return listWorkflowActivity.get(key);
    }

    public static void putIntoRAM(String key, WorkflowActivity woAc) {
        if (listWorkflowActivity.size() == 100) {
            Thread clear75PercentageOfRam = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (String temp : listWorkflowActivity.keySet()) {
                        listWorkflowActivity.remove(temp);
                        if (listWorkflowActivity.size() >= 75) {
                            break;
                        }
                    }
                }
            });
            clear75PercentageOfRam.start();
            listWorkflowActivity.put(key, woAc);
        }
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

    public static HashMap<String, WorkflowAttributeType> getListWorkflowAttributeType() {
        return listWorkflowDetailAttributeType;
    }

    public static HashMap<Integer, EventAction> getListEventAction() {
        return listEventAction;
    }
}
