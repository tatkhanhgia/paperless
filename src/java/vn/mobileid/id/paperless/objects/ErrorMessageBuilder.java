/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

/**
 *
 * @author GiaTK
 */
public interface ErrorMessageBuilder {
    public ErrorMessageBuilder sendErrorMessage(String message);
    public ErrorMessageBuilder sendErrorDescriptionMessage(String description);
    public String build();
}
