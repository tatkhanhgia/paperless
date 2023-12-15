/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.paperless.serializer.CustomQRSchemeSerializer;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize(using = CustomQRSchemeSerializer.class)
public class QRSchema {

    public static class data {

        private String name;
        private String value;

        public data() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    public static class format {

        private String version;
        private List<field> fields;

        public format() {
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public List<field> getFields() {
            return fields;
        }

        public void setFields(List<field> fields) {
            this.fields = fields;
        }
    }

    public static class field {

        private String name;
        private fieldType type;
        private String kvalue;
        
        //File
        private String file_type;
        private String file_field;
        private String file_name;
        private int share_mode;
        
        //QR
        private QR_META_DATA qr_meta_data;

        public field() {
        }

        public int getShare_mode() {
            return share_mode;
        }

        public void setShare_mode(int share_mode) {
            this.share_mode = share_mode;
        }

        public String getFile_type() {
            return file_type;
        }

        public void setFile_type(String file_type) {
            this.file_type = file_type;
        }

        public String getFile_field() {
            return file_field;
        }

        public void setFile_field(String field_field) {
            this.file_field = field_field;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public fieldType getType() {
            return type;
        }

        public void setType(fieldType type) {
            this.type = type;
        }

        public String getKvalue() {
            return kvalue;
        }

        public void setKvalue(String kvalue) {
            this.kvalue = kvalue;
        }

        public String getFile_name() {
            return file_name;
        }

        public void setFile_name(String file_name) {
            this.file_name = file_name;
        }

        public QR_META_DATA getQr_meta_data() {
            return qr_meta_data;
        }

        public void setQr_meta_data(QR_META_DATA qr_meta_data) {
            this.qr_meta_data = qr_meta_data;
        }
    }

    public static class QR_META_DATA{
        private boolean isTransparent;
        private float xCoordinator;
        private float yCoordinator;
        private float qrDimension;
        private List<Integer> pageNumber;

        public QR_META_DATA() {
        }

        public boolean isIsTransparent() {
            return isTransparent;
        }

        public float getxCoordinator() {
            return xCoordinator;
        }

        public float getyCoordinator() {
            return yCoordinator;
        }

        public float getQrDimension() {
            return qrDimension;
        }

        public List<Integer> getPageNumber() {
            return pageNumber;
        }

        public QR_META_DATA setIsTransparent(boolean isTransparent) {
            this.isTransparent = isTransparent;
            return this;
        }

        public QR_META_DATA setxCoordinator(float xCoordinator) {
            this.xCoordinator = xCoordinator;
            return this;
        }

        public QR_META_DATA setyCoordinator(float yCoordinator) {
            this.yCoordinator = yCoordinator;
            return this;
        }

        public QR_META_DATA setQrDimension(float qrDimension) {
            this.qrDimension = qrDimension;
            return this;
        }

        public QR_META_DATA setPageNumber(List<Integer> pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }
        
        
    }
    
    public enum fieldType {
        url("url"),//url
        t2("t2"), //text
        f1("f1"); //file

        private String name;

        private fieldType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private String scheme;
    private List<data> data;
    private format format;
    private String title;
    private String ci;
    
    @JsonIgnore
    private HashMap<String, byte[]> header = new HashMap<>();

    public QRSchema() {
    }

    public HashMap<String, byte[]> getHeader() {
        return header;
    }

    public void setHeader(HashMap<String, byte[]> header) {
        this.header = header;
    }

    
    
    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public List<data> getData() {
        return data;
    }

    public void setData(List<data> data) {
        this.data = data;
    }

    public format getFormat() {
        return format;
    }

    public void setFormat(format format) {
        this.format = format;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

}
