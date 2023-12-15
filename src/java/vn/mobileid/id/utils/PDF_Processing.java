/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.utils;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.text.BadElementException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static vn.mobileid.id.paperless.kernel_v2.Parsing.parseBoxCoordinate;
import vn.mobileid.id.paperless.objects.FileManagement;

/**
 *
 * @author GiaTK
 */
public class PDF_Processing {

    //<editor-fold defaultstate="collapsed" desc="Analysis file PDF">
    public static FileManagement analysisPDF(byte[] pdf) {
        try {
            PdfReader reader = new PdfReader(new ByteArrayInputStream(pdf));
            PdfDocument pdfDoc = new PdfDocument(reader);

            int page = pdfDoc.getNumberOfPages();
            float width = pdfDoc.getFirstPage().getPageSize().getWidth();
            float height = pdfDoc.getFirstPage().getPageSize().getWidth();
            FileManagement temp = new FileManagement();
            temp.setPages(page);
            temp.setWidth(width);
            temp.setHeight(height);
            temp.setSize(reader.getFileLength());
            return temp;
        } catch (IOException ex) {
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Append file PDF">
    public static byte[] appendPDF(byte[] originalPDF, byte[] appendedPDF) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(bos));
        PdfMerger merger = new PdfMerger(pdfDocument);
        PdfDocument pdfDocument_original = new PdfDocument(new PdfReader(new ByteArrayInputStream(originalPDF)));
        merger.merge(pdfDocument_original, 1, pdfDocument_original.getNumberOfPages());

        PdfDocument pdfDocument_append = new PdfDocument(new PdfReader(new ByteArrayInputStream(appendedPDF)));
        merger.merge(pdfDocument_append, 1, pdfDocument_append.getNumberOfPages());

        pdfDocument_original.close();
        pdfDocument_append.close();
        pdfDocument.close();

        return bos.toByteArray();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Merge file PDF">
    public static byte[] mergePDF(byte[] originalPdf, byte[] backgroundPdf) throws IOException {
        ByteArrayInputStream ins = new ByteArrayInputStream(backgroundPdf);
        ByteArrayInputStream ins2 = new ByteArrayInputStream(originalPdf);
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try (
                 PdfDocument backgroundDocument = new PdfDocument(new PdfReader(ins));  PdfDocument pdfDocument = new PdfDocument(new PdfReader(ins2), new PdfWriter(os))) {
            PdfFormXObject backgroundXObject = backgroundDocument.getPage(1).copyAsFormXObject(pdfDocument);

            for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
                PdfPage page = pdfDocument.getPage(i);
                PdfStream stream = page.newContentStreamBefore();
                new PdfCanvas(stream, page.getResources(), pdfDocument)
                        .addXObjectFittedIntoRectangle(backgroundXObject, page.getMediaBox());
            }
        }
        return os.toByteArray();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Add Image into PDF">
    public static byte[] addImage(
            byte[] pdf,
            byte[] img,
            int page,
            boolean isAllPage,
            boolean isLastPage,
            float x,
            float y,
            float width,
            float height,
            String textBelow) throws IOException, MalformedURLException, BadElementException {
        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdf));
        reader.setUnethicalReading(true);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(os));

        try ( Document document = new Document(pdfDoc)) {
            //Read image
            int maxPage = pdfDoc.getNumberOfPages();
            if (isLastPage) {
                page = maxPage;
            }

            ImageData imgData = ImageDataFactory.create(img);
            Image image = new Image(imgData)
                    .setFixedPosition(page > maxPage ? maxPage : page, x, y) // Sign the last page if pageSign is greater than maxPage
                    .setHeight(height)
                    .setWidth(width);

            //Read Font
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("resources/verdana.ttf");
            byte[] font = Utils.read(input);

            Paragraph par = new Paragraph();
//            par.setBackgroundColor(new DeviceRgb(0,0,0));
            par.setFixedPosition(x, (y - height / 5), width);
            par.setTextAlignment(TextAlignment.CENTER);
            par.setVerticalAlignment(VerticalAlignment.TOP);
            par.setHorizontalAlignment(HorizontalAlignment.CENTER);
            par.setHeight(height / 5);
            par.setFont(
                    PdfFontFactory.createFont(
                            font,
                            PdfEncodings.UTF8,
                            PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED));

            par.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);

            par.add(textBelow);

            par.setFontSize(5);

