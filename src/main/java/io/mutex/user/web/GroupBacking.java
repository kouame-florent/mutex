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
    
//    @Inject private GroupDAO groupDAO;
    @Inject private GroupService groupService;
    @Inject private UserGroupService userGroupService;
//    @Inject private UserGroupDAO userGroupDAO;
//    @Inject private UserDAO userDAO;
        
//    private Group selectedGroup;
//    private final ContextIdParamKey currentViewParamKey = ContextIdParamKey.GROUP_UUID;
        
//    private List<Group> groups = new ArrayList<>();
     
    
    @Override
    @PostConstruct
    protected void postConstruct() {
        initCtxParamKey(ContextIdParamKey.GROUP_UUID);
        initGroups();
    }
       
//    @PostConstruct
//    public void init(){
//        initGroups();
//    }
        
    @Override
    protected String editViewId() {
         return ViewID.EDIT_GROUP_DIALOG.id();
    }
    
    private void initGroups(){
       initContextEntities(this::finByTenant);
      //  groups = getUserTenant()
      //          .map(groupDAO::findByTenant).orElseGet(()-> Collections.EMPTY_LIST);
    }
    
    private List<Group> finByTenant(){
        return getUserTenant().map(groupService::findByTenant)
                .orElseGet(() -> Collections.EMPTY_LIST);
    }
    
//    public void openAddGroupDialog(){
//        LOG.log(Level.INFO, "OPEN  ADD GROUP DLG...");
//        Map<String,Object> options = getDialogOptions(55, 50,true);
//        PrimeFaces.current().dialog()
//                .openDynamic(ViewID.EDIT_GROUP_DIALOG.id(), options, null);
//    }
    
//    public void openUpdateGroupDialog(Group group){
//        LOG.log(Level.INFO, "OPEN UPDATE GROUP: {0}",group.getName());
//        Map<String,Object> options = getDialogOptions(55, 50,true);
//        PrimeFaces.current().dialog()
//                .openDynamic(ViewID.EDIT_GROUP_DIALOG.id(), options, 
//                        getDialogParams(ViewParamKey.GROUP_UUID,
//                                group.getUuid()));
//    }
//    
    public void provideSelectedGroup(Group group){
        selectedEntity = group;
    }
    
    
//    @Override
    public void delete(){  
//         disableUsers(selectedGroup);
//         deleteUsersGroups(selectedGroup);
//         deleteGroup(selectedGroup);
        groupService.delete(selectedEntity);
    }
    
//    private List<User> findUsersInGroup(Group group){
//        return userGroupService.findByGroup(group)
//                .stream().map(UserGroup::getUser)
//                .collect(toList());
//                   
////                    .map(List::stream).orElseGet(() -> Stream.empty())
////                    .map(UserGroup::getUser).collect(Collectors.toList());
//    }
//    
//    private boolean isInGroup(User user){
//        return  userGroupService.countAssociations(user) == 1;
//     }
    
//    private User provideDisabled(User user){
//        user.setStatus(UserStatus.DISABLED);
//        return user;
//    }
    
//    private void disableUsers(Group group){
////        findUsersInGroup(group).stream().filter(this::isInGroup)
////                .map(this::provideDisabled).forEach(userDAO::makePersistent);
//    }
    
//    private void deleteGroup(Group group){
////        Optional.ofNullable(group).ifPresent(groupDAO::makeTransient);
//    }
    
//    private void deleteUsersGroups(Group group){
////        Optional.ofNullable(group).map(userGroupDAO::findByGroup)
////                .map(List::stream).orElseGet(() -> Stream.empty())
////                .forEach(userGroupDAO::makeTransient);
//    }
    
       
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

    @Override
    protected String deleteViewId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
}
