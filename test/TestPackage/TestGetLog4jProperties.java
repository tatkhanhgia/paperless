/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Admin
 */
import org.apache.logging.log4j.core.config.properties.PropertiesConfiguration;
import org.apache.logging.log4j.util.PropertyFilePropertySource;
public class TestGetLog4jProperties {
    static Logger log = LogManager.getLogger("example/test");
    public static void main(String[] args){
//        PropertyFilePropertySource src = new PropertyFilePropertySource("C:\\Users\\Admin\\Documents\\NetBeansProjects\\QryptoServices\\web\\WEB-INF\\log4j2.properties");
        log.error("nulll");
        log.debug("debug");
        log.log(Level.FATAL, "lol");
//        PropertiesConfig a = new P;
    }
}
