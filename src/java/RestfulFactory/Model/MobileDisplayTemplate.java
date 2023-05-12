package RestfulFactory.Model;

public class MobileDisplayTemplate {

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

    private String scaIdentity;

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

    public boolean isVcEnabled() {
        return vcEnabled;
    }

    public void setVcEnabled(boolean vcEnabled) {
        this.vcEnabled = vcEnabled;
    }

    public boolean isAcEnabled() {
        return acEnabled;
    }

    public void setAcEnabled(boolean acEnabled) {
        this.acEnabled = acEnabled;
    }

    public String getScaIdentity() {
        return scaIdentity;
    }

    public void setScaIdentity(String scaIdentity) {
        this.scaIdentity = scaIdentity;
    }
}
