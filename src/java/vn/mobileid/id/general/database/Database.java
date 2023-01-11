/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.database;

import java.util.Date;
import java.util.List;
import vn.mobileid.id.general.objects.IDXCertificate;
import vn.mobileid.id.general.objects.IdentityProcessType;
import vn.mobileid.id.general.gateway.p2p.objects.P2P;
import vn.mobileid.id.general.gateway.p2p.objects.P2PEntityAttribute;
import vn.mobileid.id.general.gateway.p2p.objects.P2PFunction;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.Entity;
import vn.mobileid.id.general.objects.IdentityDocument;
import vn.mobileid.id.general.objects.IdentityDocumentType;
import vn.mobileid.id.general.objects.IdentityProcess;
import vn.mobileid.id.general.objects.IdentityProcessStatus;
import vn.mobileid.id.general.objects.IdentitySubject;
import vn.mobileid.id.general.objects.IdentityProcessAttribute;
import vn.mobileid.id.general.objects.IdentityProvider;
import vn.mobileid.id.general.objects.Province;
import vn.mobileid.id.general.objects.RegistrationParty;
import vn.mobileid.id.general.objects.RelyingParty;
import vn.mobileid.id.general.objects.RelyingPartyAttribute;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.general.objects.TSAProfile;
import vn.mobileid.id.qrypto.objects.Workflow_JSNObject;

/**
 *
 * @author ADMIN
 */
public interface Database {

    public DatabaseResponse createWorkflow(
            int template_type,
            String label,
            String created_by,
            String email
    );

    public DatabaseResponse createWorkflowTemplate(
            int workflow_id,
            String metadata,
            String HMAC,
            String created_by
    );
    
    public ResponseCode getResponse(String name);

    public List<Entity> getEntities();

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
            String created_by
    );
    
    public DatabaseResponse addEnterpriseUser(
            String email_owner,
            int enterprise_id,
            String email_user,
            String role,
            int status,
            String hmac
    );
    
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
}
