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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.type.criterion.OwnerCreterion;
import quantum.mutex.user.domain.entity.Group;
import quantum.mutex.user.domain.entity.User;
import quantum.mutex.util.EnvironmentUtils;


/**
 *
 * @author Florent
 */
@Named("ownerCriteriaBacking")
@ViewScoped
public class OwnerCriteriaBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(OwnerCriteriaBacking.class.getName());
     
    
    @Inject  private GroupDAO groupDAO;
    @Inject  private UserDAO userDAO;
    @Inject EnvironmentUtils envUtils;
    
    @Getter 
    private List<User> owners = new ArrayList<>();
    @Getter @Setter 
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
