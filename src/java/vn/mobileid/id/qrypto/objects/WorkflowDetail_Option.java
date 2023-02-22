/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.reflect.Field;
import java.util.HashMap;
import vn.mobileid.id.general.LogHandler;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(value = {"notneed"},ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class WorkflowDetail_Option {

    private String qr_background;
    private int qr_size;
    private int qr_type;
    private boolean url_code;
    private int page;
    private String stamp_in;
    private String page_size;
    private int asset_Background;
    private int asset_Append;
    private int asset_Template;
    private boolean disable_CSV_task_notification_email;
    private boolean CSV_email;
    private boolean omit_if_empty;
    private boolean email_notification;
    private int x_cordinate;
    private int y_cordinate;
    private boolean QR_placement;
    private boolean show_domain;
    private String text_below_QR;
    private String note;
    private String metadata;

    public WorkflowDetail_Option(String qr_background, int qr_size, int qr_type, boolean url_code, int page, String stamp_in, String page_size, int asset_Background, int asset_Append, int asset_Template, boolean disable_CSV_task_notification_email, boolean CSV_email, boolean omit_if_empty, boolean email_notification) {
        this.qr_background = qr_background;
        this.qr_size = qr_size;
        this.qr_type = qr_type;
        this.url_code = url_code;
        this.page = page;
        this.stamp_in = stamp_in;
        this.page_size = page_size;
        this.asset_Background = asset_Background;
        this.asset_Append = asset_Append;
        this.asset_Template = asset_Template;
        this.disable_CSV_task_notification_email = disable_CSV_task_notification_email;
        this.CSV_email = CSV_email;
        this.omit_if_empty = omit_if_empty;
        this.email_notification = email_notification;
    }

    public WorkflowDetail_Option() {
    }

    @JsonProperty("qr_background")
    public String getQr_background() {
        return qr_background;
    }

    @JsonProperty("qr_size")
    public int getQr_size() {
        return qr_size;
    }

    @JsonProperty("qr_type")
    public int getQr_type() {
        return qr_type;
    }

    @JsonProperty("url_qr_code")
    public boolean isUrl_code() {
        return url_code;
    }

    @JsonProperty("page")
    public int getPage() {
        return page;
    }

    @JsonProperty("stamp_in")
    public String getStamp_in() {
        return stamp_in;
    }

    @JsonProperty("page_size")
    public String getPage_size() {
        return page_size;
    }

    @JsonProperty("asset_Background")
    public int getAsset_Background() {
        return asset_Background;
    }

    @JsonProperty("asset_Append")
    public int getAsset_Append() {
        return asset_Append;
    }

    @JsonProperty("asset_Template")
    public int getAsset_Template() {
        return asset_Template;
    }

    @JsonProperty("disable_csv_task_notification_email")
    public boolean isDisable_CSV_task_notification_email() {
        return disable_CSV_task_notification_email;
    }

    @JsonProperty("csv_email")
    public boolean isCSV_email() {
        return CSV_email;
    }

    @JsonProperty("omit_if_empty")
    public boolean isOmit_if_empty() {
        return omit_if_empty;
    }

    @JsonProperty("email_notification")
    public boolean isEmail_notification() {
        return email_notification;
    }

    @JsonProperty("note")
    public String getNote() {
        return note;
    }

    @JsonProperty("x_cordinate")
    public int getX_cordinate() {
        return x_cordinate;
    }

    @JsonProperty("y_cordinate")
    public int getY_cordinate() {
        return y_cordinate;
    }

    @JsonProperty("qr_placement")
    public boolean isQR_placement() {
        return QR_placement;
    }

    @JsonProperty("show_domain")
    public boolean isShow_domain() {
        return show_domain;
    }

    @JsonProperty("text_below_qr")
    public String getText_below_QR() {
        return text_below_QR;
    }

    public void setQR_placement(boolean QR_placement) {
        this.QR_placement = QR_placement;
    }

    public void setShow_domain(boolean show_domain) {
        this.show_domain = show_domain;
    }

    public void setText_below_QR(String text_below_QR) {
        this.text_below_QR = text_below_QR;
    }

    
    
    public void setX_cordinate(int x_cordinate) {
        this.x_cordinate = x_cordinate;
    }

    public void setY_cordinate(int y_cordinate) {
        this.y_cordinate = y_cordinate;
    }

    
    
    public void setNote(String note) {
        this.note = note;
    }

    @JsonProperty("meta_data")
    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }   
    
    public void setQr_background(String qr_background) {
        this.qr_background = qr_background;
    }

    public void setQr_size(int qr_size) {
        this.qr_size = qr_size;
    }

    public void setQr_type(int qr_type) {
        this.qr_type = qr_type;
    }

    public void setUrl_code(boolean url_code) {
        this.url_code = url_code;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setStamp_in(String stamp_in) {
        this.stamp_in = stamp_in;
    }

    public void setPage_size(String page_size) {
        this.page_size = page_size;
    }

    public void setAsset_Background(int asset_Background) {
        this.asset_Background = asset_Background;
    }

    public void setAsset_Append(int asset_Append) {
        this.asset_Append = asset_Append;
    }

    public void setAsset_Template(int asset_Template) {
        this.asset_Template = asset_Template;
    }

    public void setDisable_CSV_task_notification_email(boolean disable_CSV_task_notification_email) {
        this.disable_CSV_task_notification_email = disable_CSV_task_notification_email;
    }

    public void setCSV_email(boolean CSV_email) {
        this.CSV_email = CSV_email;
    }

    public void setOmit_if_empty(boolean omit_if_empty) {
        this.omit_if_empty = omit_if_empty;
    }

    public void setEmail_notification(boolean email_notification) {
        this.email_notification = email_notification;
    }
    
    @JsonProperty("notneed")
    public HashMap<String, Object> getHashMap() throws IllegalArgumentException, IllegalAccessException {
        HashMap<String, Object> map = new HashMap<>();
        Field[] fields = WorkflowDetail_Option.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.get(this) instanceof Integer) {
                Integer temp = field.getInt(this);
                if (temp.intValue() == 0) {
                    continue;
                }
            }
            if (field.get(this) != null) {
                map.put(field.getName(), field.get(this));
            }
        }
        return map;
    }

    public void set(String name, String value) throws IllegalArgumentException, IllegalAccessException {        
        Field[] fields = WorkflowDetail_Option.class.getDeclaredFields();
        for (Field field : fields) {
            if(field.getName().contains(name) || field.getName().equalsIgnoreCase(name)){
                if(field.get(this) instanceof Integer){
                    int temp = Integer.parseInt(value);
                    field.set(this, temp);
                    return;
                }                
                if(field.get(this) instanceof Boolean){
                    boolean temp = Boolean.parseBoolean((String)value);
                    field.set(this, temp);
                    return;
                }
                field.set(this, value);    
                return;
            }
        }        
    }
        
}
