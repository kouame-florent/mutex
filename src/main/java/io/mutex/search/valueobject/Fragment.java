/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.valueobject;

import java.util.Objects;
import java.util.UUID;


/**
 *
 * @author Florent
 */
public class Fragment {
    

    private final String uuid;
    private final String pageUUID;
    private final String inodeUUID;
    private final String fileName;
    private final String content;
    private final int totalPageCount;
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

	public String getUuid() {
		return uuid;
	}

	public String getPageUUID() {
		return pageUUID;
	}

	public String getInodeUUID() {
		return inodeUUID;
	}

	public String getFileName() {
		return fileName;
	}

	public String getContent() {
		return content;
	}

	public int getTotalPageCount() {
		return totalPageCount;
	}

	public int getPageIndex() {
		return pageIndex;
	}

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.pageUUID);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Fragment other = (Fragment) obj;
        if (!Objects.equals(this.pageUUID, other.pageUUID)) {
            return false;
        }
        return true;
    }
    
    
        
}
