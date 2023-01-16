/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.ResponseCode;


/**
 *
 * @author ADMIN
 */
public class Resources {

    private static volatile Logger LOG = LogManager.getLogger(Resources.class);        

    private static volatile HashMap<String, ResponseCode> responseCodes = new HashMap<>();     
        
    public static synchronized void init() {
        Database db = new DatabaseImpl();

        if (responseCodes.isEmpty()) {
            List<ResponseCode> listOfResponseCode = db.getResponseCodes();
            for (ResponseCode responseCode : listOfResponseCode) {
                responseCodes.put(responseCode.getName(), responseCode);   
//                System.out.println("Name:"+responseCode.getName());
            }
        }

        if (LogHandler.isShowInfoLog()) {
            LOG.info("Service is started up and ready to use!");
        }
    }
//
//    //--------------------------------------------------------------------------
//    public static void reloadIDXCertificates() {
////        synchronized (Resources.class) {
////            Database db = new DatabaseImpl();
////            HashMap<String, IDXCertificate> newIdxCertificates = new HashMap<>();
////            List<IDXCertificate> listOfIdxCertificate = db.getIDXCertificates();
////            for (IDXCertificate idxCertificate : listOfIdxCertificate) {
////                newIdxCertificates.put(idxCertificate.getName(), idxCertificate);
////            }
////            idxCertificates = newIdxCertificates;
////        }
//    }
//
//    public static void reloadRelyingParties() {
////        synchronized (Resources.class) {
////            HashMap<String, RelyingParty> newRelyingParties = new HashMap<>();
////            HashMap<Integer, RelyingParty> newIdRelyingParties = new HashMap<>();
////            Database db = new DatabaseImpl();
////            List<RelyingParty> listOfRelyingParty = db.getRelyingParties();
////            for (RelyingParty relyingParty : listOfRelyingParty) {
////                newRelyingParties.put(relyingParty.getName(), relyingParty);
////                newIdRelyingParties.put(relyingParty.getId(), relyingParty);
////            }
////            relyingParties = newRelyingParties;
////            idRelyingParties = newIdRelyingParties;
////        }
//    }
//
//    public static void reloadIdentityDocumentTypes() {
////        Database db = new DatabaseImpl();
////        List<IdentityDocumentType> listOfIdentityDocumentType = db.getIdentityDocumentTypes();
////        for (IdentityDocumentType identityDocumentType : listOfIdentityDocumentType) {
////            identityDocumentTypes.put(identityDocumentType.getName(), identityDocumentType);
////        }
//    }
//
//    public static void reloadProcessTypes() {
////        Database db = new DatabaseImpl();
////        List<IdentityProcessType> listOfProcessType = db.getProcessTypes();
////        for (IdentityProcessType processType : listOfProcessType) {
////            processTypes.put(processType.getName(), processType);
////            idProcessTypes.put(processType.getId(), processType);
////        }
//    }
//
//    public static void reloadIdentityProcessStatuses() {
//        Database db = new DatabaseImpl();
//        List<IdentityProcessStatus> listOfIdentityProcessStatus = db.getIdentityProcessStatus();
//        for (IdentityProcessStatus identityProcessStatus : listOfIdentityProcessStatus) {
//            identityProcessStatuses.put(identityProcessStatus.getId(), identityProcessStatus);
//        }
//    }
//
//    public static void reloadIdentityProviders() {
//        Database db = new DatabaseImpl();
//        List<IdentityProvider> listOfIdentityProvider = db.getIdentityProviders();
//        for (IdentityProvider identityProvider : listOfIdentityProvider) {
//            identityProviders.put(identityProvider.getName(), identityProvider);
//            idIdentityProviders.put(identityProvider.getId(), identityProvider);
//        }
//    }
//
    public static void reloadResponseCodes() {
        Database db = new DatabaseImpl();
        List<ResponseCode> listOfResponseCode = db.getResponseCodes();
        for (ResponseCode responseCode : listOfResponseCode) {
            String cal = responseCode.getName() + "." + responseCode.getCode();
            responseCodes.put(cal, responseCode);
        }
    }
//
//    public static void reloadCertificationAuthorities() {
//        Database db = new DatabaseImpl();
//        HashMap<Integer, CertificationAuthority> newCertificationAuthorities = new HashMap<>();
//        HashMap<String, CertificationAuthority> newCertificationAuthoritiesKeyIdentifiers = new HashMap<>();
//        List<CertificationAuthority> newListOfCertificationAuthority = new ArrayList<>();
//
//        List<CertificationAuthority> listOfCA = db.getCertificationAuthorities();
//        for (CertificationAuthority certificationAuthority : listOfCA) {
//            newCertificationAuthorities.put(certificationAuthority.getCertificationAuthorityID(), certificationAuthority);
//            newCertificationAuthoritiesKeyIdentifiers.put(certificationAuthority.getSubjectKeyIdentifier(), certificationAuthority);
//            newListOfCertificationAuthority.add(certificationAuthority);
//        }
//        certificationAuthorities = newCertificationAuthorities;
//        certificationAuthoritiesKeyIdentifiers = newCertificationAuthoritiesKeyIdentifiers;
//        listOfCertificationAuthority = newListOfCertificationAuthority;
//    }
//
////    public static void reloadRegistrationParty() {
////        Database db = new DatabaseImpl();
////        List<RegistrationParty> listOfRegistrationParty = db.getRegistrationParties();
////        for (RegistrationParty registrationParty : listOfRegistrationParty) {
////            registrationParties.put(registrationParty.getRegistrationPartyID(), registrationParty);
////        }
////    }
////
////    public static void reloadTSAProfiles() {
////        Database db = new DatabaseImpl();
////        List<TSAProfile> listOfTSAProfile = db.getTSAProfiles();
////        for (TSAProfile tsaProfile : listOfTSAProfile) {
////            tsaProfiles.put(tsaProfile.getId(), tsaProfile);
////        }
////    }

  

//    public static HashMap<String, IDXCertificate> getIdxCertificates() {
//        return idxCertificates;
//    }
//
//    public static HashMap<String, RelyingParty> getRelyingParties() {
//        return relyingParties;
//    }

