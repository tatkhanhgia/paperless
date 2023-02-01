/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.kernel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.eid.object.TokenResponse;
import vn.mobileid.id.everification.object.CreateOwnerResponse;
import vn.mobileid.id.everification.object.DataCreateOwner;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.qrypto.EIDService;
import vn.mobileid.id.qrypto.QryptoConstant;
import vn.mobileid.id.qrypto.SigningService;
import vn.mobileid.id.qrypto.objects.Asset;
import vn.mobileid.id.qrypto.objects.FileDataDetails;
import vn.mobileid.id.qrypto.objects.FileManagement;
//import vn.mobileid.id.qrypto.objects.FileDataDetails.FileType;
import vn.mobileid.id.qrypto.objects.ItemDetails;
import vn.mobileid.id.qrypto.objects.KYC;
import vn.mobileid.id.qrypto.objects.ProcessWorkflowActivity_JSNObject;
import vn.mobileid.id.qrypto.objects.QryptoMessageResponse;
import vn.mobileid.id.qrypto.objects.WorkflowActivity;
import vn.mobileid.id.qrypto.objects.WorkflowDetail_Option;
import vn.mobileid.id.utils.Utils;
import vn.mobileid.id.utils.XSLT_PDF_Processing;

/**
 *
 * @author GiaTK
 */
public class ProcessWorkflowActivity {

    final private static Logger LOG = LogManager.getLogger(ProcessWorkflowActivity.class);

