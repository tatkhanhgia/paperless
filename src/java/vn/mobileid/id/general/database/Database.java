/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.database;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.paperless.objects.WorkflowActivity;

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
            int enterprise_id,
            String transaction_id
    );

    public DatabaseResponse createWorkflowTemplate(
            int workflow_id,
            String metadata,
            String HMAC,
            String created_by,
            String transaction_id
    );

    public ResponseCode getResponse(
            String name,
            String transaction_id);

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
            String create_by,
            String transaction_id
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
            String DBMS,
            String transaction_id
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
            String created_by,
            String transaction_id
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
            String created_by,
            String transactionID
    );

    public DatabaseResponse getDataRP(
            int enterprise_id,
            String transaction_id
    );

    public DatabaseResponse createQR(
            String metaData,
            String hmac,
            String created_by,
            String transaction_id
    );

    public DatabaseResponse getFileManagement(
            int fileID,
            String transaction_id
    );

    public DatabaseResponse getAsset(
            int assetID,
            String transaction_id
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
            String createdBy,
            String transaction_id
    );
    
    public List<WorkflowActivity> getListWorkflowActivity();

    public DatabaseResponse getWorkflowDetail(int id,String transaction_id);

    public DatabaseResponse createWorkflowDetail(
            int id,
            HashMap<String, Object> hashMap,
            String HMAC,
            String created_by,
            String transaction_id
    );
    
    public DatabaseResponse getTemplateType(int id, String transaction_id);

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
            byte[] data,
            boolean isSigned,
            String transaction_id
    );
    
    public DatabaseResponse getWorkflow(int id,String transaction_id);
    
    public DatabaseResponse getHashMapWorkflowTemplateType();
    
    public DatabaseResponse getAssetType();
    
    public DatabaseResponse getListWorkflow(
            String email,
            int enterprise_id,
            String status,
            String type,
            boolean use_metadata,
            String metadata,
            int offset,
            int rowcount  ,
            String transaction_id
    );
    
    public DatabaseResponse getWorkflowTemplate(int id,String transaction_id);
    
    public DatabaseResponse updateWorkflowDetail_Option(int id,
            HashMap<String, Object> map,
            String hmac,
            String created_by,
            String transaction_id
    );
    
    public DatabaseResponse login(
            String email,
            String pass,
            String transaction_id
    );
    
    public DatabaseResponse getWorkflowActivity(int id, String transaction_id);

    public DatabaseResponse getEnterpriseInfoOfUser(String email, String transaction_id);

    public DatabaseResponse getListWorkflowTemplateType();
    
    public DatabaseResponse writeRefreshToken(
            String email,
            String session_id,
            int client_credentials_enabled,
            String clientID,
            Date issue_on,
            Date expires_on,
            String hmac,
            String created_by,String transaction_id);
    
    public DatabaseResponse removeRefreshToken(            
            String refreshtoken,
            String transaction_id
    );
    
    public DatabaseResponse checkAccessToken(
            String email,
            String accesstoken,
            String transaction_id
    );
    
    public DatabaseResponse getRefreshToken(
            String refreshtoken,
            String transaction_id
    );
    
    public DatabaseResponse updateRefreshToken(
            String email,
            String session_id,
            int client_credentials_enabled,            
            Date issue_on,
            Date expires_on,
            int status,
            String hmac,
            String created_by,
            String transaction_id
    );
    
    public DatabaseResponse getUser(
            String email,
            int enterprise_id,
            String transaction_id
    );
    
    public DatabaseResponse getKEYAPI(
            int enterprise_id,
            String clientID,
            String transaction_id
    );
    
    public DatabaseResponse createUser(
            String email,
            String created_user_email,
            int enterprise_id,
            String role_name,
            long pass_expired_at,
            int business_type,
            String org_web,
            String hmac,
            String transactionID
    );
    
    public DatabaseResponse getEmailTemplate(
            int language,
            String email_noti,
            String transaction_id
    );
    
    public DatabaseResponse getEnterpriseInfo(
            int enterprise_id
    );
        
}

