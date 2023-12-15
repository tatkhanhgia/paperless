/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.exception;

/**
 *
 * @author GiaTK
 */
public class CannotFindDataSignatureException extends Exception{

    public CannotFindDataSignatureException() {
    }

    public CannotFindDataSignatureException(String string) {
        super(string);
    }

    public CannotFindDataSignatureException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public CannotFindDataSignatureException(Throwable thrwbl) {
        super(thrwbl);
    }

    public CannotFindDataSignatureException(String string, Throwable thrwbl, boolean bln, boolean bln1) {
        super(string, thrwbl, bln, bln1);
    }
    
}