    public static InternalResponse checkData(ProcessWorkflowActivity_JSNObject request) {
        List<ItemDetails> listItem = request.getItem();
        List<FileDataDetails> listFile = request.getFile_data();
        InternalResponse result;

        //Check item details
        for (ItemDetails obj : listItem) {
            result = CreateWorkflowTemplate.checkDataWorkflowTemplate(obj);
            if (result.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS) {
                return result;
            }
        }

        //Check file details
        for (FileDataDetails obj : listFile) {
            boolean gate = checkFile_type(obj.getFile_type());
            if (!gate) {
                return new InternalResponse(QryptoConstant.HTTP_CODE_BAD_REQUEST,
                        QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                                QryptoConstant.SUBCODE_MISSING_OR_ERROR_FILE_TYPE,
                                "en",
                                null));
            }
        }
        return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                QryptoMessageResponse.getErrorMessage(QryptoConstant.CODE_SUCCESS,
                        QryptoConstant.SUBCODE_SUCCESS,
                        "en",
                        null));
    }

    public static InternalResponse process(int id, HashMap<String,String> header, User uer_info, ProcessWorkflowActivity_JSNObject request) {
        try {
            //Get Data from request
            List<FileDataDetails> fileData = request.getFile_data();
            List<ItemDetails> fileItem = request.getItem();
            Object PDF = null, finger = null, card = null, photo = null;
            for (FileDataDetails file : fileData) {
                switch (file.getFile_type()) {
                    case 1: {
                        finger = file.getValue();
                        break;
                    }
                    case 2: {
                        card = file.getValue();
                        break;
                    }
                    case 3: {
                        photo = file.getValue();
                        break;
                    }
                    case 4: {
                        PDF = file.getValue();
                        break;
                    }
                }
            }

            Database DB = new DatabaseImpl();

            //Get workflow Activity and check existed            
            if(GetWorkflowActivity.checkExisted(id)){
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        QryptoMessageResponse.getErrorMessage(
                                QryptoConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                                QryptoConstant.SUBCODE_EXISTED_WORKFLOW_ACTIVITY,
                                "en",
                                null)
                );
            }
            WorkflowActivity woAc = GetWorkflowActivity.getWorkflowActivity(id);
            if(woAc == null){
                return new InternalResponse(QryptoConstant.HTTP_CODE_FORBIDDEN,
                        QryptoMessageResponse.getErrorMessage(
                                QryptoConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                                QryptoConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_EXISTED,
                                "en",
                                null)
                );
            }
            
            //Check Type Process
            
            //Get Workflow Detail to get Asset
            InternalResponse response = GetWorkflowDetail_option.getWorkflowDetail(woAc.getWorkflow_id());
            if(response.getStatus() != QryptoConstant.HTTP_CODE_SUCCESS){
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
            object = assignAllItem(request.getItem());
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
            Files.write(new File("D:\\NetBean\\QryptoServices\\file\\result.pdf").toPath(), result2.get(0), StandardOpenOption.CREATE);

            //Write into DB
            
            return new InternalResponse(QryptoConstant.HTTP_CODE_SUCCESS,
                    "Done!"
            );
        } catch (Exception e) {
            e.printStackTrace();
            if (LogHandler.isShowErrorLog()) {
                LOG.error("UNKNOWN EXCEPTION. Details: " + Utils.printStackTrace(e));
            }            
            return new InternalResponse(500, QryptoConstant.INTERNAL_EXP_MESS);
        }
    }

    //==================INTERAL METHOD/FUNCTION===================
    private static boolean checkFile_type(int i) {
        if (i <= 0 || i > 4) {
            return false;
        }
        return true;
    }

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

    //Check template type of workflow activity
    public static void checkTemplateType(int id){
        
    }
    public static void main(String[] arhs) {
//        ItemDetails a = new ItemDetails();
//        a.setField("FullName");
//        a.setMandatory_enable(true);
//        a.setType(1);
//        a.setValue("Tat Khanh Gia");
//
//        ItemDetails b = new ItemDetails();
//        b.setField("Nationality");
//        b.setMandatory_enable(true);
//        b.setType(1);
//        b.setValue("b=nal");
//
//        KYC kyc = new KYC();
//        List<ItemDetails> listItem = new ArrayList<>();
//        listItem.add(a);
//        listItem.add(b);
//        for (ItemDetails details : listItem) {
//            String field = details.getField();
//            int type = details.getType();
//            Object checkType = ItemDetails.checkType(type);
//            Object value = null;
//            if (checkType instanceof String) {
//                value = (String) details.getValue();
//            }
////                if( checkType instanceof Boolean){
////                    Boolean temp = (Boolean) details.getValue();
////                    temp.
////                }
//            if (checkType instanceof Integer) {
//                value = (Integer) details.getValue();
//            }
//            kyc = assignIntoKYC(kyc, field, value);
//        }
//        XSLT_PDF_Processing.appendData(kyc);

        String jwt = "eyJraWQiOiI5NTBhNmM2YTgwZjk0NTVmM2Y3NjU3ZDZmMTUyZGQyMjkwYjk2Y2U3IiwiYWxnIjoiUlMyNTYifQ.eyJmaW5nZXJwcmludF9jb25maWRlbmNlIjoxNDcsInRyYW5zYWN0aW9uX2lkIjoiSVNBUFAtMjMwMTE2MTc0NDQzLTIxMDk5MS0xNTkwMDkiLCJzdWIiOiJmMGE4N2FhMC00YzgyLTQ4OTgtYThhZC01ODdkNmFhNDY5ZDMiLCJkb2N1bWVudF9udW1iZXIiOiIwODAyMDAwMTUzMjIiLCJnZW5kZXIiOiJOYW0iLCJ0cmFuc2FjdGlvbl9kYXRhIjoie1wiY2hhbGxlbmdlVmFsdWVcIjpcImdFaXZWbnptUGI2NDcwODAxODM3NjQzMTJcIixcInRyYW5zYWN0aW9uRGF0YVwiOlwie1xcXCJ0cmFuc2FjdGlvblRpdGxlXFxcIjpcXFwiQVVUSE9SSVpFIFVTRVJcXFwiLFxcXCJhdXRoQ29udGVudExpc3RcXFwiOltdLFxcXCJtdWx0aXBsZVNlbGVjdExpc3RcXFwiOltdLFxcXCJzaW5nbGVTZWxlY3RMaXN0XFxcIjpbXSxcXFwibmFtZVZhbHVlUGFpckxpc3RcXFwiOlt7XFxcIm9yZGluYXJ5XFxcIjowLFxcXCJsYWJlbFxcXCI6XFxcIkFyZSB5b3Ugc3VyZSB0byBzaWduIHRoZSBlTGFib3JDb250cmFjdCBhcyBmb2xsb3dpbmcgP1xcXCIsXFxcInRpdGxlXFxcIjpcXFwiVHJhbnNhY3Rpb24gRGV0YWlsc1xcXCIsXFxcIm5hbWVWYWx1ZVBhaXJcXFwiOntcXFwiRW1wbG95ZWVcXFwiOlxcXCJUaMOhaSBQaGkgU8ahblxcXCIsXFxcIkVtcGxveWVyXFxcIjpcXFwiQ8OUTkcgVFkgQ-G7lCBQSOG6pk4gQ8OUTkcgTkdI4buGIFbDgCBE4buKQ0ggVuG7pCBNT0JJTEUtSURcXFwiLFxcXCJIYXNoIEFsZ29yaXRobVxcXCI6XFxcIlNIQTI1NlxcXCIsXFxcIkhhc2ggVmFsdWVcXFwiOlxcXCJPNjVLK1RqV1ZhWGY1REJaTXhmTjlWTTRQS1wvczFWMFlpSFRKNkszZkJ3RT1cXFwifX1dLFxcXCJkb2N1bWVudERpZ2VzdExpc3RcXFwiOlt7XFxcIm9yZGluYXJ5XFxcIjoxLFxcXCJsYWJlbFxcXCI6XFxcIkRvY3VtZW50IERpZ2VzdFxcXCIsXFxcInRpdGxlXFxcIjpcXFwiRG91Y21lbnQgRGlnZXN0MVxcXCIsXFxcImRvY3VtZW50RGlnZXN0XFxcIjp7XFxcImRpZ2VzdEFsZ29cXFwiOm51bGwsXFxcImRpZ2VzdFZhbHVlXFxcIjpudWxsfX1dfVwifSIsImlzcyI6Imh0dHBzOlwvXC9pZC5tb2JpbGUtaWQudm4iLCJwbGFjZV9vZl9yZXNpZGVuY2UiOiIyNzMgUuG6oWNoIENoYW5oLCBM4bujaSBCw6xuaCBOaMahbiwgVMOibiBBbiwgTG9uZyBBbiIsImNlcnRpZmljYXRlc19xdWVyeV9wYXRoIjoiXC9kdGlzXC92MVwvZS1pZGVudGl0eVwvY2VydGlmaWNhdGVzIiwiY2l0eV9wcm92aW5jZSI6IkxPTkcgQU4iLCJwbGFjZV9vZl9vcmlnaW4iOiJCw6xuaCBBbiwgVGjhu6cgVGjhu6thLCBMb25nIEFuIiwibmF0aW9uYWxpdHkiOiJWaeG7h3QgTmFtIiwiaXNzdWluZ19jb3VudHJ5IjoiVmnhu4d0IE5hbSIsIm1hdGNoX3Jlc3VsdCI6dHJ1ZSwibmFtZSI6IlRow6FpIFBoaSBTxqFuIiwiZmluZ2VycHJpbnRfdGhyZXNob2xkIjo2MCwiZXhwIjoxNjc0MTg5ODgzLCJpYXQiOjE2NzM4NjU4ODMsImFzc3VyYW5jZV9sZXZlbCI6IkVYVEVOREVEIiwianRpIjoiMjAzNThiM2MtYWQ5Zi00MTQ5LWIyNTktMDc1YWRiNTc0YmU3IiwiZG9jdW1lbnRfdHlwZSI6IkNJVElaRU5DQVJEIn0.dSErYjwsXOTp4WTfKdCLO5WJJU7ylXT8JyMSsZzNQF1H7l4iCApsuwB1lAxZqFzFicVSdjN2ISaEKF0E4mGulCi27ETvql3a1eidxzrcL2ppHXWXuOrV1OM1t3qBSGTn963SeGxh8pT3yjuW2pxzU5A5LZ7aZoKLCRIDdT67DcMuCruARMt9B7It11akFxK-Moce8FvhoJXCd-BUONQMgl2w4l7vzP_YjyTf023nKda045s_ZfGXH9T0Kn6vwYC0MgH-xcVcK1kAtmoGOLj7LB-rb-BbZfis_CfoU8PqM7JVgrHwz5g1qds2U9xflj1kXPwoQSsLB_-NuXXPhvgdbA";

        DataCreateOwner data = new DataCreateOwner();
        data.setEmail("giatk@mobile-id.vn");
//            createOwner.setPhone(uer_info.get);
        data.setUsername("giatk@mobile-id.vn");
        data.setPa(jwt);

        TokenResponse token = (TokenResponse) EIDService.getInstant().v1VeriOidcToken();
        String access_token = "Bearer " + token.access_token;
        CreateOwnerResponse res = (CreateOwnerResponse) EIDService.getInstant().v1OwnerCreate(data, access_token);
        System.out.println("rs:" + res.getMessage());
    }
}
