/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.email;

import com.sun.mail.util.MailSSLSocketFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author VUDP
 */
public class SendMail {

    public static void main(String[] args) throws Exception {        
        //Test hàm đính kèm file
        String filename = "testPDF.pdf";
        byte[] data = Files.readAllBytes(new File("D:\\NetBean\\qrypto\\file\\result.pdf").toPath());
        attachment(filename, data);
    }

    public static void attachment(String filename, byte[] data) throws UnsupportedEncodingException, MessagingException, GeneralSecurityException, IOException {
        String sendTo = "giatk@mobile-id.vn";
        String subject = "Test hàm gửi email";
        String content = "Đây là mail có file đính kèm";

        byte[] config = IOUtils.toByteArray(new FileInputStream("D:\\NetBean\\qrypto\\src\\java\\config.properties"));

        // parsing properties
        final Properties props = new Properties();
        Reader reader = new InputStreamReader(new ByteArrayInputStream(config), StandardCharsets.UTF_8);
        props.load(reader);

        final String username = props.getProperty("mail.smtp.username");
        final String password = props.getProperty("mail.smtp.password");

        final String sendFromAddr = props.getProperty("mail.smtp.sendfromaddr", username);
        final String sendFromName = props.getProperty("mail.smtp.sendfromname", username);

        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.socketFactory", sf);

        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols","TLSv1.2");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        
        Session session = null;
        if (username == null || password == null) {
            // Get the Session object.
            session = Session.getInstance(props, null);
        } else {
            // Get the Session object.
            session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username,
                            password);
                }
            });
        }

        // Create a default MimeMessage object.
        Message message = new MimeMessage(session);
        // Set From: header field of the header.
        message.setFrom(new InternetAddress(sendFromAddr, sendFromName));

        // Set To: header field of the header.
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(sendTo));

        // Set Subject: header field
        message.setSubject(subject);

        //Create Body mail
        Multipart multipart = new MimeMultipart();
        
            //Text in mail
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setContent(content, "text/html; charset=utf-8");
            
            //Create attachment
            MimeBodyPart textBodyPart2 = new MimeBodyPart();
            DataSource source = new ByteArrayDataSource(data, "application/octet-stream");
            textBodyPart2.setDataHandler(new DataHandler(source));
            textBodyPart2.setFileName(filename);
           

        multipart.addBodyPart(textBodyPart);
        multipart.addBodyPart(textBodyPart2);
        message.setContent(multipart);
        
        // Send message
        Transport.send(message);
        System.out.println("OK");
    }
}
