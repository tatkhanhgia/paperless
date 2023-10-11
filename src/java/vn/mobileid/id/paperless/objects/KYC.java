/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.reflect.Field;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author GiaTK
 */
@XmlRootElement(name = "KYC")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KYC {

    private String FullName;
    private String BirthDate;
    private String Nationality;
    private String PersonalNumber;
    private String IssuanceDate;
    private String PlaceOfResidence;
    private String CurrentDate;
    private String DateAfterOneYear;
    private String PreviousDay;
    private String PreviousMonth;
    private String PreviousYear;
    
    //For QR
    private String Background;
    private float width;
    private float height;
    private String QR;

//    public KYC(String FullName, String BirthDate, String Nationality, String PersonalNumber, String IssuanceDate, String PlaceOfResidence, String CurrentDate, String DateAfterOneYear, String PreviousDay, String PreviousMonth, String PreviousYear) {
//        this.FullName = FullName;
//        this.BirthDate = BirthDate;
//        this.Nationality = Nationality;
//        this.PersonalNumber = PersonalNumber;
//        this.IssuanceDate = IssuanceDate;
////        this.PlaceOfResidence = PlaceOfResidence;
//        this.CurrentDate = CurrentDate;
//        this.DateAfterOneYear = DateAfterOneYear;
//        this.PreviousDay = PreviousDay;
//        this.PreviousMonth = PreviousMonth;
//        this.PreviousYear = PreviousYear;
//    }

    public KYC() {        
    }        

    @XmlElement(name = "FullName")
    @JsonProperty("name")
    public String getFullName() {
        return FullName;
    }

    @XmlElement(name = "BirthDate")
    public String getBirthDate() {
        return BirthDate;
    }

    @XmlElement(name = "Nationality")
    @JsonProperty("nationality")
    public String getNationality() {
        return Nationality;
    }

    @XmlElement(name = "PersonalNumber")
    @JsonProperty("document_number")
    public String getPersonalNumber() {
        return PersonalNumber;
    }

    @XmlElement(name = "IssuanceDate")
    public String getIssuanceDate() {
        return IssuanceDate;
    }

    @XmlElement(name = "PlaceOfResidence")
    @JsonProperty("place_of_residence")
    public String getPlaceOfResidence() {
        return PlaceOfResidence;
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
    
    @XmlElement(name = "Background")
    public String getBackground() {
        return Background;
    }   

    @XmlElement(name = "width")
    public float getWidth() {
        return width;
    }

    @XmlElement(name = "height")
    public float getHeight() {
        return height;
    }

    @XmlElement(name = "QR")
    public String getQR() {
        return QR;
    }

    public void setQR(String QR) {
        this.QR = QR;
    }
    
    public void set(Field field, Object value) throws IllegalArgumentException, IllegalAccessException {
        field.set(this, value);
    }

    public static Field[] getHashMapFieldName() {
        Field[] fieldKYC = KYC.class.getDeclaredFields();
        return fieldKYC;
    }

    public void setFullName(String FullName) {
        this.FullName = FullName;
    }

    public void setBirthDate(String BirthDate) {
        this.BirthDate = BirthDate;
    }

    public void setNationality(String Nationality) {
        this.Nationality = Nationality;
    }

    public void setPersonalNumber(String PersonalNumber) {
        this.PersonalNumber = PersonalNumber;
    }

    public void setIssuanceDate(String IssuanceDate) {
        this.IssuanceDate = IssuanceDate;
    }

    public void setPlaceOfResidence(String PlaceOfResidence) {
        this.PlaceOfResidence = PlaceOfResidence;
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

    public void setBackground(String Background) {
        this.Background = Background;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
