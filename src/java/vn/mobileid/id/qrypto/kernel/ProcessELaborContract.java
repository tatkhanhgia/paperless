/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.kernel;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.SigningService;
import vn.mobileid.id.qrypto.objects.Asset;
import vn.mobileid.id.qrypto.objects.ItemDetails;
import vn.mobileid.id.qrypto.objects.KYC;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.qrypto.objects.WorkflowActivity;
import vn.mobileid.id.qrypto.objects.WorkflowDetail_Option;
import vn.mobileid.id.utils.XSLT_PDF_Processing;

/**
 *
 * @author GiaTK
 */
public class ProcessELaborContract {
    final private static Logger LOG = LogManager.getLogger(ProcessELaborContract.class);

    /**
     * Processing Elabor Contract
     *
     * @param woAc Workflow Activity Object
     * @param fileItem listItem get from request.getItem();
     * @param photo the Object of fileData (can be photo, fingerprint, pdf,card)
     * @return InternalResponse with Document ID.
     */
    public static InternalResponse processELaborContract(
            WorkflowActivity woAc,
            List<ItemDetails> fileItem,
            Object photo, User user
    ) {
        Database DB = new DatabaseImpl();
        //Get Workflow Detail to get Asset
        InternalResponse response = GetWorkflowDetail_option.getWorkflowDetail(woAc.getWorkflow_id());
        if (response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        //Get Asset template file from DB
        DatabaseResponse template = DB.getAsset(((WorkflowDetail_Option) response.getData()).getAsset_Template());
        if (template.getStatus() != QryptoConstant.CODE_SUCCESS) {
            return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                    QryptoMessageResponse.getErrorMessage(
                            QryptoConstant.CODE_FAIL,
                            template.getStatus(),
                            "en",
                            null)
            );
        }

        //Assign data into KYC object
        KYC object = new KYC();
        object = assignAllItem(fileItem);
        object.setCurrentDate(String.valueOf(LocalDate.now()));
        object.setDateAfterOneYear(String.valueOf(LocalDate.now().plusYears(1)));
        object.setPreviousDay(String.valueOf(LocalDate.now().plusDays(1).getDayOfMonth()));
        object.setPreviousMonth(String.valueOf(LocalDate.now().minusMonths(1).getMonthValue()));
        object.setPreviousYear(String.valueOf(LocalDate.now().minusYears(1).getYear()));

        //Read file XSLT - Assign KYC Object into Template XSLT
//            String xslt = "D:\\NetBean\\QryptoServices\\file\\test.xslt";
//            byte[] xsltB = Files.readAllBytes(new File(xslt).toPath());
//            FileManagement fileAsset = (FileManagement) template.getObject();
//            byte[] xsltC = fileAsset.getData();
        byte[] xsltC = ((Asset) template.getObject()).getBinaryData();
        byte[] html = XSLT_PDF_Processing.appendData(object, xsltC);

        //Convert from HTML to PDF
        byte[] pdf = XSLT_PDF_Processing.convertHTMLtoPDF(html);

        //Call Create Owner
//            DataCreateOwner createOwner = new DataCreateOwner();
//            createOwner.setEmail(uer_info.getEmail());
//            createOwner.setUsername(uer_info.getEmail());
//            createOwner.setPa(jwt);
//            TokenResponse token = (TokenResponse) EIDService.getInstant().v1VeriOidcToken();
//            String access_token = "Bearer " + token.access_token;
//            CreateOwnerResponse res = (CreateOwnerResponse) EIDService.getInstant().v1OwnerCreate(createOwner, access_token);
        //Call SignHashWitness
        List<byte[]> result1 = null;
        if (photo instanceof String) {
            result1 = SigningService.getInstant(3).signHashWitness(object.getFullName(), (String) photo, pdf);
        }
        if (photo instanceof byte[]) {
            String temp = Base64.getEncoder().encodeToString((byte[]) photo);
            result1 = SigningService.getInstant(3).signHashWitness(object.getFullName(), temp, pdf);
        }

        //Call SignHashBusiness        
        List<byte[]> result2 = SigningService.getInstant(3).signHashBussiness(result1.get(0));
//        try {
//            Files.write(new File("D:\\NetBean\\QryptoServices\\file\\result.pdf").toPath(), result2.get(0), StandardOpenOption.CREATE);
//        } catch (Exception e) {
//
//        }
        //Write into DB
        UpdateFileManagement.updateFileManagement(
                Integer.parseInt(woAc.getFile().getID()),
                null,
                null,
                null,
                0,
                0,
                0,
                0,
                null,
                null,
                user.getEmail(),
                result2.get(0));
        return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                "{\"document_id\":"+woAc.getFile().getID()+"}"
        );
    }

//=====================INTERNAL METHOD =======================
    //Function Assign All value in Item into KYC Object
    private static KYC assignAllItem(List<ItemDetails> listItem) {
        KYC kyc = new KYC();
        for (ItemDetails details : listItem) {
            String field = details.getField();
            int type = details.getType();
            Object checkType = ItemDetails.checkType(type);
            Object value = null;
            if (checkType instanceof String) {
                value = (String) details.getValue();
            }
//                if( checkType instanceof Boolean){
//                    Boolean temp = (Boolean) details.getValue();
//                    temp.
//                }
            if (checkType instanceof Integer) {
                value = (Integer) details.getValue();
            }
            kyc = assignIntoKYC(kyc, field, value);
        }
        return kyc;
    }

    //Assign data into KYC Object
    private static KYC assignIntoKYC(KYC oldValue, String field, Object value) {
        Field[] fields = KYC.getHashMapFieldName();
        for (Field temp : fields) {
            if (temp.getName().contains(field)) {
                try {
                    oldValue.set(temp, value);
                    return oldValue;
                } catch (IllegalArgumentException ex) {
                    if (LogHandler.isShowErrorLog()) {
                        LOG.error("Cannot assign Data into KYC Object - Details:" + ex);
                    }
                } catch (IllegalAccessException ex) {
                    if (LogHandler.isShowErrorLog()) {
                        LOG.error("Cannot assign Data into KYC Object - Details:" + ex);
                    }
                }
            }
        }
        return oldValue;
    }

    public static void main(String[] args){
//        ProcessELaborContract a = new ProcessELaborContract(100);
//        System.out.println("A:"+a.TEMPLATE_E_LABOR_CONTRACT);
//        ProcessELaborContract b = new ProcessELaborContract();
//        System.out.println("B:"+b.TEMPLATE_E_LABOR_CONTRACT);
        
    }
}
