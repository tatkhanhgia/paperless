/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.email.SMTPProperties;

/**
 *
 * @author GIATK
 */
public class Configuration {

    private static final Logger LOG = LogManager.getLogger(Configuration.class);

    private static Configuration instance;

    private Properties prop = new Properties();
    private Properties keycloakprop = new Properties();
    private Properties qryptoProp = new Properties();
    private Properties appInfo = new Properties();
    private Properties propSMTP = new Properties();

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String dbDriver;

    private String dbReadOnlyUrl;
    private String dbReadOnlyUsername;
    private String dbReadOnlyPassword;
    private String dbReadOnlyDriver;

    private String dbWriteOnlyUrl;
    private String dbWriteOnlyUsername;
    private String dbWriteOnlyPassword;
    private String dbWriteOnlyDriver;

    private int initPoolSize;
    private int minPoolIdle;
    private int maxPoolIdle;
    private int maxConnection = 1;
    private boolean showProcedures;
    private int retry = 1;
    private int appUserDBID = 1;

    private String tmpFmsFolder;

    private byte[] noImage;

    private String neurotechFolder;

    private boolean moduleNeuroTechEnabled;

    private boolean showDebugLog;
    private boolean showInfoLog;
    private boolean showWarnLog;
    private boolean showErrorLog;
    private boolean showFatalLog;
    private boolean showRequestLog;

    private String serverTimeType;
    
    //KeyCloak Server
    private String keycloakURL;
    private String keycloakRealm;    
    private String keycloakClient_secret;
    
