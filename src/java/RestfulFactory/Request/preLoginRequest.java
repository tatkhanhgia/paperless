/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RestfulFactory.Request;

import restful.sdk.API.Types;

/**
 *
 * @author GiaTK
 */
public class preLoginRequest extends Request {

    private Types.UserType userType;
    private String user;

    public preLoginRequest(Types.UserType type, String user) {
        this.userType = type;
        this.user = user;
    }

    public preLoginRequest() {
    }

    public Types.UserType getType() {
        return userType;
    }

    public void setType(Types.UserType type) {
        this.userType = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
