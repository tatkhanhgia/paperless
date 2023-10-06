/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.Transaction;

/**
 *
 * @author GiaTK
 */
public class GetTransaction {

    /**
     * Get Transaction with ID of that Transaction     
     * @return
     */
    public static InternalResponse getTransaction(
            String id,
            String transactionID
    ) throws Exception {

        Database callDB = new DatabaseImpl();
        DatabaseResponse responseDB = callDB.getTransaction(
                id,
                transactionID);

        if (responseDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            String message = PaperlessMessageResponse.getErrorMessage(
                    PaperlessConstant.CODE_FAIL,
                    responseDB.getStatus(),
                    "en",
                    null);         
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    message
            );
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                responseDB.getObject());
    }
    
    public static void main(String[] args) throws Exception{
        InternalResponse response = GetTransaction.getTransaction("680-662", "");
        Transaction transaction = (Transaction)response.getData();
        String temp = "";
    }
}
