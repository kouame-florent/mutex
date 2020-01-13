/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import io.mutex.shared.entity.BaseEntity;
import io.mutex.user.valueobject.ContextIdParamKey;
import org.primefaces.PrimeFaces;

/**
 *
 * @author root
 * @param <T>
 */
public abstract class QuantumDeleteBacking<T extends BaseEntity> extends BaseBacking {
    
   
    protected String entityUUID;
    
    public abstract void delete();
    
      
    public void closeDeleteView(){
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public String getEntityUUID() {
        return entityUUID;
    }

    public void setEntityUUID(String entityUUID) {
        this.entityUUID = entityUUID;
    }
    
    
}