    //SMTP
    private boolean ssl_enable;
    private boolean tsl_enable;
    private boolean auth;
    private String host;
    private int port;
    private String username;
    private String password;
    private String sendFromAddr;
    private String sendFromName;
         
    
    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    //Test
    private Configuration() {
        try {
//            getEnvConfig();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            //Load Paperless Services Properties-----------------------------------
//            if (Utils.isNullOrEmpty(System.getenv("DTIS_DB_URL"))) {            
                InputStream appProperties = loader.getResourceAsStream(
                        "resources/config/app.properties");
                if (appProperties != null) {
                    prop.load(appProperties);
                    if (prop.keySet() == null) {
                        String propertiesFile = Utils.getPropertiesFile("resources/config/app.properties");
                        if (propertiesFile != null) {
                            LOG.info("Read the configuation file from " + propertiesFile);
                            InputStream in = new FileInputStream(propertiesFile);
                            prop.load(in);
                            in.close();
                        } else {
                            LOG.error("Cannot find any configuation file. This is a big problem");
                        }
                    }
                    appProperties.close();
                } else {
                    String propertiesFile = Utils.getPropertiesFile("resources/config/app.properties");
                    if (propertiesFile != null) {
                        LOG.info("Read the configuation file from " + propertiesFile);
                        prop.load(new FileInputStream(propertiesFile));
                    } else {
                        LOG.error("Cannot find any configuation file. This is a big problem");
                    }
                }
//            } else {
//                LOG.debug("Using getenv to obtain configuration values");
//            }
            //Load Info Properties----------------------------------------------
            InputStream appInfoProperties = loader.getResourceAsStream("resources/config/info.properties");
            if (appInfoProperties != null) {
                appInfo.load(appInfoProperties);
                if (appInfo.keySet() == null) {
                    String propertiesFile = Utils.getPropertiesFile("resources/config/info.properties");
                    if (propertiesFile != null) {
                        LOG.info("Read the configuation file from " + propertiesFile);
                        InputStream in = new FileInputStream(propertiesFile);
                        appInfo.load(in);
                        in.close();
                    } else {
                        LOG.error("Cannot find any configuation file. This is a big problem");
                    }
                }
                appInfoProperties.close();
            } else {
                String propertiesFile = Utils.getPropertiesFile("resources/config/info.properties");
                if (propertiesFile != null) {
                    LOG.info("Read the configuation file from " + propertiesFile);
                    appInfo.load(new FileInputStream(propertiesFile));
                } else {
                    LOG.error("Cannot find any configuation file. This is a big problem");
                }
            }
            
            // Load Keycloak properties-----------------------------------------
            InputStream keycloakProperties = loader.getResourceAsStream("resources/config/keycloak.properties");
            if (keycloakProperties != null) {
                keycloakprop.load(keycloakProperties);
                if (keycloakprop.keySet() == null) {
                    String propertiesFile = Utils.getPropertiesFile("resources/config/keycloak.properties");
                    if (propertiesFile != null) {
                        LOG.info("Read the configuation file from " + propertiesFile);
                        InputStream in = new FileInputStream(propertiesFile);
                        keycloakprop.load(in);
                        in.close();
                    } else {
                        LOG.error("Cannot find any configuation file. This is a big problem");
                    }
                }
                appInfoProperties.close();
            } else {
                String propertiesFile = Utils.getPropertiesFile("resources/config/keycloak.properties");
                if (propertiesFile != null) {
                    LOG.info("Read the configuation file from " + propertiesFile);
                    keycloakprop.load(new FileInputStream(propertiesFile));
                } else {
                    LOG.error("Cannot find any configuation file. This is a big problem");
                }
            }
            
            //Load SMTP PROPERTIES====================================
            InputStream SMTP = loader.getResourceAsStream("resources/config/smtp.properties");
            if (SMTP != null) {
                propSMTP.load(SMTP);
                if (propSMTP.keySet() == null) {
                    String propertiesFile = Utils.getPropertiesFile("resources/config/smtp.properties");
                    if (propertiesFile != null) {
                        LOG.info("Read the configuation file from " + propertiesFile);
                        InputStream in = new FileInputStream(propertiesFile);
                        propSMTP.load(in);
                        in.close();
                    } else {
                        LOG.error("Cannot find any configuation file. This is a big problem");
                    }
                }
                SMTP.close();
            } else {
                String propertiesFile = Utils.getPropertiesFile("resources/config/smtp.properties");
                if (propertiesFile != null) {
                    LOG.info("Read the configuation file from " + propertiesFile);
                    propSMTP.load(new FileInputStream(propertiesFile));
                } else {
                    LOG.error("Cannot find any configuation file. This is a big problem");
                }
            }
            
            //Load QryptoService=============================================
            InputStream qryptoStream = loader.getResourceAsStream("resources/config/qrypto.properties");
            if (qryptoProp != null) {
                qryptoProp.load(qryptoStream);
                if (qryptoProp.keySet() == null) {
                    String propertiesFile = Utils.getPropertiesFile("resources/config/qrypto.properties");
                    if (propertiesFile != null) {
                        LOG.info("Read the configuation file from " + propertiesFile);
                        InputStream in = new FileInputStream(propertiesFile);
                        qryptoProp.load(in);
                        in.close();
                    } else {
                        LOG.error("Cannot find any configuation file. This is a big problem");
                    }
                }
                qryptoStream.close();
            } else {
                String propertiesFile = Utils.getPropertiesFile("resources/config/qrypto.properties");
                if (propertiesFile != null) {
                    LOG.info("Read the configuation file from " + propertiesFile);
                    qryptoProp.load(new FileInputStream(propertiesFile));
                } else {
                    LOG.error("Cannot find any configuation file. This is a big problem");
                }
            }
            
            
            dbUrl = prop.getProperty("paperless.db.url") == null ? System.getenv("DTIS_DB_URL") : prop.getProperty("paperless.db.url");
            dbUsername = prop.getProperty("paperless.db.username") == null ? System.getenv("DTIS_DB_USERNAME") : prop.getProperty("paperless.db.username");
            dbPassword = prop.getProperty("paperless.db.password") == null ? System.getenv("DTIS_DB_PASSWORD") : prop.getProperty("paperless.db.password");
            dbDriver = prop.getProperty("paperless.db.driver") == null ? System.getenv("DTIS_DB_DRIVER") : prop.getProperty("paperless.db.driver");

            dbReadOnlyUrl = prop.getProperty("paperless.db.readonly.url") == null ? System.getenv("DTIS_DB_READONLY_URL") : prop.getProperty("paperless.db.readonly.url");
            dbReadOnlyUsername = prop.getProperty("paperless.db.readonly.username") == null ? System.getenv("DTIS_DB_READONLY_USERNAME") : prop.getProperty("paperless.db.readonly.username");
            dbReadOnlyPassword = prop.getProperty("paperless.db.readonly.password") == null ? System.getenv("DTIS_DB_READONLY_PASSWORD") : prop.getProperty("paperless.db.readonly.password");
            dbReadOnlyDriver = prop.getProperty("paperless.db.readonly.driver") == null ? System.getenv("DTIS_DB_READONLY_DRIVER") : prop.getProperty("paperless.db.readonly.driver");

            dbWriteOnlyUrl = prop.getProperty("paperless.db.writeonly.url") == null ? System.getenv("DTIS_DB_WRITEONLY_URL") : prop.getProperty("paperless.db.writeonly.url");
            dbWriteOnlyUsername = prop.getProperty("paperless.db.writeonly.username") == null ? System.getenv("DTIS_DB_WRITEONLY_USERNAME") : prop.getProperty("paperless.db.writeonly.username");
            dbWriteOnlyPassword = prop.getProperty("paperless.db.writeonly.password") == null ? System.getenv("DTIS_DB_WRITEONLY_PASSWORD") : prop.getProperty("paperless.db.writeonly.password");
            dbWriteOnlyDriver = prop.getProperty("paperless.db.writeonly.driver") == null ? System.getenv("DTIS_DB_WRITEONLY_DRIVER") : prop.getProperty("paperless.db.writeonly.driver");

            initPoolSize = Integer.parseInt(prop.getProperty("paperless.db.init.connection") == null ? System.getenv("DTIS_DB_INIT_CONNECTION") : prop.getProperty("paperless.db.init.connection"));
            minPoolIdle = Integer.parseInt(prop.getProperty("paperless.db.min.idle.connection") == null ? System.getenv("DTIS_DB_MIN_IDLE_CONNECTION") : prop.getProperty("paperless.db.min.idle.connection"));
            maxPoolIdle = Integer.parseInt(prop.getProperty("paperless.db.max.idle.connection") == null ? System.getenv("DTIS_DB_MAX_IDLE_CONNECTION") : prop.getProperty("paperless.db.max.idle.connection"));
            maxConnection = Integer.parseInt(prop.getProperty("paperless.db.max.connection") == null ? System.getenv("DTIS_DB_MAX_CONNECTION") : prop.getProperty("paperless.db.max.connection"));
            showProcedures = Boolean.parseBoolean(prop.getProperty("paperless.db.logging.procedures.enabled") == null ? System.getenv("DTIS_DB_LOGGING_PROCEDURES_ENABLED") : prop.getProperty("paperless.db.logging.procedures.enabled"));
            retry = Integer.parseInt(prop.getProperty("paperless.db.retry") == null ? System.getenv("DTIS_DB_RETRY") : prop.getProperty("paperless.db.retry"));

            appUserDBID = Integer.parseInt(prop.getProperty("paperless.db.app.userid") == null ? System.getenv("DTIS_DB_APP_USERID") : prop.getProperty("paperless.db.app.userid"));

            tmpFmsFolder = prop.getProperty("paperless.db.app.temp.folder") == null ? System.getenv("DTIS_DB_APP_TEMP_FOLDER") : prop.getProperty("paperless.db.app.temp.folder");
            neurotechFolder = prop.getProperty("paperless.db.app.neurotech.folder") == null ? System.getenv("DTIS_DB_APP_NEUROTECH_FOLDER") : prop.getProperty("paperless.db.app.neurotech.folder");

            moduleNeuroTechEnabled = Boolean.parseBoolean(prop.getProperty("paperless.module.neurotech.enabled"));

            showDebugLog = Boolean.parseBoolean(System.getenv("DTIS_LOG4J_DEBUG") == null ? prop.getProperty("paperless.log4j.debug", "true") : System.getenv("DTIS_LOG4J_DEBUG"));
            showInfoLog = Boolean.parseBoolean(System.getenv("DTIS_LOG4J_INFO") == null ? prop.getProperty("paperless.log4j.info", "true") : System.getenv("DTIS_LOG4J_INFO"));
            showWarnLog = Boolean.parseBoolean(System.getenv("DTIS_LOG4J_WARN") == null ? prop.getProperty("paperless.log4j.warn", "true") : System.getenv("DTIS_LOG4J_WARN"));
            showErrorLog = Boolean.parseBoolean(System.getenv("DTIS_LOG4J_ERROR") == null ? prop.getProperty("paperless.log4j.error", "true") : System.getenv("DTIS_LOG4J_ERROR"));
            showFatalLog = Boolean.parseBoolean(System.getenv("DTIS_LOG4J_FATAL") == null ? prop.getProperty("paperless.log4j.fatal", "true") : System.getenv("DTIS_LOG4J_FATAL"));
            showRequestLog = Boolean.parseBoolean(System.getenv("DTIS_LOG4J_FATAL") == null ? prop.getProperty("paperless.log4j.request", "true") : System.getenv("DTIS_LOG4J_FATAL"));
            
            serverTimeType = prop.getProperty("server.time.type") == null ? System.getenv("SERVER_TIME_TYPE") : prop.getProperty("server.time.type");
            
            keycloakURL = keycloakprop.getProperty("dtis.keycloak.url") == null ? System.getenv("DTIS_KEYCLOAK_URL") : keycloakprop.getProperty("dtis.keycloak.url");
            keycloakRealm = keycloakprop.getProperty("dtis.keycloak.realm") == null ? System.getenv("DTIS_KEYCLOAK_REALM") : keycloakprop.getProperty("dtis.keycloak.realm");
            keycloakClient_secret = keycloakprop.getProperty("dtis.keycloak.clientsecret") == null ? System.getenv("DTIS_KEYCLOAK_CLIENTSECRET") : keycloakprop.getProperty("dtis.keycloak.clientsecret");
            
            ssl_enable = Boolean.parseBoolean(propSMTP.getProperty("mail.smtp.ssl.enable") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.ssl.enable"));
            tsl_enable = Boolean.parseBoolean(propSMTP.getProperty("mail.smtp.starttls.enable") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.starttls.enable"));
            auth = Boolean.parseBoolean(propSMTP.getProperty("mail.smtp.auth") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.auth"));
            host = propSMTP.getProperty("mail.smtp.host") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.host");
            port = Integer.parseInt(propSMTP.getProperty("mail.smtp.port") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.port"));
            username = propSMTP.getProperty("mail.smtp.username") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.username");
            password = propSMTP.getProperty("mail.smtp.password") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.password");
            sendFromAddr = propSMTP.getProperty("mail.smtp.sendfromaddr") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.sendfromaddr");
            sendFromName = propSMTP.getProperty("mail.smtp.sendfromname") == null ? System.getenv("SERVER_TIME_TYPE") : propSMTP.getProperty("mail.smtp.sendfromname");
            SMTPProperties.setProp(propSMTP);
            
            if (serverTimeType == null) {
                serverTimeType = "";
            }           
            
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Error while loading app.properties. Details. " + Utils.printStackTrace(e));
        }
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public String getDbReadOnlyUrl() {
        return dbReadOnlyUrl;
    }

    public String getDbReadOnlyUsername() {
        return dbReadOnlyUsername;
    }

    public String getDbReadOnlyPassword() {
        return dbReadOnlyPassword;
    }

    public String getDbReadOnlyDriver() {
        return dbReadOnlyDriver;
    }

    public String getDbWriteOnlyUrl() {
        return dbWriteOnlyUrl;
    }

    public String getDbWriteOnlyUsername() {
        return dbWriteOnlyUsername;
    }

    public String getDbWriteOnlyPassword() {
        return dbWriteOnlyPassword;
    }

    public String getDbWriteOnlyDriver() {
        return dbWriteOnlyDriver;
    }

    public int getInitPoolSize() {
        return initPoolSize;
    }

    public int getMinPoolIdle() {
        return minPoolIdle;
    }

    public int getMaxPoolIdle() {
        return maxPoolIdle;
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public boolean isShowProcedures() {
        return showProcedures;
    }

    public int getRetry() {
        return retry;
    }

    public int getAppUserDBID() {
        return appUserDBID;
    }

    public String getTmpFmsFolder() {
        return tmpFmsFolder;
    }

    public void setTmpFmsFolder(String tmpFmsFolder) {
        this.tmpFmsFolder = tmpFmsFolder;
    }

    public Properties getAppInfo() {        
        return appInfo;
    }

    public byte[] getNoImage() {
        return noImage;
    }

    public String getNeurotechFolder() {
        return neurotechFolder;
    }

    public boolean isModuleNeuroTechEnabled() {
        return moduleNeuroTechEnabled;
    }

    public boolean isShowDebugLog() {
        return showDebugLog;
    }

    public boolean isShowInfoLog() {
        return showInfoLog;
    }

    public boolean isShowWarnLog() {
        return showWarnLog;
    }

    public boolean isShowErrorLog() {
        return showErrorLog;
    }

    public boolean isShowFatalLog() {
        return showFatalLog;
    }

    private void getEnvConfig() {
        LOG.info("Load System env:");
        System.getenv().forEach((k, v) -> {
            LOG.info("\t" + k + ":" + v);
        });
    }

    public String getServerTimeType() {
        return serverTimeType;
    }

    public String getKeyCloakURL(){
        return keycloakURL;
    }
    
    public String getKeyCloakRealm(){
        return keycloakRealm;
    }

    public String getKeycloakClient_secret() {
        return keycloakClient_secret;
    }

    public String getQryptoHost(){
        return this.qryptoProp.getProperty("qrypto.host");
    }
    
    public String getQryptoAuthentication(){
        return this.qryptoProp.getProperty("qrypto.authorization");
    }

    public boolean isShowRequestLog() {
        return this.showRequestLog ;
    }        
}
