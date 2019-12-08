/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.web.user;

import io.mutex.domain.entity.BaseEntity;
import io.mutex.web.BaseBacking;
import io.mutex.web.ViewParamKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.primefaces.PrimeFaces;

/**
 *
 * @author root
 * @param <T>
 */
public abstract class CoreBacking<T extends BaseEntity> extends BaseBacking{
    
    List<T> entities = Collections.EMPTY_LIST;

    protected List<T> getCoreEntities(Supplier<List<T>> supplier){
      return  supplier.get();
    }
       
    public void openAddCoreEntityDialog(){
        Map<String,Object> options = getDialogOptions(55, 60, true);
        openDialog(viewId(),options,null);
    }
    
    public void openEditCoreEntityDialog(T entity){
        Map<String,Object> options = getDialogOptions(60, 50,true);
        openDialog(viewId(), options, getDialogParams(ViewParamKey.TENANT_UUID,entity.getUuid()));

   }
    
    abstract  protected String viewId();
     
    private void openDialog(String viewId,Map<String,Object> options,Map<String,List<String>> params){
        PrimeFaces.current().dialog().openDynamic(viewId, options, params);
        
    }
    
 
}
