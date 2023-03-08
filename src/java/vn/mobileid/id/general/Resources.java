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
import javax.naming.ldap.HasControls;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.tls.HashAlgorithm;
import org.bouncycastle.jcajce.provider.digest.GOST3411;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.paperless.QryptoConstant;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowTemplateType;

/**
 *
 * @author ADMIN
 */
public class Resources {

    private static volatile Logger LOG = LogManager.getLogger(Resources.class);

    private static volatile HashMap<String, ResponseCode> responseCodes = new HashMap<>();
    private static volatile HashMap<String, WorkflowActivity> ListWorkflowActivity = new HashMap<>();
    private static volatile HashMap<Integer, String> listWorkflowTemplateTypeName = new HashMap<>();
    private static volatile HashMap<String, Integer> listAssetType = new HashMap();
    private static volatile HashMap<String, WorkflowTemplateType> listWoTemplateType = new HashMap<>();
    private static volatile HashMap<String, String> listPDFWaitingAuthorize = new HashMap<>();

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

    public static void reloadListWorkflowActivity() {
        Database db = new DatabaseImpl();
        ListWorkflowActivity = new HashMap();
        List<WorkflowActivity> listOfWA = db.getListWorkflowActivity();
        for (WorkflowActivity workflowAc : listOfWA) {
            ListWorkflowActivity.put(String.valueOf(workflowAc.getId()), workflowAc);
        }
    }

    public static void reloadListWorkflowTemplateTypeName() {
        Database db = new DatabaseImpl();
        listWorkflowTemplateTypeName = new HashMap();
        DatabaseResponse res = db.getAllWorkflowTemplateType();
        if (res != null) {
            listWorkflowTemplateTypeName = (HashMap<Integer, String>) res.getObject();
        }
    }

    public static void reloadListAssetType() {
        Database db = new DatabaseImpl();
        listAssetType = new HashMap();
        DatabaseResponse res = db.getAssetType();
        if (res != null) {
            listAssetType = (HashMap<String, Integer>) res.getObject();
        }
    }

    public static void reloadListWorkflowTemplateType() {
        Database db = new DatabaseImpl();
        listWoTemplateType = new HashMap();
        DatabaseResponse res = db.getListWorkflowTemplateType();
        if (res.getStatus() != QryptoConstant.CODE_SUCCESS) {
            List<WorkflowTemplateType> list = (List<WorkflowTemplateType>)res.getObject();
            for(WorkflowTemplateType temp : list){
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

    public static HashMap<String, String> getListPDFWaiting() {
        return listPDFWaitingAuthorize;
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
}
