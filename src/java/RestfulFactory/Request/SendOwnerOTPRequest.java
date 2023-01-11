package RestfulFactory.Request;

import restful.sdk.API.Types.OTPType;
import restful.sdk.API.Types.UserType;

public class SendOwnerOTPRequest extends Request {

    private String agreementUUID;
    private String user;
    private UserType userType;
    private OTPType otpType;

    public String getAgreementUUID() {
        return agreementUUID;
    }

    public void setAgreementUUID(String agreementUUID) {
        this.agreementUUID = agreementUUID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public OTPType getOtpType() {
        return otpType;
    }

    public void setOtpType(OTPType otpType) {
        this.otpType = otpType;
    }
}
