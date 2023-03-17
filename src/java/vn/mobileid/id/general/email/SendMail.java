/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.email;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.email.Email;
import vn.mobileid.id.general.email.EmailReq;
import vn.mobileid.id.general.objects.Attachment;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.PaperlessService;
import vn.mobileid.id.paperless.objects.EmailTemplate;
import vn.mobileid.id.paperless.objects.Enterprise;

/**
 *
 * @author GiaTK
 */
public class SendMail extends Thread {

    final private static Logger LOG = LogManager.getLogger(SendMail.class);

    private String sendTo;
    private String Subject;
    private String Content;
    private String EntityName;
    private byte[] file;
    private String fileName;

    //Contructor for custom
    public SendMail(String sendTo, String Subject, String Content, String EntityName, byte[] file, String fileName) {
        this.sendTo = sendTo;
        this.Subject = Subject;
        this.Content = Content;
        this.EntityName = EntityName;
        this.file = file;
        this.fileName = fileName;
    }

    //Constructor for Elabor
    public SendMail(String sendTo, String name, String CCCD, byte[] file, String filename) {
        this.sendTo = sendTo;
        this.Subject = "eLaborContract - " + name + " - " + CCCD;
        this.Content = "Dear " + name;
        this.Content += "<br /><br /> Paperless service would like to thank you for trusting and using our services";
        this.Content += "<br /><br /> Your eLaborContract has been created successfully.";
        this.file = file;
        this.fileName = filename;
        this.EntityName = "eLaborContract";
    }

    public SendMail(String sendTo, int enterprise_id, String name, String CCCD, byte[] file, String filename) {
        this.sendTo = sendTo;
        this.file = file;
        this.fileName = filename;
        EmailTemplate template = getTemplate(enterprise_id);
        if (template == null) {
            this.Subject = "eLaborContract - " + name + " - " + CCCD;
            this.Content = "Dear " + name;
            this.Content += "<br /><br /> Paperless service would like to thank you for trusting and using our services";
            this.Content += "<br /><br /> Your eLaborContract has been created successfully.";
        } else {
            this.Subject = template.getSubject().replace("@name", name);
            this.Subject = this.Subject.replace("@CCCD", CCCD);
            this.Content = template.getBody().replace("@name", name);
        }
    }

    public  void send() {
        try {
            EmailReq request = new EmailReq();
            request.setSendTo(sendTo);
            request.setSubject(Subject);
            List<Attachment> attachs = new ArrayList<>();
            attachs.add(new Attachment(file, fileName));
            request.setAttachments(attachs);
            request.setContent(Content);
            request.setEntityName(EntityName);

            Email email = new Email(null);
            email.send(request);
        } catch (Exception ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot Send mail! - Detail:" + ex);
            }
        }
    }

    private EmailTemplate getTemplate(int enterprise_id) {
        try {
            String email_notification = "";
            Database db = new DatabaseImpl();
            DatabaseResponse res = db.getEnterpriseInfo(enterprise_id);
            if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                email_notification = "defaultemail";
            } else {
                Enterprise ent = (Enterprise) res.getObject();
                email_notification = ent.getEmail_notification();
            }

            res = db.getEmailTemplate(
                    2, //langguageEN
                    email_notification,
                    "transactionID");
            if (res.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                return null;
            }
            EmailTemplate email_template = (EmailTemplate) res.getObject();
            return email_template;
        } catch (Exception ex) {
            if (LogHandler.isShowErrorLog()) {
                ex.printStackTrace();
                LogHandler.error(SendMail.class, "Error while get Email Template");
            }
        }
        return null;
    }

}
