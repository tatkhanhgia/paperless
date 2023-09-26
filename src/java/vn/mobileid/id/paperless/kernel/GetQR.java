/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import java.awt.print.Paper;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.QR;

/**
 *
 * @author GiaTK
 */
public class GetQR {

    public static InternalResponse getQR(
            int qrId,
            String transactionId
    ) throws Exception {
        DatabaseResponse response = new DatabaseImpl().getQR(qrId, transactionId);
        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            null));
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                response.getObject()
        );
    }
    
    public static void main(String[] args) throws Exception {
        InternalResponse response = GetQR.getQR(428, "");
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            System.out.println("Message:"+response.getMessage());
        } else {
            System.out.println("Image:"+((QR)response.getData()).getImageBase64());
            
        }
    }
}
