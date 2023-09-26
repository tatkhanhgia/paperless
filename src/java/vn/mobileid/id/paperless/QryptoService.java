/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.qrypto.object.Property;
import vn.mobileid.id.qrypto.object.QRSchema;
import vn.mobileid.id.qrypto.object.qryptoEffectiveDate;
import vn.mobileid.id.qrypto.process.ProcessClaimRequest;
import vn.mobileid.id.qrypto.process.QryptoSession;
import vn.mobileid.id.qrypto.process.SessionFactory;
import vn.mobileid.id.qrypto.request.ClaimRequest;
import vn.mobileid.id.qrypto.response.ClaimResponse;
import vn.mobileid.id.qrypto.response.IssueQryptoWithFileAttachResponse;
import vn.mobileid.id.utils.Configuration;

/**
 *
 * @author GiaTK
 */
public class QryptoService {

    private int instanceId;

    private Property prop;

    private SessionFactory sessionFactory;

    private static HashMap<Integer, QryptoService> listSession = new HashMap<>();
    private static HashMap<Integer, QryptoSession> listQryptoSession = new HashMap<>();

    public static QryptoService getInstance(int i) throws Throwable {
        if (i <= 0) {
            return null;
        }
        if (QryptoService.listSession.containsKey(i)) {
            return QryptoService.listSession.get(i);
        } else {
            QryptoService qryptoService = new QryptoService(i);
            listSession.put(i, qryptoService);
            return qryptoService;
        }
    }

    private QryptoService(int i) throws Throwable {
        instanceId = i;
        prop = new Property(Configuration.getInstance().getQryptoHost(),
                Configuration.getInstance().getQryptoAuthentication());
        if (!QryptoService.listQryptoSession.containsKey(i)) {
            this.sessionFactory = SessionFactory.getInstance(prop);
            QryptoSession session = this.sessionFactory.getSession();
            session.login();
            QryptoService.listQryptoSession.put(i, session);
        }
    }

    private void renewToken(int i) throws Exception {
        try {
            QryptoService.listQryptoSession.get(i).login();
        } catch (Exception ex) {
            QryptoService.listQryptoSession.get(i).refreshToken = null;
            try {
                QryptoService.listQryptoSession.get(i).login();
            } catch (Exception e) {
                throw new IOException();
            }
        }
    }

    public IssueQryptoWithFileAttachResponse generateQR(
            QRSchema QR,
            vn.mobileid.id.qrypto.object.Configuration configuration,
            String transaction
    ) throws Exception {
            IssueQryptoWithFileAttachResponse response = QryptoService.listQryptoSession.get(this.instanceId).issueQryptoWithFileAttach(QR, configuration);
            if (response == null) {                
                renewToken(this.instanceId);               
                response = QryptoService.listQryptoSession.get(this.instanceId).issueQryptoWithFileAttach(QR, configuration);
            }
//            ClaimRequest datas = ProcessClaimRequest.generateClaimRequest(response);
//            ClaimResponse tan =  this.session.dgci_wallet_claim(datas);
//            response.setTan(tan.getTan());
            return response;
    }

    public static void main(String[] args) throws Throwable {
        QRSchema schema = new QRSchema();

        List<QRSchema.data> listData = new ArrayList<>();
        List<QRSchema.field> listField = new ArrayList<>();

        schema.setScheme("QC1");

        QRSchema.data data = new QRSchema.data();
        data.setName("kvalue");
        data.setValue("TATKHANHGIA");

        QRSchema.format format = new QRSchema.format();
        format.setVersion("2");

        QRSchema.field field1 = new QRSchema.field();
        field1.setName("Name");
        field1.setType(QRSchema.fieldType.t2);
        field1.setKvalue("kvalue");
        String a = "";
        listField.add(field1);
        listData.add(data);

        format.setFields(listField);

        schema.setData(listData);
        schema.setFormat(format);

        vn.mobileid.id.qrypto.object.Configuration configuration = new vn.mobileid.id.qrypto.object.Configuration();
        configuration.setContextIdentifier("QC1:");
        configuration.setQryptoHeight(1080);
        configuration.setQryptoWidth(1080);
        configuration.setIsTransparent(true);
        configuration.setQryptoEffectiveDate(
                new qryptoEffectiveDate("2023-03-22 00:00:00", "2023-04-30 00:00:00"));

        String transcation = "1";
        IssueQryptoWithFileAttachResponse res = QryptoService.getInstance(1).generateQR(schema, configuration, transcation);
        System.out.println("DataL:" + new ObjectMapper().writeValueAsString(res));
    }
}
