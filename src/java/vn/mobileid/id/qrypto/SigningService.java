/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto;

import RestfulFactory.SessionFactory;
import SignFile.IPdfSignFile;
import SignFile.SignFileFactory;
import com.itextpdf.text.pdf.BaseFont;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import restful.sdk.API.APIException;
import restful.sdk.API.IServerSession;
import restful.sdk.API.ISessionFactory;
import restful.sdk.API.Property;
import vn.mobileid.exsig.Algorithm;
import vn.mobileid.exsig.Color;
import vn.mobileid.exsig.DefaultFont;
import vn.mobileid.exsig.ImageGenerator;
import vn.mobileid.exsig.ImageProfile;
import vn.mobileid.exsig.PdfForm;
import vn.mobileid.exsig.PdfProfile;
import vn.mobileid.exsig.TextAlignment;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.qrypto.objects.Enterprise;
import vn.mobileid.id.qrypto.objects.FileManagement;
import vn.mobileid.id.qrypto.objects.ValueSignHash;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class SigningService {

    private static final Logger LOG = LogManager.getLogger(SigningService.class);

    private int enterprise_id_instant;

    private Property prop;

    private ISessionFactory sessionFactory;
    private IServerSession session;

    private static SigningService signingService;

    public static SigningService getInstant(int i) {
        if (i <= 0) {
            return null;
        }
        if (SigningService.signingService == null) {
            SigningService.signingService = new SigningService(i);
        }
        if (SigningService.signingService.enterprise_id_instant != i) {
            SigningService.signingService = new SigningService(i);
        }
        SigningService.signingService.enterprise_id_instant = i;
        return SigningService.signingService;
    }

    private SigningService(int i) {
        init(i);
    }

    private void init(int enterprise_id) {
        Database callDB = new DatabaseImpl();
        Enterprise object;
        FileManagement object2;

        try {
            object = (Enterprise) callDB.getDataRP(enterprise_id).getObject();
//            object2 = (FileManagement) callDB.getFile(22).getObject();
        } catch (ClassCastException ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot cast data - Details:" + ex);
            }
            return;
        }

        //Test
        try {
            byte[] arr = Files.readAllBytes(new File("D:\\NetBean\\QryptoServices\\file\\rssp.p12").toPath());
            prop = Utils.getDataRESTFromString2(object.getData(), arr);
            this.sessionFactory = SessionFactory.getInstance(
                    //                    Utils.getDataRESTFromString(object.getData(),object2.getData()),
                    prop,
                    "EN");
            this.session = sessionFactory.getServerSession();

        } catch (Throwable ex) {
            System.out.println("ex");
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot init sessionFactory - Details:" + ex);
            }
            return;
        }
    }

    public List<byte[]> signHashBussiness(byte[] content) {
        try {
            IPdfSignFile signFile = new SignFileFactory().createPdfSignFile(SignFileFactory.SignType.PAdES, Algorithm.SHA256, PdfForm.B);

            String imageBackground = "D:\\NetBean\\QryptoServices\\file\\file\\MobileID-Signature.png";
            String font_Sign = "D:\\NetBean\\QryptoServices\\file\\file\\verdana.ttf";
            byte[] font = IOUtils.toByteArray(new FileInputStream(font_Sign));

            PdfProfile profile = signFile.getProfile();
            profile.setReason("Ký hợp đồng điện tử");
            profile.setVisibleSignature("LAST", "-20,-130", "160,110", "NGƯỜI SỬ DỤNG LAO ĐỘNG");
            profile.setCheckText(false);
            profile.setCheckMark(false);
            profile.setSigningTime(Calendar.getInstance(), "dd-MM-yyyy hh:mm:ss aa");
            profile.setFont(font, BaseFont.IDENTITY_H, true, 8, 0, TextAlignment.ALIGN_LEFT, Color.BLACK);
            profile.setBorder(Color.RED);
            profile.setBackground(Files.readAllBytes(new File(imageBackground).toPath()));

            List<byte[]> src = new ArrayList<>();
            src.add(content);

            //Signing
            String agreementUUIDBussiness = "OW06742145068696660080";
            String credentialIDBussiness = "0d8535f8-e54a-43f0-acbf-d88c8ae02cbc";
            List<byte[]> results = signFile.sign(agreementUUIDBussiness, credentialIDBussiness, "12345678", src, this.session);

            int i = 0;
            List<byte[]> temp = new ArrayList<>();
            for (byte[] result : results) {
                temp.add(result);
//                System.out.println("File name: " + "signed" + i + ".pdf");
//                Files.write(Paths.get("file/signed" + i + ".pdf"), result, StandardOpenOption.CREATE);
//                i++;
            }
            return temp;
        } catch (APIException ex) {
            ex.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error(ex);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error(ex);
            }
        }
        return null;
    }

    public List<byte[]> signHashWitness(String fullNameWitness, String base64Evidence, byte[] content) {
        try {
            //Data
            String font_hand = "D:\\NetBean\\QryptoServices\\file\\file\\verdana.ttf";
            String font_Sign = "D:\\NetBean\\QryptoServices\\file\\file\\verdana.ttf";
            String agreementUUID = "9E6FA0D0-6319-4D57-A760-99BBBECB35D0";
            String credentials = "fc081a30-8ed5-40c0-93b3-7b9cbd8d40f2";

            String fullNameReplaceSpace = fullNameWitness.replaceAll(" ", "").toString();
            byte[] picture = ImageGenerator.remoteSignWithPathFont("", font_hand, font_hand, fullNameReplaceSpace, "");
            byte[] picture2 = Base64.getDecoder().decode(base64Evidence.getBytes());
            byte[] imgData = ImageGenerator.combineImage(picture, picture2);

            IPdfSignFile signFile = new SignFileFactory().createPdfSignFile(SignFileFactory.SignType.PAdES, Algorithm.SHA256, PdfForm.B);

            PdfProfile profile = signFile.getProfile();
            profile.setReason("Ký hợp đồng điện Đ/tử");
            profile.setTextContent("Ký bởi: " + fullNameWitness + "\nNgày ký: {date}"
                    + "\nNơi ký: {location}"
                    + "\nLý do: Witnessing " + fullNameWitness);
            profile.setImage(imgData, ImageProfile.IMAGE_BOTTOM);
            profile.setVisibleSignature("2", "-20,-130", "190,110", "NGƯỜI LAO ĐỘNG");
            profile.setCheckText(false);
            profile.setCheckMark(false);
            profile.setSigningTime(Calendar.getInstance(), "dd-MM-yyyy hh:mm:ss aa");
            profile.setLocation("HCM");
            byte[] font = IOUtils.toByteArray(new FileInputStream(font_Sign));
            profile.setFont(font, BaseFont.IDENTITY_H, true, 8, 0, TextAlignment.ALIGN_LEFT, Color.BLACK);

            List<byte[]> src = new ArrayList<>();
            src.add(content);

            //====================================================
            List<byte[]> results = signFile.sign(agreementUUID, credentials, "12345678", src, this.session);

            return results;
//        OutputStream OS = new FileOutputStream("D:\\NetBean\\QryptoServices\\file\\signed." + filename);
//        IOUtils.write(results.get(0), OS);
//        OS.close();
//        return valueSignHash;
        } catch (Exception e) {
            System.out.println("Ex:" + e);
            return null;
        }
    }

    public static void main(String[] arhs) throws IOException {
//        String filename = "resul.pdf";
//        String CombineImage = "D:\\NetBean\\QryptoServices\\file\\file\\CombineImage.png";
//        String base64Envidence = Base64.getEncoder().encodeToString(
//                                        Files.readAllBytes(
//                                                new File(CombineImage).toPath()));
//        SigningService.getInstant(3).signHashWitness("gia", base64Envidence, filename);
//        
//        SigningService.getInstant(3).signHashBussiness(filename, 
//                                    Files.readAllBytes(
//                                            new File("D:\\NetBean\\QryptoServices\\file\\signed.resul.pdf").toPath()));
    }
}
