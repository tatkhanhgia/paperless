/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general.api;

//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.QryptoService;

/**
 *
 * @author GiaTK
 */
//@WebServlet("/hello")
@Path("/")
public class ServicesController {

    final private static Logger LOG = LogManager.getLogger(ServicesController.class);

    @GET
    @Path("hello")
    public String hello() {
        return "hello";
    }

    // Test INFO
    @GET
    @Path("/info")
    public Response getInfo(@Context final HttpServletRequest request, String payload) {
        return Response.status(200).entity(QryptoService.getInfo()).build();
    }

    // AUTHENTICATE
    @POST
    @Path("/authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response AuthenticateJSON(@Context final HttpServletRequest request, String payload) {
        try {
            InternalResponse response = QryptoService.getToken(request, payload, 0);
            if (response.getStatus() == 200) {
                return Response.status(200).entity(response.getMessage()).build();
            } else {
                return Response.status(403).entity(response.getMessage()).build();
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error Parsing ObjectMapper");
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    @POST
    @Path("/authenticateForm")
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response AuthenticateForm(@Context final HttpServletRequest request, String payload) {
        try {
            System.out.println("ClientId:"+request.getParameter("client_id"));
            LOG.warn("ClientID:"+request.getParameter("client_id"));
            InternalResponse response = QryptoService.getToken(request, payload, 1);
            if (response.getStatus() == 200) {
                return Response.status(200).entity(response.getMessage()).build();
            } else {
                return Response.status(403).entity(response.getMessage()).build();
            }
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error Parsing ObjectMapper");
            }
            return Response.status(500).entity("Internal Server Error").build();
        }
    }

    // REVOKE TOKEN
    @DELETE
    @Path("/tokens")
    @Produces(MediaType.APPLICATION_JSON)
    public Response RevokeToken(@Context final HttpServletRequest request, String payload) {
        return Response.status(200).entity(QryptoService.getInfo()).build();
    }

    // WORKFLOW
//    // Deprecated in the furture
//    @POST
//    @Path("/v1/e-identity/oidc/token")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1Token(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.getToken(request, payload, IdentityFunction.F_OIDC_TOKEN)).build();
//    }
//
//    @GET
//    @Path("/v1/e-identity/certificates")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1Certificates(String payload) {
//        return Response.status(200).entity(IdentityServices.getIdXCertificates(null, payload)).build();
//    }
//
//    @GET
//    @Path("/v1/e-identity/certificates/{keyID}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1Certificates2(@PathParam("keyID") String keyID, String payload) {
//        return Response.status(200).entity(IdentityServices.getIdXCertificates(keyID, payload)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/subjects/create")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1SubjectCreate(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.createSubject(request, payload, IdentityFunction.F_SUBJECTS_CREATE)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/subjects/get")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1SubjectGet(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.getSubject(request, payload, IdentityFunction.F_SUBJECTS_GET)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/subjects/update")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1SubjectUpdate(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.updateSubject(request, payload, IdentityFunction.F_SUBJECTS_UPDATE)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/processes/create")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1ProcessCreate(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.createProcess(request, payload, IdentityFunction.F_PROCESSES_CREATE)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/processes/get")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1ProcessGet(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.getProcess(request, payload, IdentityFunction.F_PROCESSES_GET)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/images/upload")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1ImageUpload(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.uploadImage(request, payload, IdentityFunction.F_IMAGES_UPLOAD)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/images/download")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1ImageDownload(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.downloadImage(request, payload, IdentityFunction.F_IMAGES_DOWNLOAD)).build();
//    }
//
//    @GET
//    @Path("/v1/e-identity/images/download/{identityUUID}")
//    @Produces({"image/png", "image/jpg"})
//    public Response IdentityV1ImageGet(@PathParam("identityUUID") String identityUUID, String payload) {
//        StreamingOutput stream = new StreamingOutput() {
//            @Override
//            public void write(OutputStream os) throws IOException, WebApplicationException {
//                byte[] image = IdentityServices.getImage(identityUUID, payload);
//                os.write(image);
//                os.flush();
//            }
//        };
//        return Response.ok(stream).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/verification")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1Verification(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.performVerification(request, payload, IdentityFunction.F_VERIFICATION)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/processes/otpregeneration")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1OtpRegeneration(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.regenerateOtp(request, payload, IdentityFunction.F_PROCESSES_OTP_REGENERATION)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/processes/self-revise")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1SelfRevise(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.selfRevise(request, payload, IdentityFunction.F_PROCESSES_SELF_REVISE)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/owners/create")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1OwnerCreate(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.createOwner(request, payload, IdentityFunction.F_OWNER_CREATE)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/{livematching : (?i)livematching}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1LiveMatching(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.liveMatching(request, payload, IdentityFunction.F_LIVEMATCHING)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/{facialfeaturearea : (?i)facialfeaturearea}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1FacialFeatureArea(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.facialFeatureArea(request, payload, IdentityFunction.F_FACIAL_FEATURE_AREA)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/{passiveauthentication : (?i)passiveauthentication}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1PassiveAuthentication(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.passiveAuthentication(request, payload, IdentityFunction.F_PASSIVE_AUTHENTICATION)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/{fingerprintverification : (?i)fingerprintverification}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1FingerVerification(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.fingerprintVerification(request, payload, IdentityFunction.F_FINGERPRINT_VERIFICATION)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/{liveness : (?i)liveness}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1Liveness(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.liveness(request, payload, IdentityFunction.F_LIVENESS)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/{facematch : (?i)facematch}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1FaceMatch(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.facematch(request, payload, IdentityFunction.F_FACEMATCH)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/{enroll : (?i)enroll}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1Enroll(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.enroll(request, payload, IdentityFunction.F_ENROLL)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/idcard/upload")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1IDCardUpload(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.uploadIDCardInfo(request, payload, IdentityFunction.F_IDCARD_UPLOAD)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/ta/certificates")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1TACertificates(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.getTACertificates(request, payload, IdentityFunction.F_TA_CERTIFICATES)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/ta/signature")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1TASignature(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.getTASignature(request, payload, IdentityFunction.F_TA_SIGNATURE)).build();
//    }
//
//    @POST
//    @Path("/v1/e-identity/ocr/info")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response IdentityV1OCRInfo(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(IdentityServices.getOCRInfo(request, payload, IdentityFunction.F_OCR_INFO)).build();
//    }
//
//    //********************************************************************************************************
//    // VERIFICATION
//    @POST
//    @Path("/v1/e-verification/oidc/token")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1Token(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(VerificationServices.getToken(request, payload, VerificationFunction.F_OIDC_TOKEN)).build();
//    }
//
//    @POST
//    @Path("/v1/e-verification/cades")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1CAdES(@Context final HttpServletRequest request, String payload) {
//        return Response.status(200).entity(VerificationServices.verifyCAdES(request, payload, VerificationFunction.F_CADES)).build();
//    }
//
//    @POST
//    @Path("/v1/e-verification/pades")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1PAdES(
//            @Context final HttpServletRequest request,
//            String payload) {
//        return Response.status(200).entity(VerificationServices.verifyPAdES(
//                request, payload, VerificationFunction.F_PADES)).build();
//    }
//
//    @POST
//    @Path("/v1.1/e-verification/pades")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    public Response VerificationV11PAdES(@Context HttpServletRequest request) {
//        byte[] document = null;
//        String jsonReq = null;
//        List<FormDataBodyItem> formDataBodyItemList = new ArrayList<>();
//        if (ServletFileUpload.isMultipartContent(request)) {
//            FileItemFactory factory = new DiskFileItemFactory();
//            ServletFileUpload upload = new ServletFileUpload(factory);
//            List<FileItem> items = null;
//            try {
//                items = upload.parseRequest(request);
//            } catch (FileUploadException e) {
//                e.printStackTrace();
//                if (LogAndCacheManager.isShowErrorLog()) {
//                    LOG.error("Error while parsing request. Details: " + Utils.printStackTrace(e));
//                }
//            }
//            if (items != null) {
//                Iterator<FileItem> iter = items.iterator();
//                while (iter.hasNext()) {
//                    FileItem item = iter.next();
//                    if (!item.isFormField() && item.getSize() > 0) {
//                        if (item.getFieldName().compareToIgnoreCase("document") == 0) {
//                            try {
//                                document = Utils.saveByteArrayOutputStream(item.getInputStream());
//                                if (document != null) {
//                                    FormDataBodyItem formDataBodyItem = new FormDataBodyItem();
//                                    formDataBodyItem.setName("document");
//                                    formDataBodyItem.setContentType(item.getContentType());
//                                    formDataBodyItem.setFileName(item.getName());
//                                    formDataBodyItem.setSha256Content(
//                                            DatatypeConverter.printHexBinary(
//                                                    Crypto.hashData(document, Crypto.HASH_SHA256)).toLowerCase());
//                                    formDataBodyItemList.add(formDataBodyItem);
//                                }
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                if (LogAndCacheManager.isShowErrorLog()) {
//                                    LOG.error("Error while getting document. Details: " + Utils.printStackTrace(e));
//                                }
//                            }
//                        }
//                    } else {
//                        //System.out.println("getFieldName:" + item.getFieldName());
//                        //System.out.println(item.getString());
//                        if (item.getFieldName().compareToIgnoreCase("payload") == 0) {
//                            jsonReq = item.getString().trim();
//                            if (jsonReq != null) {
//                                FormDataBodyItem formDataBodyItem = new FormDataBodyItem();
//                                formDataBodyItem.setName("payload");
//                                try {
//                                    formDataBodyItem.setSha256Content(
//                                            DatatypeConverter.printHexBinary(
//                                                    Crypto.hashData(jsonReq.getBytes("UTF-8"), Crypto.HASH_SHA256)).toLowerCase());
//                                } catch (UnsupportedEncodingException ex) {
//
//                                }
//                                formDataBodyItemList.add(formDataBodyItem);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return Response.status(200).entity(VerificationServices.verifyPAdESV1_1(
//                request, document, jsonReq, VerificationFunction.F_PADES, formDataBodyItemList)).header("Content-Type", MediaType.APPLICATION_JSON).build();
//    }
//
//    @POST
//    @Path("/v1/e-verification/xades")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1XAdES(@Context
//            final HttpServletRequest request, String payload
//    ) {
//        return Response.status(200).entity(VerificationServices.verifyXAdES(request, payload, VerificationFunction.F_XADES)).build();
//    }
//
//    @POST
//    @Path("/v1.1/e-verification/xades")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    public Response VerificationV11XAdES(@Context HttpServletRequest request) {
//        byte[] document = null;
//        String jsonReq = null;
//        List<FormDataBodyItem> formDataBodyItemList = new ArrayList<>();
//        if (ServletFileUpload.isMultipartContent(request)) {
//            FileItemFactory factory = new DiskFileItemFactory();
//            ServletFileUpload upload = new ServletFileUpload(factory);
//            List<FileItem> items = null;
//            try {
//                items = upload.parseRequest(request);
//            } catch (FileUploadException e) {
//                e.printStackTrace();
//                if (LogAndCacheManager.isShowErrorLog()) {
//                    LOG.error("Error while parsing request. Details: " + Utils.printStackTrace(e));
//                }
//            }
//            if (items != null) {
//                Iterator<FileItem> iter = items.iterator();
//                while (iter.hasNext()) {
//                    FileItem item = iter.next();
//                    if (!item.isFormField() && item.getSize() > 0) {
//                        if (item.getFieldName().compareToIgnoreCase("document") == 0) {
//                            try {
//                                document = Utils.saveByteArrayOutputStream(item.getInputStream());
//                                if (document != null) {
//                                    FormDataBodyItem formDataBodyItem = new FormDataBodyItem();
//                                    formDataBodyItem.setName("document");
//                                    formDataBodyItem.setContentType(item.getContentType());
//                                    formDataBodyItem.setFileName(item.getName());
//                                    formDataBodyItem.setSha256Content(
//                                            DatatypeConverter.printHexBinary(
//                                                    Crypto.hashData(document, Crypto.HASH_SHA256)).toLowerCase());
//                                    formDataBodyItemList.add(formDataBodyItem);
//                                }
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                if (LogAndCacheManager.isShowErrorLog()) {
//                                    LOG.error("Error while getting document. Details: " + Utils.printStackTrace(e));
//                                }
//                            }
//                        }
//                    } else {
//                        //System.out.println("getFieldName:" + item.getFieldName());
//                        //System.out.println(item.getString());
//                        if (item.getFieldName().compareToIgnoreCase("payload") == 0) {
//                            jsonReq = item.getString();
//                            if (jsonReq != null) {
//                                FormDataBodyItem formDataBodyItem = new FormDataBodyItem();
//                                formDataBodyItem.setName("payload");
//                                formDataBodyItem.setSha256Content(
//                                        DatatypeConverter.printHexBinary(
//                                                Crypto.hashData(jsonReq.getBytes(), Crypto.HASH_SHA256)).toLowerCase());
//                                formDataBodyItemList.add(formDataBodyItem);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return Response.status(200).entity(VerificationServices.verifyXAdESV1_1(
//                request, document, jsonReq, VerificationFunction.F_XADES, formDataBodyItemList)).header("Content-Type", MediaType.APPLICATION_JSON).build();
//    }
//
//    @POST
//    @Path("/v1/e-verification/office")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1Office(@Context
//            final HttpServletRequest request, String payload
//    ) {
//        return Response.status(200).entity(VerificationServices.verifyOffice(request, payload, VerificationFunction.F_OFFICE)).build();
//    }
//
//    @POST
//    @Path("/v1.1/e-verification/office")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    public Response VerificationV11Office(@Context final HttpServletRequest request) {
//        byte[] document = null;
//        String jsonReq = null;
//        List<FormDataBodyItem> formDataBodyItemList = new ArrayList<>();
//        if (ServletFileUpload.isMultipartContent(request)) {
//            FileItemFactory factory = new DiskFileItemFactory();
//            ServletFileUpload upload = new ServletFileUpload(factory);
//            List<FileItem> items = null;
//            try {
//                items = upload.parseRequest(request);
//            } catch (FileUploadException e) {
//                e.printStackTrace();
//                if (LogAndCacheManager.isShowErrorLog()) {
//                    LOG.error("Error while parsing request. Details: " + Utils.printStackTrace(e));
//                }
//            }
//            if (items != null) {
//                Iterator<FileItem> iter = items.iterator();
//                while (iter.hasNext()) {
//                    FileItem item = iter.next();
//                    if (!item.isFormField() && item.getSize() > 0) {
//                        if (item.getFieldName().compareToIgnoreCase("document") == 0) {
//                            try {
//                                document = Utils.saveByteArrayOutputStream(item.getInputStream());
//                                if (document != null) {
//                                    FormDataBodyItem formDataBodyItem = new FormDataBodyItem();
//                                    formDataBodyItem.setName("document");
//                                    formDataBodyItem.setContentType(item.getContentType());
//                                    formDataBodyItem.setFileName(item.getName());
//                                    formDataBodyItem.setSha256Content(
//                                            DatatypeConverter.printHexBinary(
//                                                    Crypto.hashData(document, Crypto.HASH_SHA256)).toLowerCase());
//                                    formDataBodyItemList.add(formDataBodyItem);
//                                }
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                if (LogAndCacheManager.isShowErrorLog()) {
//                                    LOG.error("Error while getting document. Details: " + Utils.printStackTrace(e));
//                                }
//                            }
//                        }
//                    } else {
//                        //System.out.println("getFieldName:" + item.getFieldName());
//                        //System.out.println(item.getString());
//                        if (item.getFieldName().compareToIgnoreCase("payload") == 0) {
//                            jsonReq = item.getString();
//                            if (jsonReq != null) {
//                                FormDataBodyItem formDataBodyItem = new FormDataBodyItem();
//                                formDataBodyItem.setName("payload");
//                                formDataBodyItem.setSha256Content(
//                                        DatatypeConverter.printHexBinary(
//                                                Crypto.hashData(jsonReq.getBytes(), Crypto.HASH_SHA256)).toLowerCase());
//                                formDataBodyItemList.add(formDataBodyItem);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return Response.status(200).entity(VerificationServices.verifyOfficeV1_1(
//                request, document, jsonReq, VerificationFunction.F_OFFICE, formDataBodyItemList))
//                .header("Content-Type", MediaType.APPLICATION_JSON).build();
//    }
//
//    @POST
//    @Path("/v1/e-verification/otp/request")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1OTPRequest(@Context
//            final HttpServletRequest request, String payload
//    ) {
//        return Response.status(200).entity(VerificationServices.requestOTP(request, payload, VerificationFunction.F_OTP_REQUEST)).build();
//    }
//
//    @POST
//    @Path("/v1/e-verification/otp/verify")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1OTPVerify(@Context
//            final HttpServletRequest request, String payload
//    ) {
//        return Response.status(200).entity(VerificationServices.verifyOTP(request, payload, VerificationFunction.F_OTP_VERIFY)).build();
//    }
//
//    @POST
//    @Path("/v1/e-verification/eid/verify")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1EIDVerify(@Context
//            final HttpServletRequest request, String payload
//    ) {
//        return Response.status(200).entity(VerificationServices.verifyEID(request, payload, VerificationFunction.F_EID_VERIFY)).build();
//    }
//
//    @POST
//    @Path("/v1/owner/create")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1OwnerCreate(@Context
//            final HttpServletRequest request, String payload
//    ) {
//        return Response.status(200).entity(VerificationServices.createOwner(request, payload, VerificationFunction.F_CREATE_OWNER)).build();
//    }
//
//    @POST
//    @Path("/v1/owner/get")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1OwnerGet(@Context
//            final HttpServletRequest request, String payload
//    ) {
//        return Response.status(200).entity(VerificationServices.getOwner(request, payload, VerificationFunction.F_GET_OWNER)).build();
//    }
//
//    @POST
//    @Path("/v1/owner/update")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1OwnerUpdate(@Context
//            final HttpServletRequest request, String payload
//    ) {
//        return Response.status(200).entity(VerificationServices.updateOwner(request, payload, VerificationFunction.F_UPDATE_OWNER)).build();
//    }
//
//    @POST
//    @Path("/v1/owner/challenge")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1OwnerChallenge(@Context
//            final HttpServletRequest request, String payload
//    ) {
//        return Response.status(200).entity(VerificationServices.getChallenge(request, payload, VerificationFunction.F_GET_CHALLENGE)).build();
//    }
//
//    @POST
//    @Path("/v1/owner/certificate/register")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1OwnerCertifcateRegistration(@Context
//            final HttpServletRequest request, String payload
//    ) {
//        return Response.status(200).entity(VerificationServices.registerOwnerCertificate(request, payload, VerificationFunction.F_REGISTER_CERTIFICATE)).build();
//    }
//
//    @POST
//    @Path("/v1/owner/certificate/deregister")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1OwnerCertifcateDeregistration(@Context
//            final HttpServletRequest request, String payload
//    ) {
//        return Response.status(200).entity(VerificationServices.deregisterOwnerCertificate(request, payload, VerificationFunction.F_DEREGISTER_CERTIFICATE)).build();
//    }
//
//    @POST
//    @Path("/v1/agreement/create")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1AgreementCreate(@Context
//            final HttpServletRequest request, String payload
//    ) {
//        return Response.status(200).entity(VerificationServices.createAgreement(request, payload, VerificationFunction.F_CREATE_AGREEMENT)).build();
//    }
//
//    @POST
//    @Path("/v1/agreement/assign")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1AgreementAssign(@Context
//            final HttpServletRequest request, String payload
//    ) {
//        return Response.status(200).entity(VerificationServices.assignAgreement(request, payload, VerificationFunction.F_ASSIGN_AGREEMENT)).build();
//    }
//
//    @POST
//    @Path("/v1/agreement/get")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response VerificationV1AgreementGet(@Context
//            final HttpServletRequest request, String payload
//    ) {
//        return Response.status(200).entity(VerificationServices.getAgreement(request, payload, VerificationFunction.F_GET_AGREEMENT)).build();
//    }
//
//    @GET
//    @Path("/v1/management/reload/{tableID}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response ManagementV1Reload(
//            @Context
//            final HttpServletRequest request,
//            @PathParam("tableID") String tableID
//    ) {
//        Management.reload(request, tableID);
//        return Response.status(200).build();
//    }
//
//    @POST
//    @Path("/v1/management/licensing/import")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response ManagementV1LicensingImport(@Context
//            final HttpServletRequest request, String payload
//    ) {
//        Management.importLicense(request, payload);
//        return Response.status(200).build();
//    }
//    //********************************************************************************************************
//    // TSA
//    private static final String CHALLENGE_FORMAT = "%s realm=\"%s\"";
//
//    @POST
//    @Path("/v1/tsa")
//    @Produces("application/timestamp-query")
//    public Response TSAV1Request(@Context final HttpServletRequest request) {
//        TimeStampingAuthority tsa = new TimeStampingAuthority(request);
//        final TSAResponse tsaResponse = tsa.process();
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Database db = new DatabaseImpl();
//                DatabaseResponse dr = db.getTSALogId();
//                db.insertTSALog(
//                        dr.getTsaLogId(),
//                        String.valueOf(tsaResponse.getStatus().getStatusCode()),
//                        tsaResponse.getStatus().getReasonPhrase() + " (" + tsaResponse.getDescription() + ")",
//                        tsaResponse.getUsername(),
//                        tsaResponse.getTsaUserID(),
//                        Utils.getRequestHeader(request, "User-Agent"),
//                        tsaResponse.getTsaProfileID(),
//                        request.getRemoteAddr(),
//                        dr.getTsaLogDt(),
//                        dr.getTsaLogDt());
//            }
//        });
//        t.start();
//        if (tsaResponse.getStatus().getStatusCode() == 200) {
//            return Response.status(tsaResponse.getStatus().getStatusCode()).entity(tsaResponse.getResponseData()).build();
//        } else if (tsaResponse.getStatus().getStatusCode() == 401) {
//            return Response.status(Response.Status.UNAUTHORIZED)
//                    .header(HttpHeaders.WWW_AUTHENTICATE, String.format(CHALLENGE_FORMAT, "Basic", "Access"))
//                    .type(MediaType.TEXT_PLAIN_TYPE)
//                    .entity("Credentials are required to access this resource.")
//                    .build();
//        } else {
//            return Response.status(tsaResponse.getStatus().getStatusCode())
//                    .entity(tsaResponse.getStatus().getReasonPhrase())
//                    .build();
//        }
//    }
}
