/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class GetFileManagement {

    public static InternalResponse getFileManagement(int id,
            String transactionID) throws Exception {

        Database DB = new DatabaseImpl();
        //Data                        
        InternalResponse response = null;

        DatabaseResponse callDB = DB.getFileManagement(id, transactionID);

        try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(GetFileManagement.class,
                            "TransactionID:" + transactionID
                            + "\nCannot get File Management - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            FileManagement file = (FileManagement) callDB.getObject();

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    file);

        } catch (Exception e) {
            throw new Exception("Cannot get file management!", e);
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static void main(String[] args) {
//        InternalResponse res = GetFileManagement.getFileManagement(27
//        );
//        FileManagement a = (FileManagement) res.getData();
//        System.out.println("A:"+a.getData());
    }
}
