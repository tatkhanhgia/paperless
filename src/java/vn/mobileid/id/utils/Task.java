/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.utils;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessService_V2;
import vn.mobileid.id.paperless.kernel.UpdateUser;
import vn.mobileid.id.paperless.objects.Account;

/**
 *
 * @author GiaTK
 */
public class Task implements Runnable{
    private volatile InternalResponse response;
    private Object[] data;
    private String transactionId;

    public Task(Object[] data, String transactionId) {
        this.data = data;
        this.transactionId = transactionId;
    }
    
    public void setResponse(InternalResponse response){
        this.response = response;
    }
    
    public InternalResponse getResponse() {
        return response;
    }       
    
    public Object[] getData(){
        return data;
    }
    
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }    
       
    
}