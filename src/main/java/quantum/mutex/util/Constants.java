/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.util;

import javax.enterprise.context.Dependent;

/**
 *
 * @author Florent
 */

public class Constants {
    
    public static String APPLICATION_HOME_DIR = System.getProperty("user.home") + "/" +  ".mutex";
    public static String APPLICATION_SPOOL_DIR = System.getProperty("user.home") + "/"
            +  ".mutex" + "/spool";
    public static String APPLICATION_STORE_DIR = System.getProperty("user.home") + "/"
            +  ".mutex" + "/store";
    public static String APPLICATION_INDEXES_DIR = System.getProperty("user.home") + "/"
            +  ".mutex" + "/indexes";
    public static String STORE_SUB_DIR_NAME_DATE_FORMAT = "yyyy-mm-dd";
    
    public static String ANONYMOUS_USER_PRINCIPAL_NAME = "anonymous";
    
    public static int VIRTUAL_PAGE_CHARS_COUNT = 50000; 
    
    public static int VIRTUAL_PAGE_LINES_COUNT = 50;
    
}
