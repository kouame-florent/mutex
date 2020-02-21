/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.config;

import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;



/**
 *
 * @author root
 */
public class GlobalConfig {
    
//    @Inject
//    @ConfigProperty(name = "tika.server.ip")
//    String tikaServerIp;
//    
//    @Inject
//    @ConfigProperty(name = "mariadb.server.ip")
//    String mariadbServerIp;
    
    public static String APPLICATION_HOME_DIR = System.getProperty("user.home") + "/" +  ".mutex";
    public static String APPLICATION_SPOOL_DIR = System.getProperty("user.home") + "/"
            +  ".mutex" + "/spool";
    public static String APPLICATION_STORE_DIR = System.getProperty("user.home") + "/"
            +  ".mutex" + "/store";
    public static String APPLICATION_INDEXES_DIR = System.getProperty("user.home") + "/"
            +  ".mutex" + "/indexes";

//    public String getTikaServerIp() {
//        return tikaServerIp;
//    }
//
//    public String getMariadbServerIp() {
//        return mariadbServerIp;
//    }
//    
    
}
