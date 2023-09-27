/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.QryptoService;
import vn.mobileid.id.paperless.kernel.GetQRSize;
import vn.mobileid.id.paperless.kernel.GetTransaction;
import vn.mobileid.id.paperless.kernel.GetWorkflowDetail_option;
import vn.mobileid.id.paperless.kernel.UpdateQR;
import vn.mobileid.id.paperless.objects.FileDataDetails;
import vn.mobileid.id.paperless.objects.ItemDetails;
import vn.mobileid.id.paperless.objects.QRSize;
import vn.mobileid.id.paperless.objects.ProcessWorkflowActivity_JSNObject;
import vn.mobileid.id.paperless.objects.Transaction;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;
import vn.mobileid.id.qrypto.object.Configuration;
import vn.mobileid.id.qrypto.object.QRSchema;
import vn.mobileid.id.qrypto.object.QRSchema.fieldType;
import vn.mobileid.id.qrypto.object.qryptoEffectiveDate;
import vn.mobileid.id.qrypto.response.IssueQryptoWithFileAttachResponse;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ProcessSecureQRTemplate {

    public static InternalResponse process(
            WorkflowActivity woAc,
            List<FileDataDetails> fileData,
            List<ItemDetails> fileItem,
            User user,
            String file_name,
            String transactionID
    ) throws IOException, Exception {
        //Get Workflow Detail 
        InternalResponse response = GetWorkflowDetail_option.getWorkflowDetail(
                woAc.getWorkflow_id(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        //Get detail and write to Object QR
        Configuration configure = null;
        QRSchema QR = null;
        try {
            configure = appendWorkflowDetail_into_Configure((WorkflowDetail_Option) response.getData(), transactionID);
            QR = appendData_into_QRScheme(fileData, fileItem, transactionID);
            
            if (configure == null || QR == null) {
                return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                        "Configure hoac QR null"
                );
            }
            LogHandler.info(ProcessSecureQRTemplate.class,"\tConfigure to call QRypto:"+new ObjectMapper().writeValueAsString(configure));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            IssueQryptoWithFileAttachResponse QRdata = null;
            try {
                QRdata = QryptoService.getInstance(1).generateQR(QR, configure, transactionID);
            } catch (IOException ex) {
                return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                        "{\"message\":\"Error while calling to QRYPTO Service\",\"description_vn\":\"Lỗi do Bearer Token hết hạn hoặc bị lỗi token\"}"
                );
            } catch(Exception e){
                return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                        "{\"message\":\"Error while calling to QRYPTO Service\",\"description_vn\":\"Lỗi không xác định!\"}"
                );
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

            //Update QR
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
                    "{\"message\":\"Error while calling to Qrypto\"}"
            );
        } catch (Throwable ex) {
            ex.printStackTrace();
            return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                    "{\"message\":\"Error while calling to Qrypto\"}"
            );
        }
    }

//=====================INTERNAL METHOD =======================    
    private static Configuration appendWorkflowDetail_into_Configure(
            WorkflowDetail_Option detail,
            String transactionID) throws Exception {
        Configuration config = new Configuration();
        config.setContextIdentifier("QC1:");
        config.setIsTransparent(detail.getQr_background().equals("Transparent"));

        qryptoEffectiveDate effectiveDate = new qryptoEffectiveDate();
        Calendar now = Calendar.getInstance();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now.getTime());
        now.add(Calendar.YEAR, 1);
        String timeStamp2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now.getTime());
        effectiveDate.setNotValidBefore(timeStamp2);
        effectiveDate.setNotValidAfter(timeStamp);

        config.setQryptoEffectiveDate(effectiveDate);

        InternalResponse call = GetQRSize.getQRSize(String.valueOf(detail.getQr_size()), transactionID);

        int size = 0;
        if (call.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            LogHandler.fatal(
                    ProcessSecureQRTemplate.class,
                    transactionID,
                    call.getMessage());
            size = 1080;
        } else {
            size = ((QRSize) call.getData()).getSize();
        }
        config.setQryptoHeight(size);
        config.setQryptoWidth(size);

        return config;
    }

    private static QRSchema appendData_into_QRScheme(
            List<FileDataDetails> fileData,
            List<ItemDetails> items,
            String transactionID
    ) throws Exception {
        QRSchema QR = new QRSchema();
        QRSchema.format format = new QRSchema.format();
        QR.setScheme("QC1");
        format.setVersion("2");

        List<QRSchema.data> listData = new ArrayList<>();
        List<QRSchema.field> listField = new ArrayList<>();
        HashMap<String, byte[]> headers = new HashMap<>();

        for (ItemDetails item : items) {
            QRSchema.data data = new QRSchema.data();
            QRSchema.field field = new QRSchema.field();

            switch (item.getType()) {
                case 1: { //Type String _ text
                    String random = Utils.generateRandomString(6);
                    data.setName(random);
                    data.setValue((String) item.getValue());
                    field.setName(item.getField());
                    field.setKvalue(random);
                    field.setType(fieldType.t2);
                    break;
                }
                case 2: { //Type Boolean
                    String random = Utils.generateRandomString(6);
                    data.setName(random);
                    data.setValue(((Boolean) item.getValue()) == true ? "true" : "false");
                    field.setName(item.getField());
                    field.setKvalue(random);
                    field.setType(fieldType.t2);
                    break;
                }
                case 3: { //Type INT
                    String random = Utils.generateRandomString(6);
                    data.setName(random);
                    data.setValue(String.valueOf((Integer) item.getValue()));
                    field.setName(item.getField());
                    field.setKvalue(random);
                    field.setType(fieldType.t2);
                    break;
                }
                case 4: { //Type DATE
                    String random = Utils.generateRandomString(6);
                    data.setName(random);
                    data.setValue((String) item.getValue());
                    field.setName(item.getField());
                    field.setKvalue(random);
                    field.setType(fieldType.t2);
                    break;
                }
                case 5: { //Type custom                                        
                    field.setName(item.getFile_field());
                    field.setType(fieldType.f1);
                    field.setFile_type(item.getFile_format());
                    field.setField_field(item.getFile_field());
                    if (item.getValue() instanceof String) {
                        headers.put(item.getFile_field(), Base64.getDecoder().decode((String) item.getValue()));
                    } else {
                        headers.put(item.getFile_field(), (byte[]) item.getValue());
                    }
                    break;
                }
                default: {
                    throw new Exception("Invalid type of items");
                }
            }
            listData.add(data);
            listField.add(field);
        }
        format.setFields(listField);
        QR.setData(listData);
        QR.setFormat(format);
        QR.setTitle("Paperless Service");
        QR.setCi("");
        QR.setHeader(headers);
        return QR;
    }

    public static void main(String[] args) {

    }
}