package io.mutex.user.service;

import io.mutex.shared.service.EncryptionService;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.shared.service.StringUtil;
import io.mutex.user.entity.StandardUser;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.exception.UserLoginExistException;
import io.mutex.user.repository.StandardUserDAO;
import io.mutex.user.valueobject.RoleName;
import io.mutex.user.valueobject.UserStatus;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class StandardUserService {
    
    @Inject EnvironmentUtils envUtils;
    @Inject StandardUserDAO standardUserDAO;
    @Inject EncryptionService encryptionService;
    @Inject UserRoleService userRoleService;
    @Inject UserGroupService userGroupService;
    
    
    public Optional<StandardUser> findByUuid(String uuid){
        return standardUserDAO.findById(uuid);
    }
    
    public List<StandardUser> findByTenant(){
        return envUtils.getUserTenant()
                .map(standardUserDAO::findByTenant)
                .orElseGet(() -> Collections.EMPTY_LIST);
    }
    
    public Optional<StandardUser> createUserAndUserRole(StandardUser user) throws NotMatchingPasswordAndConfirmation, 
            UserLoginExistException{
        
        if(!arePasswordsMatching(user)){
            throw new NotMatchingPasswordAndConfirmation("Le mot de passe est different de la confirmation");
        }
        
        if(isUserWithLoginExist(user.getLogin())){
            throw  new UserLoginExistException("Ce login existe déjà");
        }
        
        Optional<StandardUser> oUser = Optional.ofNullable(user).map(this::setHashedPassword)
                    .flatMap(this::setTenant)
                    .map(u -> setStatus(u, UserStatus.DISABLED))
                    .map(this::loginToLowerCase)
                    .flatMap(standardUserDAO::makePersistent);
        
        oUser.map(u -> userRoleService.create(u, RoleName.USER));
        
        return oUser;

    }
    
    public Optional<StandardUser> updateUser(StandardUser user) throws NotMatchingPasswordAndConfirmation{
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
    
    private Optional<StandardUser> setTenant(StandardUser user){
        return envUtils.getUserTenant()
                 .map(t -> {user.setTenant(t); return user;});
    }
    
    private StandardUser loginToLowerCase(StandardUser user){
        user.setLogin(StringUtil.lowerCaseWithoutAccent(user.getLogin()));
        return user;
    }
    
    private boolean arePasswordsMatching(StandardUser standardUser){
        return standardUser.getPassword()
                .equals(standardUser.getConfirmPassword());
    }
    
     private boolean isUserWithLoginExist(String login){
        return standardUserDAO
                .findByLogin(StringUtil.lowerCaseWithoutAccent(login))
                .isPresent();
    }
     
    private StandardUser setHashedPassword(StandardUser user) {
        user.setPassword(EncryptionService.hash(user.getPassword()));
        return user;
    };
    
    private StandardUser setStatus(StandardUser user,UserStatus status) {
        user.setStatus(status);
        return user;
    };
    
    public void delete(StandardUser user) {
        deleteUsersGroups(user);
        deleteUserRoles(user);
        deleteUser(user);
    }
    
    private void deleteUsersGroups(StandardUser user){
        userGroupService.findByUser(user)
                .stream()
                .forEach(userGroupService::remove);
    }
    
    private void deleteUserRoles(StandardUser user){
        userRoleService.findByUser(user)
                .stream()
                .forEach(userRoleService::remove);
    }
    
    public void deleteUser(StandardUser user){
        standardUserDAO.makeTransient(user);
    }
    
//    private final Consumer<User> deleteUser = (User user) -> {
//         userDAO.makeTransient(user);
//    };
    
//    private final Function<User,Optional<User>> deleteUsersGroups = (User user) -> {
//        Optional.ofNullable(user).map(u -> userGroupDAO.findByUser(u))
//                .map(List::stream).orElseGet(() -> Stream.empty())
//                .forEach(userGroupDAO::makeTransient);
//        return Optional.of(user);
//    };
//    
//    private final Function<User,User> deleteUserRoles = ( User user) -> {
//                userRoleDAO.findByUser(user).stream()
//                    .forEach(userRoleDAO::makeTransient);
//                return user;
//    };
} 