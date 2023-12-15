/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless;

import RestfulFactory.Model.CertificateDetails;
import RestfulFactory.Model.Identification;
import RestfulFactory.SessionFactory;
import SignFile.IPdfSignFile;
import SignFile.SignFileFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.pdf.BaseFont;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import restful.sdk.API.APIException;
import restful.sdk.API.ICertificate;
import restful.sdk.API.IServerSession;
import restful.sdk.API.ISessionFactory;
import restful.sdk.API.IUserSession;
import restful.sdk.API.Property;
import restful.sdk.API.SigningMethodAsyncImp;
import restful.sdk.API.Types;
import restful.sdk.API.Types.AuthMode;
import restful.sdk.API.Types.IdentificationType;
import restful.sdk.API.Types.SharedMode;
import vn.mobileid.exsig.Algorithm;
import vn.mobileid.exsig.Color;
import vn.mobileid.exsig.ImageAlgin;
import vn.mobileid.exsig.ImageGenerator;
import vn.mobileid.exsig.ImageProfile;
import vn.mobileid.exsig.PdfForm;
import vn.mobileid.exsig.PdfProfile;
import vn.mobileid.exsig.PdfProfileCMS;
import vn.mobileid.exsig.TextAlignment;
import vn.mobileid.id.eid.object.JWT_Authenticate;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.policy.object.SignatureProperties;
import vn.mobileid.id.paperless.objects.Enterprise;
import vn.mobileid.id.paperless.objects.FrameSignatureProperties;
import vn.mobileid.id.general.annotation.AnnotationJWT;
import vn.mobileid.id.paperless.kernel_v2.GetEnterpriseInfo;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.objects.DataSignatureProperties;
import vn.mobileid.id.paperless.objects.Enterprise_SigningInfo;
import vn.mobileid.id.paperless.objects.Enterprise_SigningInfo.TemplateSignature;
import vn.mobileid.id.paperless.objects.SignaturePositionProperties;
import vn.mobileid.id.paperless.exception.CannotFindDataSignatureException;
import vn.mobileid.id.paperless.kernel_v2.GetEnterpriseSigningInfo;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class SigningService {

    private int enterprise_id_instant;

    private Property prop;

    private ISessionFactory sessionFactory;
    private IServerSession session;

    private static HashMap<String, IUserSession> listUserSession;
    private static SigningService signingService;

    public static SigningService getInstant(int i) throws Exception {
        if (i <= 0) {
            return null;
        }
        if (SigningService.signingService == null) {
            SigningService.signingService = new SigningService(i);
            SigningService.listUserSession = new HashMap<>();
        }
        if (SigningService.signingService.enterprise_id_instant != i) {
            SigningService.signingService = new SigningService(i);
        }
        SigningService.signingService.enterprise_id_instant = i;
        return SigningService.signingService;
    }

    private SigningService(int i) throws Exception {
        init(i);
    }

    private void init(int enterprise_id) throws Exception {
        Database callDB = new DatabaseImpl();
        Enterprise object;
        try {
            object = (Enterprise) callDB.getDataRP(
                    enterprise_id,
                    "transactionID").getObject();
        } catch (ClassCastException ex) {
            LogHandler.error(
                    SigningService.class,
                    "Cannot cast data !",
                    ex);
            return;
        }

        //Test
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("resources/rssp.p12");
//            byte[] arr = Files.readAllBytes(new File("D:\\NetBean\\qrypto\\file\\rssp.p12").toPath());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4];
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] targetArray = buffer.toByteArray();
            prop = Utils.getDataRESTFromString2(object.getData(), targetArray);
            this.sessionFactory = SessionFactory.getInstance(
                    //                    Utils.getDataRESTFromString(object.getData(),object2.getData()),
                    prop,
                    "EN");
            this.session = sessionFactory.getServerSession();

        } catch (Throwable ex) {
            LogHandler.error(
                    SigningService.class,
                    "",
                    "Cannot init sessionFactory !",
                    ex);
        }
        this.enterprise_id_instant = enterprise_id;
    }

    //<editor-fold defaultstate="collapsed" desc="Sign Hash Bussiness">
    public List<byte[]> signHashBussiness(
            byte[] content,
            SignaturePositionProperties positionSignature,
            vn.mobileid.id.paperless.object.enumration.TemplateSignature templateSignature) {
        try {
            IPdfSignFile signFile = new SignFileFactory().createPdfSignFile_SyncFlow(
                    SignFileFactory.SignType.PAdES,
                    Algorithm.SHA256,
                    PdfForm.B);

            //Read file in server
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("resources/MobileID-Signature.png");
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4];
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] imageBackground = buffer.toByteArray();

            loader = Thread.currentThread().getContextClassLoader();
            input = loader.getResourceAsStream("resources/MobileID-Signature.png");
            buffer = new ByteArrayOutputStream();
            data = new byte[4];
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] font = buffer.toByteArray();

            //Get Enterprise Signing Properties in DB     
            String agreement = null;
            String credential = null;
            String password = null;
            String boxCoordinate = null;
            String keyword = null;
            String page = null;
            String reason = null;
            String location = null;
            String textContent = null;
            String dateFormat = null;
            try {
                //Get DataSignatureProperties in Enterprise
                InternalResponse response = GetEnterpriseSigningInfo.getEnterpriseSigningInfo(enterprise_id_instant, "transactionId");
                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return null;
                }
                Enterprise_SigningInfo signingInfo = (Enterprise_SigningInfo) response.getData();

                agreement = signingInfo.getRsspinfo().getBusinessAgreementUUID();
                credential = signingInfo.getRsspinfo().getBusinessCredentialId();
                password = signingInfo.getRsspinfo().getBusinessPasswordUUID();

                DataSignatureProperties dataSignature = null;
                //<editor-fold defaultstate="collapsed" desc="Get DataSignature from Enterprise Signing Info">
                for (TemplateSignature template : signingInfo.getDataSignature()) {
                    if (template.getType().equals(templateSignature.getName())) {
                        dataSignature = template.getBusinessData();
                    }
                }
                if (dataSignature == null) {
                    throw new CannotFindDataSignatureException("Cannot find Data Signature");
                }
                //</editor-fold>

                boxCoordinate = positionSignature.getBoxCoordinate();
                keyword = positionSignature.getKeyword();
                page = positionSignature.getPage();
                reason = dataSignature.getReason();
                location = dataSignature.getLocation();
                textContent = dataSignature.getTextContent();
                dateFormat = dataSignature.getDate_format();
                if (!Utils.isNullOrEmpty(dataSignature.getDate_format())) {
                    dateFormat = dataSignature.getDate_format();
                } else {
                    dateFormat = PolicyConfiguration
                            .getInstant()
                            .getSignatureProperties()
                            .getAttributes().get(0)
                            .getSigningTimeFormat();
                }
            } catch (Exception ex) {
                agreement = "OW06742145068696660080";
                credential = "0d8535f8-e54a-43f0-acbf-d88c8ae02cbc";
                password = "12345678";
                boxCoordinate = "-20,-130,160,110";
                keyword = "NGƯỜI SỬ DỤNG LAO ĐỘNG";
                page = "LAST";
                reason = "Ky hop dong";
                location = "Quan 2";
            }

            PdfProfile profile = signFile.getProfile();
            profile.setReason(reason == null ? "Ky hop dong" : reason);
            profile.setLocation(location == null ? "Quan 2" : location);
            profile.setTextContent("");

            if (Utils.isNullOrEmpty(keyword)) {
                profile.setVisibleSignature(page, boxCoordinate, false);
            } else {
                String[] temps = boxCoordinate.split(",");
                profile.setVisibleSignature(
                        page,
                        temps[0] + "," + temps[1],
                        temps[2] + "," + temps[3],
                        keyword);
            }
            profile.setCheckText(false);
            profile.setCheckMark(false);
            profile.setFont(
                    font,
                    BaseFont.IDENTITY_H,
                    true,
                    8,
                    0,
                    TextAlignment.ALIGN_LEFT,
                    Color.BLACK);
            profile.setBackground(imageBackground);
            profile.setSigningTime(Calendar.getInstance(), dateFormat);

            List<byte[]> src = new ArrayList<>();
            src.add(content);

            //Signing                        
            List<byte[]> results = signFile.sign(
                    agreement,
                    credential,
                    password,
                    src,
                    this.session);

            int i = 0;
            List<byte[]> temp = new ArrayList<>();
            for (byte[] result : results) {
                temp.add(result);
            }
            return temp;
        } catch (APIException ex) {
            LogHandler.error(
                    SigningService.class,
                    "",
                    "Error While Signing !",
                    ex);
            return null;
        } catch (Exception ex) {
            LogHandler.error(
                    SigningService.class,
                    "",
                    "Error While Signing !",
                    ex);
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Sign Hash Witness">
    public List<byte[]> signHashWitness(
            String fullNameWitness,
            String base64Evidence,
            SignaturePositionProperties positionSignatureProperties,
            byte[] content,
            JWT_Authenticate jwt,
            FrameSignatureProperties signing,
            String transactionID
    ) throws Exception {
        try {
            fullNameWitness = new String(fullNameWitness.getBytes(StandardCharsets.UTF_8));
            //Read file from server
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("resources/verdana.ttf");

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4];
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] font = buffer.toByteArray();

            //Get DataSignatureProperties in Enterprise
            InternalResponse response = GetEnterpriseSigningInfo.getEnterpriseSigningInfo(enterprise_id_instant, transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return null;
            }

            Enterprise_SigningInfo signingInfo = (Enterprise_SigningInfo) response.getData();

            String agreementUUID = signingInfo.getRsspinfo().getWitnessAgreementUUID();
            String credentials = signingInfo.getRsspinfo().getWitnessCredentialId();
            String password = signingInfo.getRsspinfo().getWitnessPasswordUUID();

            String fullNameReplaceSpace = fullNameWitness.replaceAll(" ", "").toString();

            byte[] picture = ImageGenerator.remoteSignWithPathFont_UsingClassLoader("", "resources/FunkySignature-Regular.ttf", "resources/FunkySignature-Regular.ttf", fullNameReplaceSpace, "");
            byte[] picture2 = null;
            try {
                picture2 = Base64.getDecoder().decode(base64Evidence.replaceAll("\n", "").getBytes());
            } catch (IllegalArgumentException ex) {
            }
            byte[] imgData = ImageGenerator.combineImage(picture2, picture);

            IPdfSignFile signFile = new SignFileFactory().createPdfSignFile_SyncFlow(SignFileFactory.SignType.CMS, Algorithm.SHA256, PdfForm.B);

            PdfProfile profile = signFile.getProfileCMS();
            profile.setFontSizeMin(3);

            DataSignatureProperties dataSignature = null;

            //<editor-fold defaultstate="collapsed" desc="Get DataSignature from Enterprise Signing Info">
            for (TemplateSignature template : signingInfo.getDataSignature()) {
                if (template.getType().equals(vn.mobileid.id.paperless.object.enumration.TemplateSignature.Elabor.getName())) {
                    dataSignature = template.getSignerData();
                }
            }
            if (dataSignature == null) {
                throw new CannotFindDataSignatureException("Cannot find Data Signature");
            }
            //</editor-fold>

            String reason = dataSignature.getReason();
            String textContent = dataSignature.getTextContent();
            String page = positionSignatureProperties.getPage();
            String boxCoordinate = positionSignatureProperties.getBoxCoordinate();
            String keyword = positionSignatureProperties.getKeyword();
            float fontSize = dataSignature.getFontSize();

            String format_date = null;
            if (!Utils.isNullOrEmpty(dataSignature.getDate_format())) {
                format_date = dataSignature.getDate_format();
            } else {
                format_date = PolicyConfiguration
                        .getInstant()
                        .getSignatureProperties()
                        .getAttributes().get(0)
                        .getSigningTimeFormat();
            }

            //Append SiningProperties  
            if (signing == null) {
                reason = AnnotationJWT.replaceWithJWT(reason, jwt);
                profile.setReason(reason);
                textContent = AnnotationJWT.replaceWithJWT(reason, jwt);
                textContent = textContent.replaceAll(AnnotationJWT.Reason.getNameAnnot(), reason);
                profile.setTextContent(textContent);
                profile.setImage(imgData, ImageProfile.IMAGE_BOTTOM);

                if (Utils.isNullOrEmpty(keyword)) {
                    profile.setVisibleSignature(String.valueOf(page), boxCoordinate, false);
                } else {
                    String[] temp = boxCoordinate.split(",");
                    profile.setVisibleSignature(
                            String.valueOf(page),
                            temp[0] + "," + temp[1],
                            temp[2] + "," + temp[3],
                            keyword);
                }
                profile.setSigningTime(Calendar.getInstance(), format_date);
            } else {
                if (signing.getReason() != null) {
                    reason = signing.getReason();
                }
                reason = AnnotationJWT.replaceWithJWT(reason, jwt);
                profile.setReason(reason);

                if (signing.getTextContent() != null) {
                    textContent = signing.getTextContent();
                }
                
                textContent = AnnotationJWT.replaceWithJWT(textContent, jwt);

                if (signing.getDate() != null) {
                    textContent = textContent.replace("{date}", signing.getDate());
                }

                if (signing.getLocation() != null) {
                    textContent += "\nNơi ký: " + signing.getLocation();
                    profile.setLocation(signing.getLocation());
                }

                textContent = textContent.replaceAll(AnnotationJWT.Reason.getNameAnnot(), reason);

                profile.setTextContent(textContent);
                profile.setImage(imgData, ImageProfile.IMAGE_BOTTOM, ImageAlgin.ALIGN_LEFT);
                if (signing.getPage() != null) {
                    page = signing.getPage();
                }

                if (signing.getKeyword() != null) {
                    keyword = signing.getKeyword();
                }

                if (signing.getBoxCoordinate() != null) {
                    boxCoordinate = signing.getBoxCoordinate();
                }

                if (Utils.isNullOrEmpty(keyword)) {
                    profile.setVisibleSignature(page, boxCoordinate, false);
                } else {
                    String[] temp = boxCoordinate.split(",");
                    profile.setVisibleSignature(
                            page,
                            temp[0] + "," + temp[1],
                            temp[2] + "," + temp[3],
                            keyword);
                }
                if (signing.getFormat_date() != null) {
                    format_date = signing.getFormat_date();
                }
                profile.setSigningTime(Calendar.getInstance(), format_date);
            }

            profile.setCheckText(PolicyConfiguration
                    .getInstant()
                    .getSignatureProperties()
                    .getAttributes().get(0)
                    .isCheckText());
            profile.setCheckMark(PolicyConfiguration
                    .getInstant()
                    .getSignatureProperties()
                    .getAttributes().get(0)
                    .isCheckMark());

            if (fontSize <= 0) {
                fontSize = PolicyConfiguration
                        .getInstant()
                        .getSignatureProperties()
                        .getAttributes().get(0)
                        .getFontSize();
            }

            profile.setFont(
                    font,
                    BaseFont.IDENTITY_H,
                    true,
                    fontSize,
                    0,
                    TextAlignment.ALIGN_LEFT,
                    Color.BLACK);

            List<byte[]> src = new ArrayList<>();
            src.add(content);

            //====================================================
            List<byte[]> results = null;
            try {
                results = signFile.sign(
                        agreementUUID,
                        credentials,
                        password,
                        src,
                        this.session);
            } catch (Exception ex) {
                throw new Exception(ex);
            }

            return results;
        } catch (Exception e) {
            LogHandler.error(
                    SigningService.class,
                    transactionID,
                    "Error While Signing - Detail:",
                    e);
            throw new Exception(e);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Init User">
    public boolean initUser(String user, String pass) {
        try {
            if (SigningService.listUserSession.containsKey(user)) {
                return true;
            }
            IUserSession users = this.sessionFactory.newUserSession(user, pass);
            SigningService.listUserSession.put(user, users);
            return true;
        } catch (Throwable ex) {
            LogHandler.error(
                    SigningService.class,
                    "User " + user,
                    "Cannot Login!",
                    ex);
            return false;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get User">
    public IUserSession getUser(String user, String pass) {
        if (SigningService.listUserSession.containsKey(user)) {
            return SigningService.listUserSession.get(user);
        }
        if (initUser(user, pass)) {
            return SigningService.listUserSession.get(user);
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="List User Certificate">
    public List<ICertificate> listUserCertificate(String user, String pass) throws Throwable {
        try {
            if (SigningService.listUserSession.containsKey(user)) {
                return SigningService.listUserSession.get(user).listCertificates();
            } else {
                return null;
            }
        } catch (Exception ex) {
            LogHandler.error(
                    SigningService.class,
                    "User " + user,
                    "Cannot list Certificate!",
                    ex);
        } catch (Throwable ex) {
            LogHandler.error(
                    SigningService.class,
                    "User " + user,
                    "Cannot list Certificate!",
                    ex);
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Certificate Info">
    public ICertificate certificateInfo(String user, String pass, String credentialID) throws Throwable {
        try {
            if (SigningService.listUserSession.containsKey(user)) {
                return SigningService.listUserSession.get(user).certificateInfo(credentialID);
            } else {
                return null;
            }
        } catch (Exception ex) {
            LogHandler.error(
                    SigningService.class,
                    "User " + user,
                    "Cannot get CertificateInfo!",
                    ex);
        } catch (Throwable ex) {
            LogHandler.error(
                    SigningService.class,
                    "User " + user,
                    "Cannot get CertificateInfo!",
                    ex);
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Authorize">
    public String authorize(String user, String pass, String credentialID, String authorizeCode) {
        try {
            if (SigningService.listUserSession.containsKey(user)) {
                return SigningService.listUserSession.get(user).authorize(credentialID, 1, "", authorizeCode);
            } else {
                return null;
            }
        } catch (Exception ex) {
            LogHandler.error(
                    SigningService.class,
                    "User " + user,
                    "Cannot authorize!",
                    ex);
        } catch (Throwable ex) {
            LogHandler.error(
                    SigningService.class,
                    "User " + user,
                    "Cannot authorize!",
                    ex);
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Sign Hash User">
    public List<byte[]> signHashUser(
            String user,
            String pass,
            String fullNameWitness,
            String image,
            byte[] content,
            FrameSignatureProperties signing,
            JWT_Authenticate jwt,
            SignaturePositionProperties position
    ) {
        try {
            fullNameWitness = new String(fullNameWitness.getBytes(StandardCharsets.UTF_8));
            //Read file from server
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("resources/verdana.ttf");

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4];
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] font = buffer.toByteArray();

            String fullNameReplaceSpace = fullNameWitness.replaceAll(" ", "").toString();

            byte[] picture = ImageGenerator.remoteSignWithPathFont_UsingClassLoader("", "resources/FunkySignature-Regular.ttf", "resources/FunkySignature-Regular.ttf", fullNameReplaceSpace, "");

            byte[] imgData = ImageGenerator.combineImage(Base64.getDecoder().decode(image), picture);

            //Get DataSignatureProperties in Enterprise
            InternalResponse response = GetEnterpriseSigningInfo.getEnterpriseSigningInfo(enterprise_id_instant, "transactionId");
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return null;
            }

            Enterprise_SigningInfo signingInfo = (Enterprise_SigningInfo) response.getData();
            DataSignatureProperties dataSignature = null;

            //<editor-fold defaultstate="collapsed" desc="Get DataSignature from Enterprise Signing Info">
            for (TemplateSignature template : signingInfo.getDataSignature()) {
                if (template.getType().equals(vn.mobileid.id.paperless.object.enumration.TemplateSignature.EsignCloud.getName())) {
                    dataSignature = template.getSignerData();
                }
            }
            if (dataSignature == null) {
                throw new CannotFindDataSignatureException("Cannot find Data Signature");
            }
            //</editor-fold>

            String reason = dataSignature.getReason();
            String textContent = dataSignature.getTextContent();
            String page = position.getPage();
            String boxCoordinate = position.getBoxCoordinate();
            String keyword = position.getKeyword();
            float fontSize = dataSignature.getFontSize();
            
            String format_date = null;
            if (!Utils.isNullOrEmpty(dataSignature.getDate_format())) {
                format_date = dataSignature.getDate_format();
            } else {
                format_date = PolicyConfiguration
                        .getInstant()
                        .getSignatureProperties()
                        .getAttributes().get(0)
                        .getSigningTimeFormat();
            }

            IPdfSignFile signFile = new SignFileFactory().createPdfSignFile_forUser(SignFileFactory.SignType.CMS, Algorithm.SHA256, PdfForm.B);

            PdfProfileCMS profile = signFile.getProfileCMS();

            //Append SiningProperties  
            if (signing == null) {
                reason = AnnotationJWT.replaceWithJWT(reason, jwt);
                profile.setReason(reason);
                textContent = AnnotationJWT.replaceWithJWT(reason, jwt);
                textContent = textContent.replaceAll(AnnotationJWT.Reason.getNameAnnot(), reason);
                profile.setTextContent(textContent);
                profile.setImage(imgData, ImageProfile.IMAGE_BOTTOM, ImageAlgin.ALIGN_LEFT);
                String[] temp = boxCoordinate.split(",");
                if (Utils.isNullOrEmpty(keyword)) {
                    profile.setVisibleSignature(
                            page,
                            boxCoordinate,
                            false);
                } else {
                    profile.setVisibleSignature(
                            String.valueOf(page),
                            temp[0] + "," + temp[1],
                            temp[2] + "," + temp[3],
                            keyword);
                }
                profile.setSigningTime(Calendar.getInstance(), format_date);
            } else {
                if (signing.getReason() != null) {
                    reason = signing.getReason();
                }
                reason = AnnotationJWT.replaceWithJWT(reason, jwt);
                profile.setReason(reason);

                if (signing.getTextContent() != null) {
                    textContent = signing.getTextContent();
                }
                textContent = AnnotationJWT.replaceWithJWT(textContent, jwt);

                if (signing.getDate() != null) {
                    textContent = textContent.replace("{date}", signing.getDate());
                } else {
                    
                }

                if (signing.getLocation() != null) {
                    textContent += "\nNơi ký: " + signing.getLocation();
                    profile.setLocation(signing.getLocation());
                }

                textContent = textContent.replaceAll(AnnotationJWT.Reason.getNameAnnot(), reason);

                profile.setTextContent(textContent);
                System.out.println("Text Content:"+textContent);

                profile.setImage(imgData, ImageProfile.IMAGE_BOTTOM, ImageAlgin.ALIGN_LEFT);

                if (signing.getPage() != null) {
                    page = signing.getPage();
                }

                if (signing.getKeyword() != null) {
                    keyword = signing.getKeyword();
                }

                if (signing.getBoxCoordinate() != null) {
                    boxCoordinate = signing.getBoxCoordinate();
                }

                String[] temp = boxCoordinate.split(",");
                if (Utils.isNullOrEmpty(keyword)) {
                    profile.setVisibleSignature(
                            page,
                            boxCoordinate,
                            false);
                } else {
                    profile.setVisibleSignature(
                            page,
                            temp[0] + "," + temp[1],
                            temp[2] + "," + temp[3],
                            keyword);
                }
                if (signing.getFormat_date() != null) {
                    format_date = signing.getFormat_date();
                }
                profile.setSigningTime(Calendar.getInstance(), format_date);
            }

            profile.setCheckText(false);
            profile.setCheckMark(false);
            
            if (fontSize <= 0) {
                fontSize = PolicyConfiguration
                        .getInstant()
                        .getSignatureProperties()
                        .getAttributes().get(0)
                        .getFontSize();
            }
            
            System.out.println("Input font:"+fontSize);
            
            profile.setFont(font, BaseFont.IDENTITY_H, true, fontSize, 0, TextAlignment.ALIGN_LEFT, Color.BLACK);
            profile.setFontSizeMin(3);
            
            List<byte[]> src = new ArrayList<>();
            src.add(content);

            //====================================================
            this.initUser(user, pass);
            List<ICertificate> listCert = this.listUserCertificate(user, pass);
            int number = -1;
            for (int i = listCert.size() - 1; i >= 0; i--) {
                ICertificate cert = certificateInfo(user, pass, listCert.get(i).baseCredentialInfo().getCredentialID());
                if (!cert.baseCredentialInfo().getStatus().equals("EXPIRES")
                        && cert.baseCredentialInfo().getStatus().equals("OPERATED")) {
                    number = i;
                    break;
                }
            }
            if (number == -1) {
                issueCertificate(
                        jwt.getDocument_number(),
                        jwt.getEmail(),
                        jwt.getPhone_number(),
                        jwt.getPlace_of_residence(),
                        jwt.getCity_province(),
                        jwt.getNationality(),
                        jwt.getDocument_number());
                listCert = this.listUserCertificate(user, pass);
                number = 1;
            }
            try {
                List<byte[]> results = signFile.sign(
                        listCert.get(number).baseCredentialInfo().getCredentialID(),
                        "12345678",
                        src,
                        this.getUser(user, pass));
                return results;
            } catch (Exception e) {
                throw new NullPointerException();
            }
        } catch (Exception e) {
            LogHandler.error(
                    SigningService.class,
                    "",
                    "Error While Signing!",
                    e);
            return null;
        } catch (Throwable ex) {
            LogHandler.error(
                    SigningService.class,
                    "",
                    "Error While Signing!",
                    ex);
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create Owner">
    public void createOwner(
            String user,
            String email,
            String phone,
            String documentID) {
        try {
            this.session.createUser(user, email, phone, "PERSONAL-ID", documentID);
        } catch (Throwable ex) {
            LogHandler.error(
                    SigningService.class,
                    "User:" + user,
                    "Error While Create Owner",
                    ex);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Issuer Certificate">
    public void issueCertificate(
            String username,
            String email,
            String phone,
            String location,
            String province,
            String country,
            String documentID
    ) {
        String certProfile = "T2PSB21Y";

        SharedMode sharedMode = SharedMode.AGREEMENT_SHARED_MODE;
        AuthMode authMode = AuthMode.EXPLICIT_PIN;

        CertificateDetails crtDetails = new CertificateDetails();
        crtDetails.setCommonName(username);
        crtDetails.setEmail(email);
        if (phone != null) {
            crtDetails.setTelephoneNumber(phone);
        }
        crtDetails.setLocation(location);
        crtDetails.setStateOrProvince(province);
        crtDetails.setCountry(country.equals("Việt Nam") ? "VN" : country);
        crtDetails.setIdentifications(new Identification[1]);

        IdentificationType type = IdentificationType.PERSONAL_ID;
        Identification id = new Identification(type, documentID);
        crtDetails.getIdentifications()[0] = id;

        try {
            session.createCertificate(username, "", "", certProfile, sharedMode, authMode, 1, crtDetails);
        } catch (Throwable ex) {
            LogHandler.error(
                    SigningService.class,
                    "User:" + username,
                    "Cannot issue Certificate!",
                    ex);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check Exist">
    public boolean checkExist(String user, String pass) {
        try {
            boolean check = session.preLogin(Types.UserType.PERSONAL_ID, user);
            boolean check2 = session.preLogin(Types.UserType.USERNAME, user);
            return check || check2;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } catch (Throwable ex) {
            return false;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Hash Document">
    public static List<String> hashDocument(
            String fullNameWitness,
            byte[] content,
            List<String> images,
            FrameSignatureProperties signing,
            JWT_Authenticate jwt,
            String transactionID
    ) throws Exception {
        List<String> response = new ArrayList<>();
        for (String image : images) {
            fullNameWitness = new String(fullNameWitness.getBytes(StandardCharsets.UTF_8));
            //Read file from server
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("resources/verdana.ttf");

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4];
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] font = buffer.toByteArray();

            String fullNameReplaceSpace = fullNameWitness.replaceAll(" ", "").toString();

            byte[] picture = ImageGenerator.remoteSignWithPathFont_UsingClassLoader("", "resources/FunkySignature-Regular.ttf", "resources/FunkySignature-Regular.ttf", fullNameReplaceSpace, "");

            byte[] imgData = ImageGenerator.combineImage(Base64.getDecoder().decode(image), picture);

            IPdfSignFile signFile = new SignFileFactory().createPdfSignFile_forUser(SignFileFactory.SignType.CMS, Algorithm.SHA256, PdfForm.B);

            PdfProfile profile = signFile.getProfile();
            String reason = "";
            //Append SiningProperties  
            if (signing == null) {
                reason = "Signed by " + fullNameWitness;
                profile.setReason(reason);
                String textContent = "Ký bởi: " + fullNameWitness
                        + "\nCCCD: " + jwt.getDocument_number();
                textContent += "\nNgày ký: {date}";
                textContent += "\nLý do: " + reason;
                profile.setTextContent(textContent);
                profile.setImage(imgData, ImageProfile.IMAGE_BOTTOM);
                profile.setVisibleSignature("2", "-20,-130", "190,110", "NGƯỜI LAO ĐỘNG");
            } else {
                if (signing.getReason() != null) {
                    reason = signing.getReason();
                    reason = AnnotationJWT.replaceWithJWT(reason, jwt);
                    profile.setReason(reason);
                } else {
                    reason = "Signed by " + fullNameWitness;
                    profile.setReason(reason);
                }

                String textContent = "Ký bởi: " + fullNameWitness
                        + "\nCCCD: " + jwt.getDocument_number();
                if (signing.getDate() != null) {
                    textContent += "\nNgày ký: " + signing.getDate();
                } else {
                    textContent += "\nNgày ký: {date}";
                }
                if (signing.getLocation() != null) {
                    textContent += "\nNơi ký: " + signing.getLocation();
                }

                textContent += "\nLý do: " + reason;
                profile.setTextContent(textContent);
                profile.setImage(imgData, ImageProfile.IMAGE_BOTTOM);
                if (signing.getKeyword() != null) {
                    profile.setVisibleSignature("2", "-20,-130", "190,110", signing.getKeyword());
                } else {
                    profile.setVisibleSignature("2", "-20,-130", "190,110", "NGƯỜI LAO ĐỘNG");
                }
            }

            profile.setCheckText(false);
            profile.setCheckMark(false);
            profile.setSigningTime(Calendar.getInstance(), "dd-MM-yyyy hh:mm:ss aa");
            profile.setFont(font, BaseFont.IDENTITY_H, true, 8, 0, TextAlignment.ALIGN_LEFT, Color.BLACK);

            List<byte[]> src = new ArrayList<>();
            src.add(content);
            SigningMethodAsyncImp signInit = new SigningMethodAsyncImp();
            byte[] temporalData = profile.createTemporalFile(signInit, src);
            List<String> hashList = signInit.getHashList();
            response.add(signInit.getHashList().get(0));
        }
        return response;
    }
    //</editor-fold>

    public static void main(String[] arhs) throws Exception {
        System.out.println(SigningService.getInstant(3).checkExist("060195008949", ""));
//       SigningService.getInstant(3).createOwner("035195006147", "nghiatranthi130295@gmail.com", "84977913295", "035195006147");
    }

}
