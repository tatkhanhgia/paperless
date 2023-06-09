/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import vn.mobileid.id.paperless.kernel.process.ProcessELaborContract;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.eid.object.JWT_Authenticate;
import vn.mobileid.id.eid.object.TokenResponse;
import vn.mobileid.id.everification.object.CreateOwnerResponse;
import vn.mobileid.id.everification.object.DataCreateOwner;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.EIDService;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.kernel.process.ProcessESignCloud;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.FileDataDetails;
import vn.mobileid.id.paperless.objects.FileManagement;
import vn.mobileid.id.paperless.objects.ItemDetails;
import vn.mobileid.id.paperless.objects.ProcessWorkflowActivity_JSNObject;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.SigningProperties;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;
import vn.mobileid.id.paperless.objects.WorkflowTemplateType;

/**
 *
 * @author GiaTK
 */
public class ProcessWorkflowActivity {

    public static InternalResponse assign(
            int id,
            String filename,
            JWT_Authenticate jwt,
            User user,
            ProcessWorkflowActivity_JSNObject request,
            boolean isAssigned,
            String transactionID) throws Exception {

        //Get Data from request
        List<FileDataDetails> fileData = request.getFile_data();
        List<ItemDetails> fileItem = request.getItem();
        Database DB = new DatabaseImpl();

        //Get workflow Activity and check existed    
        InternalResponse response = GetWorkflowActivity.getWorkflowActivity(
                id,
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
//            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
//                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
//                            PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_EXISTED,
//                            "en",
//                            null)
//            );
        }
        WorkflowActivity woAc = (WorkflowActivity) response.getData();

        //If Existed => update request data
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        //Check file management to ensure that WoAc already process.
        response = GetFileManagement.getFileManagement(
                Integer.parseInt(woAc.getFile().getID()),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        FileManagement file = (FileManagement) response.getData();
        if (file.getData() != null) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_ALREADY_PROCESS,
                            "en",
                            null)
            );
        }

        //Check Type Process
