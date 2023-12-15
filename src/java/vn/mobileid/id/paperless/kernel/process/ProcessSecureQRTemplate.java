/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel.process;

import vn.mobileid.id.paperless.kernel_v2.CreateQRSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.QryptoService;
import vn.mobileid.id.paperless.kernel.GetQRSize;
import vn.mobileid.id.paperless.kernel.GetTransaction;
import vn.mobileid.id.paperless.kernel_v2.GetWorkflowDetails;
import vn.mobileid.id.paperless.kernel_v2.UpdateCSVTask;
import vn.mobileid.id.paperless.kernel_v2.UpdateQR;
import vn.mobileid.id.paperless.objects.FileDataDetails;
import vn.mobileid.id.paperless.objects.ItemDetails;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.ProcessWorkflowActivity_CSV_JSNObject.Buffer;
import vn.mobileid.id.paperless.objects.QRSize;
import vn.mobileid.id.paperless.objects.ProcessWorkflowActivity_JSNObject;
import vn.mobileid.id.paperless.objects.Transaction;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;
import vn.mobileid.id.qrypto.object.Configuration;
import vn.mobileid.id.paperless.exception.LoginException;
import vn.mobileid.id.qrypto.object.QRSchema;
import vn.mobileid.id.qrypto.object.QRSchema.fieldType;
import vn.mobileid.id.paperless.exception.QryptoException;
import vn.mobileid.id.paperless.kernel_v2.CreateWorkflowActivity;
import vn.mobileid.id.paperless.object.enumration.WorkflowAttributeTypeName;
import vn.mobileid.id.qrypto.object.qryptoEffectiveDate;
import vn.mobileid.id.qrypto.response.IssueQryptoWithFileAttachResponse;
import vn.mobileid.id.utils.Utils;
import vn.mobileid.id.utils.ZipProcessing;

/**
 *
 * @author GiaTK
 */
public class ProcessSecureQRTemplate {

