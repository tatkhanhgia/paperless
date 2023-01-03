/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author ADMIN
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseMessageJSNObject {

    // general
    private int status;
    private String message;
    private String description;
    private String transactionID;
    //-------------------------------------------------------------
    //token
    private String accessToken;
    private String tokenType;
    private Integer expiresIn;
    //end-token
    //-------------------------------------------------------------
    //subject
    private String subjectUUID;
    private String assuranceLevel;
    private String name;
    private String gender;
    private String email;
    private Boolean emailVerified;
    private String mobile;
    private Boolean mobileVerified;
    private String dob;
    private String pob;
    private String nationality;
    //end-subject
    //-------------------------------------------------------------
    //process
    private String processUUID;
    private String processStatus;
    private Date createAt;
    private Date updateAt;
    private String authorization;
    private String provider;
    private String challenge;
    private String processType;
//    private VerificationResultJSNObject verificationResult;
    private HashMap<String, String> providerParameters;
    //end-process

    // download image
    private String image;
    private String documentType;

    // otp validation
    private Integer retriesLeft;

    private String ownerUUID;
    private Long ownerIndex;


}
