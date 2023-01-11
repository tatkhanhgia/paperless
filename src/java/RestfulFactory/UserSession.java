/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestfulFactory;

import RestfulFactory.Request.LoginRequest;
import RestfulFactoryl.Response.LoginResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import restful.sdk.API.APIException;
import restful.sdk.API.HTTPUtils;
import restful.sdk.API.HttpResponse;
import restful.sdk.API.IUserSession;
import restful.sdk.API.Property;
import restful.sdk.API.Types.UserType;
import restful.sdk.API.Utils;

/**
 *
 * @author Tuan Pham
 */
public class UserSession implements IUserSession {

    private String bearer;
    private String refreshToken;
    private int retryLogin = 0;

    private Property property;
    private String lang;

    private String username;
    private String password;

    public UserSession(Property prop, String lang, String username, String password) throws Throwable {
        this.property = prop;
        this.lang = lang;
        this.username = username;
        this.password = password;
        this.login();
    }

    public boolean close() throws Throwable {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void login() throws Throwable {
        System.out.println("____________auth/login____________");
        String authHeader;
        if (refreshToken != null) {
            authHeader = refreshToken;
        } else {
            retryLogin++;
            String basic = UserType.USERNAME.toString()+ ":" + username + ":" + password;
            authHeader = property.getAuthorization() + ", Basic " + Utils.base64Encode(basic);
        }
        System.out.println("Login-retry: " + retryLogin);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setRememberMeEnabled(true);
        loginRequest.setRelyingParty(property.getRelyingParty());
        loginRequest.setLang(this.lang);
        //Console.WriteLine(loginRequest);

        String jsonReq = Utils.gsTmp.toJson(loginRequest);
        HttpResponse response = HTTPUtils.sendPost(property.getBaseUrl() + "auth/login", jsonReq, authHeader);
        if (!response.isStatus()) {
            try {
                throw new Exception(response.getMsg());
            } catch (Exception ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        LoginResponse signCloudResp = Utils.gsTmp.fromJson(response.getMsg(), LoginResponse.class);
        if (signCloudResp.getError() == 3005 || signCloudResp.getError() == 3006) {
            refreshToken = null;
            if (retryLogin >= 5) {
                retryLogin = 0;
                throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
            }
            login();
        } else if (signCloudResp.getError() != 0) {
            throw new APIException(signCloudResp.getError(), signCloudResp.getErrorDescription());
        } else {
            this.bearer = "Bearer " + signCloudResp.getAccessToken();
            if (signCloudResp.getRefreshToken() != null) {
                this.refreshToken = "Bearer " + signCloudResp.getRefreshToken();
            }
        }
    }

    @Override
    public boolean sendUserOTP() throws Throwable {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
