/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import vn.mobileid.id.general.database.DatabaseImpl_V2_Workflow;
import vn.mobileid.id.general.database.DatabaseV2_Workflow;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.Workflow;
import vn.mobileid.id.paperless.serializer.CustomListWorkflowSerializer;

/**
 *
 * @author GiaTK
 */
public class GetWorkflow {
    //<editor-fold defaultstate="collapsed" desc="Get Workflow">
    public static InternalResponse getWorkflow(
            long pWORKFLOW_ID,
            String transactionId
    )throws Exception{
        DatabaseV2_Workflow callDb = new DatabaseImpl_V2_Workflow();
        DatabaseResponse response = callDb.getWorkflow(pWORKFLOW_ID, transactionId);
        
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

    //<editor-fold defaultstate="collapsed" desc="Get Row Count of Workflow">
    /**
     * Get Row count of Workflow
     * @param pUSER_EMAIL
     * @param pENTERPRISE_ID
     * @param pWORKFLOW_STATUS
     * @param pLIST_TYPE
     * @param pUSE_META_DATA
     * @param pMETA_DATA
     * @param transactionId
     * @return int row_count
     * @throws Exception 
     */
    public static InternalResponse getRowCountOfWorkflow(
            String pUSER_EMAIL,
            long pENTERPRISE_ID,
            String pWORKFLOW_STATUS,
            String pLIST_TYPE,
            boolean pUSE_META_DATA,
            String pMETA_DATA,
            String transactionId
    )throws Exception{
        DatabaseV2_Workflow callDb = new DatabaseImpl_V2_Workflow();
        DatabaseResponse response = callDb.getRowCountOfWorkflow(
                pUSER_EMAIL, 
                pENTERPRISE_ID, 
                pWORKFLOW_STATUS,
                pLIST_TYPE, 
                pUSE_META_DATA,
                pMETA_DATA, 
                transactionId);
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
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
    
    //<editor-fold defaultstate="collapsed" desc="Get List of Workflow">
    /**
     * Get list of Workflow
     * @param pUSER_EMAIL
     * @param pENTERPRISE_ID
     * @param pWORKFLOW_STATUS
     * @param pLIST_TYPE
     * @param pUSE_META_DATA
     * @param pMETA_DATA
     * @param pOFFSET
     * @param pROW_COUNT
     * @param transactionId
     * @return List<Workflow>
     * @throws Exception 
     */
    public static InternalResponse getListWorkflow(
            String pUSER_EMAIL,
            long pENTERPRISE_ID,
            String pWORKFLOW_STATUS,
            String pLIST_TYPE,
            boolean pUSE_META_DATA,
            String pMETA_DATA,
            int pOFFSET,
            int pROW_COUNT,
            String transactionId
    )throws Exception{
        DatabaseV2_Workflow callDb = new DatabaseImpl_V2_Workflow();
        DatabaseResponse response = callDb.getListOfWorkflow(
                pUSER_EMAIL, 
                pENTERPRISE_ID, 
                pWORKFLOW_STATUS,
                pLIST_TYPE, 
                pUSE_META_DATA,
                pMETA_DATA, 
                pOFFSET,
                pROW_COUNT,
                transactionId);
        if(response.getStatus() != PaperlessConstant.CODE_SUCCESS){
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
    
    public static void main(String[] args) throws Exception{
//        InternalResponse response = GetWorkflow.getWorkflow(491, "transactionId");
//        
//        if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
//            System.out.println("Mess:"+response.getMessage());
//        } else {
//            Workflow temp = (Workflow)response.getData();
//            System.out.println(temp.getStatus());
//            System.out.println(temp.getWorkflowTemplate_type());
//            System.out.println(temp.getWorkflowTemplate_type_name());
//            System.out.println(temp.getWorkflowTemplate_type_name_en());
//        }

//        InternalResponse response = GetWorkflow.getRowCountOfWorkflow(
//                "khanhpx@mobile-id.vn", 
//                3, 
//                "1,2,3",
//                "7,8", 
//                false,
//                null, 
//                "transactionId");
//        
//        System.out.println("Mess:"+response.getMessage());
//        System.out.println("RowL:"+response.getData());

           InternalResponse response = GetWorkflow.getListWorkflow(
                   "khanhpx@mobile-id.vn", 
                   3,
                   "1,2,3", 
                   "1,2,3,4,5,6,7,8", 
                   false,
                   null, 
                   0, 
                   100,
                   "transactionId");
           
           if(response.getStatus() != PaperlessConstant.HTTP_CODE_SUCCESS){
               System.out.println("Mess:"+response.getMessage());
           } else {
               List<Workflow> list = (List<Workflow>)response.getData();
               CustomListWorkflowSerializer custom = new CustomListWorkflowSerializer(list, 1, 0);
               System.out.println(new ObjectMapper().writeValueAsString(custom));
           }
    }
}
