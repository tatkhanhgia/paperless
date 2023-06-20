/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import java.io.IOException;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowActivity;

/**
 *
 * @author GiaTK
 */
public class CreateTransaction {

    /**
     * Check Data in workflow activity is valid to create a Transaction
     * @param workflow - WorkflowActivity
     * @return boolean
     */
    public static boolean checkData(WorkflowActivity workflow) {
        if (workflow == null) {
            return false;
        }
        if (workflow.getEnterprise_id() <= 0 && workflow.getEnterprise_name() == null) {
            return false;
        }
        if (workflow.getCreated_by() == null) {
            return false;
        }
        return true;
    }

    /**
     * Processing Create a new Transaction
     * @param logID - LogID
     * @param ObjectID - ObjectID
     * @param ObjectType - ObjectType
     * @param user - User
     * @param createdby
     * @param transactionID
     * @return
     * @throws Exception 
     */
    public static InternalResponse processingCreateTransaction(
            int logID,
            int ObjectID,
            int ObjectType,
            User user,
            String createdby,
            String transactionID
    ) throws Exception {
        return processingCreateTransaction(
                logID,
                ObjectID,
                ObjectType,
                null,
                null,
                0,
                0,
                0,
                0,
                0,
                "New Transaction",
                "HMAC",
                user,
                createdby,
                transactionID);
    }

    /**
     * Processing Create a new Transaction
     * @param logID
     * @param ObjectID
     * @param ObjectType
     * @param IPAddress
     * @param initFile
     * @param pY
     * @param pX
     * @param pC
     * @param pS
     * @param pages
     * @param description
     * @param HMAC
     * @param user
     * @param created_by
     * @param transactionID
     * @return
     * @throws Exception 
     */
    private static InternalResponse processingCreateTransaction(
            int logID,
            int ObjectID,
            int ObjectType,
            String IPAddress,
            String initFile,
            int pY,
            int pX,
            int pC,
            int pS,
            int pages,
            String description,
            String HMAC,
            User user,
            String created_by,
            String transactionID) throws Exception {

        Database DB = new DatabaseImpl();

        //Create new Transaction
        DatabaseResponse callDB = DB.createTransaction(
                user.getEmail(), //email
                logID, //logID
                ObjectID, //Object ID
                ObjectType, //Object Type
                IPAddress, //IPAddress
                initFile, //initFile
                pY, //pY
                pX, //pX
                pC, //pC
                pS, //pS
                pages, //pages
                description, //des   
                HMAC, //hmac
                created_by,
                transactionID);

        try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
//                    LogHandler.error(CreateTransaction.class, "TransactionID:" + transactionID + "\nCannot create Transaction - Detail:" + message);                
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                    callDB.getIDResponse());
        } catch (Exception e) {
            throw new Exception("Cannot create Transaction!", e);
//            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
//                    e.getMessage()
//            );
        }
    }

    public static void main(String[] args) throws IOException {
//        WorkflowActivity object = new WorkflowActivity();
//        object.setEnterprise_id(3);
//        object.setCreated_by("GIATK");
//        User user = new User();
//        user.setEmail("giatk@mobile-id.vn");
//
//        String pa = "D:\\NetBeansProjects\\Restfult-SDK-Java_newest\\file\\gic.p12";
//        byte[] data = Files.readAllBytes(new File(pa).toPath());
//
//        CreateTransaction.processingCreateTransaction(
//                26,
//                6,
//                1,
//                user,
//                object.getCreated_by());
    }
}
