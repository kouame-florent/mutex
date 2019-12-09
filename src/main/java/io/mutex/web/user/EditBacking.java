/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.web.user;

import io.mutex.domain.entity.BaseEntity;
import io.mutex.web.BaseBacking;
import io.mutex.web.ViewState;

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
    
    protected ViewState initViewState(String entityUUID){
        return entityUUID.isBlank() ? ViewState.CREATE : ViewState.UPDATE;
    }
    
    public abstract void persistEntity();

    public String getEntityUUID() {
        return entityUUID;
    }

    public void setEntityUUID(String entityUUID) {
        this.entityUUID = entityUUID;
    }
    
    
    
}
