/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.objects.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import vn.mobileid.id.qrypto.objects.*;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Admin
 */
@JsonIgnoreProperties("code")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetToken_IAM_MessageJSNObject {

    private String code;
    private String message;

    //GetToken response
    private String accessToken;
    private String scope;
    private String refreshToken;
    private int refreshToken_expires_in;
    private int expires_in;
    private String tokenType;

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("access_token")
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accesstoken) {
        this.accessToken = accesstoken;
    }

    @JsonProperty("scope")
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @JsonProperty("refresh_token")
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshtoken) {
        this.refreshToken = refreshtoken;
    }

    @JsonProperty("expires_in")
    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    @JsonProperty("token_type")
    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @JsonProperty("refresh_token_expired_in")
    public int getRefreshToken_expires_in() {
        return refreshToken_expires_in;
    }

    public void setRefreshToken_expires_in(int refreshToken_expires_in) {
        this.refreshToken_expires_in = refreshToken_expires_in;
    }

}
