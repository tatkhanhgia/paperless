/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.utils;

import java.util.concurrent.Callable;
import vn.mobileid.id.general.objects.InternalResponse;

/**
 *
 * @author GiaTK
 */
public class TaskV2 implements Callable<Object>{
    private InternalResponse response;
    private Object[] data;
    private String transactionId;

    public TaskV2(Object[] data, String transactionId) {
        this.data = data;
        this.transactionId = transactionId;
    }        
    
    public Object[] get(){
        return data;
    }
    
    public String getTransactionId(){
        return transactionId;
    }
    
    @Override
    public Object call() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
