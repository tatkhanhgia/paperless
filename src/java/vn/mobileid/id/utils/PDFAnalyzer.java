/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.paperless.objects.FileManagement;

/**
 *
 * @author GiaTK
 */
public class PDFAnalyzer {
    
    public static FileManagement analysisPDF(byte[] pdf){
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
            System.out.println("Page:"+page);
            System.out.println("WIDTH:"+width);
            System.out.println("heitgh:"+height);
            System.out.println("size:"+reader.getFileLength());
            return temp;
        } catch (IOException ex) {
            return null;
        }
    }
}
