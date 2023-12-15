/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.policy.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import vn.mobileid.id.general.objects.Attributes;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PolicyPasswordRule extends Attributes {
    private int minLength;
    private int maxLength;
    private boolean onlyNumeric;
    private boolean mustContainNumeric;
    private boolean mustContainLowerCase;
    private boolean mustContainUpperCase;
    private boolean mustContainSpecialCharacter;

    public PolicyPasswordRule() {
    }

    @JsonProperty("minLength")
    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    @JsonProperty("maxLength")
    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    @JsonProperty("onlyNumeric")
    public boolean isOnlyNumeric() {
        return onlyNumeric;
    }

    public void setOnlyNumeric(boolean onlyNumeric) {
        this.onlyNumeric = onlyNumeric;
    }

    @JsonProperty("mustContainNumeric")
    public boolean isMustContainNumeric() {
        return mustContainNumeric;
    }

    public void setMustContainNumeric(boolean mustContainNumeric) {
        this.mustContainNumeric = mustContainNumeric;
    }

    @JsonProperty("mustContainLowercase")
    public boolean isMustContainLowerCase() {
        return mustContainLowerCase;
    }

    public void setMustContainLowerCase(boolean mustContainLowerCase) {
        this.mustContainLowerCase = mustContainLowerCase;
    }

    @JsonProperty("mustContainUppercase")
    public boolean isMustContainUpperCase() {
        return mustContainUpperCase;
    }

    public void setMustContainUpperCase(boolean mustContainUpperCase) {
        this.mustContainUpperCase = mustContainUpperCase;
    }

    @JsonProperty("mustContainSpecialCharater")
    public boolean isMustContainSpecialCharacter() {
        return mustContainSpecialCharacter;
    }

    public void setMustContainSpecialCharacter(boolean mustContainSpecialCharacter) {
        this.mustContainSpecialCharacter = mustContainSpecialCharacter;
    }

    
}
