/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.paperless.objects.ItemDetails;
import vn.mobileid.id.paperless.objects.Item_JSNObject;
import vn.mobileid.id.paperless.objects.KYC_V2;

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

    //<editor-fold defaultstate="collapsed" desc="append Data Version 2">
    /**
     * Append data in Object into file XSLT
     *
     * @param items
     * @param xslt
     * @return
     */
    public static byte[] appendData(Item_JSNObject items, KYC_V2.Component component, byte[] xslt) {
        byte[] xml = createXML(items.getItems(), component);
        try {
            FileOutputStream fos = new FileOutputStream("C:\\Users\\Admin\\Downloads\\GO Paperless\\test.xml");
            fos.write(xml);
            fos.close();
        } catch (Exception ex) {
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xslt);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try ( InputStream is = new ByteArrayInputStream(xml)) {
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
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="append Data Version 2 - Constructor2">
    /**
     * Append data in Object into file XSLT
     *
     * @param items
     * @param xslt
     * @return
     */
    public static byte[] appendData(List<ItemDetails> items, KYC_V2.Component component,byte[] xslt) {
        byte[] xml = createXML(items, component);
//        try {
//            FileOutputStream fos = new FileOutputStream("C:\\Users\\Admin\\Downloads\\GO Paperless\\test.xml");
//            fos.write(xml);
//            fos.close();
//        } catch (Exception ex) {
//        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xslt);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try ( InputStream is = new ByteArrayInputStream(xml)) {
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
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Convert HTML to PDF">
    public static byte[] convertHTMLtoPDF(byte[] contentHTML, FontOfTemplate template) {
        try {
            String temp = new String(contentHTML, StandardCharsets.UTF_8);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            ConverterProperties converter = new ConverterProperties();

            FontProvider fontProvider = null;
            if (template.equals(FontOfTemplate.Elabor_Template)) {
                fontProvider = loadFont_Template();
            }
            if (template.equals(FontOfTemplate.PDF_Template)) {
                fontProvider = loadFont_PDFGen();
            }
            if (fontProvider == null) {
                System.out.println("\n\tLoad default font Provider in XSLT");
                fontProvider = loadFont_PDFGen();
            }

            converter.setFontProvider(fontProvider);
            PdfDocument pdfDoc = new PdfDocument(writer);
//            pdfDoc.setDefaultPageSize(new PageSize(PageSize.A3));

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
    public static FontProvider loadFont_Template() {
        FontProvider fontProvider = new DefaultFontProvider(false, false, false);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream input = loader.getResourceAsStream("resources/verdana.ttf");
        //Read font 1
        byte[] font1 = read(input);
        //Read font 2
        input = loader.getResourceAsStream("resources/verdana-bold.ttf");
        byte[] font2 = read(input);
        //Read Font 3
        input = loader.getResourceAsStream("resources/verdana-bold-italic.ttf");
        byte[] font3 = read(input);
        //Read Font 3
        input = loader.getResourceAsStream("resources/OpendyslexicRegular-nRLZ0.otf");
        byte[] font4 = read(input);

        fontProvider.addFont(font1);
        fontProvider.addFont(font2);
        fontProvider.addFont(font3);
        fontProvider.addFont(font4);

        return fontProvider;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Load Font">
    public static FontProvider loadFont_PDFGen() {
        FontProvider fontProvider = new DefaultFontProvider(false, false, false);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        //Read Font 4
        InputStream input = loader.getResourceAsStream("resources/fonts/FontUnicode/times.ttf");
        byte[] font_time_1 = read(input);
        //Read Font 5
        input = loader.getResourceAsStream("resources/fonts/FontUnicode/timesbd.ttf");
        byte[] font_time_2 = read(input);
        //Read Font 6
        input = loader.getResourceAsStream("resources/fonts/FontUnicode/timesbi.ttf");
        byte[] font_time_3 = read(input);
        //Read Font 7
        input = loader.getResourceAsStream("resources/fonts/FontUnicode/timesi.ttf");
        byte[] font_time_4 = read(input);

        fontProvider.addFont(font_time_1);
        fontProvider.addFont(font_time_2);
        fontProvider.addFont(font_time_3);
        fontProvider.addFont(font_time_4);
        return fontProvider;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Read font">
    private static byte[] read(InputStream input) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4];
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create XML">
    public static byte[] createXML(List<ItemDetails> items, KYC_V2.Component component) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("KYC");
            doc.appendChild(rootElement);

            //Info Element
            Element infoElement = doc.createElement("Info");
            rootElement.appendChild(infoElement);
            for (ItemDetails item : items) {
                Element element = doc.createElement(item.getField());
                element.setTextContent((String) item.getValue());
                infoElement.appendChild(element);
            }

            //Gen Schema from KYC/Component
            Element componentElement = doc.createElement("Component");
            rootElement.appendChild(componentElement);
            Method[] methods = KYC_V2.Component.class.getDeclaredMethods();
            for (Method method : methods) {
                Annotation[] annotations = method.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType() == XmlElement.class) {
                        XmlElement temp = (XmlElement)annotation;
                        
                        Element element = doc.createElement(temp.name());
                        element.setTextContent((String)method.invoke(component));
                        componentElement.appendChild(element);
                    }
                }
            }
            

            //Write to OS
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(output);

            transformer.transform(source, result);
            return output.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    public static enum FontOfTemplate {
        Elabor_Template(1),
        PDF_Template(2);

        private int id;

        private FontOfTemplate(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public static void main(String[] arhs) throws Exception {
        //===============Convert XSLT to HTML => PDF==================================
//        byte[] xslt = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\response.xslt"));
//        KYC object = new KYC();
//        object.setBirthDate("07/09/2000");
//        object.setSpecialized("CÔNG NGHỆ THÔNG TIN");
//        object.setFullName("Tất Khánh Gia");
//        object.setGraduationType("Giỏi");
//        object.setQR("C:\\Users\\Admin\\Downloads\\qr2.png");
//        object.setValidationUrl("https://validation.qrypto.ee.com");
//        object.setWidth(800 / 5);
//        object.setHeight(800 / 5);
//        object.setGender("Ông");
//
//        object.setBackground("C:\\Users\\Admin\\Downloads\\background_1.png");

//        byte[] html = appendData(object, xslt);
//        byte[] pdf = convertHTMLtoPDF(html, FontOfTemplate.Elabor_Template);
//        FileOutputStream fos = new FileOutputStream("C:\\Users\\Admin\\Downloads\\result.pdf");
//        fos.write(pdf);
//        fos.close();
//        //==========Append Background + QR Image into XSLT==============================
//        FileOutputStream os = new FileOutputStream("C:\\Users\\Admin\\Downloads\\temp.tmp");
//        os.write(Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Desktop\\BG\\329247033_3020219851607214_1964181824628587693_n.jpg")));
//        os.close();
//        KYC temp = new KYC();
//        temp.setFullName("Châu Trần Anh Thư");
//        temp.setIssuanceDate("1234");
//        temp.setBackground("C:\\Users\\Admin\\Downloads\\hello.bmp");
//        temp.setQR("C:\\Users\\Admin\\Downloads\\1.jpg");
//        temp.setWidth(10);
//        temp.setHeight(10);
//        byte[] html = XSLT_PDF_Processing.appendData(temp,
//                Files.readAllBytes(new File("C:\\Users\\Admin\\Downloads\\test.xslt").toPath()));
//        byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);
//        try ( FileOutputStream fileOuputStream = new FileOutputStream("C:\\Users\\Admin\\Downloads\\afterappend.pdf")) {
//            fileOuputStream.write(pdf);
//        }
//
//        //Append pdf into final file
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(bos));
//        PdfMerger merger = new PdfMerger(pdfDocument);
//
//        PdfDocument pdfDocument_original = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdf)));
//        merger.merge(pdfDocument_original, 1, pdfDocument_original.getNumberOfPages());
//        
//        PdfDocument pdfDocument_append = new PdfDocument(new PdfReader("C:\\Users\\Admin\\Downloads\\result.pdf"));
//        merger.merge(pdfDocument_append, 1, pdfDocument_append.getNumberOfPages());
//        
//        pdfDocument_original.close();
//        pdfDocument_append.close();
//        pdfDocument.close();
//        
//        try ( FileOutputStream fileOuputStream = new FileOutputStream("C:\\Users\\Admin\\Downloads\\afterappendnewPdf.pdf")) {
//            fileOuputStream.write(bos.toByteArray());
//        }
//
//        //Delete temporal file
//        Files.delete(Paths.get("C:\\Users\\Admin\\Downloads\\temp.tmp"));

        //Create XML
        String jsonString = "{\"items\":[{\"field\":\"FullName\",\"field_name\":\"Full Name\",\"field_name_vn\":\"Họ và Tên\",\"type\":1,\"value\":\"Tata Khanh Gia\",\"mandatory_enable\":false,\"remark\":\"text\"},{\"field\":\"BirthDate\",\"field_name\":\"Birth Date\",\"field_name_vn\":\"Ngày sinh\",\"type\":1,\"value\":\"07/09/2000\",\"mandatory_enable\":false,\"remark\":\"date\"},{\"field\":\"Nationality\",\"field_name\":\"Nationality\",\"field_name_vn\":\"Quốc tịch\",\"type\":1,\"value\":\"Viet Nam\",\"mandatory_enable\":false,\"remark\":\"text\"},{\"field\":\"PersonalNumber\",\"field_name\":\"Personal Number\",\"field_name_vn\":\"Căn cước công dân\",\"type\":1,\"value\":\"079200011188\",\"mandatory_enable\":false,\"remark\":\"text\"},{\"field\":\"IssuanceDate\",\"field_name\":\"Issuance Date\",\"field_name_vn\":\"Ngày cấp\",\"type\":1,\"value\":\"hello\",\"mandatory_enable\":false,\"remark\":\"date\"},{\"field\":\"PlaceOfResidence\",\"field_name\":\"Place Of Residence\",\"field_name_vn\":\"Nơi thường trú\",\"type\":1,\"value\":\"here\",\"mandatory_enable\":false,\"remark\":\"text\"}]}";
        Item_JSNObject json = new ObjectMapper().readValue(jsonString, Item_JSNObject.class);

        KYC_V2.Component components = new KYC_V2.Component();
        components.setBackground("C:\\Users\\Admin\\Downloads\\background.png");
        components.setValidationUrl("LeuLeu");

        byte[] bytes = appendData(json, components, Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\response.xslt")));
        byte[] bytes2 = convertHTMLtoPDF(bytes, XSLT_PDF_Processing.FontOfTemplate.PDF_Template);
        FileOutputStream fos = new FileOutputStream("C:\\Users\\Admin\\Downloads\\Course - Version2.pdf");
        fos.write(bytes2);
        fos.close();
    }
}
