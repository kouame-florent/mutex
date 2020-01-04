package io.mutex.user.service;

import io.mutex.shared.service.EncryptionService;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.user.entity.StandardUser;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.exception.UserLoginExistException;
import io.mutex.user.repository.StandardUserDAO;
import io.mutex.user.valueobject.UserStatus;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class StandardUserService {
    
    @Inject EnvironmentUtils environmentUtils;
    @Inject StandardUserDAO standardUserDAO;
    @Inject EncryptionService encryptionService;
    
    public List<StandardUser> findByTenant(){
        return environmentUtils.getUserTenant()
                .map(standardUserDAO::findByTenant)
                .orElseGet(() -> Collections.EMPTY_LIST);
    }
    
    public Optional<StandardUser> createUser(StandardUser user) throws NotMatchingPasswordAndConfirmation, 
            UserLoginExistException{
        
        if(!arePasswordsMatching(user)){
            throw new NotMatchingPasswordAndConfirmation("Le mot de passe est different de la confirmation");
        }
        
        if(isUserWithLoginExist(user.getLogin())){
            throw  new UserLoginExistException("Ce login existe déjà");
        }
        
        return Optional.ofNullable(user).map(this::setHashedPassword)
                    .map(u -> setStatus(u, UserStatus.DISABLED))
                    .flatMap(standardUserDAO::makePersistent);

    }
    
    private boolean arePasswordsMatching(StandardUser standardUser){
        return standardUser.getPassword()
                .equals(standardUser.getConfirmPassword());
    }
    
     private boolean isUserWithLoginExist(String login){
        return standardUserDAO.findByLogin(login).isPresent();
    }
     
    private StandardUser setHashedPassword(StandardUser user) {
        user.setPassword(EncryptionService.hash(user.getPassword()));
        return user;
    };
    
    private StandardUser setStatus(StandardUser user,UserStatus status) {
        user.setStatus(status);
        return user;
    };
} 