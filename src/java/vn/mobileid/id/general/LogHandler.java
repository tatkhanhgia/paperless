/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general;

import java.io.IOException;
import vn.mobileid.id.utils.Configuration;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.objects.Entity;
import vn.mobileid.id.general.objects.EntityProperties;
import vn.mobileid.id.general.objects.LogController;

/**
 *
 * @author GiaTK
 */
public class LogHandler {    
    private static boolean showDebugLog;
    private static boolean showInfoLog;
    private static boolean showWarnLog;
    private static boolean showErrorLog;
    private static boolean showFatalLog;
    
    private static boolean configCaching;
    
    private static FileHandler handler;
    private static SimpleFormatter formatter;
    
    private static LogHandler instance;       
    
    private static void readConfig() {
        LogController logController = null;
        Entity systemConfig = Resources.getEntities().get(Entity.ENTITY_SYSTEM_CONFIG);
        if (systemConfig != null) {
            EntityProperties entityProperties = systemConfig.getEntityProperties();
            if (entityProperties != null) {
                logController = entityProperties.getLogController();
                showDebugLog = logController.isShowDebugLog() && Configuration.getInstance().isShowDebugLog();
                showInfoLog = logController.isShowInfoLog() && Configuration.getInstance().isShowInfoLog();
                showWarnLog = logController.isShowWarnLog() && Configuration.getInstance().isShowWarnLog();
                showErrorLog = logController.isShowErrorLog() && Configuration.getInstance().isShowErrorLog();
                showFatalLog = logController.isShowFatalLog() && Configuration.getInstance().isShowFatalLog();
                configCaching = entityProperties.isConfigCaching();
            }
        }
    }

    public static boolean isShowDebugLog() {
        readConfig();
        return showDebugLog;
    }

    public static boolean isShowInfoLog() {
        readConfig();
        return showInfoLog;
    }

    public static boolean isShowWarnLog() {
        readConfig();
        return showWarnLog;
    }

    public static boolean isShowErrorLog() {
        readConfig();
        return showErrorLog;
    }

    public static boolean isShowFatalLog() {
        readConfig();
        return showFatalLog;
    }

    public static boolean isConfigCaching() {
        readConfig();
        return configCaching;
    }
}
