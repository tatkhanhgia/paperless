package RestfulFactory.Request;

import RestfulFactory.Model.ClientInfo;
import restful.sdk.API.Types.OperationMode;
import restful.sdk.API.Types.SignAlgo;
import RestfulFactory.Model.DocumentDigests;

public class AuthorizeRequest extends Request {

    private String agreementUUID;
    private String credentialID;
    private String authorizeCode;
    
    private int numSignatures;
    private DocumentDigests documentDigests;
    private ClientInfo clientInfo;
    
    private String notificationMessage;
    private String messageCaption;
    private String message;
    
    private String logoURI;
    private String bgImageURI;
    private String rpIconURI;
    private String rpName;
    //public string confirmationPolicy { get; set; }
    private boolean vcEnabled;
    private boolean acEnabled;
    
    private OperationMode operationMode;
    
    private String scaIdentity;
    private String responseURI;
    private int validityPeriod;
    private String[] documents;
    private SignAlgo signAlgo;
    private String signAlgoParams;

    public AuthorizeRequest() {
        this.operationMode = OperationMode.S;
        this.validityPeriod = 300;
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

    public String getAuthorizeCode() {
        return authorizeCode;
    }

    public void setAuthorizeCode(String authorizeCode) {
        this.authorizeCode = authorizeCode;
    }

    public int getNumSignatures() {
        return numSignatures;
    }

    public void setNumSignatures(int numSignatures) {
        this.numSignatures = numSignatures;
    }

    public DocumentDigests getDocumentDigests() {
        return documentDigests;
    }

    public void setDocumentDigests(DocumentDigests documentDigests) {
        this.documentDigests = documentDigests;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getMessageCaption() {
        return messageCaption;
    }

    public void setMessageCaption(String messageCaption) {
        this.messageCaption = messageCaption;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLogoURI() {
        return logoURI;
    }

    public void setLogoURI(String logoURI) {
        this.logoURI = logoURI;
    }

    public String getBgImageURI() {
        return bgImageURI;
    }

    public void setBgImageURI(String bgImageURI) {
        this.bgImageURI = bgImageURI;
    }

    public String getRpIconURI() {
        return rpIconURI;
    }

    public void setRpIconURI(String rpIconURI) {
        this.rpIconURI = rpIconURI;
    }

    public String getRpName() {
        return rpName;
    }

    public void setRpName(String rpName) {
        this.rpName = rpName;
    }

    public boolean getVcEnabled() {
        return vcEnabled;
    }

    public void setVcEnabled(boolean vcEnabled) {
        this.vcEnabled = vcEnabled;
    }

    public boolean getAcEnabled() {
        return acEnabled;
    }

    public void setAcEnabled(boolean acEnabled) {
        this.acEnabled = acEnabled;
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

    public String[] getDocuments() {
        return documents;
    }

    public void setDocuments(String[] documents) {
        this.documents = documents;
    }

    public SignAlgo getSignAlgo() {
        return signAlgo;
    }

    public void setSignAlgo(SignAlgo signAlgo) {
        this.signAlgo = signAlgo;
    }

    public String getSignAlgoParams() {
        return signAlgoParams;
    }

    public void setSignAlgoParams(String signAlgoParams) {
        this.signAlgoParams = signAlgoParams;
    }

}
