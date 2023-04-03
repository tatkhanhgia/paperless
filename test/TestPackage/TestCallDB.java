/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import org.apache.commons.dbcp2.BasicDataSource;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.DataSourceReadOnly;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseConnectionManager;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.general.objects.ResponseCode;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author Admin
 */
public class TestCallDB {

    public static void main(String[] args) {
    
        long startTime = System.nanoTime();
        
        ResultSet rs = null;
        CallableStatement cals = null;
        ResponseCode responseCode = null;
        try {
            String str = "{ call TEST(?,?) }";
            Connection conn = null;
            try {
                BasicDataSource bds = DataSourceReadOnly.getInstance().getBds();
                conn = bds.getConnection();
            } catch (Exception e) {
                
                e.printStackTrace();
            }
            cals = conn.prepareCall(str);
            cals.setString("P", "'METADATA' OR 1=1");
           
            cals.execute();
            rs = cals.getResultSet();
            
            if (rs != null) {
                System.out.println(rs.getString("WORKFLOW_ID"));
            }
        } catch (Exception e) {
           
            e.printStackTrace();
        } finally {
//            DatabaseConnectionManager.getInstance().close(conn);
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        
        
    
    }
}
