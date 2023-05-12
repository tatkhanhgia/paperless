package restful.sdk.API;

import java.util.Base64;

public class Property {

    private String profile = "rssp-119.432-v2.0";
    private String baseUrl;
    private String relyingParty;
    private String relyingPartyUser;
    private String relyingPartyPassword;
    private String relyingPartySignature;
    private String relyingPartyKeyStore;
    private String relyingPartyKeyStorePassword;
    private byte[] relyingPartyKeyStoreData;

    public Property(String baseUrl,
            String relyingParty,
            String relyingPartyUser,
            String relyingPartyPassword,
            String relyingPartySignature,
            String relyingPartyKeyStore,
            String relyingPartyKeyStorePassword) {

        this.baseUrl = baseUrl;
        this.relyingParty = relyingParty;
        this.relyingPartyUser = relyingPartyUser;
        this.relyingPartyPassword = relyingPartyPassword;
        this.relyingPartySignature = relyingPartySignature;
        this.relyingPartyKeyStore = relyingPartyKeyStore;
        this.relyingPartyKeyStorePassword = relyingPartyKeyStorePassword;
    }

    public Property(String baseUrl,
            String relyingParty,
            String relyingPartyUser,
            String relyingPartyPassword,
            String relyingPartySignature,
            byte[] relyingPartyKeyStoreData,
            String relyingPartyKeyStorePassword) {

        this.baseUrl = baseUrl;
        this.relyingParty = relyingParty;
        this.relyingPartyUser = relyingPartyUser;
        this.relyingPartyPassword = relyingPartyPassword;
        this.relyingPartySignature = relyingPartySignature;
        this.relyingPartyKeyStoreData = relyingPartyKeyStoreData;
        this.relyingPartyKeyStorePassword = relyingPartyKeyStorePassword;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getRelyingParty() {
        return relyingParty;
    }

    public void setRelyingParty(String relyingParty) {
        this.relyingParty = relyingParty;
    }

    public String getRelyingPartyUser() {
        return relyingPartyUser;
    }

    public void setRelyingPartyUser(String relyingPartyUser) {
        this.relyingPartyUser = relyingPartyUser;
    }

    public String getRelyingPartyPassword() {
        return relyingPartyPassword;
    }

    public void setRelyingPartyPassword(String relyingPartyPassword) {
        this.relyingPartyPassword = relyingPartyPassword;
    }

    public String getRelyingPartySignature() {
        return relyingPartySignature;
    }

    public void setRelyingPartySignature(String relyingPartySignature) {
        this.relyingPartySignature = relyingPartySignature;
    }

    public String getRelyingPartyKeyStore() {
        return relyingPartyKeyStore;
    }

    public void setRelyingPartyKeyStore(String relyingPartyKeyStore) {
        this.relyingPartyKeyStore = relyingPartyKeyStore;
    }

    public String getRelyingPartyKeyStorePassword() {
        return relyingPartyKeyStorePassword;
    }

    public void setRelyingPartyKeyStorePassword(String relyingPartyKeyStorePassword) {
        this.relyingPartyKeyStorePassword = relyingPartyKeyStorePassword;
    }

    public String getAuthorization() throws Exception, Throwable {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String data2sign = relyingPartyUser + relyingPartyPassword + relyingPartySignature + timestamp;
        String pkcs1Signature = Utils.getPKCS1Signature(data2sign, relyingPartyKeyStore, relyingPartyKeyStorePassword);

        String strSSL2 = (relyingPartyUser + ":" + relyingPartyPassword + ":" + relyingPartySignature + ":" + timestamp + ":" + pkcs1Signature);
        byte[] byteSSL2 = strSSL2.getBytes();
        return "SSL2 " + Base64.getEncoder().encodeToString(byteSSL2);
    }

    public String getAuthorization2() throws Exception, Throwable {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String data2sign = relyingPartyUser + relyingPartyPassword + relyingPartySignature + timestamp;
        String pkcs1Signature = Utils.getPKCS1Signature(data2sign, relyingPartyKeyStoreData, relyingPartyKeyStorePassword);

        String strSSL2 = (relyingPartyUser + ":" + relyingPartyPassword + ":" + relyingPartySignature + ":" + timestamp + ":" + pkcs1Signature);
        byte[] byteSSL2 = strSSL2.getBytes();
        return "SSL2 " + Base64.getEncoder().encodeToString(byteSSL2);
    }
    
    public String getBasicToken(String user, String pass){
        String temp = "USERNAME:"+user + ":" + pass;
        return "Basic "+ Base64.getEncoder().encodeToString(temp.getBytes());
    }
    
    public byte[] getRelyingPartyKeyStoreData() {
        return relyingPartyKeyStoreData;
    }

    public void setRelyingPartyKeyStoreData(byte[] relyingPartyKeyStoreData) {
        this.relyingPartyKeyStoreData = relyingPartyKeyStoreData;
    }

    
}
