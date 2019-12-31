/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import io.mutex.shared.entity.BaseEntity;
import io.mutex.user.valueobject.ViewState;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.PrimeFaces;

/**
 *
 * @author root
 * @param <T>
 */
public abstract class QuantumEditBacking<T extends BaseEntity> extends BaseBacking{
    
    
	private static final long serialVersionUID = 1L;
	
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
    
    public abstract void edit();
    
    protected void returnToCaller(T entity){
        PrimeFaces.current().dialog().closeDynamic(entity);
    }

    public String getEntityUUID() {
        return entityUUID;
    }

    public void setEntityUUID(String entityUUID) {
        this.entityUUID = entityUUID;
    }
    
    
    
}
