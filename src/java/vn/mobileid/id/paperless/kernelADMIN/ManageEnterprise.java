/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernelADMIN;

import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;

/**
 *
 * @author GiaTK
 */
public class ManageEnterprise {
        
    public static InternalResponse getDataRP(
            int enterprise_id,
            String transactionID
    ) throws Exception{
      Database callDB = new DatabaseImpl();
      DatabaseResponse res = callDB.getDataRP(enterprise_id, transactionID);
      return null;
    }
    
    public static InternalResponse updateDataRP(
            int enterprise_id,
            String data,
            byte[] filep12,
            String transactionID
    ){
        return null;
    }
}
