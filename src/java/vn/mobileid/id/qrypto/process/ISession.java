/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.process;

import java.util.List;
import vn.mobileid.id.qrypto.object.Configuration;
import vn.mobileid.id.qrypto.object.QRSchema;
import vn.mobileid.id.qrypto.request.ClaimRequest;
import vn.mobileid.id.qrypto.response.ClaimResponse;
import vn.mobileid.id.qrypto.response.IssueQryptoWithFileAttachResponse;

/**
 *
 * @author GiaTK
 */
public interface ISession {
    public void login() throws Exception;
    
    public IssueQryptoWithFileAttachResponse issueQryptoWithFileAttach(
            QRSchema schema,
            Configuration configuration
    ) throws Exception;
            
    public ClaimResponse dgci_wallet_claim(
            ClaimRequest request
    ) throws Exception;
}
