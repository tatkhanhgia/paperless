/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.geom.Rectangle;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.QryptoService;
import vn.mobileid.id.paperless.exception.LoginException;
import vn.mobileid.id.paperless.kernel_v2.CreateQRSchema;
import vn.mobileid.id.paperless.kernel_v2.GetQRSize;
import vn.mobileid.id.paperless.kernel_v2.GetWorkflowDetails;
import vn.mobileid.id.paperless.kernel_v2.UpdateFileManagement;
import vn.mobileid.id.paperless.kernel_v2.UpdateWorkflowActivity;
import vn.mobileid.id.paperless.object.enumration.FileType;
import vn.mobileid.id.paperless.object.enumration.QRBackground;
import vn.mobileid.id.paperless.object.enumration.StampIn;
import vn.mobileid.id.paperless.object.enumration.WorkflowAttributeTypeName;
import vn.mobileid.id.paperless.objects.FileDataDetails;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.ItemDetails;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.QRSize;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;
import vn.mobileid.id.qrypto.object.Configuration;
import vn.mobileid.id.qrypto.object.QRSchema;
import vn.mobileid.id.qrypto.object.qryptoEffectiveDate;
import vn.mobileid.id.qrypto.response.DownloadFileTokenResponse;
import vn.mobileid.id.qrypto.response.IssueQryptoWithFileAttachResponse;
import vn.mobileid.id.utils.PDF_Processing;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class ProcessSimplePDFStamping {

    //<editor-fold defaultstate="collapsed" desc="Process Simple PDF Stamping">
    public static InternalResponse process(
            WorkflowActivity woAc,
            List<FileDataDetails> fileData,
            List<ItemDetails> fileItem,
            User user,
            String transactionID
    ) throws Exception {
        QRSize qrSize = null;
        int page = 0;
        StampIn stamp = StampIn.None;
        float x_coordinate = 0;
        float y_coordinate = 0;
        boolean enableURL = false;
        boolean showDomain = false;
        String url = null;
        String textBelow = null;
        FileDataDetails pdf = null;

        //<editor-fold defaultstate="collapsed" desc="Prepare PDF data">
        if (fileData == null) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_MISSING_VALUE_OF_FILE_DATA,
                            "en",
                            transactionID)
            );
        }

        for (FileDataDetails temp : fileData) {
            if (temp.getFile_type() == FileDataDetails.FileType.PDF.getValue()) {
                pdf = temp;
            }
        }

        if (pdf == null || pdf.getValue() == null || ((String) pdf.getValue()).isEmpty()) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_MISSING_VALUE_OF_FILE_DATA,
                            "en",
                            transactionID)
            );
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Process generate QR">
        InternalResponse response = ProcessSecureQRTemplate.process_(
                woAc,
                null,
                fileItem,
                user,
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        String qrData = response.getMessage();
        String imageBase64 = Utils.getFromJson("qryptoBase64", qrData);
        byte[] qrImage = Base64.getDecoder().decode(imageBase64);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get Workflow Detail">
        response = GetWorkflowDetails.getWorkflowDetail(
                woAc.getWorkflow_id(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get all Workflow Attribute Type in Workflow Details">
        List<WorkflowAttributeType> list = (List<WorkflowAttributeType>) response.getData();
        WorkflowAttributeType attribute_qrSize = null;

        for (WorkflowAttributeType a : list) {
            if (a.getId() == WorkflowAttributeTypeName.PAGE.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                page = Integer.parseInt((String) a.getValue());
            }
            if (a.getId() == WorkflowAttributeTypeName.STAMP_IN.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                switch ((Integer.parseInt((String) a.getValue()))) {
                    case 1: {
                        stamp = StampIn.Last_Page;
                        break;
                    }
                    case 2: {
                        stamp = StampIn.All_Page;
                        break;
                    }
                }
            }
            if (a.getId() == WorkflowAttributeTypeName.X_COORDINATE.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                x_coordinate = Float.parseFloat((String) a.getValue());
            }
            if (a.getId() == WorkflowAttributeTypeName.Y_COORDINATE.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                y_coordinate = Float.parseFloat((String) a.getValue());
            }
            if (a.getId() == WorkflowAttributeTypeName.TEXT_BELOW_QR.getId() && (a.getValue() != null) && !((String) a.getValue()).isEmpty()) {
                textBelow = (String) a.getValue();
            }
            if (a.getId() == WorkflowAttributeTypeName.QR_SIZE.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                attribute_qrSize = a;
            }
            if (a.getId() == WorkflowAttributeTypeName.URL_CODE.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                enableURL = Integer.parseInt((String) a.getValue()) == 1;
            }
            if (a.getId() == WorkflowAttributeTypeName.SHOW_DOMAIN_BELOW_QR.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                showDomain = Integer.parseInt((String) a.getValue()) == 1;
            }
        }

        //Get QR Size
        if (attribute_qrSize != null) {
            response = GetQRSize.getQRSize(String.valueOf(attribute_qrSize.getValue()), transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            qrSize = (QRSize) response.getData();
        }

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Process Add Image and text">
        if (showDomain) {
            System.out.println("Show domain");
            url = PolicyConfiguration.getInstant().getURLofQR().getUrl();
            if (textBelow != null) {
                url += "\n";
                url += textBelow;
            }
            System.out.println("URL:" + url);
        }
        boolean All_Page = (stamp.getId() == StampIn.All_Page.getId());
        boolean Last_Page = (stamp.getId() == StampIn.Last_Page.getId());
        byte[] result = PDF_Processing.addImage_test(
                Base64.getDecoder().decode((String) pdf.getValue()),
                qrImage,
                page,
                All_Page,
                Last_Page,
                x_coordinate,
                y_coordinate,
                Utils.calculateQRSize(qrSize.getSize()),
                Utils.calculateQRSize(qrSize.getSize()),
                url,
                true);
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

        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Process Simple PDF Stamping - Call throught Qrypto">
    public static InternalResponse processV2(
            WorkflowActivity woAc,
            List<FileDataDetails> fileData,
            List<ItemDetails> fileItem,
            User user,
            String transactionID
    ) throws Exception {
        QRSize qrSize = null;
        int page = 0;
        StampIn stamp = StampIn.None;
        float x_coordinate = 0;
        float y_coordinate = 0;
        boolean enableURL = false;
        boolean showDomain = false;
        String url = null;
        String textBelow = null;
        FileDataDetails pdf = null;
        boolean isTransparent = false;

        //<editor-fold defaultstate="collapsed" desc="Prepare PDF data">
        if (fileData == null) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_MISSING_VALUE_OF_FILE_DATA,
                            "en",
                            transactionID)
            );
        }

        for (FileDataDetails temp : fileData) {
            if (temp.getFile_type() == FileDataDetails.FileType.PDF.getValue()) {
                pdf = temp;
            }
        }

        if (pdf == null || pdf.getValue() == null || ((String) pdf.getValue()).isEmpty()) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_MISSING_VALUE_OF_FILE_DATA,
                            "en",
                            transactionID)
            );
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get Workflow Detail">
        InternalResponse response = GetWorkflowDetails.getWorkflowDetail(
                woAc.getWorkflow_id(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Get all Workflow Attribute Type in Workflow Details">
        List<WorkflowAttributeType> list = (List<WorkflowAttributeType>) response.getData();
        WorkflowAttributeType attribute_qrSize = null;

        for (WorkflowAttributeType a : list) {
            if (a.getId() == WorkflowAttributeTypeName.PAGE.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                page = Integer.parseInt((String) a.getValue());
            }
            if (a.getId() == WorkflowAttributeTypeName.STAMP_IN.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                switch ((Integer.parseInt((String) a.getValue()))) {
                    case 1: {
                        stamp = StampIn.Last_Page;
                        break;
                    }
                    case 2: {
                        stamp = StampIn.All_Page;
                        break;
                    }
                }
            }
            if (a.getId() == WorkflowAttributeTypeName.X_COORDINATE.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                x_coordinate = Float.parseFloat((String) a.getValue());
            }
            if (a.getId() == WorkflowAttributeTypeName.Y_COORDINATE.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                y_coordinate = Float.parseFloat((String) a.getValue());
            }
            if (a.getId() == WorkflowAttributeTypeName.TEXT_BELOW_QR.getId() && (a.getValue() != null) && !((String) a.getValue()).isEmpty()) {
                textBelow = (String) a.getValue();
            }
            if (a.getId() == WorkflowAttributeTypeName.QR_SIZE.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                attribute_qrSize = a;
            }
            if (a.getId() == WorkflowAttributeTypeName.URL_CODE.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                enableURL = Integer.parseInt((String) a.getValue()) == 1;
            }
            if (a.getId() == WorkflowAttributeTypeName.SHOW_DOMAIN_BELOW_QR.getId() && (a.getValue() != null) && Integer.parseInt((String) a.getValue()) != 0) {
                showDomain = Integer.parseInt((String) a.getValue()) == 1;
            }
            if (a.getId() == WorkflowAttributeTypeName.QR_BACKGROUND.getId() && (a.getValue() != null) && !((String) a.getValue()).isEmpty()) {
                String qrbackground = (String) a.getValue();
                isTransparent = QRBackground.isTransparent(qrbackground);
            }
        }

        //Get QR Size
        if (attribute_qrSize != null) {
            response = GetQRSize.getQRSize(String.valueOf(attribute_qrSize.getValue()), transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            qrSize = (QRSize) response.getData();
        }

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Get pages from All_Page - Last_Page">
        if (showDomain) {
            System.out.println("Show domain");
            url = PolicyConfiguration.getInstant().getURLofQR().getUrl();
            if (textBelow != null) {
                url += "\n";
                url += textBelow;
            }
            System.out.println("URL:" + url);
        }
        boolean All_Page = (stamp.getId() == StampIn.All_Page.getId());
        boolean Last_Page = (stamp.getId() == StampIn.Last_Page.getId());
        List<Integer> pages = new ArrayList<>();
        if (All_Page) {
            pages = PDF_Processing.getPages(Base64.getDecoder().decode((String) pdf.getValue()), false);
        } else {
            if (Last_Page) {
                pages = PDF_Processing.getPages(Base64.getDecoder().decode((String) pdf.getValue()), true);
            } else {
                if (PDF_Processing.getPages(Base64.getDecoder().decode((String) pdf.getValue()), true).get(0) < page) {
                    pages = PDF_Processing.getPages(Base64.getDecoder().decode((String) pdf.getValue()), true);
                } else {
                    pages.add(page);
                }
            }
        }
        if (pages == null || pages.isEmpty()) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWDETAIL,
                            PaperlessConstant.SUBCODE_CANNOT_GET_PAGES,
                            "en",
                            transactionID)
            );
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Change Root of X-Y (LEFT-TOP) into X-Y(left-bottom)">
        Rectangle pageSize = PDF_Processing.getPageSize(Base64.getDecoder().decode((String) pdf.getValue()));
        float[] temp = Utils.changeRootPercentage(
                x_coordinate,
                y_coordinate,
                pageSize.getWidth(),
                pageSize.getHeight(),
                qrSize.getPoint());
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Call Core Qrypto">
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        IssueQryptoWithFileAttachResponse callQrypto = createQR(
                fileItem,
                Base64.getDecoder().decode((String) pdf.getValue()),
                temp[0],
                temp[1],
                qrSize.getSize(),
                qrSize.getPoint(),
                isTransparent,
                pages,
                calendar.getTime(),
                transactionID);

        if (callQrypto == null || Utils.isNullOrEmpty(callQrypto.getFileTokenList())) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_500,
                    new PaperlessMessageResponse().sendErrorMessage("CANNOT GENERATE QR FROM QRYPTO").build()
            );
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Download PDF from Qrypto">
        byte[] filePDFFinal = null;
        try {
            DownloadFileTokenResponse qryptoResponse = QryptoService.getInstance(1).downloadFileToken(callQrypto.getFileTokenList().get(0));
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

        //<editor-fold defaultstate="collapsed" desc="Append Url">
        if (enableURL) {
            try {
                filePDFFinal = PDF_Processing.addText(
                        filePDFFinal,
                        page,
                        All_Page,
                        Last_Page,
                        x_coordinate,
                        y_coordinate,
                        qrSize.getPoint(),
                        qrSize.getPoint(),
                        textBelow);
            } catch (Exception ex) {
                System.err.println("Workflow Activity:"+woAc.getId() + " - Cannot Append URL into file PDF");
            }
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Update FileManagement after process">
        FileManagement file_final = PDF_Processing.analysisPDF(filePDFFinal);
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
                filePDFFinal,
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

        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
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
            file.setFile_field("FileStamping.pdf");

            ItemDetails file_ = new ItemDetails();
            file_.setField("FilePDF");
            file_.setType(5);
            file_.setFile_format("application/pdf");
            file_.setFile_name("PDFStamping.pdf");
            file_.setFile_field("FileStamping.pdf");

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
}
