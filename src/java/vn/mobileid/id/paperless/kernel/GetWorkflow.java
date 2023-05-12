/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import java.util.List;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.Workflow;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class GetWorkflow {

    public static InternalResponse getWorkflow(
            int id,
            String transactionID) throws Exception {

        Database DB = new DatabaseImpl();
        InternalResponse response = null;

        DatabaseResponse callDB = DB.getWorkflow(id, transactionID);

        try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                if (LogHandler.isShowErrorLog()) {
                    message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            callDB.getStatus(),
                            "en",
                            null);
                    LogHandler.error(GetWorkflow.class,
                            "TransactionID:" + transactionID
                            + "\nCannot get Workflow - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            Workflow workflow = (Workflow) callDB.getObject();
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    workflow);

        } catch (Exception e) {
            throw new Exception("Cannot get Workflow!", e);
//            return new InternalResponse(500,PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse getListWorkflow(
            String email,
            int enterprise_id,
            String status,
            String type,
            boolean use_metadata,
            String metadata,
            int offset,
            int rowcount,
            String transactionID
    ) throws Exception {

        Database DB = new DatabaseImpl();
        InternalResponse response = null;

        DatabaseResponse callDB = DB.getListWorkflow(
                email,
                enterprise_id,
                status,
                type,
                use_metadata,
                metadata,
                offset,
                rowcount,
                transactionID);

        try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
                if (LogHandler.isShowErrorLog()) {
                    LogHandler.error(GetWorkflow.class, "TransactionID:" + transactionID + "\nCannot get list Workflow - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            List<Workflow> object = (List<Workflow>) callDB.getObject();

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    object);

        } catch (Exception e) {
            throw new Exception("Cannot get list of workflow!", e);
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static void main(String[] args) throws Exception {
        List<Workflow> lis = (List<Workflow>) GetWorkflow.getListWorkflow(
                "khanhpx@mobile-id.vn",
                3,
                "1,2",
                "1,2,3,4,5,6,7,8",
                false,
                "",
                0,
                10,
                "transactionID").getData();
        for (Workflow a : lis) {
            System.out.println(a.getWorkflow_id());
        }

//        Workflow a = (Workflow)GetWorkflow.getWorkflow(30).getData();
//        System.out.println("ID:"+a.getWorkflow_id());
//        System.out.println("Template:"+a.getTemplate_type_name());
//
//        System.out.println("Workflow type:"+a.getWorkflow_type_name());
    }
}
