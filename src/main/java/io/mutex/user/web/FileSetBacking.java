/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import io.mutex.index.entity.Inode;
import io.mutex.index.service.InodeServiceImpl;
import io.mutex.user.entity.Searcher;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.valueobject.ViewID;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author florent
 */
@Named(value = "fileSetBacking")
@ViewScoped
public class FileSetBacking extends QuantumMainBacking<Inode> implements Serializable{
    
    @Inject InodeServiceImpl inodeService;

    @PostConstruct
    @Override
    protected void postConstruct() {
        initCtxParamKey(ContextIdParamKey.INODE_UUID);
        initFiles();
    }

    @Override
    protected String editViewId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String deleteViewId() {
        return ViewID.DELETE_INODE_DIALOG.id();
    }

    private void initFiles() {
        initContextEntities(this::finByOwner);

    }
    
    private List<Inode> finByOwner() {
        return getAuthenticatedUser().map(u -> inodeService.findByOwner((Searcher)u))
                .orElseGet(() -> Collections.EMPTY_LIST);
    }
    
    public void handleDeleteReturn(SelectEvent event) {
        initFiles();
    }
   
}
