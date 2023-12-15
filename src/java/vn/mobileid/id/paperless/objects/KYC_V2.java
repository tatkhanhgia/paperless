/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @authorGiaTK
 */
@JsonRootName("KYC_V2")
@JsonIgnoreProperties(ignoreUnknown = true)
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
        public String getBackground() {
            return Background;
        }

        @XmlElement(name = "width")
        public String getWidth() {
            return width;
        }

        @XmlElement(name = "height")
        public String getHeight() {
            return height;
        }

        @XmlElement(name = "QR")
        public String getQR() {
            return QR;
        }

        @XmlElement(name = "Url")
        public String getValidationUrl() {
            return ValidationUrl;
        }

        @XmlElement(name = "CurrentDate")
        public String getCurrentDate() {
            return CurrentDate;
        }

        @XmlElement(name = "DateAfterOneYear")
        public String getDateAfterOneYear() {
            return DateAfterOneYear;
        }

        @XmlElement(name = "PreviousDay")
        public String getPreviousDay() {
            return PreviousDay;
        }

        @XmlElement(name = "PreviousMonth")
        public String getPreviousMonth() {
            return PreviousMonth;
        }

        @XmlElement(name = "PreviousYear")
        public String getPreviousYear() {
            return PreviousYear;
        }

        @XmlElement(name = "Day")
        public String getDaySign() {
            return daySign;
        }

        @XmlElement(name = "Month")
        public String getMonthSign() {
            return monthSign;
        }

        @XmlElement(name = "Year")
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

}
