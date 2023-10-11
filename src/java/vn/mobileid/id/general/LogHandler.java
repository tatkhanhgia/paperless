/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.database.Database;
import vn.mobileid.id.general.database.DatabaseImpl;
import vn.mobileid.id.general.objects.DatabaseResponse;
import vn.mobileid.id.paperless.PaperlessConstant;
import vn.mobileid.id.paperless.objects.LogAPI;

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
    private static boolean showRequestLog;

    private static boolean configCaching;

    private static LogHandler instance;

    private static void readConfig() {
        showDebugLog = Configuration.getInstance().isShowDebugLog();
        showInfoLog = Configuration.getInstance().isShowInfoLog();
        showWarnLog = Configuration.getInstance().isShowWarnLog();
        showErrorLog = Configuration.getInstance().isShowErrorLog();
        showRequestLog = Configuration.getInstance().isShowRequestLog();
    }

    public static boolean isShowRequestLog() {
        readConfig();
        return showRequestLog;
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
     *
     * @param object defines the class
     * @param message
     */
    public static void request(Class object, String message) {
        if (isShowRequestLog()) {
            Logger LOG = LogManager.getLogger(object);
            LOG.log(Level.forName("REQUEST", 350), message);
        }
    }

    /**
     * Using for log the debug into file
     *
     * @param object
     * @param message
     */
    public static void debug(Class object, String message) {
        if (isShowDebugLog()) {
            Logger LOG = LogManager.getLogger(object);
            LOG.debug(message);
        }
    }

    /**
     * Using for log the debug into file
     *
     * @param object
     * @param message
     */
    public static void debug(Class object, String transactionID, String message) {
        if (isShowDebugLog()) {
            Logger LOG = LogManager.getLogger(object);
            LOG.debug("TransactionID:" + transactionID + "\n" + message);
        }
    }

    /**
     * Using for log the error into file
     *
     * @param object
     * @param message
     */
    public static void error(
            Class object,
            String message,
            Exception ex) {
        if (isShowErrorLog()) {
            Logger LOG = LogManager.getLogger(object);
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] trace = null;
            Throwable[] throwAble = ex.getSuppressed();
            sb.append(ex.getClass().getName());
            sb.append(": ");
            sb.append(ex.getLocalizedMessage());
            sb.append("_");sb.append(message);
            if (ex.getCause() != null) {
                sb.append("\n");
                sb.append("Cause by:").append(ex.getCause().getMessage());
                trace = ex.getCause().getStackTrace();
            } else {
                trace = ex.getStackTrace();
            }
            sb.append("\n\t");
            List<String> temp = new ArrayList<>();
            for (int i = trace.length - 1; i >= 0; i--) {
//                if (trace[i].getClassName().equals(ManagementController.class.getCanonicalName())) {
//                    for (int j = i; j >= 0; j--) {
//                        temp.add(trace[j].getClassName() + " at(" + trace[j].getMethodName() + ":" + trace[j].getLineNumber() + ")");
//                    }
//                    break;
//                }
                temp.add(trace[i].getClassName() + " at(" + trace[i].getMethodName() + ":" + trace[i].getLineNumber() + ")");
            }
            for (int i = (temp.size() - 1); i >= 0; i--) {
                sb.append(String.format("%5s", temp.get(i)));
                sb.append("\n\t");
            }
            LOG.error(sb.toString());
            System.out.println(sb.toString()); 
        }
    }
    
    /**
     * Using for log the error into file
     *
     * @param object
     * @param transaction
     * @param message
     * @param ex
     */
    public static void error(
            Class object,
            String transaction,
            String message,
            Throwable ex) {
        if (isShowErrorLog()) {
            ex.printStackTrace();
            Logger LOG = LogManager.getLogger(object);
            String messageFinal = "TransactionID:"+transaction +"\n";
            messageFinal += message +"\n";
            messageFinal += ex.toString();           
            Throwable a = ExceptionUtils.getRootCause(ex);            
            messageFinal += "\n\t" + a.getCause().getMessage();
            for (StackTraceElement stackTraceElement : a.getStackTrace()) {
                messageFinal = messageFinal + System.lineSeparator() + "\t" + stackTraceElement.toString();
            }
            LOG.error(messageFinal);
        }
    }

//    /**
//     * Using for log the error into file
//     *
//     * @param object
//     * @param message
//     */
//    public static void error(
//            Class object,
//            String message) {
//        if (isShowErrorLog()) {            
//            Logger LOG = LogManager.getLogger(object);
//            LOG.error(message);
//        }
//    }

    /**
     * Using for log the error into file
     *
     * @param object
     * @param message
     */
    public static void error(
            Class object,
            String transaction,
            String message) {
        if (isShowErrorLog()) {            
            Logger LOG = LogManager.getLogger(object);
            String temp = transaction
                    + "\n\t" + message;
            LOG.error(message);
        }
    }

    /**
     * Using for log the error into file
     *
     * @param object
     * @param message
     */
    public static void error(
            Class object,
            String transactionID,
            String message,
            Exception ex) {
        if (isShowErrorLog()) {
            ex.printStackTrace();
            Logger LOG = LogManager.getLogger(object);
            message += ex.toString();
            Throwable a = ExceptionUtils.getRootCause(ex);
            for (StackTraceElement stackTraceElement : a.getStackTrace()) {
                message = message + System.lineSeparator() + "\t" + stackTraceElement.toString();
            }
            LOG.error("TransactionID:" + transactionID + "\n" + message);
        }
    }

    /**
     * Using for log the info into file
     *
     * @param object
     * @param message
     */
    public static void info(Class object, String message) {
        if (isShowInfoLog()) {
            Logger LOG = LogManager.getLogger(object);
            LOG.info(message);
        }
    }

    /**
     * Using for log the info into file
     *
     * @param object
     * @param message
     */
    public static void info(
            Class object,
            String transactionID,
            String message) {
//        if (isShowInfoLog()) {
            Logger LOG = LogManager.getLogger(object);
            LOG.info("TransactionID:" + transactionID + "\n" + message);
//        }
    }
    
    public static void fatal(
            Class object,
            String transactionID,
            String message
    ){
        if(isShowFatalLog()){
            Logger LOG = LogManager.getLogger(object);
            StringBuilder sb = new StringBuilder();
            sb.append("Fatal Error:").append(message);
            sb.append("\n\t");
            sb.append("TransactionID:").append(transactionID);
            LOG.fatal(sb.toString());
            System.out.println(sb.toString()); 
        }
    }
    
    public static void logIntoDB(
            LogAPI log,
            String transaction_id
    ){
        try{
            Database callDB = new DatabaseImpl();
            DatabaseResponse res = callDB.logIntoDB(
                    log.getEmail(),
                    log.getEnterprise_id(),
                    log.getWorkflow_activity_id(),
                    log.getApp_name(),
                    log.getApi_key(),
                    log.getVersion(),
                    log.getService_name(),
                    log.getUrl(),
                    log.getHttp_verb(),
                    log.getStatus_code(),
                    log.getRequest(),
                    log.getResponse(),
                    log.getHmac(),
                    log.getCreated_by(),
                    transaction_id);
            if(res.getStatus() != PaperlessConstant.CODE_SUCCESS){
//                LogHandler.fatal(
//                        LogHandler.class,
//                        "Cannot append Log API!!!",
//                        transaction_id);
            }
        } catch (Exception ex){
            
        }
    }
}
