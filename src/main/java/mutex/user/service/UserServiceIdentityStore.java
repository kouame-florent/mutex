/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.user.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toSet;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import org.apache.commons.codec.digest.DigestUtils;
import mutex.user.repository.UserDAO;
import mutex.user.repository.UserRoleDAO;
import mutex.user.domain.entity.User;
import mutex.user.domain.entity.UserRole;
import mutex.user.domain.valueobject.UserStatus;


/**
 *
 * @author root
 */
@ApplicationScoped
public class UserServiceIdentityStore implements IdentityStore {

    private static final Logger LOG = Logger.getLogger(UserServiceIdentityStore.class.getName());
    
  
    @Inject
    UserDAO userDAO;
  
    @Inject
    UserRoleDAO userRoleDAO;  

      
    @Override
    public CredentialValidationResult validate(Credential credential) { 
        LOG.log(Level.INFO,"--> VALIDATE CREDENTIALS...");
        return findUser(credential)
                .map(u -> new CredentialValidationResult(u.getLogin(), getRoles(u)))
                .orElseGet(() -> CredentialValidationResult.INVALID_RESULT);
    }
    
    
    
    private Optional<User> findUser(Credential credential) {
        UsernamePasswordCredential login = (UsernamePasswordCredential) credential;
//        LOG.log(Level.INFO, "--> LOGIN: {0}",login.getCaller());
//        LOG.log(Level.INFO, "--> RAW PASSWD {0}",login.getPasswordAsString());
//        LOG.log(Level.INFO, "--> HASH PASSWD: {0}",DigestUtils.sha256Hex(login.getPasswordAsString()));
         
        return userDAO.findWithStatus(login.getCaller(),
                DigestUtils.sha256Hex(login.getPasswordAsString()),UserStatus.ENABLED);
    }

    private Set<String> getRoles(User user) {
        return userRoleDAO.findByUser(user)
                .stream().map(UserRole::getRoleName)
                .collect(toSet());
    }
}
