/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Asset;
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
@Deprecated
public class CreateWorkflow {

    /**
     * Check Data in workflow is valid to create a Workflow
     *
     * @param workflow - Workflow
     * @return no Object => Check Status
     */
    public static InternalResponse checkDataWorkflow(Workflow workflow) {
        if (workflow == null) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            "en",
                            null));
        }
        if (Utils.isNullOrEmpty(workflow.getLabel())) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOW,
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
                PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_SUCCESS,
                        PaperlessConstant.SUBCODE_SUCCESS,
                        "en",
                        null));
    }

    @Deprecated
    /**
     * Create a new Workflow
     *
     * @param workflow - Workflow
     * @param user - User
     * @param transactionID
     * @return String / ID of that Workflow
     * @throws Exception
     */
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
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            createWorkflow.getStatus(),
                            "en",
                            null)
            );
        }

        //Get Default data detail - template of template Type         
        InternalResponse res = GetWorkflowTemplateType.getWorkflowTemplateTypeFromDB(
                workflow.getWorkflowTemplate_type(),
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        WorkflowTemplateType template = (WorkflowTemplateType) res.getData();
        Item_JSNObject object = new ObjectMapper().readValue(template.getMetadata_template(), Item_JSNObject.class);
        res = CheckWorkflowTemplate.processingCreateWorkflowTemplate(
                createWorkflow.getIDResponse_int(),
                object,
                user.getEmail(),
                "HMAC",
                transactionID);

        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        WorkflowDetail_Option object2 = new ObjectMapper().readValue(template.getMetadata_detail(), WorkflowDetail_Option.class);
        res = CreateWorkflowDetail_option.createWorkflowDetail(
                createWorkflow.getIDResponse_int(),
                object2,
                "HMAC",
                user.getEmail(),
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        //Update UsedBy in Asset
        if (object2.getAsset_Append() > 0) {
            InternalResponse temp = GetAsset.getAsset(object2.getAsset_Append(), transactionID);
            if(temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
                return temp;
            }
            Asset asset = (Asset)temp.getData();
            String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
            used_by += createWorkflow.getIDResponse_int() + ",";
            asset.setUsed_by(used_by);
            UpdateAsset.updateAsset(asset, user, transactionID);
        }
        if (object2.getAsset_Background() > 0) {
            InternalResponse temp = GetAsset.getAsset(object2.getAsset_Background(), transactionID);
            if(temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
                return temp;
            }
            Asset asset = (Asset)temp.getData();
            String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
            used_by += createWorkflow.getIDResponse_int() + ",";
            asset.setUsed_by(used_by);
            UpdateAsset.updateAsset(asset, user, transactionID);
        }
        if (object2.getAsset_Template() > 0) {
            InternalResponse temp = GetAsset.getAsset(object2.getAsset_Template(), transactionID);
            if(temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
                return temp;
            }
            Asset asset = (Asset)temp.getData();
            String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
            used_by += createWorkflow.getIDResponse_int() + ",";
            asset.setUsed_by(used_by);
            UpdateAsset.updateAsset(asset, user, transactionID);
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "{\"workflow_id\":" + createWorkflow.getIDResponse_int() + "}"
        );
    }

    public static void main(String[] args) throws JsonProcessingException {
        String a = "{\"QR_size\":2,\"QR_type\":1,\"page\":1,\"stamp_in\":1,\"page_size\":\"A4\",\"x_cordinate\":1,\"y_cordinate\":1,\"show_domain\":true,\"text_below_qr\":\"https://www.mobile-id.vn\",\"qr_placement\":true}";
        WorkflowDetail_Option object2 = new ObjectMapper().readValue(a, WorkflowDetail_Option.class);
        System.out.println(object2.getText_below_QR());
        System.out.println(object2.getQr_size());
    }
}
