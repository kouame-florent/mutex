package io.mutex.user.service.impl;

import io.mutex.shared.service.EncryptionService;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.shared.service.StringUtil;
import io.mutex.user.entity.StandardUser;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.exception.UserLoginExistException;
import io.mutex.user.repository.StandardUserDAO;
import io.mutex.user.service.StandardUserService;
import io.mutex.user.service.UserGroupService;
import io.mutex.user.service.UserRoleService;
import io.mutex.user.valueobject.RoleName;
import io.mutex.user.valueobject.UserStatus;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Stateless
public class StandardUserServiceImpl implements StandardUserService {
    
    @Inject EnvironmentUtils envUtils;
    @Inject StandardUserDAO standardUserDAO;
    @Inject EncryptionService encryptionService;
    @Inject UserRoleService userRoleService;
    @Inject UserGroupService userGroupService;
        
    @Override
    public Optional<StandardUser> findByUuid(@NotBlank String uuid){
        return standardUserDAO.findById(uuid);
    }
    
    @Override
    public List<StandardUser> findByTenant(){
        return envUtils.getUserTenant()
                .map(standardUserDAO::findByTenant)
                .orElseGet(() -> Collections.EMPTY_LIST);
    }
    
    @Override
    public Optional<StandardUser> create(@NotNull StandardUser user) throws NotMatchingPasswordAndConfirmation, 
            UserLoginExistException{
        
        if(!arePasswordsMatching(user)){
            throw new NotMatchingPasswordAndConfirmation("Le mot de passe est different de la confirmation");
        }
        
        if(isUserWithLoginExist(user.getLogin())){
            throw  new UserLoginExistException("Ce login existe déjà");
        }
        
        Optional<StandardUser> oUser = Optional.ofNullable(user).map(this::setHashedPassword)
                    .flatMap(this::setTenant)
                    .map(u -> changeStatus(u, UserStatus.DISABLED))
                    .map(this::loginToLowerCase)
                    .flatMap(standardUserDAO::makePersistent);
        
        oUser.ifPresent(this::createUserRole);
        
        return oUser;

    }
    
    private void createUserRole(StandardUser user){
        userRoleService.create(user, RoleName.USER);
    }
    
    @Override
    public Optional<StandardUser> update(@NotNull StandardUser user) throws NotMatchingPasswordAndConfirmation{
        if(!arePasswordsMatching(user)){
            throw new NotMatchingPasswordAndConfirmation("Le mot de passe est different de la confirmation");
        }
        
        Optional<StandardUser> oUser = standardUserDAO
                .findByLogin(StringUtil.lowerCaseWithoutAccent(user.getLogin()));
        
        if((oUser.isPresent() && oUser.filter(a -> a.equals(user)).isEmpty()) ){
            throw new NotMatchingPasswordAndConfirmation("Le mot de passe est different de la confirmation");
        }
        
        return standardUserDAO.makePersistent(loginToLowerCase(user));
        
    }
    
    private Optional<StandardUser> setTenant(@NotNull StandardUser user){
        return envUtils.getUserTenant()
                 .map(t -> {user.setTenant(t); return user;});
    }
    
    private StandardUser loginToLowerCase(@NotNull StandardUser user){
        user.setLogin(StringUtil.lowerCaseWithoutAccent(user.getLogin()));
        return user;
    }
    
    private boolean arePasswordsMatching(@NotNull StandardUser standardUser){
        return standardUser.getPassword()
                .equals(standardUser.getConfirmPassword());
    }
    
     private boolean isUserWithLoginExist(@NotBlank String login){
        return standardUserDAO
                .findByLogin(StringUtil.lowerCaseWithoutAccent(login))
                .isPresent();
    }
     
    private StandardUser setHashedPassword(@NotNull StandardUser user) {
        user.setPassword(EncryptionService.hash(user.getPassword()));
        return user;
    };
    
    private StandardUser changeStatus(@NotNull StandardUser user,UserStatus status) {
        user.setStatus(status);
        return user;
    };
    
    @Override
    public void enable(@NotNull StandardUser user){
      StandardUser usr = changeStatus(user, UserStatus.ENABLED);
      standardUserDAO.makePersistent(usr);
    }
    
    @Override
    public void disable(@NotNull StandardUser user){
        StandardUser usr = changeStatus(user, UserStatus.DISABLED);
        standardUserDAO.makePersistent(usr);
    }
    
    @Override
    public void delete(@NotNull StandardUser user) {
        deleteUsersGroups(user);
        deleteUserRoles(user);
        deleteUser(user);
    }
    
    private void deleteUsersGroups(@NotNull StandardUser user){
        userGroupService.findByUser(user)
                .stream()
                .forEach(userGroupService::remove);
    }
    
    private void deleteUserRoles(@NotNull StandardUser user){
        userRoleService.findByUser(user)
                .stream()
                .forEach(userRoleService::remove);
    }
    
    private void deleteUser(@NotNull StandardUser user){
        standardUserDAO.makeTransient(user);
    }

} 