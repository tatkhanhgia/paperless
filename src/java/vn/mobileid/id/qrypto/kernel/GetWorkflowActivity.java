/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.qrypto.kernel;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.qrypto.objects.WorkflowActivity;

/**
 *
 * @author GiaTK
 */
public class GetWorkflowActivity {
    final private static Logger LOG = LogManager.getLogger(GetWorkflowActivity.class);

    public static List<WorkflowActivity> getListWorkflowActivity(){
        List<WorkflowActivity> list = new ArrayList<>();
        if(!Resources.getListWorkflowActivity().isEmpty() ||                
                Resources.getListWorkflowActivity().size() > 0)  {
            Resources.getListWorkflowActivity().forEach(
                    (key, value) -> {
                    list.add(value);
                });
            return list;
        }
        Resources.reloadListWorkflowActivity();
        Resources.getListWorkflowActivity().forEach(
                    (key, value) -> {
                    list.add(value);
                });
            return list;
    }
    
    public static void main(String[] args){
        List<WorkflowActivity> list = GetWorkflowActivity.getListWorkflowActivity();
        for(WorkflowActivity temp : list){
            System.out.println("A:"+temp.getId());
        }
    }
}
