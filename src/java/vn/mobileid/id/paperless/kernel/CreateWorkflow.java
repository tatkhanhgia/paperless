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
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Item_JSNObject;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.Workflow;
import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;
import vn.mobileid.id.paperless.objects.WorkflowTemplateType;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class CreateWorkflow {

//    final private static Logger LOG = LogManager.getLogger(CreateWorkflow.class);
    public static InternalResponse checkDataWorkflow(Workflow workflow) {
        if (workflow == null) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            "en",
                            null));
        }
        if (Utils.isNullOrEmpty(workflow.getLabel())) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            PaperlessConstant.SUBCODE_MISSING_WORKFLOW_LABEL,
                            "en",
                            null));
        }
//        if (Utils.isNullOrEmpty(workflow.getCreated_by())) {
//            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
//                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOW,
//                            PaperlessConstant.SUBCODE_MISSING_WORKFLOW_CREATED_BY,
//                            "en",
//                            null));
//        }
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_SUCCESS,
                        PaperlessConstant.SUBCODE_SUCCESS,
                        "en",
                        null));
    }

    public static InternalResponse processingCreateWorkflow(
            Workflow workflow,
            User user,
            String transactionID
    ) throws Exception {

        Database DB = new DatabaseImpl();

        DatabaseResponse createWorkflow = DB.createWorkflow(
                workflow.getWorkflowTemplate_type(),
                workflow.getLabel(),
                user.getName(),
                user.getEmail(),
                user.getAid(),
                transactionID
        );

        if (createWorkflow.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            createWorkflow.getStatus(),
                            "en",
                            null)
            );
        }

        //Create Workflow Detail - This is default Data
////            WorkflowDetail_Option detail = new WorkflowDetail_Option();
////            detail.setQr_background("WHITE");
////            detail.setQr_size(4);
////            detail.setQr_type(1);
////            detail.setUrl_code(false);
////            String temp = "a:1;b:2;c:3";
////            detail.setMetadata(temp);
//////          detail.setPage(1);fication(false);
//////          detail.setStamp_in(1);
//////          detail.setPage_size("A4");
//////          detail.setAsset_Background(0);
////            detail.setAsset_Template(7);
//////          detail.setAsset_Append(0);
////            detail.setDisable_CSV_task_notification_email(false);
////            detail.setCSV_email(false);
////            detail.setOmit_if_empty(false);
////            detail.setEmail_notification(false);
////            String hmac = "HMAC";
//
//            InternalResponse response = CreateWorkflowDetail_option.createWorkflowDetail(
//                    createWorkflow.getIDResponse_int(),
//                    detail,
//                    hmac,
//                    user.getEmail());
        //Get Default data detail - template of template Type 
        try {
            InternalResponse res = GetWorkflowTemplateType.getWorkflowTemplateTypeFromDB(
                    workflow.getWorkflowTemplate_type(),
                    transactionID);
            WorkflowTemplateType template = (WorkflowTemplateType) res.getData();
            Item_JSNObject object = new ObjectMapper().readValue(template.getMetadata_template(), Item_JSNObject.class);
            CheckWorkflowTemplate.processingCreateWorkflowTemplate(createWorkflow.getIDResponse_int(), object, user.getEmail(), transactionID);

            WorkflowDetail_Option object2 = new ObjectMapper().readValue(template.getMetadata_detail(), WorkflowDetail_Option.class);
            CreateWorkflowDetail_option.createWorkflowDetail(createWorkflow.getIDResponse_int(), object2, "HMAC", user.getEmail(), transactionID);

            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                    "{\"workflow_id\":" + createWorkflow.getIDResponse_int() + "}"
            );
        } catch (Exception e) {                                        
            throw new Exception("Cannot create Workflow!", e);
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        String a = "{\"QR_size\":2,\"QR_type\":1,\"page\":1,\"stamp_in\":1,\"page_size\":\"A4\",\"x_cordinate\":1,\"y_cordinate\":1,\"show_domain\":true,\"text_below_qr\":\"https://www.mobile-id.vn\",\"qr_placement\":true}";
        WorkflowDetail_Option object2 = new ObjectMapper().readValue(a, WorkflowDetail_Option.class);
        System.out.println(object2.getText_below_QR());
        System.out.println(object2.getQr_size());
    }
}
