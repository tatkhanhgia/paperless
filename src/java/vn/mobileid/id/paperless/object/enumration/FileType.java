/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

/**
 *
 * @author GiaTK
 */
public enum FileType {
    PDF("PDF"),
    WORD("DOCX"),
    XSLT("XSLT");

    private String name;

    FileType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
