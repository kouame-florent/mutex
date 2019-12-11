/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.valueobject;

import java.util.UUID;
import lombok.Getter;

/**
 *
 * @author Florent
 */
public class Fragment {
    
    @Getter
    private final String uuid;
    @Getter
    private final String pageUUID;
    @Getter
    private final String inodeUUID;
    @Getter
    private final String fileName;
    @Getter
    private final String content;
    @Getter
    private final int totalPageCount;
    @Getter
    private final int pageIndex;

    public static class Builder{
        private String uuid        = UUID.randomUUID().toString();
        private String pageUUID    = "";
        private String inodeUUID   = "";
        private String fileName    = "";
        private String content     = "";
        private int totalPageCount = 0;
        private int pageIndex      = 0;
        
        public Builder(){}
         
        public Builder uuid(String val){
            uuid = val; return this;
        }
        public Builder pageUUID(String val){
            pageUUID = val; return this;
        }
        public Builder inodeUUID(String val){
            inodeUUID = val; return this;
        }
        public Builder fileName(String val){
            fileName = val; return this;
        }
        public Builder content(String val){
            content = val; return this;
        }
        public Builder totalPageCount(int val){
            totalPageCount = val; return this;
        }
        public Builder pageIndex (int val){
            pageIndex = val; return this;
        }
        
        public Fragment build(){
            return new Fragment(this);
        }
    }
    
    private Fragment(Builder builder) {
        uuid = builder.uuid;
        pageUUID = builder.pageUUID;
        inodeUUID = builder.inodeUUID;
        fileName = builder.fileName;
        content = builder.content;
        totalPageCount = builder.totalPageCount;
        pageIndex = builder.pageIndex;
    }
 
}
