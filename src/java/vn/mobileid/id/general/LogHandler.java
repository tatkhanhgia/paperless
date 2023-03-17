/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general;

import java.io.IOException;
import org.apache.logging.log4j.Level;
import vn.mobileid.id.utils.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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


    private static LogHandler instance;

    private static void readConfig() {
        showDebugLog = Configuration.getInstance().isShowDebugLog();
        showInfoLog = Configuration.getInstance().isShowInfoLog();
        showWarnLog = Configuration.getInstance().isShowWarnLog();
        showErrorLog = Configuration.getInstance().isShowErrorLog();
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

    /**
     * Using for log the request/response into file
     * @param object defines the class
     * @param message 
     */
    public static void request(Class object, String message){
        Logger LOG = LogManager.getLogger(object);
        LOG.log(Level.forName("REQUEST", 350), message);
    }
    
    /**
     * Using for log the debug into file
     * @param object
     * @param message 
     */
    public static void debug(Class object, String message){
        Logger LOG = LogManager.getLogger(object);
        LOG.debug(message);
    }
    
        /**
     * Using for log the debug into file
     * @param object
     * @param message 
     */
    public static void debug(Class object,String transactionID ,String message){
        Logger LOG = LogManager.getLogger(object);
        LOG.debug("TransactionID:"+transactionID+"\n"+message);
    }
    
    /**
     * Using for log the error into file
     * @param object
     * @param message 
     */
    public static void error(Class object, String message){
        Logger LOG = LogManager.getLogger(object);
        LOG.error(message);
    }
    
        /**
     * Using for log the error into file
     * @param object
     * @param message 
     */
    public static void error(Class object, String transactionID, String message){
        Logger LOG = LogManager.getLogger(object);
        LOG.error("TransactionID:"+transactionID + "\n"+message);
    }
    
    /**
     * Using for log the info into file
     * @param object
     * @param message 
     */
    public static void info(Class object, String message){
        Logger LOG = LogManager.getLogger(object);
        LOG.info(message);
    }
    
        /**
     * Using for log the info into file
     * @param object
     * @param message 
     */
    public static void info(Class object, String transactionID,String message){
        Logger LOG = LogManager.getLogger(object);
        LOG.info("TransactionID:"+transactionID+"\n"+message);
    }
}
