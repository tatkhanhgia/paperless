/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

/**
 *
 * @author GiaTK
 */
public enum WorkflowAttributeTypeName {
    ASSET_TEMPLATE(1, 1),
    CSV_EMAIL(2, ""),
    CSV_TASK_NOTIFICATION(3, true),
    EMAIL_NOTIFICATION(4, true),
    OMIT_IF_EMPTY(5, true),
    QR_BACKGROUND(6, ""),
    QR_SIZE(7, 1),
    QR_TYPE(8, 1),
    URL_CODE(9, true),
    METADATA(10, ""),
    QR_PLACEMENT(11, 1),
    SHOW_DOMAIN_BELOW_QR(12, ""),
    ASSET_BACKGROUND(13, 1),
    ASSET_APPEND(14, 1),
    PAGE_SIZE(15, 1),
    NOTE(21, ""),
    PAGE(23, 1),
    X_COORDINATE(24, 1),
    Y_COORDINATE(25, 1),
    TEXT_BELOW_QR(26, ""),
    STAMP_IN(27, 1),
    ASSET_ELABOR(28, 1),
    ASSET_ESIGN(29, 1);
    
    private int id;
    private Object type;

    private WorkflowAttributeTypeName(int id, Object type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public Object getType() {
        return type;
    }
    
    public static Object getType(int id){
        for(WorkflowAttributeTypeName type : values()){
            if(type.getId() == id){
                return type.getType();
            }
        }
        return null;
    }
}
