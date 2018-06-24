/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import quantum.mutex.domain.Group;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.service.GroupService;


/**
 *
 * @author Florent
 */
@Named(value = "groupBacking")
@ViewScoped
public class GroupBacking extends BaseBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(GroupBacking.class.getName());
    
    @Inject GroupDAO groupDAO;
    @Inject GroupService groupService;
    
    private Group selectedGroup;
        
    private final List<Group> groups = new ArrayList<>();
    
    
    
    @PostConstruct
    public void init(){
        initGroups();
    }
    
    private void initGroups(){
        selectedGroup = null;
        groups.clear();
        groups.addAll(getTenantGroups());
    }
    
    private List<Group> getTenantGroups(){
       return getUserTenant().map(groupDAO::findByTenant).orElseGet(() -> new ArrayList<>());
       
    }
    
    public void openAddGroupDialog(){
        LOG.log(Level.INFO, "OPEN  ADD GROUP DLG...");
        Map<String,Object> options = getDialogOptions(45, 40,true);
        PrimeFaces.current().dialog()
                .openDynamic("edit-group-dlg", options, null);
    }
    
    public void openUpdateGroupDialog(Group group){
        LOG.log(Level.INFO, "OPEN UPDATE GROUP: {0}",group.getName());
        Map<String,Object> options = getDialogOptions(45, 40,true);
        PrimeFaces.current().dialog()
                .openDynamic("edit-group-dlg", options, 
                        getDialogParams(ViewParamKey.GROUP_UUID,
                                group.getUuid().toString()));
    }
    
    public void deleteGroup(){  
        
        LOG.log(Level.INFO, "-->> SELECTED DELETE GROUP: {0}",selectedGroup);
        groupService.delete(selectedGroup);
       
    }
    
    public void handleAddGroupReturn(SelectEvent event){
        initGroups();
        selectedGroup = (Group)event.getObject();
        
    }
    
    public void handleDialogClose(CloseEvent closeEvent){
        initGroups();
    }
    
    public List<Group> getGroups() {
        return groups;
    }

    public Group getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(Group selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public GroupDAO getGroupDAO() {
        return groupDAO;
    }

    public UserGroupDAO getUserGroupDAO() {
        return userGroupDAO;
    }
    
    
}
