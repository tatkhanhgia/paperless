/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import java.util.List;
import vn.mobileid.id.general.database.DatabaseImpl_V2_QR;
import vn.mobileid.id.general.database.DatabaseV2_QR;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.QRSize;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;

/**
 *
 * @author GiaTK
 */
public class GetQRSize {
    
    //<editor-fold defaultstate="collapsed" desc="Get List of QR Size">
    /**
     * Get a list of QR Size
     * @param transactionId
     * @return List<QRSize>
     * @throws Exception 
     */
    public static InternalResponse getListQRSize(String transactionId)throws Exception{
        DatabaseV2_QR callDb = new DatabaseImpl_V2_QR();
        
        DatabaseResponse response = callDb.getListQRSize(transactionId);
        
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
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Get QR Size">
    /**
     * Get a list of QR Size
     * @param pQR_SIZE_NAME
     * @param transactionId
     * @return QRSize Object
     * @throws Exception 
     */
    public static InternalResponse getQRSize(String pQR_SIZE_NAME, String transactionId)throws Exception{
        DatabaseV2_QR callDb = new DatabaseImpl_V2_QR();
        
        DatabaseResponse response = callDb.getQRSize(pQR_SIZE_NAME, transactionId);
        
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
    //</editor-fold>
    
    
    public static void main(String[] args) throws Exception {
        InternalResponse response = GetQRSize.getListQRSize("transactionId");
        
        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
            System.out.println("Mess:"+response.getMessage());
        } else {
            List<QRSize> list = (List<QRSize>)response.getData();
            for(QRSize a : list){
                System.out.println("Name:"+a.getQr_size_name());
            }
        }
    }
}
