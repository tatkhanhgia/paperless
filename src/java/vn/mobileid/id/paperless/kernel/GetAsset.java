/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import java.io.IOException;
import java.util.List;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;

/**
 *
 * @author GiaTK
 */
public class GetAsset {

    /**
     * Get Asset
     * @param id - ID of Asset
     * @param transactionID
     * @return Asset
     * @throws Exception 
     */
    public static InternalResponse getAsset(
            int id,
            String transactionID)
            throws Exception {
        Database DB = new DatabaseImpl();       

        DatabaseResponse callDB = DB.getAsset(id, transactionID);

        try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            Asset asset = (Asset) callDB.getObject();

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    asset);

        } catch (Exception e) {
            throw new Exception("Cannot get Asset!", e);
        }
    }

    /**
     * Get a Template of the Asset
     * @param id - Id of the Asset
     * @param transactionID
     * @return String - Metadata of that Asset
     * @throws Exception 
     */
    public static InternalResponse getTemplateOfAsset(
            int id,
            String transactionID)
            throws Exception {
        Database DB = new DatabaseImpl();       

        DatabaseResponse callDB = DB.getAsset(id, transactionID);

        try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = null;
                message = PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            Asset asset = (Asset) callDB.getObject();

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    asset.getMetadata());
        } catch (Exception e) {
            throw new Exception("Cannot get Asset Template!", e);
        }
    }

    /**
     * Get a list of Asset
     * @param ent_id - Enterprise id
     * @param email - Email of User
     * @param file_name - Get with condition file name
     * @param type - Get with condition type of Asset
     * @param transactionID
     * @return List of Asset
     * @throws Exception 
     */
    public static InternalResponse getListAsset(
            int ent_id,
            String email,
            String file_name,
            String type,
            int offset,
            int rowcount,
            String transactionID
    ) throws Exception {
        Database DB = new DatabaseImpl();        

        DatabaseResponse callDB = DB.getListAsset(
                ent_id,
                email,
                file_name,
                type,
                offset,
                rowcount,
                transactionID);

        try {
            if (callDB.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                String message = PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        callDB.getStatus(),
                        "en",
                        null);
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message
                );
            }

            List<Asset> listAsset = (List<Asset>) callDB.getObject();

            return new InternalResponse(
                    PaperlessConstant.HTTP_CODE_SUCCESS,
                    listAsset);

        } catch (Exception e) {
            throw new Exception("Cannot get List of Asset!", e);
        }
    }

    public static void main(String[] args) throws IOException, Exception {
//        Asset resposne = GetAsset.getTemplateOfAsset(7);        
//        Files.write(new File("D:\\NetBean\\QryptoServices\\file\\asset.xslt").toPath(), resposne.getBinaryData(), StandardOpenOption.CREATE);

//        List<Asset> listAsset = (List<Asset>) GetAsset.getListAsset(
//                3,
//                "khanhpx@mobile-id.vn",
//                null,
//                null,
//                "transactionID").getData();
//        listAsset.forEach((value)->{
//            System.out.println(value.getCreated_at());
//        });
//        CustomWorkflowActivitySerializer tempp = new CustomWorkflowActivitySerializer(listAsset,0,0);
//      
////        SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1,0,0,null));
////        simpleModule.addSerializer();
//        
//        String temp = new ObjectMapper().writeValueAsString(tempp);
//      
//        System.out.println("Temp:"+temp);

          InternalResponse response = GetAsset.getListAsset(
                  3,
                  "khanhpx@mobile-id.vn",
                  null, 
                  "1,2,3",
                  0, 
                  100,
                  "transactionId");
          if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
              System.out.println("Mess:"+response.getMessage());
          } else{
              List<Asset> assets = (List<Asset>) response.getData();
              for(Asset asset:assets){
                  System.out.println("Name:"+asset.getName());
              }
          }
    }
}
