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
import vn.mobileid.id.everification.objects.CertificationAuthority;
import vn.mobileid.id.general.objects.TSAProfile;
import vn.mobileid.id.qrypto.objects.QryptoWorkflowJSNObject;

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
}