//            cell.add(par);
            document.add(par);
            document.add(image);
        }

        return os.toByteArray();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Add Image into PDF">
    public static byte[] addImage_test(
            byte[] pdf,
            byte[] img,
            int page,
            boolean isAllPage,
            boolean isLastPage,
            float x,
            float y,
            float width,
            float height,
            String textBelow,
            boolean changeRoot) throws IOException, MalformedURLException, BadElementException {

        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdf));
        reader.setUnethicalReading(true);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(os));

        try ( Document document = new Document(pdfDoc)) {
            //Read image
            int maxPage = pdfDoc.getNumberOfPages();
            if (isLastPage) {
                page = maxPage;
            }

            if (changeRoot) {
                float[] temp = Utils.changeRootPercentage(
                        x,
                        y,
                        pdfDoc.getPage(page).getPageSize().getWidth(),
                        pdfDoc.getPage(page).getPageSize().getHeight(),
                        width);
                x = temp[0];
                y = temp[1];
            }
            System.out.println("X_final:"+x);
            System.out.println("Y_final:"+y);
            System.out.println("Page:"+page);
            ImageData imgData = ImageDataFactory.create(img);
            

            //Read Font
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("resources/verdana.ttf");
            byte[] font = Utils.read(input);

            if (!isAllPage) {
                System.out.println("Not all page!");
                Image image = new Image(imgData)
                    .setFixedPosition(page > maxPage ? maxPage : page, x, y) // Sign the last page if pageSign is greater than maxPage
                    .setHeight(height)
                    .setWidth(width);
                
                Table table = new Table(1);
                table.setFixedPosition(page > maxPage ? maxPage : page, x, (y - height / 4), width);
                table.setHeight(height / 4);

                Cell cell = new Cell();
                cell.setBorder(Border.NO_BORDER);
                cell.setTextAlignment(TextAlignment.CENTER);

                Paragraph par = new Paragraph();

                par.setFont(
                        PdfFontFactory.createFont(
                                font,
                                PdfEncodings.UTF8,
                                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED));

                par.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);

                if (textBelow != null) {
                    par.add(textBelow);
                }

                IRenderer renderer = par.createRendererSubTree().setParent(
                        cell.createRendererSubTree().setParent(
                                table.createRendererSubTree().setParent(document.getRenderer())));
                LayoutArea layoutArea = new LayoutArea(page > maxPage ? maxPage : page, new Rectangle(width, height / 6));

                float lFontSize = 0.0001f, rFontSize = 10000;

                for (int i = 0; i < 100; i++) {
                    float mFontSize = (lFontSize + rFontSize) / 2;
                    par.setFontSize(mFontSize);
                    LayoutResult result = renderer.layout(new LayoutContext(layoutArea));
                    if (result.getStatus() == LayoutResult.FULL) {
                        lFontSize = mFontSize;
                    } else {
                        rFontSize = mFontSize;
                    }
                }

                float finalFontSize = lFontSize;
                par.setFontSize(finalFontSize);

                cell.add(par);
                table.addCell(cell);
                document.add(table);
                document.add(image);
            } else {
                for (int numberPage = 1; numberPage <= pdfDoc.getNumberOfPages(); numberPage++) {
                    Image image = new Image(imgData)
                    .setFixedPosition(numberPage, x, y) // Sign the last page if pageSign is greater than maxPage
                    .setHeight(height)
                    .setWidth(width);
                    
                    Table table = new Table(1);
                    table.setFixedPosition(numberPage, x, (y - height / 4), width);
                    table.setHeight(height / 4);

                    Cell cell = new Cell();
                    cell.setBorder(Border.NO_BORDER);
                    cell.setTextAlignment(TextAlignment.CENTER);

                    Paragraph par = new Paragraph();

                    par.setFont(
                            PdfFontFactory.createFont(
                                    font,
                                    PdfEncodings.UTF8,
                                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED));

                    par.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);

                    if (textBelow != null) {
                        par.add(textBelow);
                    }

                    IRenderer renderer = par.createRendererSubTree().setParent(
                            cell.createRendererSubTree().setParent(
                                    table.createRendererSubTree().setParent(document.getRenderer())));
                    LayoutArea layoutArea = new LayoutArea(numberPage, new Rectangle(width, height / 6));

                    float lFontSize = 0.0001f, rFontSize = 10000;

                    for (int i = 0; i < 100; i++) {
                        float mFontSize = (lFontSize + rFontSize) / 2;
                        par.setFontSize(mFontSize);
                        LayoutResult result = renderer.layout(new LayoutContext(layoutArea));
                        if (result.getStatus() == LayoutResult.FULL) {
                            lFontSize = mFontSize;
                        } else {
                            rFontSize = mFontSize;
                        }
                    }

                    float finalFontSize = lFontSize;
                    par.setFontSize(finalFontSize);

                    cell.add(par);
                    table.addCell(cell);
                    document.add(table);
                    document.add(image);
                }
            }
        }

        return os.toByteArray();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Page of PDF">
    public static List<Integer> getPages(
            byte[] pdf, 
            boolean isLastPage){
        try{
            PdfReader reader = new PdfReader(new ByteArrayInputStream(pdf));
            PdfDocument pdfDoc = new PdfDocument(reader);

            if(isLastPage){
                return Arrays.asList(pdfDoc.getNumberOfPages());
            } 
            List<Integer> pages = new ArrayList<>();
            for(int i=1; i<=pdfDoc.getNumberOfPages(); i++){
                pages.add(i);
            }
            return pages;
        } catch(Exception ex){
            return null;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Get Dimension of page PDF">
    public static Rectangle getPageSize(byte[] pdf){
        try{
            PdfReader reader = new PdfReader(new ByteArrayInputStream(pdf));
            PdfDocument pdfDoc = new PdfDocument(reader);
            Rectangle rect = null;
            
            for(int i=1; i<=pdfDoc.getNumberOfPages(); i++){
                rect = pdfDoc.getPage(i).getPageSize();
            }
            return rect;
        } catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Add Text into PDF">
    public static byte[] addText(
            byte[] pdf,            
            int page,
            boolean isAllPage,
            boolean isLastPage,
            float x,
            float y,
            float width,
            float height,
            String textBelow) throws IOException, MalformedURLException, BadElementException {

        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdf));
        reader.setUnethicalReading(true);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(os));

        try ( Document document = new Document(pdfDoc)) {
            //Read image
            int maxPage = pdfDoc.getNumberOfPages();
            if (isLastPage) {
                page = maxPage;
            }

            //Read Font
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream input = loader.getResourceAsStream("resources/verdana.ttf");
            byte[] font = Utils.read(input);

            if (!isAllPage) {
                System.out.println("Not all page!");
                
                Table table = new Table(1);
                table.setFixedPosition(page > maxPage ? maxPage : page, x, (y - height / 4), width);
                table.setHeight(height / 4);

                Cell cell = new Cell();
                cell.setBorder(Border.NO_BORDER);
                cell.setTextAlignment(TextAlignment.CENTER);

                Paragraph par = new Paragraph();

                par.setFont(
                        PdfFontFactory.createFont(
                                font,
                                PdfEncodings.UTF8,
                                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED));

                par.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);

                if (textBelow != null) {
                    par.add(textBelow);
                }

                IRenderer renderer = par.createRendererSubTree().setParent(
                        cell.createRendererSubTree().setParent(
                                table.createRendererSubTree().setParent(document.getRenderer())));
                LayoutArea layoutArea = new LayoutArea(page > maxPage ? maxPage : page, new Rectangle(width, height / 6));

                float lFontSize = 0.0001f, rFontSize = 10000;

                for (int i = 0; i < 100; i++) {
                    float mFontSize = (lFontSize + rFontSize) / 2;
                    par.setFontSize(mFontSize);
                    LayoutResult result = renderer.layout(new LayoutContext(layoutArea));
                    if (result.getStatus() == LayoutResult.FULL) {
                        lFontSize = mFontSize;
                    } else {
                        rFontSize = mFontSize;
                    }
                }

                float finalFontSize = lFontSize;
                par.setFontSize(finalFontSize);

                cell.add(par);
                table.addCell(cell);
                document.add(table);
            } else {
                for (int numberPage = 1; numberPage <= pdfDoc.getNumberOfPages(); numberPage++) {
                    
                    Table table = new Table(1);
                    table.setFixedPosition(numberPage, x, (y - height / 4), width);
                    table.setHeight(height / 4);

                    Cell cell = new Cell();
                    cell.setBorder(Border.NO_BORDER);
                    cell.setTextAlignment(TextAlignment.CENTER);

                    Paragraph par = new Paragraph();

                    par.setFont(
                            PdfFontFactory.createFont(
                                    font,
                                    PdfEncodings.UTF8,
                                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED));

                    par.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.FIT);

                    if (textBelow != null) {
                        par.add(textBelow);
                    }

                    IRenderer renderer = par.createRendererSubTree().setParent(
                            cell.createRendererSubTree().setParent(
                                    table.createRendererSubTree().setParent(document.getRenderer())));
                    LayoutArea layoutArea = new LayoutArea(numberPage, new Rectangle(width, height / 6));

                    float lFontSize = 0.0001f, rFontSize = 10000;

                    for (int i = 0; i < 100; i++) {
                        float mFontSize = (lFontSize + rFontSize) / 2;
                        par.setFontSize(mFontSize);
                        LayoutResult result = renderer.layout(new LayoutContext(layoutArea));
                        if (result.getStatus() == LayoutResult.FULL) {
                            lFontSize = mFontSize;
                        } else {
                            rFontSize = mFontSize;
                        }
                    }

                    float finalFontSize = lFontSize;
                    par.setFontSize(finalFontSize);

                    cell.add(par);
                    table.addCell(cell);
                    document.add(table);
                }
            }
        }

        return os.toByteArray();
    }
    //</editor-fold>
    
    public static void main(String[] args) throws Exception {
//        byte[] back = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\KHUNG GIAY CHUNG NHAN KHACH HANG.pdf"));
//        byte[] ori = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\Template Course of Hong Bang University.pdf"));
//        
//        byte[] result = mergePDF(ori, back);
//        FileOutputStream fos = new FileOutputStream("C:\\Users\\Admin\\Downloads\\test.pdf");
//        fos.write(result);
//        fos.close();

//<editor-fold defaultstate="collapsed" desc="Add Image Test">
//        byte[] pdf = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\unsign.pdf"));
//        byte[] img = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\qr2.png"));
//        String text = "validation.qrypto.ee\nhttps://www.mobile-id.vn\nhelllo";
//
//        byte[] result = addImage_test(
//                pdf,
//                img,
//                3,
//                false,
//                false,
//                20,
//                1000,
//                100,
//                100,
//                text,
//                false);
//        FileOutputStream fos = new FileOutputStream("C:\\Users\\Admin\\Downloads\\temp.pdf");
//        fos.write(result);
//        fos.close();
//</editor-fold>
        
//<editor-fold defaultstate="collapsed" desc="Get Dimension Page">
    Rectangle rect = getPageSize(Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\response.pdf")));
        System.out.println("W:"+rect.getWidth());
        System.out.println("H:"+rect.getHeight());
        System.out.println(parseBoxCoordinate("0,0,20,30", Math.round(rect.getWidth()), Math.round(rect.getHeight()), ""));
        
//</editor-fold>
    }
}
