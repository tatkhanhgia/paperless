/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.everification.object;

/**
 *
 * @author Mobile ID 22
 */
public class CreateOwnerResponse {
    public int status;
    public String message;
    public String owner_id;
    public String transaction_id;
    public String owner_index;
    public String username_owner;
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }
    
    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }
    
    public String getOwner_index() {
        return owner_index;
    }

    public void setOwner_index(String owner_index) {
        this.owner_index = owner_index;
    }
    
    public String getUsername_owner() {
        return username_owner;
    }

    public void setUsername_owner(String username_owner) {
        this.username_owner = username_owner;
    }
}
