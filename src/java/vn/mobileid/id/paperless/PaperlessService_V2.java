/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.InternalResponse;
import static vn.mobileid.id.paperless.PaperlessService.verifyToken;
import vn.mobileid.id.paperless.kernelADMIN.DeleteUser;
import vn.mobileid.id.paperless.kernel_v2.GetUser;
import vn.mobileid.id.paperless.kernel_v2.GetWorkflowActivity;
import vn.mobileid.id.paperless.kernel_v2.UpdateUser;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.utils.Excel_Processing;
import vn.mobileid.id.general.PolicyConfiguration;
import vn.mobileid.id.utils.Task;
import vn.mobileid.id.utils.TaskV2;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class PaperlessService_V2 {

    public static InternalResponse updateGeneralProfile(
            final HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        //Check valid token        
        InternalResponse response = PaperlessService.verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        User user_info = response.getUser();

        //Check payload
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        ObjectMapper mapper = new ObjectMapper();
        Account account = new Account();
        try {
            account = mapper.readValue(payload, Account.class);
        } catch (Exception e) {
            LogHandler.error(
                    PaperlessAdminService.class,
                    transactionID,
                    "Cannot parse payload",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        
        response = UpdateUser.updateUser(
                account.getUser_email(),
                account.getUser_name(),
                account.getMobile_number(),
                null,
                0,
                null,
                0,
                false,
                null,
                0,
                account.getOrganizationWebsite(),
                "hmac",
                account.getUser_email(),
                transactionID);
        response.setUser(user_info);
        return response;
    }

    //<editor-fold defaultstate="collapsed" desc="Update Enterprise User">
    public static InternalResponse updateEnterpriseUser(
            final HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        //Check valid token        
        InternalResponse response = PaperlessService.verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        User user_token = response.getUser();

        //Get User extend data
        response = GetUser.getUser(
                user_token.getEmail(),
                0,
                user_token.getAid(),
                transactionID,
                true);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        User user_info = (User) response.getData();
        user_info.setAid(user_token.getAid());
        user_info.setAzp(user_token.getAzp());

        //Check payload
        if (Utils.isNullOrEmpty(payload)) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }

        //Check role
        if (!user_info.getRole_name().equalsIgnoreCase("owner") && !user_info.getRole_name().equalsIgnoreCase("admin")) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    null);
        }

        ObjectMapper mapper = new ObjectMapper();
        Account account = new Account();
        try {
            account = mapper.readValue(payload, Account.class);
        } catch (Exception e) {
            LogHandler.error(
                    PaperlessAdminService.class,
                    transactionID,
                    "Cannot parse payload",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            PaperlessMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Thread Pool
        ExecutorService executor = Executors.newFixedThreadPool(2);

        //Create runnable updateProfile
        Task task = new Task(new Object[]{account}, transactionID) {
            @Override
            public void run() {
                try {
                    Account temp = (Account) this.getData()[0];
                    this.setResponse(
                            UpdateUser.updateUser(
                                    temp.getUser_email(),
                                    temp.getUser_name(),
                                    temp.getMobile_number(),
                                    null,
                                    0,
                                    null,
                                    0,
                                    false,
                                    null,
                                    0,
                                    temp.getOrganizationWebsite(),
                                    "hmac",
                                    temp.getUser_email(),
                                    transactionID));
                } catch (Exception ex) {
                    LogHandler.error(this.getClass(), "", ex);
                }
            }
        };

        //Create runnable update Role        
        Task task2 = new Task(new Object[]{account, user_info}, transactionID) {
            @Override
            public void run() {
                try {
                    Account temp = (Account) this.getData()[0];                    
                    User temp2 = (User) this.getData()[1];
                    this.setResponse(
                            UpdateUser.updateRole(
                                    temp.getUser_email(),
                                    temp2.getAid(),
                                    temp.getRole(),
                                    transactionID));
                } catch (Exception ex) {
                    LogHandler.error(this.getClass(), "", ex);
                }
            }
        };

        executor.submit(task);
        executor.submit(task2);

        executor.shutdown();
        while (!executor.isTerminated()) {
        }
//        if(     task.getResponse() == null||
//                task2.getResponse() == null ||
//                task.getResponse().getStatus()!=PaperlessConstant.HTTP_CODE_SUCCESS ||
//                task2.getResponse().getStatus()!=PaperlessConstant.HTTP_CODE_SUCCESS){
//            return new InternalResponse(
//                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
//                    "{\"Message\":\"Cannot update user profile\"}");
//        }
        if (task.getResponse() == null || task2.getResponse() == null) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_500,
                    ""
            );
        }
        if(task.getResponse().getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            return task.getResponse();
        }
        if(task2.getResponse().getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            return task2.getResponse();
        }
        response = new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "");
        response.setUser(user_token);
        return response;
    }
    //</editor-fold>
    

    public static InternalResponse deleteUser(
            final HttpServletRequest request,
            String payload,
            String transactionID) throws Exception {
        //Check valid token        
        InternalResponse response = PaperlessService.verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }
        User user_info = response.getUser();

