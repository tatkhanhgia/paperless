/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author GiaTK
 */
public class AppInfo {
    private String dateformat;
    private String version;
    private String description;
    private List<String> lang;
    private String logo;
    private List<String> authType;
    private List<String> method;
    private String region;

    public AppInfo() {
    }

    public String getDateFormat() {
        return dateformat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateformat = dateFormat;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateformat() {
        return dateformat;
    }

    public void setDateformat(String dateformat) {
        this.dateformat = dateformat;
    }

    public List<String> getLang() {
        return lang;
    }

    public void setLang(List<String> lang) {
        this.lang = lang;
    }

  

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<String> getAuthType() {
        return authType;
    }

    public void setAuthType(List<String> authType) {
        this.authType = authType;
    }

    public List<String> getMethod() {
        return method;
    }

    public void setMethod(List<String> method) {
        this.method = method;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
    
    public void set(String fieldName, String value) throws IllegalArgumentException, IllegalAccessException{
        for(Field field : this.getClass().getDeclaredFields()){            
            if(fieldName.contains(field.getName())){                
                if( value.contains(";") && value.split(";").length > 1){
                    List<String> data = new ArrayList<>();
                    String[] token = value.split(";");
                    for(String temp : token){
                        data.add(temp);
                    }
                    field.set(this, data);
                    continue;
                }
                field.set(this, value);
            }
        }
    }
    
    public static AppInfo cast(Properties prop) throws IllegalArgumentException, IllegalAccessException{
        AppInfo temp = new AppInfo();
        for(String key : prop.stringPropertyNames()){
            temp.set(key, prop.getProperty(key));
        }
        return temp;
    }
    
}
