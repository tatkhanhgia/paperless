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
import vn.mobileid.id.paperless.objects.FileManagement.FileType;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.FrameSignatureProperties;
import vn.mobileid.id.paperless.objects.WorkflowActivity;

/**
 *
 * @author GiaTK
 */
public class CreateFileManagement {

    /**
     * Check Data in workflow activity is valid to create File Management
     *
     * @param workflow - WorkflowActivity
     * @return true - false
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
     * Create new File Management
     *
     * @param workflow - WorkflowActivity
     * @param UUID - UUID
     * @param nameFile - name of File
     * @param HMAC - HMAC
     * @param fileData - Binary/ data of file
     * @param user - User
     * @param transactionID
     * @return ID of FileManagement
     * @throws Exception
     */
    public static InternalResponse processingCreateFileManagement(
            WorkflowActivity workflow,
            String UUID,
            String nameFile,
            String HMAC,
            byte[] fileData,
            User user,
            FileType file_type,
            String transactionID
    ) throws Exception {
        return processingCreateFileManagement(
                workflow,
                UUID,
                nameFile,
                0,
                0,
                0,
                0,
                HMAC,
                fileData,
                user,
                "DBMS",
                file_type,
                "",
                "",
                transactionID);
    }

    /**
     * Create new File Management
     *
     * @param workflow - WorkflowActivity
     * @param HMAC
     * @param fileData - Binary/ data of file
     * @param user - User
     * @param transactionID
     * @return ID of FileManagement
     * @throws Exception
     */
    public static InternalResponse processingCreateFileManagement(
            WorkflowActivity workflow,
            String HMAC,
            byte[] fileData,
            User user,
            FileType file_type,
            String transactionID) throws Exception {
        return processingCreateFileManagement(
                workflow,
                "UUID",
                null,
                0,
                0,
                0,
                0,
                HMAC,
                fileData,
                user,
                "DBMS",
                file_type,
                "",
                "",
                transactionID);
    }

    /**
     * Create new File Management
     *
     * @param workflow - WorkflowActivity
     * @param page - int
     * @param size - int
     * @param width - int
     * @param height - int
     * @param HMAC
     * @param fileData - Binary/data of file
     * @param user - User
     * @param transactionID
     * @return ID of FileManagement
     * @throws Exception
     */
    public static InternalResponse processingCreateFileManagement(
            WorkflowActivity workflow,
            int page,
            int size,
            int width,
            int height,
            String HMAC,
            byte[] fileData,
            User user,
            FileType file_type,
            String signing_properties,
            String hash_values,
            String transactionID) throws Exception {
        return processingCreateFileManagement(
                workflow,
                "UUID",
                null,
                page,
                size,
                width,
                height,
                HMAC,
                fileData,
                user,
                "DBMS",
                file_type,
                signing_properties,
                hash_values,
                transactionID);
    }

    /**
     * Create new File Management
     *
     * @param workflow - WorkflowActivity
     * @param nameFile - String / Name of file
     * @param page - int
     * @param size - int
     * @param width - int
     * @param height - int
     * @param HMAC
     * @param fileData - Binary / Data of file
     * @param user - User
     * @param transactionID
     * @return ID of FileManagement
     * @throws Exception
     */
    public static InternalResponse processingCreateFileManagement(
            WorkflowActivity workflow,
            String nameFile,
            int page,
            int size,
            int width,
            int height,
            String HMAC,
            byte[] fileData,
            User user,
            FileType file_type,
            String signing_properties,
            String hash_values,
            String transactionID) throws Exception {
        return processingCreateFileManagement(
                workflow,
                "UUID",
                nameFile,
                page,
                size,
                width,
                height,
                HMAC,
                fileData,
                user,
                "DBMS",
                file_type,
                signing_properties,
                hash_values,
                transactionID);
    }

    /**
     * Create a new FileManagement
     *
     * @param workflow
     * @param UUID
     * @param nameFile
     * @param page
     * @param size
     * @param width
     * @param height
     * @param HMAC
     * @param fileData
     * @param user
     * @param DBMS
     * @param transactionID
     * @return ID of FileManagement
     * @throws Exception
     */
    private static InternalResponse processingCreateFileManagement(
            WorkflowActivity workflow,
            String UUID,
            String nameFile,
            int page,
            int size,
            int width,
            int height,
            String HMAC,
            byte[] fileData,
            User user,
            String DBMS,
            FileType file_type,
            String signing_properties,
            String hash_values,
            String transactionID) throws Exception {

        Database DB = new DatabaseImpl();

        //Create new File Management
        DatabaseResponse callDB = DB.createFileManagement(
                UUID, //UUID
                nameFile, //name
                page, //page
                size, //size
                width, //width
                height, //height
                fileData, //file data
                HMAC, //HMAC
                user.getName(),
                DBMS,
                file_type.getName(),
                signing_properties,
                hash_values,
                transactionID);

        try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                message = PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
//                    LogHandler.error(CreateFileManagement.class, "TransactionID:" + transactionID + "\nCannot create File Management - Detail:" + message);                
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    callDB.getIDResponse());
        } catch (Exception e) {
            throw new Exception("Cannot create User_Activity_log!", e);
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
//        String pa = "D:\\NetBean\\QryptoServices\\file\\rssp.p12";
//        byte[] data = Files.readAllBytes(new File(pa).toPath());
//
////        CreateFileManagement.processingCreateFileManagement(
////                object,
////                "HMAC",
////                data, user);
//        CreateFileManagement.processingCreateFileManagement(
//                object,
//                "RSSP",
//                0,
//                0,
//                0,
//                0,
//                "HMAC",
//                data,
//                user);
//
    }
}
