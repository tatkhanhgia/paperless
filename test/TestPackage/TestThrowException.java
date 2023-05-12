/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

/**
 *
 * @author GiaTK
 */
public class TestThrowException {
    public static int a() throws Exception{
        try{
            String b = "ád";
            return Integer.parseInt(b);
        } catch (Exception e){
            throw new Exception("Fail ABC", e);
        }
    }
}
