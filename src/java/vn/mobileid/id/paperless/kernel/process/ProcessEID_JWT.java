/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import vn.mobileid.id.eid.object.JWT_Authenticate;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.KYC;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class ProcessEID_JWT {

    public static HashMap<String, String[]> mapping = new HashMap<>();

    public static InternalResponse getInfoJWT(String jwt) {
        //Decode JWT
        String[] chunks = jwt.split("\\.");

        String header = null;
        String payload = null;
        String signature = null;
        JWT_Authenticate jwtData = new JWT_Authenticate();

        try {
            header = new String(Base64.getUrlDecoder().decode(chunks[0]), "UTF-8");  //Algorithm - tokentype
            payload = new String(Base64.getUrlDecoder().decode(chunks[1]), "UTF-8"); //Data - User
            signature = chunks[2];
        } catch (Exception e) {
            LogHandler.error(
                    ProcessEID_JWT.class,
                    "transaction",
                    "Cannot decode token!!",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_JWT,
                            PaperlessConstant.SUBCODE_INVALID_JWT_TOKEN,
                            "en",
                            null));
        }
        try {
            jwtData = new ObjectMapper().readValue(payload, JWT_Authenticate.class);
        } catch (Exception e) {
            LogHandler.error(
                    ProcessEID_JWT.class,
                    "transaction",
                    "Error while mapping JWT data!!",
                    e);
            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_BAD_REQUEST,
                    PaperlessMessageResponse.getErrorMessage(
                            PaperlessConstant.CODE_INVALID_PARAMS_JWT,
                            PaperlessConstant.SUBCODE_INVALID_JWT_TOKEN,
                            "en",
                            null));
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                jwtData);
    }

    public static InternalResponse mappingIntoKYC(JWT_Authenticate data) {
        Field[] fields_KYC = KYC.getHashMapFieldName();
        Field[] fields_jwt = JWT_Authenticate.getHashMapFieldName();
        for (Field field : fields_KYC) {
            if (!mapping.containsKey(field.getName())) {
                for (Field field_jwt : fields_jwt) {
                    if (field.getName().contains(field_jwt.getName())) {
                        String[] temp = new String[]{field_jwt.getName()};
                        mapping.put(field.getName(), temp);
                    }
                }
            }
        }
        return null;
    }
}