//        woAc = GetWorkflowActivity.getWorkflowActivityFromDB(id, transactionID);
        response = GetWorkflowTemplateType.getWorkflowTemplateType(
                woAc.getWorkflow_template_type(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        WorkflowTemplateType templateType = (WorkflowTemplateType) response.getData();

        switch (templateType.getName()) {
            case "E-LABOR CONTRACT": {
                if (isAssigned) {
                    response = ProcessELaborContract.assignELaborContract(
                            woAc,
                            fileItem,
                            user,
                            filename,
                            transactionID);
                    if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                        return response;
                    }

                    //After Assign Success. Replace Data WorkflowAc in listWorkflow Resources
                    response = GetWorkflowActivity.getWorkflowActivityFromDB(
                            woAc.getId(),
                            transactionID);
                    if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                        return response;
                    }
                    woAc = (WorkflowActivity) response.getData();
                    Resources.getListWorkflowActivity().replace(String.valueOf(woAc.getId()), woAc);
                    return response;
                }
                return new InternalResponse(500, "Pending");
            }
            case "ESIGNCLOUD": {
                if (isAssigned) {
                    response = ProcessESignCloud.assignEsignCloud(
                            woAc,
                            fileItem,
                            user,
                            filename,
                            transactionID);
                    if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                        return response;
                    }
                    //After Assign Success. Replace Data WorkflowAc in listWorkflow Resources
                    response = GetWorkflowActivity.getWorkflowActivityFromDB(
                            woAc.getId(),
                            transactionID);
                    if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                        return response;
                    }
                    woAc = (WorkflowActivity) response.getData();
                    Resources.getListWorkflowActivity().replace(String.valueOf(woAc.getId()), woAc);
                    return response;
                }
            }
            default: {
                return new InternalResponse(500, "NOT PROVIDED YET");
            }
        }
    }

    /**
     * Using this function to process the workflow activity which type is
     * "ESignCloud" - "E-laborcontract"
     *
     * @param user
     * @param idWA
     * @param jwt
     * @param request
     * @param headers
     * @param transactionID
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static InternalResponse processAuthen(
            User user,
            int idWA,
            JWT_Authenticate jwt,
            ProcessWorkflowActivity_JSNObject request,
            HashMap<String, String> headers,
            String transactionID) throws IOException, Exception {

        List<FileDataDetails> fileData = request.getFile_data();
        SigningProperties signingObject = request.getSigning_properties();

        Database DB = new DatabaseImpl();

        //Get workflow Activity and check existed            
        InternalResponse response = GetWorkflowActivity.getWorkflowActivity(
                idWA,
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
//            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
//                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
//                            PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_EXISTED,
//                            "en",
//                            null)
//            );
        }
        WorkflowActivity woAc = (WorkflowActivity) response.getData();

        if (woAc.getRequestData() == null) {            
            response = GetWorkflowActivity.getWorkflowActivityFromDB(
                    idWA,
                    transactionID);
            woAc = (WorkflowActivity) response.getData();
            Resources.getListWorkflowActivity().replace(String.valueOf(woAc.getId()), woAc);
        }

        response = GetFileManagement.getFileManagement(
                Integer.parseInt(woAc.getFile().getID()),
                transactionID);

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        FileManagement file = (FileManagement) response.getData();
        if (file.getData() == null) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_PROCESS_YET,
                            "en",
                            null)
            );
        }
        if (file.isIsSigned() == true) {
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
                            PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_ALREADY_PROCESS,
                            "en",
                            null)
            );
        }

        //Check Type Process
//            WorkflowActivity woAcTemp = GetWorkflowActivity.getWorkflowActivityFromDB(
//                    idWA,
//                    transactionID);        
        response = GetWorkflowTemplateType.getWorkflowTemplateType(
                woAc.getWorkflow_template_type(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        WorkflowTemplateType templateType = (WorkflowTemplateType) response.getData();

        switch (templateType.getName()) {
            case "E-LABOR CONTRACT": {
                InternalResponse res = ProcessELaborContract.processELaborContractWithAuthen(
                        user,
                        woAc.getId(),
                        file,
                        woAc.getRequestData(),
                        jwt,
                        fileData,
                        signingObject,
                        transactionID);
                if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return res;
                }
                res.setData(woAc);

                //After Signing Success. Replace Data WorkflowAc in listWorkflow Resources
                response = GetWorkflowActivity.getWorkflowActivity(
                        idWA,
                        transactionID);
                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return response;
                }
                woAc = (WorkflowActivity) response.getData();
                Resources.getListWorkflowActivity().replace(String.valueOf(woAc.getId()), woAc);
                return res;
            }
            case "ESIGNCLOUD": {
                InternalResponse res = ProcessESignCloud.processEsignCloudWithAuthen(
                        user,
                        woAc.getId(),
                        file,
                        woAc.getRequestData(),
                        jwt,
                        fileData,
                        signingObject,
                        transactionID);
                if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return res;
                }
                res.setData(woAc);

                //After Signing Success. Replace Data WorkflowAc in listWorkflow Resources
                response = GetWorkflowActivity.getWorkflowActivity(
                        idWA,
                        transactionID);
                if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                    return response;
                }
                woAc = (WorkflowActivity) response.getData();
                Resources.getListWorkflowActivity().replace(String.valueOf(woAc.getId()), woAc);
                return res;
            }
            default: {
                return new InternalResponse(500, "NOT PROVIDED YET");
            }
        }
    }

    /**
     * Using this function to process the workflow activity which type is "QR",
     * ..
     *
     * @param id
     * @param filename
     * @param jwt
     * @param uer_info
     * @param request
     * @param isAssigned
     * @param transactionID
     * @return
     * @throws Exception
     */
    public static InternalResponse process(
            int id,
            String filename,
            JWT_Authenticate jwt,
            User uer_info,
            ProcessWorkflowActivity_JSNObject request,
            boolean isAssigned,
            String transactionID) throws Exception {

        //Get Data from request
        List<FileDataDetails> fileData = request.getFile_data();
        List<ItemDetails> fileItem = request.getItem();
        Database DB = new DatabaseImpl();

        //Check data of request
        InternalResponse response = CheckWorkflowTemplate.checkDataWorkflowTemplate(request);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        //Get workflow Activity and check existed            
        response = GetWorkflowActivity.getWorkflowActivity(
                id,
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
//            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
//                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
//                            PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_EXISTED,
//                            "en",
//                            null)
//            );
        }
        WorkflowActivity woAc = (WorkflowActivity) response.getData();

        //Check Type Process
//        woAc = GetWorkflowActivity.getWorkflowActivityFromDB(id, transactionID);
        response = GetWorkflowTemplateType.getWorkflowTemplateType(
                woAc.getWorkflow_template_type(),
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }

        WorkflowTemplateType templateType = (WorkflowTemplateType) response.getData();

        switch (templateType.getName()) {
            case "E-LABOR CONTRACT": {
                return new InternalResponse(500, "NOT PROVIDED YET");
            }
            case "ESIGNCLOUD": {
                return new InternalResponse(500, "NOT PROVIDED YET");
            }
            case "SECURE QR TEMPLATE": {

            }
            default: {
                return new InternalResponse(500, "NOT PROVIDED YET");
            }
        }
    }

    public static InternalResponse getDocumentHash(
            User user,
            int idWA,
            JWT_Authenticate jwt,
            ProcessWorkflowActivity_JSNObject request,
            HashMap<String, String> headers,
            String transactionID
    ) throws Exception {
        List<FileDataDetails> fileData = request.getFile_data();
        SigningProperties signingObject = request.getSigning_properties();

        //Get workflow Activity and check existed            
        InternalResponse response = GetWorkflowActivity.getWorkflowActivity(
                idWA,
                transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
//            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
//                    PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
//                            PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_EXISTED,
//                            "en",
//                            null)
//            );
        }
        WorkflowActivity woAc = (WorkflowActivity) response.getData();
        response = GetWorkflowDetail_option.getWorkflowDetail(woAc.getWorkflow_id(), transactionID);
        WorkflowDetail_Option details = (WorkflowDetail_Option) response.getData();
        response = GetAsset.getAsset(details.getAsset_Template(), transactionID);

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        Asset file = (Asset) response.getData();
//            if (file.getData() == null) {
//                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
//                        PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
//                                PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_DOES_NOT_PROCESS_YET,
//                                "en",
//                                null)
//                );
//            }
//            if (file.isIsSigned() == true) {
//                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
//                        PaperlessMessageResponse.getErrorMessage(PaperlessConstant.CODE_INVALID_PARAMS_WORKFLOWACTIVITY,
//                                PaperlessConstant.SUBCODE_WORKFLOW_ACTIVITY_ALREADY_PROCESS,
//                                "en",
//                                null)
//                );
//            }

        //Check Type Process
        String item = "{\"items\":" + new ObjectMapper().writeValueAsString(request.getItem()) + "}";
//            System.out.println("String:"+item);
        InternalResponse res = ProcessELaborContract.getHashDocument(
                user,
                woAc.getId(),
                file,
                item,
                jwt,
                fileData,
                signingObject,
                transactionID);
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        return res;

//            response = GetWorkflowTemplateType.getWorkflowTemplateType(
//                    woAcTemp.getWorkflow_template_type(),
//                    transactionID);
//            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//                return response;
//            }
//
//            WorkflowTemplateType templateType = (WorkflowTemplateType) response.getData();            
//            switch (templateType.getName()) {
//                case "EID CONTRACT": {
//                    InternalResponse res = ProcessELaborContract.getHashDocument(
//                            user,
//                            woAc.getId(),
//                            woAc.getFile(),
//                            woAc.getRequestData(),
//                            jwt,
//                            fileData,
//                            signingObject,
//                            transactionID);
//                    if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
//                        return res;
//                    }                    
//                    return res;
//                }
//                case "ESIGNCLOUD": {
//                    return new InternalResponse(500, "NOT PROVIDED YET");
//                }
//                default: {
//                    return new InternalResponse(500, "NOT PROVIDED YET");
//                }
//            }       
    }

    //==================INTERAL METHOD/FUNCTION===================
    //=========================MAIN================================
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
