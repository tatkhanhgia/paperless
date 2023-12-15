/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.itextpdf.kernel.geom.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Enterprise_SigningInfo {

    private RSSPInfoProperties rsspinfo;
    private List<TemplateSignature> dataSignature;
    private QRPositionProperties qrProperties;

    public Enterprise_SigningInfo() {
    }

    @JsonProperty("rssp_info")
    public RSSPInfoProperties getRsspinfo() {
        return rsspinfo;
    }

    @JsonProperty("data_signature")
    public List<TemplateSignature> getDataSignature() {
        return dataSignature;
    }

    @JsonProperty("qr_position")
    public QRPositionProperties getQrProperties() {
        return qrProperties;
    }
    
    //=================================
    public void setQrProperties(QRPositionProperties qrProperties) {
        this.qrProperties = qrProperties;
    }

    public void setRsspinfo(RSSPInfoProperties rsspinfo) {
        this.rsspinfo = rsspinfo;
    }

    public void setDataSignature(List<TemplateSignature> dataSignature) {
        this.dataSignature = dataSignature;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TemplateSignature {
        //Type
        private String type;

        private DataSignatureProperties signerData;
        private SignaturePositionProperties signerPosition;

        private DataSignatureProperties businessData;
        private SignaturePositionProperties businessPosition;

        public TemplateSignature() {
        }

        @JsonProperty("template_type")
        public String getType() {
            return type;
        }

        @JsonProperty("signer_data")
        public DataSignatureProperties getSignerData() {
            return signerData;
        }

        @JsonProperty("signer_position")
        public SignaturePositionProperties getSignerPosition() {
            return signerPosition;
        }

        @JsonProperty("business_data")
        public DataSignatureProperties getBusinessData() {
            return businessData;
        }

        @JsonProperty("business_position")
        public SignaturePositionProperties getBusinessPosition() {
            return businessPosition;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setSignerData(DataSignatureProperties signerData) {
            this.signerData = signerData;
        }

        public void setSignerPosition(SignaturePositionProperties signerPosition) {
            this.signerPosition = signerPosition;
        }

        public void setBusinessData(DataSignatureProperties businessData) {
            this.businessData = businessData;
        }

        public void setBusinessPosition(SignaturePositionProperties businessPosition) {
            this.businessPosition = businessPosition;
        }

    }

    public static void main(String[] args) throws Exception {
//        RSSPInfoProperties info = new RSSPInfoProperties();
//        info.setBusinessAgreementUUID("OW06742145068696660080");
//        info.setBusinessCredentialId("0d8535f8-e54a-43f0-acbf-d88c8ae02cbc");
//        info.setBusinessPasswordUUID("12345678");
//
//        info.setWitnessAgreementUUID("9E6FA0D0-6319-4D57-A760-99BBBECB35D0");
//        info.setWitnessCredentialId("fc081a30-8ed5-40c0-93b3-7b9cbd8d40f2");
//        info.setWitnessPasswordUUID("12345678");
//
//        TemplateSignature templateELabor = new TemplateSignature();
//        templateELabor.setType("elabor");
//        DataSignatureProperties data_signature = new DataSignatureProperties();
//        data_signature.setType("elabor");
//        data_signature.setLocation("");
//        data_signature.setReason("Witnessing - @Name - @DocNumber");
//        data_signature.setTextContent("Ký bởi @Name\nCCCD: @DocNumber\nNgày ký: {date}\nLý do: @Reason");
//        data_signature.setDate_format("dd/MM/yyyy hh:mm:ss");
//
//        DataSignatureProperties data_signature_business = new DataSignatureProperties();
//        data_signature_business.setType("elabor");
//
//        templateELabor.setSignerData(data_signature);
//        templateELabor.setBusinessData(data_signature_business);
//
//        TemplateSignature templateESign = new TemplateSignature();
//        templateESign.setType("esign");
//        data_signature = new DataSignatureProperties();
//        data_signature.setType("esign");
//        data_signature.setLocation("");
//        data_signature.setReason("Signed by @Name");
//        data_signature.setTextContent("Ký bởi @Name\nCCCD: @DocNumber\nNgày ký: {date}\nLý do: @Reason");
//        data_signature.setDate_format("dd/MM/yyyy hh:mm:ss");
//
//        data_signature_business = new DataSignatureProperties();
//        data_signature_business.setType("esign");
//
//        templateESign.setSignerData(data_signature);
//        templateESign.setBusinessData(data_signature_business);
//
//        Enterprise_SigningInfo ent = new Enterprise_SigningInfo();
//        ent.setRsspinfo(info);
//
//        List<TemplateSignature> templates = new ArrayList<>();
//        templates.add(templateESign);
//        templates.add(templateELabor);
//
//        ent.setDataSignature(templates);
//
//        System.out.println("String 1:"+new ObjectMapper().writeValueAsString(ent));
//
//        //Position
//        PositionSignaturePropeties position_signer = new PositionSignaturePropeties();
//        position_signer.setBoxCoordinate("-20,-140,200,125");
//        position_signer.setKeyword("NGƯỜI LAO ĐỘNG");
//        position_signer.setPage("2");
//        position_signer.setType_Name("elabor");
//        
//        PositionSignaturePropeties position_business = new PositionSignaturePropeties();
//        position_business.setBoxCoordinate("-20,-130,160,110");
//        position_business.setKeyword("NGƯỜI SỬ DỤNG LAO ĐỘNG");
//        position_business.setPage("2");
//        position_business.setType_Name("elabor");
//        
//        TemplateSignature template_elabor_2 = new TemplateSignature();
//        template_elabor_2.setType("elabor");
//        template_elabor_2.setSignerPosition(position_signer);
//        template_elabor_2.setBusinessPosition(position_business);
//        
//        System.out.println("String 2:"+new ObjectMapper().writeValueAsString(template_elabor_2));


          //Create QR Position  
          Enterprise_SigningInfo signinInfo = new Enterprise_SigningInfo();
          QRPositionProperties position = new QRPositionProperties();
          position.setX(0);
          position.setY(0);
          position.setIsTransparent(false);
          signinInfo.setQrProperties(position);
          
          System.out.println(new ObjectMapper().writeValueAsString(signinInfo));
          
          String json = "{\"qr_position\":{\"x_coordinate\":0.0,\"y_coordinate\":0.0,\"transparent\":false,\"pages\":[1]}}";
          Enterprise_SigningInfo qr = new ObjectMapper().readValue(json, Enterprise_SigningInfo.class);
          System.out.println(qr.getQrProperties().getPages());
    }
}
 