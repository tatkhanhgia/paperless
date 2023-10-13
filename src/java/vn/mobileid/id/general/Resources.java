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
import vn.mobileid.id.general.database.DatabaseImpl_V2_User;
import vn.mobileid.id.general.database.DatabaseImpl_V2_WorkflowDetails;
import vn.mobileid.id.general.database.DatabaseV2;
import vn.mobileid.id.general.database.DatabaseV2_User;
import vn.mobileid.id.general.database.DatabaseV2_WorkflowDetails;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Category;
import vn.mobileid.id.paperless.objects.EventAction;
import vn.mobileid.id.paperless.objects.GenerationType;
import vn.mobileid.id.paperless.objects.StatusOfAccount;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;
import vn.mobileid.id.paperless.objects.WorkflowTemplateType;
import vn.mobileid.id.paperless.objects.WorkflowType;

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

    //Save  AssetType
    private static volatile HashMap<String, Integer> listAssetType = new HashMap();

    //Save Workflow Template Type
    private static volatile HashMap<Integer, WorkflowTemplateType> listWoTemplateType = new HashMap<>();

    //Save AuthorizeCode when user has been created
    private static volatile HashMap<String, String> queueAuthorizeCode = new HashMap();

    //Save ForgotPassword when user using API "Forgot Password"
    private static volatile HashMap<String, String> queueForgotPassword = new HashMap<>();

    //Save Workflow Detail Attribute Type in DB <=> mapping with Workflow Detail
    private static volatile HashMap<String, WorkflowAttributeType> listWorkflowAttributeType = new HashMap<>();

    //Save Event Action in DB <=> Mapping with Action, API 
    private static volatile HashMap<Integer, EventAction> listEventAction = new HashMap<>();
    
    //Save Category
    private static volatile HashMap<Integer, Category> listCategory = new HashMap<>();
    
    //Save User Status in DB
    private static volatile HashMap<Integer, StatusOfAccount> listStatusOfAccount = new HashMap<>();
    
    //Save Workflow Type
    private static volatile HashMap<Integer, WorkflowType> listWorkflowType = new HashMap<>();
    
    //Save GenerationType
    private static volatile HashMap<Integer, GenerationType> listGenerationType = new HashMap<>();

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
        
        reloadListTypeOfStatusAccount();
        
        reloadListGenerationType();
        
        reloadListWorkflowType();
        
        reloadListCategory();

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
    
    public static void reloadListTypeOfStatusAccount()throws Exception{
        DatabaseV2_User db = new DatabaseImpl_V2_User();
        DatabaseResponse res = db.getTypeOfStatus();
        listStatusOfAccount = new HashMap<>();
        if(res.getStatus() == PaperlessConstant.CODE_SUCCESS){
            List<StatusOfAccount> status = (List<StatusOfAccount>)res.getObject();
            for(StatusOfAccount temp : status){
                listStatusOfAccount.put(temp.getId(), temp);
            }
        }
    }

    public static void reloadListWorkflowTemplateType() throws Exception {
        Database db = new DatabaseImpl();
        listWoTemplateType = new HashMap();
        DatabaseResponse res = db.getListWorkflowTemplateType();
        if (res.getStatus() == PaperlessConstant.CODE_SUCCESS) {
            List<WorkflowTemplateType> list = (List<WorkflowTemplateType>) res.getObject();
            for (WorkflowTemplateType temp : list) {
                listWoTemplateType.put(temp.getId(), temp);
            }
        }
    }
    
    public static void reloadListCategory() throws Exception {
        DatabaseV2_User db = new DatabaseImpl_V2_User();
        listCategory = new HashMap();
        DatabaseResponse res = db.getCategory();
        if (res.getStatus() == PaperlessConstant.CODE_SUCCESS) {
            List<Category> list = (List<Category>) res.getObject();
            for (Category temp : list) {
                listCategory.put(temp.getId(), temp);
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
        listWorkflowAttributeType.clear();
        for (WorkflowAttributeType temp : temps) {
            listWorkflowAttributeType.put(temp.getName(), temp);
        }
    }
    
    public static void reloadListWorkflowType() throws Exception{
        DatabaseV2 callDb = new DatabaseImpl_V2();
        DatabaseResponse response = callDb.getWorkflowType();
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
            throw new Exception("Cannot load list Wofklow Type!");
        }
        List<WorkflowType> temps = (List<WorkflowType>)response.getObject();
        listWorkflowType.clear();
        for(WorkflowType data : temps){
            listWorkflowType.put(data.getId(), data);
        }
    }
    
    public static void reloadListGenerationType() throws Exception{
        DatabaseV2 callDb = new DatabaseImpl_V2();
        DatabaseResponse response = callDb.getGenerationType();
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
            throw new Exception("Cannot load list Generation Type!");
        }
        List<GenerationType> temps = (List<GenerationType>)response.getObject();
        listGenerationType.clear();
        for(GenerationType data : temps){
            listGenerationType.put(data.getId(), data);
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

    public static HashMap<String, Integer> getListAssetType() {
        return listAssetType;
    }

    public static HashMap<Integer, WorkflowTemplateType> getListWorkflowTemplateType() {
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
        return listWorkflowAttributeType;
    }

    public static HashMap<Integer, EventAction> getListEventAction() {
        return listEventAction;
    }
    
    public static HashMap<Integer, StatusOfAccount> getListStatusOfAccount(){
        return listStatusOfAccount;
    }

    public static HashMap<Integer, WorkflowType> getListWorkflowType() {
        return listWorkflowType;
    }

    public static HashMap<Integer, GenerationType> getListGenerationType() {
        return listGenerationType;
    }
    
    public static HashMap<Integer, Category> getListCategory(){
        return listCategory;
    }
}
