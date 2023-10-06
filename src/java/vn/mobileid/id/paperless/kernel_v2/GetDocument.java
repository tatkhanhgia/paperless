/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.kernel.GetTransaction;
import vn.mobileid.id.paperless.object.enumration.ObjectType;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.KYC;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.Transaction;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.utils.XSLT_PDF_Processing;

/**
 *
 * @author GiaTK
 */
public class GetDocument {
    /**
     * Get Document of the WorkflowActivity
     * @param workflowActivityID - ID of the Workflow Activity
     * @param transactionID
     * @return FileManagement
     * @throws Exception 
     */
    public static InternalResponse getDocument(
            int workflowActivityID,
            String transactionID) throws Exception {
//        try {        
        InternalResponse response = null;

        response = GetWorkflowActivity.getWorkflowActivity(
                workflowActivityID,
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_EXISTED,
                            "en",
                            null));
        }
        WorkflowActivity woAc = (WorkflowActivity) response.getData();

        //Get Transaction
        response = GetTransaction.getTransaction(woAc.getTransaction(), transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        
        //From info of transaction. Get fileManagement or QR
        Transaction transaction = (Transaction)response.getData();
        if(!transaction.getObject_type().equals(ObjectType.PDF)){
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"Message\":\"The type of Workflow Activity cannot access this API\"}"
            );
        } else {
            response = GetFileManagement.getFileManagement(transaction.getObject_id(), transactionID);
        }

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        FileManagement file = (FileManagement) response.getData();
        
        if (file.getData() == null) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_PROCESS_YET,
                            "en",
                            null)
            );
        }
        if (file.isIsSigned() == false) {
            if(woAc.getRequestData() == null ){
                woAc = (WorkflowActivity)GetWorkflowActivity.getWorkflowActivity(woAc.getId(), transactionID).getData();
            }
            KYC object = new ObjectMapper().readValue(
                    woAc.getRequestData(),
                    KYC.class);
            byte[] xsltC = file.getData();
            byte[] html = XSLT_PDF_Processing.appendData(object, xsltC);

            //Convert from HTML to PDF
            byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);
            file.setData(pdf);
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                file);
    }
}
