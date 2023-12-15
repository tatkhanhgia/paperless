/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import vn.mobileid.id.general.database.DatabaseImpl_V2_Workflow;
import vn.mobileid.id.general.database.DatabaseV2_Workflow;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.kernel.CheckWorkflowTemplate;
import vn.mobileid.id.paperless.kernel.GetWorkflowTemplateType;
import vn.mobileid.id.paperless.object.enumration.WorkflowAttributeTypeName;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.Item_JSNObject;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.Workflow;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;
import vn.mobileid.id.paperless.objects.WorkflowTemplate;
import vn.mobileid.id.paperless.objects.WorkflowTemplateType;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class CreateWorkflow {

    //<editor-fold defaultstate="collapsed" desc="Create new Workflow">
    /**
     * Create new Workflow
     *
     * @param workflow
     * @param user
     * @param transactionID
     * @return long WorkflowId
     * @throws Exception
     */
    public static InternalResponse processingCreateWorkflow(
            Workflow workflow,
            User user,
            String transactionID
    ) throws Exception {
        DatabaseV2_Workflow callDb = new DatabaseImpl_V2_Workflow();

        DatabaseResponse response = callDb.createWorkflow(
                user.getEmail(),
                user.getAid(),
                workflow.getWorkflowTemplate_type(),
                workflow.getLabel(),
                "hmac",
                user.getEmail(),
                transactionID);

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            null)
            );
        }

        Long workflowId = (long) response.getObject();

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
                workflowId.intValue(),
                object,
                user.getEmail(),
                "HMAC",
                transactionID);

        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        List<WorkflowAttributeType> object2 = WorkflowAttributeType.castTo(template.getMetadata_detail());

        List<WorkflowAttributeType> object3 = template.convertToWorkflowAttributeType(object2);

        res = CreateWorkflowDetails.createWorkflowDetail(
                workflowId.intValue(),
                object3,
                "hmac",
                user.getEmail(),
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        //Update UsedBy in Asset
        for (WorkflowAttributeType attribute : object2) {
            //Asset Template
            if (attribute.getId() == WorkflowAttributeTypeName.ASSET_TEMPLATE.getId() && attribute.getValue() != null) {
                InternalResponse temp = GetAsset.getAsset((int) attribute.getValue(), transactionID);
                if (temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    String message = temp.getMessage();
                    message.replace("{", "{\"error\":\"CANNOT_UPDATE_COLUMN_USED_BY_OF_ASSET\"");
                    temp.setMessage(message);
                    return temp;
                }
                Asset asset = (Asset) temp.getData();
                String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
                used_by += workflowId.intValue() + ",";
//            asset.setUsed_by(used_by);
                UpdateAsset.updateAsset(
                        asset.getId(),
                        user.getEmail(),
                        null,
                        -1,
                        -1,
                        used_by,
                        null,
                        null,
                        null,
                        null,
                        null,
                        user.getEmail(),
                        transactionID);
            }
            //Asset Background
            if (attribute.getId() == WorkflowAttributeTypeName.ASSET_BACKGROUND.getId() && attribute.getValue() != null) {
                InternalResponse temp = GetAsset.getAsset((int) attribute.getValue(), transactionID);
                if (temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    String message = temp.getMessage();
                    message.replace("{", "{\"error\":\"CANNOT_UPDATE_COLUMN_USED_BY_OF_ASSET\"");
                    temp.setMessage(message);
                    return temp;
                }
                Asset asset = (Asset) temp.getData();
                String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
                used_by += workflowId.intValue() + ",";
//            asset.setUsed_by(used_by);
                UpdateAsset.updateAsset(
                        asset.getId(),
                        user.getEmail(),
                        null,
                        -1,
                        -1,
                        used_by,
                        null,
                        null,
                        null,
                        null,
                        null,
                        user.getEmail(),
                        transactionID);
            }
            //Asset Append
            if (attribute.getId() == WorkflowAttributeTypeName.ASSET_APPEND.getId() && attribute.getValue() != null) {
                InternalResponse temp = GetAsset.getAsset((int) attribute.getValue(), transactionID);
                if (temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return temp;
                }
                Asset asset = (Asset) temp.getData();
                String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
                used_by += workflowId.intValue() + ",";
                asset.setUsed_by(used_by);
                UpdateAsset.updateAsset(
                        asset.getId(),
                        user.getEmail(),
                        null,
                        -1,
                        -1,
                        used_by,
                        null,
                        null,
                        null,
                        null,
                        null,
                        user.getEmail(),
                        transactionID);
            }
            //Asset ELabor
            if (attribute.getId() == WorkflowAttributeTypeName.ASSET_ELABOR.getId() && attribute.getValue() != null) {
                InternalResponse temp = GetAsset.getAsset((int) attribute.getValue(), transactionID);
                if (temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    String message = temp.getMessage();
                    message.replace("{", "{\"error\":\"CANNOT_UPDATE_COLUMN_USED_BY_OF_ASSET\"");
                    temp.setMessage(message);
                    return temp;
                }
                Asset asset = (Asset) temp.getData();
                String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
                used_by += workflowId.intValue() + ",";
//            asset.setUsed_by(used_by);
                UpdateAsset.updateAsset(
                        asset.getId(),
                        user.getEmail(),
                        null,
                        -1,
                        -1,
                        used_by,
                        null,
                        null,
                        null,
                        null,
                        null,
                        user.getEmail(),
                        transactionID);
            }
            //Asset ELabor
            if (attribute.getId() == WorkflowAttributeTypeName.ASSET_ELABOR.getId() && attribute.getValue() != null) {
                InternalResponse temp = GetAsset.getAsset((int) attribute.getValue(), transactionID);
                if (temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    String message = temp.getMessage();
                    message.replace("{", "{\"error\":\"CANNOT_UPDATE_COLUMN_USED_BY_OF_ASSET\"");
                    temp.setMessage(message);
                    return temp;
                }
                Asset asset = (Asset) temp.getData();
                String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
                used_by += workflowId.intValue() + ",";
//            asset.setUsed_by(used_by);
                UpdateAsset.updateAsset(
                        asset.getId(),
                        user.getEmail(),
                        null,
                        -1,
                        -1,
                        used_by,
                        null,
                        null,
                        null,
                        null,
                        null,
                        user.getEmail(),
                        transactionID);
            }
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "{\"workflow_id\":" + workflowId + "}"
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check data of the workflow">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create new Workflow based on data of workflow parent">
    /**
     * Create new Workflow
     *
     * @param workflowId_parent
     * @param workflow
     * @param user
     * @param transactionID
     * @return long WorkflowId
     * @throws Exception
     */
    public static InternalResponse processingCreateWorkflowBasedOnWorkflowID(
            int workflowId_parent,
            Workflow workflow,
            User user,
            String transactionID
    ) throws Exception {
        DatabaseV2_Workflow callDb = new DatabaseImpl_V2_Workflow();

        DatabaseResponse response = callDb.createWorkflow(
                user.getEmail(),
                user.getAid(),
                workflow.getWorkflowTemplate_type(),
                workflow.getLabel(),
                "hmac",
                user.getEmail(),
                transactionID);

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            null)
            );
        }

        Long workflowId = (long) response.getObject();

        //Get Worklflow Template of Workflow Parent        
        InternalResponse res = GetWorkflowTemplate.getWorkflowTemplate(workflowId_parent, transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        WorkflowTemplate template = (WorkflowTemplate) res.getData();
        Item_JSNObject object = new ObjectMapper().readValue(template.getMeta_data_template(), Item_JSNObject.class);
        res = CheckWorkflowTemplate.processingCreateWorkflowTemplate(
                workflowId.intValue(),
                object,
                user.getEmail(),
                "HMAC",
                transactionID);

        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        res = GetWorkflowDetails.getWorkflowDetail(workflowId_parent, transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        List<WorkflowAttributeType> object2 = (List<WorkflowAttributeType>) res.getData();

        res = CreateWorkflowDetails.createWorkflowDetail(
                workflowId.intValue(),
                object2,
                "hmac",
                user.getEmail(),
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        //Update UsedBy in Asset
        for (WorkflowAttributeType attribute : object2) {
            //Asset Template
            if (attribute.getId() == WorkflowAttributeTypeName.ASSET_TEMPLATE.getId() && attribute.getValue() != null) {
                InternalResponse temp = GetAsset.getAsset(Integer.parseInt((String) attribute.getValue()), transactionID);
                if (temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    String message = temp.getMessage();
                    message.replace("{", "{\"error\":\"CANNOT_UPDATE_COLUMN_USED_BY_OF_ASSET\"");
                    temp.setMessage(message);
                    return temp;
                }
                Asset asset = (Asset) temp.getData();
                String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
                used_by += workflowId.intValue() + ",";
//            asset.setUsed_by(used_by);
                UpdateAsset.updateAsset(
                        asset.getId(),
                        user.getEmail(),
                        null,
                        -1,
                        -1,
                        used_by,
                        null,
                        null,
                        null,
                        null,
                        null,
                        user.getEmail(),
                        transactionID);
            }
            //Asset Background
            if (attribute.getId() == WorkflowAttributeTypeName.ASSET_BACKGROUND.getId() && attribute.getValue() != null) {
                InternalResponse temp = GetAsset.getAsset(Integer.parseInt((String) attribute.getValue()), transactionID);
                if (temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    String message = temp.getMessage();
                    message.replace("{", "{\"error\":\"CANNOT_UPDATE_COLUMN_USED_BY_OF_ASSET\"");
                    temp.setMessage(message);
                    return temp;
                }
                Asset asset = (Asset) temp.getData();
                String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
                used_by += workflowId.intValue() + ",";
//            asset.setUsed_by(used_by);
                UpdateAsset.updateAsset(
                        asset.getId(),
                        user.getEmail(),
                        null,
                        -1,
                        -1,
                        used_by,
                        null,
                        null,
                        null,
                        null,
                        null,
                        user.getEmail(),
                        transactionID);
            }
            //Asset Append
            if (attribute.getId() == WorkflowAttributeTypeName.ASSET_APPEND.getId() && attribute.getValue() != null) {
                InternalResponse temp = GetAsset.getAsset(Integer.parseInt((String) attribute.getValue()), transactionID);
                if (temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return temp;
                }
                Asset asset = (Asset) temp.getData();
                String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
                used_by += workflowId.intValue() + ",";
                asset.setUsed_by(used_by);
                UpdateAsset.updateAsset(
                        asset.getId(),
                        user.getEmail(),
                        null,
                        -1,
                        -1,
                        used_by,
                        null,
                        null,
                        null,
                        null,
                        null,
                        user.getEmail(),
                        transactionID);
            }
            //Asset ELabor
            if (attribute.getId() == WorkflowAttributeTypeName.ASSET_ELABOR.getId() && attribute.getValue() != null) {
                InternalResponse temp = GetAsset.getAsset(Integer.parseInt((String) attribute.getValue()), transactionID);
                if (temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    String message = temp.getMessage();
                    message.replace("{", "{\"error\":\"CANNOT_UPDATE_COLUMN_USED_BY_OF_ASSET\"");
                    temp.setMessage(message);
                    return temp;
                }
                Asset asset = (Asset) temp.getData();
                String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
                used_by += workflowId.intValue() + ",";
//            asset.setUsed_by(used_by);
                UpdateAsset.updateAsset(
                        asset.getId(),
                        user.getEmail(),
                        null,
                        -1,
                        -1,
                        used_by,
                        null,
                        null,
                        null,
                        null,
                        null,
                        user.getEmail(),
                        transactionID);
            }
            //Asset ESign
            if (attribute.getId() == WorkflowAttributeTypeName.ASSET_ESIGN.getId() && attribute.getValue() != null) {
                InternalResponse temp = GetAsset.getAsset(Integer.parseInt((String) attribute.getValue()), transactionID);
                if (temp.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    String message = temp.getMessage();
                    message.replace("{", "{\"error\":\"CANNOT_UPDATE_COLUMN_USED_BY_OF_ASSET\"");
                    temp.setMessage(message);
                    return temp;
                }
                Asset asset = (Asset) temp.getData();
                String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
                used_by += workflowId.intValue() + ",";
//            asset.setUsed_by(used_by);
                UpdateAsset.updateAsset(
                        asset.getId(),
                        user.getEmail(),
                        null,
                        -1,
                        -1,
                        used_by,
                        null,
                        null,
                        null,
                        null,
                        null,
                        user.getEmail(),
                        transactionID);
            }
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "{\"workflow_id\":" + workflowId + "}"
        );
    }
    //</editor-fold>

    public static void main(String[] args) throws Exception {

    }
}
