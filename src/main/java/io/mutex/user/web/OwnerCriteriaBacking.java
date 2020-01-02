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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import io.mutex.user.repository.GroupDAO;
import io.mutex.user.repository.UserDAO;
import io.mutex.search.valueobject.OwnerCreterion;
import io.mutex.user.entity.Group;
import io.mutex.user.entity.User;
import io.mutex.shared.service.EnvironmentUtils;


/**
 *
 * @author Florent
 */
@Named("ownerCriteriaBacking")
@ViewScoped
public class OwnerCriteriaBacking implements Serializable{

	private static final long serialVersionUID = 1L;


	private static final Logger LOG = Logger.getLogger(OwnerCriteriaBacking.class.getName());
     
    
    @Inject  private GroupDAO groupDAO;
    @Inject  private UserDAO userDAO;
    @Inject EnvironmentUtils envUtils;
     
    private List<User> owners = new ArrayList<>();
    private List<User> selectedOwners = new ArrayList<>();
    
    @PostConstruct
    public void init(){
        List<User> users = envUtils.getUser().map(this::getUserGroups)
                .map(this::getUsersFromGroups)
                .orElseGet(() -> Collections.EMPTY_LIST);
        owners = users.stream().distinct().collect(Collectors.toList());
    }
    
    public void validate(){
        selectedOwners.stream()
                .forEach(o -> LOG.log(Level.INFO, "--> SELECTED USER: {0}", o.getLogin()));
        Optional<OwnerCreterion> oc = OwnerCreterion.of(selectedOwners.stream()
                .map(User::getLogin).collect(Collectors.toList()));
        returnToCaller(oc);
    }
    
    private void returnToCaller(Optional<OwnerCreterion> ownerCreteria){
        PrimeFaces.current().dialog().closeDynamic(ownerCreteria);
    }
    
    private List<Group> getUserGroups(User user){
        return groupDAO.findByUser(user);
    }
    
    private List<User> getUsersFromGroups(List<Group> groups){
        return groups.stream().map(userDAO::findAllUser)
                .flatMap(List::stream).collect(Collectors.toList());
    }
}
