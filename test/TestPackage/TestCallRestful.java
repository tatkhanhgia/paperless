/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

import RestfulFactory.Model.CertificateDetails;
import RestfulFactory.Model.DocumentDigests;
import RestfulFactory.Model.Identification;
import RestfulFactory.SessionFactory;
import RestfulFactory.UserCertificate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import restful.sdk.API.CertificateInfo;
import restful.sdk.API.ICertificate;
import restful.sdk.API.IServerSession;
import restful.sdk.API.Property;
import restful.sdk.API.Types;
import restful.sdk.API.Types.AuthMode;
import restful.sdk.API.Types.IdentificationType;
import restful.sdk.API.Types.SharedMode;
import vn.mobileid.id.paperless.SigningService;

/**
 *
 * @author Admin
 */
public class TestCallRestful {

    public static String PATH_TO_FILE_CONFIG = "file\\RSSP.ssl2";

    public static String userName = "giatk202303010001";
    public static String passWord = "12345678";
    public static Properties prop = new Properties();
    public static IServerSession session;
    public static ICertificate crt;
    public static String certChain;

    public static void main(String[] args) throws IOException, Exception, Throwable {

//          SigningService.getInstant(3).createOwner(userName, "giatk@mobile-id.vn","0566477847", "079200011185");
//          SigningService.getInstant(3).issueCertificate(
//                  userName,
//                  "giatk@mobile-id.vn",
//                  "0566477847",
//                  "HỒ CHÍ MINH",
//                  "VN",
//                  "079200011185");

        SigningService.getInstant(3).initUser(userName, passWord);
         System.out.println(SigningService.getInstant(3).checkExist("as", passWord));
//        SigningService.getInstant(3).initUser(userName, passWord);
//        List<ICertificate> list = SigningService.getInstant(3).listUserCertificate(userName, passWord);
////        System.out.println(list.get(0).baseCredentialInfo().getCredentialID());
//        ICertificate cer = SigningService.getInstant(3).certificateInfo(userName, passWord, ((UserCertificate) list.get(0)).baseCredentialInfo().getCredentialID());
//        
//        String sad = SigningService.getInstant(3).authorize(userName, passWord, ((UserCertificate) cer).baseCredentialInfo().getCredentialID(), "12345678");
//
//        DocumentDigests document = new DocumentDigests();
//        document.setHashAlgorithmOID(Types.HashAlgorithmOID.SHA_256);
//        List<byte[]> hash = new ArrayList<>();
//        byte[] hashs = Files.readAllBytes(new File("D:\\NetBean\\paperless\\file\\result.pdf").toPath());
//        hash.add(hashs);
//        document.setHashes(hash) ;
//        byte[] image = Files.readAllBytes(new File("D:\\NetBean\\paperless\\file\\file\\300x400.png").toPath());
//        byte[] content = Files.readAllBytes(new File("D:\\NetBean\\paperless\\file\\result.pdf").toPath());
//        List<byte[]> list = SigningService.getInstant(3).signHashUser(
//                userName,
//                passWord,
//                "TKG",
//                image,
//                content,
//                null);
//        OutputStream OS = new FileOutputStream("D:\\NetBean\\paperless\\file\\testt.pdf");
//        IOUtils.write(list.get(0), OS);
//        OS.close();
    }

    private static void printUsage() {
        System.out.println("\n========== RESTFUL SDK FUNCTIONS =========");
        System.out.println("1. auth/login");
        System.out.println("2. owner/create");
        System.out.println("3. credentials/issue");
    }

    public static IServerSession Handshake_func() throws IOException {

        try {

            File file = new File(PATH_TO_FILE_CONFIG);
            InputStream stream = new FileInputStream(file);

            if (stream == null) {
                System.out.println("Can read config-file: [" + file + "]");
            }
            try (final InputStreamReader in = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                prop.load(in);
            }
            if (prop.keySet() == null) {
                System.out.println("Not found keys in [" + file + "]");
            }

            String baseUrl = prop.getProperty("mobileid.rssp.baseurl");
            String relyingParty = prop.getProperty("mobileid.rssp.rp.name");
            String relyingPartyUser = prop.getProperty("mobileid.rssp.rp.user");
            String relyingPartyPassword = prop.getProperty("mobileid.rssp.rp.password");
            String relyingPartySignature = prop.getProperty("mobileid.rssp.rp.signature");
            String relyingPartyKeyStore = prop.getProperty("mobileid.rssp.rp.keystore.file");
            String relyingPartyKeyStorePassword = prop.getProperty("mobileid.rssp.rp.keystore.password");

            Property property = new Property(baseUrl,
                    relyingParty,
                    relyingPartyUser,
                    relyingPartyPassword,
                    relyingPartySignature,
                    relyingPartyKeyStore,
                    relyingPartyKeyStorePassword);

            session = SessionFactory.getInstance(property, "en").getServerSession();
            return session;
        } catch (Throwable ex) {
            Logger.getLogger(TestCallRestful.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String getInputAsString() {
        Scanner s = new Scanner(System.in);
        return s.nextLine();
    }

    public static int getInputAsInt() throws Throwable {
        Scanner s = new Scanner(System.in);
        do {
            try {
                String str = s.nextLine();
                int number = Integer.parseInt(str);
                return number;
            } catch (Exception ex) {
                System.out.print("\nInvalid param, it must be a number: ");
            }
        } while (true);
    }

//    public static String getFileAsString(String file) throws Throwable {
//        InputStream inStream = new FileInputStream(file);
//        CertificateFactory cf = CertificateFactory.getInstance("X.509");
//        X509Certificate cert2 = (X509Certificate) cf.generateCertificate(inStream);
//        return Utils.base64Encode(cert2.getEncoded());
//    }
    public static IdentificationType getIdentificationType() throws Throwable {
        System.out.println("\tIdentification Type, select a number ");
        System.out.println("\t1. PERSONAL-ID");
        System.out.println("\t2. PASSPORT-ID");
        System.out.println("\t3. CITIZEN-IDENTITY-CARD");
        System.out.println("\t4. BUDGET-CODE");
        System.out.println("\t5. TAX-CODE");
        System.out.println("\t6. DECISION-CODE");
        System.out.println("\t7. SOCIAL-INSURANCE");
        System.out.println("\t8. UNIT-CODE");
        while (true) {
            System.out.print("Select: ");
            int sel = getInputAsInt();
            switch (sel) {
                case 1:
                    return IdentificationType.PERSONAL_ID;
                case 2:
                    return IdentificationType.PASSPORT_ID;
                case 3:
                    return IdentificationType.CITIZEN_IDENTITY_CARD;
                case 4:
                    return IdentificationType.BUDGET_CODE;
                case 5:
                    return IdentificationType.TAX_CODE;
                case 6:
                    return IdentificationType.DECISION_CODE;
                case 7:
                    return IdentificationType.SOCIAL_INSURANCE;
                case 8:
                    return IdentificationType.UNIT_CODE;
                default:
                    System.out.println("Invalid value");
                    break;
            }
        }
    }
}
