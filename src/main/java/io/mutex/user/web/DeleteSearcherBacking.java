/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import io.mutex.user.entity.Searcher;
import io.mutex.user.valueobject.ContextIdParamKey;
import java.io.Serializable;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import io.mutex.user.service.SearcherService;

/**
 *
 * @author root
 */
@Named(value = "deleteSearcherBacking")
@ViewScoped
public class DeleteSearcherBacking extends QuantumDeleteBacking<Searcher> implements Serializable{
     
    private static final long serialVersionUID = 1L;
 
    @Inject SearcherService searcherService; 
    
    @PostConstruct
    @Override
    protected void postConstruct() {
        iniCtxtParamKey(ContextIdParamKey.USER_UUID);
    }
         
    @Override
    public void delete(){
        Optional.ofNullable(entityUUID)
                .flatMap(searcherService::findByUuid)
                .ifPresent(searcherService::delete);
        closeDeleteView();
    }

}
