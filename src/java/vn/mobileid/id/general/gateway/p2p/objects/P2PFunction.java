/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.gateway.p2p.objects;

/**
 *
 * @author VUDP
 */
public class P2PFunction {

    final public static String SEND_SMS = "SEND_SMS";
    final public static String SEND_EMAIL = "SEND_EMAIL";
    final public static String UPLOAD_FILE = "UPLOAD_FILE";
    final public static String DOWNLOAD_FILE = "DOWNLOAD_FILE";
    final public static String ENROLL_CERTIFICATE = "ENROLL_CERTIFICATE";
    final public static String REVOKE_CERTIFICATE = "REVOKE_CERTIFICATE";
    final public static String SIGN_TSA = "SIGN_TSA";
    final public static String MANAGE_FOAS = "MANAGE_FOAS";
    final public static String AUTHORIZE_FOAS = "AUTHORIZE_FOAS";
    final public static String DOWNLOAD_CRL = "DOWNLOAD_CRL";
    final public static String CHECK_OCSP = "CHECK_OCSP";
    final public static String DECLINE_CERTIFICATE = "DECLINE_CERTIFICATE";
    final public static String APPROVE_CERTIFICATE = "APPROVE_CERTIFICATE";
    final public static String PUSH_ENROLL_RESULT = "PUSH_ENROLL_RESULT";
    final public static String OCR = "OCR";
    final public static String GETSESSION = "GET_SESSION";
    final public static String LIVENESS = "LIVENESS";
    final public static String MATCHING_3D_2D = "MATCHING_3D_2D";
    final public static String IAM_GET_TOKEN = "GET_SERVICE_TOKEN";
    final public static String IAM_CREATE_USER = "REGISTRATION_USER";
    final public static String IAM_GET_USER = "GET_USER_INFO";
    final public static String VERIFY_FINGERPRINT = "VERIFY_FINGERPRINT";

    private int p2pFunctionID;
    private String name;

    public int getP2pFunctionID() {
        return p2pFunctionID;
    }

    public void setP2pFunctionID(int p2pFunctionID) {
        this.p2pFunctionID = p2pFunctionID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
