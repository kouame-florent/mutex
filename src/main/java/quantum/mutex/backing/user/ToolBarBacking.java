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
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.UserGroup;

/**
 *
 * @author Florent
 */
@Named(value = "toolBarBacking")
@ViewScoped
public class ToolBarBacking extends BaseBacking implements Serializable{
    
    @Inject private GroupDAO groupDAO;
    @Inject private UserGroupDAO userGroupDAO;
    
    @Getter @Setter
    private List<Group> groups = new ArrayList<>();
    
    @Getter @Setter
    private List<Group> selectedGroups = new ArrayList<>();
      
    @PostConstruct
    public void init(){
        initGroups();
    }
    
    private void initGroups(){
        List<UserGroup> ugs = getUser()
                .map(u -> userGroupDAO.findByUser(u))
                .getOrElse(() -> Collections.EMPTY_LIST);
        groups = ugs.stream().map(UserGroup::getGroup)
                .collect(Collectors.toList());
     }
    
    
}
