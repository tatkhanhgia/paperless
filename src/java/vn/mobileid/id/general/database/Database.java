/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.database;

import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.qrypto.objects.WorkflowActivity;

/**
 *
 * @author ADMIN
 */
public interface Database {

    public DatabaseResponse createWorkflow(
            int template_type,
            String label,
            String created_by,
            String email,
            int enterprise_id
    );

    public DatabaseResponse createWorkflowTemplate(
            int workflow_id,
            String metadata,
            String HMAC,
            String created_by
    );

    public ResponseCode getResponse(String name);

    public List<ResponseCode> getResponseCodes();

    public List<Integer> getVerificationFunctionIDGrantForRP(int relyingPartyId);

    public DatabaseResponse createUserActivityLog(
            String email,
            int enterprise_id,
            String module,
            String action,
            String info_key,
            String info_value,
            String detail,
            String agent,
            String agent_detail,
            String HMAC,
            String create_by
    );

    public DatabaseResponse createFileManagement(
            String UUID,
            String name,
            int pages,
            int size,
            int width,
            int height,
            byte[] fileData,
            String HMAC,
            String created_by,
            String DBMS
    );

//    public DatabaseResponse addEnterpriseUser(
//            String email_owner,
//            int enterprise_id,
//            String email_user,
//            String role,
//            int status,
//            String hmac
//    );
    public DatabaseResponse createTransaction(
            String email,
            int logID,
            int UUID,
            int type,
            String IPAddress,
            String initFile, //Name file
            int pY,
            int pX,
            int PC,
            int pS,
            int pages,
            String des,
            String hmac,
            String created_by
    );

    public DatabaseResponse createWorkflowActivity(
            int enterprise_id,
            int workflow_id,
            String user_email,
            String transaction_id,
            int file_link,
            int csv,
            String remark,
            int use_test_token,
            int enable_production,
            int enable_update,
            int workflow_type,
            String request_data,
            String HMAC,
            String created_by
    );

    public DatabaseResponse getDataRP(
            int enterprise_id
    );

    public DatabaseResponse createQR(
            String metaData,
            String hmac,
            String created_by
    );

    public DatabaseResponse getFileManagement(
            int fileID
    );

    public DatabaseResponse getAsset(
            int assetID
    );   

    public DatabaseResponse uploadAsset(
            String email,
            int enterprise_id,
            int type,
            String file_name,
            long size,
            String UUID,
            String pDBMS_PROPERTY,           
            String metaData,
            byte[] fileData,                        
            String hmac,
            String createdBy
    );
    
    public List<WorkflowActivity> getListWorkflowActivity();

    public DatabaseResponse getWorkflowDetail(int id);

    public DatabaseResponse createWorkflowDetail(
            int id,
            HashMap<String, Object> hashMap,
            String HMAC,
            String created_by);
    
    public DatabaseResponse getTemplateType(int id);

    public HashMap<Integer, String> initTemplateTypeForProcessClass();

    public DatabaseResponse updateFileManagement(
            int id,
            String UUID,
            String DBMS,
            String name,
            int pages,
            int width,
            int height,
            int status,
            String hmac,
            String created_by,            
            String last_modified_by,                    
            byte[] data);
    
    public DatabaseResponse getWorkflow(int id);
    
    public DatabaseResponse getAllWorkflowTemplateType();
    
    public DatabaseResponse getAssetType();
    
    public DatabaseResponse getListWorkflow(
            String email,
            int enterprise_id,
            String status,
            String type,
            boolean use_metadata,
            String metadata,
            int offset,
            int rowcount            
    );
    
    public DatabaseResponse getWorkflowTemplate(int id);
    
    public DatabaseResponse updateWorkflowDetail_Option(int id,
            HashMap<String, Object> map,
            String hmac,
            String created_by);
    
    public DatabaseResponse login(
            String email,
            String pass
    );
    
    public DatabaseResponse getWorkflowActivity(int id);

    public DatabaseResponse getEnterpriseInfoOfUser(String email);

    public DatabaseResponse getListWorkflowTemplateType();
}

