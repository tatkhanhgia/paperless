/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import vn.mobileid.id.general.email.Email;
import vn.mobileid.id.general.email.EmailReq;
import vn.mobileid.id.general.objects.Attachment;

/**
 *
 * @author GiaTK
 */
public class TestSendMail {
    public static void main(String[] arhs) throws IOException{
        EmailReq request = new EmailReq();
        request.setSendTo("giatk@mobile-id.vn");
        request.setSubject("SigedFilePDF");
        List<Attachment> attachs = new ArrayList<>();
        attachs.add(new Attachment(Files.readAllBytes(new File("D:\\NetBean\\qrypto\\file\\result.pdf").toPath()), "sIGNEDpdf"));
        request.setAttachments(attachs);
        request.setContent("test Content");
        request.setEntityName("EntityName");
        
        Email email = new Email(null);
        email.send(request);
    }
}
