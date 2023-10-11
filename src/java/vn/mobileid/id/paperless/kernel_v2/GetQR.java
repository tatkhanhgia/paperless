/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.database.DatabaseImpl_V2_QR;
import vn.mobileid.id.general.database.DatabaseV2_QR;
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

    //<editor-fold defaultstate="collapsed" desc="Get QR">
    public static InternalResponse getQR(
            long pQR_ID,
            String transactionId
    ) throws Exception {
        DatabaseV2_QR callDb = new DatabaseImpl_V2_QR();

        DatabaseResponse response = callDb.getQR(pQR_ID, transactionId);

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_FAIL,
                            response.getStatus(),
                            "en",
                            transactionId)
            );
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                response.getObject()
        );
    }
    //</editor-fold>
    
    public static void main(String[] args)throws Exception {
        InternalResponse response = GetQR.getQR(420, "");
        
        QR temp = (QR)response.getData();
        System.out.println(temp.getId());
        System.out.println(temp.getImage());
        System.out.println(temp.getMeta_data());
        System.out.println(temp.getCreated_at());
        System.out.println(temp.getCreated_by());
    }
}
