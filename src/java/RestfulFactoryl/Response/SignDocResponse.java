/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RestfulFactoryl.Response;

import java.util.List;

/**
 *
 * @author Tuan Pham
 */
public class SignDocResponse extends Response {

    private List<byte[]> documentWithSignature;
    private int remainingSigningCounter;

    private int remainingCounter;
    private int tempLockoutDuration;

    public List<byte[]> getDocumentWithSignature() {
        return documentWithSignature;
    }

    public void setDocumentWithSignature(List<byte[]> documentWithSignature) {
        this.documentWithSignature = documentWithSignature;
    }

    public int getRemainingSigningCounter() {
        return remainingSigningCounter;
    }

    public void setRemainingSigningCounter(int remainingSigningCounter) {
        this.remainingSigningCounter = remainingSigningCounter;
    }

    public int getRemainingCounter() {
        return remainingCounter;
    }

    public void setRemainingCounter(int remainingCounter) {
        this.remainingCounter = remainingCounter;
    }

    public int getTempLockoutDuration() {
        return tempLockoutDuration;
    }

    public void setTempLockoutDuration(int tempLockoutDuration) {
        this.tempLockoutDuration = tempLockoutDuration;
    }
    
}
