/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.license;

/**
 *
 * @author ADMIN
 */
public class LicenseServerData {

    private long createdDt;
    private long expiredDt;
    private boolean liveMatchingEnabled;
    private long liveMatchingMaxTransactionCounter;
    
    private boolean facialFeatureAreaEnabled;
    private long facialFeatureAreaMaxTransactionCounter;
    
    private boolean paEnabled;
    private long paMaxTransactionCounter;
    
    private boolean cadesVerificationEnabled;
    private long cadesVerificationMaxTransactionCounter;
    
    private boolean xadesVerificationEnabled;
    private long xadesVerificationMaxTransactionCounter;
    
    private boolean padesVerificationEnabled;
    private long padesVerificationMaxTransactionCounter;

    public long getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(long createdDt) {
        this.createdDt = createdDt;
    }

    public long getExpiredDt() {
        return expiredDt;
    }

    public void setExpiredDt(long expiredDt) {
        this.expiredDt = expiredDt;
    }

    public boolean isLiveMatchingEnabled() {
        return liveMatchingEnabled;
    }

    public void setLiveMatchingEnabled(boolean liveMatchingEnabled) {
        this.liveMatchingEnabled = liveMatchingEnabled;
    }

    public long getLiveMatchingMaxTransactionCounter() {
        return liveMatchingMaxTransactionCounter;
    }

    public void setLiveMatchingMaxTransactionCounter(long liveMatchingMaxTransactionCounter) {
        this.liveMatchingMaxTransactionCounter = liveMatchingMaxTransactionCounter;
    }

    public boolean isFacialFeatureAreaEnabled() {
        return facialFeatureAreaEnabled;
    }

    public void setFacialFeatureAreaEnabled(boolean facialFeatureAreaEnabled) {
        this.facialFeatureAreaEnabled = facialFeatureAreaEnabled;
    }

    public long getFacialFeatureAreaMaxTransactionCounter() {
        return facialFeatureAreaMaxTransactionCounter;
    }

    public void setFacialFeatureAreaMaxTransactionCounter(long facialFeatureAreaMaxTransactionCounter) {
        this.facialFeatureAreaMaxTransactionCounter = facialFeatureAreaMaxTransactionCounter;
    }

    public boolean isPaEnabled() {
        return paEnabled;
    }

    public void setPaEnabled(boolean paEnabled) {
        this.paEnabled = paEnabled;
    }

    public long getPaMaxTransactionCounter() {
        return paMaxTransactionCounter;
    }

    public void setPaMaxTransactionCounter(long paMaxTransactionCounter) {
        this.paMaxTransactionCounter = paMaxTransactionCounter;
    }

    public boolean isCadesVerificationEnabled() {
        return cadesVerificationEnabled;
    }

    public void setCadesVerificationEnabled(boolean cadesVerificationEnabled) {
        this.cadesVerificationEnabled = cadesVerificationEnabled;
    }

    public long getCadesVerificationMaxTransactionCounter() {
        return cadesVerificationMaxTransactionCounter;
    }

    public void setCadesVerificationMaxTransactionCounter(long cadesVerificationMaxTransactionCounter) {
        this.cadesVerificationMaxTransactionCounter = cadesVerificationMaxTransactionCounter;
    }

    public boolean isXadesVerificationEnabled() {
        return xadesVerificationEnabled;
    }

    public void setXadesVerificationEnabled(boolean xadesVerificationEnabled) {
        this.xadesVerificationEnabled = xadesVerificationEnabled;
    }

    public long getXadesVerificationMaxTransactionCounter() {
        return xadesVerificationMaxTransactionCounter;
    }

    public void setXadesVerificationMaxTransactionCounter(long xadesVerificationMaxTransactionCounter) {
        this.xadesVerificationMaxTransactionCounter = xadesVerificationMaxTransactionCounter;
    }

    public boolean isPadesVerificationEnabled() {
        return padesVerificationEnabled;
    }

    public void setPadesVerificationEnabled(boolean padesVerificationEnabled) {
        this.padesVerificationEnabled = padesVerificationEnabled;
    }

    public long getPadesVerificationMaxTransactionCounter() {
        return padesVerificationMaxTransactionCounter;
    }

    public void setPadesVerificationMaxTransactionCounter(long padesVerificationMaxTransactionCounter) {
        this.padesVerificationMaxTransactionCounter = padesVerificationMaxTransactionCounter;
    }
    
    
}
