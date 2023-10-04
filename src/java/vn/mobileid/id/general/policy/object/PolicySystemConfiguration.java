/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.policy.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.List;
import vn.mobileid.id.general.objects.Attributes;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName("attributes")
public class PolicySystemConfiguration  extends Attributes{
    
    private List<SystemConfiguration> attributes;
       
    public PolicySystemConfiguration() {
    }

    @JsonProperty("attributes")
    public List<SystemConfiguration> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<SystemConfiguration> attributes) {
        this.attributes = attributes;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SystemConfiguration {

        private int default_row_count;
        private TokenConfiguration tokenConfig;
        private String dateFormat;

        @JsonProperty("defaultRowCount")
        public int getDefault_row_count() {
            return default_row_count;
        }

        public void setDefault_row_count(int default_row_count) {
            this.default_row_count = default_row_count;
        }

        @JsonProperty("token")
        public TokenConfiguration getTokenConfig() {
            return tokenConfig;
        }

        public void setTokenConfig(TokenConfiguration tokenConfig) {
            this.tokenConfig = tokenConfig;
        }

        @JsonProperty("dateFormat")
        public String getDateFormat() {
            return dateFormat;
        }

        public void setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }
        
        

    }

    public static enum TypeDate {
        Second("secound"),
        Hour("hour"),
        Day("day"),
        Month("month");

        private String name;

        private TypeDate(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TokenConfiguration {

        private String alg;
        private String typ;
        private long access_token_expired_in;
        private long refresh_token_expired_in;
        private long password_user_expired_at;
        private TypeDate type_date;

        @JsonProperty("alg")
        public String getAlg() {
            return alg;
        }

        public void setAlg(String alg) {
            this.alg = alg;
        }

        @JsonProperty("typ")
        public String getTyp() {
            return typ;
        }

        public void setTyp(String typ) {
            this.typ = typ;
        }

        @JsonProperty("accessTokenExpiredIn")
        public long getAccess_token_expired_in() {
            return access_token_expired_in;
        }

        public void setAccess_token_expired_in(long access_token_expired_in) {
            this.access_token_expired_in = access_token_expired_in;
        }

        @JsonProperty("refreshTokenExpiredIn")
        public long getRefresh_token_expired_in() {
            return refresh_token_expired_in;
        }

        public void setRefresh_token_expired_in(long refresh_token_expired_in) {
            this.refresh_token_expired_in = refresh_token_expired_in;
        }

        @JsonProperty("passwordUserExpiredAt")
        public long getPassword_user_expired_at() {
            return password_user_expired_at;
        }

        public void setPassword_user_expired_at(long password_user_expired_at) {
            this.password_user_expired_at = password_user_expired_at;
        }

        @JsonProperty("typeDate")
        public TypeDate getType_date() {
            return type_date;
        }

        public void setType_date(String type_date) {
            if (type_date.equals(TypeDate.Day.getName())) {
                this.type_date = TypeDate.Day;
            }
            if (type_date.equals(TypeDate.Hour.getName())) {
                this.type_date = TypeDate.Hour;
            }
            if (type_date.equals(TypeDate.Month.getName())) {
                this.type_date = TypeDate.Month;
            }
            if (type_date.equals(TypeDate.Second.getName())) {
                this.type_date = TypeDate.Second;
            }
        }
    }
}
