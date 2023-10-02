/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import java.util.List;
import vn.mobileid.id.general.database.DatabaseImpl_V2_QR;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.QRType;

/**
 *
 * @author GiaTK
 */
public class GetQRType {
    
    /**
     * Get a list of QR Size
     * @param transactionId
     * @return
     * @throws Exception 
     */
    public static InternalResponse getListQRType(String transactionId)throws Exception{
        DatabaseImpl_V2_QR callDb = new DatabaseImpl_V2_QR();
        
        DatabaseResponse response = callDb.getListQRType(transactionId);
        
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
            return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
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
    
    public static void main(String[] args) throws Exception {
        InternalResponse response = GetQRType.getListQRType("transactionId");
        
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            System.out.println("Mess:"+response.getMessage());
        } else {
            List<QRType> list = (List<QRType>)response.getData();
            for(QRType a : list){
                System.out.println("Name:"+a.getQr_type_name());
            }
        }
    }
}
