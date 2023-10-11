/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.database;

import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.EventAction;
import vn.mobileid.id.paperless.objects.GenerationType;
import vn.mobileid.id.paperless.objects.WorkflowType;

/**
 *
 * @author GiaTK
 */
public class DatabaseImpl_V2 implements DatabaseV2{

    @Override
    public DatabaseResponse getEventActions() throws Exception {
        String nameStore = "{ CALL USP_USER_ACTIVITY_EVENT_LIST()}";

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                EventAction.class,
                nameStore,
                null,
                null,
                "Get List Event Action");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
       
        return response;
    }

    @Override
    public DatabaseResponse getGenerationType() throws Exception {
        String nameStore = "{ CALL USP_GENERATION_TYPE_LIST()}";

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                GenerationType.class,
                nameStore,
                null,
                null,
                "Get List Genration Type");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
       
        return response;
    }

    @Override
    public DatabaseResponse getWorkflowType() throws Exception {
        String nameStore = "{ CALL USP_WORKFLOW_TYPE_LIST()}";

        DatabaseResponse response = CreateConnection.executeStoreProcedure(
                WorkflowType.class,
                nameStore,
                null,
                null,
                "Get List Workflow Type");

        LogHandler.debug(this.getClass(), response.getDebugString());

        if (response.getStatus() != PaperlessConstant.CODE_SUCCESS && response.getRows() != null) {
            return response;
        }
       
        return response;
    }
    
}
