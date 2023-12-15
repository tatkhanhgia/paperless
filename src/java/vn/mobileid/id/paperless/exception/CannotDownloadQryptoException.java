/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.exception;

/**
 *
 * @author Admin
 */
public class CannotDownloadQryptoException extends Exception{

    public CannotDownloadQryptoException() {
    }

    public CannotDownloadQryptoException(String string) {
        super(string);
    }

    public CannotDownloadQryptoException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public CannotDownloadQryptoException(Throwable thrwbl) {
        super(thrwbl);
    }

    public CannotDownloadQryptoException(String string, Throwable thrwbl, boolean bln, boolean bln1) {
        super(string, thrwbl, bln, bln1);
    }
    
}
