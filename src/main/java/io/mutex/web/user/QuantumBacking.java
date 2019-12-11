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
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.primefaces.PrimeFaces;

/**
 *
 * @author root
 * @param <T>
 */
public abstract class QuantumBacking<T extends BaseEntity> extends BaseBacking{

    private static final Logger LOG = Logger.getLogger(QuantumBacking.class.getName());
     
    List<T> entities = Collections.EMPTY_LIST;
    
    public List<T> initView(Supplier<List<T>> dataInitializer){
        return dataInitializer.get();
    }
       
    public void openAddView(int widthPercent,int heightPercent,boolean closable){
        Map<String,Object> options = getDialogOptions(widthPercent, heightPercent, closable);
        PrimeFaces.current().dialog().openDynamic(viewId(), options, null);
    }
            
    public void openEditView(T entity,int widthPercent,int heightPercent,
            boolean closable,ViewParamKey viewParamKey){
        Map<String,Object> options = getDialogOptions(widthPercent, heightPercent, closable);
        PrimeFaces.current().dialog()
                .openDynamic(viewId(), options,dialogParams(Map.of(viewParamKey, List.of(entity.getUuid()))));

    }
    
    public Map<String,List<String>> dialogParams(Map<ViewParamKey,List<String>> params){
       return params.entrySet().stream()
               .collect(Collectors.toMap(e -> e.getKey().param(),e -> e.getValue()));
    }
    
    abstract public void delete();
    
    abstract  protected String viewId();
    
}
