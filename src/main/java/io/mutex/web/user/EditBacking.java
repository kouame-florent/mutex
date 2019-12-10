/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.web.user;

import io.mutex.domain.entity.BaseEntity;
import io.mutex.web.BaseBacking;
import io.mutex.web.ViewState;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author root
 * @param <T>
 */
public abstract class EditBacking<T extends BaseEntity> extends BaseBacking{
    
    protected ViewState viewState; 
    protected String entityUUID;
    protected T currentEntity;
    
    protected abstract T initEntity(String entityUUID);
    protected abstract void viewAction();
    
    protected ViewState initViewState(String entityUUID){
        if(StringUtils.isBlank(entityUUID)){
            return ViewState.CREATE;
        }
        return ViewState.UPDATE;
    }
    
    public abstract void persistEntity();

    public String getEntityUUID() {
        return entityUUID;
    }

    public void setEntityUUID(String entityUUID) {
        this.entityUUID = entityUUID;
    }
    
    
    
}
