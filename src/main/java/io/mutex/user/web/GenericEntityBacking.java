/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;


import java.util.List;
import javax.annotation.PostConstruct;
import org.primefaces.event.SelectEvent;
import io.mutex.shared.entity.BaseEntity;  

/**
 *
 * @author Florent
 * @param <T>
 */
public abstract class GenericEntityBacking<T extends BaseEntity> extends BaseBacking{
    
    private List<T> entities;
     
    private T selectedEntity;
     
    private void initEntities(){
        selectedEntity = null;
        entities = retrieveEntities();
    }
     
    @PostConstruct
    public void init(){
        initEntities();
    }
     
    public abstract List<T> retrieveEntities();
    public abstract void openAddEntityDialog();
    public abstract void openUpdateEntityDialog(T entity);
   
    public void handleAddUserReturn(SelectEvent event){
        initEntities();
        selectedEntity = (T)event.getObject();
    }

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
