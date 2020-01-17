/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import io.mutex.shared.entity.BaseEntity;
import io.mutex.user.valueobject.ContextIdParamKey;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 *
 * @author root
 * @param <T>
 */
public abstract class QuantumMainWithContext<T extends BaseEntity> extends QuantumBaseBacking{
    
    private static final long serialVersionUID = 1L;

    protected String ctxUUID;
    protected T ctxEntity;
    private ContextIdParamKey contextIdParamKey;
    
    protected abstract T initCtxEntity(String entityUUID);
    protected void initCtxId(ContextIdParamKey ctxIdParamKey){
        this.contextIdParamKey = ctxIdParamKey;
    }
    
    protected List<T> entities = Collections.EMPTY_LIST;
    protected T selectedEntity;
    protected void initMainEntities(Supplier<List<T>> entityInitializer){
        entities = entityInitializer.get();
    } 
    
    protected abstract void viewAction();
    
   

    public ContextIdParamKey getContextIdParamKey() {
        return contextIdParamKey;
    }
    
    
}
