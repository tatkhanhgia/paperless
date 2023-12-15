/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

/**
 *
 * @author GiaTK
 */
public enum QRBackground {
    White("White", "WHITE"),
    Transparent("Transparent", "TRANSPARENT");
    
    private String[] name;

    private QRBackground(String name, String name2) {
        this.name = new String[2];
        this.name[0] = name;
        this.name[1] = name2;
    }

    public String[] getName() {
        return name;
    }
    
    public static boolean isTransparent(String qrBackground){
        for(QRBackground background : values()){
            if(background.getName()[0].equals(qrBackground) ||
                    background.getName()[1].equals(qrBackground)){
                return true;
            }
        }
        return false;
    }
}
