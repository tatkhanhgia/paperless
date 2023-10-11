/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

/**
 *
 * author GiaTK
 * In Workflow Activity to manage the Type of DownloadLink
 */
public enum DownloadLinkType {
    PDF("PDF"),
    IMAGE("IMAGE"),
    ZIP("ZIP");
    
    private String type;

    private DownloadLinkType(String type) {
        this.type= type;
    }

    public String getType() {
        return type;
    }
}
