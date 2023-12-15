/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class CheckPasswordRule {

    //<editor-fold defaultstate="collapsed" desc="Check Password">
    public static InternalResponse checkComplexOfPassword(String password) {
        //Check Min Len
        if (!checkMinLength(password)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PASSWORD,
                            PaperlessConstant.SUBCODE_LENGTH_OF_PASSWORD_TOO_SHORT,
                            "en",
                            password)
            );
        }

        //Check Max Len
        if (!checkMaxLength(password)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PASSWORD,
                            PaperlessConstant.SUBCODE_LENGTH_OF_PASSWORD_TOO_LONG,
                            "en",
                            password)
            );
        }

        //Check complex of Password
        if (PolicyConfiguration.getInstant().getPasswordRule().isOnlyNumeric()) {
            if (checkNumeric(password)) {
                return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, "");
            } else {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_INVALID_PASSWORD,
                                PaperlessConstant.SUBCODE_PASSWORD_MUST_ONLY_NUMERIC,
                                "en",
                                password)
                );
            }
        }

        if (PolicyConfiguration.getInstant().getPasswordRule().isMustContainLowerCase()) {
            if (!checkLowerCase(password)) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_INVALID_PASSWORD,
                                PaperlessConstant.SUBCODE_PASSWORD_NEEDS_CONTAIN_LOWERCASE,
                                "en",
                                password)
                );
            }
        }

        if (PolicyConfiguration.getInstant().getPasswordRule().isMustContainUpperCase()) {
            if (!checkUpperCase(password)) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_INVALID_PASSWORD,
                                PaperlessConstant.SUBCODE_PASSWORD_NEEDS_CONTAIN_UPPERCASE,
                                "en",
                                password)
                );
            }
        }

        if (PolicyConfiguration.getInstant().getPasswordRule().isMustContainSpecialCharacter()) {
            if (!checkSpecialChar(password)) {
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_INVALID_PASSWORD,
                                PaperlessConstant.SUBCODE_PASSWORD_NEEDS_CONTAIN_SPECIALCHAR,
                                "en",
                                password)
                );
            }

            if (PolicyConfiguration.getInstant().getPasswordRule().isMustContainNumeric()) {
                if (!checkContainsNumeric(password)) {
                    return new InternalResponse(
                            PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                            PaperlessMessageResponse.getErrorMessage(
                                    PaperlessConstant.CODE_INVALID_PASSWORD,
                                    PaperlessConstant.SUBCODE_PASSWORD_NEEDS_CONTAIN_NUMERIC,
                                    "en",
                                    password)
                    );
                }
            }
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }

    //</editor-fold>
    //==========================================================================
    //<editor-fold defaultstate="collapsed" desc="Check length of password">
    private static boolean checkMinLength(String password) {
        try {
            int len = password.length();
            int min = PolicyConfiguration.getInstant().getPasswordRule().getMinLength();
            return (len >= min);
        } catch (Exception ex) {
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check length of password">
    private static boolean checkMaxLength(String password) {
        try {
            int len = password.length();
            int max = PolicyConfiguration.getInstant().getPasswordRule().getMaxLength();
            return (len <= max);
        } catch (Exception ex) {
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check is Numeric">
    private static boolean checkNumeric(String password) {
        String regex = "^[0-9]*$";
        return password.matches(regex);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check is LowerCase">
    private static boolean checkLowerCase(String password) {
        return password.matches(".*[a-z]+.*");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check is UpperCase">
    private static boolean checkUpperCase(String password) {
        return password.matches(".*[A-Z].*");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check is Special Character">
    private static boolean checkSpecialChar(String password) {
        return password.matches("^.*[!@#$%^&*-.()_;:{},<>]+.*$");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check contains Numeric">
    private static boolean checkContainsNumeric(String password) {
        String regex = "^.*[0-9].*$";
        return password.matches(regex);
    }
    //</editor-fold>

    public static void main(String[] args) {
        String password = "heh2ehADAưefw123123e";
        System.out.println(password.matches("^.*[0-9].*$"));

    }
}
