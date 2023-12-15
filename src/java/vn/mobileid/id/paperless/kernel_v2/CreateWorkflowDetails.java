/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

import java.util.ArrayList;
import java.util.List;
import vn.mobileid.id.general.database.DatabaseImpl_V2_WorkflowDetails;
import vn.mobileid.id.general.database.DatabaseV2_WorkflowDetails;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.general.objects.InternalResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.object.enumration.WorkflowAttributeTypeName;
import vn.mobileid.id.paperless.objects.PaperlessMessageResponse;
import vn.mobileid.id.paperless.objects.WorkflowAttributeType;

/**
 *
 * @author GiaTK
 */
public class CreateWorkflowDetails {

    //<editor-fold defaultstate="collapsed" desc="Create Workflow Details">
    public static InternalResponse createWorkflowDetail(
            int id,
            List<WorkflowAttributeType> details,
            String hmac,
            String created_by,
            String transactionID
    ) throws Exception {
        DatabaseV2_WorkflowDetails callDb = new DatabaseImpl_V2_WorkflowDetails();
        for (WorkflowAttributeType temp : details) {
            if (temp.getId() == WorkflowAttributeTypeName.ASSET_APPEND.getId()
                    || temp.getId() == WorkflowAttributeTypeName.ASSET_BACKGROUND.getId()
                    || temp.getId() == WorkflowAttributeTypeName.ASSET_TEMPLATE.getId()
                    || temp.getId() == WorkflowAttributeTypeName.ASSET_ELABOR.getId()
                    || temp.getId() == WorkflowAttributeTypeName.ASSET_ESIGN.getId()) {
                try {
                    if (temp.getValue() != null && ((int) temp.getValue()) == 0) {
                        temp.setValue(null);
                    }
                } catch (Exception ex) {
                    if (temp.getValue() != null && ((String) temp.getValue()).equals("0")) {
                        temp.setValue(null);
                    }
                }
            }
            DatabaseResponse response = callDb.createWorkflowDetail(
                    id,
                    temp.getId(),
                    temp.getValue(),
                    hmac,
                    created_by,
                    transactionID);
            if (response.getStatus() != PaperlessConstant.CODE_SUCCESS) {
                return new InternalResponse(PaperlessConstant.HTTP_CODE_FORBIDDEN,
                        PaperlessMessageResponse.getErrorMessage(
                                PaperlessConstant.CODE_FAIL,
                                response.getStatus(),
                                "en",
                                transactionID)
                );
            }
        }
        return new InternalResponse(
                PaperlessConstant.HTTP_CODE_SUCCESS,
                ""
        );
    }
    //</editor-fold>

    public static void main(String[] args) throws Exception {
        WorkflowAttributeType attribute = new WorkflowAttributeType();
        attribute.setId(1);
        attribute.setValue(21);

        WorkflowAttributeType attribute2 = new WorkflowAttributeType();
        attribute2.setId(6);
        attribute2.setValue("White");

        WorkflowAttributeType attribute3 = new WorkflowAttributeType();
        attribute3.setId(3);
        attribute3.setValue(true);

        List<WorkflowAttributeType> list = new ArrayList<>();
        list.add(attribute);
        list.add(attribute2);
        list.add(attribute3);

        InternalResponse response = CreateWorkflowDetails.createWorkflowDetail(
                491,
                list,
                "HMAC",
                "GIATK",
                "transactionId");
        System.out.println("Mess:" + response.getMessage());
    }
}
