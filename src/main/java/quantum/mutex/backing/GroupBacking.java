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
import org.primefaces.event.SelectEvent;
import quantum.mutex.domain.Group;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.UserGroupDAO;


/**
 *
 * @author Florent
 */
@Named(value = "groupBacking")
@ViewScoped
public class GroupBacking extends BaseBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(GroupBacking.class.getName());
    
    @Inject GroupDAO groupDAO;
    
    
    private Group selectedGroup;
        
    private final List<Group> groups = new ArrayList<>();
    
    @PostConstruct
    public void init(){
        initGroups();
    }
    
    private void initGroups(){
        groups.clear();
        groups.addAll(getTenantGroups());
    }
    
    private List<Group> getTenantGroups(){
       return getUserTenant().map(groupDAO::findByTenant).orElseGet(() -> new ArrayList<>());
       
    }
    
    public void openAddGroupDialog(){
        LOG.log(Level.INFO, "OPEN  ADD GROUP DLG...");
        Map<String,Object> options = getDialogOptions(45, 40);
        PrimeFaces.current().dialog()
                .openDynamic("add-group-dlg", options, null);
                
    }
    
    public void handleAddGroupRerurn(SelectEvent event){
        initGroups();
        selectedGroup = (Group)event.getObject();
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
