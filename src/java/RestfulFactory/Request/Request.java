package RestfulFactory.Request;

public class Request {

    private String profile;
    private String lang;
    private String requestID;
    private String rpRequestID;

    public Request() {
        this.profile = "rssp-119.432-v2.0";
        this.lang = "VN";
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRpRequestID() {
        return rpRequestID;
    }

    public void setRpRequestID(String rpRequestID) {
        this.rpRequestID = rpRequestID;
    }
}
