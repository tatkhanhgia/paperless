/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.lang.reflect.Field;
import java.util.HashMap;
import vn.mobileid.id.paperless.serializer.CustomWorkflowDetailSerializer;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(value = {"notneed"}, ignoreUnknown = true)
@JsonSerialize(using = CustomWorkflowDetailSerializer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName("workflow_option")
public class WorkflowDetail_Option extends Object {

    private String qr_background;

    private String qr_size_string;
    private int qr_size;

    private String qr_type;

    private String url_code_string;
    private boolean url_code;

    private String page_string;
    private int page;

    private String stamp_in;
    
    private String page_size;

    private String asset_background_string;
    private int asset_background;

    private String asset_append_string;
    private int asset_append;
    
    private String asset_elabor_string;
    private int asset_elabor;

    private String asset_esign_string;
    private int asset_esign;

    private String asset_template_string;
    private int asset_template;

    private String disable_CSV_task_notification_email_string;
    private boolean disable_CSV_task_notification_email;

    private String CSV_email_string;
    private boolean CSV_email;

    private String omit_if_empty_string;
    private boolean omit_if_empty;

    private String email_notification_string;
    private boolean email_notification;

    private String x_cordinate_string;
    private int x_cordinate;

    private String y_cordinat_string;
    private int y_cordinate;

    private String QR_placement_string;
    private boolean QR_placement;

    private String show_domain_string;
    private boolean show_domain;

    private String text_below_QR;
    
    private String note;
    
    private String metadata;
    
    public WorkflowDetail_Option(String qr_background, int qr_size, String qr_type, boolean url_code, int page, String stamp_in, String page_size, int asset_Background, int asset_Append, int asset_Template, boolean disable_CSV_task_notification_email, boolean CSV_email, boolean omit_if_empty, boolean email_notification) {
        this.qr_background = qr_background;
        this.qr_size = qr_size;
        this.qr_type = qr_type;
        this.url_code = url_code;
        this.page = page;
        this.stamp_in = stamp_in;
        this.page_size = page_size;
        this.asset_background = asset_Background;
        this.asset_append = asset_Append;
        this.asset_template = asset_Template;
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
    public String getQr_type() {
        return qr_type;
    }

    @JsonProperty("url_code")
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

    @JsonProperty("asset_background")
    public int getAsset_Background() {
        return asset_background;
    }

    @JsonProperty("asset_append")
    public int getAsset_Append() {
        return asset_append;
    }

    @JsonProperty("asset_template")
    public int getAsset_Template() {
        return asset_template;
    }

    @JsonProperty("disable_CSV_task_notification_email")
    public boolean isDisable_CSV_task_notification_email() {
        return disable_CSV_task_notification_email;
    }

    @JsonProperty("CSV_email")
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
        this.QR_placement_string = "1";
        this.QR_placement = QR_placement;
    }

    public void setShow_domain(boolean show_domain) {
        this.show_domain_string = "1";
        this.show_domain = show_domain;
    }

    public void setText_below_QR(String text_below_QR) {
        this.text_below_QR = text_below_QR;
    }

    public void setX_cordinate(int x_cordinate) {
        this.x_cordinate_string = "1";
        this.x_cordinate = x_cordinate;
    }

    public void setY_cordinate(int y_cordinate) {
        this.y_cordinat_string = "1";
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
        this.qr_size_string = "1";
        this.qr_size = qr_size;
    }

    public void setQr_type(String qr_type) {
        this.qr_type = qr_type;
    }

    public void setUrl_code(boolean url_code) {
        this.url_code_string = "1";
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
        this.asset_background_string = "1";
        this.asset_background = asset_Background;
    }

    public void setAsset_Append(int asset_Append) {
        this.asset_append_string = "1";
        this.asset_append = asset_Append;
    }

    public void setAsset_Template(int asset_Template) {
        this.asset_template_string = "1";
        this.asset_template = asset_Template;
    }

    public void setDisable_CSV_task_notification_email(boolean disable_CSV_task_notification_email) {
        this.disable_CSV_task_notification_email_string = "1";
        this.disable_CSV_task_notification_email = disable_CSV_task_notification_email;
    }

    public void setCSV_email(boolean CSV_email) {
        this.CSV_email_string = "1";
        this.CSV_email = CSV_email;
    }

    public void setOmit_if_empty(boolean omit_if_empty) {
        this.omit_if_empty_string = "1";
        this.omit_if_empty = omit_if_empty;
    }

    public void setEmail_notification(boolean email_notification) {
        this.email_notification_string = "1";
        this.email_notification = email_notification;
    }

    public String getAsset_elabor_string() {
        return asset_elabor_string;
    }

    public void setAsset_elabor_string(String asset_elabor_string) {
        this.asset_elabor_string = asset_elabor_string;
    }

    public int getAsset_elabor() {
        return asset_elabor;
    }

    public void setAsset_elabor(int asset_elabor) {
        this.asset_elabor = asset_elabor;
    }

    public String getAsset_esign_string() {
        return asset_esign_string;
    }

    public void setAsset_esign_string(String asset_esign_string) {
        this.asset_esign_string = asset_esign_string;
    }

    public int getAsset_esign() {
        return asset_esign;
    }

    public void setAsset_esign(int asset_esign) {
        this.asset_esign = asset_esign;
    }
    
    

    @JsonProperty("notneed")
    public HashMap<String, Object> getHashMap() throws IllegalArgumentException, IllegalAccessException {
        HashMap<String, Object> map = new HashMap<>();
        Field[] fields = WorkflowDetail_Option.class.getDeclaredFields();
        int flag = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            if (flag == 2) {
                flag = 0;
                continue;
            }
            if (flag == 1) {
                map.put(field.getName(), field.get(this));
                flag = 0;
                continue;
            }
            if (field.getName().endsWith("string")) {
                try {
                    if (((String) field.get(this)) == null) {
                        flag = 2;
                        continue;
                    }
                } catch (Exception ex) {
                    continue;
                }
                flag = 1;
                continue;
            }
            try {
                if (field.get(this) != null) {
                    map.put(field.getName(), field.get(this));
                }
            } catch (Exception ex) {

            }
        }
        return map;
    }

    public void set(String name, String value) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = WorkflowDetail_Option.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getName().endsWith("string") && field.getName().contains(name)) {
                field.set(this, "1");
                continue;
            }
            if (field.getName().contains(name) || field.getName().equalsIgnoreCase(name)) {
                if (field.get(this) instanceof Integer) {
                    int temp = Integer.parseInt(value);
                    field.set(this, temp);
                    return;
                }
                if (field.get(this) instanceof Boolean) {
                    boolean temp = Boolean.parseBoolean((String) value);
                    field.set(this, temp);
                    return;
                }
                field.set(this, value);
                return;
            }
        }
    }

    public static void main(String[] args) throws JsonProcessingException, IllegalArgumentException, IllegalAccessException {
        WorkflowDetail_Option option = new WorkflowDetail_Option();
        option.setDisable_CSV_task_notification_email(false);
        option.setQr_size(100);
        option.setQR_placement(true);
        System.out.println(new ObjectMapper().writeValueAsString(option));
//        for(Field field : WorkflowDetail_Option.class.getDeclaredFields()){
//            System.out.println(field);
//        }
        HashMap<String, Object> a = option.getHashMap();
        for (String b : a.keySet()) {
            System.out.println(a.get(b));
        }
    }

}
