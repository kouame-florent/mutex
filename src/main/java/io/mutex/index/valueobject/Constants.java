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
    public static final int HIGHLIGHT_FRAGMENT_SIZE = 200;
    
    public static String ANONYMOUS_USER_PRINCIPAL_NAME = "anonymous";
    public static String ANONYMOUS_SPACE_NAME = "Mutex";
//    public static String ROOT_DEFAULT_LOGIN = "root@mutex.com";
    
    public static String DEFAULT_SPACE = "default";
    public static String DEFAULT_GROUP = "default";
    public static String DEFAULT_TEST_LOGIN = "test@mutex.io";
    public static String DEFAULT_TEST_PASSWD = "test1234";
    
    public static String ADMIN_DEFAULT_SPACE = "mutex";
    public static String ADMIN_DEFAULT_GROUP = "admin";
    public static String ADMIN_DEFAULT_LOGIN = "admin@mutex.io";
    public static String ADMIN_DEFAULT_PASSWD = "admin1234";
    public static String ADMIN_DEFAULT_NAME = "administrator";
    
    public static int VIRTUAL_PAGE_CHARS_COUNT = 50000; 
    public static int VIRTUAL_PAGE_LINES_COUNT = 50;
//    public static int SEARCH_RESULT_THRESHOLD = 50;
//    public static int MAX_FRAGMENT_PER_FILE = 2; 
    public static int AUTOCOMPLETE_TOKEN_MAX_SIZE = 3;
    public static int CONTENT_PARTITION_SIZE = 5000;
    
    public static int QUERY_MATCH_PHRASE_SLOP = 1;
    public static int TOP_HITS_PER_FILE = 2;
    public static int TOP_HITS_AGRREGATE_BUCKETS_NUMBER = 20;
    public static String META_DEFAULT_SEARCH_TEXT = "file_name";
    
    public static int TERM_SUGGESTION_MAX_RESULT_SIZE = 2;
    
}
