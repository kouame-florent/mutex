/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.backing.ViewID;
import quantum.mutex.backing.ViewParamKey;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.user.domain.entity.Group;
import quantum.mutex.user.domain.entity.UserGroup;

/**
 *
 * @author Florent
 */
@Named(value = "groupSetBacking")
@ViewScoped
public class GroupSetBacking extends BaseBacking implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 2369191816205681133L;

	private static final Logger LOG = Logger.getLogger(GroupSetBacking.class.getName());
    
    @Inject private GroupDAO groupDAO;
    @Inject private UserGroupDAO userGroupDAO;
    
    private Group selectedGroup;
    
    private List<Group> groups = new ArrayList<>();
     
    @PostConstruct
    public void init(){
        initGroups();
    }
    
    private void initGroups(){
        List<UserGroup> ugs = getUser()
                .map(u -> userGroupDAO.findByUser(u))
                .orElseGet(() -> Collections.EMPTY_LIST);
        groups = ugs.stream().map(UserGroup::getGroup)
                .collect(Collectors.toList());
     }
    
    public void openUploadDialog(Group group){
        Map<String,Object> options = getDialogOptions(95, 95,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.UPLOAD_DIALOG.id(), options, 
                        getDialogParams(ViewParamKey.GROUP_UUID,
                                group.getUuid().toString()));
    }
    
    public void openFileSetDialog(Group group){
        Map<String,Object> options = getDialogOptions(75, 60,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.UPLOAD_DIALOG.id(), options, 
                        getDialogParams(ViewParamKey.GROUP_UUID,
                                group.getUuid().toString()));
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
    
    
}
