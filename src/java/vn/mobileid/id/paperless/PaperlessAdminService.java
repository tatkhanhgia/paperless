/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.kernel.CreateWorkflow;
import vn.mobileid.id.paperless.kernel.ManageTokenWithDB;
import vn.mobileid.id.paperless.kernelADMIN.CreateAccount;
import vn.mobileid.id.paperless.objects.Account;
import vn.mobileid.id.paperless.objects.QryptoMessageResponse;
import vn.mobileid.id.paperless.objects.Workflow;
import vn.mobileid.id.utils.Configuration;
import vn.mobileid.id.utils.Utils;

/**
 * Using for check valid of data!!
 *
 * @author GiaTK
 */
public class PaperlessAdminService {

    final private static Properties appInfo = Configuration.getInstance().getAppInfo();

    public static InternalResponse getToken(
            final HttpServletRequest request,
            String payload,
            int functionId,
            String transactionID) {
//        if (functionId == 0) {
//            ManageTokenWithIAM token = new ManageTokenWithIAM();
//            InternalResponse response = token.processJSON_getToken(request, payload);
//            return response;
//        } else {
//            ManageTokenWithIAM token = new ManageTokenWithIAM();
//            InternalResponse response = token.processFORM_getToken(request, payload);
//            return response;
//        }
        if (functionId == 0) {
            InternalResponse response = ManageTokenWithDB.processLogin(
                    request,
                    payload,
                    transactionID);
            return response;
        } else {
            InternalResponse response = ManageTokenWithDB.processLoginURLEncode(
                    request,
                    Utils.getDataFromURLEncode(payload),
                    transactionID);
            return response;
        }
    }

    public static InternalResponse createAccount(
            final HttpServletRequest request,
            String payload,
            String transactionID) {
        //Check valid token
        InternalResponse response = verifyToken(request, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS || response == null) {
            return response;
        }        

        if (Utils.isNullOrEmpty(payload)) {
            if (LogHandler.isShowDebugLog()) {
                LogHandler.debug(PaperlessAdminService.class, transactionID, "No Payload found!!");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_NO_PAYLOAD_FOUND,
                            "en",
                            null));
        }              

        ObjectMapper mapper = new ObjectMapper();
        Account account = new Account();
        try {
            account = mapper.readValue(payload, Account.class);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessAdminService.class, transactionID, "Cannot parse payload");
            }
            return new InternalResponse(PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    QryptoMessageResponse.getErrorMessage(PaperlessConstant.CODE_FAIL,
                            PaperlessConstant.SUBCODE_INVALID_PAYLOAD_STRUCTURE,
                            QryptoMessageResponse.getLangFromJson(payload),
                            null));
        }

        //Check valid data 
        InternalResponse result = null;
        result = CreateAccount.checkDataAccount(account);
        if (result.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return result;
        }

        //Processing
        try {
            return null;
//            return CreateWorkflow.processingCreateWorkflow(
//                    workflow,
//                    user_info,
//                    transactionID);
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LogHandler.error(PaperlessAdminService.class, transactionID, "Cannot create new Workflow");
            }
            return new InternalResponse(500, PaperlessConstant.INTERNAL_EXP_MESS);
        }
    }

    public static InternalResponse verifyToken(
            final HttpServletRequest request,
            String transactionID
    ) {
        InternalResponse response = ManageTokenWithDB.processVerify(
                request,
                transactionID,
                true);
        return response;
    }
}
