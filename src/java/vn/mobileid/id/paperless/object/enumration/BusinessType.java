/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

/**
 *
 * @author GiaTK
 */
public enum BusinessType {
    PERSONAL(1),
    BUSINESS(2);
    private int type;

    private BusinessType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getName_vn(){
        if(type ==1){
            return "Cá nhân";
        }
        if(type ==2){
            return "Doanh nghiệp";
        }
        return "Default";
    }
    
    public static BusinessType valueOf(Object type) {
        for (BusinessType type_ : values()) {
            if (type instanceof Integer) {
                if (type_.getType() == (int) type) {
                    return type_;
                }
            }
            if (type instanceof Long) {
                Long temp = (long) type;
                if (type_.getType() == temp.intValue()) {
                    return type_;
                }
            }
        }
        return BusinessType.PERSONAL;
    }
}
