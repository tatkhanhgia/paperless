package RestfulFactory.Request;

import restful.sdk.API.Types.UserType;

public class AgreementAssignRequest extends Request {

    private String agreementUUID;
    private String user;
    private UserType userType; //UserType nam trong Types.cs
    private String authorizeCode;

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

    public String getAuthorizeCode() {
        return authorizeCode;
    }

    public void setAuthorizeCode(String authorizeCode) {
        this.authorizeCode = authorizeCode;
    }
    
}
