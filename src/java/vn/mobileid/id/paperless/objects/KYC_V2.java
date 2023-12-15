/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @authorGiaTK
 */
//@JsonRootName("KYC_V2")
//@JsonIgnoreProperties(ignoreUnknown = true)
public class KYC_V2 {
    
    @JsonRootName("KYC_V2")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Component {

        //For QR
        private String Background;
        private String width;
        private String height;
        private String QR;
        private String ValidationUrl;
        
        //For Course
        private String daySign;
        private String monthSign;
        private String yearSign;

        //ELabor
        private String CurrentDate;
        private String DateAfterOneYear;
        private String PreviousDay;
        private String PreviousMonth;
        private String PreviousYear;

        public Component() {
        }

        @XmlElement(name = "Background")
        @JsonProperty("background")
        public String getBackground() {
            return Background;
        }

        @XmlElement(name = "width")
        @JsonProperty("width")
        public String getWidth() {
            return width;
        }

        @XmlElement(name = "height")
        @JsonProperty("height")
        public String getHeight() {
            return height;
        }

        @XmlElement(name = "QR")
        @JsonProperty("qr")
        public String getQR() {
            return QR;
        }

        @XmlElement(name = "Url")
        @JsonProperty("validationUrl")
        public String getValidationUrl() {
            return ValidationUrl;
        }

        @XmlElement(name = "CurrentDate")
        @JsonProperty("currentDate")
        public String getCurrentDate() {
            return CurrentDate;
        }

        @XmlElement(name = "DateAfterOneYear")
        @JsonProperty("dateAfterOneYear")
        public String getDateAfterOneYear() {
            return DateAfterOneYear;
        }

        @XmlElement(name = "PreviousDay")
        @JsonProperty("previousDay")
        public String getPreviousDay() {
            return PreviousDay;
        }

        @XmlElement(name = "PreviousMonth")
        @JsonProperty("previousMonth")
        public String getPreviousMonth() {
            return PreviousMonth;
        }

        @XmlElement(name = "PreviousYear")
        @JsonProperty("previousYear")
        public String getPreviousYear() {
            return PreviousYear;
        }

        @XmlElement(name = "Day")
        @JsonProperty("daySign")
        public String getDaySign() {
            return daySign;
        }

        @XmlElement(name = "Month")
        @JsonProperty("monthSign")
        public String getMonthSign() {
            return monthSign;
        }

        @XmlElement(name = "Year")
        @JsonProperty("yearSign")
        public String getYearSign() {
            return yearSign;
        }

        public void setDaySign(String daySign) {
            this.daySign = daySign;
        }

        public void setMonthSign(String monthSign) {
            this.monthSign = monthSign;
        }

        public void setYearSign(String yearSign) {
            this.yearSign = yearSign;
        }
        
        public void setBackground(String Background) {
            this.Background = Background;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public void setQR(String QR) {
            this.QR = QR;
        }

        public void setValidationUrl(String ValidationUrl) {
            this.ValidationUrl = ValidationUrl;
        }

        public void setCurrentDate(String CurrentDate) {
            this.CurrentDate = CurrentDate;
        }

        public void setDateAfterOneYear(String DateAfterOneYear) {
            this.DateAfterOneYear = DateAfterOneYear;
        }

        public void setPreviousDay(String PreviousDay) {
            this.PreviousDay = PreviousDay;
        }

        public void setPreviousMonth(String PreviousMonth) {
            this.PreviousMonth = PreviousMonth;
        }

        public void setPreviousYear(String PreviousYear) {
            this.PreviousYear = PreviousYear;
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
//        String temp = "{\"KYC_V2\":{\"width\":null,\"height\":null,\"daySign\":null,\"monthSign\":null,\"yearSign\":null,\"dateAfterOneYear\":\"2024-12-15\",\"previousMonth\":\"11\",\"currentDate\":\"2023-12-15\",\"previousDay\":\"16\",\"previousYear\":\"2022\",\"background\":null,\"qr\":null,\"validationUrl\":null}}";
        String temp = "{\"items\":[{\"field\":\"FullName\",\"mandatory_enable\":false,\"type\":1,\"value\":\"Tat Khanh Gia\",\"remark\":\"text\",\"field_name\":\"Full Name\",\"field_name_vn\":\"H? và Tên\"},{\"field\":\"BirthDate\",\"mandatory_enable\":false,\"type\":1,\"value\":\"07/09/2000\",\"remark\":\"date\",\"field_name\":\"Birth Date\",\"field_name_vn\":\"Ngày sinh\"},{\"field\":\"Nationality\",\"mandatory_enable\":false,\"type\":1,\"value\":\"VN\",\"remark\":\"text\",\"field_name\":\"Nationality\",\"field_name_vn\":\"Qu?c t?ch\"},{\"field\":\"PersonalNumber\",\"mandatory_enable\":false,\"type\":1,\"value\":\"079200011188\",\"remark\":\"text\",\"field_name\":\"Personal Number\",\"field_name_vn\":\"C?n c??c công dân\"},{\"field\":\"IssuanceDate\",\"mandatory_enable\":false,\"type\":1,\"value\":\"07/09/2001\",\"remark\":\"date\",\"field_name\":\"Issuance Date\",\"field_name_vn\":\"Ngày c?p\"},{\"field\":\"PlaceOfResidence\",\"mandatory_enable\":false,\"type\":1,\"value\":\"Place\",\"remark\":\"text\",\"field_name\":\"Place Of Residence\",\"field_name_vn\":\"N?i th??ng trú\"}],\"KYC_V2\":{\"width\":null,\"height\":null,\"daySign\":null,\"monthSign\":null,\"yearSign\":null,\"dateAfterOneYear\":\"2024-12-15\",\"previousMonth\":\"11\",\"currentDate\":\"2023-12-15\",\"previousDay\":\"16\",\"previousYear\":\"2022\",\"background\":null,\"qr\":null,\"validationUrl\":null}}";
        JsonNode node = new ObjectMapper().readTree(temp);
        System.out.println(node.size());
        Iterator<Entry<String, JsonNode>> temps = node.fields();
        while(temps.hasNext()){
            Entry<String, JsonNode> a = temps.next();
            Annotation[] annots = KYC_V2.Component.class.getDeclaredAnnotations();
            for(Annotation aa : annots){
                if(aa.annotationType() == JsonRootName.class){
                    JsonRootName rootName = (JsonRootName) aa;
                    System.out.println("Value:"+rootName.value());
                }
            }
        }
//        System.out.println(com.getCurrentDate());
//        System.out.println(com.getDateAfterOneYear());
    }
}
