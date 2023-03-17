/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

/**
 *
 * @author GiaTK
 */
public class EmailTemplate {
    private String subject;
    private String body;
    private byte[] data;
    private String filename;

    public EmailTemplate() {
    }

    public EmailTemplate(String subject, String body, byte[] data, String filename) {
        this.subject = subject;
        this.body = body;
        this.data = data;
        this.filename = filename;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    
}
