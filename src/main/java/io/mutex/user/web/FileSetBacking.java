/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import io.mutex.index.entity.Inode;
import io.mutex.shared.entity.BaseEntity;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.valueobject.ViewID;
import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author florent
 */
@Named
@ViewScoped
public class FileSetBacking extends QuantumMainBacking<Inode> implements Serializable{

    @Override
    protected void postConstruct() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String editViewId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String deleteViewId() {
        return ViewID.DELETE_INODE_DIALOG.id();
    }

   
   
}
