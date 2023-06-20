/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel;

import java.util.List;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.keycloak.obj.User;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.Asset;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.Workflow;
import vn.mobileid.id.paperless.objects.WorkflowDetail_Option;

/**
 *
 * @author GiaTK
 */
public class UpdateWorkflowDetail_option {

    /**
     * Update Workflow Option of the workflow
     *
     * @param id
     * @param email
     * @param aid - Enterprise _ id
     * @param detail
     * @param hmac
     * @param created_by
     * @param transactionID
     * @return
     * @throws Exception
     */
    public static InternalResponse updateWorkflowOption(
            int id,
            User user,
            int aid,
            WorkflowDetail_Option detail,
            String hmac,
            String created_by,
            String transactionID) throws Exception {

        //Get Old Option => get Asset        
        updateAsset(id, detail, user, transactionID);

        Database DB = new DatabaseImpl();

        DatabaseResponse callDB = DB.updateWorkflowDetail_Option(
                id,
                user.getEmail(),
                aid,
                detail.getHashMap(),
                hmac,
                created_by,
                transactionID);

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

        if (callDB.getObject() != null) {
            for (Integer error : (List<Integer>) callDB.getObject()) {
                String message = "{All data already update except:";
                message += PaperlessMessageResponse.getErrorMessage(
                        PaperlessConstant.CODE_FAIL,
                        error,
                        "en",
                        null);
                return new InternalResponse(
                        PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        message + "}"
                );
            }
        }

        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "");
    }

    private static InternalResponse updateAsset(
            int id,
            WorkflowDetail_Option detail,
            User user,
            String transactionID) throws Exception {
//        InternalResponse response = GetWorkflow.getWorkflow(id, transactionID);
//        if( response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
//            return response;
//        }
//        Workflow worfklow = (Workflow)response.getData();
        InternalResponse response = GetWorkflowDetail_option.getWorkflowDetail(id, transactionID);
        if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
            return response;
        }
        WorkflowDetail_Option temp = (WorkflowDetail_Option) response.getData();

        //Update old Asset
        if (temp.getAsset_Append() > 0 && (detail.getAsset_Append() != temp.getAsset_Append())) {
            response = GetAsset.getAsset(temp.getAsset_Append(), transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            Asset asset = (Asset) response.getData();
            String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
            String[] tokens = used_by.split(String.valueOf(id));
            used_by = "";
            if (tokens.length <= 1) {
                used_by = tokens[0];
            } else {
                used_by += tokens[0].substring(0, tokens[0].length());
                used_by += tokens[1].substring(1);
            }
            asset.setUsed_by(used_by);
            UpdateAsset.updateAsset(asset, user, transactionID);
        }
        if (temp.getAsset_Background() > 0 && (detail.getAsset_Background() != temp.getAsset_Background())) {
            response = GetAsset.getAsset(temp.getAsset_Background(), transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            Asset asset = (Asset) response.getData();
            String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
            String[] tokens = used_by.split(String.valueOf(id));
            used_by = "";
            if (tokens.length <= 1) {
                used_by = tokens[0];
            } else {
                used_by += tokens[0].substring(0, tokens[0].length());
                used_by += tokens[1].substring(1);
            }
            asset.setUsed_by(used_by);
            UpdateAsset.updateAsset(asset, user, transactionID);
        }
        if (temp.getAsset_Template() > 0 && (detail.getAsset_Template() != temp.getAsset_Template())) {
            response = GetAsset.getAsset(temp.getAsset_Template(), transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            Asset asset = (Asset) response.getData();
            String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();            
            String[] tokens = used_by.split(String.valueOf(id));
            used_by = "";
            if (tokens.length <= 1) {
                used_by = tokens[0];
            } else {
                used_by += tokens[0].substring(0, tokens[0].length());
                used_by += tokens[1].substring(1);
            }            
            asset.setUsed_by(used_by);
            UpdateAsset.updateAsset(asset, user, transactionID);
        }

        //Update new Asset
        if (detail.getAsset_Append() > 0 && (detail.getAsset_Append() != temp.getAsset_Append())) {
            response = GetAsset.getAsset(detail.getAsset_Append(), transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            Asset asset = (Asset) response.getData();
            String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
            used_by += id + ",";
            asset.setUsed_by(used_by);
            UpdateAsset.updateAsset(asset, user, transactionID);
        }
        if (detail.getAsset_Background() > 0 && (detail.getAsset_Background() != temp.getAsset_Background())) {
            response = GetAsset.getAsset(detail.getAsset_Background(), transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            Asset asset = (Asset) response.getData();
            String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
            used_by += id + ",";
            asset.setUsed_by(used_by);
            UpdateAsset.updateAsset(asset, user, transactionID);
        }
        if (detail.getAsset_Template() > 0 && (detail.getAsset_Template() != temp.getAsset_Template())) {
            response = GetAsset.getAsset(detail.getAsset_Template(), transactionID);
            if (response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS) {
                return response;
            }
            Asset asset = (Asset) response.getData();
            String used_by = asset.getUsed_by() == null ? "" : asset.getUsed_by();
            used_by += id + ",";
            asset.setUsed_by(used_by);
            UpdateAsset.updateAsset(asset, user, transactionID);
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                "");
    }

    public static void main(String[] args) {
//        WorkflowDetail_Option detail = new WorkflowDetail_Option();
////        detail.setMetadata("metadata 1-2-3-4-4");
//        detail.setCSV_email(true);
//                
//        InternalResponse res = UpdateWorkflowDetail_option.updateWorkflowOption(
//                12,
//                detail,
//                "",
//                "GIATK");
//        
//        System.out.println("Mes:"+res.getMessage());
        String used_by = "113,484,142,";
        String[] tokens = used_by.split(String.valueOf(484));
        used_by = "";
        if (tokens.length <= 1) {
            used_by = tokens[0];
        } else {
            used_by += tokens[0].substring(0, tokens[0].length() );
            used_by += tokens[1].substring(1);
        }
        System.out.println("UsedBy:"+used_by);
    }

}
