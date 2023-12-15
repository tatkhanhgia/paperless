/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import vn.mobileid.id.general.database.DatabaseImpl_V2_Enterprise;
import vn.mobileid.id.general.database.DatabaseV2_Enterprise;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.objects.Enterprise_SigningInfo;
import vn.mobileid.id.paperless.objects.Enterprise_SigningInfo.TemplateSignature;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class GetEnterpriseSigningInfo {

    //<editor-fold defaultstate="collapsed" desc="Get Enterprise Signing Info">
    /**
     * Get Enterprise Signing Info (DataSignature + PositionSignature +
     * RSSPInfo)
     *
     * @param enterpriseId
     * @param transactionId
     * @return Enterprise_SigningInfo
     * @throws Exception
     */
    public static InternalResponse getEnterpriseSigningInfo(
            int enterpriseId,
            String transactionId
    ) throws Exception {
        DatabaseV2_Enterprise db = new DatabaseImpl_V2_Enterprise();

        DatabaseResponse response = db.getSigningInfoOfEnterprise(enterpriseId, transactionId);

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
        List<HashMap<String, Object>> rows = response.getRows();
        for (HashMap<String, Object> row : rows) {
            if (row.containsKey("SIGNING_INFO_PROPERTIES")) {
                String json = (String) row.get("SIGNING_INFO_PROPERTIES");
                Enterprise_SigningInfo ent = new ObjectMapper().readValue(json, Enterprise_SigningInfo.class);
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_SUCCESS, ent
                );
            }
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_BAD_REQUEST, ""
        );
    }
    //</editor-fold>

    public static void main(String[] args) throws Exception {
        InternalResponse response = getEnterpriseSigningInfo(3, "");

        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            System.out.println("Out:");
            return;
        }
        Enterprise_SigningInfo ent = (Enterprise_SigningInfo) response.getData();
        System.out.println(ent.getRsspinfo().getBusinessAgreementUUID());
        System.out.println(ent.getRsspinfo().getBusinessCredentialId());
        System.out.println(ent.getRsspinfo().getBusinessPasswordUUID());
        
        System.out.println("============");
        System.out.println(ent.getRsspinfo().getWitnessAgreementUUID());
        System.out.println(ent.getRsspinfo().getWitnessCredentialId());
        System.out.println(ent.getRsspinfo().getWitnessPasswordUUID());
        
        System.out.println("============");
        
        for(TemplateSignature template : ent.getDataSignature()){
            System.out.println("=======Template "+template.getType()+"=======");
            System.out.println(template.getType());
            System.out.println(template.getBusinessData().getReason());
            System.out.println(template.getBusinessData().getLocation());
            System.out.println(template.getBusinessData().getTextContent());
            System.out.println(template.getBusinessData().getDate_format());
            
            System.out.println("============");
            System.out.println(template.getSignerData().getReason());
            System.out.println(template.getSignerData().getLocation());
            System.out.println(template.getSignerData().getTextContent());
            System.out.println(template.getSignerData().getDate_format());
        }
    }
}
