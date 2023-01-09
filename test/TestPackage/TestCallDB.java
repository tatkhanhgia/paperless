/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

import java.sql.Connection;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseConnectionManager;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;

/**
 *
 * @author Admin
 */
public class TestCallDB {

    public static void main(String[] args) {
        Database DB = new DatabaseImpl();
        DatabaseResponse callDB = DB.createWorkflowActivity(
                3, //enterpriseID
                8, //workflowID
                "bcd@gmail.com",    //useremail
                "23--1", //transactionid
                1,  //file link
                QryptoConstant.FLAG_FALSE_DB,  //csv
                "remakr not", //remark
                QryptoConstant.FLAG_FALSE_DB,  //use test token
                QryptoConstant.FLAG_FALSE_DB,  //is production
                QryptoConstant.FLAG_FALSE_DB,  //is update
                QryptoConstant.WORKFLOW_TYPE_PDF_GENERATOR,  //workflow type
                "request data",  //request data
                "hmac",   //hmac
                "GIATK");  //created by
            if(callDB.getStatus() != QryptoConstant.CODE_SUCCESS ){              
                InternalResponse a =  new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        QryptoMessageResponse.getErrorMessage(
                                QryptoConstant.CODE_FAIL,
                                callDB.getStatus(),
                                "en"
                                , null)
                );
                System.out.println("Mes:"+a.getMessage());
            }
            System.out.println("ID:"+callDB.getIDResponse());
            System.out.println("ID:"+callDB.getTransactionID());
    }
}
