/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

import java.sql.Connection;
import vn.mobileid.id.general.database.DatabaseConnectionManager;

/**
 *
 * @author Admin
 */
public class TestCallDB {
    public static void main(String[] args){
        //Init info
        Connection conn = null;
        conn = DatabaseConnectionManager.getInstance().openReadOnlyConnection();
        
    }
}
