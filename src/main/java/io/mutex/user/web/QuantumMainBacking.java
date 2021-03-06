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
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.primefaces.PrimeFaces;

/**
 *
 * @author root
 * @param <T>
 */
public abstract class QuantumMainBacking<T extends BaseEntity> extends QuantumBaseBacking{

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(QuantumMainBacking.class.getName());
     
    protected List<T> entities = Collections.EMPTY_LIST;
    protected T selectedEntity;
    protected ContextIdParamKey contextIdParamKey;
       
    protected abstract void postConstruct();
     
    protected void initContextEntities(Supplier<List<T>> entityInitializer){
        entities = entityInitializer.get();
    }
    
    protected void initCtxParamKey(ContextIdParamKey ctxId){
        this.contextIdParamKey = ctxId;
    }
       
    public void openAddView(int widthPercent,int heightPercent,boolean closable){
        Map<String,Object> options = getDialogOptions(widthPercent, heightPercent, closable);
        PrimeFaces.current().dialog().openDynamic(editViewId(), options, null);
    }
            
    public void openEditView(@NotNull(message = "Entity cannot be null") T entity,
            int widthPercent,int heightPercent,boolean closable){
       
        Map<String,Object> options = getDialogOptions(widthPercent, heightPercent, closable);
        
        PrimeFaces.current().dialog()
                .openDynamic(editViewId(), 
                        options,dialogParams(Map.of(contextIdParamKey, List.of(entity.getUuid())))); 

    }
    
    public void openDeleteView(T entity){
        Map<String,Object> options = getDialogOptions(40, 30, true);
        PrimeFaces.current().dialog().openDynamic(deleteViewId(), options, 
                dialogParams(Map.of(contextIdParamKey, List.of(entity.getUuid()))));
    }
   
    
    public Map<String,List<String>> dialogParams(Map<ContextIdParamKey,List<String>> params){
       return params.entrySet().stream()
               .collect(Collectors.toMap(e -> e.getKey().param(),e -> e.getValue()));
    }
    
    abstract  protected String editViewId();
    abstract protected String deleteViewId();


    public T getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(T selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    public List<T> getEntities() {
        return entities;
    }

}
