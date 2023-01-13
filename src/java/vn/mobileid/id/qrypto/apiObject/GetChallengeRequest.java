/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.apiObject;

/**
 *
 * @author GiaTK
 */
public class GetChallengeRequest {
    private String challenge_type;
    private String transaction_data;
    private String lang;
    private String username;

    public String getChallenge_type() {
        return challenge_type;
    }

    public void setChallenge_type(String challenge_type) {
        this.challenge_type = challenge_type;
    }

    
    public String getTransaction_data() {
        return transaction_data;
    }

    public void setTransaction_data(String transaction_data) {
        this.transaction_data = transaction_data;
    }
    
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
