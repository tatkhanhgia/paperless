/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author GiaTK
 */
public class TestCatchException {

    public static void main(String[] args) {
        try {
            int q = TestThrowException.a();
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.toString();
            Throwable a = ExceptionUtils.getRootCause(e);
            for (StackTraceElement stackTraceElement : a.getStackTrace()) {
                message = message + System.lineSeparator() + "\t" + stackTraceElement.toString();
            }
            System.out.println("Mes:" + message);
        }
    }
}