    public static HashMap<String, ResponseCode> getResponseCodes() {
        return responseCodes;
    }

//    public static HashMap<String, IdentityDocumentType> getIdentityDocumentTypes() {
//        return identityDocumentTypes;
//    }
//
//    public static HashMap<String, IdentityProcessType> getProcessTypes() {
//        return processTypes;
//    }
//
//    public static HashMap<Integer, IdentityProcessType> getIdProcessTypes() {
//        return idProcessTypes;
//    }
//
//    public static HashMap<Integer, RelyingParty> getIdRelyingParties() {
//        return idRelyingParties;
//    }

   

//    public static HashMap<String, P2P> getP2ps() {
//        return p2ps;
//    }
//
//    public static List<P2PFunction> getP2pFunctions() {
//        return p2pFunctions;
//    }
//
//    public static P2PFunction getP2PFunction(String functionName) {
//        for (P2PFunction p2pFunction : p2pFunctions) {
//            if (p2pFunction.getName().compareTo(functionName) == 0) {
//                return p2pFunction;
//            }
//        }
//        return null;
//    }
//
//    public static HashMap<Integer, IdentityProcessStatus> getIdentityProcessStatuses() {
//        return identityProcessStatuses;
//    }
//
//    public static HashMap<String, IdentityProvider> getIdentityProviders() {
//        return identityProviders;
//    }
//
//    public static HashMap<Integer, IdentityProvider> getIdIdentityProviders() {
//        return idIdentityProviders;
//    }
//
//    public static Province getProvince() {
//        if (province.getEnglishTable().isEmpty()
//                || province.getVietnameseTable().isEmpty()
//                || province.getNameToRemark().isEmpty()) {
//            Database db = new DatabaseImpl();
//            province = db.getProvince();
//        }
//        return province;
//    }
//
//    public static HashMap<Integer, CertificationAuthority> getCertificationAuthorities() {
//        return certificationAuthorities;
//    }
//
//    public static HashMap<String, CertificationAuthority> getCertificationAuthoritiesKeyIdentifiers() {
//        return certificationAuthoritiesKeyIdentifiers;
//    }
//
//    public static List<CertificationAuthority> getListOfCertificationAuthority() {
//        return listOfCertificationAuthority;
//    }
//
//    public static HashMap<Integer, RegistrationParty> getRegistrationParties() {
//        return registrationParties;
//    }
//
//    public static HashMap<Integer, TSAProfile> getTsaProfiles() {
//        return tsaProfiles;
//    }

}
