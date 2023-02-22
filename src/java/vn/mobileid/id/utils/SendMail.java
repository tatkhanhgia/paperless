/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.utils;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.email.Email;
import vn.mobileid.id.general.email.EmailReq;
import vn.mobileid.id.general.objects.Attachment;
import vn.mobileid.id.qrypto.QryptoService;

/**
 *
 * @author GiaTK
 */
public class SendMail {

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
    public SendMail(String sendTo, String name, String CCCD, byte[] file, String filename){
        this.sendTo = sendTo;
        this.Subject = "eLaborContract -"+name+" - "+CCCD;
        this.Content = "Dear " + name;
        this.Content += "<br /><br /> Paperless service would like to thank you for trusting and using our services";
        this.Content += "<br /><br /> Your eLaborContract has been created successfully.";
        this.file = file;
        this.fileName = filename;
        this.EntityName = "eLaborContract";        
    }

    public void createMessage(String name) {
        Content = "Dear " + name;
        Content += "<br /><br /> Paperless service would like to thank you for trusting and using our services";
        Content += "<br /><br /> Your eLaborContract has been created successfully.";
    }

    public void send() {
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
}
