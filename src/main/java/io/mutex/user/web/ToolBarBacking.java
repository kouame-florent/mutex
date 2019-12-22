/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import io.mutex.user.repository.GroupDAO;
import io.mutex.user.repository.UserGroupDAO;
import io.mutex.user.entity.Group;
import io.mutex.user.entity.UserGroup;

/**
 *
 * @author Florent
 */
@Named(value = "toolBarBacking")
@ViewScoped
public class ToolBarBacking extends BaseBacking implements Serializable{
   
	private static final long serialVersionUID = 1L;
	@Inject private GroupDAO groupDAO;
    @Inject private UserGroupDAO userGroupDAO;
    

    private List<Group> groups = new ArrayList<>();
    private List<Group> selectedGroups = new ArrayList<>();
      
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

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Group> getSelectedGroups() {
		return selectedGroups;
	}

	public void setSelectedGroups(List<Group> selectedGroups) {
		this.selectedGroups = selectedGroups;
	}
    
    
}
