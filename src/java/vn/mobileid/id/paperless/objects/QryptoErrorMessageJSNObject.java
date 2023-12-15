/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties("error")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class QryptoErrorMessageJSNObject {    
    private String code;
    private String code_description;
    private RemarkLanguage remark;
    
    //For login
    private int remaining_counter;
    private Lock lock;

    public QryptoErrorMessageJSNObject() {
    }

    @JsonProperty("error")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("error_description")
    public String getCode_description() {
        return code_description;
    }

    public void setCode_description(String code_description) {
        this.code_description = code_description;
    }

    @JsonProperty("remark_language")
    public RemarkLanguage getRemark() {
        return remark;
    }

    @JsonProperty("lock")
    public Lock getLock() {
        return lock;
    }

    @JsonProperty("remaining_counter")
    public int getRemaining_counter() {
        return remaining_counter;
    }

    public void setRemaining_counter(int remaining_counter) {
        this.remaining_counter = remaining_counter;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }
    
    public void setRemark(RemarkLanguage remark) {
        this.remark = remark;
    }
    
    public static class RemarkLanguage{
        private String remark_VN;
        private String remark_EN;

        public RemarkLanguage() {
        }

        @JsonProperty("error_remark_vn")
        public String getRemark_VN() {
            return remark_VN;
        }

        public void setRemark_VN(String remark_VN) {
            this.remark_VN = remark_VN;
        }

        @JsonProperty("error_remark_en")
        public String getRemark_EN() {
            return remark_EN;
        }

        public void setRemark_EN(String remark_EN) {
            this.remark_EN = remark_EN;
        }
    }
    
    public static class Lock{
        private int minute;
        private int second;

        public Lock() {
        }

        @JsonProperty("remaining_lock_minute")
        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        @JsonProperty("remaining_lock_second")
        public int getSecond() {
            return second;
        }

        public void setSecond(int second) {
            this.second = second;
        }
    }
    
    public static void main(String[] args) throws Exception{
        RemarkLanguage remark = new RemarkLanguage();
        remark.setRemark_EN("en");
        remark.setRemark_VN("vn");
        
        QryptoErrorMessageJSNObject message = new QryptoErrorMessageJSNObject();
        message.setRemark(remark);
        message.setCode("Code");
        message.setCode_description("Des");
        
        System.out.println(new ObjectMapper().writeValueAsString(message));
    }
}
