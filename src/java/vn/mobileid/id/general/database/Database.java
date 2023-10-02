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
    ) throws Exception;

    public DatabaseResponse createWorkflowTemplate(
            int workflow_id,
            String metadata,
            String HMAC,
            String created_by,
            String transaction_id
    ) throws Exception;

    public ResponseCode getResponse(
            String name,
            String transaction_id) throws Exception;

    public List<ResponseCode> getResponseCodes() throws Exception;

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
    ) throws Exception;

    public DatabaseResponse createFileManagement(
            String UUID,
            String name,
            int pages,
            int size,
            float width,
            float height,
            byte[] fileData,
            String HMAC,
            String created_by,
            String DBMS,
            String file_type,
            String signing_properties,
            String hash_values,
            String transaction_id
    ) throws Exception;

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
    ) throws Exception;

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
    ) throws Exception;

    public DatabaseResponse getDataRP(
            int enterprise_id,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse createQR(
            String metaData,
            String hmac,
            String created_by,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse getFileManagement(
            long fileID,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse getAsset(
            int assetID,
            String transaction_id
    ) throws Exception;

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
    ) throws Exception;

    public List<WorkflowActivity> getListWorkflowActivity() throws Exception;

    public DatabaseResponse getListWorkflowActivityWithCondition(
            String email,
            int aid,
            String email_search,
            Date date,
            String g_type,
            String status,
            boolean is_test,
            boolean is_product,
            boolean is_custom_range,
            Date fromdate,
            Date todate,
            String languagename,
            int offset,
            int rowcount,
            String transactionID
    ) throws Exception;

    public DatabaseResponse getWorkflowDetail(int id, String transaction_id) throws Exception;

    public DatabaseResponse createWorkflowDetail(
            int id,
            HashMap<String, Object> hashMap,
            String HMAC,
            String created_by,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse getTemplateType(int id, String transaction_id) throws Exception;

    public HashMap<Integer, String> initTemplateTypeForProcessClass() throws Exception;

    public DatabaseResponse updateFileManagement(
            long id,
            String UUID,
            String DBMS,
            String name,
            int pages,
            long size,
            float width,
            float height,
            int status,
            String hmac,
            String created_by,
            String last_modified_by,
            byte[] data,
            boolean isSigned,
            String file_type,
            String signing_properties,
            String hash_values,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse getWorkflow(int id, String transaction_id) throws Exception;

    public DatabaseResponse getHashMapWorkflowTemplateType() throws Exception;

    public DatabaseResponse getAssetType() throws Exception;

    public DatabaseResponse getListWorkflow(
            String email,
            int enterprise_id,
            String status,
            String type,
            boolean use_metadata,
            String metadata,
            int offset,
            int rowcount,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse getWorkflowTemplate(int id, String transaction_id) throws Exception;

    public DatabaseResponse updateWorkflowDetail_Option(
            int id,
            String email,
            int ent_id,
            HashMap<String, Object> map,
            String hmac,
            String created_by,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse login(
            String email,
            String pass,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse getWorkflowActivity(
            int id,
            String transaction_id)
            throws Exception;

    public DatabaseResponse getEnterpriseInfoOfUser(
            String email,
            String transaction_id) throws Exception;

    public DatabaseResponse getListWorkflowTemplateType() throws Exception;

    public DatabaseResponse writeRefreshToken(
            String email,
            String session_id,
            int client_credentials_enabled,
            String clientID,
            Date issue_on,
            Date expires_on,
            String hmac,
            String created_by, String transaction_id) throws Exception;

    public DatabaseResponse removeRefreshToken(
            String refreshtoken,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse checkAccessToken(
            String email,
            String accesstoken,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse getRefreshToken(
            String refreshtoken,
            String transaction_id
    ) throws Exception;

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
    ) throws Exception;

    public DatabaseResponse getUser(
            String email,
            int user_id,
            int enterprise_id,
            String transaction_id,
            boolean returnTypeUser
    ) throws Exception;

    public DatabaseResponse getKEYAPI(
            int enterprise_id,
            String clientID,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse createUser(
            String email,
            String password,
            String mobile_number,
            String created_user_email,
            String created_user_name,
            int enterprise_id,
            String role_name,
            long pass_expired_at,
            int business_type,
            String org_web,
            String hmac,
            String transactionID
    ) throws Exception;

    public DatabaseResponse getEmailTemplate(
            int language,
            String email_noti,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse getEnterpriseInfo(
            int enterprise_id,
            String enterprise_name
    ) throws Exception;

    public DatabaseResponse getAuthenticatePassword(
            String email,
            int languege_id,
            String email_type,
            String transactionID
    ) throws Exception;

    public DatabaseResponse verifyEmail(
            String email,
            String password,
            String transactionID
    ) throws Exception;

    public DatabaseResponse deactiveWorkflow(
            int workflow_id,
            String email,
            int enterprise_id,
            String modified_by,
            String transactionID
    ) throws Exception;

    public DatabaseResponse reactiveWorkflow(
            int workflow_id,
            String email,
            int enterprise_id,
            String modified_by,
            String transactionID
    ) throws Exception;

    public DatabaseResponse updateWorkflowTemplate(
            int workflow_id,
            String user_email,
            int enterprise_id,
            String meta_data,
            String hmac,
            String last_modified_by,
            String transactionID
    ) throws Exception;

    public DatabaseResponse getListAsset(
            int ent_id,
            String email,
            String file_name,
            String type,
            int offset,
            int rowcount,
            String transactionID
    ) throws Exception;

    public DatabaseResponse getCertificate(
            String service_name,
            String remark,
            String url,
            String transactionID
    ) throws Exception;

    public DatabaseResponse getStatusUser(
            String email,
            String transactionID
    ) throws Exception;

    public DatabaseResponse updateUserPassword(
            String email,
            String password,
            String transactionID
    ) throws Exception;

    public DatabaseResponse updateUserPassword(
            String email,
            String old_password,
            String new_password,
            String transactionID
    ) throws Exception;

    public DatabaseResponse updateEnterpriseInfo(
            int enterprise_id,
            String dataRP,
            byte[] fileP12,
            String transactionID
    ) throws Exception;

    public DatabaseResponse getQRSize(
            String qr_size_name,
            String transactionID
    ) throws Exception;

    public DatabaseResponse updateRequestDataOfWorkflowActivity(
            int id,
            String meta_data,
            String modified_by,
            String transactionID
    ) throws Exception;

    public DatabaseResponse updateStatusWorkflowActivity(
            int id,
            String status,
            boolean process_enable,
            String last_modified_by,
            String transactionID
    ) throws Exception;

    public DatabaseResponse getTotalRecordsWorkflow(
            String email,
            int enterprise_id,
            String status,
            String type,
            boolean use_metadata,
            String metadata,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse logIntoDB(
            String email,
            int enterprise_id,
            int workflow_activity,
            String app_name,
            String api_key,
            String version,
            String service_name,
            String url,
            String http_verb,
            int status_code,
            String request,
            String response,
            String hmac,
            String created_by,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse getPolicyAttribute(
            int id
    ) throws Exception;

    public DatabaseResponse updateQR(
            int id,
            String meta_data,
            String image,
            String modified_by,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse updateAsset(
            int id,
            String email,
            String file_name,
            int asset_type,
            long size,
            String uuid,
            String dms,
            String meta_data,
            byte[] binary_data,
            String hmac,
            String modified_by,
            String used_by,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse deleteAsset(
            int id,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse getTransaction(
            String id,
            String transaction_id
    ) throws Exception;

    public DatabaseResponse getListUser(
            String enterpriseName,
            int enterpriseId,
            int offset,
            int rowCount,
            String transactionId
    ) throws Exception;
    
    public DatabaseResponse getTotalRecordsAsset(
            int enterpriseId,
            String email,
            String fileName,
            String type,
            String transactionId
    ) throws Exception;
    
    public DatabaseResponse getTotalRecordsWorkflowActivity(
            String email,
            int enterpriseId,
            String emailSearch,
            String date,
            String gType,
            String status,
            boolean isTest,
            boolean isProduct,            
            boolean isCustomRange,
            String fromDate,
            String toDate,
            String languageName,
            String transactionId
           
    )throws Exception;
}
