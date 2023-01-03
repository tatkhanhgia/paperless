/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.objects;

import java.util.Date;

/**
 *
 * @author ADMIN
 */
public class DatabaseResponse {
    private long identityLogId;
    private Date identityLogDt;
    
    private long identitySubjectId;
    private Date identitySubjectDt;
    
    private long p2pId;
    private Date p2pDt;
    
    private long identityProcessId;
    private Date identityProcessDt;
    
    private long identityDocumentId;
    private Date identityDocumentDt;
    
    private long verificationLogId;
    private Date verificationLogDt;
    
    private long tsaLogId;
    private Date tsaLogDt;
    
    private long identityID;
    private Date identityDT;
    
    // otp
    private int status;
    private int remainingCounter;
    private long ownerID;
            

    public long getIdentityLogId() {
        return identityLogId;
    }

    public void setIdentityLogId(long identityLogId) {
        this.identityLogId = identityLogId;
    }

    public Date getIdentityLogDt() {
        return identityLogDt;
    }

    public void setIdentityLogDt(Date identityLogDt) {
        this.identityLogDt = identityLogDt;
    }

    public long getIdentitySubjectId() {
        return identitySubjectId;
    }

    public void setIdentitySubjectId(long identitySubjectId) {
        this.identitySubjectId = identitySubjectId;
    }

    public Date getIdentitySubjectDt() {
        return identitySubjectDt;
    }

    public void setIdentitySubjectDt(Date identitySubjectDt) {
        this.identitySubjectDt = identitySubjectDt;
    }

    public long getP2pId() {
        return p2pId;
    }

    public void setP2pId(long p2pId) {
        this.p2pId = p2pId;
    }

    public Date getP2pDt() {
        return p2pDt;
    }

    public void setP2pDt(Date p2pDt) {
        this.p2pDt = p2pDt;
    }

    public long getIdentityProcessId() {
        return identityProcessId;
    }

    public void setIdentityProcessId(long identityProcessId) {
        this.identityProcessId = identityProcessId;
    }

    public Date getIdentityProcessDt() {
        return identityProcessDt;
    }

    public void setIdentityProcessDt(Date identityProcessDt) {
        this.identityProcessDt = identityProcessDt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRemainingCounter() {
        return remainingCounter;
    }

    public void setRemainingCounter(int remainingCounter) {
        this.remainingCounter = remainingCounter;
    }

    public long getIdentityDocumentId() {
        return identityDocumentId;
    }

    public void setIdentityDocumentId(long identityDocumentId) {
        this.identityDocumentId = identityDocumentId;
    }

    public Date getIdentityDocumentDt() {
        return identityDocumentDt;
    }

    public void setIdentityDocumentDt(Date identityDocumentDt) {
        this.identityDocumentDt = identityDocumentDt;
    }

    public long getVerificationLogId() {
        return verificationLogId;
    }

    public void setVerificationLogId(long verificationLogId) {
        this.verificationLogId = verificationLogId;
    }

    public Date getVerificationLogDt() {
        return verificationLogDt;
    }

    public void setVerificationLogDt(Date verificationLogDt) {
        this.verificationLogDt = verificationLogDt;
    }

    public long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(long ownerID) {
        this.ownerID = ownerID;
    }

    public long getTsaLogId() {
        return tsaLogId;
    }

    public void setTsaLogId(long tsaLogId) {
        this.tsaLogId = tsaLogId;
    }

    public Date getTsaLogDt() {
        return tsaLogDt;
    }

    public void setTsaLogDt(Date tsaLogDt) {
        this.tsaLogDt = tsaLogDt;
    }

    public long getIdentityID() {
        return identityID;
    }

    public void setIdentityID(long identityID) {
        this.identityID = identityID;
    }

    public Date getIdentityDT() {
        return identityDT;
    }

    public void setIdentityDT(Date identityDT) {
        this.identityDT = identityDT;
    }
    
    
}
