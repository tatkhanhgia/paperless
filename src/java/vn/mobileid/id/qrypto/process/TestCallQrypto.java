/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.paperless.QryptoService;
import vn.mobileid.id.qrypto.object.Configuration;
import vn.mobileid.id.qrypto.object.QRSchema;
import vn.mobileid.id.qrypto.object.QRSchema.data;
import vn.mobileid.id.qrypto.object.QRSchema.field;
import vn.mobileid.id.qrypto.object.QRSchema.format;
import vn.mobileid.id.qrypto.object.qryptoEffectiveDate;
import vn.mobileid.id.qrypto.response.IssueQryptoWithFileAttachResponse;

/**
 *
 * @author GIATK
 */
public class TestCallQrypto {
    public static void main(String[] args)throws Exception, Throwable {
        Configuration config = new Configuration();
        config.setContextIdentifier("QC1:");
        config.setIsTransparent(false);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        qryptoEffectiveDate date = new qryptoEffectiveDate(
                dateFormat.format(new Date(System.currentTimeMillis())),
                "2023-12-25 15:04:00");
        config.setQryptoEffectiveDate(date);
        config.setQryptoDimension(1080);
                
        QRSchema QR = new QRSchema();
        QR.setScheme("QC1");
        
        List<data> datas = new ArrayList<>();
        data a = new data();
        a.setName("fd19c4db-859c-45a5-b0be-1c9e96ea53a7");
        a.setValue("Tất Khánh Gia");
        datas.add(a);
        
        QR.setData(datas);
        
        format format = new format();
        format.setVersion("2");
        
        List<QRSchema.field> fields = new ArrayList<>();
            field field = new field();
            field.setName("Tên");
            field.setType(QRSchema.fieldType.t2);
            field.setKvalue("fd19c4db-859c-45a5-b0be-1c9e96ea53a7");
            fields.add(field);
        
            field field2 = new field();
            field2.setName("File1");
            field2.setType(QRSchema.fieldType.f1);
            field2.setFile_type("application/pdf");
            field2.setFile_field("a6fbf534-7487-4863-9ffc-94810c4038bf");
            field2.setFile_name("BA_eKYC_TechnicalRequirement.pdf");
            field2.setShare_mode(2);
            field2.setQr_meta_data(new QRSchema.QR_META_DATA()
                    .setIsTransparent(false)
                    .setxCoordinator(50)
                    .setyCoordinator(50)
                    .setQrDimension(100)
                    .setPageNumber(Arrays.asList(1)));
            fields.add(field2);
        format.setFields(fields);
            
        QR.setFormat(format);
        QR.setTitle("Demo Qrypto");
        QR.setCi("");
        
        HashMap<String,byte[]> headers = new HashMap<>();
        headers.put("a6fbf534-7487-4863-9ffc-94810c4038bf", Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\unsign.pdf")));
        QR.setHeader(headers);
        
        IssueQryptoWithFileAttachResponse QRdata = QryptoService
                .getInstance(1)
                .generateQR(QR, config, "tran");
        
        System.out.println("Base64:"+QRdata.getQryptoBase64());
        FileOutputStream fos = new FileOutputStream("C:\\Users\\Admin\\Downloads\\IMAGE.png");
        fos.write(Base64.getDecoder().decode(QRdata.getQryptoBase64())); 
        fos.close();
    }
}
