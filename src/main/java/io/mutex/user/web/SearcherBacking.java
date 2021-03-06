/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import io.mutex.user.valueobject.GroupType;
import io.mutex.user.entity.Searcher;
import io.mutex.user.entity.User;
import io.mutex.user.service.UserGroupService;
import io.mutex.user.service.UserRoleService;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.user.valueobject.ViewID;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.service.SearcherService;


/**
 *
 * @author Florent
 */
@Named(value = "searcherBacking")
@ViewScoped
public class SearcherBacking extends QuantumMainBacking<Searcher> implements Serializable{

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(SearcherBacking.class.getName());
    
    @Inject SearcherService searcherService;
    @Inject UserRoleService userRoleService;
    @Inject UserGroupService userGroupService;
  
    @Override
    @PostConstruct
    public void postConstruct(){
        initCtxParamKey(ContextIdParamKey.USER_UUID);
        initUsers();
    }
    
    private void initUsers(){
        initContextEntities(searcherService::findAllOrderByName);
    }
 
    @Override
    protected String editViewId() {
        return ViewID.EDIT_USER_DIALOG.id();
    }
    
    @Override
    protected String deleteViewId() {
        return ViewID.DELETE_USER_DIALOG.id();
    }

    public void openDeleteView(int widthPercent,int heightPercent,boolean closable){
        Map<String,Object> options = getDialogOptions(widthPercent, heightPercent, closable);
        PrimeFaces.current().dialog().openDynamic("delete-user-dlg", options, null);
    }
  
    public void openEditUserGroupDialog( User user){
        Map<String,Object> options = getDialogOptions(65, 60,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.EDIT_USER_GROUP_DIALOG.id(), options, 
                        getDialogParams(ContextIdParamKey.USER_UUID,
                                user.getUuid()));
    }  
 
//    public String getUserMainGroup(Searcher user){
//        return userGroupService.findUserPrimaryGroup(user)
//                 .map(ug -> ug.getGroup().getName()).orElseGet(() -> "");
//    }
//    
//    public int getSecondaryGroupCount(Searcher user){
//        return userGroupService.findByUserAndGroupType(user, GroupType.SECONDARY).size();
//    }
//    
//    public List<String> getSecondaryGroupNames(Searcher user){
//        return userGroupService.findByUserAndGroupType(user, GroupType.SECONDARY)
//                .stream().map(ug -> ug.getGroup().getName())
//                .collect(Collectors.toList());
//    }

    public void handleAddUserReturn(SelectEvent event){
        LOG.log(Level.INFO, "--> HANDLE USER RET: {0}", event);
        initUsers();
        selectedEntity = (Searcher)event.getObject();
        userRoleService.cleanOrphansUserRole();
    }
    
    public void handleDeleteReturn(SelectEvent event){
        initUsers();
    }
    
    public void enable(Searcher user){
        searcherService.enable(user);
        initUsers();
    }
     
    public void disable(Searcher user){
        searcherService.disable(user);
        initUsers();
    }

    public boolean showEnableLink( User user){
        return (userGroupService.getAssociations(user) > 0) 
                && (user.getStatus().equals(UserStatus.DISABLED));
    }
    
    public boolean showDisableLink( User user){
        return (userGroupService.getAssociations(user) > 0) 
                && (user.getStatus().equals(UserStatus.ENABLED));
    }
   
    public void handleDialogClose(CloseEvent closeEvent){
        initUsers();
    }
 
}
