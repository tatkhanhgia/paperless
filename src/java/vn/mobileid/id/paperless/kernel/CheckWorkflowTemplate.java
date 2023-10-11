
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.FileDataDetails;
import vn.mobileid.id.paperless.objects.Item_JSNObject;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.ItemDetails;
import vn.mobileid.id.paperless.objects.ProcessWorkflowActivity_JSNObject;

/**
 *
 * @author GiaTK
 */
public class CheckWorkflowTemplate {

    /**
     * Check Item of workflow template
     *
     * @param workflow : Item of that workflowTemplate
     * @return no Object =>check status
     */
    public static InternalResponse checkDataWorkflowTemplate(
            Item_JSNObject workflow) {
        for (ItemDetails detail : workflow.getItems()) {
            InternalResponse response = checkDataWorkflowTemplate(detail);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_SUCCESS,
                        PaperlessConstant.SUBCODE_SUCCESS,
                        "en",
                        null));
    }

    /**
     * Check Detail of an Item in workflow template
     *
     * @param workflow - ItemDetails of An Item
     * @return no object => check status
     */
    public static InternalResponse checkDataWorkflowTemplate(
            ItemDetails workflow) {
        if (workflow == null) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            "en",
                            null));
        }
        if (workflow.getField() == null || workflow.getField().isEmpty() || workflow.getField().length() <= 0) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            PaperlessConstant.SUBCODE_MISSING_INPUT_FIELD,
                            "en",
                            null));
        }
        if (workflow.getType() <= 0 || workflow.getType() > PaperlessConstant.NUMBER_OF_ITEMS_TYPE) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            PaperlessConstant.SUBCODE_MISSING_OR_ERROR_FIELD_TYPE,
                            "en",
                            null));
        }
        if (workflow.getValue() == null) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOW,
                            PaperlessConstant.SUBCODE_MISSING_OR_ERROR_VALUE,
                            "en",
                            null));
        }
        if (workflow.getType() == 5) {
            if (workflow.getFile_field() == null || workflow.getFile_field().isEmpty()) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                                PaperlessConstant.SUBCODE_MISSING_FILE_FIELD_IN_ITEMS,
                                "en",
                                null));
            }
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_SUCCESS,
                        PaperlessConstant.SUBCODE_SUCCESS,
                        "en",
                        null));

    }

    /**
     * Check Item of a ProcessWorkflowActivity Object
     *
     * @param request - ProcessWorkflowActivity Object
     * @return no object => check status
     */
    public static InternalResponse checkItem(
            ProcessWorkflowActivity_JSNObject request) {
        List<ItemDetails> listItem = request.getItem();
        InternalResponse result;

        //Check item details
        for (ItemDetails obj : listItem) {
            result = CheckWorkflowTemplate.checkDataWorkflowTemplate(obj);
            if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return result;
            }
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_SUCCESS,
                        PaperlessConstant.SUBCODE_SUCCESS,
                        "en",
                        null));
    }

    /**
     * Check FileData of a ProcessWorkflowActivity Object
     *
     * @param request - ProcessWorkflowActivity Object
     * @return no object => check status
     */
    public static InternalResponse checkFile_Data(
            ProcessWorkflowActivity_JSNObject request) {
        List<FileDataDetails> listFile = request.getFile_data();
        InternalResponse result;

        //Check file details
        if(listFile == null){
            return new InternalResponse(
                PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                        PaperlessConstant.SUBCODE_MISSING_VALUE_OF_FILE_DATA,
                        "en",
                        null));
        }
        for (FileDataDetails obj : listFile) {
            result = checkFile_type(obj);
            if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return result;
            }
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_SUCCESS,
                        PaperlessConstant.SUBCODE_SUCCESS,
                        "en",
                        null));
    }

    /**
     * Check Item and FileData of a ProcessWorkflowActivity Object
     *
     * @param request - ProcessWorkflowActivity Object
     * @return no object => check status
     */
    public static InternalResponse checkDataWorkflowTemplate(
            ProcessWorkflowActivity_JSNObject request) {
        List<FileDataDetails> listFile = request.getFile_data();
        List<ItemDetails> listItem = request.getItem();
        InternalResponse result;

        //Check file details
        if (listFile != null) {
            for (FileDataDetails obj : listFile) {
                result = checkFile_type(obj);
                if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return result;
                }
            }
        }

        if (listItem != null) {
            for (ItemDetails obj : listItem) {
                result = CheckWorkflowTemplate.checkDataWorkflowTemplate(obj);
                if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return result;
                }
            }
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_SUCCESS,
                        PaperlessConstant.SUBCODE_SUCCESS,
                        "en",
                        null));
    }

    /**
     * Create a new WorkflowTemplate
     *
     * @param workflow_id - Workflow ID
     * @param workflow - Item of that Workflow
     * @param user_mail - Email of user
     * @param transactionID
     * @return no Object => CheckStatus
     * @throws Exception
     */
    public static InternalResponse processingCreateWorkflowTemplate(
            int workflow_id,
            Item_JSNObject workflow,
            String user_mail,
            String HMAC,
            String transactionID) throws Exception {

        Database DB = new DatabaseImpl();
        DatabaseResponse createWorkflow = DB.createWorkflowTemplate(
                workflow_id,
                new ObjectMapper().writeValueAsString(workflow),
                HMAC,
                user_mail,
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
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }

    //==========INTERNAL FUNCTION=================================
    private static InternalResponse checkFile_type(
            FileDataDetails filedata) {
        if (filedata.getFile_type() <= 0
                || filedata.getFile_type() > PaperlessConstant.NUMBER_OF_FILE_DATA) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_MISSING_OR_ERROR_FILE_TYPE,
                            "en",
                            null));
        }
        if (filedata.getValue() == null) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_MISSING_OR_ERROR_FILE_TYPE,
                            "en",
                            null));
        }
        if (filedata.getFile_type() == 5
                && (filedata.getFile_field() == null
                || filedata.getFile_field().isEmpty())) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_MISSING_FILE_FIELD_IN_FILE_DATA,
                            "en",
                            null));
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                PaperlessConstant.CODE_SUCCESS);
    }
}
