/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.policy.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import vn.mobileid.id.general.objects.Attributes;

/**
 *
 * @author GiaTK
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName("attributes")
public class PolicyUserActivityConfiguration  extends Attributes{
    
    private List<TemplateUserActivityConfiguration> attributes;
       
    public PolicyUserActivityConfiguration() {
    }

    @JsonProperty("attributes")
    public List<TemplateUserActivityConfiguration> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<TemplateUserActivityConfiguration> attributes) {
        this.attributes = attributes;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TemplateUserActivityConfiguration {
        private String createChildOfWorkflowActivityCSV;
        private String createWorkflowActivity;

        @JsonProperty("createWorkflowActivity")
        public String getCreateWorkflowActivity() {
            return createWorkflowActivity;
        }

        @JsonProperty("createChildOfCSV")
        public String getCreateChildOfWorkflowActivityCSV() {
            return createChildOfWorkflowActivityCSV;
        }

        public void setCreateChildOfWorkflowActivityCSV(String createChildOfWorkflowActivityCSV) {
            this.createChildOfWorkflowActivityCSV = createChildOfWorkflowActivityCSV;
        }
        
        public void setCreateWorkflowActivity(String createWorkflowActivity) {
            this.createWorkflowActivity = createWorkflowActivity;
        }
    }
    
    public static void main(String[] args) throws JsonProcessingException {
        String json = "{\"attributes\":{\"attributeType\":\"TEMPLATE_USER_ACTIVITY\",\"remarkEn\":\"Template when add new User Activity\",\"remark\":\"Mẫu dành cho các hành động của người dùng\",\"attributes\":[{\"create_workflowactivity\":\"Create new Workflow Activity by @Email\"}]}}";
        PolicyUserActivityConfiguration policy = new ObjectMapper().readValue(json, PolicyUserActivityConfiguration.class);
    }
}
