/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.util.JAXBSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;
import vn.mobileid.id.qrypto.objects.KYC;

/**
 *
 * @author GiaTK
 */
public class XSLT_PDF_Processing {

    public static void main(String[] args) throws TransformerConfigurationException, JAXBException, IOException, TransformerException, SAXException, ParserConfigurationException {
        KYC a = new KYC();
        a.setBirthDate("birth");
        a.setCurrentDate("currentdate");
        a.setDateAfterOneYear("datae after one uer");
        a.setFullName("fullname");
        a.setIssuanceDate("issurance date");
        a.setNationality("National");
        a.setPersonalNumber("perosnale number");
        a.setPlaceOfResidence("olace of residence");
        a.setPreviousDay("previous day");
        a.setPreviousMonth("previous month");
        a.setPreviousYear("previous year");

        appendData(a);
//        TransformerFactory tf = TransformerFactory.newInstance();
//        StreamSource xslt = new StreamSource("D:\\NetBean\\QryptoServices\\file\\test.xslt");
//
//        Transformer transformer = tf.newTransformer(xslt);
//
//        JAXBContext jc = JAXBContext.newInstance(KYC.class);
//        JAXBSource source = new JAXBSource(jc, a);
//        StreamResult result = new StreamResult(System.out);
//        transformer.transform(source, result);

//        Marshaller mar = jc.createMarshaller();
//        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//        mar.marshal(a, new File("D:\\NetBean\\QryptoServices\\file\\data.xml"));
//
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        try ( InputStream is = new FileInputStream("D:\\NetBean\\QryptoServices\\file\\data.xml")) {
//
//            DocumentBuilder db = dbf.newDocumentBuilder();
//
//            FileOutputStream output = new FileOutputStream("D:\\NetBean\\QryptoServices\\file\\result.html");
//
//            TransformerFactory tf = TransformerFactory.newInstance();
//
//            Transformer transformer = tf.newTransformer(
//                    new StreamSource(new File("D:\\NetBean\\QryptoServices\\file\\test.xslt")));
//
//            transformer.transform(new DOMSource(db.parse(is)), new StreamResult(output));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

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

    public static void appendData(Object ob) {
        try {
            //Test data            
            String fileTemplate = "D:\\NetBean\\QryptoServices\\file\\test.xslt";
            String fileResult = "D:\\NetBean\\QryptoServices\\file\\result.html";                        
            byte[] test = Files.readAllBytes(new File(fileTemplate).toPath());
                        
            ByteArrayInputStream inputStream = new ByteArrayInputStream(test);
            
            JAXBContext jc = JAXBContext.newInstance(ob.getClass());

            Marshaller mar = jc.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            mar.marshal(ob, out);
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try ( InputStream is = new ByteArrayInputStream(out.toByteArray())) {
                DocumentBuilder db = dbf.newDocumentBuilder();
                FileOutputStream output = new FileOutputStream(fileResult); //chua thay fileResult
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = tf.newTransformer(
                        new StreamSource(inputStream));

                transformer.transform(new DOMSource(db.parse(is)), new StreamResult(output));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JAXBException ex) {
            Logger.getLogger(XSLT_PDF_Processing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XSLT_PDF_Processing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
