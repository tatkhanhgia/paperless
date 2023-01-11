package RestfulFactory.Request;

import RestfulFactory.Model.ClientInfo;

public class LoginRequest extends Request {

    private String relyingParty;
    private boolean rememberMeEnabled;
    private ClientInfo clientInfo;

    public LoginRequest() {
    }

    public boolean getRememberMeEnabled() {
        return rememberMeEnabled;
    }

    public void setRememberMeEnabled(boolean rememberMeEnabled) {
        this.rememberMeEnabled = rememberMeEnabled;
    }

    public String getRelyingParty() {
        return relyingParty;
    }

    public void setRelyingParty(String relyingParty) {
        this.relyingParty = relyingParty;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }
    
}
