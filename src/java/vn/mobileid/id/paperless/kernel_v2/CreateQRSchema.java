/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.kernel.GetQRSize;
import vn.mobileid.id.paperless.kernel.process.ProcessSecureQRTemplate;
import vn.mobileid.id.paperless.object.enumration.WorkflowAttributeTypeName;
import vn.mobileid.id.paperless.objects.FileDataDetails;
import vn.mobileid.id.paperless.objects.ItemDetails;
import vn.mobileid.id.paperless.objects.QRSize;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;
import vn.mobileid.id.qrypto.object.Configuration;
import vn.mobileid.id.qrypto.object.QRSchema;
import vn.mobileid.id.qrypto.object.qryptoEffectiveDate;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class CreateQRSchema {

    //<editor-fold defaultstate="collapsed" desc="Create QR Schema">
    public static QRSchema createQRSchema(
            List<FileDataDetails> fileData,
            List<ItemDetails> items,
            QRSchema.QR_META_DATA positionQR,
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
                    field.setType(QRSchema.fieldType.t2);
                    listData.add(data);
                    listField.add(field);
                    break;
                }
                case 2: { //Type Boolean
                    String random = Utils.generateRandomString(6);
                    data.setName(random);
                    data.setValue(((Boolean) item.getValue()) == true ? "true" : "false");
                    field.setName(item.getField());
                    field.setKvalue(random);
                    field.setType(QRSchema.fieldType.t2);
                    listData.add(data);
                    listField.add(field);
                    break;
                }
                case 3: { //Type INT
                    String random = Utils.generateRandomString(6);
                    data.setName(random);
                    data.setValue(String.valueOf((Integer) item.getValue()));
                    field.setName(item.getField());
                    field.setKvalue(random);
                    field.setType(QRSchema.fieldType.t2);
                    listData.add(data);
                    listField.add(field);
                    break;
                }
                case 4: { //Type DATE
                    String random = Utils.generateRandomString(6);
                    data.setName(random);
                    data.setValue((String) item.getValue());
                    field.setName(item.getField());
                    field.setKvalue(random);
                    field.setType(QRSchema.fieldType.t2);
                    listData.add(data);
                    listField.add(field);
                    break;
                }
                case 5: { //Type Binary                                        
                    field.setType(QRSchema.fieldType.f1);
                    field.setName(item.getField());
                    field.setFile_type(item.getFile_format());
                    field.setFile_field(item.getFile_field());
                    field.setFile_name(item.getFile_name());
                    field.setShare_mode(3);
                    for (FileDataDetails file : fileData) {
                        if (item.getFile_field().equals(file.getFile_field())) {
                            if (file.getValue() instanceof String) {
                                headers.put(item.getFile_field(), Base64.getDecoder().decode((String) file.getValue()));
                            } else {
                                headers.put(item.getFile_field(), (byte[]) file.getValue());
                            }
                        }
                    }
                    if (positionQR != null) {
                        field.setQr_meta_data(positionQR);
                    }
                    listField.add(field);
                    break;
                }
                default: {
                    throw new Exception("Invalid type of items");
                }
            }
        }
        format.setFields(listField);
        QR.setData(listData);
        QR.setFormat(format);
        QR.setTitle("GoPaperless Service");
        QR.setCi("");
        QR.setHeader(headers);
        return QR;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create Config">
    public static Configuration createConfiguration(
            List<WorkflowAttributeType> details,
            User user,
            String transactionID) throws Exception {
        Configuration config = new Configuration();
        config.setContextIdentifier("QC1:");
        for (WorkflowAttributeType temp : details) {
            if (temp.getId() == WorkflowAttributeTypeName.QR_BACKGROUND.getId()) //QR Background
            {
                try {
                    System.out.println("ProcessSecureQRTemplate: QR Background:" + ((String) temp.getValue()));
                    if (((String) temp.getValue()).equalsIgnoreCase("transparent")) {
                        config.setIsTransparent(true);
                    } else {
                        config.setIsTransparent(false);
                    }
                } catch (Exception ex) {
                    config.setIsTransparent(true);
                }
            }
        }

        qryptoEffectiveDate effectiveDate = new qryptoEffectiveDate();
        Calendar now = Calendar.getInstance();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now.getTime());
        if (user.getQr_expired_time() > 0) {
            now.add(Calendar.DATE, ((Long) user.getQr_expired_time()).intValue());
        } else {
            now.add(Calendar.DATE, ((Long) PolicyConfiguration.getInstant().getQR_ExpiredTime().getQr_expired_time()).intValue());
        }
        String timeStamp2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now.getTime());
        effectiveDate.setNotValidBefore(timeStamp);
        effectiveDate.setNotValidAfter(timeStamp2);

        config.setQryptoEffectiveDate(effectiveDate);

        WorkflowAttributeType qrSize = new WorkflowAttributeType();
        for (WorkflowAttributeType temp : details) {
            if (temp.getId() == WorkflowAttributeTypeName.QR_SIZE.getId()) //QR Size
            {
                qrSize = temp;
                break;
            }
        }
        InternalResponse call = GetQRSize.getQRSize(String.valueOf(qrSize.getValue()), transactionID);

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
        config.setQryptoDimension(size);

        return config;
    }
    //</editor-fold>
}
