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
import vn.mobileid.id.qrypto.objects.Enterprise_JSNObject;
import vn.mobileid.id.qrypto.objects.FileManagement_JSNObject;
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
    
    private Property prop ;

    private ISessionFactory sessionFactory;
    private IServerSession session;

    private static SigningService signingService;

    public static SigningService getInstant(int i) {   
        if(i<=0){
            return null;
        }
        if (SigningService.signingService == null) {
            SigningService.signingService = new SigningService(i);
        }
        if(SigningService.signingService.enterprise_id_instant != i){
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
        Enterprise_JSNObject object;
        FileManagement_JSNObject object2;

        try {
            object = (Enterprise_JSNObject) callDB.getDataRP(enterprise_id).getObject();
            object2 = (FileManagement_JSNObject) callDB.getFile(22).getObject();
        } catch (ClassCastException ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot cast data - Details:" + ex);
            }
            return;
        }

        //Test
        try {
            byte[] arr = Files.readAllBytes(new File("D:\\NetBean\\QryptoServices\\file\\rssp.p12").toPath());
            prop = Utils.getDataRESTFromString2(object.getData(),arr);
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

    private void signHashBussiness(String filename, byte[] content) {
        try {
            IPdfSignFile signFile = new SignFileFactory().createPdfSignFile(SignFileFactory.SignType.PAdES, Algorithm.SHA256, PdfForm.B);

            PdfProfile profile = signFile.getProfile();
            profile.setReason("Ký hợp đồng điện tử");
            profile.setVisibleSignature("1", "-20,-130", "160,110", "DIGITAL SIGNATURE");
            profile.setCheckText(false);
            profile.setCheckMark(false);
            profile.setFont(DefaultFont.Times, 9, 1.1f, TextAlignment.ALIGN_LEFT, Color.RED);
            profile.setBorder(Color.RED);

            List<byte[]> src = new ArrayList<>();
            src.add(content);

            //Signing
            String agreementUUIDBussiness = "OW06742145068696660080";
            String credentialIDBussiness = "0d8535f8-e54a-43f0-acbf-d88c8ae02cbc";
            List<byte[]> results = signFile.sign(agreementUUIDBussiness, credentialIDBussiness, "12345678", src, this.session);

            int i = 0;
            for (byte[] result : results) {
                System.out.println("File name: " + "signed" + i + ".pdf");
                Files.write(Paths.get("file/signed" + i + ".pdf"), result, StandardOpenOption.CREATE);
                i++;
            }
        } catch (APIException ex) {
            System.out.println("Ex:"+ex);
            if (LogHandler.isShowErrorLog()) {
                LOG.error(ex);
            }
        } catch (Exception ex) {
            System.out.println("Ex:"+ex);
            if (LogHandler.isShowErrorLog()) {
                LOG.error(ex);
            }
        }
    }

    private ValueSignHash signHashWitness(String fullNameWitness, String base64Evidence, String filename){
        try{
        System.out.println("file name : " + filename);
        ValueSignHash valueSignHash = null;
        
        File imgFile = new File("");
        
        //Data
        String font_hand = "D:\\NetBean\\QryptoServices\\file\\file\\Fz-Jim-Sintergate.ttf";        
        String font_Sign = "D:\\NetBean\\QryptoServices\\file\\file\\FunkySignature-Regular.ttf";
        String logos = "D:\\NetBean\\QryptoServices\\file\\file\\MobileID-Signature.png";
        String agreementUUID = "9E6FA0D0-6319-4D57-A760-99BBBECB35D0";
        String credentials = "fc081a30-8ed5-40c0-93b3-7b9cbd8d40f2";
        
        String fullNameReplaceSpace = fullNameWitness.replaceAll(" ", "").toString();
        byte[] picture = ImageGenerator.remoteSignWithPathFont("", font_hand, font_hand, fullNameReplaceSpace, "");
        byte[] picture2 = Base64.getDecoder().decode(base64Evidence.getBytes());
        ImageGenerator.combineImage(picture, picture2, imgFile.getAbsolutePath());
        byte[] imgData = IOUtils.toByteArray(new FileInputStream(imgFile));

        IPdfSignFile signFile = new SignFileFactory().createPdfSignFile(SignFileFactory.SignType.PAdES, Algorithm.SHA256, PdfForm.B );

        PdfProfile profile = signFile.getProfile();
        profile.setReason("Ký hợp đồng điệnDĐ tử");
        profile.setTextContent("Signed by: " + fullNameWitness + "\nSigned at: {date}"
                + "\nLocation: {location}"
                + "\nReason: Witnessing " + fullNameWitness);
        profile.setImage(imgData, ImageProfile.IMAGE_BOTTOM);
        profile.setVisibleSignature("2", "-20,-130", "190,110", "NGƯỜI LAO ĐỘNG");
        profile.setCheckText(false);
        profile.setCheckMark(false);

        profile.setSigningTime(Calendar.getInstance(), "dd-MM-yyyy hh:mm:ss aa");
        profile.setLocation("HCM");

        byte[] font = IOUtils.toByteArray(new FileInputStream(font_Sign));
        byte[] logo = IOUtils.toByteArray(new FileInputStream(logos));
        System.out.println("-------font: " + font_Sign);
        System.out.println("-------font-byte-array: " + font);

        profile.setFont(font, BaseFont.IDENTITY_H, true, 8, 0, TextAlignment.ALIGN_LEFT, Color.BLACK);

        File f = new File("D:\\NetBean\\QryptoServices\\file\\"+ filename);
        byte[] fileContent = FileUtils.readFileToByteArray(f);
        List<byte[]> src = new ArrayList<>();
        src.add(fileContent);

        //=======================================================
        valueSignHash = new ValueSignHash();
        valueSignHash.setValueSignHash("-----");
        valueSignHash.setHashAlgo(Algorithm.SHA256.name());
        //====================================================

        List<byte[]> results = signFile.sign(agreementUUID, credentials, "12345678", src, this.session);
        OutputStream OS = new FileOutputStream("D:\\NetBean\\QryptoServices\\file\\signed." + filename);
        IOUtils.write(results.get(0), OS);
        OS.close();

        return valueSignHash;
        } catch (Exception e){
            System.out.println("Ex:"+e);
            return null;
        }
    }
    
    public static void main(String[] arhs) throws IOException {
//        String path = "D:\\NetBean\\QryptoServices\\file\\sample.pdf";
//        byte[] content = Files.readAllBytes(new File(path).toPath());
//        SigningService.getInstant().signHashBussiness("hallo", content);
        String filename = "";
//        SigningService.getInstant().signHashWitness(filename, filename, filename)
    }
}
