/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import static vn.mobileid.id.paperless.kernel.process.ProcessELaborContract.assignAllItem;
import vn.mobileid.id.paperless.kernel_v2.GetAsset;
import vn.mobileid.id.paperless.kernel_v2.GetWorkflowDetails;
import vn.mobileid.id.paperless.kernel_v2.UpdateFileManagement;
import vn.mobileid.id.paperless.kernel_v2.UpdateWorkflowActivity;
import vn.mobileid.id.paperless.object.enumration.FileType;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.FileDataDetails;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.ItemDetails;
import vn.mobileid.id.paperless.objects.KYC;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;
import vn.mobileid.id.utils.PDFAnalyzer;
import vn.mobileid.id.utils.Utils;
import vn.mobileid.id.utils.XSLT_PDF_Processing;

/**
 *
 * @author GiaTK
 */
public class ProcessPDFGenerator {

    //<editor-fold defaultstate="collapsed" desc="Process PDF Generator">
    public static InternalResponse process(
            WorkflowActivity woAc,
            List<FileDataDetails> fileData,
            List<ItemDetails> fileItem,
            User user,
            String file_name,
            String transactionID
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Process generate QR">
        InternalResponse response = ProcessSecureQRTemplate.process_(
                woAc,
                fileData,
                fileItem,
                user,
                file_name,
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        String qrData = response.getMessage();
        String imageBase64 = Utils.getFromJson("qryptoBase64", qrData);
        byte[] qrImage = Base64.getDecoder().decode(imageBase64);
        File qr_file = File.createTempFile("QR-", ".tmp");
        File background_file = File.createTempFile("background-", ".tmp");
        FileOutputStream os = new FileOutputStream(qr_file);
        os.write(qrImage);
        os.close();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get Workflow Detail">
        response = GetWorkflowDetails.getWorkflowDetail(
                woAc.getWorkflow_id(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        //</editor-fold>

        Asset template = null;
        Asset background = null;
        Asset append = null;

        //<editor-fold defaultstate="collapsed" desc="Get Assets in Workflow Details">
        List<WorkflowAttributeType> list = (List<WorkflowAttributeType>) response.getData();
        WorkflowAttributeType attribute_Template = null;
        WorkflowAttributeType attribute_Background = null;
        WorkflowAttributeType attribute_Append = null;
        for (WorkflowAttributeType a : list) {            
            if (a.getId() == PaperlessConstant.ASSET_TYPE_TEMPLATE && (a.getValue()!=null) && Integer.parseInt((String)a.getValue())!=0) {
                attribute_Template = a;
            }
            if (a.getId() == PaperlessConstant.ASSET_TYPE_BACKGROUND && (a.getValue()!=null) && Integer.parseInt((String)a.getValue())!=0) {
                System.out.println("Back:"+a.getValue());
                attribute_Background = a;
            }
            if (a.getId() == PaperlessConstant.ASSET_TYPE_APPEND && (a.getValue()!=null) && Integer.parseInt((String)a.getValue())!=0) {
                System.out.println("Append:"+a.getValue());
                attribute_Append = a;
            }
        }

        //Get Template
        response = GetAsset.getAsset(
                attribute_Template,
                transactionID);

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        template = (Asset) response.getData();

        //Get Background
        if (attribute_Background != null) {
            response = GetAsset.getAsset(
                    attribute_Background,
                    transactionID);

            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            background = (Asset) response.getData();
        }

        //Get Append
        if (attribute_Append != null) {
            response = GetAsset.getAsset(
                    attribute_Append,
                    transactionID);

            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            append = (Asset) response.getData();
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Assign data into KYC Object">
        KYC kyc = new KYC();
        kyc = assignAllItem(fileItem);
        kyc.setQR(qr_file.getAbsolutePath());
        if (background != null) {
            os = new FileOutputStream(background_file);
            os.write(background.getBinaryData());
            os.close();
            kyc.setBackground(background_file.getAbsolutePath());
        }
        kyc.setCurrentDate(String.valueOf(LocalDate.now()));
        kyc.setDateAfterOneYear(String.valueOf(LocalDate.now().plusYears(1)));
        kyc.setPreviousDay(String.valueOf(LocalDate.now().plusDays(1).getDayOfMonth()));
        kyc.setPreviousMonth(String.valueOf(LocalDate.now().minusMonths(1).getMonthValue()));
        kyc.setPreviousYear(String.valueOf(LocalDate.now().minusYears(1).getYear()));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Append KYC into Asset Template">
        byte[] xsltC = template.getBinaryData();
        byte[] html = XSLT_PDF_Processing.appendData(kyc, xsltC);
        byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Merge pdf file if exist Asset Append">
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (append != null) {
            PdfDocument pdfDocument = new PdfDocument(new PdfWriter(bos));
            PdfMerger merger = new PdfMerger(pdfDocument);
            PdfDocument pdfDocument_original = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdf)));
            merger.merge(pdfDocument_original, 1, pdfDocument_original.getNumberOfPages());

            PdfDocument pdfDocument_append = new PdfDocument(new PdfReader(new ByteArrayInputStream(append.getBinaryData())));
            merger.merge(pdfDocument_append, 1, pdfDocument_append.getNumberOfPages());

            pdfDocument_original.close();
            pdfDocument_append.close();
            pdfDocument.close();
//            FileOutputStream oss = new FileOutputStream("C:\\Users\\Admin\\Downloads\\resultAppend.pdf");
//            oss.write(bos.toByteArray());
//            oss.close();
        } else {
            bos.write(pdf);
            bos.close();
//            FileOutputStream oss = new FileOutputStream("C:\\Users\\Admin\\Downloads\\resultWithoutAppend.pdf");
//            oss.write(pdf);
//            oss.close();
        }
        //</editor-fold>

        //Delete all Temporal Data save in Local
        qr_file.delete();
        background_file.delete();

        //<editor-fold defaultstate="collapsed" desc="Update FileManagement after process">
        FileManagement file_final = PDFAnalyzer.analysisPDF(bos.toByteArray());
        InternalResponse res = UpdateFileManagement.updateFileManagement(
                woAc.getFile().getID(),
                null,
                null,
                file_final.getPages(),
                file_final.getSize(),
                file_final.getWidth(),
                file_final.getHeight(),
                0,
                null,
                null,
                user.getEmail(),
                bos.toByteArray(),
                true,
                FileType.PDF.getName(),
                null,
                null,
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        //</editor-fold>

        
        //Update WorkflowActivity
        res = UpdateWorkflowActivity.updateMetadata(
                woAc.getId(),
                new ObjectMapper().writeValueAsString(fileItem),
                user.getName() == null ? user.getEmail() : user.getName(),
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }

        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>
}
