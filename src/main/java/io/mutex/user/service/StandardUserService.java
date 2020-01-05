package io.mutex.user.service;

import io.mutex.shared.service.EncryptionService;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.shared.service.StringUtil;
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
    
    @Inject EnvironmentUtils envUtils;
    @Inject StandardUserDAO standardUserDAO;
    @Inject EncryptionService encryptionService;
    
    
    public Optional<StandardUser> findByUuid(String uuid){
        return standardUserDAO.findById(uuid);
    }
    
    public List<StandardUser> findByTenant(){
        return envUtils.getUserTenant()
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
                    .flatMap(this::setTenant)
                    .map(u -> setStatus(u, UserStatus.DISABLED))
                    .map(this::loginToLowerCase)
                    .flatMap(standardUserDAO::makePersistent);

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
} 