//        //Check payload
//        if (Utils.isNullOrEmpty(payload)) {
//            return new InternalResponse(
//                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
//                    PaperlessMessageResponse.getErrorMessage(
//                            PaperlessConstant.CODE_FAIL,
//                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
//                            "en",
//                            null));
//        }
        
        //Check email to be delete in payload
        String emailToBeDelete = Utils.getFromJson("user_email", payload);
        if(Utils.isNullOrEmpty(emailToBeDelete)){
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_KEYCLOAK,
                            PaperlessConstant.SUBCODE_MISSING_USER_EMAIL,
                            "en",
                            null));
        }
        //Get role
        response = GetUser.getUser(user_info.getEmail(),
                0,
                user_info.getAid(),
                transactionID, true);
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){return response;}
                
        User temp = response.getUser();
        //Check role
        if (!temp.getRole_name().equalsIgnoreCase("owner") && !temp.getRole_name().equalsIgnoreCase("admin")) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_UNAUTHORIZED,
                    null);
        }
        
        response = DeleteUser.deleteUser(
                user_info.getEmail(), 
                emailToBeDelete, 
                user_info.getAid(), 
                transactionID);
        response.setUser(user_info);
        return response;
    }

    //<editor-fold defaultstate="collapsed" desc="Get Report of Workflow Ac">
    public static InternalResponse getReportofWorkflowActivity(
            final HttpServletRequest request,
            String transactionID) throws JsonProcessingException, Exception {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }

        User user_info = response.getUser();

        //Process header        
        String URI = request.getRequestURI();
        URI = URI.replaceFirst(".*workflowactivity/report/", "");
        String[] data = URI.split("/");
        String status = data[0];
        String search = "1,2,3,4";
        if (status != null) {
            if (status.contains("ACTIVE")) {
                search = "1";
            } else if (status.contains("HIDDEN")) {
                search = "2";
            } else if (status.contains("EXPIRED")) {
                search = "3";
            } else if (status.contains("REVOKE")) {
                search = "4";
            } else if (status.contains("ALL")) {
                search = "1,2,3,4";
            }
        }

        int page_no;
        int record;
        int numberOfRecords = PaperlessConstant.DEFAULT_ROW_COUNT;
        try {
            page_no = (data[1] == null ? 0 : Integer.parseInt(data[1]));
            record = (data[2] == null ? 0 : Integer.parseInt(data[2]));
        } catch (Exception e) {
            page_no = 0;
            record = 0;
        }
        try {
            numberOfRecords = Integer.parseInt(Utils.getRequestHeader(request, "x-number-records"));
            page_no = 0;
            record = 0;
        } catch (Exception ex) {
        }

        //Search type
        String email_search = Utils.getRequestHeader(request, "x-search-email");
        String start_ = Utils.getRequestHeader(request, "x-search-start");
        String stop_ = Utils.getRequestHeader(request, "x-search-stop");
        Date start = null, stop = null;
        try {
            if (start_ != null) {
                SimpleDateFormat format = new SimpleDateFormat(PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
                start = format.parse(start_);
            }
            if (stop_ != null) {
                SimpleDateFormat format = new SimpleDateFormat(PolicyConfiguration
                        .getInstant()
                        .getSystemConfig()
                        .getAttributes()
                        .get(0)
                        .getDateFormat());
                stop = format.parse(stop_);
            }
        } catch (Exception ex) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    "{\"error\":\"INVALID DATE\",\"error_description\":\"Please follow this format of Date:" + PolicyConfiguration
                            .getInstant()
                            .getSystemConfig()
                            .getAttributes()
                            .get(0)
                            .getDateFormat() + "\"}"
            );
        }

        //Thread Pool to check template
        String searchTemplate = "1,2,3,4,5,6,7,8";
        String template = Utils.getRequestHeader(request, "x-search-type");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        TaskV2 templateType1 = new TaskV2(new Object[]{template}, transactionID) {
            @Override
            public Object call() {
                try {
                    String result = null;
                    String template = (String) this.get()[0];
                    if (template != null) {
                        template = new String(Base64.getDecoder().decode(template));
                        switch (template) {
                            case "QR GENERATOR": {
                                result = "1";
                                break;
                            }
                            case "PDF GENERATOR": {
                                result = "2";
                                break;
                            }
                            case "SIMPLE PDF STAMPING": {
                                result = "3";
                                break;
                            }
                            case "PDF STAMPING": {
                                result = "4";
                                break;
                            }
                            case "FILE STAMPING PROCESSING": {
                                result = "5";
                                break;
                            }
                            case "LEI PDF STAMPING": {
                                result = "6";
                                break;
                            }
                            case "E-LABOR CONTRACT": {
                                result = "7";
                                break;
                            }
                            case "ESIGNCLOUD": {
                                result = "8";
                                break;
                            }
                            default: {
                                result = "1,2,3,4,5,6,7,8";
                                break;
                            }
                        }
                    }
                    return result;
                } catch (Exception ex) {
                    return null;
                }
            }
        };

        TaskV2 templateType2 = new TaskV2(new Object[]{template}, transactionID) {
            @Override
            public Object call() {
                try {
                    String template = new String(Base64.getDecoder().decode((String) this.get()[0]));
                    String[] temps = template.split(",");
                    for (String temp : temps) {
                        Integer.parseInt(temp);
                    }
                    return template;
                } catch (Exception ex) {
                    try {
                        String[] temps = template.split(",");
                        for (String temp : temps) {
                            Integer.parseInt(temp);
                        }
                        return template;
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        };

        //Get future
        Future<?> future1 = executor.submit(templateType1);
        Future<?> future2 = executor.submit(templateType2);
        executor.shutdown();

        searchTemplate
                = (future1.get() == null)
                ? (future2.get() == null
                ? searchTemplate
                : (String) future2.get())
                : (String) future1.get();

        //Processing
        InternalResponse res = GetWorkflowActivity.getListWorkflowActivity(
                user_info.getEmail(), 
                user_info.getAid(), 
                email_search, 
                null, 
                searchTemplate, 
                search, 
                "1,2,3",
                (start != null || stop != null), 
                start, 
                stop, 
                (page_no <= 1) ? 0 : (page_no - 1) * record, 
                record == 0 ? numberOfRecords : record, 
                transactionID);   
        if (res.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return res;
        }
        List<WorkflowActivity> list = (List<WorkflowActivity>) res.getData();
        byte[] fileExcel =null;
        try{
            fileExcel = Excel_Processing.createExcel(list);
        }catch(Exception ex){
            LogHandler.error(PaperlessService_V2.class,
                    "Cannot create Excel file",
                    ex);
            res.setStatus(PaperlessConstant.HTTP_CODE_BAD_REQUEST);
            res.setMessage("{\"Message\":\"Cannot create Excel file!\"}");
            return res;
        }
        res.setData(fileExcel);
        res.setUser(user_info);
        return res;
    }
    //</editor-fold>
    
}