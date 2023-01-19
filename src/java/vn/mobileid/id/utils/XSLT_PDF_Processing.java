/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.utils;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import vn.mobileid.id.qrypto.SigningService;
import vn.mobileid.id.qrypto.objects.KYC;

/**
 *
 * @author GiaTK
 */
public class XSLT_PDF_Processing {

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

    public static byte[] appendData(Object ob, byte[] xslt) {
        try {
            //Test data            

//            String fileResult = "D:\\NetBean\\QryptoServices\\file\\result.html";                                    
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xslt);

            JAXBContext jc = JAXBContext.newInstance(ob.getClass());

            Marshaller mar = jc.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            mar.marshal(ob, out);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try ( InputStream is = new ByteArrayInputStream(out.toByteArray())) {
                DocumentBuilder db = dbf.newDocumentBuilder();
                ByteArrayOutputStream output = new ByteArrayOutputStream(); //chua thay fileResult
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
            Logger.getLogger(XSLT_PDF_Processing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static byte[] convertHTMLtoPDF(byte[] contentHTML) {
        try {

//            List<String> list = Files.readAllLines(path)
//            
//            String temp = "";
//            for(String a : list){
//                temp+= a;
//            }
            String temp = new String(contentHTML, StandardCharsets.UTF_8);

//            FileOutputStream outputStream = new FileOutputStream(new File("D:\\NetBean\\QryptoServices\\file\\resul.pdf"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            ConverterProperties converter = new ConverterProperties();
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(new PageSize(PageSize.A3));

            Document document = HtmlConverter.convertToDocument(temp, pdfDoc, converter);
            document.close();

            return outputStream.toByteArray();
//            document.close();
        } catch (Exception ex) {
            Logger.getLogger(XSLT_PDF_Processing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void main(String[] arhs) throws IOException {
        //Data get from DB
        String xslt = "D:\\NetBean\\QryptoServices\\file\\test.xslt";
        String a = "D:\\NetBean\\QryptoServices\\file\\file\\CombineImage.png";
        String image = Base64.getEncoder().encodeToString(Files.readAllBytes(new File(a).toPath()));
        byte[] xsltB = Files.readAllBytes(new File(xslt).toPath());

        
        //Begin flow 
        KYC object = new KYC("TATKHANHGIA",
                "07/09/2000",
                "VN",
                "079200011188",
                "IssuanceDate",
                "PlaceOfResidence",
                "19/01/2023",
                "19/01/2024",
                "18",
                "01",
                "2022");

        byte[] html = XSLT_PDF_Processing.appendData(object, xsltB);
        
        byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);
        List<byte[]> result1 = SigningService.getInstant(3).signHashWitness("TATKHANHGIA", image, pdf);
        
        for(byte[] temp : result1){
            List<byte[]> result2 = SigningService.getInstant(3).signHashBussiness("haha", temp);
            Files.write(new File("D:\\NetBean\\QryptoServices\\file\\resultll.pdf").toPath(), result2.get(0), StandardOpenOption.CREATE);
            
        }
    }
}
