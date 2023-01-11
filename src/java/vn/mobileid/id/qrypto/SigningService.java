/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto;

import RestfulFactory.SessionFactory;
import SignFile.IPdfSignFile;
import SignFile.SignFileFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import restful.sdk.API.APIException;
import restful.sdk.API.IServerSession;
import restful.sdk.API.ISessionFactory;
import vn.mobileid.exsig.Algorithm;
import vn.mobileid.exsig.Color;
import vn.mobileid.exsig.DefaultFont;
import vn.mobileid.exsig.PdfForm;
import vn.mobileid.exsig.PdfProfile;
import vn.mobileid.exsig.TextAlignment;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.qrypto.objects.Enterprise_JSNObject;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class SigningService {

    private static final Logger LOG = LogManager.getLogger(SigningService.class);

    private Properties prop = new Properties();

    private ISessionFactory sessionFactory;
    private IServerSession session;

    private static SigningService signingService;

    //Information of Enterprise
    private String RP_name;
    private String RP_user;
    private String RP_pass;
    private String RP_signature;
    private String RP_keystorefile;
    private String RP_keystorePass;

    public static SigningService getInstant() {
        if (SigningService.signingService == null) {
            SigningService.signingService = new SigningService();
        }
        return SigningService.signingService;
    }

    private SigningService() {
        init();
    }

    private void init() {
        Database callDB = new DatabaseImpl();
        Enterprise_JSNObject object;
        try {
            object = (Enterprise_JSNObject) callDB.getDataRP(3).getObject();
//            prop.load(new StringReader(object.getData()));
//        } catch (IOException ex) {
//            if(LogHandler.isShowErrorLog()){
//                LOG.error("Cannot read data RESTFUL DATA in DB - Details:"+ex);
//            }
//            return;
        } catch (ClassCastException ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot cast data - Details:" + ex);
            }
            return;
        }

        //Read data
//        this.RP_name = prop.getProperty("");
//        this.RP_user = prop.getProperty("");
//        this.RP_pass = prop.getProperty("");
//        this.RP_keystorefile = prop.getProperty("");
//        this.RP_keystorePass = prop.getProperty("");
        try {
            this.sessionFactory = SessionFactory.getInstance(
                    Utils.getDataRESTFromString(object.getData()),
                    "EN");
            this.session = sessionFactory.getServerSession();
        } catch (Throwable ex) {
            java.util.logging.Logger.getLogger(SigningService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void signHashBussiness(String filename, byte[] content) {
        try {
            IPdfSignFile signFile = new SignFileFactory().createPdfSignFile(SignFileFactory.SignType.PAdES, Algorithm.SHA256, PdfForm.B);

            PdfProfile profile = signFile.getProfile();
            profile.setReason("Ký hợp đồng điện tử");
            profile.setVisibleSignature("2", "-20,-130", "160,110", "NGƯỜI SỬ DỤNG LAO ĐỘNG");
            profile.setCheckText(false);
            profile.setCheckMark(false);
            profile.setFont(DefaultFont.Times, 9, 1.1f, TextAlignment.ALIGN_LEFT, Color.RED);
            profile.setBorder(Color.RED);

            List<byte[]> src = new ArrayList<>();
            src.add(content);
            
            //Signing
//            List<byte[]> result = signFile.sign(agreementUUIDBussiness, credentialIDBussiness, "12345678", src, this.session);
            
        } catch (APIException ex) {
            if(LogHandler.isShowErrorLog()){
                LOG.error("");
            }
        } catch (Exception ex) {
            if(LogHandler.isShowErrorLog()){
                LOG.error("");
            }
        }
    }
    
    public static void main(String[] arhs) throws IOException{
//        String path = "";
//        byte[] content = Files.readAllBytes(new File(path).toPath());
        SigningService.getInstant().signHashBussiness("hallo", null);
    }
}
