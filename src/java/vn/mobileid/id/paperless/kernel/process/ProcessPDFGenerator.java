/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel.process;

import vn.mobileid.id.paperless.kernel_v2.CreateQRSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.QryptoService;
import vn.mobileid.id.paperless.exception.LoginException;
import vn.mobileid.id.paperless.kernel.GetTransaction;
import static vn.mobileid.id.paperless.kernel.process.ProcessELaborContract.assignAllItem;
import vn.mobileid.id.paperless.kernel_v2.CreateWorkflowActivity;
import vn.mobileid.id.paperless.kernel_v2.GetAsset;
import vn.mobileid.id.paperless.kernel_v2.GetFileManagement;
import vn.mobileid.id.paperless.kernel_v2.GetQRSize;
import vn.mobileid.id.paperless.kernel_v2.GetWorkflowActivity;
import vn.mobileid.id.paperless.kernel_v2.GetWorkflowDetails;
import vn.mobileid.id.paperless.kernel_v2.UpdateCSVTask;
import vn.mobileid.id.paperless.kernel_v2.UpdateFileManagement;
import vn.mobileid.id.paperless.kernel_v2.UpdateWorkflowActivity;
import vn.mobileid.id.paperless.object.enumration.FileType;
import vn.mobileid.id.paperless.object.enumration.QRBackground;
import vn.mobileid.id.paperless.object.enumration.WorkflowAttributeTypeName;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.Enterprise_SigningInfo;
import vn.mobileid.id.paperless.objects.FileDataDetails;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.ItemDetails;
import vn.mobileid.id.paperless.objects.KYC;
import vn.mobileid.id.paperless.objects.KYC_V2;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.QRPositionProperties;
import vn.mobileid.id.paperless.objects.ProcessWorkflowActivity_CSV_JSNObject;
import vn.mobileid.id.paperless.objects.QRSize;
import vn.mobileid.id.paperless.objects.Transaction;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;
import vn.mobileid.id.qrypto.object.Configuration;
import vn.mobileid.id.qrypto.object.QRSchema;
import vn.mobileid.id.qrypto.object.qryptoEffectiveDate;
import vn.mobileid.id.qrypto.response.DownloadFileTokenResponse;
import vn.mobileid.id.qrypto.response.IssueQryptoWithFileAttachResponse;
import vn.mobileid.id.utils.PDF_Processing;
import vn.mobileid.id.utils.Utils;
import vn.mobileid.id.utils.XSLT_PDF_Processing;
import vn.mobileid.id.utils.ZipProcessing;

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
            boolean returnBinary,
            String transactionID
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Process generate QR">
        InternalResponse response = ProcessSecureQRTemplate.process_(
                woAc,
                fileData,
                fileItem,
                user,
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
        QRSize qrSize = null;
        boolean enableURL = false;
        boolean backgroundAsAnImage = false;

        //<editor-fold defaultstate="collapsed" desc="Get Assets and QR Size in Workflow Details">
        List<WorkflowAttributeType> list = (List<WorkflowAttributeType>) response.getData();
        WorkflowAttributeType attribute_Template = null;
        WorkflowAttributeType attribute_Background = null;
        WorkflowAttributeType attribute_Append = null;
        WorkflowAttributeType attribute_qrSize = null;
        WorkflowAttributeType attribute_enable_URL = null;

        for (WorkflowAttributeType a : list) {
            if (a.getId() == WorkflowAttributeTypeName.ASSET_TEMPLATE.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                attribute_Template = a;
            }
            if (a.getId() == WorkflowAttributeTypeName.ASSET_BACKGROUND.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                attribute_Background = a;
            }
            if (a.getId() == WorkflowAttributeTypeName.ASSET_APPEND.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                attribute_Append = a;
            }
            if (a.getId() == WorkflowAttributeTypeName.QR_SIZE.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                attribute_qrSize = a;
            }
            if (a.getId() == WorkflowAttributeTypeName.URL_CODE.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                attribute_enable_URL = a;
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

        //Get QR Size
        if (attribute_qrSize != null) {
            response = GetQRSize.getQRSize(String.valueOf(attribute_qrSize.getValue()), transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            qrSize = (QRSize) response.getData();
        }

        //Get URL Enable
        if (attribute_enable_URL != null) {
            enableURL = Integer.parseInt((String) attribute_enable_URL.getValue()) == 1;
            System.out.println("Process PDF Generator - Enable URL:" + enableURL);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Assign data into KYC Object">
        KYC kyc = new KYC();
        kyc = assignAllItem(fileItem);
        kyc.setQR(qr_file.getAbsolutePath());
        if (background != null) {
            //<editor-fold defaultstate="collapsed" desc="If Background is an Image">
            if (Utils.isImage(background.getName())) {
                backgroundAsAnImage = true;
                os = new FileOutputStream(background_file);
                os.write(background.getBinaryData());
                os.close();
                kyc.setBackground(background_file.getAbsolutePath());
            }
            //</editor-fold>
        }
        kyc.setWidth(qrSize.getSize() / 5);
        kyc.setHeight(qrSize.getSize() / 5);
        if (enableURL) {
            kyc.setValidationUrl(PolicyConfiguration.getInstant().getURLofQR().getUrl());
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        System.out.println("SignDate:" + kyc.getSignDate());
        if (kyc.getSignDate() != null && !kyc.getSignDate().isEmpty()) {
            try {
                calendar.setTime(Utils.getDateFromString(kyc.getSignDate()));
            } catch (Exception ex) {
                calendar.setTime(kyc.getDate() == null ? new Date(System.currentTimeMillis()) : kyc.getDate());
            }
        } else {
            calendar.setTime(kyc.getDate() == null ? new Date(System.currentTimeMillis()) : kyc.getDate());
        }
        kyc.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        kyc.setMonth(calendar.get(Calendar.MONTH) + 1);
        kyc.setYear(calendar.get(Calendar.YEAR));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Append KYC into Asset Template">
        byte[] xsltC = template.getBinaryData();
        byte[] html = XSLT_PDF_Processing.appendData(kyc, xsltC);
        byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html, XSLT_PDF_Processing.FontOfTemplate.PDF_Template);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="If background is a PDF => Merge background as an background of originalPDF">
        if (!backgroundAsAnImage && background != null) {
            byte[] background_pdf = background.getBinaryData();
            pdf = PDF_Processing.mergePDF(pdf, background_pdf);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Merge pdf file if exist Asset Append">
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (append != null) {
            bos.write(PDF_Processing.appendPDF(pdf, append.getBinaryData()));
        } else {
            bos.write(pdf);
            bos.close();
        }
        byte[] result = bos.toByteArray();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Delete all Temporal Data save in local">
        qr_file.delete();
        background_file.delete();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Update FileManagement after process">
        FileManagement file_final = PDF_Processing.analysisPDF(result);
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
                result,
                true,
                FileType.PDF.getName(),
                null,
                null,
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Update metadata of Workflow Activity">
        res = UpdateWorkflowActivity.updateMetadata(
                woAc.getId(),
                new ObjectMapper().writeValueAsString(fileItem),
                user.getName() == null ? user.getEmail() : user.getName(),
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        //</editor-fold>

        if (returnBinary) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                    result
            );
        } else {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                    ""
            );
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Process PDF Generator Version 2">
    public static InternalResponse processV2(
            WorkflowActivity woAc,
            List<FileDataDetails> fileData,
            List<ItemDetails> fileItem,
            User user,
            boolean returnBinary,
            String transactionID
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Get Workflow Detail">
        InternalResponse response = GetWorkflowDetails.getWorkflowDetail(
                woAc.getWorkflow_id(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        //</editor-fold>

        Asset template = null;
        Asset background = null;
        Asset append = null;
        QRSize qrSize = null;
        boolean enableURL = false;
        QRPositionProperties positionQR = null;

        //<editor-fold defaultstate="collapsed" desc="Get Assets and QR Size in Workflow Details">
        List<WorkflowAttributeType> list = (List<WorkflowAttributeType>) response.getData();
        WorkflowAttributeType attribute_Template = null;
        WorkflowAttributeType attribute_Background = null;
        WorkflowAttributeType attribute_Append = null;
        WorkflowAttributeType attribute_qrSize = null;
        WorkflowAttributeType attribute_enable_URL = null;
        WorkflowAttributeType attribute_qrBackground = null;

        for (WorkflowAttributeType a : list) {
            if (a.getId() == WorkflowAttributeTypeName.ASSET_TEMPLATE.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                attribute_Template = a;
            }
            if (a.getId() == WorkflowAttributeTypeName.ASSET_BACKGROUND.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                attribute_Background = a;
            }
            if (a.getId() == WorkflowAttributeTypeName.ASSET_APPEND.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                attribute_Append = a;
            }
            if (a.getId() == WorkflowAttributeTypeName.QR_SIZE.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                attribute_qrSize = a;
            }
            if (a.getId() == WorkflowAttributeTypeName.URL_CODE.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                attribute_enable_URL = a;
            }
            if (a.getId() == WorkflowAttributeTypeName.QR_BACKGROUND.getId() && (a.getValue() != null)) {
                attribute_qrBackground = a;
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

        //Get PositionQRProperties in metadata of Asset
        System.out.println("Template ID:" + attribute_Template.getId());
        response = GetAsset.getMetadataOfAsset(
                template.getId(),
                transactionID);

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        try {
            Asset asset = (Asset) response.getData();
            Enterprise_SigningInfo ent = new ObjectMapper().readValue(asset.getMetadata(), Enterprise_SigningInfo.class);
            positionQR = ent.getQrProperties();
        } catch (JsonProcessingException ex) {
            System.err.println("PDF Generator -  Cannot parse String in metadata into PositionQRProperties");
        }

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

        //Get QR Size
        if (attribute_qrSize != null) {
            response = GetQRSize.getQRSize(String.valueOf(attribute_qrSize.getValue()), transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            qrSize = (QRSize) response.getData();
        }

        //Get URL Enable
        if (attribute_enable_URL != null) {
            enableURL = Integer.parseInt((String) attribute_enable_URL.getValue()) == 1;
            System.out.println("Process PDF Generator - Enable URL:" + enableURL);
        }

        //Get QR Background
        if (attribute_qrBackground != null) {
            boolean transparent = QRBackground.isTransparent((String) attribute_qrBackground.getValue());
            positionQR.setIsTransparent(transparent);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Processing">
        if (positionQR == null
                || Utils.isNullOrEmpty(positionQR.getPages())) {
            System.err.println("Using append");
            response = flow_AppendIntoPDF(
                    woAc,
                    fileData,
                    fileItem,
                    user,
                    template,
                    background,
                    append,
                    qrSize,
                    enableURL,
                    returnBinary,
                    transactionID);
        } else {
            System.err.println("Gen from position");
            response = flow_GenFromPositionQR(
                    woAc,
                    fileData,
                    fileItem,
                    user,
                    template,
                    background,
                    append,
                    qrSize,
                    enableURL,
                    positionQR,
                    returnBinary,
                    transactionID);
        }
        //</editor-fold>

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        //<editor-fold defaultstate="collapsed" desc="If enable URL">
        byte[] finalPdf = (byte[]) response.getData();
        FileOutputStream fos = new FileOutputStream("C:\\Users\\Admin\\Downloads\\response22.pdf");
        fos.write(finalPdf);
        fos.close();
        if (enableURL) {
            //<editor-fold defaultstate="collapsed" desc="Change root => Parse from top - left into bottom left">
            float[] changes = Utils.changeRootPercentage(
                    positionQR.getX(),
                    positionQR.getY(),
                    PDF_Processing.getPageSize(finalPdf).getWidth(),
                    PDF_Processing.getPageSize(finalPdf).getHeight(),
                    qrSize.getPoint());
            //</editor-fold>
            
            finalPdf = PDF_Processing.addText(
                    finalPdf,
                    positionQR.getPages().get(0),
                    false,
                    false,
                    changes[0],
                    changes[1],
                    qrSize.getPoint(),
                    qrSize.getPoint(),
                    PolicyConfiguration.getInstant().getURLofQR().getUrl());
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Update FileManagement after process">
        FileManagement file_final = PDF_Processing.analysisPDF(finalPdf);
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
                finalPdf,
                true,
                FileType.PDF.getName(),
                null,
                null,
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Update metadata of Workflow Activity">
        res = UpdateWorkflowActivity.updateMetadata(
                woAc.getId(),
                new ObjectMapper().writeValueAsString(fileItem),
                user.getName() == null ? user.getEmail() : user.getName(),
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        //</editor-fold>

        if (returnBinary) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                    finalPdf
            );
        } else {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                    ""
            );
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Process PDF Generator and return Binary data">
    public static InternalResponse process(
            WorkflowActivity woAc,
            List<FileDataDetails> fileData,
            List<ItemDetails> fileItem,
            User user,
            String transactionID
    ) throws Exception {
        return process(woAc, fileData, fileItem, user, false, transactionID);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Process CSV of PDF Generator">
    public static InternalResponse processCSV(
            WorkflowActivity woAc,
            List<ProcessWorkflowActivity_CSV_JSNObject.Buffer> listItem,
            User user,
            String transactionID
    ) throws IOException, Exception {
        List<String> names = new ArrayList<>();
        List<byte[]> pdfs = new ArrayList<>();

        for (ProcessWorkflowActivity_CSV_JSNObject.Buffer object : listItem) {
            //<editor-fold defaultstate="collapsed" desc="Create workflow Activity child of PDF Generator">
            InternalResponse response = CreateWorkflowActivity.processingCreateWorkflowActivity_typePDF(
                    woAc,
                    woAc.getCsv().getId(),
                    user,
                    transactionID);
            //</editor-fold>

            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            Long woAcChild = (long) response.getData();

            //Get WoAC
            response = GetWorkflowActivity.getWorkflowActivity(woAcChild.intValue(), transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            WorkflowActivity child = (WorkflowActivity) response.getData();

            //Get Transaction
            response = GetTransaction.getTransaction(child.getTransaction(), transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            Transaction transaction = (Transaction) response.getData();

            //Get File Management
            response = GetFileManagement.getFileManagement_NoneGetFromFMS(transaction.getObject_id(), transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            FileManagement file = (FileManagement) response.getData();
            child.setFile(file);

            response = ProcessPDFGenerator.process(
                    child,
                    null,
                    object.getItemDetails(),
                    user,
                    true,
                    transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }

            pdfs.add((byte[]) response.getData());
            names.add(file.getName());
        }

        //Zip file
        byte[] zip = ZipProcessing.zipFile(names, pdfs);

        //Update CSVTask
        return UpdateCSVTask.updateCSV(
                woAc.getCsv().getId(),
                null,
                null,
                0,
                zip.length,
                null,
                null,
                user.getName() == null ? user.getEmail() : user.getName(),
                zip,
                transactionID);
    }
//</editor-fold>

    //==========================================================================
    //<editor-fold defaultstate="collapsed" desc="Create QR">
    /**
     * Create PDF Stamping
     *
     * @param items
     * @param fileData
     * @param x
     * @param y
     * @param qrSize
     * @param transparent
     * @param pages
     * @param timestamp1
     * @param timestamp2
     * @param transactionId
     * @return
     * @throws Exception
     */
    private static IssueQryptoWithFileAttachResponse createQR(
            List<ItemDetails> items,
            byte[] fileData,
            float x,
            float y,
            float qrSize,
            float qrPoint,
            boolean transparent,
            List<Integer> pages,
            Date timestamp1,
            String transactionId
    ) throws Exception {
        try {
            //<editor-fold defaultstate="collapsed" desc="Create Configuration">
            Configuration config = new Configuration();
            config.setContextIdentifier("QC1:");
            config.setIsTransparent(transparent);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Calendar now = Calendar.getInstance();
            now.setTime(timestamp1);
            now.add(Calendar.DATE, ((Long) PolicyConfiguration.getInstant().getQR_ExpiredTime().getQr_expired_time()).intValue());

            qryptoEffectiveDate date = new qryptoEffectiveDate(
                    dateFormat.format(timestamp1),
                    dateFormat.format(now.getTime()));
            config.setQryptoEffectiveDate(date);
            config.setQryptoDimension(Math.round(qrSize));
            config.setGetFileTokenList(true);
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Create QRSchema">
            FileDataDetails file = new FileDataDetails();
            file.setValue(fileData);
            file.setFile_field("FileGenerator.pdf");

            ItemDetails file_ = new ItemDetails();
            file_.setField("FilePDF");
            file_.setType(5);
            file_.setFile_format("application/pdf");
            file_.setFile_name("PDFGenerator.pdf");
            file_.setFile_field("FileGenerator.pdf");

            items.add(file_);

            QRSchema.QR_META_DATA positionQR = new QRSchema.QR_META_DATA();
            positionQR.setxCoordinator(Math.round(x));
            positionQR.setyCoordinator(Math.round(y));
            positionQR.setIsTransparent(transparent);
            positionQR.setQrDimension(Math.round(qrPoint));
            positionQR.setPageNumber(pages);

            QRSchema qrSchema = CreateQRSchema.createQRSchema(
                    Arrays.asList(file),
                    items,
                    positionQR,
                    transactionId);
            System.out.println("QRSchema:" + new ObjectMapper().writeValueAsString(qrSchema));
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Call Qrypto">
            try {
                IssueQryptoWithFileAttachResponse QRdata = QryptoService
                        .getInstance(1)
                        .generateQR(qrSchema, config, "tran");
                return QRdata;
            } catch (LoginException ex) {
                QryptoService.getInstance(1).login();
                IssueQryptoWithFileAttachResponse QRdata = QryptoService
                        .getInstance(1)
                        .generateQR(qrSchema, config, "tran");
                return QRdata;
            }

            //</editor-fold>
        } catch (Exception ex) {
            System.out.println("Cannot create QR");
            ex.printStackTrace();
            return null;
        } catch (Throwable ex) {
            System.out.println("Cannot create QR");
            ex.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Flow 1 : PDF Generator tranditional (Append data into xslt)">
    private static InternalResponse flow_AppendIntoPDF(
            WorkflowActivity woAc,
            List<FileDataDetails> fileData,
            List<ItemDetails> fileItem,
            User user,
            Asset template,
            Asset background,
            Asset append,
            QRSize qrSize,
            boolean enableURL,
            boolean returnBinary,
            String transactionID
    ) throws Exception {
        System.out.println("Background:" + background == null);
        System.out.println("Append:" + append == null);
        //<editor-fold defaultstate="collapsed" desc="Process generate QR">
        InternalResponse response = ProcessSecureQRTemplate.process_(
                woAc,
                fileData,
                fileItem,
                user,
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

        //<editor-fold defaultstate="collapsed" desc="Assign data into KYC Object">
        KYC_V2.Component kyc = new KYC_V2.Component();

        kyc.setQR(qr_file.getAbsolutePath());
        if (background != null) {
            //<editor-fold defaultstate="collapsed" desc="If Background is an Image">
            if (Utils.isImage(background.getName()) && Utils.isImage(background.getBinaryData())) {
                os = new FileOutputStream(background_file);
                os.write(background.getBinaryData());
                os.close();
                kyc.setBackground(background_file.getAbsolutePath());
            }
            //</editor-fold>
        }
        kyc.setWidth(String.valueOf(Utils.calculateQRSize(qrSize.getSize())));
        kyc.setHeight(String.valueOf(Utils.calculateQRSize(qrSize.getSize())));
        if (enableURL) {
            kyc.setValidationUrl(PolicyConfiguration.getInstant().getURLofQR().getUrl());
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        String signDate = null;
        for (ItemDetails item : fileItem) {
            if (item.getField().contains("SignDate")) {
                signDate = (String) item.getValue();
            }
        }
        if (signDate != null && !signDate.isEmpty()) {
            try {
                calendar.setTime(Utils.getDateFromString(signDate));
            } catch (Exception ex) {
                System.err.println("PDF Generator - Cannot get SignDate from JSON => Using Default");
                calendar.setTime(new Date(System.currentTimeMillis()));
            }
        } else {
            System.err.println("PDF Generator - Cannot get SignDate from JSON => Using Default");
            calendar.setTime(new Date(System.currentTimeMillis()));
        }
        kyc.setDaySign(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        kyc.setMonthSign(String.valueOf(calendar.get(Calendar.MONTH) + 1));
        kyc.setYearSign(String.valueOf(calendar.get(Calendar.YEAR)));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Append KYC into Asset Template">
        byte[] xsltC = template.getBinaryData();
        byte[] html = XSLT_PDF_Processing.appendData(fileItem, kyc, xsltC);
        byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html, XSLT_PDF_Processing.FontOfTemplate.PDF_Template);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="If background is a PDF => Merge background as an background of originalPDF">
        if (background != null && !Utils.isImage(background.getBinaryData())) {
            byte[] background_pdf = background.getBinaryData();
            pdf = PDF_Processing.mergePDF(pdf, background_pdf);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Merge pdf file if exist Asset Append">
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (append != null) {
            bos.write(PDF_Processing.appendPDF(pdf, append.getBinaryData()));
        } else {
            bos.write(pdf);
            bos.close();
        }
        byte[] result = bos.toByteArray();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Delete all Temporal Data save in local">
        qr_file.delete();
        background_file.delete();
        //</editor-fold>

        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, result);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Flow 2: PDF Generator with PositionQRProperties in Asset">
    private static InternalResponse flow_GenFromPositionQR(
            WorkflowActivity woAc,
            List<FileDataDetails> fileData,
            List<ItemDetails> fileItem,
            User user,
            Asset template,
            Asset background,
            Asset append,
            QRSize qrSize,
            boolean enableURL,
            QRPositionProperties postionQRProperties,
            boolean returnBinary,
            String transactionID
    ) throws Exception {
        //<editor-fold defaultstate="collapsed" desc="Assign data into Component">
        KYC_V2.Component component = new KYC_V2.Component();
//        if (enableURL) {
//            component.setValidationUrl(PolicyConfiguration.getInstant().getURLofQR().getUrl());
//        }

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        String signDate = null;
        for (ItemDetails item : fileItem) {
            if (item.getField().contains("SignDate")) {
                signDate = (String) item.getValue();
            }
        }

        if (signDate != null && !signDate.isEmpty()) {
            try {
                calendar.setTime(Utils.getDateFromString(signDate));
            } catch (Exception ex) {
                System.err.println("PDF Generator - Cannot get SignDate from JSON => Using Default");
                calendar.setTime(new Date(System.currentTimeMillis()));
            }
        } else {
            System.err.println("PDF Generator - Cannot get SignDate from JSON => Using Default");
            calendar.setTime(new Date(System.currentTimeMillis()));
        }
        component.setDaySign(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        component.setMonthSign(String.valueOf(calendar.get(Calendar.MONTH) + 1));
        component.setYearSign(String.valueOf(calendar.get(Calendar.YEAR)));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Convert XSLT into PDF">
        byte[] xsltC = template.getBinaryData();
        byte[] html = XSLT_PDF_Processing.appendData(fileItem, component, xsltC);
        byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html, XSLT_PDF_Processing.FontOfTemplate.PDF_Template);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="If background is a PDF => Merge background as an background of originalPDF">
        if (Utils.isImage(background.getBinaryData()) && background != null && Utils.isImage(background.getBinaryData())) {
            byte[] background_pdf = background.getBinaryData();
            pdf = PDF_Processing.mergePDF(pdf, background_pdf);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Merge pdf file if exist Asset Append">
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (append != null) {
            bos.write(PDF_Processing.appendPDF(pdf, append.getBinaryData()));
        } else {
            bos.write(pdf);
            bos.close();
        }
        byte[] result = bos.toByteArray();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="If List<int> page in positionQR null => auto page 1">
        if (Utils.isNullOrEmpty(postionQRProperties.getPages())) {
            System.err.println("PDF Generator - Cannot get List pages in PositionQRProperties => Using default page = 1");
            postionQRProperties.setPages(Arrays.asList(1));
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Change root => Parse from top - left into bottom left">
        float[] changes = Utils.changeRootPercentage(
                postionQRProperties.getX(),
                postionQRProperties.getY(),
                PDF_Processing.getPageSize(result).getWidth(),
                PDF_Processing.getPageSize(result).getHeight(),
                qrSize.getPoint());
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process generate QR">
        IssueQryptoWithFileAttachResponse QR = createQR(
                fileItem,
                result,
                changes[0],
                changes[1],
                qrSize.getSize(),
                qrSize.getPoint(),
                postionQRProperties.isIsTransparent(),
                postionQRProperties.getPages(),
                calendar.getTime(),
                transactionID);

        if (QR == null || Utils.isNullOrEmpty(QR.getFileTokenList())) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                    new PaperlessMessageResponse().sendErrorMessage("CANNOT GENERATE QR FROM QRYPTO").build()
            );
        }
        //</editor-fold>    

        //<editor-fold defaultstate="collapsed" desc="Download PDF from Qrypto">
        byte[] filePDFFinal = null;
        try {
            DownloadFileTokenResponse qryptoResponse = QryptoService.getInstance(1).downloadFileToken(QR.getFileTokenList().get(0));
            filePDFFinal = Base64.getDecoder().decode(qryptoResponse.getContent());
        } catch (Exception ex) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                    new PaperlessMessageResponse().sendErrorMessage("CANNOT CALL QRYPTO TO DOWNLOAD FILE").build()
            );
        } catch (Throwable ex) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                    new PaperlessMessageResponse().sendErrorMessage("CANNOT CALL QRYPTO TO DOWNLOAD FILE").build()
            );
        }
        //</editor-fold>

        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS, filePDFFinal);
    }
    //</editor-fold>

    public static void main(String[] args) throws ParseException {
        Date a = new Date(System.currentTimeMillis());

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
//        TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse("2022-02-22T20:22:02+09:00");
        System.out.println(format.format(a));
    }
}
