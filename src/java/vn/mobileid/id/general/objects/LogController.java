/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.objects;

/**
 *
 * @author GiaTK
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogController {

    private boolean showDebugLog;
    private boolean showInfoLog;
    private boolean showWarnLog;
    private boolean showErrorLog;
    private boolean showFatalLog;

    public LogController() {
        showDebugLog = false;
        showInfoLog = true;
        showErrorLog = true;
        showFatalLog = true;
        showWarnLog = true;
    }

    @JsonProperty("showDebugLog")
    public boolean isShowDebugLog() {
        return showDebugLog;
    }

    public void setShowDebugLog(boolean showDebugLog) {
        this.showDebugLog = showDebugLog;
    }

    @JsonProperty("showInfoLog")
    public boolean isShowInfoLog() {
        return showInfoLog;
    }

    public void setShowInfoLog(boolean showInfoLog) {
        this.showInfoLog = showInfoLog;
    }

    @JsonProperty("showWarnLog")
    public boolean isShowWarnLog() {
        return showWarnLog;
    }

    public void setShowWarnLog(boolean showWarnLog) {
        this.showWarnLog = showWarnLog;
    }

    @JsonProperty("showErrorLog")
    public boolean isShowErrorLog() {
        return showErrorLog;
    }

    public void setShowErrorLog(boolean showErrorLog) {
        this.showErrorLog = showErrorLog;
    }

    @JsonProperty("showFatalLog")
    public boolean isShowFatalLog() {
        return showFatalLog;
    }

    public void setShowFatalLog(boolean showFatalLog) {
        this.showFatalLog = showFatalLog;
    }
}

