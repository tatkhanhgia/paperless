/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.utils;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.font.FontProvider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.NodeList;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.paperless.objects.ItemDetails;
import vn.mobileid.id.paperless.objects.Item_JSNObject;
import vn.mobileid.id.paperless.objects.KYC;

/**
 *
 * @author GiaTK
 */
public class XSLT_PDF_Processing {

    final private static Logger LOG = LogManager.getLogger(XSLT_PDF_Processing.class);

    //<editor-fold defaultstate="collapsed" desc="Gen Schema">
    public static void genSchema(Object ob) throws JAXBException, IOException {
        JAXBContext jc = JAXBContext.newInstance(ob.getClass());
        jc.generateSchema(new SchemaOutputResolver() {

            @Override
            public Result createOutput(String namespaceURI, String suggestedFileName)
                    throws IOException {
                StreamResult result = new StreamResult(System.out);
                result.setSystemId(suggestedFileName);
                return result;
            }

        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="append Data">
    /**
     * Append data in Object into file XSLT
     *
     * @param ob
     * @param xslt
     * @return
     */
    public static byte[] appendData(Object ob, byte[] xslt) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xslt);

            JAXBContext jc = JAXBContext.newInstance(ob.getClass());

            Marshaller mar = jc.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            mar.marshal(ob, out);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try ( InputStream is = new ByteArrayInputStream(out.toByteArray())) {
                DocumentBuilder db = dbf.newDocumentBuilder();

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = tf.newTransformer(
                        new StreamSource(inputStream));

                StreamResult result = new StreamResult(output);
                transformer.transform(new DOMSource(db.parse(is)), result);

                return output.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JAXBException ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while append User Data into XSLT! - Detail" + ex);
            }
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Convert HTML to PDF">
    public static byte[] convertHTMLtoPDF(byte[] contentHTML) {
        try {
            String temp = new String(contentHTML, StandardCharsets.UTF_8);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            ConverterProperties converter = new ConverterProperties();

            FontProvider fontProvider = loadFont();
            converter.setFontProvider(fontProvider);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(new PageSize(PageSize.A3));

            com.itextpdf.layout.Document document = HtmlConverter.convertToDocument(temp, pdfDoc, converter);
            document.close();

            return outputStream.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while append User Data into XSLT! - Detail" + ex);
            }
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Value in XSLT">
    public static Item_JSNObject getValueFromXSLT(byte[] xslt) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xslt);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(inputStream);

            Item_JSNObject item = new Item_JSNObject();
            doc.getDocumentElement().normalize();
            NodeList map = doc.getElementsByTagName("xsl:value-of");
            for (int i = 0; i < map.getLength(); i++) {
                ItemDetails detail = new ItemDetails();
                String value = map.item(i).getAttributes().item(0).getFirstChild().getNodeValue();
                detail.setField(value);
                detail.setMandatory_enable(true);
                detail.setType(1);
                detail.setValue(value);
                item.appendData(detail);
            }
            return item;
        } catch (Exception ex) {
            LogHandler.error(
                    XSLT_PDF_Processing.class,
                    "Cannot parse Data XSLT to object",
                    ex);
        }
        return null;
    }
    //</editor-fold>   

    //<editor-fold defaultstate="collapsed" desc="Load Font">
    public static FontProvider loadFont() {
        try {
            FontProvider fontProvider = new DefaultFontProvider(false, false, false);
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("resources/verdana.ttf");

            //Read font 1
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4];
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] font1 = buffer.toByteArray();

