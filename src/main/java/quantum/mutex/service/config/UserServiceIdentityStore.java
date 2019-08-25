/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.config;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import quantum.mutex.domain.dao.UserDAO;
import quantum.mutex.domain.entity.User;

/**
 *
 * @author root
 */
@ApplicationScoped
public class UserServiceIdentityStore implements IdentityStore{
    
    @Inject
    UserDAO userDAO;
    
    @Override
    public CredentialValidationResult validate(Credential credential) {
        UsernamePasswordCredential login = (UsernamePasswordCredential) credential;
        String email = login.getCaller();
        String password = login.getPasswordAsString();
        
        return CredentialValidationResult.INVALID_RESULT;
    } 
}
