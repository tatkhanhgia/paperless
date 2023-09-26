/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import com.fasterxml.jackson.databind.ObjectMapper;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.KYC;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.QR;
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
     *
     * @param workflowActivityID - ID of ther Workflow Activity
     * @param transactionID
     * @return FileManagement
     * @throws Exception
     */
    public static InternalResponse getDocument(
            int workflowActivityID,
            String transactionID) throws Exception {
        InternalResponse response = null;

        long start = System.currentTimeMillis();
        response = GetWorkflowActivity.getWorkflowActivity(
                workflowActivityID,
                transactionID);
        System.out.println("\n\tTime get Workflow Activity:" + (System.currentTimeMillis() - start));
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

        //Each type have an other way to get binary
        FileManagement files = null;
        switch (woAc.getWorkflow_template_type()) {
            case 7: {
            }
            case 8: {
                start = System.currentTimeMillis();
                response = GetFileManagement.getFileManagement(Integer.parseInt(woAc.getFile().getID()), transactionID);
                System.out.println("\n\tTime get FileManagement:" + (System.currentTimeMillis() - start));
                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return response;
                }
                files = (FileManagement) response.getData();

                if (files.getData() == null) {
                    return new InternalResponse(
                            PaperlessConstant.HTTP_CODE_FORBIDDEN,
                            PaperlessMessageResponse.getErrorMessage(
                                    PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                                    PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_PROCESS_YET,
                                    "en",
                                    null)
                    );
                }
                if (files.isIsSigned() == false) {
                    try {
                        start = System.currentTimeMillis();
                        if (woAc.getRequestData() == null) {
                            woAc = (WorkflowActivity) GetWorkflowActivity.getWorkflowActivityFromDB(woAc.getId(), transactionID).getData();
                        }
                        System.out.println("\n\tTime get RequestData in DB:" + (System.currentTimeMillis() - start));
                        KYC object = new ObjectMapper().readValue(
                                woAc.getRequestData(),
                                KYC.class);
                        start = System.currentTimeMillis();
                        byte[] xsltC = files.getData();
                        byte[] html = XSLT_PDF_Processing.appendData(object, xsltC);
                        System.out.println("\n\tTime convert RequestData to XML-HTML:" + (System.currentTimeMillis() - start));
                        //Convert from HTML to PDF
                        start = System.currentTimeMillis();
                        byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);
                        System.out.println("\n\tTime convert HTML to PDF:" + (System.currentTimeMillis() - start));
                        files.setData(pdf);
                    } catch (Exception ex) {
                    }
                }
                break;
            }
            case 2: {
                //Get Transaction first
                start = System.currentTimeMillis();
                response = GetTransaction.getTransaction(woAc.getTransaction(), transactionID);
                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return response;
                }
                System.out.println("\n\tTime get Transaction in DB:"+(System.currentTimeMillis()-start));

                //Get QR with ID in transaction
                start = System.currentTimeMillis();
                response = GetQR.getQR(
                        ((Transaction) response.getData()).getObject_id(),
                        transactionID);
                System.out.println("\n\tTime get QR Image in DB:"+(System.currentTimeMillis()-start));
                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return response;
                }
                files.setData(((QR) response.getData()).getImage());
                break;
            }
            default: {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                        "{\"Message\":\"This template type does not have a way to download document\"}");
            }
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                files);
    }
}
