/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import io.mutex.user.entity.Group;
import io.mutex.user.valueobject.ViewID;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.service.GroupService;
import io.mutex.user.service.UserGroupService;


/**
 *
 * @author Florent
 */
@Named(value = "groupBacking")
@ViewScoped
public class GroupBacking extends QuantumBacking<Group> implements Serializable{

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(GroupBacking.class.getName());
    
    @Inject private GroupService groupService;
    @Inject private UserGroupService userGroupService;
    
    @Override
    @PostConstruct
    protected void postConstruct() {
        initCtxParamKey(ContextIdParamKey.GROUP_UUID);
        initGroups();
    }
         
    @Override
    protected String editViewId() {
         return ViewID.EDIT_GROUP_DIALOG.id();
    }
    
    @Override
    protected String deleteViewId() {
        return ViewID.DELETE_GROUP_DIALOG.id();
    }
    
    private void initGroups(){
       initContextEntities(this::finByTenant);
      
    }
    
    private List<Group> finByTenant(){
        return getUserTenant().map(groupService::findByTenant)
                .orElseGet(() -> Collections.EMPTY_LIST);
    }

    public void provideSelectedGroup(Group group){
        selectedEntity = group;
    }
     
    public void handleDeleteReturn(SelectEvent event){
        initGroups();
    }
           
    public void handleEditGroupReturn(SelectEvent event){
        LOG.log(Level.INFO, "---> RETURN FROM HANDLE GROUP ...");
        initGroups();
        selectedEntity = (Group)event.getObject();
    }
    
    public void handleDialogClose(CloseEvent closeEvent){
        initGroups();
    }
    
    public long countGroupMembers( Group group){
        return userGroupService.countGroupMembers(group);
    }

}
