/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.config;

/**
 *
 * @author root
 */
public class GlobalConfig {
    public static String APPLICATION_HOME_DIR = System.getProperty("user.home") + "/" +  ".mutex";
    public static String APPLICATION_SPOOL_DIR = System.getProperty("user.home") + "/"
            +  ".mutex" + "/spool";
    public static String APPLICATION_STORE_DIR = System.getProperty("user.home") + "/"
            +  ".mutex" + "/store";
    public static String APPLICATION_INDEXES_DIR = System.getProperty("user.home") + "/"
            +  ".mutex" + "/indexes";
}
