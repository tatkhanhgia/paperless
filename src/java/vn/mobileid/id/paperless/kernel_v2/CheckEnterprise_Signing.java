/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Enterprise_SigningInfo;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class CheckEnterprise_Signing {
    //<editor-fold defaultstate="collapsed" desc="check QR Position Proprties">
    public static InternalResponse checkQRPositionProperties(
            Enterprise_SigningInfo ent,
            String transactionId
    ){
          try{
              if (ent.getQrProperties() == null || Utils.isNullOrEmpty(ent.getQrProperties().getPages())) {
                    return new InternalResponse(
                            PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                            PaperlessMessageResponse.getErrorMessage(
                                    PaperlessConstant.CODE_INVALID_PARAMS_ASSET,
                                    PaperlessConstant.SUBCODE_MISSING_QR_POSITION_PROPERTIES_IN_PAYLOAD,
                                    "en",
                                    transactionId)
                    );
                }
              return new InternalResponse(PaperlessConstant.HTTP_CODE_SUCCESS,ent);
          }catch(Exception ex){
              return new InternalResponse(
                            PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                            PaperlessMessageResponse.getErrorMessage(
                                    PaperlessConstant.CODE_INVALID_PARAMS_ASSET,
                                    PaperlessConstant.SUBCODE_MISSING_QR_POSITION_PROPERTIES_IN_PAYLOAD,
                                    "en",
                                    transactionId)
                    );
          }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Check Signature Position Properties">
   public static InternalResponse checkSignaturePositionProperties(
           Enterprise_SigningInfo ent,
           String transactionId)
   {
       try{
              if (Utils.isNullOrEmpty(ent.getDataSignature())) {
                    return new InternalResponse(
                            PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                            PaperlessMessageResponse.getErrorMessage(
                                    PaperlessConstant.CODE_INVALID_PARAMS_ASSET,
                                    PaperlessConstant.SUBCODE_MISSING_SIGNATURE_POSITION_PROPERTIES_IN_PAYLOAD,
                                    "en",
                                    transactionId)
                    );
                }
              return new InternalResponse(
                      PaperlessConstant.HTTP_CODE_SUCCESS,
                      ent
              );
          }catch(Exception ex){
              return new InternalResponse(
                            PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                            PaperlessMessageResponse.getErrorMessage(
                                    PaperlessConstant.CODE_INVALID_PARAMS_ASSET,
                                    PaperlessConstant.SUBCODE_MISSING_QR_POSITION_PROPERTIES_IN_PAYLOAD,
                                    "en",
                                    transactionId)
                    );
          }
   } 
   //</editor-fold>
}