    //<editor-fold defaultstate="collapsed" desc="Process QR Template">
    public static InternalResponse process(
            WorkflowActivity woAc,
            List<FileDataDetails> fileData,
            List<ItemDetails> fileItem,
            User user,
            String file_name,
            String transactionID
    ) throws IOException, Exception {
        //Get Workflow Detail 
        InternalResponse response = GetWorkflowDetails.getWorkflowDetail(
                woAc.getWorkflow_id(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        //Get detail and write to Object QR
        Configuration configure = null;
        QRSchema QR = null;
        try {
            configure = CreateQRSchema.createConfiguration((List<WorkflowAttributeType>) response.getData(), user,transactionID);
            QR = CreateQRSchema.createQRSchema(fileData, fileItem, null,transactionID);

            if (configure == null || QR == null) {
                return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                        new PaperlessMessageResponse().sendErrorMessage("ERROR WHILE PREPARING DATA TO CALL QRYPTO").build()
                );
            }

            IssueQryptoWithFileAttachResponse QRdata = null;
            try {
                QRdata = QryptoService.getInstance(1).generateQR(QR, configure, transactionID);
            } catch (LoginException loginEx) {
                QryptoService.getInstance(1).login();
                QRdata = QryptoService.getInstance(1).generateQR(QR, configure, transactionID);
            }
            //Get Transaction =>get QR Id
            InternalResponse res = GetTransaction.getTransaction(woAc.getTransaction(), transactionID);
            if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return res;
            }
            //update QR
            ProcessWorkflowActivity_JSNObject test = new ProcessWorkflowActivity_JSNObject();
            test.setFile_data(fileData);
            test.setItem(fileItem);

            response = UpdateQR.updateQR(
                    ((Transaction) res.getData()).getObject_id(),
                    new ObjectMapper().writeValueAsString(test),
                    QRdata.getQryptoBase64(),
                    user.getEmail(),
                    transactionID);

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    new ObjectMapper().writeValueAsString(QRdata)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                    new PaperlessMessageResponse().sendErrorMessage("ERROR WHILE CALLING TO QRYPTO").build()
            );
        } catch (Throwable ex) {
            ex.printStackTrace();
            return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                    new PaperlessMessageResponse().sendErrorMessage("ERROR WHILE CALLING TO QRYPTO").build()
            );
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Process QR Template without UpdateQR">
    public static InternalResponse process_(
            WorkflowActivity woAc,
            List<FileDataDetails> fileData,
            List<ItemDetails> fileItem,
            User user,
            String transactionID
    ) throws IOException, Exception {
        //Get Workflow Detail 
        InternalResponse response = GetWorkflowDetails.getWorkflowDetail(
                woAc.getWorkflow_id(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        //Get detail and write to Object QR
        Configuration configure;
        QRSchema QR;
        try {
            configure = CreateQRSchema.createConfiguration((List<WorkflowAttributeType>) response.getData(), user,transactionID);
            QR = CreateQRSchema.createQRSchema(fileData, fileItem, null,transactionID);

            if (configure == null || QR == null) {
                return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                        "Configure hoac QR null"
                );
            }

            IssueQryptoWithFileAttachResponse QRdata = null;
            try {
                QRdata = QryptoService.getInstance(1).generateQR(QR, configure, transactionID);
            } catch (LoginException loginEx) {
                QryptoService.getInstance(1).login();
                QRdata = QryptoService.getInstance(1).generateQR(QR, configure, transactionID);
            }

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    new ObjectMapper().writeValueAsString(QRdata)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_500,
                    "{Lỗi trong lúc thực hiện call Qrypto}"
            );
        } catch (Throwable ex) {
            ex.printStackTrace();
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_500,
                    "{Lỗi trong lúc thực hiện call Qrypto}"
            );
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Process CSV Task">
    public static InternalResponse processCSV(
            WorkflowActivity woAc,
            List<Buffer> listItem,
            User user,
            String file_name,
            String transactionID
    ) throws IOException, Exception {
        List<String> names = new ArrayList<>();
        List<byte[]> qrImages = new ArrayList<>();

        //<editor-fold defaultstate="collapsed" desc="Get Workflow Detail">
        InternalResponse response = GetWorkflowDetails.getWorkflowDetail(
                woAc.getWorkflow_id(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        //</editor-fold>

        List<WorkflowAttributeType> temp = (List<WorkflowAttributeType>) response.getData();
        for (Buffer object : listItem) {
            //<editor-fold defaultstate="collapsed" desc="Create QR">
            Configuration configure = null;
            QRSchema QR = null;
            String imageQR = "";
            try {
                configure = CreateQRSchema.createConfiguration(temp, user, transactionID);
                QR = CreateQRSchema.createQRSchema(null, object.getItemDetails(), null,transactionID);

                if (configure == null || QR == null) {
                    return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                            "Configure hoac QR null"
                    );
                }

                IssueQryptoWithFileAttachResponse QRdata = null;
                try {
                    QRdata = QryptoService.getInstance(1).generateQR(QR, configure, transactionID);
                } catch (LoginException loginEx) {
                    QryptoService.getInstance(1).login();
                    QRdata = QryptoService.getInstance(1).generateQR(QR, configure, transactionID);
                }

                imageQR = QRdata.getQryptoBase64();
                qrImages.add(Base64.getDecoder().decode(imageQR));

                //<editor-fold defaultstate="collapsed" desc="Create Workflow Activity child">
                InternalResponse response2 = CreateWorkflowActivity.processingCreateWorkflowActivity_typeQR(
                        woAc,
                        woAc.getCsv().getId(),
                        imageQR,
                        user,
                        transactionID);
                if (response2.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return response2;
                }
                //</editor-fold>
                
                String name = (String)response2.getData();
                names.add(name+".png");
            } catch (QryptoException e) {
                return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                        new PaperlessMessageResponse()
                                .sendErrorMessage("Error while processing in Qrypto Service")
                                .sendErrorDescriptionMessage(e.getMessage())
                                .build()
                );
            } catch (Exception e) {
                e.printStackTrace();
                return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                        new PaperlessMessageResponse()
                                .sendErrorMessage("Error while processing in Qrypto Service")
                                .build()
                );
            } catch (Throwable ex) {
                ex.printStackTrace();
                return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                        new PaperlessMessageResponse()
                                .sendErrorMessage("Error while processing in Qrypto Service")
                                .build()
                );
            }
            //</editor-fold>
        }

        //Zip file
        byte[] zip = ZipProcessing.zipFile(names, qrImages);

        //Update CSVTask
        return UpdateCSVTask.updateCSV(
                woAc.getCsv().getId(),
                null,
                null,
                0,
                zip.length,
                null,
                null,
                user.getName() == null ? user.getEmail() : user.getName(),
                zip,
                transactionID);
    }
//</editor-fold>
    //=====================INTERNAL METHOD =======================    
}
