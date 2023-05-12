/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class CreateWorkflowDetail_option {

    public static InternalResponse createWorkflowDetail(
            int id,
            WorkflowDetail_Option detail,
            String hmac,
            String created_by,
            String transactionID) throws Exception {
        
            InternalResponse response = null;
            Database DB = new DatabaseImpl();

            DatabaseResponse callDB = DB.createWorkflowDetail(id,
                    detail.getHashMap(),
                    hmac,
                    created_by,
                    transactionID);

            try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                message = PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            callDB.getStatus(),
                            "en",
                            null);
                if (LogHandler.isShowErrorLog()) {                    
                    LogHandler.error(CreateWorkflowDetail_option.class,
                            "TransactionID:"+transactionID+
                            "\nCannot create Workflow Detail - Detail:" + message);
                }
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }                        

            return new InternalResponse(
                    PaperlessConstant.CODE_SUCCESS,
                    "");

        } catch (Exception e) {            
            throw new Exception("Cannot Create workflow detail!", e);
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        int id = 12;
        WorkflowDetail_Option detail = new WorkflowDetail_Option();
        detail.setQr_background("WHITE");
        detail.setQr_size(2);
//        detail.setQr_type(1);
        detail.setUrl_code(false);
//        detail.setPage(1);
//        detail.setStamp_in(1);
//        detail.setPage_size("A4");
//        detail.setAsset_Background(0);
//        detail.setAsset_Template(7);
//        detail.setAsset_Append(0);
//        detail.setX_cordinate(1);
//        detail.setY_cordinate(1);
        detail.setDisable_CSV_task_notification_email(false);
        detail.setCSV_email(false);
        detail.setOmit_if_empty(false);
//        detail.setEmail_notification(false);
//        detail.setQR_placement(true);
//        detail.setShow_domain(true);
//        detail.setText_below_QR("https://www.mobile-id.vn");
        
        String hmac = "HMAC";
        String created_by = "GIATK";
        
        System.out.println(new ObjectMapper().writeValueAsString(detail));
//        InternalResponse response = CreateWorkflowDetail_option.createWorkflowDetail(
//                id,
//                detail,
//                hmac,
//                created_by);
//        System.out.println("Response:"+response.getStatus());
//        System.out.println("Mes:"+response.getMessage());
    }
}
