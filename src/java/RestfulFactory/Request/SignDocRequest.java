/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestfulFactory.Request;

import RestfulFactory.Model.ClientInfo;
import java.util.HashMap;
import java.util.List;
import restful.sdk.API.Types.ConformanceLevel;
import restful.sdk.API.Types.HashAlgorithmOID;
import restful.sdk.API.Types.OperationMode;
import restful.sdk.API.Types.SignAlgo;
import restful.sdk.API.Types.SignatureFormat;
import restful.sdk.API.Types.SignedPropertyType;

/**
 *
 * @author Tuan Pham
 */
public class SignDocRequest extends Request {

    private String agreementUUID;
    private String credentialID;
    private String SAD;

    private List<byte[]> documents;
    private HashAlgorithmOID hashAlgorithmOID;
    private SignAlgo signAlgo;
    private String signAlgoParams;

    private SignatureFormat signatureFormat;
    private ConformanceLevel conformanceLevel;
    private String signedEnvelopeProperty;
    private HashMap<SignedPropertyType, Object> signedProps;

    private OperationMode operationMode;
    private String scaIdentity;
    private String responseURI;
    private int validityPeriod;

    private ClientInfo clientInfo;

    public SignDocRequest() {
        this.validityPeriod = 300;
        this.operationMode = OperationMode.S;
        this.signatureFormat = SignatureFormat.P;
        this.conformanceLevel = ConformanceLevel.B_B;
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

    public List<byte[]> getDocuments() {
        return documents;
    }

    public void setDocuments(List<byte[]> documents) {
        this.documents = documents;
    }

    public HashAlgorithmOID getHashAlgorithmOID() {
        return hashAlgorithmOID;
    }

    public void setHashAlgorithmOID(HashAlgorithmOID hashAlgorithmOID) {
        this.hashAlgorithmOID = hashAlgorithmOID;
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

    public SignatureFormat getSignatureFormat() {
        return signatureFormat;
    }

    public void setSignatureFormat(SignatureFormat signatureFormat) {
        this.signatureFormat = signatureFormat;
    }

    public ConformanceLevel getConformanceLevel() {
        return conformanceLevel;
    }

    public void setConformanceLevel(ConformanceLevel conformanceLevel) {
        this.conformanceLevel = conformanceLevel;
    }

    public String getSignedEnvelopeProperty() {
        return signedEnvelopeProperty;
    }

    public void setSignedEnvelopeProperty(String signedEnvelopeProperty) {
        this.signedEnvelopeProperty = signedEnvelopeProperty;
    }

    public HashMap<SignedPropertyType, Object> getSignedProps() {
        return signedProps;
    }

    public void setSignedProps(HashMap<SignedPropertyType, Object> signedProps) {
        this.signedProps = signedProps;
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
