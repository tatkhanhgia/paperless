/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

import org.apache.commons.compress.utils.Lists;

/**
 *
 * @author GiaTK
 * In FileManagement to manage the Type of File
 */
public enum FileType {
    PDF("PDF"),
    pdf("pdf"),
    WORD("DOCX"),
    doc("doc"),
    XSLT("XSLT"),
    xslt("xslt"),
    png("png"),
    PNG("PNG"),
    jpeg("jpeg"),
    jpg("jpg"),
    JPG("JPG"),
    JPEG("JPEG");

    private String name;

    FileType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static boolean isContaint(String extension){
        for(FileType type : values()){
            System.out.println("TypeName:"+type.getName());
            System.out.println("ExtensionName:"+extension);
            if(type.getName().equals(extension)){
                System.out.println("Equal:true");
                return true;
            }
        }
        System.out.println("Equal:false");
        return false;
    }
    
    public static boolean isImage(String extension){
        FileType[] temp = new FileType[]{FileType.JPEG, FileType.PNG, FileType.jpeg, FileType.png};
        for(FileType temp_ : temp){
            if(temp_.getName().equals(extension)){
                return true;
            }
        }
        return false;
    }
}
