package RestfulFactory.Request;

import RestfulFactory.Model.ClientInfo;
import RestfulFactory.Model.DocumentDigests;
import restful.sdk.API.Types.OperationMode;
import restful.sdk.API.Types.SignAlgo;

public class SignHashRequest extends Request {

    private String agreementUUID;
    private String credentialID;
    private String SAD;

    private DocumentDigests documentDigests;
    private SignAlgo signAlgo;
    private String signAlgoParams;

    private OperationMode operationMode;
    private String scaIdentity;
    private String responseURI;
    private int validityPeriod;
    
    private ClientInfo clientInfo;

    public SignHashRequest() {
        this.validityPeriod = 300;
        this.operationMode = OperationMode.S;
    }

    public SignAlgo getSignAlgo() {
        return signAlgo;
    }

    public void setSignAlgo(SignAlgo signAlgo) {
        this.signAlgo = signAlgo;
    }

    public String getAgreementUUID() {
        return agreementUUID;
    }

    public void setAgreementUUID(String agreementUUID) {
        this.agreementUUID = agreementUUID;
    }

    public String getCredentialID() {
        return credentialID;
    }

    public void setCredentialID(String credentialID) {
        this.credentialID = credentialID;
    }

    public String getSAD() {
        return SAD;
    }

    public void setSAD(String SAD) {
        this.SAD = SAD;
    }

    public DocumentDigests getDocumentDigests() {
        return documentDigests;
    }

    public void setDocumentDigests(DocumentDigests documentDigests) {
        this.documentDigests = documentDigests;
    }

    public String getSignAlgoParams() {
        return signAlgoParams;
    }

    public void setSignAlgoParams(String signAlgoParams) {
        this.signAlgoParams = signAlgoParams;
    }

    public OperationMode getOperationMode() {
        return operationMode;
    }

    public void setOperationMode(OperationMode operationMode) {
        this.operationMode = operationMode;
    }

    public String getScaIdentity() {
        return scaIdentity;
    }

    public void setScaIdentity(String scaIdentity) {
        this.scaIdentity = scaIdentity;
    }

    public String getResponseURI() {
        return responseURI;
    }

    public void setResponseURI(String responseURI) {
        this.responseURI = responseURI;
    }

    public int getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(int validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }
}