            //Read font 2
            input = loader.getResourceAsStream("resources/verdana-bold.ttf");
            buffer = new ByteArrayOutputStream();
            nRead = 0;
            data = new byte[4];
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] font2 = buffer.toByteArray();

            //Read Font 3
            input = loader.getResourceAsStream("resources/verdana-bold-italic.ttf");
            buffer = new ByteArrayOutputStream();
            nRead = 0;
            data = new byte[4];
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] font3 = buffer.toByteArray();

            fontProvider.addFont(font1);
            fontProvider.addFont(font2);
            fontProvider.addFont(font3);
            return fontProvider;
        } catch (IOException ex) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Cannot read font file");
            }
            return null;
        }
    }
    //</editor-fold>

    public static void main(String[] arhs) throws IOException {
        //==========Append Background + QR Image into XSLT==============================
        FileOutputStream os = new FileOutputStream("C:\\Users\\Admin\\Downloads\\temp.tmp");
        os.write(Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Desktop\\BG\\329247033_3020219851607214_1964181824628587693_n.jpg")));
        os.close();
        KYC temp = new KYC();
        temp.setFullName("Châu Trần Anh Thư");
        temp.setIssuanceDate("1234");
        temp.setBackground("C:\\Users\\Admin\\Downloads\\hello.bmp");
        temp.setQR("C:\\Users\\Admin\\Downloads\\1.jpg");
        temp.setWidth(10);
        temp.setHeight(10);
        byte[] html = XSLT_PDF_Processing.appendData(temp,
                Files.readAllBytes(new File("C:\\Users\\Admin\\Downloads\\test.xslt").toPath()));
        byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);
        try ( FileOutputStream fileOuputStream = new FileOutputStream("C:\\Users\\Admin\\Downloads\\afterappend.pdf")) {
            fileOuputStream.write(pdf);
        }

        //Append pdf into final file
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(bos));
        PdfMerger merger = new PdfMerger(pdfDocument);

        PdfDocument pdfDocument_original = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdf)));
        merger.merge(pdfDocument_original, 1, pdfDocument_original.getNumberOfPages());
        
        PdfDocument pdfDocument_append = new PdfDocument(new PdfReader("C:\\Users\\Admin\\Downloads\\result.pdf"));
        merger.merge(pdfDocument_append, 1, pdfDocument_append.getNumberOfPages());
        
        pdfDocument_original.close();
        pdfDocument_append.close();
        pdfDocument.close();
        
        try ( FileOutputStream fileOuputStream = new FileOutputStream("C:\\Users\\Admin\\Downloads\\afterappendnewPdf.pdf")) {
            fileOuputStream.write(bos.toByteArray());
        }

        //Delete temporal file
        Files.delete(Paths.get("C:\\Users\\Admin\\Downloads\\temp.tmp"));

        //Get data from XSLT
//        Item_JSNObject item = XSLT_PDF_Processing.getValueFromXSLT(Files.readAllBytes(new File("D:\\NetBean\\qrypto\\file\\test.xslt").toPath()));
//        System.out.println(new ObjectMapper().writeValueAsString(item));
        //Data get from DB
//        String xslt = "D:\\NetBean\\qrypto\\file\\test.xslt";
//        String a = "D:\\NetBean\\qrypto\\file\\file\\300x400.png";
//        String image = Base64.getEncoder().encodeToString(Files.readAllBytes(new File(a).toPath()));
//        byte[] xsltB = Files.readAllBytes(new File(xslt).toPath());
        //Begin flow 
//        KYC object = new KYC("TATKHANHGIA",
//                "07/09/2000",
//                "VN",
//                "079200011188",
//                "IssuanceDate",
//                "PlaceOfResidence",
//                "19/01/2023",
//                "19/01/2024",
//                "18",
//                "01",
//                "2022");
//        KYC object = new KYC();
//
//        byte[] html = XSLT_PDF_Processing.appendData(object, xsltB);
//
//        byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);
//        Files.write(new File("D:\\NetBean\\qrypto\\file\\result.pdf").toPath(), pdf, StandardOpenOption.CREATE);
//        List<byte[]> result1 = SigningService.getInstant(3).signHashWitness("TATKHANHGIA", image, pdf);
//        
//        for(byte[] temp : result1){
//            List<byte[]> result2 = SigningService.getInstant(3).signHashBussiness( temp);
//            Files.write(new File("D:\\NetBean\\qrypto\\file\\result.pdf").toPath(), result2.get(0), StandardOpenOption.CREATE);
//            
//        }
    }
}
