/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import com.fasterxml.jackson.databind.ObjectMapper;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.KYC;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.utils.XSLT_PDF_Processing;

/**
 *
 * @author GiaTK
 */
public class GetDocument {

    public static InternalResponse getDocument(
            int workflowActivityID,
            String transactionID) throws Exception {
//        try {
        Database DB = new DatabaseImpl();
        InternalResponse response = null;

        response = GetWorkflowActivity.getWorkflowActivity(
                workflowActivityID,
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_EXISTED,
                            "en",
                            null));
        }
        WorkflowActivity woAc = (WorkflowActivity) response.getData();

        response = GetFileManagement.getFileManagement(Integer.parseInt(woAc.getFile().getID()), transactionID);

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        FileManagement file = (FileManagement) response.getData();

        if (file.getData() == null) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_PROCESS_YET,
                            "en",
                            null)
            );
        }
        if (file.isIsSigned() == false) {
            if(woAc.getRequestData() == null ){
                woAc = (WorkflowActivity)GetWorkflowActivity.getWorkflowActivityFromDB(woAc.getId(), transactionID).getData();
            }
            KYC object = new ObjectMapper().readValue(woAc.getRequestData(), KYC.class);
            byte[] xsltC = file.getData();
            byte[] html = XSLT_PDF_Processing.appendData(object, xsltC);

            //Convert from HTML to PDF
            byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);
            file.setData(pdf);
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                file);

//        } catch (Exception e) {
//            if (LogHandler.isShowErrorLog()) {
////                e.printStackTrace();
//                LogHandler.error(GetDocument.class,"UNKNOWN EXCEPTION. Details: " + e);
//            }
//            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
//        }
    }
}
