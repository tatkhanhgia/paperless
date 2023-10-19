/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import restful.sdk.API.HTTPUtils;
import restful.sdk.API.HttpResponse;
import vn.mobileid.id.general.Configuration;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class UploadToFMS {
    public static InternalResponse uploadToFMS(
            byte[] data,
            String transactionID
    ){
        LogHandler.info(UploadToFMS.class, 
                "Send to FMS:\n\tURL:"+Configuration.getInstance().getUrlFMS() +"/upload"+"");
        HttpResponse response = HTTPUtils.sendPost(
                Configuration.getInstance().getUrlFMS() +"/upload", 
                HTTPUtils.ContentType.JSON, 
                data,
                "pdf");
        System.out.println("====FMS====");
        System.out.println("\tStatus:"+response.getHttpCode());
        System.out.println("\tMess:"+response.getMsg());
        if(response.getHttpCode()!=200){
            LogHandler.error(
                    UpdateFileManagement.class,
                    transactionID,
                    response.getMsg());
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FMS,
                            PaperlessConstant.SUBCODE_FMS_REJECT_UPLOAD,
                            "en",
                            transactionID));
        }
        System.out.println("\tMessage get From FMS:"+response.getMsg());
        System.out.println("==========");
        String uuid = Utils.getFromJson("UUID", response.getMsg());
        return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,uuid);
    }
    
    public static void main(String[] args) {
        String temp = "{\"UUID\":\"638AA1E1099B1293120814F0008AE97D\"}";
        System.out.println(Utils.getFromJson(temp, temp));
    }
}
