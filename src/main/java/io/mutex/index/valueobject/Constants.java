/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.valueobject;



/**
 *
 * @author Florent
 */

public class Constants {
    
//    public static String APPLICATION_HOME_DIR = System.getProperty("user.home") + "/" +  ".mutex";
//    public static String APPLICATION_SPOOL_DIR = System.getProperty("user.home") + "/"
//            +  ".mutex" + "/spool";
//    public static String APPLICATION_STORE_DIR = System.getProperty("user.home") + "/"
//            +  ".mutex" + "/store";
//    public static String APPLICATION_INDEXES_DIR = System.getProperty("user.home") + "/"
//            +  ".mutex" + "/indexes";
    public static String STORE_SUB_DIR_NAME_DATE_FORMAT = "yyyy-MM-dd";
    public static String DATE_FORMAT = "yyyyMMddTHHmmss";
    
    public static final String HIGHLIGHT_PRE_TAG = "<em style='font-weight:bolder;font-style:normal'>";
    public static final String HIGHLIGHT_POST_TAG = "</em>";
    public static final int HIGHLIGHT_NUMBER_OF_FRAGMENTS = 5;
    public static final int HIGHLIGHT_FRAGMENT_SIZE = 100;
    
    public static String ANONYMOUS_USER_PRINCIPAL_NAME = "anonymous";
    public static String ANONYMOUS_TENANT_NAME = "Mutex";
    public static String ROOT_DEFAULT_LOGIN = "root@mutex.com";
    
    public static int VIRTUAL_PAGE_CHARS_COUNT = 50000; 
    public static int VIRTUAL_PAGE_LINES_COUNT = 50;
    public static int SEARCH_Optional_THRESHOLD = 30;
    public static int MAX_FRAGMENT_PER_FILE = 2; 
    public static int AUTOCOMPLETE_TOKEN_MAX_SIZE = 3;
    public static int CONTENT_PARTITION_SIZE = 5000;
    
    public static int QUERY_MATCH_PHRASE_SLOP = 1;
    public static int TOP_HITS_PER_FILE = 2;
    public static String META_DEFAULT_SEARCH_TEXT = "file_name";
    
}
