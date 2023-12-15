/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.Date;
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
    
    //For Course
    private String GraduationType; //Loại
    private String Specialized; //CHuyên ngành
    private int Day;
    private int Month;
    private int Year;
    private String SerialNumber; //Số hiệu
    private String ReferenceNumber; //Sổ gốc
    private String Gender; //1: Ông, 2:Bà
    private String ValidationUrl;
    private String SignDate;

    //Internal 
    private Date date; //Lưu trữ dữ liệu date phòng khi cần

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

    @XmlElement(name = "GraduationType")
    public String getGraduationType() {
        return GraduationType;
    }

    @XmlElement(name = "Specialized")
    public String getSpecialized() {
        return Specialized;
    }

    @XmlElement(name = "Day")
    public int getDay() {
        return Day;
    }

    @XmlElement(name = "Month")
    public int getMonth() {
        return Month;
    }

    @XmlElement(name = "Year")
    public int getYear() {
        return Year;
    }

    @XmlElement(name = "SerialNumber")
    public String getSerialNumber() {
        return SerialNumber;
    }

    @XmlElement(name = "ReferenceNumber")
    public String getReferenceNumber() {
        return ReferenceNumber;
    }

    @XmlElement(name = "Gender")
    public String getGender() {
        return Gender;
    }

    @XmlElement(name = "Url")
    public String getValidationUrl() {
        return ValidationUrl;
    }

    public String getSignDate() {
        return SignDate;
    }

    public void setValidationUrl(String ValidationUrl) {
        this.ValidationUrl = ValidationUrl;
    }
    
    public void setSerialNumber(String SerialNumber) {
        this.SerialNumber = SerialNumber;
    }

    public void setReferenceNumber(String ReferenceNumber) {
        this.ReferenceNumber = ReferenceNumber;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }
    
    public void setGraduationType(String GraduationType) {
        this.GraduationType = GraduationType;
    }

    public void setSpecialized(String Specialized) {
        this.Specialized = Specialized;
    }

    public void setDay(int Day) {
        this.Day = Day;
    }

    public void setMonth(int Month) {
        this.Month = Month;
    }

    public void setYear(int Year) {
        this.Year = Year;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setSignDate(String SignDate) {
        this.SignDate = SignDate;
    }
    
    public static void main(String[] args) throws Exception{
        String payload = "{\"items\":[{\"field\":\"FullName\",\"mandatory_enable\":false,\"type\":1,\"value\":\"Tat Khanh Gia\",\"remark\":\"text\"},{\"field\":\"BirthDate\",\"mandatory_enable\":false,\"type\":1,\"value\":\"07092000\",\"remark\":\"date\"},{\"field\":\"Nationality\",\"mandatory_enable\":false,\"type\":1,\"value\":\"\",\"remark\":\"text\"},{\"field\":\"PersonalNumber\",\"mandatory_enable\":false,\"type\":1,\"value\":\"\",\"remark\":\"text\"},{\"field\":\"IssuanceDate\",\"mandatory_enable\":false,\"type\":1,\"value\":\"\",\"remark\":\"date\"},{\"field\":\"PlaceOfResidence\",\"mandatory_enable\":false,\"type\":1,\"value\":\"\",\"remark\":\"text\"}]}";
        KYC object = new ObjectMapper().enable(DeserializationFeature.UNWRAP_ROOT_VALUE).readValue(payload, KYC.class);
        System.out.println("Objects:"+object.getFullName());
    }
}
